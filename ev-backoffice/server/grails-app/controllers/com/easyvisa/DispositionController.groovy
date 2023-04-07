package com.easyvisa

import com.easyvisa.document.DocumentAttachment
import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.ExceptionUtils
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic
import org.apache.http.HttpStatus
import org.grails.web.servlet.mvc.exceptions.ControllerExecutionException
import org.springframework.context.MessageSource

import java.time.LocalDate

@Secured([Role.EV])
@CompileStatic
class DispositionController implements IErrorHandler {

    SpringSecurityService springSecurityService
    PackageDocumentService packageDocumentService
    OrganizationService organizationService
    PermissionsService permissionsService
    MessageSource messageSource

    @Secured([Role.EMPLOYEE])
    def userDispositions(DispositionCommand dispositionCommand) {
        Organization organization = dispositionCommand.findOrganization()
        User currUser = springSecurityService.currentUser as User
        permissionsService.assertIsActive(currUser, organization)
        LegalRepresentative legalRepresentative = dispositionCommand.findAttorney()
        validateAttorney(legalRepresentative, organization)
        PaginationResponseDto responseDto = packageDocumentService.getUserDispositions(dispositionCommand,
                legalRepresentative, organization)
        response.setIntHeader('X-total-count', responseDto.totalCount)
        response.setHeader('Access-Control-Expose-Headers', 'X-total-count')
        render responseDto.result as JSON
    }

    @Secured([Role.EMPLOYEE])
    def edit(Long id, DispositionCommand dispositionCommand) {
        DocumentAttachment documentAttachment = DocumentAttachment.get(id)
        this.validateDocumentAttachmentProcess(documentAttachment, dispositionCommand)

        final User currentUser = springSecurityService.currentUser as User
        Organization organization = documentAttachment.documentReference.aPackage.organization
        permissionsService.validateEmployeeNonTraineePosition(currentUser, organization, 'user.is.not.active.or.trainee.in.organization')
        LegalRepresentative legalRepresentative = documentAttachment.documentReference.aPackage.attorney
        permissionsService.assertIsActive(legalRepresentative)
        def userDispositionData = packageDocumentService.updateDocumentDisposition(documentAttachment, dispositionCommand,
                currentUser, this.getCurrentDate())
        Integer userDispositionCount = packageDocumentService.getDocumentAttachmentDispositionCount(legalRepresentative, organization)
        response.setIntHeader('X-total-count', userDispositionCount)
        response.setHeader('Access-Control-Expose-Headers', 'X-total-count')
        render userDispositionData as JSON
    }

    @Secured([Role.EMPLOYEE])
    def get(Long id) {
        User currUser = springSecurityService.currentUser as User
        DocumentAttachment documentAttachment = DocumentAttachment.get(id)
        if (!documentAttachment) {
            return renderError(HttpStatus.SC_NOT_FOUND, 'disposition.not.found.with.id')
        }
        Organization organization = documentAttachment.documentReference.aPackage.organization
        permissionsService.assertIsActive(currUser, organization)
        LegalRepresentative legalRepresentative = documentAttachment.documentReference.aPackage.attorney
        permissionsService.assertIsActive(legalRepresentative)
        try {
            def dispositionData = packageDocumentService.getDocumentDisposition(documentAttachment)
            this.sendAsFile(dispositionData['file'], dispositionData['fileName'] as String,
                    dispositionData['contentType'] as String, documentAttachment.id as Long)
        } catch (EasyVisaException e) {
            renderError(e.errorCode, e.errorMessageCode)
        }
    }

    private sendAsFile(outputFile, String fileName, String contentType, Long dispositionId) {
        try {
            response.setContentType(contentType)
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName)
            this.addHeaderFields('X-disposition-id', dispositionId)
            this.addHeaderFields('X-file-name', fileName)
            render(file: outputFile, contentType: contentType)
        } catch (ControllerExecutionException cee) {
            handleFileDownloadError(cee)
        }
    }

    private addHeaderFields(String fieldName, def fieldValue) {
        response.addHeader(fieldName, "${fieldValue}")
        response.setHeader('Access-Control-Expose-Headers', fieldName)
    }

    private void validateAttorney(LegalRepresentative legalRepresentative, Organization organization) {
        if (legalRepresentative != null) {
            permissionsService.assertIsActive(legalRepresentative)
            if (!organizationService.doesEmployeeBelongToOrganization(legalRepresentative.id, organization.id)) {
                throw ExceptionUtils.createAccessDeniedException('employee.not.in.organization')
            }
        }
    }


    private void validateDocumentAttachmentProcess(DocumentAttachment documentAttachment, DispositionCommand dispositionCommand) {
        if (documentAttachment == null) {
            throw ExceptionUtils.createNotFoundException('disposition.not.found.with.id')
        }

        if (documentAttachment.isApproved != null) {
            String dispositionType = dispositionCommand.approve ? "Approve" : "Reject"
            throw ExceptionUtils.createUnProcessableDataException('disposition.already.processed', null, [dispositionType])
        }
    }

    private LocalDate getCurrentDate() {
        String calenderDay = request.getHeader("Current-Date")
        LocalDate calendar = (calenderDay != null) ? DateUtil.localDate(calenderDay) : DateUtil.today()
        return calendar
    }
}
