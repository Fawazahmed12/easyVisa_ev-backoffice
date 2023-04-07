package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Employment History
 * SubSection: Employment Status
 * Question: (Q_1015) Are you still working at this employer?
 *
 * Notes: This question ONLY appears for the SECOND (and subsequent iterations) of 'Employment Status XX’
 */
@Component
class AreYouStillWorkingAtThisEmployerRule extends BaseComputeRule {

    private static String RULE_NAME = "AreYouStillWorkingAtThisEmployerRule";
    private static Integer ITERATION_COUNT = 2;
    private static String EMPLOYMENT_END_VALUE = "TO PRESENT"; // For Current Employmmnet
    //When did this employment end for this employer?

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        boolean successfulMatch = Answer.isValidAnswer(answer) && answer.doesMatch(RelationshipTypeConstants.YES.value)
        return new Outcome(answer.getValue(), successfulMatch);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        String EMPLOYMENT_END_PATH = questionNodeInstance.getDefinitionNode().getRuleParam();
        String employmentEndFieldPath = EMPLOYMENT_END_PATH + '/' + answer.getIndex();
        List<String> pathInfoList = employmentEndFieldPath.split("/")
        Answer employmentEndAnswer = new Answer(packageId: ruleEvaluationContext.packageId, applicantId: ruleEvaluationContext.applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: EMPLOYMENT_END_VALUE, path: employmentEndFieldPath, index: pathInfoList[3])
        answerService.saveAnswer(employmentEndAnswer);
    }
}
