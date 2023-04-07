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
 * Section: Relatives Who Will Experience Extreme Hardship if You Are Inadmissible to the United States
 * Question: (Q_3657) Do you have another relative (only a spouse or parent and they MUST be either a U.S. citizen or  LPR (Lawful Permanent Resident)) who would experience extreme hardship if you were refused admission to the United States?
 * Form: I-601
 * Notes: This question only appers ONCE, on the first iteration.
 *        After that, it disappears and the [Add Another] button appears at the end of each iteration.
 * */

@Component
class AnotherRelativeInForm601WouldExperienceExtremeHardshipApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "AnotherRelativeInForm601WouldExperienceExtremeHardshipApplicableRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry


    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
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
        Answer answer = questionNodeInstance.getAnswer();
        Integer repeatingIndex = answer.getIndex();
        if (repeatingIndex != 0) {
            questionNodeInstance.setVisibility(false);
        }
    }
}
