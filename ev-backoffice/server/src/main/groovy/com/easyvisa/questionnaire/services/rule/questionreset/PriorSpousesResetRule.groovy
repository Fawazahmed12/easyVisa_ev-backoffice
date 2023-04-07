package com.easyvisa.questionnaire.services.rule.questionreset

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.rule.IResetQuestionRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *   This rule will reset the 'Prior Spouses' repeating-group triggering questions.
 *
 *   The 'Prior Spouses' susbsection will get generated if a user answers 'Yes' to the question:
 *   'Where you married to anyone prior to this person?'.
 *
 *   This rule will reset the  above question answer to 'No'
 */

@Component
class PriorSpousesResetRule implements IResetQuestionRule {

    private static String RULE_NAME = "PriorSpousesResetRule";

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
        String previouslyMarriedFieldPath = repeatingQuestionGroupInstance.getResetRuleParam();
        Answer maritalStatusAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, previouslyMarriedFieldPath);
        if (Answer.isValidAnswer(maritalStatusAnswer) && maritalStatusAnswer.getValue() == RelationshipTypeConstants.YES.value) {
            List<String> pathInfoList = previouslyMarriedFieldPath.split("/")
            String ANSWER_VALUE = RelationshipTypeConstants.NO.value;
            Answer repeatQuestionResetAnswer = new Answer(packageId: ruleEvaluationContext.packageId, applicantId: ruleEvaluationContext.applicantId,
                    sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                    value: ANSWER_VALUE, path: previouslyMarriedFieldPath)
            answerService.saveAnswer(repeatQuestionResetAnswer)
        }
    }
}
