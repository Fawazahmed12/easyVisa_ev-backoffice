package com.easyvisa

import com.easyvisa.document.*
import com.easyvisa.dto.EmailDto
import com.easyvisa.dto.PackageDocumentProgressDto
import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.enums.*
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.SectionCompletionStatus
import com.easyvisa.questionnaire.answering.DocumentHelpNodeInstance
import com.easyvisa.questionnaire.answering.DocumentNodeInstance
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.MilestoneReminderEvaluationContext
import com.easyvisa.questionnaire.model.*
import com.easyvisa.questionnaire.services.ContinuationSheetService
import com.easyvisa.questionnaire.services.DocumentService
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.questionnaire.services.RuleActionHandler
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.DateUtils
import com.easyvisa.utils.PdfUtils
import com.easyvisa.utils.StringUtils
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import groovy.transform.CompileDynamic
import groovy.transform.TypeCheckingMode
import org.apache.commons.io.Charsets
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.MailMessage
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
@GrailsCompileStatic
class PackageDocumentService {

    public static final int BATCH_SIZE = 100
    public static final String DOCUMENT_NOTE_TYPE = 'DOCUMENT_NOTE_TYPE'
    public static final String DOCUMENT_MILESTONE_TYPE = 'DOCUMENT_MILESTONE_TYPE'
    public static final String PRINT_DOWNLOAD_USCIS_FORMS = 'PRINT_DOWNLOAD_USCIS_FORMS'
    @Autowired
    DocumentService documentService

    @Autowired
    ApplicantService applicantService

    @Autowired
    QuestionnaireService questionnaireService

    @Autowired
    FileService fileService

    @Autowired
    ContinuationSheetService continuationSheetService

    @Autowired
    PdfPopulationService pdfPopulationService

    @Autowired
    OrganizationService organizationService

    @Autowired
    EvMailService evMailService

    @Autowired
    PageRenderer groovyPageRenderer

    @Autowired
    EmailVariableService emailVariableService

    @Autowired
    AttorneyService attorneyService

    @Autowired
    AlertService alertService

    @Autowired
    PackageService packageService

    @Autowired
    RuleActionHandler ruleActionHandler
    @Autowired
    AsyncService asyncService

    @Transactional
    @CompileDynamic
    def fetchPackageRequiredDocuments(Long packageId, LocalDate currentDate) {
        Package aPackage = Package.get(packageId)
        def packageRequiredDocuments = []
        List<ImmigrationBenefit> benefits = aPackage.orderedBenefits
        ImmigrationBenefitCategory directBenefitCategory = aPackage.directBenefit.category
        String benefitCategoryId = directBenefitCategory.getEasyVisaId()
        Petitioner petitioner = aPackage.petitioner
        def requiredDocAttachmentMap = this.findRequiredDocAttachmentMapper(aPackage)
        List<DocumentNodeInstance> documentNodeInstanceList
        int packageApplicantIndex = 0;
        if (petitioner) {
            Applicant sponsorApplicant = petitioner.applicant
            documentNodeInstanceList = documentService.documentsByBenefitCategory(packageId, sponsorApplicant.id, benefitCategoryId,
                    ApplicantType.Petitioner, currentDate)
            packageRequiredDocuments.add([applicantType    : ApplicantType.Petitioner.name(),
                                           applicantTitle   : ApplicantType.Petitioner.name(),
                                           applicantId      : sponsorApplicant.id,
                                           applicantName    : sponsorApplicant.name,
                                           direct           : false,
                                           order            : packageApplicantIndex++,
                                           requiredDocuments: this.toUIRequiredDocumentData(documentNodeInstanceList, sponsorApplicant, requiredDocAttachmentMap)])
        }

        benefits.eachWithIndex { it, index ->
            ImmigrationBenefitCategory benefitCategory = it.category // Needs to be Fixed
            benefitCategoryId = benefitCategory.getEasyVisaId()
            Long applicantId = it.applicant.id
            ApplicantType documentApplicantType = it.direct ? ApplicantType.Beneficiary : ApplicantType.Derivative_Beneficiary
            documentNodeInstanceList = documentService.documentsByBenefitCategory(packageId, applicantId, benefitCategoryId,
                    documentApplicantType, currentDate)
            packageRequiredDocuments.add([applicantType    : ApplicantType.Beneficiary.name(),
                                           applicantTitle   : it.direct ? ApplicantType.Beneficiary.name() : getApplicantTitle(index, benefits.size()),
                                           applicantId      : applicantId,
                                           applicantName    : it.applicant.name,
                                           direct           : it.direct,
                                           order            : packageApplicantIndex++,
                                           requiredDocuments: this.toUIRequiredDocumentData(documentNodeInstanceList, it.applicant, requiredDocAttachmentMap)])
        }
        return packageRequiredDocuments.sort {
            it.order
        }
    }


    @Transactional
    BaseDocument validateAndUploadDocumentAttachment(DocumentAttachmentUploadCommand attachmentCommand,
                                                     User currentUser, LocalDate currentDate) {
        this.uploadDocumentAttachment(attachmentCommand, currentUser, currentDate)
    }


    @Transactional
    EasyVisaFile validateAndFindDocumentAttachmentFile(DocumentAttachmentFileCommand attachmentCommand, User currentUser) {
        Map docAttachedListData = this.findDocumentAttachmentList(attachmentCommand)
        List attachments = docAttachedListData.attachments as List
        if (!attachments.any { it['id'] == attachmentCommand.attachmentId }) {
            throw new EasyVisaException('errorCode': HttpStatus.SC_UNAUTHORIZED, 'message': "Not Authorized to fetch document attachment")
        }
        EasyVisaFile easyVisaFile = EasyVisaFile.findById(attachmentCommand.attachmentId)
        return easyVisaFile
    }

    /**
     *
     * If a panel was marked as complete (which can ONLY be done by attorneys), then if a client changes ANYTHING in the panel,
     * then the check mark must be deleted (changed back to a hyphen),
     * because the attorney must again disposition the panel to ensure that the client did not make a mistake by adding or deleting documents in the panel.
     */
    @Transactional
    BaseDocument validateAndDeleteDocumentAttachmentFiles(DocumentAttachmentsCommand attachmentDeleteCommand,
                                                          User currentUser, LocalDate currentDate) {
        def docAttachmentListInputData = this.validateDocumentAttachments(attachmentDeleteCommand)
        Long[] attachmentIdList = attachmentDeleteCommand.attachmentIdList
        attachmentIdList.each { Long easyVisaFileId ->
            this.deleteDocumentAttachmentByEasyVisaFileId(easyVisaFileId)
        }

        BaseDocument baseDocument = docAttachmentListInputData['baseDocument'] as BaseDocument
        Package aPackage = attachmentDeleteCommand.easyVisaPackage
        Applicant applicant = this.applicantService.findApplicantByUser(currentUser.id)
        if (applicant && aPackage.doesUserBelongToPackage(applicant)) {
            switch (attachmentDeleteCommand.documentType) {
                case DocumentType.REQUIRED_DOCUMENT:
                    baseDocument = documentService.saveRequiredDocument(attachmentDeleteCommand.requiredDocument, currentUser)
                    break
                case DocumentType.DOCUMENT_SENT_TO_US:
                    baseDocument = documentService.saveSentDocument(attachmentDeleteCommand.sentDocument, currentUser)
                    break
                case DocumentType.DOCUMENT_RECEIVED_FROM_US:
                    baseDocument = documentService.saveReceivedDocument(attachmentDeleteCommand.receivedDocument, currentUser)
                    break
            }
        }
        this.documentUpdateCompletionHandler(aPackage, baseDocument.documentType, currentUser, currentDate)
        baseDocument
    }

    private validateDocumentAttachments(DocumentAttachmentsCommand documentAttachmentsCommand) {
        BaseDocument baseDocument = this.findBaseDocument(documentAttachmentsCommand)
        Map docAttachedListData = this.fetchDocumentAttachmentList(baseDocument, documentAttachmentsCommand)
        List attachments = docAttachedListData.attachments as List
        Map<Long, List<Object>> attachmentIdMapper = attachments.groupBy { it['id'] as Long }
        Long[] attachmentIdList = documentAttachmentsCommand.attachmentIdList
        attachmentIdList.each { Long attachmentId ->
            if (!attachmentIdMapper[attachmentId]) {
                throw new EasyVisaException('errorCode': HttpStatus.SC_UNAUTHORIZED, 'message': "Not Authorized to fetch document attachment")
            }
        }
        [baseDocument: baseDocument, docAttachedListData: docAttachedListData]
    }

    @Transactional
    Map validateAndDownloadDocumentAttachmentFiles(DocumentAttachmentsCommand documentAttachmentCommand, User currentUser) {
        def docAttachmentListInputData = this.validateDocumentAttachments(documentAttachmentCommand)
        Long[] attachmentIdList = documentAttachmentCommand.attachmentIdList
        if (attachmentIdList.size() == 1) {
            EasyVisaFile easyVisaFile = EasyVisaFile.findById(attachmentIdList[0])
            return [file: fileService.getFile(easyVisaFile), fileName: easyVisaFile.originalName, contentType: easyVisaFile.fileType]
        }
        this.generateDocumentAttachmentZipFile(documentAttachmentCommand, docAttachmentListInputData['docAttachedListData'] as Map)
    }

