package com.easyvisa.questionnaire.services.rule.impl


import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Address History
 * SubSection: Current Physical Address
 * Question: (Q_42) In what country is your current physical address?
 * Notes:  Here we need to have 2 rules for the same question...
 *         So introduced new rule-type called composite rule...
 *         Rule1: EmployerCountrySelectionRule
 *         Rule2: AutoSyncCurrentPhysicalAddressRule
 *
 *         Here for the evaluation part we have used 'EmployerCountrySelectionRule'.
 *         And for triggering-action we have used 'AutoSyncCurrentPhysicalAddressRule'
 *
 *         In evaluation, if the 'successfulMatch' value from outcome is true,
 *         then system will call trigger-action method..
 *         Here always need to call triggering-action part,
 *         therefore we are setting outcome value as true..
 */


@Component
class AutoSyncPhysicalAddressCountrySelectionCompositeRule extends AutoSyncCurrentPhysicalAddressRule {

    private static String RULE_NAME = "AutoSyncPhysicalAddressCountrySelectionCompositeRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    EmployerCountrySelectionRule employerCountrySelectionRule;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Outcome employerCountryOutcome = this.employerCountrySelectionRule.evaluateOutcome(nodeRuleEvaluationContext);
        employerCountryOutcome.setSuccessfulMatch(true);
        return employerCountryOutcome;
    }


    @Override
    Boolean evaluateAutoPopulateCurrentMailingAddressRule(String ruleParam, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Boolean result = super.evaluateAutoPopulateCurrentMailingAddressRule(ruleParam, nodeRuleEvaluationContext);
        if (!result) {
            return result;
        }

        String[] ruleParams = ruleParam.split(";");
        if(ruleParams.size()==3){
            Outcome employerCountryOutcome = this.employerCountrySelectionRule.evaluateOutcome(nodeRuleEvaluationContext);
            return (employerCountryOutcome.relationshipType == ruleParams[2]);
        }
        return result;
    }
}
