package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct



/**
 * Section: Family Information
 * SubSection: Children Information
 * Question: (Q_1251) Given Name (First name)
 * Notes:  Whenever user change the value for the above question, then need to sync the same value to the below question in
 *          'Dependents (Children)' subsection. This will take care of sync process..
 *         '(Q_1269) Select Child to Auto-Fill data (If child was listed in the previous subsection)'
 */

@Component
class AutoSyncChildFirstName extends BaseComputeRule {

    private static String RULE_NAME = "AutoSyncChildFirstName";

    // Select Child to Auto-Fill data (If child was listed in the previous subsection)
    private static String SELECT_CHILD_TO_AUTOFILL_FIELD_PATH = 'Sec_familyInformation/SubSec_dependentsChildren/Q_1269';


    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.getValue(), true);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        Answer givenNameFieldAnswer = questionNodeInstance.getAnswer();
        Boolean evaluateRule = false;
        List<Answer> selectedAutoFillFieldAnswerList = nodeRuleEvaluationContext.findAnswerListByPathILike(SELECT_CHILD_TO_AUTOFILL_FIELD_PATH);
        selectedAutoFillFieldAnswerList.stream()
                .filter({ answer -> Answer.isValidAnswer(answer) && answer.value==previousAnswer.value })
                .forEach({ selectedAutoFillFieldAnswer ->
            selectedAutoFillFieldAnswer.value = givenNameFieldAnswer.getValue();
            this.answerService.saveAnswer(selectedAutoFillFieldAnswer, evaluateRule)
        });
    }
}
