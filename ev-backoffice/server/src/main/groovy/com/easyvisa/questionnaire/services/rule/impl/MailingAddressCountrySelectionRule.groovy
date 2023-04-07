package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.Country
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.util.CurrentMailingAddressRuleUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 * Section: Address History
 * SubSection: Current Mailing Address
 * Question: Q_70
 *
 * This rule will check whether the selected value from countyList dropdown is 'US' or not.
 * This rule will generate two labels 'US' and 'Other'.
 * The rule will check if the answer is anything other than 'US', it will return "other" as the outcome
 * This question's child nodes have two links one with 'US' and the other as 'other'
 *
 * EV-3331: This rule also determines if the Country is to be hardcoded as USA
 *
 */
@Component
class MailingAddressCountrySelectionRule extends BaseComputeRule {

    private static String RULE_NAME = 'MailingAddressCountrySelectionRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry


    PackageQuestionnaireService packageQuestionnaireService
    CurrentMailingAddressRuleUtil currentMailingAddressRuleUtil

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    public Outcome evaluateOutcome(NodeRuleEvaluationContext answerContext) {
        // if Benefit Category has Form 864, then mailing address country is fixed to US
        if (hasForm864(answerContext) || this.evaluateEmployerCountrySelectionRule(answerContext)) {
            return new Outcome(RelationshipTypeConstants.UNITED_STATES.value, true)
        }

        return new Outcome(RelationshipTypeConstants.OTHER.value, true)
    }

    private Boolean evaluateEmployerCountrySelectionRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return Answer.isValidAnswer(answer) && answer.doesMatch(Country.UNITED_STATES.displayName)
    }

    private Boolean hasForm864(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return packageQuestionnaireService.isQuestionIncluded(nodeRuleEvaluationContext, PdfForm.I864)
    }

    @Override
    String determineAnswer(NodeRuleEvaluationContext ruleEvaluationContext, Answer answer, Outcome outcome) {
        if (hasForm864(ruleEvaluationContext)) {
            // Value is fixed to US if Benefit Category has Form 864
            return Country.UNITED_STATES.displayName
        } else {
            return answer?.value
        }

    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()

        if (currentMailingAddressRuleUtil.isQuestionVisible(ruleEvaluationContext)) {
            questionNodeInstance.setVisibility(true)
        } else {

            questionNodeInstance.setVisibility(false)
        }
    }
}
