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

/***
 *
 *  Section:  Sec_familyInformation / Sec_familyInformationForBeneficiary
 *  SubSection: SubSec_priorSpouses / SubSec_priorSpousesForBeneficiary
 *  Question: (Q_1226/Q_2849) Date of death of this spouse as listed on death certificate
 *
 *  This rule will apply to the above question... If user answered to the above question then auto fill the same vale to the following question
 *  Question: (Q_1228 / Q_2851) Date Marriage Ended
 *
 *  Programmer Notes: If user answered 'Death' to the question 'Date of death of this spouse as listed on death certificate',
 *                    then autofill the answer to this question with the SAME answer to question 'Date of death of this spouse as listed on death certificate'
 */


@Component
class AutoFillDateMarriageEndedRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoFillDateMarriageEndedRule";

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
        if (this.evaluateAutoFillDateMarriageEndedRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false);
    }


    private Boolean evaluateAutoFillDateMarriageEndedRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return Answer.isValidAnswer(answer);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        this.autoFillDateMarriageEndedValue(nodeRuleEvaluationContext);
    }

    private void autoFillDateMarriageEndedValue(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer dateOfDeathAnswer = questionNodeInstance.getAnswer()
        String ruleParam = questionNodeInstance.getDefinitionNode().getRuleParam();
        String dateMarriageEndedPath = ruleParam + '/' + dateOfDeathAnswer.getIndex();
        List<String> pathInfoList = dateMarriageEndedPath.split("/")
        Answer dateMarriageEndedAnswer = new Answer(packageId: nodeRuleEvaluationContext.packageId, applicantId: nodeRuleEvaluationContext.applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: dateOfDeathAnswer.value, path: dateMarriageEndedPath, index: pathInfoList[3])
        answerService.saveAnswer(dateMarriageEndedAnswer);
    }
}
