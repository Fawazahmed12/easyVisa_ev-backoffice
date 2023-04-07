package com.easyvisa.questionnaire.services.rule.answervisibilityvalidation


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class AutoSyncParentNameFieldValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'AutoSyncParentNameFieldValidationRule'

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
        String answerVisibilityValidationRuleParam = questionNodeInstance.getAnswerVisibilityValidationRuleParam();
        String[] ruleParams = answerVisibilityValidationRuleParam.split(",");
        String BIRTH_NAME_POPULATE_PATH = ruleParams[0];
        Answer currentNamePopulateAnswer = ruleEvaluationContext.findAnswerByPath(BIRTH_NAME_POPULATE_PATH);
        if (!Answer.isValidAnswer(currentNamePopulateAnswer)) {
            return questionNodeInstance.isVisibility();
        }

        String isCurrentNameSameAsBirthName = EasyVisaNode.normalizeAnswer(currentNamePopulateAnswer.getValue())
        if (isCurrentNameSameAsBirthName == RelationshipTypeConstants.YES.value) {
            return true
        }
        return questionNodeInstance.isVisibility();
    }
}
