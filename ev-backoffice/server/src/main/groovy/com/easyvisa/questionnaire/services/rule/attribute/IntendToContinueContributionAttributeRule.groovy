package com.easyvisa.questionnaire.services.rule.attribute

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section: Support & Contributions
 * SubSection: Nature of Contributions
 * Question:
 *      (Q_1408) For how many [weeks, months, years] do you intend to continue making this contribution?
 *
 * Notes:  The Time period descriptor (in rectangular brackets [] in this question) is a variable and comes
 * from the radio button answer to the  previous question
 *      'For how long do you intend to continue making this contribution?'
 *  insert either 'Weeks' or 'Months' or 'Years'
 *  If they chose 'Lump Sum' answer in the previous question
 *      'How frequently do you intend to make this contribution?',
 *  then insert '1' into this numeric field
 */
@Component
class IntendToContinueContributionAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'IntendToContinueContributionAttributeRule'
    private static String LUMP_SUM_VALUE = "Lump Sum";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String attributeRuleParam = questionNodeInstance.getAttributeRuleParam();
        Answer selectedFrequentContributionAnswer = questionNodeInstance.getAnswer()
        String intendToThisContributionFieldPath = attributeRuleParam + '/' + selectedFrequentContributionAnswer.getIndex();
        Answer intendToThisContributionAnswer = ruleEvaluationContext.findAnswerByPath(intendToThisContributionFieldPath);
        if (Answer.isValidAnswer(intendToThisContributionAnswer) && StringUtils.equals(intendToThisContributionAnswer.value, LUMP_SUM_VALUE)) {
            Map attributes = questionNodeInstance.getAttributes();
            attributes.put(TemplateOptionAttributes.DISABLED.getValue(), true);
        }
    }
}
