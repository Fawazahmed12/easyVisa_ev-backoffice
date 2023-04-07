package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 *   Aapply this rule to 'SubSection' called 'Current Spouse'.
 *   If user selected 'Married' OR 'Legally Separated' for the question 'What is your current marital status?',
 *   then generate 'Current Spouse' subsection below.
 *   Also, if user selected 'Married' OR 'Legally Separated' option and the
 *   Immigration Benefit category is K-1/K-3, then a Warning message goes to the
 *   attorney's Warnings tab in the Task Queue.
 *
 *   This rule will check the  answers to the following question from 'Marital Status' subsection
 *    a.   What is your current marital status? -> 'Married' OR 'Legally Separated'
 * This rule will generate two labels 'yes' and 'no'. If the above condition are true then the result is 'yes'.
 */


@Component
class CurrentSpouseApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = 'CurrentSpouseApplicableRule';

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluateCurrentSpouseApplicableRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false);
    }


    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return this.evaluateCurrentSpouseApplicableRule(nodeRuleEvaluationContext);
    }

    private Boolean evaluateCurrentSpouseApplicableRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String MARITAL_STATUS_FIELD_PATH = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        List<Answer> maritalStatusAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), MARITAL_STATUS_FIELD_PATH);
        if (maritalStatusAnswerList.isEmpty()) {
            return false;
        }

        Answer maritalStatusAnswer = maritalStatusAnswerList[0];
        String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue());
        if (maritalStatusAnswerValue == RelationshipTypeConstants.MARRIED.value || maritalStatusAnswerValue == RelationshipTypeConstants.LEGALLY_SEPERATED.value) {
            return true;
        }
        return false;
    }

}
