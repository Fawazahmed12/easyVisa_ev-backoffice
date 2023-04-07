package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.enums.Country
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule will check whether the selected value from countyList dropdown is 'US' or not.
 * This rule will generate two labels 'US' and 'Other'.
 * The rule will check if the answer is anything other than 'US', it will return "other" as the outcome
 * This question's child nodes have two links one with 'US' and the other as 'other'
 *
 *
 * This is rule is reused in multiple sections (the param this will makes use of is different)
 */
@Component
class EmployerCountrySelectionRule extends BaseComputeRule {

    private static String RULE_NAME = 'EmployerCountrySelectionRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(this.getRuleName(), this)
    }

    @Override
    public Outcome evaluateOutcome(NodeRuleEvaluationContext answerContext) {
        if (this.evaluateEmployerCountrySelectionRule(answerContext)) {
            return new Outcome(RelationshipTypeConstants.UNITED_STATES.value, true)
        }
        return new Outcome(RelationshipTypeConstants.OTHER.value, false)
    }

    private Boolean evaluateEmployerCountrySelectionRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return Answer.isValidAnswer(answer) && answer.doesMatch(Country.UNITED_STATES.getDisplayName())
    }

    protected String getRuleName() {
        return EmployerCountrySelectionRule.RULE_NAME;
    }
}
