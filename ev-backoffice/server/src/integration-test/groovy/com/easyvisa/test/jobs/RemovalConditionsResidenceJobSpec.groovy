package com.easyvisa.test.jobs

import com.easyvisa.AbstractAttorneyNotifications
import com.easyvisa.Package
import com.easyvisa.RemovalConditionsResidenceNotificationsJob
import com.easyvisa.User
import com.easyvisa.document.DocumentMilestone
import com.easyvisa.enums.DocumentMilestoneType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration

@Integration
class RemovalConditionsResidenceJobSpec extends TestAbstractReminder {

    @Override
    protected AbstractAttorneyNotifications setupJob() {
        RemovalConditionsResidenceNotificationsJob paymentJob = new RemovalConditionsResidenceNotificationsJob()
        paymentJob.evMailService = evMailService
        paymentJob.alertService = alertService
        paymentJob.emailVariableService = emailVariableService
        paymentJob.packageDocumentService = packageDocumentService
        paymentJob.packageReminderService = packageReminderService
        paymentJob
    }

    @Override
    protected void prepareSuccessJob(PackageTestBuilder testHelper, PackageTestBuilder testHelperOpen,
                                     PackageTestBuilder testHelperOpenTwoDays, Integer lastActivity = null) {
        Package.withNewTransaction {
            User user = testHelperOpenTwoDays.packageLegalRepresentative.user
            user.refresh()
            Integer lastActive = lastActivity != null ? lastActivity * -1 : -2
            if (lastActivity == 1) {
                lastActive = -99
            }
            new DocumentMilestone(aPackage:testHelperOpenTwoDays.aPackage, milestoneDate:getLastActivityDate(lastActive),
                    milestoneTypeId:DocumentMilestoneType.GREENCARD_PERMANENT_EXPIRATION.easyVisaId, updatedBy: user, createdBy:user)
                    .save(failOnError:true)
            createAttorneyNotifications(testHelper, testHelperOpen, testHelperOpenTwoDays)
        }
    }

    @Override
    protected EmailTemplateType getEmailTemplateType() {
        return EmailTemplateType.REMOVAL_CONDITIONS_RESIDENCE
    }

    @Override
    protected EasyVisaSystemMessageType getAlertType() {
        return EasyVisaSystemMessageType.REMOVAL_CONDITIONS_RESIDENCE
    }

    @Override
    protected Boolean isTimedOut() {
        return Boolean.TRUE
    }

    @Override
    protected ImmigrationBenefitCategory getPrincipalBeneficiaryCategory() {
        ImmigrationBenefitCategory.REMOVECOND
    }

}
