package com.easyvisa

import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.rest.token.AccessToken
import org.apache.http.HttpStatus

@GrailsCompileStatic
class EmployeeController implements IErrorHandler {

    SpringSecurityService springSecurityService
    PermissionsService permissionsService
    AttorneyService attorneyService
    EmployeeService employeeService
    def messageSource
    EasyVisaAuthTokenJsonRenderer accessTokenJsonRenderer

    @Secured([Role.EMPLOYEE])
    def create(Long id, CreateEmployeeCommand createEmployeeCommand) {
        if (createEmployeeCommand.email == null) {
            throw ExceptionUtils.createUnProcessableDataException('employee.work.email.required')
        }
        if (createEmployeeCommand.position == null) {
            throw ExceptionUtils.createUnProcessableDataException('position.not.provided')
        }
        Organization organization = Organization.get(id)
        if (organization) {
            User user = springSecurityService.currentUser as User
            Employee employee = attorneyService.findEmployeeByUser(user.id)

            permissionsService.assertIsAdmin(user, organization)
            OrganizationEmployee organizationEmployee = employeeService.createEmployee(createEmployeeCommand, employee, organization)
            response.status = HttpStatus.SC_CREATED
            render(template: '/user/orgEmployee', model: [organizationEmployee: organizationEmployee])
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    @Secured([Role.EMPLOYEE])
    def update(Long id, Long employeeId, EmployeeCommand employeeCommand) {
        if (employeeCommand.email == null) {
            throw ExceptionUtils.createUnProcessableDataException('employee.work.email.required')
        }
        User user = springSecurityService.currentUser as User
        Organization organization = Organization.get(id)
        Employee employee = Employee.get(employeeId)
        if (organization && employee) {
            permissionsService.assertIsAdmin(user, organization)
            OrganizationEmployee organizationEmployee = employeeService.updateEmployee(employeeCommand, employee,
                    organization, user)
            response.status = HttpStatus.SC_OK
            render(template:'/user/orgEmployee', model:[organizationEmployee:organizationEmployee])
        } else {
            render status:HttpStatus.SC_NOT_FOUND
        }
    }

    @Secured([Role.EMPLOYEE])
    def show(Long id, Long employeeId) {
        User user = springSecurityService.currentUser as User
        Organization organization = Organization.get(id)
        Employee employee = Employee.get(employeeId)
        if (organization && employee) {
            permissionsService.assertIsAdmin(user, organization)
            OrganizationEmployee organizationEmployee = employeeService.getLastOrganizationEmployee(organization, employee)
            response.status = HttpStatus.SC_OK
            render(template: '/user/orgEmployee', model: [organizationEmployee: organizationEmployee])
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    @Secured([Role.EMPLOYEE])
    def convertToAttorney(ConvertEmployeeToAttorneyCommand convertCommand) {
        if (convertCommand.validate()) {
            if (convertCommand.paymentMethod.validate()) {
                AccessToken accessToken = attorneyService.convertToAttorney(springSecurityService.currentUser as User, convertCommand)
                render accessTokenJsonRenderer.generateJson(accessToken)
            } else {
                respond convertCommand.paymentMethod.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
            }
        } else {
            respond convertCommand.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

}