    @Transactional
    Map validateAndDownloadAllDocumentAttachments(DocumentAttachmentCommand documentAttachmentCommand, LocalDate currentDate) {
        Package aPackage = documentAttachmentCommand.easyVisaPackage
        Applicant applicant = documentAttachmentCommand.applicant

        Map downloadAllDependentData = [:]
        if (documentAttachmentCommand.documentType == DocumentType.REQUIRED_DOCUMENT) {
            downloadAllDependentData = getDownloadAllRequiredDocData(aPackage, applicant, currentDate)
        } else if (documentAttachmentCommand.documentType == DocumentType.DOCUMENT_SENT_TO_US) {
            downloadAllDependentData = getDownloadAllSentDocData(aPackage, applicant)
        } else if (documentAttachmentCommand.documentType == DocumentType.DOCUMENT_RECEIVED_FROM_US) {
            downloadAllDependentData = getDownloadAllReceivedDocData(aPackage, applicant)
        }

        Map<Long, String> documentRefIdToSourceIdMapper = downloadAllDependentData.documentRefIdToSourceIdMapper as Map<Long, String>
        Map<String, String> sourceIdToFolderNameMap = downloadAllDependentData.sourceIdToFolderNameMap as Map<String, String>
        Map<Long, List<DocumentAttachment>> docAttachmentsByRefId = downloadAllDependentData.docAttachmentsByRefId as Map<Long, List<DocumentAttachment>>;

        String quarantineFolderPath = fileService.getQurantineFolderPath()
        Files.createDirectories(Paths.get(quarantineFolderPath))
        String zipFileName = UUID.randomUUID().toString()
        String zipFilePath = "${quarantineFolderPath}/${zipFileName}"
        Files.createDirectories(Paths.get(zipFilePath))

        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ZipOutputStream zipOut = new ZipOutputStream(baos)
        Map<String, AtomicInteger> attachmentNameEntryMapper = [:]
        docAttachmentsByRefId.each { Long documentRefId, List<DocumentAttachment> documentAttachmentList ->
            String documentId = documentRefIdToSourceIdMapper[documentRefId] // It can be either FormId, DocumentId or xx etc
            String folderName = sourceIdToFolderNameMap[documentId] // folderName
            String targetFolderName = folderName.replaceAll("[^A-Za-z-0-9]", "_")
            // Create folder with 'documentName'
            String targetFolderPath = Paths.get("${zipFilePath}/${targetFolderName}").toString()
            Files.createDirectories(Paths.get(targetFolderPath))

            this.copyAttachmentsToTargetFolder(documentAttachmentList, targetFolderPath, attachmentNameEntryMapper)
            this.zipFile(new File(targetFolderPath), zipOut)
        }
        zipOut.close()
        ByteArrayInputStream outputBytesArrayStream = new ByteArrayInputStream(baos.toByteArray())
        File mainDir = new File(zipFilePath)
        mainDir.exists() && mainDir.directory && mainDir.deleteDir()
        return [file: outputBytesArrayStream, fileName: zipFileName, contentType: PdfUtils.ZIP_MIMETYPE]
    }

    private copyAttachmentsToTargetFolder(List<DocumentAttachment> documentAttachmentList, String targetFolder,
                                          Map<String, AtomicInteger> attachmentNameEntryMapper) {
        documentAttachmentList.each { DocumentAttachment documentAttachment ->
            EasyVisaFile easyVisaFile = documentAttachment.file
            File sourceFile = fileService.getFile(easyVisaFile)
            //check if file
            if (sourceFile?.isFile()) {
                String fileName = easyVisaFile.originalName as String
                AtomicInteger attachmentEntryIncrementer = attachmentNameEntryMapper[fileName] ?: AtomicInteger.newInstance()
                int attachmentEntryIndex = attachmentEntryIncrementer.incrementAndGet()
                attachmentNameEntryMapper[fileName] = attachmentEntryIncrementer
                String attachmentName = (attachmentEntryIndex == 1) ? fileName : "${attachmentEntryIndex}_${fileName}"
                File targetFile = new File("${targetFolder}/${attachmentName}")
                Files.copy(sourceFile.toPath(), targetFile.toPath())
            }
        }
    }

    private Map getDownloadAllRequiredDocData(Package aPackage, Applicant applicant, LocalDate currentDate) {
        List<RequiredDocument> requiredDocuments = RequiredDocument.findAllByAPackageAndApplicant(aPackage, applicant)
        Map<Long, String> documentRefIdToSourceIdMapper = [:]
        requiredDocuments.each { RequiredDocument requiredDocument ->
            documentRefIdToSourceIdMapper[requiredDocument.id] = requiredDocument.documentId
        }

        List<DocumentNodeInstance> documentNodeInstanceList = documentService.findAllDocuments(aPackage.id, currentDate)
        Map<String, String> sourceIdToFolderNameMap = [:]
        documentNodeInstanceList.each { DocumentNodeInstance documentNodeInstance ->
            sourceIdToFolderNameMap[documentNodeInstance.id] = documentNodeInstance.name
        }

        List<DocumentAttachment> documentAttachments = this.getDocumentAttachmentList(DocumentType.REQUIRED_DOCUMENT, requiredDocuments as List<BaseDocument>)
        Map<Long, List<DocumentAttachment>> docAttachmentsByRefId = documentAttachments.groupBy {
            it.documentReference.id
        }

        return [
                'documentRefIdToSourceIdMapper': documentRefIdToSourceIdMapper,
                'sourceIdToFolderNameMap'      : sourceIdToFolderNameMap,
                'docAttachmentsByRefId'        : docAttachmentsByRefId
        ]
    }

    private Map getDownloadAllSentDocData(Package aPackage, Applicant applicant) {
        List<SentDocument> sentDocumentList = SentDocument.findAllByAPackageAndApplicant(aPackage, applicant)
        Map<Long, String> documentRefIdToSourceIdMapper = [:]
        sentDocumentList.each { SentDocument sentDocument ->
            documentRefIdToSourceIdMapper[sentDocument.id] = sentDocument.formId
        }

        List formDataList = this.fetchAllUSCISForms(aPackage.id)
        Map<String, String> sourceIdToFolderNameMap = [:]
        formDataList.each { Object formData ->
            sourceIdToFolderNameMap[formData['formId'] as String] = formData['formName'] as String
        }

        List<DocumentAttachment> documentAttachments = this.getDocumentAttachmentList(DocumentType.DOCUMENT_SENT_TO_US, sentDocumentList as List<BaseDocument>)
        Map<Long, List<DocumentAttachment>> docAttachmentsByRefId = documentAttachments.groupBy {
            it.documentReference.id
        }

        return [
                'documentRefIdToSourceIdMapper': documentRefIdToSourceIdMapper,
                'sourceIdToFolderNameMap'      : sourceIdToFolderNameMap,
                'docAttachmentsByRefId'        : docAttachmentsByRefId
        ]
    }

    private Map getDownloadAllReceivedDocData(Package aPackage, Applicant applicant) {
        List<ReceivedDocument> receivedDocumentList = ReceivedDocument.findAllByAPackageAndApplicant(aPackage, applicant)
        Map<Long, String> documentRefIdToSourceIdMapper = [:]
        receivedDocumentList.each { ReceivedDocument receivedDocument ->
            documentRefIdToSourceIdMapper[receivedDocument.id] = receivedDocument.receivedDocumentType.name()
        }

        ReceivedDocumentType[] receivedDocumentTypes = ReceivedDocumentType.values()
        Map<String, String> sourceIdToFolderNameMap = [:]
        receivedDocumentTypes.each { ReceivedDocumentType receivedDocumentType ->
            sourceIdToFolderNameMap[receivedDocumentType.name()] = receivedDocumentType.description
        }

        List<DocumentAttachment> documentAttachments = this.getDocumentAttachmentList(DocumentType.DOCUMENT_RECEIVED_FROM_US, receivedDocumentList as List<BaseDocument>)
        Map<Long, List<DocumentAttachment>> docAttachmentsByRefId = documentAttachments.groupBy {
            it.documentReference.id
        }

        return [
                'documentRefIdToSourceIdMapper': documentRefIdToSourceIdMapper,
                'sourceIdToFolderNameMap'      : sourceIdToFolderNameMap,
                'docAttachmentsByRefId'        : docAttachmentsByRefId
        ]
    }


    private void zipFile(File folderToZip, ZipOutputStream zipFileStream) throws IOException {
        String folderName = folderToZip.getName()
        File[] children = folderToZip.listFiles()
        for (File childFile : children) {
            String filePath = "${folderName}/${childFile.getName()}"
            zipFileStream.putNextEntry(new ZipEntry(filePath))
            InputStream inputStream = new FileInputStream(childFile)
            zipFileStream.write(inputStream.getBytes())
            zipFileStream.closeEntry()
            inputStream.close()
        }
    }


