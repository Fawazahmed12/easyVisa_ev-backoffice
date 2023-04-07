package com.easyvisa

import com.easyvisa.dto.PackageResponseDto
import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.PackageStatus
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

import java.time.LocalDate

@GrailsCompileStatic
class PackageController implements IErrorHandler {

    PackageService packageService
    PackageQuestionnaireService packageQuestionnaireService
    PackageDocumentService packageDocumentService
    AttorneyService attorneyService
    ApplicantService applicantService
    SpringSecurityService springSecurityService
    FileService fileService
    PermissionsService permissionsService
    MessageSource messageSource
    SqlService sqlService

    @SuppressWarnings('FactoryMethodName')
    @Secured([Role.EMPLOYEE])
    def create(PackageCommand packageCommand) {
        final User user = springSecurityService.currentUser as User
        permissionsService.validateEmployeeNonTraineePosition(user, packageCommand.organization)
        validatePackagePayload(packageCommand)
        PackageResponseDto packageResponse = packageService.validateAndCreatePackage(packageCommand)
        renderPackageAndMessage(packageResponse, HttpStatus.SC_CREATED)
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def find(FindPackageCommand findPackageCommand) {
        findPackageCommand.checkSingleListParams(params)
        if (findPackageCommand) {
            PaginationResponseDto responseDto
            final User user = springSecurityService.currentUser as User
            if (user.applicant) {
                final Applicant applicant = applicantService.findApplicantByUser(user.id)
                findPackageCommand.applicantStatus.remove(PackageStatus.LEAD)
                // don't need to remove 'DELETED', since we need to display deleted package in Review page
                responseDto = sqlService.findPackages(findPackageCommand, [applicant.id], findPackageCommand.applicantStatus)
            } else if (user.employee || user.owner) {
                Organization organization = Organization.get(findPackageCommand.organizationId)
                if (organization == null) {
                    renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found', [findPackageCommand.organizationId])
                }
                permissionsService.assertIsActive(user, organization)
                responseDto = sqlService.findPackages(findPackageCommand, null, findPackageCommand.status)
            } else {
                throw new EasyVisaException(errorCode: HttpStatus.SC_FORBIDDEN, errorMessageCode: 'notpermitted.error')
            }
            response.setIntHeader('X-total-count', responseDto.totalCount)
            response.setHeader('Access-Control-Expose-Headers', 'X-total-count')
            if (findPackageCommand.shrink) {
                List result = []
//                render(view: '/shrunkPackages', model: [packageList: responseDto.result], status: HttpStatus.SC_OK)
                //TODO: solution to have better performance
                createResponse(responseDto, result)
                render(result as JSON)
            } else {
                render(view: '/packages', model: [packageList: responseDto.result], status: HttpStatus.SC_OK)
            }
        } else {
            response.status = HttpStatus.SC_UNPROCESSABLE_ENTITY
            render(['errors': [['code': HttpStatus.SC_UNPROCESSABLE_ENTITY, 'message': messageSource.getMessage('search.query.not.valid', null, request.locale)]]] as JSON)
        }
    }

    private List<Object> createResponse(PaginationResponseDto responseDto, List result) {
        responseDto.result.each {
            Package aPackage = it as Package
            List applicants = []
            aPackage.packageApplicantsUi.each {
                Map applicantMap = [:]
                if (it.applicantType != ApplicantType.Petitioner.uiValue) {
                    applicantMap.putAll([benefitCategory: it.category.name(), benefitCategoryDescription: it.category.description])
                }
                Applicant applicant = it.applicant
                Address address = applicant.profile.address
                applicantMap.putAll([citizenshipStatus: it.citizenshipStatus?.name(), applicantType: it.applicantType,
                                     profile          : ['firstName' : applicant.profile.firstName, 'lastName': applicant.profile.lastName,
                                                         'middleName': applicant.profile.middleName,
                                                         homeAddress : ['line1'     : address?.line1,
                                                                        'line2'     : address?.line2,
                                                                        'city'      : address?.city,
                                                                        'country'   : address?.country?.name(),
                                                                        'state'     : address?.state?.name(),
                                                                        'province'  : address?.province,
                                                                        'zipCode'   : address?.zipCode,
                                                                        'postalCode': address?.postalCode],
                                                         id          : applicant.id]])
                applicants << applicantMap
            }
            LegalRepresentative legRep = aPackage.attorney
            Map attorney = convertAttorney(legRep)
            List assignees = []
            aPackage.orderedAssignees.each {
                Map assignee = [startDate: it.startDate,
                                endDate  : it.endDate,
                                status   : it.status.displayName,]
                assignee.putAll(convertAttorney(it.representative as LegalRepresentative))

                assignees << assignee
            }

            result << [status                          : aPackage.status.name(), applicants: applicants, representative: attorney,
                       questionnaireSyncStatus         : aPackage.questionnaireSyncStatus?.name(),
                       resentativeId                   : aPackage.attorney.id, id: aPackage.id, easyVisaId: aPackage.easyVisaId, owed: aPackage.owed,
                       lastActiveOn                    : aPackage.lastActiveOn, assignees: assignees,
                       documentCompletedPercentage     : Math.floor(aPackage.documentCompletedPercentage)?.round(),
                       questionnaireCompletedPercentage: Math.floor(aPackage.questionnaireCompletedPercentage)?.round(),
                       creationDate                    : aPackage.dateCreated, title: aPackage.title, categories: aPackage.categories]
        }
    }

    private Map convertAttorney(LegalRepresentative legRep) {
        Profile attorneyProfile = legRep.profile
        [firstName    : attorneyProfile.firstName,
         lastName     : attorneyProfile.lastName,
         middleName   : attorneyProfile.middleName,
         id           : legRep.id,
         officeAddress: convertOfficeAddress(legRep),
         officeEmail  : legRep.profile.email,
         email        : attorneyProfile.email,
         faxNumber    : legRep.faxNumber,
         officePhone  : legRep.officePhone,
         mobilePhone  : legRep.mobilePhone,
         easyVisaId   : attorneyProfile?.easyVisaId,
         profilePhoto : attorneyProfile.profilePhotoUrl]
    }

    private Map convertOfficeAddress(LegalRepresentative legRep) {
        Address address = legRep.officeAddress
        Map result = null
        if (address) {
            result = [line1     : address?.line1,
                      line2     : address?.line2,
                      city      : address?.city,
                      country   : address.country?.name(),
                      state     : address.state?.name(),
                      province  : address.province,
                      zipCode   : address.zipCode,
                      postalCode: address.postalCode,]
        }
        result
    }

    @Secured([Role.EMPLOYEE])
    def transfer(TransferPackageCommand transferPackageCommand) {
        if (transferPackageCommand.organizationId == null) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'organization.id.required')
        }
        final LegalRepresentative representative = transferPackageCommand.representative
        User receivingUser = representative?.user

