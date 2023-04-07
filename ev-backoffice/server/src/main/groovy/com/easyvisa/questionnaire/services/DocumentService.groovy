package com.easyvisa.questionnaire.services

import com.easyvisa.User
import com.easyvisa.document.DocumentMilestone
import com.easyvisa.document.ReceivedDocument
import com.easyvisa.document.RequiredDocument
import com.easyvisa.document.SentDocument
import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.answering.DocumentHelpNodeInstance
import com.easyvisa.questionnaire.answering.DocumentNodeInstance
import com.easyvisa.questionnaire.model.*
import com.easyvisa.questionnaire.repositories.ContinuationSheetDAO
import com.easyvisa.questionnaire.repositories.DocumentDAO
import com.easyvisa.questionnaire.repositories.FormDAO
import com.easyvisa.questionnaire.repositories.MilestoneTypeDAO
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.plugin.cache.Cacheable
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDate
import java.util.stream.Collectors

@Service
@GrailsCompileStatic
class DocumentService {

    @Autowired
    DocumentDAO documentDAO

    @Autowired
    FormDAO formDAO

    @Autowired
    MilestoneTypeDAO milestoneTypeDAO

    @Autowired
    ContinuationSheetDAO continuationSheetDAO

    @Autowired
    QuestionnaireService questionnaireService

    @Transactional
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    List<DocumentNodeInstance> documentsByBenefitCategory(Long packageId, Long applicantId, String benefitCategoryId,
                                                          ApplicantType applicantType, LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        List<Document> documentList = this.documentsByBenefitCategory(questionnaireVersion.questVersion, benefitCategoryId, applicantType)
        List<DocumentNodeInstance> documentNodeInstanceList = documentList.stream()
                .map({ document -> this.buildDocumentNodeInstance(document, currentDate) })
                .filter({ documentNodeInstance -> documentNodeInstance.isVisibility() })
                .sorted(Comparator.comparing({ DocumentNodeInstance documentNodeInstance -> documentNodeInstance.order }))
                .collect(Collectors.toList())
        return documentNodeInstanceList
    }

    @Cacheable("documentsByBenefitCategory")
    List<Document> documentsByBenefitCategory(String questVersion, String benefitCategoryId, ApplicantType applicantType) {
        return documentDAO.documentsByBenefitCategory(questVersion, benefitCategoryId, applicantType)
    }


    @Transactional
    List<DocumentNodeInstance> findAllDocuments(Long packageId, LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        List<Document> documentList = documentDAO.findAllDocuments(questionnaireVersion.questVersion)
        List<DocumentNodeInstance> documentNodeInstanceList = documentList.stream()
                .map({ document -> this.buildDocumentNodeInstance(document, currentDate) })
                .collect(Collectors.toList())
        return documentNodeInstanceList
    }


    private DocumentNodeInstance buildDocumentNodeInstance(Document document, LocalDate currentDate) {
        DocumentNodeInstance documentNodeInstance = new DocumentNodeInstance(document, DisplayTextLanguage.defaultLanguage, currentDate)
        documentNodeInstance.setVisibility(true)
        Set<EasyVisaNode> children = document.getChildren()
        for (EasyVisaNode easyVisaNode : children) {
            DocumentHelp documentHelp = (DocumentHelp) easyVisaNode
            DocumentHelpNodeInstance documentHelpNodeInstance = new DocumentHelpNodeInstance(documentHelp, DisplayTextLanguage.defaultLanguage, currentDate)
            documentNodeInstance.addChild(documentHelpNodeInstance)
        }
        documentNodeInstance.sortChildren()
        return documentNodeInstance
    }


    RequiredDocument saveRequiredDocument(RequiredDocument requiredDocument, User currentUser) {
        RequiredDocument savedRequiredDocument = RequiredDocument.findByAPackageAndApplicantAndDocumentId(requiredDocument.aPackage, requiredDocument.applicant, requiredDocument.documentId)
        if (savedRequiredDocument) {
            savedRequiredDocument.isApproved = false
            savedRequiredDocument.updatedBy = currentUser
            return savedRequiredDocument.save(failOnError: true)
        }
        requiredDocument.isApproved = false
        requiredDocument.createdBy = currentUser
        requiredDocument.updatedBy = currentUser
        requiredDocument.save(failOnError: true)
    }


    @Cacheable("findFormsByBenefitCategory")
    List<Form> findFormsByBenefitCategory(String questVersion, String benefitCategoryId) {
        return formDAO.findFormsByBenefitCategory(questVersion, benefitCategoryId)
    }


    @Cacheable("fetchFormToContinuationSheetListMapper")
    Map<String, Set<ContinuationSheet>> fetchFormToContinuationSheetListMapper(String questVersion) {
        List<Form> formList = formDAO.findAllForms(questVersion)
        Map<String, Set<ContinuationSheet>> formToContinuationSheetListMapper = continuationSheetDAO.fetchFormToContinuationSheetListMapper(questVersion, formList)
        return formToContinuationSheetListMapper
    }

