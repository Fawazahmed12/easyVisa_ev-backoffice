package com.easyvisa.questionnaire.services.rule.inputsource

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicInputDatasourceRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class ContactInformationCountryListGenerator implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'ContactInformationCountryListGenerator'
    private static String NOT_APPLICABLE = "Not Applicable";
    private static String COUNTRYLIST_INPUTTYPE_SOURCE = "countryListDropdown";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    private QuestionnaireService questionnaireService

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this);
    }


    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        InputSourceType countryListInputSourceType = questionnaireService.getInputSourceType(COUNTRYLIST_INPUTTYPE_SOURCE, questionNodeInstance.questVersion, questionNodeInstance.displayTextLanguage);
        String contactInformationAvailabilityFieldPath = questionNodeInstance.getInputTypeSourceRuleParam();
        Answer contactInformationAvailabilityAnswer = ruleEvaluationContext.findAnswerByPath(contactInformationAvailabilityFieldPath);
        if (!Answer.isValidAnswer(contactInformationAvailabilityAnswer)) {
            return countryListInputSourceType;
        }
        String contactInformationAvailabilityAnswerValue = contactInformationAvailabilityAnswer.getValue();
        if (contactInformationAvailabilityAnswerValue == 'true') {
            List<InputSourceType.ValueMap> values = [new InputSourceType.ValueMap(NOT_APPLICABLE)]
            InputSourceType inputSourceType = new InputSourceType(RULE_NAME, values);
            return this.questionnaireService.getTranslatedInputSourceType(inputSourceType, questionNodeInstance.questVersion,
                    questionNodeInstance.displayTextLanguage);
        }
        return countryListInputSourceType;
    }
}
