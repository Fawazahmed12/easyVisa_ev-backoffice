package com.easyvisa.questionnaire.services.rule.tooltip


import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule fetches the Applicant's attorney detail to show a appropriate tooltip when the following condition is met
 *
 * Section: Intro Questions
 * SubSection: Previous Immigration (Visa) Petitions You Filed for Another Person
 * Question: Have you filed 2 or more petitions for an Alien Fiance(e) (other than for your spouse) in your lifetime?
 * Tooltip: If you answered 'Yes' to this question, you must request a waiver of the International Marriage Broker Regulation Act (IMBRA) filing limitation:
 *
 * You are filing this petition on behalf of your fiancé(e) and you previously filed Form I-129Fs on behalf of two or more fiancé(e) beneficiaries.
 *
 * You should contact your representative [Insert representative name] to discuss this issue.
 *
 * Here is the office phone contact information for your representative:
 * [Insert representative's office number]
 */
@Component
class ContactRepresentativeOtherTooltipRule extends ContactRepresentativeTooltipRule {

    private static String RULE_NAME = 'ContactRepresentativeOtherTooltipRule'

    String contactRepTemplateString = '''If you answered 'Yes' to this question, you must request a waiver of the International Marriage Broker Regulation 
    Act (IMBRA) filing limitation:<br/><br/>You are filing this petition on behalf of your fiancé(e) and you previously filed Form I-129Fs 
    on behalf of two or more fiancé(e) beneficiaries.<br/><br/>You should contact your representative #representative-name to discuss this issue.
    <br/><br/>Here is the office phone contact information for your representative:<br/>#representative-office-number'''

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicTooltiptRule(RULE_NAME, this);
    }

    @Override
    String generateDynamicTooltip(NodeRuleEvaluationContext ruleEvaluationContext) {
        return generateTooltip(ruleEvaluationContext, contactRepTemplateString)
    }
}
