package com.easyvisa.questionnaire.services.rule.questionreset

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.rule.IResetQuestionRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/***
 *  Here this repeating-question-group is available only if user answered 'Yes'
 *  to the question(passed by param)
 *
 *  This rule will reset the above triggering question. (i.e) save its value as 'No'.
 */

@Component
class RepeatingQuestionsResetRule implements IResetQuestionRule {

    private static String RULE_NAME = "RepeatingQuestionsResetRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerQuestionResetRule(RULE_NAME, this);
    }

    @Override
    void resetTriggeringQuestions(NodeRuleEvaluationContext ruleEvaluationContext) {
        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance = (RepeatingQuestionGroupNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = repeatingQuestionGroupInstance.getResetRuleParam();
        String[] ruleParams = ruleParam.split(",");
        String QUESTION_FIELD_PATH = ruleParams[0];
        String ANSWER_VALUE = ruleParams[1];
        List<String> pathInfoList = QUESTION_FIELD_PATH.split("/")
        Answer repeatQuestionResetAnswer = new Answer(packageId: ruleEvaluationContext.packageId, applicantId: ruleEvaluationContext.applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: ANSWER_VALUE, path: QUESTION_FIELD_PATH)
        answerService.saveAnswer(repeatQuestionResetAnswer)
    }
}
