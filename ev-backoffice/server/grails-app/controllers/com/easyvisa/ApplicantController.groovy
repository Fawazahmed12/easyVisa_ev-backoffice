package com.easyvisa

import com.easyvisa.utils.ExceptionUtils
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic
import org.apache.commons.validator.routines.EmailValidator
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

@Secured([Role.EV])
@CompileStatic
class ApplicantController implements IErrorHandler {

    MessageSource messageSource
    ApplicantService applicantService
    PermissionsService permissionsService
    SpringSecurityService springSecurityService

    @Secured([Role.EMPLOYEE])
    def find(String email, String organizationId) {
        assertParameters(email, organizationId)
        Organization organization = Organization.get(organizationId)
        if (organization) {
            permissionsService.validateEmployeeNonTraineePosition(springSecurityService.currentUser as User,
                                                                    organization)
            Applicant applicant = applicantService.findApplicant(email)
            if (applicant) {
                render(template: '/user/applicantInPackages', model: [applicant: applicant], status: HttpStatus.SC_OK)
            } else {
                renderError(HttpStatus.SC_NOT_FOUND, 'applicant.not.found')
            }
        } else {
            renderError(HttpStatus.SC_UNPROCESSABLE_ENTITY, 'organization.not.found', [organizationId])
        }
    }

//    @Secured([Role.USER])
//    def deleteData(Long id) {
//        Applicant applicant = Applicant.get(id)
//        if (!applicant || applicant.profile.user) {
//            throw ExceptionUtils.createUnProcessableDataException('applicant.not.found.with.id')
//        }
//        applicantService.deleteApplicantData(applicant)
//        render([id: id] as JSON)
//    }

    @Secured([Role.USER])
    def transferPackage(ApplicantTransferPackageCommand transferPackageCommand) {
        LegalRepresentative representative = transferPackageCommand.representative
        User receivingUser = representative?.user
        if (receivingUser?.paid && receivingUser?.enabled && receivingUser?.activeMembership) {
            User currentUser = springSecurityService.currentUser as User
            Package aPackage = transferPackageCommand.package
            if (!aPackage) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY,
                        errorMessageCode: 'package.not.found.with.id')
            }
            applicantService.transferPackage(aPackage, representative, currentUser,
                    transferPackageCommand.organization)
            render([representativeId: representative.id,
                    firstName       : representative.profile.firstName,
                    lastName        : representative.profile.lastName,
                    middleName      : representative.profile.middleName,] as JSON)
        } else {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY,
                    errorMessageCode: 'attorney.not.found')
        }
    }

    void assertParameters(String search, String organizationId) {
        if (!search || !organizationId) {
            throw ExceptionUtils.createUnProcessableDataException('email.and.organization.id.required')
        }
        if (!EmailValidator.instance.isValid(search)) {
            throw ExceptionUtils.createUnProcessableDataException('default.invalid.email.message', null, [search])
        }
    }

}