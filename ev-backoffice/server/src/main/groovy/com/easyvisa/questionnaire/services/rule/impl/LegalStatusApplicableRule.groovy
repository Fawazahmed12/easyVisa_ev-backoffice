package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 * This rule is applicable to the following subsections in the section(Legal Status in U.S.)
 * 1. U.S. Citizens (Subsection)
 * 2. LPR (Lawful Permanent Resident), also commonly referred to as a Green Card holder (Subsection)
 *
 *
 */

@Component
class LegalStatusApplicableRule extends BaseComputeRule {
    private static String RULE_NAME = "LegalStatusApplicableRule";
    private static String LEGAL_STATUS_IN_US_PATH = "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluateLegalStatusApplicableRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false);
    }


    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return (this.evaluateLegalStatusApplicableRule(nodeRuleEvaluationContext))
    }

    private Boolean evaluateLegalStatusApplicableRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Answer legalStatusInUSAnswer = nodeRuleEvaluationContext.findAnswerByPath(LEGAL_STATUS_IN_US_PATH);
        if (!Answer.isValidAnswer(legalStatusInUSAnswer)) {
            return false;
        }

        String legalStatusInUSAnswerValue = legalStatusInUSAnswer.getValue();
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        return (legalStatusInUSAnswerValue == ruleParam);
    }
}
