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
class AutoSyncAddressFieldValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'AutoSyncAddressFieldValidationRule'

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
        String MAILING_ADDRESS_POPULATE_PATH = ruleParams[0];
        Answer currentMailingAddressPopulateAnswer = ruleEvaluationContext.findAnswerByPath(MAILING_ADDRESS_POPULATE_PATH);
        if (!Answer.isValidAnswer(currentMailingAddressPopulateAnswer)) {
            return questionNodeInstance.isVisibility();
        }

        String isCurrentMailingAddressSameAsPhysicalAddress = EasyVisaNode.normalizeAnswer(currentMailingAddressPopulateAnswer.getValue())
        if (isCurrentMailingAddressSameAsPhysicalAddress == RelationshipTypeConstants.YES.value) {
            return true
        }
        return questionNodeInstance.isVisibility();
    }
}
