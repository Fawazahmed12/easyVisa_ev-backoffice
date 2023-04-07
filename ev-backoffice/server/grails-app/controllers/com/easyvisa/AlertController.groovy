package com.easyvisa

import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.utils.ExceptionUtils
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

@Secured(['ROLE_EV'])
class AlertController implements IErrorHandler {

    SpringSecurityService springSecurityService
    AlertService alertService
    PackageDocumentService packageDocumentService
    OrganizationService organizationService
    AttorneyService attorneyService
    MessageSource messageSource

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def userAlerts(EasyVisaSystemMessageCommand alertsCommand) {
        User user = springSecurityService.currentUser as User
        PaginationResponseDto responseDto = alertService.getUserAlerts(user, alertsCommand)
        response.setIntHeader('X-total-count', responseDto.totalCount)
        response.setHeader('Access-Control-Expose-Headers', 'X-total-count')
        render(view:'/list', model:[templatePath:'/alert/alert', list:responseDto.result, var:'alert'])
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def count(AttorneyCountsCommand attorneyCounts) {
        User currentUser = springSecurityService.currentUser as User
        Map result = [alerts:alertService.countUserAlerts(currentUser)]
        if (!currentUser.isApplicant()
                && !(currentUser.isEmployeeOnly()
                && !organizationService.organizations(attorneyService.findEmployeeByUser(currentUser.id),
                new OrganizationMenuCommand(status:EmployeeStatus.ACTIVE)))) {
            LegalRepresentative attorney = attorneyCounts.findAttorney()
            Organization organization = attorneyCounts.findOrganization()
            if (!organizationService.doesEmployeeBelongToOrganization(attorneyService.findEmployeeByUser(currentUser.id).id, organization.id)) {
                throw ExceptionUtils.createAccessDeniedException('user.is.not.active.in.organization')
            }
            validateAttorney(attorney, organization)
            result['warnings'] = alertService.countAttorneyWarnings(attorney, organization)
            result['dispositionsCount'] = packageDocumentService.getDocumentAttachmentDispositionCount(attorney, organization)
        }
        render(result as JSON)
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def deleteAlerts() {
            List<Long> alertIds = params.list('ids')
            User user = springSecurityService.currentUser as User
            List<Alert> alerts = Alert.getAll(alertIds)
            alertService.deleteMultipleEvSystemMessages(alerts, user)
            render([deletedAlertIds: alerts*.id] as JSON)
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def get(Long id) {
        Alert alert = Alert.get(id)
        User user = springSecurityService.currentUser as User
        if (alert) {
            if (user == alert.recipient) {
                render(template: '/alert/alert', model: [alert: alert])
            } else {
                renderError(HttpStatus.SC_FORBIDDEN, 'alert.not.available.for.user')
            }
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'alert.not.found.with.id')
        }
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def edit(Long id, EasyVisaSystemMessageCommand alertsCommand) {
        Alert alert = Alert.get(id)
        if (alert) {
            alert = alertService.updateEvSystemMessage(alert, alertsCommand.read, alertsCommand.starred)
            render(template: '/alert/alert', model: [alert: alert])
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'alert.not.found.with.id')
        }
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def reply(Long id) {
        try {
            Alert alert = Alert.get(id)
            if (alert) {
                Boolean accepted = Boolean.parseBoolean(request.JSON.accept)

                alertService.validateAndReplyAlert(alert, springSecurityService.currentUser as User, accepted)
                render([message: messageSource.getMessage('alert.request.processed', null, request.locale)] as JSON)
            } else {
                renderError(HttpStatus.SC_NOT_FOUND, 'alert.not.found.with.id')
            }
        }
        catch (EasyVisaException e) {
            renderError(e.errorCode, e.errorMessageCode, e.params, e.errorMessageType)
        }
    }

    private void validateAttorney(LegalRepresentative legalRepresentative, Organization organization) {
        if (legalRepresentative != null) {
            if (!legalRepresentative.user.activeMembership) {
                throw ExceptionUtils.createAccessDeniedException('user.is.not.active')
            }
            if (!organizationService.doesEmployeeBelongToOrganization(legalRepresentative.id, organization.id)) {
                throw ExceptionUtils.createAccessDeniedException('employee.not.in.organization')
            }
        }
   }
}
