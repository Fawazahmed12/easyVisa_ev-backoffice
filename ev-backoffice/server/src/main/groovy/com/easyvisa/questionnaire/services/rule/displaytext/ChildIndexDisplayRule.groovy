package com.easyvisa.questionnaire.services.rule.displaytext

import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDisplayTextRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section: Family Information
 * SubSection: Children Information
 * Question: Date of Birth of Child #iterationIndex (Children under 18 only)
 * Notes:  For each 'Add Another' iteration, increment of the Child number in the question:
 *         'Date of Birth of Child 1 (Children under 18 only)' to
 *         'Date of Birth of Child 2 (Children under 18 only)', etc.
 */
@Component
class ChildIndexDisplayRule implements IDisplayTextRule {
    private static String RULE_NAME = 'ChildIndexDisplayRule'
    private static String CHILD_INDEX_PLACEHOLDER = '#iterationIndex'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDisplayTextRule(RULE_NAME, this);
    }

    @Override
    String generateDisplayText(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String displayText = questionNodeInstance.getDisplayText();
        Integer repeatingIndex = questionNodeInstance.getRepeatingIndex(); //it is zero based value
        Integer childCount = ++repeatingIndex;
        String updatedDisplayText = displayText.replaceAll(CHILD_INDEX_PLACEHOLDER, "${childCount}")
        return updatedDisplayText
    }
}
