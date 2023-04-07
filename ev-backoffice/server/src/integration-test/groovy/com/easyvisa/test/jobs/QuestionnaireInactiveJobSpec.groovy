package com.easyvisa.test.jobs

import com.easyvisa.AbstractAttorneyNotifications
import com.easyvisa.Package
import com.easyvisa.QuestionnaireInactiveNotificationsJob
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration

@Integration
class QuestionnaireInactiveJobSpec extends TestAbstractReminder {

    @Override
    protected AbstractAttorneyNotifications setupJob() {
        QuestionnaireInactiveNotificationsJob questionnaireInactiveJob = new QuestionnaireInactiveNotificationsJob()
        questionnaireInactiveJob.evMailService = evMailService
        questionnaireInactiveJob.alertService = alertService
        questionnaireInactiveJob.emailVariableService = emailVariableService
        questionnaireInactiveJob.packageReminderService = packageReminderService
        questionnaireInactiveJob
    }

    @Override
    protected void prepareSuccessJob(PackageTestBuilder testHelper, PackageTestBuilder testHelperOpen,
                                     PackageTestBuilder testHelperOpenTwoDays, Integer lastActivity = null) {
        Package.withNewTransaction {
            Answer.executeUpdate('update Answer a set a.lastUpdated = :date where a.packageId = :packId',
                    [date:getLastActivityDate(lastActivity), packId:testHelperOpenTwoDays.aPackage.id])
            createAttorneyNotifications(testHelper, testHelperOpen, testHelperOpenTwoDays)
        }
    }

    @Override
    protected EmailTemplateType getEmailTemplateType() {
        return EmailTemplateType.QUESTIONNAIRE_INACTIVITY
    }

    @Override
    protected EasyVisaSystemMessageType getAlertType() {
        return EasyVisaSystemMessageType.PACKAGE_QUESTIONNAIRE_INACTIVE
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