        if (receivingUser?.paid && receivingUser?.enabled && receivingUser?.activeMembership) {
            Employee currentEmployee = attorneyService.findEmployeeByUser(springSecurityService.currentUserId as Long)
            packageService.validateAndTransferPackages(transferPackageCommand.packages, representative, currentEmployee, transferPackageCommand.organization)
            render([representativeId: representative.id,
                    firstName       : representative.profile.firstName,
                    lastName        : representative.profile.lastName,
                    middleName      : representative.profile.middleName,] as JSON)
        } else {
            int errorCode = !receivingUser?.paid ? HttpStatus.SC_FORBIDDEN : HttpStatus.SC_UNPROCESSABLE_ENTITY
            String errorMessageCode = !receivingUser?.paid ? 'attorney.not.paid' : 'attorney.not.found'
            throw new EasyVisaException(errorCode: errorCode, errorMessageCode: errorMessageCode)
        }
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def get(Long id) {
        Package aPackage = Package.get(id)
        if (aPackage && aPackage.status != PackageStatus.DELETED) {
            permissionsService.validatePackageReadAccess(springSecurityService.currentUser as User, aPackage)
            render(template: '/package', model: [aPackage: aPackage], status: HttpStatus.SC_OK)
        } else {
            response.status = HttpStatus.SC_NOT_FOUND
            render(['errors': [['code': HttpStatus.SC_NOT_FOUND, 'message': messageSource.getMessage('package.not.found.with.id', null, request.locale)]]] as JSON)
        }
    }

