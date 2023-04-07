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
class MarriageDateMilestoneReminderRule implements IMilestoneReminderRule {

    private static String RULE_NAME = 'MarriageDateReminderRule'

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
        def notificationInfoList = [
                [notificationType:NotificationType.PERMANENT_RESIDENCE_CARD, oneTimeNotification:false],
                [notificationType:NotificationType.MARRIAGE_CERTIFICATE, oneTimeNotification:true],
                [notificationType:NotificationType.WORK_AUTHORIZATION, oneTimeNotification:true]
        ]
        this.packageReminderService.sendReminderNotification(ruleEvaluationContext, notificationInfoList)
        this.packageReminderService.updatePackageReminder(ruleEvaluationContext.aPackage,
                NotificationType.MARRIAGE_IN_90_DAYS_OF_ARRIVAL, Boolean.TRUE)
    }
}
