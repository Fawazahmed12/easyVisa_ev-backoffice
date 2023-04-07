package com.easyvisa.questionnaire.services.rule.impl


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *   Apply this rule to 'SubSection' called 'Prior Spouses'.
 *
 *   If user selected 'Divorced' OR 'Widowed' OR 'Marriage Annulled' for the question 'What is your current marital status?',
 *   then generate 'Prior Spouses' subsection below.
 *
 *   The 'Prior Spouses' susbsection will also get generated  after the 'Current Spouse' subsection if a currently married user answers  'Yes' to the question:
 *   'Where you married to anyone prior to this person?'.
 *
 *   This rule will check the  answers to the following questions from 'Marital Status' subsection
 *    a.   What is your current marital status? -> 'Divorced' OR 'Widowed' OR 'Marriage Annulled'
 *    b.   Where you married to anyone prior to this person? -> 'Yes'
 * This rule will generate two labels 'yes' and 'no'. If one of the above conditions are true then the result is 'yes'.
 */

@Component
class PriorSpousesApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "PriorSpousesApplicableRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Boolean canShowPriorSpousesSubSection = this.evaluatePriorSpousesApplicableRule(nodeRuleEvaluationContext);
        return canShowPriorSpousesSubSection;
    }

    private Boolean evaluatePriorSpousesApplicableRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = ruleParam.split(",");
        String maritalStatusFieldPath = ruleParams[0];
        String previouslyMarriedFieldPath = ruleParams[1];
        List<Answer> maritalStatusAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), maritalStatusFieldPath);
        if (!maritalStatusAnswerList.isEmpty()) {
            Answer maritalStatusAnswer = maritalStatusAnswerList[0];
            String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue());
            if (maritalStatusAnswerValue == RelationshipTypeConstants.DIVORCED.value ||
                    maritalStatusAnswerValue == RelationshipTypeConstants.WIDOWED.value ||
                    maritalStatusAnswerValue == RelationshipTypeConstants.MARRIAGE_ANULLED.value) {
                return true;
            }
        }

        List<Answer> wereYouPreviouslyMarriedList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), previouslyMarriedFieldPath);
        if (!wereYouPreviouslyMarriedList.isEmpty()) {
            Answer wereYouPreviouslyMarriedAnswer = wereYouPreviouslyMarriedList[0];
            String wereYouPreviouslyMarriedAnswerValue = EasyVisaNode.normalizeAnswer(wereYouPreviouslyMarriedAnswer.getValue());
            if (wereYouPreviouslyMarriedAnswerValue == RelationshipTypeConstants.YES.value) {
                return true;
            }
        }

        return false;
    }
}
