package com.easyvisa

import com.easyvisa.document.BaseDocument
import com.easyvisa.document.DocumentMilestone
import com.easyvisa.document.DocumentNote
import com.easyvisa.dto.PackageDocumentProgressDto
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.questionnaire.model.MilestoneType
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus
import org.grails.web.servlet.mvc.exceptions.ControllerExecutionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource

import java.time.LocalDate

@Secured([Role.EV])
@GrailsCompileStatic
class DocumentController implements IErrorHandler {

    PackageDocumentService packageDocumentService
    FileService fileService
    SpringSecurityService springSecurityService
    ApplicantService applicantService
    MessageSource messageSource
    PermissionsService permissionsService

    @Autowired
    PackageQuestionnaireService packageQuestionnaireService

    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchPackageRequiredDocuments(Long packageId) {
        LocalDate currentDate = this.getCurrentDate()
        def packageDocuments = packageDocumentService.fetchPackageRequiredDocuments(packageId, currentDate)
        render packageDocuments as JSON
    }


    @Secured([Role.EMPLOYEE, Role.USER])
    def deleteDocumentAttachments(DocumentAttachmentsCommand attachmentDeleteCommand) {
        final User currentUser = springSecurityService.currentUser as User
        this.validatePackageDocumentAccess(attachmentDeleteCommand.packageId, attachmentDeleteCommand.attachmentRefId, attachmentDeleteCommand.documentType.displayName)
        BaseDocument baseDocument = packageDocumentService.validateAndDeleteDocumentAttachmentFiles(attachmentDeleteCommand, currentUser, this.getCurrentDate())
        render fetchDocumentAttachmentList(baseDocument, attachmentDeleteCommand) as JSON
    }


    @Secured([Role.EMPLOYEE, Role.USER])
    def downloadDocumentAttachments(DocumentAttachmentsCommand documentAttachmentCommand) {
        final User currentUser = springSecurityService.currentUser as User
        this.validatePackageDocumentAccess(documentAttachmentCommand.packageId, documentAttachmentCommand.attachmentRefId,
                documentAttachmentCommand.documentType.displayName, false)
        Map docAttachmentFileData = packageDocumentService.validateAndDownloadDocumentAttachmentFiles(documentAttachmentCommand, currentUser)
        this.sendAsFile(docAttachmentFileData.file, docAttachmentFileData.fileName as String, docAttachmentFileData.contentType as String)
    }


    @Secured([Role.EMPLOYEE, Role.USER])
    def downloadAllDocumentAttachments(DocumentAttachmentCommand documentAttachmentCommand) {
        final User currentUser = springSecurityService.currentUser as User
        this.validatePackageDocumentAccess(documentAttachmentCommand.packageId,
                documentAttachmentCommand.documentType?.name(), documentAttachmentCommand.documentType.displayName, false)
        Map docAttachmentFileData = packageDocumentService.validateAndDownloadAllDocumentAttachments(documentAttachmentCommand, this.getCurrentDate())
        this.sendAsFile(docAttachmentFileData.file, docAttachmentFileData.fileName as String, docAttachmentFileData.contentType as String)
    }


