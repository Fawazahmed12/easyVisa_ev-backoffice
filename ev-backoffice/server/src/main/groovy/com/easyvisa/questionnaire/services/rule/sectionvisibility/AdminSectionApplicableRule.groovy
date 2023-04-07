package com.easyvisa.questionnaire.services.rule.sectionvisibility


import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.ISectionVisibilityRule
import com.easyvisa.questionnaire.answering.rule.SectionVisibilityRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


@Component
class AdminSectionApplicableRule implements ISectionVisibilityRule {

    private static String RULE_NAME = 'AdminSectionApplicableRule'


    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerSectionVisibilityRules(RULE_NAME, this);
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(SectionVisibilityRuleEvaluationContext ruleEvaluationContext) {
        SectionNodeInstance sectionNodeInstance = ruleEvaluationContext.getSectionNodeInstance();
        sectionNodeInstance.setVisibility(false);
    }
}