    SentDocument saveSentDocument(SentDocument sentDocument, User currentUser) {
        SentDocument savedSentDocument = SentDocument.findByAPackageAndApplicantAndFormId(sentDocument.aPackage, sentDocument.applicant, sentDocument.formId)
        if (savedSentDocument) {
            savedSentDocument.sentDate = sentDocument.sentDate ?: savedSentDocument.sentDate
            savedSentDocument.isApproved = savedSentDocument.sentDate ? savedSentDocument.isApproved : false
            savedSentDocument.updatedBy = currentUser
            return savedSentDocument
        }
        sentDocument.createdBy = currentUser
        sentDocument.updatedBy = currentUser
        sentDocument.save(failOnError: true)
    }

    DocumentMilestone saveDocumentMilestone(DocumentMilestone documentMilestone, User currentUser) {
        DocumentMilestone savedDocumentMilestone = DocumentMilestone.findByAPackageAndMilestoneTypeId(documentMilestone.aPackage, documentMilestone.milestoneTypeId)
        if (savedDocumentMilestone) {
            savedDocumentMilestone.milestoneDate = documentMilestone.milestoneDate
            savedDocumentMilestone.updatedBy = currentUser
            return savedDocumentMilestone.save(failOnError: true)
        }
        documentMilestone.createdBy = currentUser
        documentMilestone.updatedBy = currentUser
        documentMilestone.save(failOnError: true)
    }

    ReceivedDocument saveReceivedDocument(ReceivedDocument receivedDocument, User currentUser) {
        ReceivedDocument savedReceivedDocument = ReceivedDocument.findByAPackageAndApplicantAndReceivedDocumentType(receivedDocument.aPackage, receivedDocument.applicant, receivedDocument.receivedDocumentType)
        if (savedReceivedDocument) {
            savedReceivedDocument.receivedDate = receivedDocument.receivedDate ?: savedReceivedDocument.receivedDate
            savedReceivedDocument.isApproved = receivedDocument.receivedDate ? savedReceivedDocument.isApproved : false
            savedReceivedDocument.updatedBy = currentUser
            return savedReceivedDocument
        }
        receivedDocument.createdBy = currentUser
        receivedDocument.updatedBy = currentUser
        receivedDocument.save(failOnError: true)
    }


    Form findFormById(String questVersion, String formId) {
        return this.formDAO.getFormById(questVersion, formId)
    }

    Document findDocumentById(String questVersion, String formId) {
        return this.documentDAO.getDocumentById(questVersion, formId)
    }

    @Transactional
    RequiredDocument updateRequiredDocument(RequiredDocument requiredDocument, User currentUser) {
        RequiredDocument savedRequiredDocument = RequiredDocument.findByAPackageAndApplicantAndDocumentId(requiredDocument.aPackage, requiredDocument.applicant, requiredDocument.documentId)
        if (savedRequiredDocument) {
            savedRequiredDocument.isApproved = requiredDocument.isApproved
            savedRequiredDocument.updatedBy = currentUser
            return savedRequiredDocument.save(failOnError: true)
        }
        requiredDocument.isApproved = true
        requiredDocument.createdBy = currentUser
        requiredDocument.updatedBy = currentUser
        requiredDocument.save(failOnError: true)
    }

    @Transactional
    ReceivedDocument updateReceivedDocument(ReceivedDocument receivedDocument, User currentUser) {
        ReceivedDocument savedReceivedDocument = ReceivedDocument.findByAPackageAndApplicantAndReceivedDocumentType(receivedDocument.aPackage, receivedDocument.applicant, receivedDocument.receivedDocumentType)
        savedReceivedDocument.isApproved = receivedDocument.isApproved
        savedReceivedDocument.updatedBy = currentUser
        savedReceivedDocument.save(failOnError: true)
    }

    @Transactional
    SentDocument updateSentDocument(SentDocument sentDocument, User currentUser) {
        SentDocument savedSentDocument = SentDocument.findByAPackageAndApplicantAndFormId(sentDocument.aPackage, sentDocument.applicant, sentDocument.formId)
        savedSentDocument.isApproved = sentDocument.isApproved
        savedSentDocument.updatedBy = currentUser
        savedSentDocument.save(failOnError: true)
    }

    List<MilestoneType> findMilestoneTypesByBenefitCategory(String questVersion, String benefitCategoryId) {
        return milestoneTypeDAO.findMilestoneTypesByBenefitCategory(questVersion, benefitCategoryId)
    }

    MilestoneType getMilestoneTypeById(String questVersion, String milestoneTypeId) {
        return milestoneTypeDAO.getMilestoneTypeById(questVersion, milestoneTypeId)
    }
}
