package com.easyvisa.questionnaire.services.rule.answervisibilityvalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 * When one unit of measure has been entered,
 * then if the user changes to another unit of measure, then convert the entered data into selected unit .
 * Here need to maintain the both unit answers
 *
 * Example:
 *
 * Section: Biographic Information
 * SubSection: Weight
 * Question: (Q_105) What units of measure do you use?
 * Options: [Metric, Imperial]
 *
 * If Metric then display, (Q_6009) What is your weight? (In Kilogram)
 * If Imperial then display, (Q_106) What is your weight? (In lbs)
 *
 * Section: Biographic Information
 * SubSection: Height
 * Question: (Q_101) What units of measure do you use?
 * Options: [Metric, Imperial]
 *
 * If Metric then display, (Q_102) Centimeters,
 * If Imperial then display, (Q_103) Feet and (Q_104) Inches
 * */

@Component
class BMINumberInputValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'BMINumberInputValidationRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerVisibilityValidationRule(RULE_NAME, this);
    }

    @Override
    void populatePdfFieldAnswer(NodeRuleEvaluationContext ruleEvaluationContext) {

    }

    @Override
    Boolean validateAnswerVisibility(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String bmiUnitMeasurementFieldPath = questionNodeInstance.getAnswerVisibilityValidationRuleParam()
        Answer bmiUnitMeasurementAnswer = ruleEvaluationContext.findAnswerByPath(bmiUnitMeasurementFieldPath)
        return Answer.isValidAnswer(bmiUnitMeasurementAnswer);
    }
}
