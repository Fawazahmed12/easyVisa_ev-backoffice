package com.easyvisa.questionnaire.services.rule.displaytext

import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDisplayTextRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Employment History
 * SubSection: Employment Status
 * RepeatingQuestionGroup: Employer/Employment Status #iterationCount
 * Question: Employer/Employment Status #iterationCount
 * Notes:  This rule applies to both question and repeating-question-group
 */

@Component
class EmploymentStatusDisplayTextRule implements IDisplayTextRule {

    private static String RULE_NAME = 'EmploymentStatusDisplayTextRule'
    private static String ITERATION_INDEX_PLACEHOLDER = '#iterationCount'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDisplayTextRule(RULE_NAME, this);
    }

    @Override
    String generateDisplayText(NodeRuleEvaluationContext ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance();
        String displayText = easyVisaNodeInstance.getDisplayText();
        Integer repeatingIndex = this.getRepeatingIndex(easyVisaNodeInstance);//it is zero based value
        Integer childCount = ++repeatingIndex;
        String updatedDisplayText = displayText.replaceAll(ITERATION_INDEX_PLACEHOLDER, "${childCount}")
        return updatedDisplayText
    }

    Integer getRepeatingIndex(EasyVisaNodeInstance easyVisaNodeInstance) {
        Integer repeatingIndex = 0;
        if (easyVisaNodeInstance instanceof QuestionNodeInstance) {
            QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) easyVisaNodeInstance;
            repeatingIndex = questionNodeInstance.getRepeatingIndex();
        } else if (easyVisaNodeInstance instanceof RepeatingQuestionGroupNodeInstance) {
            RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) easyVisaNodeInstance;
            repeatingIndex = repeatingQuestionGroupNodeInstance.getAnswerIndex();
        }
        return repeatingIndex;
    }
}
