package com.easyvisa.test.jobs

import com.easyvisa.AbstractAttorneyNotifications
import com.easyvisa.Package
import com.easyvisa.PermanentResidenceCardNotificationsJob
import com.easyvisa.User
import com.easyvisa.document.DocumentMilestone
import com.easyvisa.enums.DocumentMilestoneType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration

@Integration
class PermanentResidenceCardJobSpec extends TestAbstractReminder {

    @Override
    protected AbstractAttorneyNotifications setupJob() {
        PermanentResidenceCardNotificationsJob paymentJob = new PermanentResidenceCardNotificationsJob()
        paymentJob.evMailService = evMailService
        paymentJob.alertService = alertService
        paymentJob.emailVariableService = emailVariableService
        paymentJob.packageReminderService = packageReminderService
        paymentJob.packageDocumentService = packageDocumentService
        paymentJob
    }

    @Override
    protected void prepareSuccessJob(PackageTestBuilder testHelper, PackageTestBuilder testHelperOpen,
                                     PackageTestBuilder testHelperOpenTwoDays, Integer lastActivity = null) {
        Package.withNewTransaction {
            User user = testHelperOpenTwoDays.packageLegalRepresentative.user.refresh()
            new DocumentMilestone(aPackage:testHelperOpenTwoDays.aPackage, milestoneDate:getLastActivityDate(lastActivity),
                    milestoneTypeId:DocumentMilestoneType.MARRIAGE_DATE.easyVisaId, updatedBy: user, createdBy:user)
                    .save(failOnError:true)
            createAttorneyNotifications(testHelper, testHelperOpen, testHelperOpenTwoDays)
        }
    }

    @Override
    protected EmailTemplateType getEmailTemplateType() {
        return EmailTemplateType.PERMANENT_RESIDENCE_CARD
    }

    @Override
    protected EasyVisaSystemMessageType getAlertType() {
        return EasyVisaSystemMessageType.PERMANENT_RESIDENCE_CARD
    }

    @Override
    protected Boolean isTimedOut() {
        return Boolean.FALSE
    }

    @Override
    protected ImmigrationBenefitCategory getPrincipalBeneficiaryCategory() {
        ImmigrationBenefitCategory.K1K3
    }

}
