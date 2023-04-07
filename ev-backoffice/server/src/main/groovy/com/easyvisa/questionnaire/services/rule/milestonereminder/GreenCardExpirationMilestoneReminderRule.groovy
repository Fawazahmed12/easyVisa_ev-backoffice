package com.easyvisa.questionnaire.services.rule.milestonereminder

import com.easyvisa.PackageReminderService
import com.easyvisa.enums.NotificationType
import com.easyvisa.questionnaire.answering.rule.IMilestoneReminderRule
import com.easyvisa.questionnaire.answering.rule.MilestoneReminderEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class GreenCardExpirationMilestoneReminderRule implements IMilestoneReminderRule {

    private static String RULE_NAME = 'GreenCardExpirationReminderRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry
    @Autowired(required = false)
    PackageReminderService packageReminderService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerMilestoneReminderRule(RULE_NAME, this)
    }

    @Override
    void executeMilestoneReminder(MilestoneReminderEvaluationContext ruleEvaluationContext) {
        if (packageReminderService.isGreenCardReminderActiveDate(ruleEvaluationContext.aPackage)) {
            def notificationInfoList = [
                    [notificationType: NotificationType.REMOVAL_CONDITIONS_RESIDENCE, oneTimeNotification: false]
            ]
            packageReminderService.sendReminderNotification(ruleEvaluationContext, notificationInfoList)
        }
    }

}
