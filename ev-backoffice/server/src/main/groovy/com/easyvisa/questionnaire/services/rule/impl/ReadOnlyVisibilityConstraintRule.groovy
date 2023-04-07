package com.easyvisa.questionnaire.services.rule.impl


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * In questionnaire, there are some questions which has default values...
 * And don't need to display this question in questionnaire page..
 * But need to print its default value in USCIS forms..
 *
 * In this case, we are setting its visibility as false from this rule.
 *
 * Example:
 *
 * Section: Address History
 * SubSection: Current Physical Address
 * Question: (Q_6000) When did you move out of this address?
 */

@Component
class ReadOnlyVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = "ReadOnlyVisibilityConstraintRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.getValue(), true);
    }


    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance();
        easyVisaNodeInstance.setVisibility(false);
    }
}
