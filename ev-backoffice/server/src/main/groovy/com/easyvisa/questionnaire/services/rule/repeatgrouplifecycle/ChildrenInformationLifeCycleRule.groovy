package com.easyvisa.questionnaire.services.rule.repeatgrouplifecycle

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.rule.IRepeatGroupLifeCycleRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class ChildrenInformationLifeCycleRule implements IRepeatGroupLifeCycleRule {

    private static String RULE_NAME = "ChildrenInformationLifeCycleRule";

    //Given Name (First name)
    private static String SELECT_NAME_FIELD_PATH = 'Sec_familyInformation/SubSec_childrenInformation/Q_1251';

    // Select Child to Auto-Fill data (If child was listed in the previous subsection)
    private static String SELECT_CHILD_TO_AUTOFILL_FIELD_PATH = 'Sec_familyInformation/SubSec_dependentsChildren/Q_1269';

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerRepeatGroupLifeCycleRule(RULE_NAME, this);
    }

    @Override
    void onEntry(NodeRuleEvaluationContext ruleEvaluationContext) {
    }

    @Override
    void onPreExit(NodeRuleEvaluationContext ruleEvaluationContext) {
        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String selectNameFieldPath = SELECT_NAME_FIELD_PATH + '/' + repeatingQuestionGroupNodeInstance.getAnswerIndex();
        Answer selectNameFieldAnswer = ruleEvaluationContext.findAnswerByPath(selectNameFieldPath);
        if (!Answer.isValidAnswer(selectNameFieldAnswer)) {
            return;
        }

        Boolean evaluateRule = false;
        List<Answer> selectedAutoFillFieldAnswerList = ruleEvaluationContext.findAnswerListByPathILike(SELECT_CHILD_TO_AUTOFILL_FIELD_PATH);
        selectedAutoFillFieldAnswerList.stream()
                .filter({ answer -> Answer.isValidAnswer(answer) && answer.value == selectNameFieldAnswer.value })
                .forEach({ selectedAutoFillFieldAnswer ->
            selectedAutoFillFieldAnswer.value = '';
            this.answerService.saveAnswer(selectedAutoFillFieldAnswer, evaluateRule)
        });
    }

    @Override
    void onPostExit(NodeRuleEvaluationContext ruleEvaluationContext) {
    }
}
