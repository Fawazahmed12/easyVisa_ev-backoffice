package com.easyvisa.questionnaire.services.rule.visibility


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class EmploymentEndDateIterationVisibilityRule extends QuestionnaireFormVisibilityWithIterationOnRule {

    private static String RULE_NAME = "EmploymentEndDateIterationVisibilityRule"

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerVisibilityComputeRules(RULE_NAME, this)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        Boolean visibility = evaluateEmploymentEndDateIterationVisibilityRule(ruleEvaluationContext)
        easyVisaNodeInstance.setVisibility(visibility)
    }

    Boolean evaluateEmploymentEndDateIterationVisibilityRule(NodeRuleEvaluationContext ruleEvaluationContext) {
        return evaluateQuestionnaireFormVisibilityWithIterationOnRule(ruleEvaluationContext) && evaluateEmploymentEndDateVisibility(ruleEvaluationContext)
    }


    Boolean evaluateEmploymentEndDateVisibility(NodeRuleEvaluationContext ruleEvaluationContext) {
        String ruleParam = getEmploymentEndDateRuleParam(ruleEvaluationContext)
        String ARE_YOU_STILL_WORKING_PATH = ruleParam.split(',')[0]
        String areYouStillWorkingAnswerValue = ruleParam.split(',')[1]

        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String areYouStillWorkingFieldPath = ARE_YOU_STILL_WORKING_PATH + '/' + questionNodeInstance.getRepeatingIndex()
        Answer areYouStillWorkingAnswer = ruleEvaluationContext.findAnswerByPath(areYouStillWorkingFieldPath)

        if (!Answer.isValidAnswer(areYouStillWorkingAnswer) && areYouStillWorkingAnswerValue == RelationshipTypeConstants.YES.value) {
            return false
        }

        if (Answer.isValidAnswer(areYouStillWorkingAnswer) && areYouStillWorkingAnswer.getValue() != areYouStillWorkingAnswerValue) {
            return false
        }

        return true
    }


    private String getEmploymentEndDateRuleParam(ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        String[] ruleParams = easyVisaNodeInstance.getVisibilityRuleParam().split("\\|")
        return ruleParams[2]
    }
}


