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
 * Section: Other U.S. citizen(s), LPR(s) (Lawful Permanent Resident), or any other family members with ties to the United States that you would like the USCIS to consider in deciding whether you should be lawfully admitted to the United States
 * RepeatingQuestionGroup: (RQG_otherPeopleWithTiesToUS) Other People With Ties to U.S.
 * Form: I-601
 * Notes: Here this repeatingQuestionGroup is created by questions from other section called  'Extreme Hardship for Relatives'..
 * So triggering question is not a part of this section..
 *
 * Therfore don't allow user to remove all the iterations... This rule will handle this condition..
 * */

@Component
class OtherPeopleWithTieToUSAddButtonConstraintRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'OtherPeopleWithTieToUSAddButtonConstraintRule';

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

        Map attributes = repeatingQuestionGroupNodeInstance.getAttributes();
        attributes[TemplateOptionAttributes.ADDREPEATINGBUTTON.getValue()] = true;
        attributes[TemplateOptionAttributes.REMOVEREPEATINGBUTTON.getValue()] = (totalRepeatCount != 1);
    }
}
