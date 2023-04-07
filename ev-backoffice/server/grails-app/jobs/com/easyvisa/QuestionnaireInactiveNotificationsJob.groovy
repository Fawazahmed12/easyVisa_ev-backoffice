package com.easyvisa

import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.NotificationType
import com.easyvisa.questionnaire.Answer
import groovy.transform.CompileStatic

@CompileStatic
class QuestionnaireInactiveNotificationsJob extends AbstractAttorneyNotifications {

    static concurrent = false

    void execute() {
        execute('Questionnaire Inactive', NotificationType.QUESTIONNAIRE_INACTIVITY,
                EasyVisaSystemMessageType.PACKAGE_QUESTIONNAIRE_INACTIVE)
    }

    @Override
    protected List<Package> findPackages(EmailTemplate emailTemplate) {
        findAttorneyPackages(emailTemplate.attorney)
    }

    @Override
    protected boolean isReadyToSend(Package aPackage) {
        return aPackage.questionnaireCompletedPercentage != 100
    }

    @Override
    protected boolean isCheckMaxPeriod() {
        return true
    }

    @Override
    protected Integer getLastActivityDays(Package aPackage) {
        List<Answer> lastAnswer = Answer.createCriteria().list(max:1) {
            eq('packageId', aPackage.id)
            order('lastUpdated', 'desc')
        } as List<Answer>
        Date lastDate = lastAnswer.size() > 0 ? lastAnswer.first().lastUpdated : aPackage.opened
        getDaysPeriod(lastDate)
    }

    @Override
    protected Map setExtraEmailParams(Map params, Integer passedDays, Package aPackage) {
        emailVariableService.addQuestionnaireInactiveInterval(params, passedDays)
    }

}
