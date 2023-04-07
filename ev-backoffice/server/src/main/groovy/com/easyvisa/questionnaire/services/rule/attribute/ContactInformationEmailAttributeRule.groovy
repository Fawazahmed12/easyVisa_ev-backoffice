package com.easyvisa.questionnaire.services.rule.attribute

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class ContactInformationEmailAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'ContactInformationEmailAttributeRule'
    private static String NONE = 'None'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String contactInformationAvailabilityFieldPath = questionNodeInstance.getAttributeRuleParam();
        Answer contactInformationAvailabilityAnswer = ruleEvaluationContext.findAnswerByPath(contactInformationAvailabilityFieldPath);
        if (Answer.isValidAnswer(contactInformationAvailabilityAnswer) && contactInformationAvailabilityAnswer.value == 'true') {
            Map attributes = questionNodeInstance.getAttributes();
            attributes.put(TemplateOptionAttributes.DISABLED.getValue(), true);
            questionNodeInstance.setContextualClue(NONE);
        }
    }
}
