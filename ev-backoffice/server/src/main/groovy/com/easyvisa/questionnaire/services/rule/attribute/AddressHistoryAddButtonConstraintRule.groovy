package com.easyvisa.questionnaire.services.rule.attribute


import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Address History
 * SubSection: Previous Physical Address(es) within 5 Years
 * Applicant: Petitioner / Beneficiary
 * RepeatingQuestionGroup : (RQG_previousPhysicalAddress) Previous Physical Address(es) within 5 Years
 *
 * Notes:  Here we are applying this rule to 'RepeatingQuestionGroup'. This rule will set the following attribute values as false..
 *  1. showRemoveButton ( If we are having only one iteration, then we should hot display this button)
 *  2. showAddButton ( Show this button only for the last iteration)
 */

@Component
class AddressHistoryAddButtonConstraintRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'AddressHistoryAddButtonConstraintRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Integer totalRepeatCount = repeatingQuestionGroupNodeInstance.getTotalRepeatCount();

        Integer answerIndex = repeatingQuestionGroupNodeInstance.getAnswerIndex(); //zero based value
        Integer currentIterationValue = answerIndex + 1;

        Map attributes = repeatingQuestionGroupNodeInstance.getAttributes()
        attributes[TemplateOptionAttributes.REMOVEREPEATINGBUTTON.getValue()] = (totalRepeatCount != 1)
        attributes[TemplateOptionAttributes.ADDREPEATINGBUTTON.getValue()] = (currentIterationValue == totalRepeatCount)
    }
}