    @Secured([Role.EMPLOYEE, Role.USER])
    def uploadDocumentAttachment(DocumentAttachmentUploadCommand attachmentCommand) {
        final User currentUser = springSecurityService.currentUser as User
        this.validatePackageDocumentAccess(attachmentCommand.packageId, attachmentCommand.attachmentRefId, attachmentCommand.documentType.displayName)
        BaseDocument baseDocument = packageDocumentService.validateAndUploadDocumentAttachment(attachmentCommand, currentUser, this.getCurrentDate())
        render fetchDocumentAttachmentList(baseDocument, attachmentCommand) as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def findDocumentAttachment(DocumentAttachmentFileCommand attachmentCommand) {
        final User currentUser = springSecurityService.currentUser as User
        EasyVisaFile easyVisaFile = packageDocumentService.validateAndFindDocumentAttachmentFile(attachmentCommand, currentUser)
        this.sendAsFile(fileService.getFile(easyVisaFile), easyVisaFile.originalName, easyVisaFile.fileType)
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def findDocumentAttachmentThumbnail(DocumentAttachmentFileCommand attachmentCommand) {
        final User currentUser = springSecurityService.currentUser as User
        EasyVisaFile easyVisaFile = packageDocumentService.validateAndFindDocumentAttachmentFile(attachmentCommand, currentUser)
        this.sendAsFile(fileService.getThumbnailFile(easyVisaFile), easyVisaFile.originalName, easyVisaFile.fileType)
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def saveDocumentNote(DocumentNoteCommand documentNoteCommand) {
        final User currentUser = springSecurityService.currentUser as User
        this.validatePackageDocumentAccess(documentNoteCommand.packageId, documentNoteCommand.documentNoteType.name(), PackageDocumentService.DOCUMENT_NOTE_TYPE)
        DocumentNote documentNote = packageDocumentService.saveDocumentNote(documentNoteCommand, currentUser)
        render packageDocumentService.toUIDocumentNoteData(documentNote) as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def removeDocumentNote(Long packageId, Long documentNoteId) {
        final User currentUser = springSecurityService.currentUser as User
        this.validatePackageDocumentAccess(packageId, documentNoteId as String, PackageDocumentService.DOCUMENT_NOTE_TYPE)
        packageDocumentService.validateAndRemoveDocumentNote(packageId, documentNoteId, currentUser)
        render([id: documentNoteId] as JSON)
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def findDocumentNotes(Long packageId) {
        final User currentUser = springSecurityService.currentUser as User
        def documentNoteListData = packageDocumentService.fetchDocumentNoteList(packageId, currentUser)
        render documentNoteListData as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchPackageUSCISData(Long packageId) {
        final User currentUser = springSecurityService.currentUser as User
        def packageUSCISData = packageDocumentService.fetchPackageUSCISData(packageId, currentUser)
        render packageUSCISData as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def findUSCISForm(FormContinuationSheetPrintCommand formContinuationSheetPrintCommand) {
        List<String> formIdList = formContinuationSheetPrintCommand.formInfoList ?: []
        List<String> continuationSheetIdList = formContinuationSheetPrintCommand.continuationSheetInfoList ?: []
        Integer numberOfOutputFilesCount = [formIdList, continuationSheetIdList].flatten().size()
        this.validatePrintUSCISData(numberOfOutputFilesCount)

        final User currentUser = springSecurityService.currentUser as User
        Map<String, Object> result = packageDocumentService.findUSCISForm(currentUser, formContinuationSheetPrintCommand.packageId,
                formIdList[0], continuationSheetIdList[0])
        this.sendAsFile(result['file'], result['filename'] as String, result['mimetype'] as String)
    }

    private void validatePrintUSCISData(Integer numberOfOutputFilesCount) {
        if (numberOfOutputFilesCount > 1) {
            throw new EasyVisaException('errorCode': HttpStatus.SC_BAD_REQUEST, 'message': "Only one of formId or continuationSheetId should be provided, but not both together")
        } else if (numberOfOutputFilesCount == 0) {
            throw new EasyVisaException('errorCode': HttpStatus.SC_BAD_REQUEST, 'message': "Invalid inputs - Should provide either formId or continuationSheetId, both should not be empty")
        }
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def downloadUSCISForms(FormContinuationSheetPrintCommand formContinuationSheetPrintCommand) {
        List<String> formIdList = formContinuationSheetPrintCommand.formInfoList ?: []
        List<String> continuationSheetIdList = formContinuationSheetPrintCommand.continuationSheetInfoList ?: []
        Integer numberOfOutputFilesCount = [formIdList, continuationSheetIdList].flatten().size()
        if (numberOfOutputFilesCount == 0) {
            throw new EasyVisaException('errorCode': HttpStatus.SC_BAD_REQUEST, 'message': "Invalid inputs - Both 'formInfoList' and 'continuationSheetInfoList' should not be empty")
        }

        this.validatePackageDocumentAccess(formContinuationSheetPrintCommand.packageId, formContinuationSheetPrintCommand.packageId as String, PackageDocumentService.PRINT_DOWNLOAD_USCIS_FORMS, false)
        final User currentUser = springSecurityService.currentUser as User
        if (numberOfOutputFilesCount == 1) {
            Map<String, Object> result = packageDocumentService.findUSCISForm(currentUser, formContinuationSheetPrintCommand.packageId,
                    formIdList[0], continuationSheetIdList[0])
            this.sendAsFile(result['file'], result['filename'] as String, result['mimetype'] as String)
        } else {
            Map uscisFormFileData = packageDocumentService.downloadUSCISForms(currentUser, formContinuationSheetPrintCommand.packageId,
                    formIdList, continuationSheetIdList)
            this.sendAsFile(uscisFormFileData.file, uscisFormFileData.fileName as String, uscisFormFileData.contentType as String)
        }
    }


    @Secured([Role.EMPLOYEE])
    def fetchAllUSCISForms(Long packageId) {
        def uscisForms = packageDocumentService.fetchAllUSCISForms(packageId)
        render uscisForms as JSON
    }


    @Secured([Role.EMPLOYEE])
    def findBlankUSCISForm(Long packageId, String formId) {
        Map result = packageDocumentService.findBlankUSCISForm(packageId, formId)
        this.sendAsFile(result['file'], result['filename'] as String, result['mimetype'] as String)
    }


    @Secured([Role.EMPLOYEE])
    def downloadBlankUSCISForms(Long packageId) {
        List<String> formIdList = params.list('formIdList') ?: []
        if (formIdList.size() == 0) {
            throw new EasyVisaException('errorCode': HttpStatus.SC_BAD_REQUEST, 'message': "Invalid input - 'formIdList' should not be empty")
        }
        Map uscisFormFileData = packageDocumentService.downloadBlankUSCISForms(packageId, formIdList)
        this.sendAsFile(uscisFormFileData.file, uscisFormFileData.fileName as String, uscisFormFileData.contentType as String)
    }


    private sendAsFile(outputFile, String fileName, String contentType) {
        try {
            response.setContentType(contentType)
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName)
            response.addHeader('X-file-name', fileName)
            response.setHeader('Access-Control-Expose-Headers', 'X-file-name')
            render(file: outputFile, contentType: contentType)
        } catch (ControllerExecutionException cee) {
            handleFileDownloadError(cee)
        }
    }

    private fetchDocumentAttachmentList(BaseDocument baseDocument, DocumentAttachmentCommand attachmentCommand) {
        def docAttachedListData = packageDocumentService.fetchDocumentAttachmentList(baseDocument, attachmentCommand)
        return docAttachedListData
    }


    @Secured([Role.EMPLOYEE, Role.USER])
    def saveDocumentMilestone(DocumentMilestoneCommand documentMilestoneCommand) {
        final User currentUser = springSecurityService.currentUser as User
        this.validatePackageDocumentAccess(documentMilestoneCommand.packageId, documentMilestoneCommand.milestoneTypeId, PackageDocumentService.DOCUMENT_MILESTONE_TYPE)
        Map responseData = packageDocumentService.saveDocumentMilestone(documentMilestoneCommand, currentUser)
        MilestoneType milestoneType = packageDocumentService.getMilestoneTypeById(responseData.questVersion as String, documentMilestoneCommand.milestoneTypeId)
        render packageDocumentService.toUIDocumentMilestoneData(milestoneType, [responseData.documentMilestone as DocumentMilestone]) as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def findDocumentMilestones(Long packageId) {
        final User currentUser = springSecurityService.currentUser as User
        def documentMilestoneListData = packageDocumentService.findDocumentMilestones(packageId, currentUser)
        render documentMilestoneListData as JSON
    }


    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchPackageSentDocuments(Long packageId) {
        def packageSentDocuments = packageDocumentService.fetchPackageSentDocuments(packageId)
        render packageSentDocuments as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def saveDocumentActionDate(DocumentActionDateCommand documentActionDateCommand) {
        final User currentUser = springSecurityService.currentUser as User
        this.validatePackageDocumentAccess(documentActionDateCommand.packageId,
                documentActionDateCommand.attachmentRefId, documentActionDateCommand.documentType.displayName)
        render packageDocumentService.saveDocumentActionDate(documentActionDateCommand, currentUser, this.getCurrentDate()) as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchPackageReceivedDocuments(Long packageId) {
        def receivedDocumentListData = packageDocumentService.findPackageReceivedDocuments(packageId)
        render receivedDocumentListData as JSON
    }


    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchDocumentsAccessState(Long id) {
        this.validatePackageReadAccess(id)
        Package aPackage = Package.get(id)
        User user = springSecurityService.currentUser as User
        def documentAccessState = packageDocumentService.fetchDocumentAccessState(aPackage, user)
        render documentAccessState as JSON
    }

    @Secured([Role.ATTORNEY])
    def saveDocumentApproval(DocumentApprovalCommand documentApprovalCommand) {
        final User currentUser = springSecurityService.currentUser as User
        this.validatePackageDocumentAccess(documentApprovalCommand.packageId, documentApprovalCommand.attachmentRefId,
                documentApprovalCommand.documentType.displayName)
        render packageDocumentService.saveDocumentApproval(documentApprovalCommand, currentUser, this.getCurrentDate()) as JSON
    }

    private void validatePackageDocumentAccess(Long packageId,
                                               String sourceFieldId, String documentType,
                                               Boolean canCheckReadOnly = Boolean.TRUE) {
        Package packageInstance = Package.get(packageId)
        if (packageInstance == null) {
            throw ExceptionUtils.createNotFoundException('package.not.found.with.id')
        }

        User user = springSecurityService.currentUser as User
        Map documentAccessState = this.packageDocumentService.fetchDocumentAccessState(packageInstance, user)
        Map accessState = documentAccessState.get(documentType) as HashMap
        if (!accessState.access || (accessState.readOnly && canCheckReadOnly)) {
            throw ExceptionUtils.createAccessDeniedException('user.not.allowed.to.access.document', null, [sourceFieldId], ErrorMessageType.INVALID_DOCUMENTPORTAL_ACCESS)
        }
    }


    @Secured(value = [Role.USER], httpMethod = 'GET')
    def progress(Long id) {
        Package aPackage = Package.get(id)
        if (aPackage) {
            Applicant applicant = applicantService.findApplicantByUser(springSecurityService.currentUserId as Long)
            if (!(applicant && aPackage.doesUserBelongToPackage(applicant))) {
                throw ExceptionUtils.createAccessDeniedException('user.not.allowed.to.access.package')
            }
            LocalDate currentDate = this.getCurrentDate()
            List<PackageDocumentProgressDto> result = packageDocumentService.calculateProgress(aPackage, currentDate)
            render result as JSON
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'package.not.found.with.id')
        }
    }


    private validatePackageReadAccess(Long packageId) {
        Package aPackage = Package.get(packageId)
        if (aPackage) {
            User user = springSecurityService.currentUser as User
            permissionsService.validatePackageReadAccess(user, aPackage)
        } else {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'package.not.found.with.id')
        }
    }

    private LocalDate getCurrentDate() {
        String calenderDay = request.getHeader("Current-Date")
        LocalDate calendar = (calenderDay != null) ? DateUtil.localDate(calenderDay) : DateUtil.today()
        return calendar
    }
}
