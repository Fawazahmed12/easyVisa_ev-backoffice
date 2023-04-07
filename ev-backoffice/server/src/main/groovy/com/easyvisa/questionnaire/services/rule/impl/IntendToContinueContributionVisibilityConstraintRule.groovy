package com.easyvisa.questionnaire.services.rule.impl


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Support & Contributions
 * SubSection: Nature of Contributions
 * Question: (Q_1408) For how many [weeks, months, years] do you intend to continue making this contribution?
 *
 * Notes:  The above question ONLY gets asked if the user answered
 *         Other than 'Lump Sum' to the question
 *         Q_1407: 'How frequently do you intend to make this contribution?'
 *
 * Notes: This question does not visible to the user. But it gets Printed in the form
 */
@Component
class IntendToContinueContributionVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = 'IntendToContinueContributionVisibilityConstraintRule'
    private static String LUMP_SUM_VALUE = "Lump Sum"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.getValue(), true)
    }


    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String attributeRuleParam = questionNodeInstance.getAttributeRuleParam()
        String intendToThisContributionFieldPath = attributeRuleParam + '/' + questionNodeInstance.getRepeatingIndex()
        Answer intendToThisContributionAnswer = nodeRuleEvaluationContext.findAnswerByPath(intendToThisContributionFieldPath)
        if (Answer.isValidAnswer(intendToThisContributionAnswer) && StringUtils.equals(intendToThisContributionAnswer.value, LUMP_SUM_VALUE)) {
            questionNodeInstance.setVisibility(false)
        }
    }

}
