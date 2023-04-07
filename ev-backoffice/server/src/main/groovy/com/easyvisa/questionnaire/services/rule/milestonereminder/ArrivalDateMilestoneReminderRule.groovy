package com.easyvisa.questionnaire.services.rule.milestonereminder

import com.easyvisa.EmailVariableService
import com.easyvisa.PackageReminderService
import com.easyvisa.enums.NotificationType
import com.easyvisa.questionnaire.answering.rule.IMilestoneReminderRule
import com.easyvisa.questionnaire.answering.rule.MilestoneReminderEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class ArrivalDateMilestoneReminderRule implements IMilestoneReminderRule {

    private static String RULE_NAME = 'ArrivalDateReminderRule'

    @Autowired(required = false)
    PackageReminderService packageReminderService
    @Autowired(required = false)
    EmailVariableService emailVariableService
    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerMilestoneReminderRule(RULE_NAME, this)
    }

    @Override
    void executeMilestoneReminder(MilestoneReminderEvaluationContext ruleEvaluationContext) {
        def notificationInfoList = [
                [notificationType:NotificationType.MARRIAGE_IN_90_DAYS_OF_ARRIVAL, oneTimeNotification:false]
        ]
        Map params = emailVariableService.addMarriageExpiration([:], packageReminderService.getMarriageExpiration(ruleEvaluationContext.aPackage))
        this.packageReminderService.sendReminderNotification(ruleEvaluationContext, notificationInfoList, params)
    }
}
