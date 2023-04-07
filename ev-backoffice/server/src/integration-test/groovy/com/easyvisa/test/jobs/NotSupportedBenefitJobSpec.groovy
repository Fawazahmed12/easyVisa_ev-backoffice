package com.easyvisa.test.jobs

import com.easyvisa.AbstractAttorneyNotifications
import com.easyvisa.NotSupportedBenefitCategoryNotificationsJob
import com.easyvisa.Package
import com.easyvisa.PaymentBlockedPackagesNotificationsJob
import com.easyvisa.enums.*
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration

@Integration
class NotSupportedBenefitJobSpec extends TestAbstractReminder {

    @Override
    protected AbstractAttorneyNotifications setupJob() {
        NotSupportedBenefitCategoryNotificationsJob notSupportedBenefitJob = new NotSupportedBenefitCategoryNotificationsJob()
        notSupportedBenefitJob.evMailService = evMailService
        notSupportedBenefitJob.alertService = alertService
        notSupportedBenefitJob.emailVariableService = emailVariableService
        notSupportedBenefitJob.packageReminderService = packageReminderService
        notSupportedBenefitJob
    }

    @Override
    protected void prepareSuccessJob(PackageTestBuilder testHelper, PackageTestBuilder testHelperOpen,
                                     PackageTestBuilder testHelperOpenTwoDays, Integer lastActivity = null) {
        Package.withNewTransaction {
            testHelperOpenTwoDays.aPackage.refresh().status = PackageStatus.BLOCKED
            testHelperOpenTwoDays.aPackage.blockedType = PackageBlockedType.NOT_SUPPORTED_IMMIGRATION
            testHelperOpenTwoDays.aPackage.save(failOnError:true, flush:true)
            Package.executeUpdate('update Package set lastUpdated = :lastSent', [lastSent:getLastActivityDate(lastActivity)])
            createAttorneyNotifications(testHelper, testHelperOpen, testHelperOpenTwoDays)
        }
    }

    @Override
    protected EmailTemplateType getEmailTemplateType() {
        return EmailTemplateType.NOT_SUPPORTED_IMMIGRATION_PROCESS
    }

    @Override
    protected EasyVisaSystemMessageType getAlertType() {
        return EasyVisaSystemMessageType.PACKAGE_NOT_SUPPORTED_IMMIGRATION_PROCESS
    }

    @Override
    protected Boolean isTimedOut() {
        return Boolean.FALSE
    }

    @Override
    protected ImmigrationBenefitCategory getPrincipalBeneficiaryCategory() {
        ImmigrationBenefitCategory.F1_A
    }

}