    @Secured([Role.EMPLOYEE])
    def deleteLeads(DeletePackageCommand deleteCommand) {
        final User user = springSecurityService.currentUser as User
        permissionsService.validateEmployeeNonTraineePosition(user, deleteCommand.organization)
        List packageIds = packageService.deleteLeads(deleteCommand.organization, deleteCommand.startDate.clearTime(), (deleteCommand.endDate + 1).clearTime())
        render(['deletedPackageIds': packageIds] as JSON)
    }

    @Secured([Role.EMPLOYEE])
    def deleteSelectedLeads(DeleteSelectedLeadsCommand deleteSelectedLeadsCommand) {
        final User user = springSecurityService.currentUser as User
        permissionsService.validateEmployeeNonTraineePosition(user, deleteSelectedLeadsCommand.organization)
        List packageIds = packageService.deleteLeadPackages(deleteSelectedLeadsCommand.getPackages())
        render(['deletedPackageIds': packageIds] as JSON)
    }

    @Secured([Role.EMPLOYEE])
    def deleteTransferred(DeleteTransferredPackagesCommand command) {
        final User user = springSecurityService.currentUser as User
        permissionsService.assertIsAdmin(user, command.organization)
        List packageIds = packageService.deleteTransferredPackages(command.getPackages(), command.organization)
        render(['deletedPackageIds': packageIds] as JSON)
    }

    @Secured([Role.EMPLOYEE])
    def updateAmountOwed(Long id, AmountOwedCommand amountOwed) {

        if (amountOwed.hasErrors() ) {
            render(status: HttpStatus.SC_UNPROCESSABLE_ENTITY,
                    ['errors': [['code'   : HttpStatus.SC_UNPROCESSABLE_ENTITY,
                                 'message': messageSource.getMessage('amount.owed.invalid',
                                         null, request.locale)]]] as JSON)

        } else if (id) {
            Package aPackage = getPackageIfNotDeleted(id)
            User currentUser = springSecurityService.currentUser as User
            permissionsService.validatePackageWriteAccess(currentUser, aPackage)

            PackageResponseDto packageResponseDto = packageService.updateOwedAmount(aPackage, amountOwed)
            renderPackageAndMessage(packageResponseDto)
        } else {
            response.status = HttpStatus.SC_NOT_FOUND
            render(['errors': [['code'   : HttpStatus.SC_NOT_FOUND,
                                'message': messageSource.getMessage('package.not.found.with.id',
                                        null, request.locale)]]] as JSON)
        }
    }

    @Secured([Role.EMPLOYEE])
    def edit(Long id, PackageCommand packageCommand, Boolean skipReminders) {
        validatePackagePayload(packageCommand)
        packageCommand.skipReminders = skipReminders
        if (id) {
            Package aPackage = getPackageIfNotDeleted(id)
            User currentUser = springSecurityService.currentUser as User
            permissionsService.validatePackageWriteAccess(currentUser, aPackage)

            PackageResponseDto packageResponseDto = packageService.updatePackage(aPackage, packageCommand)
            if (aPackage.status == PackageStatus.OPEN) {
                packageQuestionnaireService.populateDefaultQuestionnaireAnswers(aPackage, this.getCurrentDate())
                packageDocumentService.renewDocumentCompletionStatus(aPackage, currentUser, this.getCurrentDate())
            }
            renderPackageAndMessage(packageResponseDto)
        } else {
            response.status = HttpStatus.SC_NOT_FOUND
            render(['errors': [['code'   : HttpStatus.SC_NOT_FOUND,
                                'message': messageSource.getMessage('package.not.found.with.id',
                                        null, request.locale)]]] as JSON)
        }
    }

