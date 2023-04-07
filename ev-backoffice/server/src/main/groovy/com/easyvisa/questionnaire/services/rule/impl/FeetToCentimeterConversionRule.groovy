package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Biographic Information (Petitioner and Beneficiary)
 * SubSection: Height
 * Question: (Q_103 / Q_2310) Feet
 *           (Q_104 / Q_2311) Inches
 * Notes:  This only appears if user selected 'Imperial' to the
 *         question 'What units of measure do you use?'
 *
 * This rule will convert the given height value in feet and inches into height in centimeters..
 * And save the conversion(centimeters) value to corresponding path based on
 * the argument passed to this question.
 *
 * Since this rule is common to both questions, here we are passing
 *  question (Feet,Inches and Centimeters) paths by ruleParams..
 */
@Component
class FeetToCentimeterConversionRule extends BaseComputeRule {

    private static String RULE_NAME = 'FeetToCentimeterConversionRule'

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
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = ruleParam.split(",");
        String feetFieldPath = ruleParams[0];
        Answer feetAnswer = nodeRuleEvaluationContext.findAnswerByPath(feetFieldPath);
        return Answer.isValidAnswer(feetAnswer)
    }


    private saveHeightConversionAnswers(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = ruleParam.split(",");
        String feetFieldPath = ruleParams[0];
        String inchesFieldPath = ruleParams[1];
        String centimeterFieldPath = ruleParams[2];

        Answer feetAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, feetFieldPath)
        Answer inchesAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, inchesFieldPath)

        int feetValue = feetAnswer.getValue() as Integer;
        int inchesValue = Answer.isValidAnswer(inchesAnswer) ? inchesAnswer.getValue() as Integer : 0;

        int totalHeightInInches = (feetValue * 12) + inchesValue;
        BigDecimal centimeterBigDecimalValue = (totalHeightInInches * 2.54);
        double centimeterDoubleValue = centimeterBigDecimalValue.doubleValue();
        def centimeterValue = centimeterDoubleValue.trunc() as Integer;

        saveHeightConversionAnswer(centimeterFieldPath, centimeterValue.toString(), nodeRuleEvaluationContext);
    }


    private saveHeightConversionAnswer(String questionFieldPath, String answerValue, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        List<String> pathInfoList = questionFieldPath.split("/");
        Answer centimeterAnswer = new Answer(packageId: nodeRuleEvaluationContext.packageId, applicantId: nodeRuleEvaluationContext.applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: answerValue, path: questionFieldPath);
        Boolean evaluateRule = false;
        answerService.saveAnswer(centimeterAnswer, evaluateRule);
    }
}
