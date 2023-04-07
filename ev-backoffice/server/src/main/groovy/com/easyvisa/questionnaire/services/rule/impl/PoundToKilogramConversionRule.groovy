package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.math.RoundingMode


/**
 * Section: Biographic Information (Petitioner and Beneficiary)
 * SubSection: Weight
 * Question: (Q_106 / Q_2315) What is your weight?  [(i.e) in lbs]
 * Notes:  This only appears if user selected 'Imperial' to the
 *         question 'What units of measure do you use?'
 *
 * This rule will convert the given weight value in lbs to weight in kg..
 * And save the conversion(ks) value to corresponding path based on
 * the argument passed to this question.
 *
 */

@Component
class PoundToKilogramConversionRule extends BaseComputeRule {

    private static String RULE_NAME = 'PoundToKilogramConversionRule'

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
        Answer weightInLbsAnswer = questionNodeInstance.getAnswer()
        BigDecimal weightInLbs = new BigDecimal(weightInLbsAnswer.value);
        // https://stackoverflow.com/questions/4591206/arithmeticexception-non-terminating-decimal-expansion-no-exact-representable/4591223
        BigDecimal weightInKilogram = weightInLbs.divide(2.2, 2, RoundingMode.HALF_DOWN); // This will convert lbs into kg
        double weightInKgDoubleValue = weightInKilogram.doubleValue();
        long weightInKg = weightInKgDoubleValue.round();
        String weightInKgFieldPath = questionNodeInstance.getRuleParam();
        saveWeightConversionAnswer(weightInKgFieldPath, weightInKg.toString(), nodeRuleEvaluationContext);
    }


    private saveWeightConversionAnswer(String questionFieldPath, String answerValue, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        List<String> pathInfoList = questionFieldPath.split("/");
        Answer centimeterAnswer = new Answer(packageId: nodeRuleEvaluationContext.packageId, applicantId: nodeRuleEvaluationContext.applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: answerValue, path: questionFieldPath);
        Boolean evaluateRule = false;
        answerService.saveAnswer(centimeterAnswer, evaluateRule);
    }
}
