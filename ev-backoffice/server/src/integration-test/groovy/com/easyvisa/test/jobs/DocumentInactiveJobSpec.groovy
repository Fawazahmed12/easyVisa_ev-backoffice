package com.easyvisa.test.jobs

import com.easyvisa.AbstractAttorneyNotifications
import com.easyvisa.DocumentsInactiveNotificationsJob
import com.easyvisa.Package
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration

@Integration
class DocumentInactiveJobSpec extends TestAbstractReminder {

    @Override
    protected AbstractAttorneyNotifications setupJob() {
        DocumentsInactiveNotificationsJob documentInactiveJob = new DocumentsInactiveNotificationsJob()
        documentInactiveJob.evMailService = evMailService
        documentInactiveJob.alertService = alertService
        documentInactiveJob.emailVariableService = emailVariableService
        documentInactiveJob.packageDocumentService = packageDocumentService
        documentInactiveJob.packageReminderService = packageReminderService
        documentInactiveJob
    }

    @Override
    protected void prepareSuccessJob(PackageTestBuilder testHelper, PackageTestBuilder testHelperOpen,
                                     PackageTestBuilder testHelperOpenTwoDays, Integer lastActivity = null) {
        Package.withNewTransaction {
            testHelperOpenTwoDays.aPackage.refresh().opened = getLastActivityDate(lastActivity)
            testHelperOpenTwoDays.aPackage.save(failOnError:true)
            createAttorneyNotifications(testHelper, testHelperOpen, testHelperOpenTwoDays)
        }
    }

    @Override
    protected EmailTemplateType getEmailTemplateType() {
        return EmailTemplateType.DOCUMENT_PORTAL_INACTIVITY
    }

    @Override
    protected EasyVisaSystemMessageType getAlertType() {
        return EasyVisaSystemMessageType.PACKAGE_DOCUMENT_PORTAL_INACTIVE
    }

    @Override
    protected Boolean isTimedOut() {
        return Boolean.TRUE
    }

    @Override
    protected ImmigrationBenefitCategory getPrincipalBeneficiaryCategory() {
        ImmigrationBenefitCategory.F1_A
    }

}
