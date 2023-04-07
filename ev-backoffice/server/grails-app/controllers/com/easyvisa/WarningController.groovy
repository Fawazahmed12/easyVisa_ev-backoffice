package com.easyvisa

import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.utils.ExceptionUtils
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

class WarningController implements IErrorHandler {

    SpringSecurityService springSecurityService
    AlertService alertService
    OrganizationService organizationService
    AttorneyService attorneyService
    MessageSource messageSource

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def userWarnings(WarningCommand warningCommand) {
        Organization organization = warningCommand.findOrganization()
        Long currentUserId = (Long) springSecurityService.currentUserId
        if (!organizationService.doesEmployeeBelongToOrganization(attorneyService.findEmployeeByUser(currentUserId).id,
                organization.id)) {
            throw ExceptionUtils.createAccessDeniedException('user.is.not.active.in.organization')
        }
        LegalRepresentative attorney = warningCommand.findAttorney()
        validateAttorney(attorney, organization)
        PaginationResponseDto responseDto = alertService.getUserWarnings(warningCommand, attorney, organization)
        response.setIntHeader('X-total-count', responseDto.totalCount)
        response.setHeader('Access-Control-Expose-Headers', 'X-total-count')
        render(view:'/list', model:[templatePath:'/alert/warning', list:responseDto.result, var:'warning'])
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def deleteWarnings() {
        List<Long> warningIds = params.list('ids')
        List<Warning> warnings = Warning.getAll(warningIds)
        alertService.deleteMultipleEvSystemMessages(warnings, null)
        render([deletedWarningIds: warnings*.id] as JSON)
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def get(Long id) {
        Warning warning = Warning.get(id)
        if (warning) {
            render(template: '/warning/warning', model: [warning: warning])
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'warning.not.found.with.id')
        }
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def edit(Long id, WarningCommand warningCommand) {
        Warning warning = Warning.get(id)
        if (warning) {
            warning = alertService.updateEvSystemMessage(warning, warningCommand.read, warningCommand.starred)
            render(template: '/alert/warning', model: [warning: warning])
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'warning.not.found.with.id')
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
