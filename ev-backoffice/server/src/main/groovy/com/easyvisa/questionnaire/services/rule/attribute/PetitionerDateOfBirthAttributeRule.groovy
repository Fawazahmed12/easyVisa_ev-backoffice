package com.easyvisa.questionnaire.services.rule.attribute


import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.util.DateUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate

/**
 * Section: Birth Information
 * SubSection: Birth Information
 * Question: (Q_88) Date of Birth
 *
 * Note: This rule will calculate and set the dob-calender min and max date values
 *        Maximum: 18 years from today
 *        Minimum: 120 years from Maximum date..
 *        The reason is that sponsors (Petitioners) must be at least 18 years old and only one person in history
 *        has lived to be more than 120 years old!
 *
 * **/
@Component
class PetitionerDateOfBirthAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'PetitionerDateOfBirthAttributeRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Map attributes = questionNodeInstance.getAttributes();
        LocalDate today = ruleEvaluationContext.currentDate;
        LocalDate maximumDate = today.minusYears(18);
        LocalDate minimumDate = today.minusYears(120);
        attributes.put(TemplateOptionAttributes.MINIMUMDATE.getValue(), DateUtil.fromDate(minimumDate));
        attributes.put(TemplateOptionAttributes.MAXIMUMDATE.getValue(), DateUtil.fromDate(maximumDate));
    }
}
