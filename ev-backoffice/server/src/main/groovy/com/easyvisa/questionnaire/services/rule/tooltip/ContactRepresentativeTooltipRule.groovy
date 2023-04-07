package com.easyvisa.questionnaire.services.rule.tooltip

import com.easyvisa.Package
import com.easyvisa.User
import com.easyvisa.questionnaire.answering.rule.IDynamicToolTipRule
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
 * Question: Have you had a petition of Alien Fiance(e) approved by the USCIS within the previous two years? Help
 * Tooltip: If you answered 'Yes' to this question, you must request a waiver of the International Marriage Broker Regulation Act (IMBRA) filing limitation:
 *
 * You are filing this petition on behalf of your fiancé(e), you have previously had a Form I-129F approved,
 * and less than two years have passed since the filing date of your previously approved petition.
 *
 * You should contact your representative *[Insert representative name] *to discuss this issue.
 *
 * Here is the office phone contact information for your representative:
 * [Insert representative's office number]
 */
@Component
class ContactRepresentativeTooltipRule implements IDynamicToolTipRule {

    private static String RULE_NAME = 'ContactRepresentativeTooltipRule'

    String contactRepTemplateString = '''If you answered 'Yes' to this question, you must request a waiver of the 
    International Marriage Broker Regulation Act (IMBRA) filing limitation:<br/><br/>You are filing this petition on behalf of your fiancé(e), you have previously 
    had a Form I-129F approved, and less than two years have passed since the filing date of your previously approved petition.<br/><br/>You should 
    contact your representative #representative-name to discuss this issue.<br/><br/>
    Here is the office phone contact information for your representative:<br/>#representative-office-number'''

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

    protected String generateTooltip(NodeRuleEvaluationContext ruleEvaluationContext, String template) {
        Package aPackage = Package.get(ruleEvaluationContext.packageId)
        User user = aPackage.attorney.getUser()
        String contactToolTipString = template.replaceAll('#representative-name', "'${user.username}'")
        String representativeOfficeNumber = aPackage.attorney.officePhone ?: '';
        contactToolTipString = contactToolTipString.replaceAll('#representative-office-number', "'${representativeOfficeNumber}'")
        return contactToolTipString
    }
}
