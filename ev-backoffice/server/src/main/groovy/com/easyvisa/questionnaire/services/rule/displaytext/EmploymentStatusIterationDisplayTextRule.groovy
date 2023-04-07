package com.easyvisa.questionnaire.services.rule.displaytext


import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDisplayTextRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct



/**
 * Section: Employment History
 * SubSection: Employment Status
 * Question:
 *      (Q_1008) What is your current employment status?
 *      (Q_1013) Are you self-employed?
 *      (Q_1029) What is your occupation at this employer?
 *
 * Notes:  This rule applies to the above questions.. This rule will change the question label
 * if it comes in second (and subsequent iterations) inside repeating-group
 */


@Component
class EmploymentStatusIterationDisplayTextRule implements IDisplayTextRule {

    private static String RULE_NAME = 'EmploymentStatusIterationDisplayTextRule'
    private static Integer ITERATION_COUNT = 2;

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDisplayTextRule(RULE_NAME, this);
    }

    @Override
    String generateDisplayText(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance(); \

        String displayText = questionNodeInstance.getDisplayText();
        Integer repeatingIndex = questionNodeInstance.getRepeatingIndex(); //it is zero based value
        Integer iterationCount = ++repeatingIndex;
        if (iterationCount >= ITERATION_COUNT) {
            String updatedDisplayText = questionNodeInstance.getDisplayTextRuleParam();
            return updatedDisplayText;
        }
        return displayText;
    }
}