    @Secured([Role.EMPLOYEE])
    def sendWelcomeEmail(Long id) {
        Package aPackage = Package.get(id)
        if (aPackage) {
            permissionsService.validatePackageWriteAccess(springSecurityService.currentUser as User, aPackage)
            packageService.sendWelcomeEmail(aPackage)
            String messageCode = 'package.welcome.email.sent'
            if (!aPackage.welcomeEmailSentOn) {
                messageCode = 'package.welcome.email.sent.first'
            }
            render(['message': messageSource.getMessage(messageCode, null, request.locale)] as JSON)
        } else {
            response.status = HttpStatus.SC_NOT_FOUND
            render(['errors': [['code': HttpStatus.SC_NOT_FOUND, 'message': messageSource.getMessage('package.not.found.with.id', null, request.locale)]]] as JSON)
        }
    }

    @Secured([Role.EMPLOYEE])
    def sendApplicantInvite(Long id) {
        Package aPackage = Package.get(id)
        String applicantId = request.JSON['applicantId']
        if (aPackage) {
            if (applicantId) {
                Applicant applicant = Applicant.get(applicantId)
                if (applicant) {
                    permissionsService.validatePackageWriteAccess(springSecurityService.currentUser as User, aPackage)
                    packageService.sendApplicantInviteEmail(aPackage, applicant)
                    render(['message': messageSource.getMessage('package.applicant.invite.sent', null, request.locale)] as JSON)
                } else {
                    render(['errors': [['code': HttpStatus.SC_NOT_FOUND, 'message': messageSource.getMessage('applicant.not.found.with.id', null, request.locale)]]] as JSON)
                }
            } else {
                packageService.sendAllApplicantsInvite(aPackage)
                render(['message': messageSource.getMessage('package.applicant.invite.sent', null, request.locale)] as JSON)
            }

        } else {
            response.status = HttpStatus.SC_NOT_FOUND
            render(['errors': [['code': HttpStatus.SC_NOT_FOUND, 'message': messageSource.getMessage('package.not.found.with.id', null, request.locale)]]] as JSON)
        }
    }

    @Secured([Role.EMPLOYEE])
    def uploadRetainer(Long id, PackageRetainerCommand packageRetainerCommand) {
        Package aPackage = Package.get(id)
        if (aPackage) {
            if ([PackageStatus.CLOSED, PackageStatus.TRANSFERRED, PackageStatus.DELETED].contains(aPackage.status)) {
                throw ExceptionUtils.createUnProcessableDataException('package.closed.package.changes')
            }
            permissionsService.validatePackageWriteAccess(springSecurityService.currentUser as User, aPackage)
            EasyVisaFile retainerFile = fileService.uploadRetainer(packageRetainerCommand.retainerFile, aPackage.attorney.profile, aPackage)
            render(template: '/easyVisaFile', model: [easyVisaFile: retainerFile], status: HttpStatus.SC_CREATED)

        } else {
            response.status = HttpStatus.SC_NOT_FOUND
            render(['errors': [['code': HttpStatus.SC_NOT_FOUND, 'message': messageSource.getMessage('package.not.found.with.id', null, request.locale)]]] as JSON)
        }
    }

    @Secured([Role.EMPLOYEE])
    def deleteRetainer(Long id) {
        Package aPackage = Package.get(id)
        if (aPackage) {
            permissionsService.validatePackageWriteAccess(springSecurityService.currentUser as User, aPackage)
            if ([PackageStatus.TRANSFERRED, PackageStatus.DELETED].contains(aPackage.status)) {
                throw ExceptionUtils.createUnProcessableDataException('package.transferred.package.changes')
            }
            fileService.deleteRetainer(aPackage)
            render(['message': messageSource.getMessage('package.retainer.deleted', null, request.locale)] as JSON)
        } else {
            response.status = HttpStatus.SC_NOT_FOUND
            render(['errors': [['code': HttpStatus.SC_NOT_FOUND, 'message': messageSource.getMessage('package.not.found.with.id', null, request.locale)]]] as JSON)
        }
    }

