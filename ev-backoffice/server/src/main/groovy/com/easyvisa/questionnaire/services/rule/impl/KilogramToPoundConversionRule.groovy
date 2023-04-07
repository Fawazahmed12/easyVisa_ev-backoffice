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
 * Section: Biographic Information (Petitioner and Beneficiary)
 * SubSection: Weight
 * Question: (Q_6009 / Q_6010) What is your weight?  [(i.e) in kilogram]
 * Notes:  This only appears if user selected 'Metric' to the
 *         question 'What units of measure do you use?'
 *
 * This rule will convert the given weight value in kilogram to weight in lbs..
 * And save the conversion(lbs) value to corresponding path based on
 * the argument passed to this question.
 *
 */

@Component
class KilogramToPoundConversionRule extends BaseComputeRule {

    private static String RULE_NAME = 'KilogramToPoundConversionRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    AnswerService answerService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    public Outcome evaluateOutcome(NodeRuleEvaluationContext answerContext) {
        if (this.hasValidWeight(answerContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false)
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        this.saveWeightConversionAnswers(nodeRuleEvaluationContext);
    }


    private Boolean hasValidWeight(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return Answer.isValidAnswer(answer)
    }

    private saveWeightConversionAnswers(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        Answer weightInKilogramAnswer = questionNodeInstance.getAnswer()
        double weightInKilogram = weightInKilogramAnswer.getValue() as double;
        BigDecimal weightInLbs = (weightInKilogram * 2.2); // This will convert kg into lbs
        double weightInLbsDoubleValue = weightInLbs.doubleValue();
        def weightInPounds = weightInLbsDoubleValue.round();
        String weightInPoundFieldPath = questionNodeInstance.getRuleParam();
        saveWeightConversionAnswer(weightInPoundFieldPath, weightInPounds.toString(), nodeRuleEvaluationContext);
    }

    private saveWeightConversionAnswer(String questionFieldPath, String answerValue, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        List<String> pathInfoList = questionFieldPath.split("/");
        Answer feetAnswer = new Answer(packageId: nodeRuleEvaluationContext.packageId, applicantId: nodeRuleEvaluationContext.applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: answerValue, path: questionFieldPath)
        Boolean evaluateRule = false;
        answerService.saveAnswer(feetAnswer, evaluateRule);
    }
}
