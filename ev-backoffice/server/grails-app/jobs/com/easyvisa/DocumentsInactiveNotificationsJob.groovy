package com.easyvisa

import com.easyvisa.document.BaseDocument
import com.easyvisa.enums.DocumentType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.NotificationType
import com.easyvisa.questionnaire.util.DateUtil
import groovy.transform.CompileDynamic

@CompileDynamic
class DocumentsInactiveNotificationsJob extends AbstractAttorneyNotifications {

    static concurrent = false

    PackageDocumentService packageDocumentService

    void execute() {
        execute('Documents Inactive', NotificationType.DOCUMENT_PORTAL_INACTIVITY,
                EasyVisaSystemMessageType.PACKAGE_DOCUMENT_PORTAL_INACTIVE)
    }

    @Override
    protected List<Package> findPackages(EmailTemplate emailTemplate) {
        findAttorneyPackages(emailTemplate.attorney)
    }

    @Override
    protected boolean isReadyToSend(Package aPackage) {
        return aPackage.documentCompletedPercentage != 100
    }

    @Override
    protected boolean isCheckMaxPeriod() {
        return true
    }

    @Override
    protected Integer getLastActivityDays(Package aPackage) {
        List<BaseDocument> lastDocument = BaseDocument.createCriteria().list(max:1) {
            eq('aPackage', aPackage)
            order('lastUpdated', 'desc')
        } as List<BaseDocument>
        Date lastDate = lastDocument.size() > 0 ? lastDocument.first().lastUpdated : aPackage.opened
        getDaysPeriod(lastDate)
    }

    @Override
    protected Map setExtraEmailParams(Map params, Integer passedDays, Package aPackage) {
        Map missingDocs = findMissingDocuments(aPackage)
        Map missingDaysParams = emailVariableService.addDocumentsInactiveInterval(params, passedDays)
        emailVariableService.addMissingDocuments(missingDaysParams, missingDocs)
    }

    private Map findMissingDocuments(Package aPackage) {
        Map result = [:]
        collectRequiredDocs(aPackage, result)
        collectSentDocs(aPackage, result)
        collectReceivedDocs(aPackage, result)
        result
    }

    private void collectRequiredDocs(Package aPackage, LinkedHashMap result) {
        Map requiredDocs = packageDocumentService.fetchPackageRequiredDocuments(aPackage.id, DateUtil.today()).collectEntries {
            List items = it['requiredDocuments'].findAll { !it['isApproved'] }.collect { it['name'] }
            if (items.size() > 0) {
                [it['applicantName'], items]
            } else {
                [:]
            }
        }
        if (!requiredDocs.isEmpty()) {
            result.put(DocumentType.REQUIRED_DOCUMENT.panelName, requiredDocs)
        }
    }

    private void collectSentDocs(Package aPackage, LinkedHashMap result) {
        def documents = packageDocumentService.fetchPackageSentDocuments(aPackage.id)
        Map sentDocs = documents.collectEntries {
            List items = it['sentDocuments'].findAll {
                !(it['actionDate'] != '' && ((List) it['attachments']).size() && it['attachments'].every { it['approved'] })
            }.collect { it['name'] }
            if (items.size() > 0) {
                [it['applicantName'], items]
            } else {
                [:]
            }
        }
        if (!sentDocs.isEmpty()) {
            result.put(DocumentType.DOCUMENT_SENT_TO_US.panelName, sentDocs)
        }
    }

    private void collectReceivedDocs(Package aPackage, Map result) {
        Map receivedDocs = packageDocumentService.findPackageReceivedDocuments(aPackage.id)
        List received = receivedDocs['receivedDocuments'].findAll { !it['isApproved'] }.collect { it['description'] }
        if (received.size() > 0) {
            result.put(DocumentType.DOCUMENT_RECEIVED_FROM_US.panelName, [(receivedDocs['applicantName']): received])
        }
    }

}
