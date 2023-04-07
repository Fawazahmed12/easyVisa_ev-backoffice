package com.easyvisa.questionnaire.services.rule.displaytext

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDisplayTextRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section: Address History
 * SubSection: Previous Physical Addresses
 * Question:
 *      (Q_2051) Were any of those previous addresses (since [date of green card gets inserted here]) located outside of the United States?
 *
 * Notes:
 */

@Component
class LocatedOutsideOfUSDisplayTextRule implements IDisplayTextRule {

    private static String RULE_NAME = 'LocatedOutsideOfUSDisplayTextRule'
    private static String CONDT_PERMANENT_RESIDENCE_PATH = "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2028"
    private static String TEMPLATE_PLACEHOLDER = "\\[date of green card gets inserted here\\]";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDisplayTextRule(RULE_NAME, this);
    }

    @Override
    String generateDisplayText(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Integer repeatingIndex = questionNodeInstance.getRepeatingIndex(); //it is zero based value

        String condtPermanentResidenceFieldPath = CONDT_PERMANENT_RESIDENCE_PATH;
        Answer condtPermanentResidenceAnswer = ruleEvaluationContext.findAnswerByPath(condtPermanentResidenceFieldPath);

        String displayText = questionNodeInstance.getDisplayText();
        if (Answer.isValidAnswer(condtPermanentResidenceAnswer)) {
            String condtPermanentResidenceValue = condtPermanentResidenceAnswer.getValue();
            String updatedDisplayText = displayText.replaceAll(TEMPLATE_PLACEHOLDER, condtPermanentResidenceValue);
            return updatedDisplayText;
        }
        return displayText;
    }
}
