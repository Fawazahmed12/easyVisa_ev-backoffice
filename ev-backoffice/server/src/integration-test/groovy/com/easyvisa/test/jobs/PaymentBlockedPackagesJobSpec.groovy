package com.easyvisa.test.jobs

import com.easyvisa.AbstractAttorneyNotifications
import com.easyvisa.Package
import com.easyvisa.PaymentBlockedPackagesNotificationsJob
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PackageBlockedType
import com.easyvisa.enums.PackageStatus
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration

@Integration
class PaymentBlockedPackagesJobSpec extends TestAbstractReminder {

    @Override
    protected AbstractAttorneyNotifications setupJob() {
        PaymentBlockedPackagesNotificationsJob paymentJob = new PaymentBlockedPackagesNotificationsJob()
        paymentJob.evMailService = evMailService
        paymentJob.alertService = alertService
        paymentJob.emailVariableService = emailVariableService
        paymentJob.packageReminderService = packageReminderService
        paymentJob
    }

    @Override
    protected void prepareSuccessJob(PackageTestBuilder testHelper, PackageTestBuilder testHelperOpen,
                                     PackageTestBuilder testHelperOpenTwoDays, Integer lastActivity = null) {
        Package.withNewTransaction {
            testHelperOpenTwoDays.aPackage.refresh().status = PackageStatus.BLOCKED
            testHelperOpenTwoDays.aPackage.blockedType = PackageBlockedType.PAYMENT
            testHelperOpenTwoDays.aPackage.save(failOnError:true, flush:true)
            Package.executeUpdate('update Package set lastUpdated = :lastSent', [lastSent:getLastActivityDate(lastActivity)])
            createAttorneyNotifications(testHelper, testHelperOpen, testHelperOpenTwoDays)
        }
    }

    @Override
    protected EmailTemplateType getEmailTemplateType() {
        return EmailTemplateType.PAYMENT
    }

    @Override
    protected EasyVisaSystemMessageType getAlertType() {
        return EasyVisaSystemMessageType.PACKAGE_PAYMENT
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
