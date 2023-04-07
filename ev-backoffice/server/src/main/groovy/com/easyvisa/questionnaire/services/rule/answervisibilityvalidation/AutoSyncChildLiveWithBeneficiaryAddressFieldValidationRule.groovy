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
class AutoSyncChildLiveWithBeneficiaryAddressFieldValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'AutoSyncChildLiveWithBeneficiaryAddressFieldValidationRule'

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
        Integer repeatingIndex = questionNodeInstance.repeatingIndex;
        String answerVisibilityValidationRuleParam = questionNodeInstance.getAnswerVisibilityValidationRuleParam();
        String[] ruleParams = answerVisibilityValidationRuleParam.split(",");
        String ADDRESS_POPULATE_PATH = "${ruleParams[0]}/${repeatingIndex}";
        Answer childLiveWithBeneficiaryAddressPopulateAnswer = ruleEvaluationContext.findAnswerByPath(ADDRESS_POPULATE_PATH);
        if (!Answer.isValidAnswer(childLiveWithBeneficiaryAddressPopulateAnswer)) {
            return questionNodeInstance.isVisibility();
        }

        String isChildLiveWithBeneficiaryAddressSameAsPhysicalAddress = EasyVisaNode.normalizeAnswer(childLiveWithBeneficiaryAddressPopulateAnswer.getValue())
        if (isChildLiveWithBeneficiaryAddressSameAsPhysicalAddress == RelationshipTypeConstants.YES.value) {
            return true
        }
        return questionNodeInstance.isVisibility();
    }
}
