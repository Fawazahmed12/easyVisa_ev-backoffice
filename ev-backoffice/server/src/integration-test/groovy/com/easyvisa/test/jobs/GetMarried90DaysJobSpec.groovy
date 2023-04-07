package com.easyvisa.test.jobs

import com.easyvisa.AbstractAttorneyNotifications
import com.easyvisa.GetMarried90DaysNotificationsJob
import com.easyvisa.Package
import com.easyvisa.User
import com.easyvisa.document.DocumentMilestone
import com.easyvisa.enums.DocumentMilestoneType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration

@Integration
class GetMarried90DaysJobSpec extends TestAbstractReminder {

    @Override
    protected AbstractAttorneyNotifications setupJob() {
        GetMarried90DaysNotificationsJob paymentJob = new GetMarried90DaysNotificationsJob()
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
                    milestoneTypeId:DocumentMilestoneType.ARRIVAL_DATE_IN_US.easyVisaId, updatedBy: user, createdBy:user)
                    .save(failOnError:true)
            createAttorneyNotifications(testHelper, testHelperOpen, testHelperOpenTwoDays)
        }
    }

    @Override
    protected EmailTemplateType getEmailTemplateType() {
        return EmailTemplateType.MARRIAGE_IN_90_DAYS_OF_ARRIVAL
    }

    @Override
    protected EasyVisaSystemMessageType getAlertType() {
        return EasyVisaSystemMessageType.MARRIAGE_IN_90_DAYS_OF_ARRIVAL
    }

    @Override
    protected Boolean isTimedOut() {
        return Boolean.TRUE
    }

    @Override
    protected ImmigrationBenefitCategory getPrincipalBeneficiaryCategory() {
        ImmigrationBenefitCategory.K1K3
    }

}