    private Map generateDocumentAttachmentZipFile(DocumentAttachmentsCommand documentAttachmentCommand,
                                                  Map docAttachedListData) {
        def attachmentIdMapper = [:]
        List attachments = docAttachedListData.attachments as List
        attachments.each {
            Long attachmentId = it['id'] as Long
            attachmentIdMapper[attachmentId] = it
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ZipOutputStream zipFileStream = new ZipOutputStream(baos, Charsets.UTF_8)
        Map<String, AtomicInteger> attachmentNameEntryMapper = [:]
        Long[] attachmentIdList = documentAttachmentCommand.attachmentIdList
        attachmentIdList.each {
            def attachment = attachmentIdMapper[it]
            EasyVisaFile easyVisaFile = EasyVisaFile.findById(attachment['id'] as Long)
            File file = fileService.getFile(easyVisaFile)
            //check if file
            if (file.isFile()) {
                String fileName = attachment['fileName'] as String
                AtomicInteger attachmentEntryIncrementer = attachmentNameEntryMapper[fileName] ?: AtomicInteger.newInstance()
                int attachmentEntryIndex = attachmentEntryIncrementer.incrementAndGet()
                attachmentNameEntryMapper[fileName] = attachmentEntryIncrementer
                String attachmentName = (attachmentEntryIndex == 1) ? fileName : "${attachmentEntryIndex}_${fileName}"
                zipFileStream.putNextEntry(new ZipEntry(attachmentName))
                InputStream inputStream = new FileInputStream(file)
                zipFileStream.write(inputStream.getBytes())
                zipFileStream.closeEntry()
                inputStream.close()
            }
        }
        zipFileStream.close()
        String zipFileName = "${documentAttachmentCommand.attachmentRefId}_attachments"
        return [file: new ByteArrayInputStream(baos.toByteArray()), fileName: zipFileName, contentType: PdfUtils.ZIP_MIMETYPE]
    }


    @Transactional
    Map findDocumentAttachmentList(DocumentAttachmentCommand documentAttachmentCommand) {
        BaseDocument baseDocument = this.findBaseDocument(documentAttachmentCommand)
        return this.fetchDocumentAttachmentList(baseDocument, documentAttachmentCommand)
    }


    private BaseDocument findBaseDocument(DocumentAttachmentCommand documentAttachmentCommand) {
        BaseDocument baseDocument
        switch (documentAttachmentCommand.documentType) {
            case DocumentType.REQUIRED_DOCUMENT:
                baseDocument = RequiredDocument.findByAPackageAndApplicantAndDocumentId(documentAttachmentCommand.easyVisaPackage,
                        documentAttachmentCommand.applicant, documentAttachmentCommand.attachmentRefId)
                break
            case DocumentType.DOCUMENT_SENT_TO_US:
                baseDocument = SentDocument.findByAPackageAndApplicantAndFormId(documentAttachmentCommand.easyVisaPackage,
                        documentAttachmentCommand.applicant, documentAttachmentCommand.attachmentRefId)
                break
            case DocumentType.DOCUMENT_RECEIVED_FROM_US:
                // TODO test whether we qre getting receivedDocumentType or NOT
                ReceivedDocumentType receivedDocumentType = ReceivedDocumentType[documentAttachmentCommand.attachmentRefId] as ReceivedDocumentType
                baseDocument = ReceivedDocument.findByAPackageAndApplicantAndReceivedDocumentType(documentAttachmentCommand.easyVisaPackage,
                        documentAttachmentCommand.applicant, receivedDocumentType)
                break
        }
        baseDocument
    }


    @Transactional
    private BaseDocument uploadDocumentAttachment(DocumentAttachmentUploadCommand attachmentCommand,
                                                  User currentUser, LocalDate currentDate) {
        BaseDocument baseDocument
        switch (attachmentCommand.documentType) {
            case DocumentType.REQUIRED_DOCUMENT:
                baseDocument = documentService.saveRequiredDocument(attachmentCommand.requiredDocument, currentUser)
                break
            case DocumentType.DOCUMENT_SENT_TO_US:
                baseDocument = documentService.saveSentDocument(attachmentCommand.sentDocument, currentUser)
                break
            case DocumentType.DOCUMENT_RECEIVED_FROM_US:
                baseDocument = documentService.saveReceivedDocument(attachmentCommand.receivedDocument, currentUser)
                break
        }
        EasyVisaFile easyVisaFile = this.uploadAttachment(baseDocument, currentUser, attachmentCommand.attachment)
        DocumentAttachment documentAttachment = addDocumentAttachment(attachmentCommand.documentType, baseDocument,
                easyVisaFile, currentUser)
        documentAttachment.save(flush: true)
        Package aPackage = attachmentCommand.easyVisaPackage
        if (currentUser.id != aPackage.attorney.user.id) {
            sendDocumentAlert(aPackage.id, easyVisaFile, documentAttachment, currentUser)
        }
        this.documentUpdateCompletionHandler(aPackage, baseDocument.documentType, currentUser, currentDate)
        return baseDocument
    }

    private void sendDocumentAlert(Long packageId, EasyVisaFile easyVisaFile,
                                   DocumentAttachment documentAttachment,
                                   User currentUser) {
        Package aPackage = Package.get(packageId)
        EasyVisaSystemMessageType alertType = EasyVisaSystemMessageType.PACKAGE_APPLICANT_DISPOSITION
        Profile userProfile = Profile.findByUser(currentUser);
        String body = alertService.renderTemplate(alertType.templatePath,
                [applicantName: userProfile.fullName, documentName: easyVisaFile.originalName,
                 panelName    : getDocumentPanelName(documentAttachment, aPackage)])
        alertService.createAlert(alertType, aPackage.attorney.user, userProfile.fullName, body)
    }

    private EasyVisaFile uploadAttachment(BaseDocument baseDocument, User currentUser, MultipartFile multipartFile) {
        String subDirectoryPath = fileService.combinePackageFileFolder(baseDocument.aPackage.id, baseDocument.applicant.id)
//                "${FileService.PACKAGES_FOLDER_PREFIX}/${baseDocument.aPackageId}/${baseDocument.applicantId}"
        fileService.validateAndUploadFile(multipartFile, currentUser.profile, subDirectoryPath)
    }

    private DocumentAttachment addDocumentAttachment(DocumentType documentType, BaseDocument baseDocument, EasyVisaFile easyVisaFile, User currentUser) {
        DocumentAttachment documentAttachment = new DocumentAttachment(
                documentReference: baseDocument,
                documentType: documentType,
                file: easyVisaFile,
                createdBy: currentUser,
                updatedBy: currentUser
        )
        documentAttachment.save(failOnError: true)
    }

    // If a package has more than one Derivative Beneficiary, then they should be numbered sequentially (i.e. Derivative Beneficiary 1, Derivative Beneficiary 2, Derivative Beneficiary 3)
    private String getApplicantTitle(int iterationIndex, int totalBenefits) {
        ApplicantType derivativeBeneficiary = ApplicantType.Derivative_Beneficiary
        if (totalBenefits <= 2) {
            return derivativeBeneficiary.getValue()
        }
        return derivativeBeneficiary.getValue() + " " + iterationIndex
    }


    private Map findRequiredDocAttachmentMapper(Package aPackage) {
        RequiredDocument[] requiredDocuments = RequiredDocument.findAllByAPackage(aPackage)
        List<DocumentAttachment> documentAttachments = this.getNonDispositionedDocumentAttachmentList(aPackage, DocumentType.REQUIRED_DOCUMENT)
        Map<Long, List<DocumentAttachment>> docAttachmentsByRefId = documentAttachments.groupBy {
            it.documentReference.id
        }
        Map<String, Map> requiredDocAttachmentMap = [:]
        requiredDocuments.each { RequiredDocument requiredDocument ->
            DocumentAttachment[] requiredDocAttachments = docAttachmentsByRefId[requiredDocument.id] ?: []
            String requiredDocAttachmentKey = "${requiredDocument.documentId}_${requiredDocument.applicantId}"
            requiredDocAttachmentMap[requiredDocAttachmentKey] =
                    [
                            isApproved            : requiredDocument.isApproved,
                            requiredDocAttachments: requiredDocAttachments
                    ]
        }
        requiredDocAttachmentMap
    }


    @Transactional
    private List<DocumentAttachment> getNonDispositionedDocumentAttachmentList(Package aPackage, DocumentType documentType) {
        List<BaseDocument> baseDocuments = BaseDocument.findAllByAPackageAndDocumentType(aPackage, documentType)
        return this.getDocumentAttachmentList(documentType, baseDocuments)
    }


    private List<DocumentAttachment> getDocumentAttachmentList(DocumentType documentType, List<BaseDocument> baseDocuments) {
        if (baseDocuments.size() == 0) {
            return []
        }
        List<DocumentAttachment> documentAttachmentList = DocumentAttachment.createCriteria().list {
            'in'('documentReference', baseDocuments)
            eq('documentType', documentType)
            or {
                eq('isApproved', true)
                isNull('isApproved')
            }
        } as List<DocumentAttachment>
        return documentAttachmentList
    }


    private toUIRequiredDocumentData(List<DocumentNodeInstance> documentNodeInstanceList, Applicant applicant,
                                     def requiredDocAttachmentMap) {
        documentNodeInstanceList.collect {
            String requiredDocAttachmentMapperId = "${it.id}_${applicant.id}"
            def requiredDocAttachmentMapObject = requiredDocAttachmentMap[requiredDocAttachmentMapperId] ?: [isApproved: false, requiredDocAttachments: []]
            List<DocumentAttachment> documentAttachmentList = requiredDocAttachmentMapObject['requiredDocAttachments'] as List<DocumentAttachment>
            return [
                    id         : it.id,
                    name       : it.name,
                    description: it.description,
                    helpText   : toUITooltip(it),
                    isApproved : requiredDocAttachmentMapObject['isApproved'] ?: false,
                    attachments: this.toUIAttachmentData(documentAttachmentList)
            ]
        }
    }

    private toUITooltip(DocumentNodeInstance documentNodeInstance) {
        String breakStr = '<br>'
        List<String> documentHelps = []
        for (EasyVisaNodeInstance easyVisaNodeInstance : documentNodeInstance.children) {
            DocumentHelpNodeInstance documentHelpNodeInstance = (DocumentHelpNodeInstance) easyVisaNodeInstance
            documentHelps.add(documentHelpNodeInstance.tooltip)
            documentHelps.add(breakStr)
        }
        String documentHelpText = documentHelps.join(' ')
        return documentHelpText
    }

    @Transactional
    Map fetchDocumentAttachmentList(def baseDocument, DocumentAttachmentCommand documentAttachmentListCommand) {
        List<DocumentAttachment> documentAttachmentList = []
        if (baseDocument) {
            documentAttachmentList = DocumentAttachment.createCriteria().list {
                eq('documentReference', baseDocument)
                eq('documentType', baseDocument['documentType'])
                or {
                    eq('isApproved', true)
                    isNull('isApproved')
                }
            } as List<DocumentAttachment>
        }
        def docAttachedListData = [
                id          : documentAttachmentListCommand.attachmentRefId,
                applicantId : documentAttachmentListCommand.applicantId,
                documentType: documentAttachmentListCommand.documentType.name(),
                isApproved  : baseDocument['documentType'] != DocumentType.DOCUMENT_SENT_TO_US ? baseDocument['isApproved'] : null,
                attachments : this.toUIAttachmentData(documentAttachmentList),
        ]
        return docAttachedListData
    }


    private toUIAttachmentData(List<DocumentAttachment> documentAttachmentList) {
        documentAttachmentList.sort { it.dateCreated }
        documentAttachmentList.collect {
            EasyVisaFile file = it.file
            return [
                    id         : file.id,
                    fileName   : file.originalName,
                    fileType   : file.fileType,
                    approved   : it.isApproved,
                    dateCreated: it.dateCreated,
                    lastUpdated: it.lastUpdated
            ]
        }
    }


    private deleteDocumentAttachmentByEasyVisaFileId(Long easyVisaFileId) {
        EasyVisaFile easyVisaFile = EasyVisaFile.findById(easyVisaFileId)
        DocumentAttachment documentAttachment = DocumentAttachment.findByFile(easyVisaFile)
        documentAttachment.delete(failOnError: true)
        fileService.deleteEasyVisaFile(easyVisaFile)
    }


    void deleteDocumentAttachment(DocumentAttachment documentAttachment) {
        EasyVisaFile easyVisaFile = documentAttachment.file
        documentAttachment.delete(failOnError: true)
        fileService.deleteEasyVisaFile(easyVisaFile)
    }


    @Transactional
    DocumentNote saveDocumentNote(DocumentNoteCommand documentNoteCommand, User currentUser) {
        DocumentNote documentNote = documentNoteCommand.getDocumentNote(currentUser)
        documentNote.save(failOnError: true)
    }


    @Transactional
    def validateAndRemoveDocumentNote(Long packageId, Long documentNoteId, User currentUser) {
        DocumentNote documentNote = DocumentNote.findById(documentNoteId)
        if (!documentNote) {
            throw new EasyVisaException('errorCode': HttpStatus.SC_NOT_FOUND, 'message': "document-note not found for the given id")
        }
        if (documentNote.aPackageId != packageId) {
            throw new EasyVisaException('errorCode': HttpStatus.SC_UNAUTHORIZED, 'message': "Not Authorized to remove document-note for the given packageId")
        }
        documentNote.delete(failOnError: true)
    }


    @Transactional
    def fetchPackageUSCISData(Long packageId, User currentUser) {
        Package aPackage = Package.get(packageId)
        List<Map> packageForms = this.fetchPackageForms(aPackage)
        def packageContinuationSheets = this.fetchPackageContinuationSheets(aPackage, packageForms)
        return [
                packageForms             : packageForms,
                packageContinuationSheets: packageContinuationSheets
        ]
    }


    @Transactional
    Map findUSCISForm(User currentUser, Long packageId,
                      String formId,
                      String continuationSheetId) {
        Package aPackage = Package.get(packageId)
        List<Applicant> applicants = aPackage.getApplicants()
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        Map<String, Set<Section>> formToSectionListMapper = this.questionnaireService.fetchFormToSectionListMapper(questionnaireVersion.questVersion)
        Map<String, SectionCompletionStatus> sectionCompletionStatusMapper = this.fetchSectionCompletionStatusMapper(aPackage)

        if (formId != null) {
            List<Long> formApplicantIdList = applicants.collect { it.id }
            Boolean hasFormCompleted = this.hasQuestionniareFormCompleted(aPackage, formId, formToSectionListMapper, sectionCompletionStatusMapper)
            Map<String, Object> formPdfResult = pdfPopulationService.generateFormPdfByApplicants(packageId, formApplicantIdList, formId, hasFormCompleted)
            return formPdfResult
        }

        Form sourceForm = this.questionnaireService.fetchFormByContinuationSheet(questionnaireVersion.questVersion, continuationSheetId)
        ContinuationSheet continuationSheet = this.continuationSheetService.continuationSheetById(questionnaireVersion.questVersion, continuationSheetId)
        Long applicantId = this.getContinuationSheetApplicant(aPackage, continuationSheet)
        Boolean hasCompleted = this.hasQuestionniareFormCompleted(aPackage, sourceForm.id, formToSectionListMapper, sectionCompletionStatusMapper)
        Map<String, Object> continuationSheetPdfResult = pdfPopulationService.getPdf(packageId, applicantId, null, continuationSheetId, null, hasCompleted)
        return continuationSheetPdfResult
    }


    @Transactional
    Map downloadUSCISForms(User currentUser, Long packageId,
                           List<String> formIdList,
                           List<String> continuationSheetIdList) {
        Package aPackage = Package.get(packageId)
        List<Applicant> applicants = aPackage.getApplicants()
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        Map<String, Set<Section>> formToSectionListMapper = this.questionnaireService.fetchFormToSectionListMapper(questionnaireVersion.questVersion)
        Map<String, SectionCompletionStatus> sectionCompletionStatusMapper = this.fetchSectionCompletionStatusMapper(aPackage)

        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ZipOutputStream zipFileStream = new ZipOutputStream(baos, Charsets.UTF_8)
        List<Long> applicantIdList = applicants.collect { it.id }
        formIdList.each { formId ->
            Boolean hasFormCompleted = this.hasQuestionniareFormCompleted(aPackage, formId, formToSectionListMapper, sectionCompletionStatusMapper)
            Map<String, Object> uscisFormResult = pdfPopulationService.generateFormPdfByApplicants(packageId, applicantIdList, formId, hasFormCompleted)
            this.addToZipFile(uscisFormResult, zipFileStream)
        }

        continuationSheetIdList.each { continuationSheetId ->
            Form sourceForm = this.questionnaireService.fetchFormByContinuationSheet(questionnaireVersion.questVersion, continuationSheetId)
            ContinuationSheet continuationSheet = this.continuationSheetService.continuationSheetById(questionnaireVersion.questVersion, continuationSheetId)
            Long applicantId = this.getContinuationSheetApplicant(aPackage, continuationSheet)
            Boolean hasCompleted = this.hasQuestionniareFormCompleted(aPackage, sourceForm.id, formToSectionListMapper, sectionCompletionStatusMapper)
            Map<String, Object> uscisContinuationSheetResult = pdfPopulationService.getPdf(packageId, applicantId, null, continuationSheetId, null, hasCompleted)
            this.addToZipFile(uscisContinuationSheetResult, zipFileStream)
        }
        zipFileStream.close()
        String zipFileName = UUID.randomUUID().toString()
        return [file: new ByteArrayInputStream(baos.toByteArray()), fileName: zipFileName, contentType: PdfUtils.ZIP_MIMETYPE]
    }

    private Long getContinuationSheetApplicant(Package aPackage, ContinuationSheet continuationSheet) {
        Applicant petitionerApplicant = aPackage.petitioner?.applicant
        Applicant principalBenficiary = aPackage.getPrincipalBeneficiary()
        Long applicantId = continuationSheet.applicantType == ApplicantType.Petitioner.value ? petitionerApplicant?.id : principalBenficiary?.id
        return applicantId
    }

    @Transactional
    List fetchAllUSCISForms(Long packageId) {
        QuestionnaireVersion questionnaireVersion = questionnaireService.findQuestionnaireVersion(packageId)
        List<Form> formList = this.questionnaireService.findAllForms(questionnaireVersion.questVersion)
        List formDataList = formList.collect {
            return [
                    formId     : it.id,
                    formName   : it.name,
                    displayText: it.displayText
            ]
        }
        return formDataList
    }


    @Transactional
    Map findBlankUSCISForm(Long packageId, String formId) {
        QuestionnaireVersion questionnaireVersion = questionnaireService.findQuestionnaireVersion(packageId)
        Map<String, Object> result = pdfPopulationService.getBlankFormPdf(formId, questionnaireVersion.questVersion)
        return result
    }


    @Transactional
    Map downloadBlankUSCISForms(Long packageId, List<String> formIdList) {
        if (formIdList.size() == 1) {
            Map<String, Object> result = this.findBlankUSCISForm(packageId, formIdList[0])
            return [file: result['file'], fileName: result['filename'], contentType: result['mimetype']]
        }

        QuestionnaireVersion questionnaireVersion = questionnaireService.findQuestionnaireVersion(packageId)
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ZipOutputStream zipFileStream = new ZipOutputStream(baos, Charsets.UTF_8)
        formIdList.each { formId ->
            Map<String, Object> uscisBlankFormResult = pdfPopulationService.getBlankFormPdf(formId, questionnaireVersion.questVersion)
            this.addToZipFile(uscisBlankFormResult, zipFileStream)
        }
        zipFileStream.close()
        String zipFileName = UUID.randomUUID().toString()
        return [file: new ByteArrayInputStream(baos.toByteArray()), fileName: zipFileName, contentType: PdfUtils.ZIP_MIMETYPE]
    }

    def addToZipFile(Map<String, Object> uscisFormResult, ZipOutputStream zipFileStream) {
        InputStream inputStream = uscisFormResult['file'] as InputStream
        String fileName = uscisFormResult['filename'] as String
        zipFileStream.putNextEntry(new ZipEntry(fileName))
        zipFileStream.write(inputStream.getBytes())
        zipFileStream.closeEntry()
        inputStream.close()
    }


    List<Map> fetchPackageForms(Package aPackage) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(aPackage.id)
        Map<String, Set<Section>> formToSectionListMapper = this.questionnaireService.fetchFormToSectionListMapper(questionnaireVersion.questVersion)
        Map<String, SectionCompletionStatus> sectionCompletionStatusMapper = this.fetchSectionCompletionStatusMapper(aPackage)

        Comparator formOrderComparator = [compare: { Form a, Form b -> a.order <=> b.order }] as Comparator
        Set<Form> packageFormList = new TreeSet<>(formOrderComparator)
        List<ImmigrationBenefit> benefits = aPackage.orderedBenefits
        benefits.eachWithIndex { it, index ->
            ImmigrationBenefitCategory benefitCategory = it.category // Needs to be Fixed
            List<Form> formList = this.documentService.findFormsByBenefitCategory(questionnaireVersion.questVersion, benefitCategory.easyVisaId)
            packageFormList.addAll(formList)
        }

        Applicant petitionerApplicant = aPackage.petitioner?.applicant
        Applicant principalBenficiary = aPackage.getPrincipalBeneficiary()
        List<Answer> maritalStatusAnswerList = this.fetchMaritalStatusAnswers(aPackage)
        List<Map> packageForms = packageFormList.collect {
            Form form = it as Form
            String signerName = form.applicantType == ApplicantType.Petitioner.value ? petitionerApplicant?.name : principalBenficiary?.name
            Boolean hasFormCompleted = this.hasQuestionniareFormCompleted(aPackage, form.id, formToSectionListMapper, sectionCompletionStatusMapper)
            return [
                    formId              : form.id,
                    formName            : form.name,
                    displayText         : form.displayText,
                    displayFormId       : this.getDisplayFormId(form.displayText),
                    displayFormName     : this.getDisplayFormName(form.displayText),
                    hasCompleted        : hasFormCompleted,
                    signerName          : signerName,
                    questionConflictData: this.fetchQuestionConflictData(aPackage, form, maritalStatusAnswerList)
            ]
        } as List<Map>
        packageForms
    }


