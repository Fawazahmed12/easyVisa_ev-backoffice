package com.easyvisa.questionnaire.services.rule.answercompletionvalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerCompletionValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 *
 * This rule will check the answerValue as integer and checks the alphaNumericCharacter length
 * by using rule-param.
 *
 * This rule is common for the  following questions
 *  1. What is your USCIS ELIS Account Number?  (EXACTLY 12 numbers)
 *          ( Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_115)
 *
 *  2. What is your USCIS ELIS Account Number?  (EXACTLY 12 numbers)
 *          ( Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2411)
 *
 *  3. What is your USCIS ELIS Account Number?  (EXACTLY 12 numbers)
 *          ( Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2758)
 *
 *  4. What is your USCIS ELIS Account Number?  (EXACTLY 12 numbers)
 *          ( Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2798)
 */

@Component
class AlphaNumericAnswerCompletionRule implements IAnswerCompletionValidationRule {

    private static String RULE_NAME = 'AlphaNumericAnswerCompletionRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerCompletionValidationRule(RULE_NAME, this);
    }

    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        if(!Answer.isValidAnswer(answer)){
            return false;
        }
        String answerValue = answer.getValue();
        String numericCharacterLength = questionNodeInstance.getAnswerCompletionValidationRuleParam()
        return (answerValue.length() == numericCharacterLength.toInteger()) ? true : false;
    }
}
