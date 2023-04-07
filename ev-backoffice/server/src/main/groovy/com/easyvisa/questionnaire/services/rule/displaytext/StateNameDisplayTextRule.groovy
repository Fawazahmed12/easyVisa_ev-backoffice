package com.easyvisa.questionnaire.services.rule.displaytext


import com.easyvisa.enums.Country
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDisplayTextRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

//In what Province/Territory/Prefecture/Parish did your marriage take place?
//Province/Territory/Prefecture/Parish
//Province
//In what province did this marriage take place?
//What is this spouse's Province/Territory/Prefecture/Parish?


@Component
class StateNameDisplayTextRule implements IDisplayTextRule {

    private static String RULE_NAME = 'StateNameDisplayTextRule'

    private static String PROVINCE_PLACEHOLDER = "Province\\/Territory\\/Prefecture\\/Parish";
    private static String STATE_VALUE = "State";

    private List<String> stateEligibleCountries = [
            Country.MEXICO.displayName,
            Country.GERMANY.displayName,
            Country.MYANMAR.displayName,
            Country.AUSTRALIA.displayName,
            Country.INDIA.displayName
    ];

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDisplayTextRule(RULE_NAME, this);
    }

    @Override
    String generateDisplayText(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String countryFieldPath = questionNodeInstance.getDisplayTextRuleParam();

        String displayText = questionNodeInstance.getDisplayText();
        String updatedDisplayText = displayText;

        Answer selectedCountryAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, countryFieldPath)
        if (Answer.isValidAnswer(selectedCountryAnswer) && stateEligibleCountries.contains(selectedCountryAnswer.value)) {
            updatedDisplayText = updatedDisplayText.replaceAll(PROVINCE_PLACEHOLDER, STATE_VALUE);
        }
        return updatedDisplayText
    }
}