    Map<String, SectionCompletionStatus> fetchSectionCompletionStatusMapper(Package aPackage) {
        List<SectionCompletionStatus> sectionCompletionStatusList = SectionCompletionStatus.findAllByPackageId(aPackage.id)
        Map<String, SectionCompletionStatus> sectionCompletionStatusMapper = new HashMap<String, SectionCompletionStatus>()
        sectionCompletionStatusList.each { sectionCompletionStatus ->
            String sectionCompletionStatusKey = sectionCompletionStatus.applicantId + "_" + sectionCompletionStatus.sectionId
            sectionCompletionStatusMapper[sectionCompletionStatusKey] = sectionCompletionStatus
        }
        return sectionCompletionStatusMapper
    }


    Boolean hasQuestionniareFormCompleted(Package aPackage, String formId,
                                          Map<String, Set<Section>> formToSectionListMapper,
                                          Map<String, SectionCompletionStatus> sectionCompletionStatusMapper) {
        Applicant petitionerApplicant = aPackage.petitioner?.applicant
        Boolean hasPetitionerDataFilled = petitionerApplicant ? this.hasApplicantQuestionniareFormCompleted(formId, petitionerApplicant.id,
                ApplicantType.Petitioner.value, formToSectionListMapper, sectionCompletionStatusMapper) : true
        Applicant principalBenficiary = aPackage.getPrincipalBeneficiary()
        Boolean hasBeneficiaryDataFilled = this.hasApplicantQuestionniareFormCompleted(formId, principalBenficiary.id,
                ApplicantType.Beneficiary.value, formToSectionListMapper, sectionCompletionStatusMapper)
        return hasPetitionerDataFilled && hasBeneficiaryDataFilled
    }

