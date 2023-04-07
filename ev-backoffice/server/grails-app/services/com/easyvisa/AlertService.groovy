package com.easyvisa

import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.questionnaire.Answer
import com.easyvisa.utils.ExceptionUtils
import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import grails.plugin.springsecurity.SpringSecurityService
import org.grails.orm.hibernate.cfg.GrailsHibernateUtil

class AlertService {

    PageRenderer groovyPageRenderer
    EvMailService evMailService
    OrganizationService organizationService
    AttorneyService attorneyService
    SpringSecurityService springSecurityService

    @Transactional
    Warning createPackageWarning(Package packageObj, Applicant applicant,
                                 EasyVisaSystemMessageType messageType, String messageBody,
                                 String questionId = null, Answer answer = null, String subject = null,
                                 String source = EvSystemMessage.EASYVISA_SOURCE, Boolean sentEmail = Boolean.TRUE) {
        Warning warning = new Warning(applicant: applicant, messageType: messageType, questionId: questionId,
                answer: answer, source: source)
        warning.with {
            aPackage = packageObj
            it.subject = subject ?: messageType.subject
            body = messageBody
        }
        if (sentEmail) {
            evMailService.sendWarningEmail(warning, packageObj.attorney)
        }
        warning.save(failOnError: true)
    }

    @Transactional
    PaginationResponseDto getUserAlerts(User user, EasyVisaSystemMessageCommand alertsCommand) {
        PaginationResponseDto response = new PaginationResponseDto()
        response.result = Alert.createCriteria().list(alertsCommand.paginationParams) {
            eq('recipient', user)
            if (alertsCommand.read != null) {
                eq('isRead', alertsCommand.read)
            }
            order(alertsCommand.sortFieldName, alertsCommand.sortOrder)
        } as List<Alert>

        response.totalCount = Alert.createCriteria().get {
            projections {
                count('id')
            }
            eq('recipient', user)
            if (alertsCommand.read != null) {
                eq('isRead', alertsCommand.read)
            }
        } as Integer
        response
    }

    @Transactional
    PaginationResponseDto getUserWarnings(WarningCommand warningCommand, LegalRepresentative legalRepresentative,
                                          Organization organization) {
        PaginationResponseDto responseDto = new PaginationResponseDto()
        responseDto.result = Warning.createCriteria().list(warningCommand.paginationParams) {
            aPackage {
                if (legalRepresentative) {
                    eq('attorney', legalRepresentative)
                }
                eq('organization', organization)
            }
            if (warningCommand.read != null) {
                eq('isRead', warningCommand.read)
            }
            if (warningCommand.sort == 'representative') {
                aPackage {
                    attorney {
                        'profile' {
                            order('lastName', warningCommand.sortOrder)
                            order('firstName', warningCommand.sortOrder)
                        }
                    }
                }
            }
            if (warningCommand.sort == 'applicant') {
                aPackage {
                    order('title', warningCommand.sortOrder)
                }
            } else {
                order(warningCommand.sortFieldName, warningCommand.sortOrder)
            }
        } as List<Warning>

        responseDto.totalCount = Warning.createCriteria().get {
            projections {
                count('id')
            }
            aPackage {
                if (legalRepresentative) {
                    eq('attorney', legalRepresentative)
                }
                eq('organization', organization)
            }
            if (warningCommand.read != null) {
                eq('isRead', warningCommand.read)
            }
        } as Integer
        responseDto
    }

    @Transactional
    Map<String, Integer> countUserAlerts(final User user) {
        List alertsWithCounts = Alert.createCriteria().list {
            projections {
                count('isRead')
                groupProperty('isRead')
            }
            eq('recipient', user)
        }
        calculateCounts(alertsWithCounts)
    }

    @Transactional
    Map<String, Integer> countAttorneyWarnings(final LegalRepresentative attorney, final Organization organization) {
        List warningsWithCounts = Warning.createCriteria().list {
            projections {
                count('isRead')
                groupProperty('isRead')
            }
            aPackage {
                eq('organization', organization)
                if (attorney) {
                    eq('attorney', attorney)
                }
            }
        }
        calculateCounts(warningsWithCounts)
    }

    private Map<String, Integer> calculateCounts(List alertsWithCounts) {
        Map<String, Integer> result = ['read': 0, 'unread': 0]
        result << (alertsWithCounts.collectEntries {
            [(it.last() ? 'read' : 'unread'): it.first()]
        }) as Map<String, Integer>
    }

