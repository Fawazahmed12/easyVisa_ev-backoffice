package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.math.RoundingMode

/**
 * Section: Biographic Information (Petitioner and Beneficiary)
 * SubSection: Height
 * Question: (Q_102 / Q_2312) Centimeters
 * Notes:  This only appears if user selected 'Metric' to the
 *         question 'What units of measure do you use?'
 *
 * This rule will convert the given height value in centimeters to height in feet and inches..
 * And save the conversion(feet and inches) values to corresponding paths based on
 * the argument passed to this question.
 *
 * Here Feet and Inches questions paths are passed by ruleParams..
 */

@Component
class CentimeterToFeetConversionRule extends BaseComputeRule {

    private static String RULE_NAME = 'CentimeterToFeetConversionRule'

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
        if (this.hasValidHeight(answerContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false)
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        this.saveHeightConversionAnswers(nodeRuleEvaluationContext);
    }


    private Boolean hasValidHeight(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return Answer.isValidAnswer(answer)
    }

    private saveHeightConversionAnswers(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = ruleParam.split(",");
        String feetFieldPath = ruleParams[0];
        String inchesFieldPath = ruleParams[1];

        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) easyVisaNodeInstance;
        Answer centimeterAnswer = questionNodeInstance.getAnswer()
        int totalHeightInCentimeter = centimeterAnswer.getValue() as Integer
        BigDecimal totalHeightInInches = (totalHeightInCentimeter / 2.54);
        BigDecimal oneFeetInInches = new BigDecimal("12");

        BigDecimal inchesBigDecimalValue = (totalHeightInInches.remainder(oneFeetInInches));
        double inchesDoubleValue = inchesBigDecimalValue.doubleValue();
        def inchesFloorValue = Math.floor(inchesDoubleValue);
        def inchesValue = inchesFloorValue.round();

        // https://stackoverflow.com/questions/4591206/arithmeticexception-non-terminating-decimal-expansion-no-exact-representable/4591223
        def feetBigDecimalValue = totalHeightInInches.divide(oneFeetInInches, 2, RoundingMode.HALF_DOWN);
        double feetDoubleValue = feetBigDecimalValue.doubleValue();
        def feetValue = feetDoubleValue.trunc() as Integer;

        saveHeightConversionAnswer(feetFieldPath, feetValue.toString(), nodeRuleEvaluationContext);
        saveHeightConversionAnswer(inchesFieldPath, inchesValue.toString(), nodeRuleEvaluationContext);
    }

    private saveHeightConversionAnswer(String questionFieldPath, String answerValue, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        List<String> pathInfoList = questionFieldPath.split("/");
        Answer feetAnswer = new Answer(packageId: nodeRuleEvaluationContext.packageId, applicantId: nodeRuleEvaluationContext.applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: answerValue, path: questionFieldPath)
        Boolean evaluateRule = false;
        answerService.saveAnswer(feetAnswer, evaluateRule);
    }
}