    Boolean hasApplicantQuestionniareFormCompleted(String formId, Long applicantId, String sectionApplicantType,
                                                   Map<String, Set<Section>> formToSectionListMapper,
                                                   Map<String, SectionCompletionStatus> sectionCompletionStatusMapper) {
        Set<Section> formAssociatedSections = formToSectionListMapper[formId] ?: Collections.<Section> emptySet()
        if (!formAssociatedSections) {
            return true // Some forms don't have any Sections... Example: 600, 600A and 693
        }

        // We dont display some sections like Admin, but we will populate pdfFields for this forms. So for this computation we need to ignore this section
        List<Section> answeredSectionList = formAssociatedSections.stream()
                .filter({ section -> return (section.applicantType == sectionApplicantType && section.id != "Sec_admin") })
                .collect(Collectors.toList())

        Boolean hasQuestionniareCompleted = answeredSectionList.every { section ->
            SectionCompletionStatus sectionCompletionStatus = sectionCompletionStatusMapper[applicantId + "_" + section.id]
            return (sectionCompletionStatus && sectionCompletionStatus.completionState == SectionCompletionState.COMPLETED)
        }
        return hasQuestionniareCompleted
    }

    def fetchPackageContinuationSheets(Package aPackage, List<Map> packageForms) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(aPackage.id)
        Map<String, Set<ContinuationSheet>> formToContinuationSheetListMapper = this.documentService.fetchFormToContinuationSheetListMapper(questionnaireVersion.questVersion)

