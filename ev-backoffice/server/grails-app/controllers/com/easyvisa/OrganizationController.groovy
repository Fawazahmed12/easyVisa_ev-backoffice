package com.easyvisa

import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.utils.ExceptionUtils
import com.easyvisa.utils.StringUtils
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.GroovyRowResult
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

@Secured(['ROLE_EV'])
class OrganizationController implements IErrorHandler {

    MessageSource messageSource
    OrganizationService organizationService
    FileService fileService
    SpringSecurityService springSecurityService
    PermissionsService permissionsService
    AttorneyService attorneyService
    ProcessService processService

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def edit(Long id, OrganizationCommand organizationCommand) {
        Organization organization = Organization.get(id)
        if (organization) {
            organization = organizationService.updateOrganization(organization, organizationCommand)
            render(view: '/organization/organization', model: [organization: organization])
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found', [id])
        }
    }

    @Secured([Role.EMPLOYEE])
    @Deprecated
    def employees(Long id) {
        if (id) {
            List<Employee> employees = organizationService.getEmployees(id)
            render(view: '/employees', model: [employeeList: employees], status: HttpStatus.SC_OK)
        } else {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'organization.id.required')
        }
    }

    @Secured([Role.EMPLOYEE])
    def representatives(Long id, RepresentativeListCommand command) {
        Organization organization = Organization.get(id)
        if (organization) {
            User user = springSecurityService.currentUser as User
            permissionsService.assertIsActive(user, organization)

            if (command.view.equals('menu')) {
                List representativeMenu = organizationService.getRepresentativeMenu(id, command.includeInactive)
                if (!user.activeMembership || !user.paid) {
                    Long attorneyId = user.isRepresentative() ? attorneyService.findAttorneyByUser(user.id).id : null
                    representativeMenu.removeAll { it.id != attorneyId }
                }
                render(view: '/attorney/representativeMenus', model: [representatives: representativeMenu], status: HttpStatus.SC_OK)
            } else {
                permissionsService.assertIsPaid(user)
                permissionsService.assertIsActive(user)
                List<LegalRepresentative> representatives = organizationService.getRepresentatives(id)
                render(view: '/representatives', model: [representatives: representatives], status: HttpStatus.SC_OK)
            }
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found', [id])
        }
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def uploadProfilePicture(Long id, ProfilePictureCommand pictureCommand) {
        Organization organization = Organization.get(id)
        User user = springSecurityService.currentUser as User
        permissionsService.assertIsAdmin(user, organization)
        organization = organizationService.setProfilePicture(organization, pictureCommand.profilePhoto, user.profile)
        render([url: organization.profilePhotoUrl] as JSON)
    }

    @Secured([Role.ATTORNEY, Role.OWNER])
    def inviteMember(Long id, JoinOrganizationCommand organizationCommand) {
        Organization organization = Organization.get(id)
        if (organization) {
            organizationCommand.validateFields()
            InviteToOrganizationRequest inviteToOrganizationRequest = organizationService.validateAndCreateInviteToOrganizationRequest(organization, springSecurityService.currentUser as User,
                    organizationCommand.evId, organizationCommand.email)
            Map result = [:]
            result.firstName = inviteToOrganizationRequest.employee.profile.firstName
            result.middleName = inviteToOrganizationRequest.employee.profile.middleName
            result.lastName = inviteToOrganizationRequest.employee.profile.lastName
            result.requestId = inviteToOrganizationRequest.id

            render(result as JSON)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found', [id])
        }
    }

    @Secured([Role.EMPLOYEE])
    def inviteLegalRep(String evId, String email) {
        LegalRepresentative currentLegalRep = attorneyService.findAttorneyByUser(springSecurityService.currentUserId)
        if (currentLegalRep) {
            permissionsService.assertIsActive(currentLegalRep)
            permissionsService.assertIsPaid()
            InviteToCreateOrganizationRequest processRequest = organizationService.validateAndCreateNewOrganizationCreationInvite(currentLegalRep, email, evId)
            Profile profile = processRequest.representative.profile
            render([requestId : processRequest.id,
                    firstName : profile.firstName,
                    lastName  : profile.lastName,
                    middleName: profile.middleName] as JSON)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'current.user.not.representative')
        }
    }

    @Secured([Role.ATTORNEY])
    def deleteLegalRepInvite() {
        User currentUser = springSecurityService.currentUser as User
        InviteToCreateOrganizationRequest processRequest = InviteToCreateOrganizationRequest.findByRequestedByAndState(currentUser.profile, ProcessRequestState.PENDING)
        if (processRequest) {
            processService.withDrawLegalRepInvite(processRequest)
            render([requestId: processRequest.id] as JSON)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'no.pending.organization.invite')
        }
    }

    //TODO: it's not used on the UI. Consider to delete it
    @Secured([Role.ATTORNEY, Role.OWNER])
    def validateInviteMember(Long id, ValidateAttorneyCommand attorneyCommand) {
        attorneyCommand.validateFields()
        Organization organization = Organization.get(id)
        if (!organization) {
            renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found', [id])
        } else {
            Employee employee = attorneyService.validateOrganizationInvite(attorneyCommand.email, attorneyCommand.easyVisaId, organization)
            Profile profile = employee.profile
            render([representativeId: employee.id,
                    firstName       : profile.firstName,
                    lastName        : profile.lastName,
                    middleName      : profile.middleName] as JSON)
        }
    }

    @Secured([Role.EMPLOYEE, Role.OWNER])
    def leave(Long id) {
        Organization organization = Organization.get(id)
        Employee employee = attorneyService.findEmployeeByUser(springSecurityService.currentUserId as Long)
        if (organization) {
            LegalRepresentative representative = LegalRepresentative.get(employee.id)
            if (representative) {
                organizationService.leaveOrganization(organization, representative)
                render(template: '/user/attorney', model: [legalRepresentative: representative])
            } else {
                organizationService.leaveOrganization(organization, employee)
                render(template: '/user/employeeProfile', model: [employee: employee])
            }
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found', [id])
        }
    }

    @Secured([Role.EMPLOYEE])
    def deleteJoinRequest(String evId, Long requestId) {
        List<JoinOrganizationRequest> pendingJoinRequests = JoinOrganizationRequest.createCriteria().list(max: 1) {
            eq('id', requestId)
            eq('state', ProcessRequestState.PENDING)
            'organization' {
                eq('easyVisaId', evId)
            }
            'requestedBy' {
                eq('user.id', springSecurityService.currentUserId)
            }
        } as List<JoinOrganizationRequest>

        if (pendingJoinRequests.isEmpty()) {
            throw new EasyVisaException(errorMessageCode: 'join.request.not.found', errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY)
        } else {
            JoinOrganizationRequest request = pendingJoinRequests.first()
            organizationService.cancelJoinOrganizationRequest(request)
            render(template: '/user/firmJoinRequestDetails', model: [id          : request.id,
                                                                     profile     : request.requestedBy,
                                                                     organization: request.organization])
        }
    }

    @Secured([Role.EMPLOYEE])
    def joinRequest(String evId) {
        String adminEmail = request.JSON.email

        if (!StringUtils.isValidEmail(adminEmail)) {
            throw new EasyVisaException(errorMessageCode: 'invalid.email.format', errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY)
        }

        Organization organization = Organization.findByEasyVisaId(evId)
        Employee requester = attorneyService.findEmployeeByUser(springSecurityService.currentUserId as Long)

        if (organization) {
            JoinOrganizationRequest joinOrganizationRequest = organizationService.validateAndCreateJoinOrganizationRequest(organization, requester, adminEmail)
            render(template: '/user/firmJoinRequestDetails', model: [id          : joinOrganizationRequest.id,
                                                                     profile     : joinOrganizationRequest.employee.profile,
                                                                     organization: organization])
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found.with.ev.id', [evId])
        }
    }

    @Secured([Role.EMPLOYEE])
    def organizationEmployees(Long id, FindOrganizationEmployeeCommand command) {
        Organization organization = Organization.get(id)
        if (organization) {
            permissionsService.assertIsActive(springSecurityService.currentUser, organization)

            List<GroovyRowResult> employeeList
            int totalCount

            (employeeList, totalCount) = organizationService.getOrganizationEmployees(organization, command)

            response.setIntHeader('X-total-count', totalCount)
            response.setHeader('Access-Control-Expose-Headers', 'X-total-count')

            render(view: '/employee/permissions', model: [employeeList: employeeList])
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found', [id])
        }
    }

    @Secured([Role.EMPLOYEE])
    def list(OrganizationMenuCommand command) {
        User user = springSecurityService.currentUser as User
        if (command.status != EmployeeStatus.ACTIVE) {
            permissionsService.assertIsPaid(user)
            permissionsService.assertIsActive(user)
        }
        Employee employee = attorneyService.findEmployeeByUser(user.id)

        List<OrganizationEmployee> organizationEmployeeList = organizationService.organizations(employee, command)

        render(view: '/organization/organizationMenus', model: [organizationEmployeeList: organizationEmployeeList])
    }

    @Secured([Role.EMPLOYEE])
    def withdrawInvitation(Long id, Long employeeId) {
        Organization organization = Organization.get(id)
        if (organization == null) {
            renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found', [id])
        }
        Employee employee = Employee.get(employeeId)
        if (employee == null) {
            renderError(HttpStatus.SC_NOT_FOUND, 'employee.not.found', [employeeId])
        }
        if (organization && employee) {
            User user = springSecurityService.currentUser as User
            permissionsService.assertIsActive(user)
            permissionsService.assertIsPaid(user)
            permissionsService.assertIsAdmin(user, organization)
            if (OrganizationEmployee.findByOrganizationAndEmployee(organization, employee) == null) {
                throw ExceptionUtils.createUnProcessableDataException('invitation.already.withdrawn')
            }
            OrganizationEmployee invitee = OrganizationEmployee.findByOrganizationAndEmployeeAndStatus(organization, employee, EmployeeStatus.PENDING)
            if (invitee == null) {
                throw ExceptionUtils.createUnProcessableDataException('invalid.operation')
            }
            organizationService.withdrawInvitation(invitee, user)
            response.status = HttpStatus.SC_OK
        }
    }

}
