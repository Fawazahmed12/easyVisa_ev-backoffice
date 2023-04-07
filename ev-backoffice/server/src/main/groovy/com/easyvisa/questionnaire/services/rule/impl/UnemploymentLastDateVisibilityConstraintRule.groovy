package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Employment History
 * SubSection: Employment Status
 * Question: (Q_1010) What was the last date of your unemployment?
 *           (Q_1012) What was the last date of your unemployment?
 *
 * Notes: This question ONLY appears for the SECOND (and subsequent iterations) of 'Employment Status XX’
 */
@Component
class UnemploymentLastDateVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = "UnemploymentLastDateVisibilityConstraintRule";
    private static Integer ITERATION_COUNT = 2;

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
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Integer repeatingIndex = questionNodeInstance.getRepeatingIndex(); //it is zero based value
        Integer iterationCount = ++repeatingIndex;
        if (iterationCount < ITERATION_COUNT) {
            questionNodeInstance.setVisibility(false);
        }
    }
}
