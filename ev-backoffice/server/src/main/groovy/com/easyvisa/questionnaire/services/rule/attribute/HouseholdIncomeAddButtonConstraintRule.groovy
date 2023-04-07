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
 * Section: Family Information
 * SubSection: Household Income
 * RepeatingQuestionGroup : (RQG_householdIncome) Household Income
 *
 * Notes:  If user clicks Add Another button, then the above questions in this  subsection are repeated,
 * except for the first question (Do  you have any siblings, parents, or adult children (living in your same residence)
 * who will be combing their income/assets to assist in supporting the beneficiary/beneficiaries in this application?).
 * This can ONLY be clicked a MAXIMUM of 3 times, because the form only allows 4 iterations and there are NO Continuation Sheets
 * for this subsection.
 */

@Component
class HouseholdIncomeAddButtonConstraintRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'HouseholdIncomeAddButtonConstraintRule'
    private static Integer MAX_ITERATIONS_ALLOWED = 4;

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
        attributes[TemplateOptionAttributes.ADDREPEATINGBUTTON.getValue()] = ((answerIndex + 1) == totalRepeatCount) ? this.evaluateAddRepeatingButtonValue(ruleEvaluationContext) : false;
        attributes[TemplateOptionAttributes.REMOVEREPEATINGBUTTON.getValue()] = true;
    }

    Boolean evaluateAddRepeatingButtonValue(NodeRuleEvaluationContext ruleEvaluationContext) {
        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Integer totalRepeatCount = repeatingQuestionGroupNodeInstance.getTotalRepeatCount();// It's not zero based..
        return (totalRepeatCount < MAX_ITERATIONS_ALLOWED);
    }
}