        def packageContinuationSheets = []
        List<Answer> maritalStatusAnswerList = this.fetchMaritalStatusAnswers(aPackage)
        packageForms.eachWithIndex { Map packageFormData, Integer index ->
            Set<ContinuationSheet> continuationSheetList = formToContinuationSheetListMapper[packageFormData['formId'] as String] ?: Collections.<ContinuationSheet> emptySet()
            continuationSheetList.sort { it.order }
            def formContinuationSheetList = continuationSheetList.collect {
                return [
                        continuationSheetId  : it.id,
                        continuationSheetName: it.sheetName,
                        continuationSheetPage: it.page,
                        continuationSheetPart: it.part,
                        continuationSheetItem: it.item,
                        formId               : packageFormData['formId'] as String,
                        formName             : packageFormData['formName'] as String,
                        hasCompleted         : packageFormData['hasCompleted'] as Boolean,
                        questionConflictData : this.fetchQuestionConflictData(aPackage, packageFormData, maritalStatusAnswerList)
                ]
            }
            packageContinuationSheets.addAll(formContinuationSheetList as List)
        }
        packageContinuationSheets
    }

    private List<Answer> fetchMaritalStatusAnswers(Package aPackage) {
        String maritalStatusFieldPath = "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781"
        List<Answer> maritalStatusAnswerList = Answer.findAllByPackageIdAndPathIlike(aPackage.id, maritalStatusFieldPath)
        return maritalStatusAnswerList
    }


    def fetchQuestionConflictData(Package aPackage, def form, List<Answer> maritalStatusAnswerList) {
        def questionConflictData = null
        List<Applicant> applicants = aPackage.getApplicants()
        applicants.each { Applicant applicant ->
            List<Answer> applicantMaritalStatusAnswerList = maritalStatusAnswerList.findAll {
                it.applicantId == applicant.id
            } ?: []
            if (this.hasQuestionConflictInApplicant(aPackage, applicant, applicantMaritalStatusAnswerList)) {
                questionConflictData = [
                        responseLabel     : this.getMaritalStatusAnswer(aPackage, applicant, applicantMaritalStatusAnswerList),
                        applicantName     : applicant.name,
                        formName          : form['displayText'],
                        representativeName: aPackage?.attorney?.profile?.name,
                        representativeType: aPackage.attorney?.representativeType?.name(),
                        officeNumber      : aPackage.attorney?.officePhone,
                        mobileNumber      : aPackage.attorney?.mobilePhone
                ]
            }
        }
        questionConflictData
    }


    private Boolean hasQuestionConflictInApplicant(Package aPackage, Applicant applicant, List<Answer> maritalStatusAnswerList) {
        String maritalStatusAnswerValue = this.getMaritalStatusAnswer(aPackage, applicant, maritalStatusAnswerList)
        if (maritalStatusAnswerValue == RelationshipTypeConstants.LEGALLY_SEPERATED.value || maritalStatusAnswerValue == RelationshipTypeConstants.MARRIAGE_ANULLED.value) {
            return true
        }
        return false
    }

    private String getMaritalStatusAnswer(Package aPackage, Applicant applicant, List<Answer> maritalStatusAnswerList) {
        if (maritalStatusAnswerList.isEmpty()) {
            return
        }
        Answer maritalStatusAnswer = maritalStatusAnswerList[0]
        String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue())
        return maritalStatusAnswerValue
    }


    @Transactional
    def fetchDocumentNoteList(Long packageId, User currentUser) {
        Package aPackage = Package.get(packageId)
        List<DocumentNote> documentNoteList = DocumentNote.findAllByAPackage(aPackage)
        documentNoteList.sort { it.dateCreated }
        documentNoteList.collect { this.toUIDocumentNoteData(it) }
    }


    def toUIDocumentNoteData(DocumentNote documentNote) {
        Profile creator = documentNote.creator
        return [
                id              : documentNote.id,
                subject         : documentNote.subject,
                documentNoteType: documentNote.documentNoteType.name(),
                createdDate     : this.formatNoteDate(documentNote.dateCreated),
                createdTime     : this.formatTime(documentNote.dateCreated),
                creator         : [firstName: creator.firstName, middleName: creator.middleName, lastName: creator.lastName]
        ]
    }

    private String formatDate(Date date) {
        date.format(DateUtil.PDF_FORM_DATE_FORMAT)
    }

    private String formatNoteDate(Date date) {
        date.format(DateUtil.DOC_NOTE_DATE_FORMAT)
    }

    private String formatTime(Date date) {
        date.format(DateUtil.DOC_NOTE_TIME_FORMAT)
    }


    @Transactional
    def fetchPackageSentDocuments(Long packageId) {
        def packageSentDocuments = []
        Package aPackage = Package.get(packageId)
        List<ImmigrationBenefit> benefits = aPackage.orderedBenefits
        benefits.eachWithIndex { it, index ->
            Long applicantId = it.applicant.id
            def sentDocuments = []
            if (it.direct) {
                QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
                ImmigrationBenefitCategory benefitCategory = it.category // Needs to be Fixed
                String benefitCategoryId = benefitCategory.getEasyVisaId()
                Map sentDocAttachmentMap = this.findSentDocAttachmentMapper(aPackage)
                List<Form> formList = documentService.findFormsByBenefitCategory(questionnaireVersion.questVersion, benefitCategoryId)
                sentDocuments = this.toUISentDocumentData(formList, it.applicant, sentDocAttachmentMap)
            }
            packageSentDocuments.push([
                    applicantType: it.direct ? ApplicantType.Beneficiary.name() : getApplicantTitle(index, benefits.size()),
                    applicantId  : applicantId,
                    applicantName: it.applicant.name,
                    sentDocuments: sentDocuments])
        }
        packageSentDocuments
    }


    private Map findSentDocAttachmentMapper(Package aPackage) {
        SentDocument[] sentDocuments = SentDocument.findAllByAPackage(aPackage)
        List<DocumentAttachment> documentAttachments = this.getNonDispositionedDocumentAttachmentList(aPackage, DocumentType.DOCUMENT_SENT_TO_US)
        Map<Long, List<DocumentAttachment>> docAttachmentsByRefId = documentAttachments.groupBy {
            it.documentReference.id
        }
        Map<String, Map> sentDocAttachmentMap = [:]
        sentDocuments.each { SentDocument sentDocument ->
            DocumentAttachment[] sentDocAttachments = docAttachmentsByRefId[sentDocument.id] ?: []
            String sentDocAttachmentKey = "${sentDocument.formId}_${sentDocument.applicantId}"
            sentDocAttachmentMap[sentDocAttachmentKey] = [
                    actionDate        : sentDocument.sentDate,
                    isApproved        : sentDocument.isApproved,
                    sentDocAttachments: sentDocAttachments
            ]
        }
        sentDocAttachmentMap
    }


    private toUISentDocumentData(List<Form> formList, Applicant applicant, Map sentDocAttachmentMap) {
        formList.collect {
            String sentDocAttachmentMapperId = "${it.id}_${applicant.id}"
            def sentDocAttachmentMapObject = sentDocAttachmentMap[sentDocAttachmentMapperId] ?: [actionDate: '', isApproved: null, sentDocAttachments: []]
            List<DocumentAttachment> documentAttachmentList = sentDocAttachmentMapObject['sentDocAttachments'] as List<DocumentAttachment>
            return [
                    id         : it.id,
                    name       : it.name,
                    isApproved : sentDocAttachmentMapObject['isApproved'],
                    actionDate : sentDocAttachmentMapObject['actionDate'] ? this.formatDate(sentDocAttachmentMapObject['actionDate'] as Date) : '',
                    attachments: this.toUIAttachmentData(documentAttachmentList)
            ]
        }
    }


    @Transactional
    def findDocumentMilestones(Long packageId, User currentUser) {
        Package aPackage = Package.get(packageId)
        List<DocumentMilestone> documentMilestoneList = DocumentMilestone.findAllByAPackage(aPackage)

        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        ImmigrationBenefitCategory directBenefitCategory = aPackage.directBenefit.category
        String benefitCategoryId = directBenefitCategory.getEasyVisaId()
        List<MilestoneType> milestoneTypeList = this.documentService.findMilestoneTypesByBenefitCategory(questionnaireVersion.questVersion, benefitCategoryId)
        milestoneTypeList.sort { it.order }
        milestoneTypeList.collect { this.toUIDocumentMilestoneData(it, documentMilestoneList) }
    }

    def toUIDocumentMilestoneData(MilestoneType milestoneType, List<DocumentMilestone> documentMilestoneList) {
        DocumentMilestone documentMilestone = documentMilestoneList.find {
            it.milestoneTypeId == milestoneType.id
        }
        return [
                milestoneTypeId: milestoneType.id,
                description    : milestoneType.displayText,
                dataLabel      : milestoneType.dateLabel,
                milestoneDate  : documentMilestone?.milestoneDate?.format(DateUtil.PDF_FORM_DATE_FORMAT),
        ]
    }

    @Transactional
    Map saveDocumentMilestone(DocumentMilestoneCommand documentMilestoneCommand, User currentUser) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(documentMilestoneCommand.packageId)
        DocumentMilestone documentMilestone = documentService.saveDocumentMilestone(documentMilestoneCommand.documentMilestone, currentUser)
        MilestoneType milestoneType = this.getMilestoneTypeById(questionnaireVersion?.questVersion, documentMilestoneCommand.milestoneTypeId)
        if (milestoneType?.reminderRule) {
            MilestoneReminderEvaluationContext ruleEvaluationContext = new MilestoneReminderEvaluationContext(documentMilestoneCommand.easyVisaPackage, documentMilestone, milestoneType)
            this.ruleActionHandler.executeMilestoneReminderRule(milestoneType.reminderRule, ruleEvaluationContext)
        }
        return [
                questVersion     : questionnaireVersion?.questVersion,
                documentMilestone: documentMilestone
        ]
    }


    @Transactional
    def saveDocumentActionDate(DocumentActionDateCommand documentActionDateCommand, User currentUser, LocalDate currentDate) {
        Date actionDate
        switch (documentActionDateCommand.documentType) {
            case DocumentType.DOCUMENT_SENT_TO_US:
                SentDocument sentDocument = documentActionDateCommand.sentDocument
                SentDocument savedSentDocument = documentService.saveSentDocument(sentDocument, currentUser)
                actionDate = savedSentDocument.sentDate
                this.documentUpdateCompletionHandler(sentDocument.aPackage, sentDocument.documentType, currentUser, currentDate)
                break
            case DocumentType.DOCUMENT_RECEIVED_FROM_US:
                ReceivedDocument receivedDocument = documentActionDateCommand.receivedDocument
                ReceivedDocument savedReceivedDocument = documentService.saveReceivedDocument(receivedDocument, currentUser)
                actionDate = savedReceivedDocument.receivedDate
                break

        }
        return fetchDocumentActionDate(actionDate, documentActionDateCommand)
    }


    private fetchDocumentActionDate(Date actionDate, DocumentActionDateCommand documentActionDateCommand) {
        def documentActionDateData = [
                id          : documentActionDateCommand.attachmentRefId,
                applicantId : documentActionDateCommand.applicantId,
                documentType: documentActionDateCommand.documentType.name(),
                actionDate  : this.formatDate(actionDate)
        ]
        return documentActionDateData
    }


    @Transactional
    def findPackageReceivedDocuments(Long packageId) {
        Package aPackage = Package.get(packageId)
        ImmigrationBenefit directBenefit = aPackage.directBenefit
        Map receivedDocAttachmentMap = this.findReceivedDocAttachmentMapper(aPackage)
        ReceivedDocumentType[] receivedDocumentTypes = ReceivedDocumentType.values()
        receivedDocumentTypes.sort { it.order }
        Applicant applicant = directBenefit.applicant
        return [
                applicantType    : ApplicantType.Beneficiary.name(),
                applicantId      : applicant.id,
                applicantName    : applicant.name,
                receivedDocuments: this.toUIReceivedDocumentData(receivedDocumentTypes, applicant, receivedDocAttachmentMap)
        ]
    }


    private Map findReceivedDocAttachmentMapper(Package aPackage) {
        ReceivedDocument[] receivedDocuments = ReceivedDocument.findAllByAPackage(aPackage)
        List<DocumentAttachment> documentAttachments = this.getNonDispositionedDocumentAttachmentList(aPackage, DocumentType.DOCUMENT_RECEIVED_FROM_US)
        Map<Long, List<DocumentAttachment>> docAttachmentsByRefId = documentAttachments.groupBy {
            it.documentReference.id
        }
        Map<String, Map> receivedDocAttachmentMap = [:]
        receivedDocuments.each { ReceivedDocument receivedDocument ->
            DocumentAttachment[] receivedDocAttachments = docAttachmentsByRefId[receivedDocument.id] ?: []
            String receivedDocAttachmentKey = "${receivedDocument.receivedDocumentType.name()}_${receivedDocument.applicantId}"
            receivedDocAttachmentMap[receivedDocAttachmentKey] = [
                    actionDate            : receivedDocument.receivedDate,
                    isApproved            : receivedDocument.isApproved,
                    receivedDocAttachments: receivedDocAttachments
            ]
        }
        receivedDocAttachmentMap
    }


    private toUIReceivedDocumentData(ReceivedDocumentType[] receivedDocumentTypes, Applicant applicant,
                                     Map receivedDocAttachmentMap) {
        receivedDocumentTypes.collect {
            ReceivedDocumentType receivedDocumentType = it as ReceivedDocumentType
            String receivedDocAttachmentMapperId = "${receivedDocumentType.name()}_${applicant.id}"
            def receivedDocAttachmentMapObject = receivedDocAttachmentMap[receivedDocAttachmentMapperId] ?: [actionDate: '', isApproved: null, receivedDocAttachments: []]
            List<DocumentAttachment> documentAttachmentList = receivedDocAttachmentMapObject['receivedDocAttachments'] as List<DocumentAttachment>
            return [
                    id                  : receivedDocumentType.name(),
                    receivedDocumentType: receivedDocumentType.name(),
                    description         : receivedDocumentType.description,
                    helpText            : receivedDocumentType.helpText,
                    isApproved          : receivedDocAttachmentMapObject['isApproved'] as Boolean,
                    actionDate          : receivedDocAttachmentMapObject['actionDate'] ? this.formatDate(receivedDocAttachmentMapObject['actionDate'] as Date) : '',
                    attachments         : this.toUIAttachmentData(documentAttachmentList)
            ]
        }
    }

    @Transactional
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    PaginationResponseDto getUserDispositions(DispositionCommand dispositionCommand,
                                              LegalRepresentative legalRepresentative, Organization organization) {
        PaginationResponseDto responseDto = new PaginationResponseDto()
        List<DocumentAttachment> documentAttachmentList = DocumentAttachment.createCriteria()
                .list(dispositionCommand.paginationParams) {
            createAlias('documentReference', 'docRef')
            createAlias('docRef.aPackage', 'package')
            createAlias('package.attorney', 'attorney')
            createAlias('attorney.profile', 'profile')
            isNull('isApproved')
            eq('package.organization', organization)
            not { 'in'("package.status",[PackageStatus.LEAD, PackageStatus.TRANSFERRED]) }
            if (legalRepresentative) {
                eq('package.attorney', legalRepresentative)
            }
            if (dispositionCommand.sort == 'representative') {
                order('profile.lastName', dispositionCommand.sortOrder)
                order('profile.firstName', dispositionCommand.sortOrder)
            } else {
                order(dispositionCommand.sortFieldName, dispositionCommand.sortOrder)
            }
        }
        responseDto.result = documentAttachmentList.collect { this.toUIDocumentDispositionData(it) }
        responseDto.totalCount = this.getDocumentAttachmentDispositionCount(legalRepresentative, organization)
        responseDto
    }

    @Transactional
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    Integer getDocumentAttachmentDispositionCount(LegalRepresentative legalRepresentative, Organization organization) {
        DocumentAttachment.createCriteria().get {
            projections {
                count('id')
            }
            createAlias('documentReference', 'docRef')
            createAlias('docRef.aPackage', 'package')
            isNull('isApproved')
            eq('package.organization', organization)
            not { 'in'("package.status",[PackageStatus.LEAD, PackageStatus.TRANSFERRED]) }
            if (legalRepresentative) {
                eq('package.attorney', legalRepresentative)
            }
        } as Integer
    }

    private toUIDocumentDispositionData(DocumentAttachment documentAttachment) {
        Package aPackage = documentAttachment.documentReference.aPackage
        Applicant applicant = documentAttachment.documentReference.applicant
        String clientNames = aPackage.applicants.sort { it.id }.collect {
            it.profile?.lastName + ', ' + it.profile?.firstName
        }.join(' + ')
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        return [
                id                : documentAttachment.id,
                applicantId       : applicant.id,
                applicantName     : clientNames,
                representativeId  : aPackage.attorney.id,
                representativeName: aPackage.attorney.profile.title,
                fileName          : documentAttachment.file.originalName,
                benefitCategory   : immigrationBenefit?.category?.name(),
                createdDate       : documentAttachment.dateCreated,
                panelName         : this.getDocumentPanelName(documentAttachment, aPackage)
        ]
    }


    private String getDocumentPanelName(DocumentAttachment documentAttachment, Package aPackage) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(aPackage.id)
        DocumentType documentType = documentAttachment.documentType
        String panelName = documentType.getPanelName()
        switch (documentType) {
            case DocumentType.REQUIRED_DOCUMENT:
                RequiredDocument requiredDocument = RequiredDocument.findById(documentAttachment.documentReference.id)
                panelName = this.documentService.findDocumentById(questionnaireVersion.questVersion,
                        requiredDocument?.documentId)?.name ?: documentType.getPanelName()
                break
            case DocumentType.DOCUMENT_SENT_TO_US:
                SentDocument sentDocument = SentDocument.findById(documentAttachment.documentReference.id)
                panelName = this.documentService.findFormById(questionnaireVersion.questVersion,
                        sentDocument?.formId)?.name ?: documentType.getPanelName()
                break
            case DocumentType.DOCUMENT_RECEIVED_FROM_US:
                ReceivedDocument receivedDocument = ReceivedDocument.findById(documentAttachment.documentReference.id)
                panelName = receivedDocument?.receivedDocumentType?.description ?: documentType.getPanelName()
                break
        }
        return panelName
    }

    /**
     *
     * For 'Signed Documents' panel, we don't have panel complete mark (hyphen/tick).
     * So here we are treating this panel as complete only if all of its attachments are marked as approve.
     * Thats why, here every time if user has approved 'Signed Documents' attachment, then we are invoking  'documentUpdateCompletionHandler'
     */
    @Transactional
    def updateDocumentDisposition(DocumentAttachment documentAttachment, DispositionCommand dispositionCommand,
                                  User currentUser, LocalDate currentDate) {
        if (dispositionCommand.read != null) {
            documentAttachment.isRead = dispositionCommand.read
        }
        if (dispositionCommand.approve != null) {
            this.approveDocumentDisposition(documentAttachment, dispositionCommand, currentUser)
        }
        documentAttachment.updatedBy = currentUser
        documentAttachment.save(failOnError: true)

        BaseDocument baseDocument = documentAttachment.documentReference
        if (dispositionCommand.approve != null && baseDocument.documentType == DocumentType.DOCUMENT_SENT_TO_US) {
            this.documentUpdateCompletionHandler(baseDocument.aPackage, baseDocument.documentType, currentUser, currentDate)
        }
        return this.toUIDocumentDispositionData(documentAttachment)
    }


    private approveDocumentDisposition(DocumentAttachment documentAttachment, DispositionCommand dispositionCommand, User currentUser) {
        documentAttachment.isRead = true
        documentAttachment.isApproved = dispositionCommand.approve
        documentAttachment.dispositionDate = Date.newInstance()
        documentAttachment.dispositionBy = currentUser
        if (documentAttachment.isApproved == false) {
            evMailService.validateVariables(EmailTemplateType.DOCUMENT_REJECTION_NOTIFICATION,
                    documentAttachment.rejectionMailSubject, documentAttachment.rejectionMailMessage)
            documentAttachment.rejectionMailMessage = dispositionCommand.rejectionMailMessage
            documentAttachment.rejectionMailSubject = dispositionCommand.rejectionMailSubject
            sendEmailAsync({
                Package aPackage = Package.get(documentAttachment.documentReference.aPackage.id)
                Profile uploader = documentAttachment.file.uploader
                EmailDto documentRejectionEmailDto  = buildDocumentRejectionEmailDto(documentAttachment, aPackage, uploader)
                EasyVisaSystemMessageType warningMessageType = EasyVisaSystemMessageType.REJECTED_DOCUMENT_DISPOSITION
                alertService.createAlert(warningMessageType, uploader.user,aPackage.attorney.profile.fullName,
                        documentRejectionEmailDto.emailBody, documentRejectionEmailDto.emailSubject)
            }, "Send approve document disposition for Document Attachment [${documentAttachment.id}]")
        }
    }

    EmailDto buildDocumentRejectionEmailDto(DocumentAttachment documentAttachment, Package aPackage, Profile uploader) {
        String repEmail = aPackage.attorney.profile.email
        String message = StringUtils.textToHTML(documentAttachment.rejectionMailMessage)
        Map params = evMailService.buildPackageEmailParams(aPackage)
        String panelName = this.getDocumentPanelName(documentAttachment, aPackage)
        params = emailVariableService.addDocumentPanelName(params, panelName)
        params = emailVariableService.addDocumentFile(params, documentAttachment.file)
        message = evMailService.evaluateTemplate(message, params)
        String subject = evMailService.evaluateTemplate(documentAttachment.rejectionMailSubject, params)

        String toEmail = uploader.email

        EmailDto emailDto = new EmailDto()
        emailDto.with {
            it.toEmail = toEmail
            fromName = aPackage.attorney.profile.fullName
            fromEmail = evMailService.emailFromEmail
            replyTo = repEmail
            emailSubject = subject
            emailBody = message
        }
        emailDto
    }

    @Transactional
    def getDocumentDisposition(DocumentAttachment documentAttachment) {
        EasyVisaFile easyVisaFile = documentAttachment.file
        return [
                file       : fileService.getFile(easyVisaFile),
                fileName   : easyVisaFile.originalName,
                contentType: easyVisaFile.fileType
        ]
    }


    def saveDocumentApproval(DocumentApprovalCommand documentApprovalCommand, User currentUser, LocalDate currentDate) {
        Boolean isApproved
        def panelName
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(documentApprovalCommand.packageId)
        DocumentType type = documentApprovalCommand.documentType
        switch (type) {
            case DocumentType.REQUIRED_DOCUMENT:
                RequiredDocument requiredDocument = documentApprovalCommand.requiredDocument
                RequiredDocument savedRequiredDocument = documentService.updateRequiredDocument(requiredDocument, currentUser)
                isApproved = savedRequiredDocument.isApproved
                if (savedRequiredDocument.isApproved) {
                    panelName = this.documentService.findDocumentById(questionnaireVersion.questVersion, requiredDocument.documentId)?.name
                    sendDocumentApproval(savedRequiredDocument, type, panelName)
                }
                break
            case DocumentType.DOCUMENT_SENT_TO_US:
                SentDocument sentDocument = documentApprovalCommand.sentDocument
                SentDocument savedSentDocument = documentService.updateSentDocument(sentDocument, currentUser)
                isApproved = savedSentDocument.isApproved
                if (savedSentDocument.isApproved) {
                    panelName = this.documentService.findFormById(questionnaireVersion.questVersion, sentDocument.formId)?.name
                    sendDocumentApproval(savedSentDocument, type, panelName)
                }
                break
            case DocumentType.DOCUMENT_RECEIVED_FROM_US:
                ReceivedDocument receivedDocument = documentApprovalCommand.receivedDocument
                ReceivedDocument savedReceivedDocument = documentService.updateReceivedDocument(receivedDocument, currentUser)
                isApproved = savedReceivedDocument.isApproved
                if (savedReceivedDocument.isApproved) {
                    panelName = receivedDocument.receivedDocumentType.description
                    sendDocumentApproval(savedReceivedDocument, type, panelName)
                }
                break
        }
        this.documentUpdateCompletionHandler(documentApprovalCommand.easyVisaPackage, type, currentUser, currentDate)
        return fetchDocumentApproval(isApproved, documentApprovalCommand)
    }

    private MailMessage sendDocumentApproval(BaseDocument baseDocument, DocumentType type, String panelName) {
        EmailDto documentApprovalEmailDto  = buildDocumentApprovalEmailDto(baseDocument, type, panelName)
        User applicantUser = baseDocument.aPackage.getClient()?.user
        Profile attorneyProfile = baseDocument.aPackage.attorney?.profile
        EasyVisaSystemMessageType warningMessageType = EasyVisaSystemMessageType.DOCUMENT_SUB_PANEL_COMPLETION
        alertService.createAlert(warningMessageType, applicantUser, attorneyProfile.fullName, documentApprovalEmailDto.emailBody)
    }


    private fetchDocumentApproval(Boolean isApproved, DocumentApprovalCommand documentApprovalCommand) {
        def documentApprovalData = [
                id          : documentApprovalCommand.attachmentRefId,
                applicantId : documentApprovalCommand.applicantId,
                documentType: documentApprovalCommand.documentType.name(),
                isApproved  : isApproved
        ]
        return documentApprovalData
    }

    EmailDto buildDocumentApprovalEmailDto(BaseDocument baseDocument, DocumentType panelName, String subPanelName) {
        Package aPackage = Package.get(baseDocument.aPackage.id)
        EmailTemplateType emailTemplateType = EmailTemplateType.SUB_PANEL_COMPLETION
        String subject = emailTemplateType.subject
        String content = groovyPageRenderer.render(template: emailTemplateType.path)
        Map params = evMailService.buildPackageEmailParams(aPackage)
        params = emailVariableService.addDocumentPanelName(params, panelName.panelName)
        params = emailVariableService.addDocumentSubPanelName(params, subPanelName)
        String mailContent = evMailService.evaluateTemplate(content, params)

        Applicant client = aPackage.getClient()
        EmailDto emailDto = new EmailDto()
        String repEmail = aPackage.attorney.profile.email
        emailDto.with {
            toEmail = client?.profile?.email
            fromName = aPackage?.attorney?.profile?.fullName
            fromEmail = evMailService.emailFromEmail
            replyTo = repEmail
            emailSubject = subject
            emailBody = mailContent
        }
        emailDto
    }


    @Transactional
    List<PackageDocumentProgressDto> calculateProgress(Package aPackage, LocalDate currentDate) {
        List<PackageDocumentProgressInfo> documentProgressInfoList = this.getPackageDocumentProgressList(aPackage, currentDate);
        return documentProgressInfoList.collect { it.toPackageDocumentProgressDto(aPackage) }
    }


    @Transactional
    private List<PackageDocumentProgressInfo> getPackageDocumentProgressList(Package aPackage, LocalDate currentDate) {
        List<PackageDocumentProgressInfo> documentProgressInfoList = []
        documentProgressInfoList << this.calculateRequiredDocumentProgress(aPackage, currentDate)
        documentProgressInfoList << this.calculateSentDocumentProgress(aPackage)
        documentProgressInfoList << this.calculateReceivedDocumentProgress(aPackage)
        return documentProgressInfoList
    }


    private PackageDocumentProgressInfo calculateRequiredDocumentProgress(Package aPackage, LocalDate currentDate) {
        def packageRequiredDocuments = this.fetchPackageRequiredDocuments(aPackage.id, currentDate)
        int approvedRequiredDocumentCount = 0
        int totalRequiredDocumentCount = 0
        List allRequiredDocumentAttachments = []
        packageRequiredDocuments.each { applicantRequiredDocData ->
            List requiredDocuments = applicantRequiredDocData['requiredDocuments'] as List
            totalRequiredDocumentCount += requiredDocuments.size()
            requiredDocuments.each { requiredDocData ->
                approvedRequiredDocumentCount += (requiredDocData['isApproved']) ? 1 : 0
                allRequiredDocumentAttachments.addAll(requiredDocData['attachments'] as List)
            }
        }
        String documentPanelName = DocumentType.REQUIRED_DOCUMENT.panelName
        PackageDocumentProgressInfo requiredDocumentProgress = new PackageDocumentProgressInfo(documentPanelName, approvedRequiredDocumentCount,
                totalRequiredDocumentCount, allRequiredDocumentAttachments)
        requiredDocumentProgress
    }

    // In Signed doc panel, there is no 'approved' functionality in the panel lvel, so treat doc as approved if doc has action-date and also all individual attachments are approved
    // We have changed the above functionality. Now we have panel level approved so calculation has been changed
    private PackageDocumentProgressInfo calculateSentDocumentProgress(Package aPackage) {
        def packageSignedDocuments = this.fetchPackageSentDocuments(aPackage.id)
        int approvedSentDocumentCount = 0
        int totalSentDocumentCount = 0
        List allSentDocumentAttachments = []
        packageSignedDocuments.each { applicantSentDocData ->
            List sentDocuments = applicantSentDocData['sentDocuments'] as List
            totalSentDocumentCount += sentDocuments.size()
            sentDocuments.each { sentDocData ->
                approvedSentDocumentCount += (sentDocData['isApproved']) ? 1 : 0
                allSentDocumentAttachments.addAll(sentDocData['attachments'] as List)
            }
        }
        String documentPanelName = DocumentType.DOCUMENT_SENT_TO_US.panelName
        PackageDocumentProgressInfo sentDocumentProgress = new PackageDocumentProgressInfo(documentPanelName, approvedSentDocumentCount,
                totalSentDocumentCount, allSentDocumentAttachments)
        sentDocumentProgress
    }


    private PackageDocumentProgressInfo calculateReceivedDocumentProgress(Package aPackage) {
        def applicantReceivedDocData = this.findPackageReceivedDocuments(aPackage.id)
        int approvedReceivedDocumentCount = 0
        List allReceivedDocumentAttachments = []
        List receivedDocuments = applicantReceivedDocData['receivedDocuments'] as List
        int totalReceivedDocumentCount = receivedDocuments.size()
        receivedDocuments.each { receivedDocData ->
            approvedReceivedDocumentCount += (receivedDocData['isApproved']) ? 1 : 0
            allReceivedDocumentAttachments.addAll(receivedDocData['attachments'] as List)
        }

        String documentPanelName = DocumentType.DOCUMENT_RECEIVED_FROM_US.panelName
        PackageDocumentProgressInfo receivedDocumentProgress = new PackageDocumentProgressInfo(documentPanelName, approvedReceivedDocumentCount,
                totalReceivedDocumentCount, allReceivedDocumentAttachments)
        receivedDocumentProgress
    }


    @Transactional
    void sendDocumentPortalCompletionAlert(Package aPackage) {
        def totalCompletedPercentage = aPackage.documentCompletedPercentage
        if (totalCompletedPercentage == 100) {
            sendEmailAsync({
                sendDocumentCompletion(aPackage.id)
            }, "Send Package [${aPackage.id}] Document Portal completion alert")
        }
    }

    private void sendDocumentCompletion(Long packageId) {
        Package aPackage = Package.get(packageId)
        Applicant applicant = aPackage.getClient()
        EasyVisaSystemMessageType attorneyWarningMessageType = EasyVisaSystemMessageType.DOCUMENT_PORTAL_COMPLETED_TO_ATTORNEY
        String attorneyWarningBody = alertService.renderTemplate(attorneyWarningMessageType.templatePath, [aPackage: aPackage])
        alertService.createAlert(attorneyWarningMessageType, aPackage.attorney.user, null, attorneyWarningBody)

        EasyVisaSystemMessageType warningMessageType = EasyVisaSystemMessageType.DOCUMENT_PORTAL_COMPLETED
        String body = alertService.renderTemplate(warningMessageType.templatePath, [aPackage: aPackage])
        alertService.createAlert(warningMessageType, applicant.profile.user, null, body)
    }

    @Transactional
    def documentUpdateCompletionHandler(Package aPackage, DocumentType documentType,
                                        User currentUser, LocalDate currentDate) {
        this.saveDocumentCompletionStatus(aPackage, documentType, currentUser, currentDate)
        this.updatePackageDocumentCompletionPercentage(aPackage, currentDate)
        this.sendDocumentPortalCompletionAlert(aPackage)
    }

    @Transactional
    void renewDocumentCompletionStatus(Package aPackage, User currentUser, LocalDate currentDate) {
        documentUpdateCompletionHandler(aPackage, DocumentType.REQUIRED_DOCUMENT, currentUser, currentDate)
        documentUpdateCompletionHandler(aPackage, DocumentType.DOCUMENT_SENT_TO_US, currentUser, currentDate)
        documentUpdateCompletionHandler(aPackage, DocumentType.DOCUMENT_RECEIVED_FROM_US, currentUser, currentDate)
    }


    @Transactional
    private saveDocumentCompletionStatus(Package aPackage, DocumentType documentType, User currentUser, LocalDate currentDate) {
        Double completedPercentage = this.findDocumentCompletionPercentage(aPackage, documentType, currentDate)
        DocumentCompletionStatus documentCompletionStatus = DocumentCompletionStatus.findByAPackageAndDocumentType(aPackage, documentType)
        if (documentCompletionStatus == null) {
            documentCompletionStatus = new DocumentCompletionStatus(aPackage: aPackage, documentType: documentType, createdBy: currentUser)
        }
        documentCompletionStatus.setCompletedPercentage(completedPercentage)
        documentCompletionStatus.setUpdatedBy(currentUser)
        documentCompletionStatus.save(failOnError: true)
    }


    private Double findDocumentCompletionPercentage(Package aPackage, DocumentType documentType, LocalDate currentDate) {
        PackageDocumentProgressInfo documentProgressInfo
        switch (documentType) {
            case DocumentType.REQUIRED_DOCUMENT:
                documentProgressInfo = this.calculateRequiredDocumentProgress(aPackage, currentDate)
                break
            case DocumentType.DOCUMENT_SENT_TO_US:
                documentProgressInfo = this.calculateSentDocumentProgress(aPackage)
                break
            case DocumentType.DOCUMENT_RECEIVED_FROM_US:
                documentProgressInfo = this.calculateReceivedDocumentProgress(aPackage)
                break
        }
        double completedPercentage = documentProgressInfo?.getCompletedPercentage() ?: 0
        return completedPercentage
    }


    @Transactional
    private updatePackageDocumentCompletionPercentage(Package aPackage, LocalDate currentDate) {
        List<PackageDocumentProgressInfo> documentProgressInfoList = this.getPackageDocumentProgressList(aPackage, currentDate);
        int totalApprovedDocumentCount = documentProgressInfoList.sum { it['approvedDocumentCount'] } as Integer
        int totalDocumentCount = documentProgressInfoList.sum { it['totalDocumentCount'] } as Integer
        BigDecimal completedPercentage = (totalApprovedDocumentCount / totalDocumentCount) * 100;
        aPackage.setDocumentCompletedPercentage(completedPercentage.toDouble().round(2))
        this.packageService.savePackage(aPackage)
    }


    MilestoneType getMilestoneTypeById(String questVersion, String milestoneTypeId) {
        return this.documentService.getMilestoneTypeById(questVersion, milestoneTypeId)
    }


    @Transactional
    Date getMilestoneDate(Package aPackage, DocumentMilestoneType documentMilestoneType) {
        DocumentMilestone documentMilestone = DocumentMilestone.findByAPackageAndMilestoneTypeId(aPackage, documentMilestoneType.easyVisaId)
        documentMilestone?.milestoneDate
    }

    @Transactional
    void copyPackageDocumentPortalData(Package fromPackage, Package toPackage) {
        DocumentMilestone.executeUpdate("""insert into DocumentMilestone (
                                                    version, aPackage, milestoneDate,
                                                    milestoneTypeId, dateCreated, lastUpdated, createdBy, updatedBy)
                                                 select 0L, :toPackage, milestoneDate,
                                                    milestoneTypeId, dateCreated, lastUpdated, createdBy, updatedBy
                                                 from DocumentMilestone
                                                 where aPackage = :fromPackage""",
                [fromPackage: fromPackage, toPackage: toPackage])
        DocumentCompletionStatus.executeUpdate("""insert into DocumentCompletionStatus (
                                                    version, aPackage, documentType,
                                                    completedPercentage, dateCreated, lastUpdated, createdBy, updatedBy)
                                                 select 0L, :toPackage, documentType,
                                                    completedPercentage, dateCreated, lastUpdated, createdBy, updatedBy
                                                 from DocumentCompletionStatus
                                                 where aPackage = :fromPackage""",
                [fromPackage: fromPackage, toPackage: toPackage])
        fromPackage.applicants.each {fromApplicant ->
            Applicant toApplicant = toPackage.applicants.find { it.profile.easyVisaId == fromApplicant.profile.easyVisaId }
            long offset = 0
            Map<Long, BaseDocument> oldNewCache = [:]
            List<DocumentAttachment> documentAttachments = findDocumentAttachmentsToTransfer(offset, fromPackage, fromApplicant)
            while (documentAttachments) {
                documentAttachments.each {
                    DocumentAttachment documentAttachment = it
                    BaseDocument referenceToSet = oldNewCache.computeIfAbsent(it.documentReference.id, {
                        documentAttachment.documentReference.copy(toPackage, toApplicant).save(failOnError: true)
                    })
                    DocumentAttachment copy = it.copy()
                    copy.documentReference = referenceToSet
                    copy.file = fileService.copyFile(it.file, toPackage, toApplicant)
                    copy.save(failOnError: true)
                }
                offset += BATCH_SIZE
                documentAttachments = findDocumentAttachmentsToTransfer(offset, fromPackage, fromApplicant)
            }
        }
    }

    @Transactional
    void deleteDocumentPortalData(Package aPackage) {
        DocumentMilestone.executeUpdate("delete from DocumentMilestone where aPackage = :aPackage", [aPackage: aPackage])
        DocumentCompletionStatus.executeUpdate("delete from DocumentCompletionStatus where aPackage = :aPackage", [aPackage: aPackage])
        List<BaseDocument> baseDocs = []
        aPackage.applicants.each {applicant ->
            long offset = 0
            List<DocumentAttachment> documentAttachments = findDocumentAttachmentsToTransfer(offset, aPackage, applicant)
            while (documentAttachments) {
                documentAttachments.each {
                    baseDocs.add(it.documentReference)
                    it.delete(failOnError: true)
                    fileService.deleteEasyVisaFile(it.file)
                }
                offset += BATCH_SIZE
                documentAttachments = findDocumentAttachmentsToTransfer(offset, aPackage, applicant)
            }
        }
        baseDocs.each { it.delete(failOnError: true) }
    }


    /**
     *
     * LEAD - changes and access are not allowed to any one (i.e) nobody can access it
     * OPEN - allowed to non trainee organization users and a package applicants (i.e) relevant organization employees and applicants
     * BLOCKED - changes and access allowed to non trainee organization users (i.e) relevant organization employees
     * CLOSED - read-only access for non trainee organization users and a package applicants
     * TRANSFERRED - read-only access for all
     *
     * Remember that organization employees with the Trainee position can only view the document, not edit it.
     *
     * The above points are true for all panels in document-portal page execpt REQUIRED_DOCUMENT
     * REQUIRED_DOCUMENT - For closed package, we have to allow user to download/upload documents (i.e) make readOnly as false
     */
    @Transactional
    Map fetchDocumentAccessState(Package aPackage, User user) {
        Map accessState = [access: true, readOnly: false]
        Map accessStateForRequiredDoc = (aPackage.status!=PackageStatus.CLOSED) ? accessState : [access: true, readOnly: true]
        Map documentAccessState = new HashMap()
        documentAccessState.put(DocumentType.REQUIRED_DOCUMENT.displayName, accessStateForRequiredDoc)
        documentAccessState.put(DocumentType.DOCUMENT_SENT_TO_US.displayName, accessState)
        documentAccessState.put(DocumentType.DOCUMENT_RECEIVED_FROM_US.displayName, accessState)
        documentAccessState.put(DOCUMENT_NOTE_TYPE, accessState)
        documentAccessState.put(DOCUMENT_MILESTONE_TYPE, accessState)
        documentAccessState.put(PRINT_DOWNLOAD_USCIS_FORMS, accessState)
        return documentAccessState
    }

    @CompileDynamic
    private List<DocumentAttachment> findDocumentAttachmentsToTransfer(long offset, Package aPackage, Applicant applicant) {
        DocumentAttachment.createCriteria().list(max: BATCH_SIZE, offset: offset) {
            'documentReference' {
                eq('aPackage', aPackage)
                eq('applicant', applicant)
            }
        } as List<DocumentAttachment>
    }

    private void sendEmailAsync(Runnable command, String name) {
        asyncService.runAsync(command, name)
    }

    private void sendEmailAsyncDelayed(Runnable command, String name) {
        asyncService.runAsyncDelayed(command, name)
    }

    private String getDisplayFormId(String displayText) {
        return displayText.split(' ')[0]
    }

    private String getDisplayFormName(String displayText) {
        String formId = displayText.split(' ')[0]
        String displayFormName = displayText.replace(formId + ' ', '')
        return displayFormName
    }

}