    String renderSystemMessageContent(EvSystemMessage systemMessage) {
        if (systemMessage.body) {
            return systemMessage.body
        }
        ProcessRequest processRequest
        if (systemMessage.class == Alert) {
            processRequest = GrailsHibernateUtil.unwrapIfProxy(((Alert) systemMessage).processRequest)
        }
        String templatePath = systemMessage.messageType?.templatePath
        if (templatePath) {
            renderTemplate(templatePath, [alert: systemMessage, processRequest: processRequest, currentUser: springSecurityService.currentUser])
        }
    }

    String renderTemplate(String path, Map model) {
        groovyPageRenderer.render(template: path, model: model)
    }

    @Transactional
    EvSystemMessage updateEvSystemMessage(EvSystemMessage systemMessage, Boolean isRead, Boolean isStarred) {
        if (isRead != null) {
            systemMessage.isRead = isRead
        }
        if (isStarred != null) {
            systemMessage.isStarred = isStarred
        }
        systemMessage.save(failOnError: true)
    }

    @Transactional
    void deleteMultipleEvSystemMessages(List<EvSystemMessage> systemMessages, User user) {
        if (systemMessages.contains(null)) {
            throw ExceptionUtils.createUnProcessableDataException('alert.not.found.with.id')
        }
        systemMessages.each {
            deleteEvSystemMessage(it, user)
        }
    }

    @Transactional
    void deleteEvSystemMessage(EvSystemMessage systemMessage, User user) {
        if (systemMessage.class != Alert || ((Alert) systemMessage).recipient == user) {
            systemMessage.delete(failOnError: true)
        } else {
            throw ExceptionUtils.createAccessDeniedException('alert.not.editable.for.user')
        }
    }

    @Transactional
    void createProcessRequestAlert(ProcessRequest request, EasyVisaSystemMessageType alertMessageType, User user,
                                   String alertSource = null, String body = null, String aSubject = null) {

        if (user?.activeMembership) {
            String alertSubject = aSubject ?: alertMessageType.subject
            Alert alert = new Alert(processRequest: request, messageType: alertMessageType,
                    subject: alertSubject, recipient: user, source: alertSource, body: body)
                    .save(failOnError: true)
            evMailService.sendAlertEmail(alert)
        }
    }

    @Transactional
    void createAlert(EasyVisaSystemMessageType alertMessageType, User user, String alertSource = null,
                     String body = null, String aSubject = null) {

        createProcessRequestAlert(null, alertMessageType, user, alertSource, body, aSubject)
    }

    void validateIfAlertCanBeReplied(Alert alert, User user) {
        if (!(EasyVisaSystemMessageType.processRequestAlertTypes.contains(alert.messageType))) {
            throw ExceptionUtils.createUnProcessableDataException('alert.not.replyable')
        }
        if (alert.recipient != user && alert.processRequest.requestedBy.user != user) {
            throw ExceptionUtils.createAccessDeniedException('alert.not.available.for.user')
        }
        if (alert?.processRequest?.state != ProcessRequestState.PENDING && alert.messageType == EasyVisaSystemMessageType.INVITE_TO_ORGANIZATION) {
            LegalRepresentative legalRepresentative = attorneyService.findAttorneyByUser(alert.recipient.id)
            Organization lawFirm = organizationService.getLawFirmOrganization(legalRepresentative)
            String lawFirmName = lawFirm?.name;
            throw ExceptionUtils.createUnProcessableDataException('organization.join.request.is.already.accepted', null, [lawFirmName], null, null, ErrorMessageType.INVITATION_ALREADY_ACCEPTED);
        }
        if (alert?.processRequest?.state != ProcessRequestState.PENDING && alert.messageType == EasyVisaSystemMessageType.INVITE_ATTORNEY_TO_CREATE_ORGANIZATION) {
            throw ExceptionUtils.createUnProcessableDataException('new.lawfirm.invitation.is.already.accepted', null, null, null, null, ErrorMessageType.INVITATION_ALREADY_ACCEPTED)
        }
        if (alert?.processRequest?.state != ProcessRequestState.PENDING) {
            throw ExceptionUtils.createUnProcessableDataException('alert.is.already.handled')
        }
    }

    void validateAndReplyAlert(Alert alert, User user, Boolean isAccepted) {
        validateIfAlertCanBeReplied(alert, user)
        ProcessRequest request = GrailsHibernateUtil.unwrapIfProxy(((Alert) alert).processRequest)
        if (isAccepted) {
            request.acceptRequest()
        } else {
            request.denyRequest()
        }
    }

}