    @Secured([Role.EMPLOYEE])
    def changeStatus(Long id, PackageMoveCommand moveCommand) {
        Package aPackage = Package.get(id)
        if (aPackage) {
            permissionsService.validatePackageWriteAccess(springSecurityService.currentUser as User, aPackage,
                    'trainees.not.allowed.change.package.status')
            PackageResponseDto packageResponseDto = packageService.maybeChangePackageStatus(aPackage, aPackage.status,
                    moveCommand.newStatus)
            renderPackageAndMessage(packageResponseDto)
            packageService.prepareQuestionnaire(packageResponseDto.aPackage.id, this.getCurrentDate())
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'package.not.found.with.id')
        }
    }

    private void renderPackageAndMessage(PackageResponseDto packageResponseDto, Integer status = HttpStatus.SC_OK) {
        packageResponseDto.messages.each {
            if (it.messageCode) {
                it.message = messageSource.getMessage(it.messageCode, it.messageCodeArgs, request.locale)
            }
        }
        render(template: '/packageMessage', model: [packageResponse: packageResponseDto], status: status)
    }


    @Secured([Role.EMPLOYEE])
    def sendBill(Long id, SendBillCommand sendBillCommand) {
        Package aPackage = Package.get(id)
        if (aPackage) {
            final User user = springSecurityService.currentUser as User
            permissionsService.assertIsActive(user)
            permissionsService.assertIsActive(user, aPackage.organization)
            permissionsService.assertIsPaid(user)

            if ([PackageStatus.LEAD, PackageStatus.CLOSED, PackageStatus.TRANSFERRED, PackageStatus.DELETED].contains(aPackage.status)) {
                throw (ExceptionUtils.createUnProcessableDataException('package.status.is.not.open'))
            }

            List<EmployeePosition> requiredPositionList = [EmployeePosition.PARTNER, EmployeePosition.ATTORNEY, EmployeePosition.MANAGER, EmployeePosition.EMPLOYEE]
            permissionsService.assertHasPosition(aPackage.organization, requiredPositionList, user)

            def feeCharge = sendBillCommand.getFeeCharges()
            List charges = feeCharge.charges as List
            BigDecimal total = feeCharge.total as BigDecimal

            packageService.sendBill(aPackage, sendBillCommand.email, charges, total)
            render(status: HttpStatus.SC_OK)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'package.not.found.with.id')
        }
    }

    private void validatePackagePayload(PackageCommand packageCommand) {
        ApplicantCommand beneficiary = packageCommand.applicants.find { it.direct }
        if (!beneficiary) {
            throw ExceptionUtils.createUnProcessableDataException('direct.benefit.missed')
        }
        Set<String> emails = []
        packageCommand.applicants.each {
            if (!it.applicantType) {
                throw ExceptionUtils.createUnProcessableDataException('applicant.type.missed')
            }
            String email = it.profile.email
            if (!email && it.inviteApplicant) {
                throw ExceptionUtils.createUnProcessableDataException('applicant.invite.email.missed', null, [it.profile.fullName])
            }
            if (email && emails.contains(email)) {
                throw ExceptionUtils.createUnProcessableDataException('unique.applicant.per.package', null, [email])
            }
            emails << email
        }
        (packageCommand.petitioners ?: packageCommand.applicants).each {
            if (!it.profile.email) {
                throw ExceptionUtils.createUnProcessableDataException('petitioner.beneficiary.email.missing')
            }
        }
    }

    private Package getPackageIfNotDeleted(Long id) {
        Package aPackage = Package.get(id)
        if (aPackage.status == PackageStatus.DELETED) {
            throw ExceptionUtils.createNotFoundException('package.not.found.with.id')
        }
        return aPackage

    }

    private LocalDate getCurrentDate() {
        String calenderDay = request.getHeader("Current-Date")
        LocalDate calendar = (calenderDay != null) ? DateUtil.localDate(calenderDay) : DateUtil.today()
        return calendar
    }

}
