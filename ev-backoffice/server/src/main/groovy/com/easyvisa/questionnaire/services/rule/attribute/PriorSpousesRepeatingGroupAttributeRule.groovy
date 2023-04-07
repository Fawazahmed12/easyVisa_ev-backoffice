package com.easyvisa.questionnaire.services.rule.attribute

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 *   Apply this rule to 'RepeatingGroup' called 'Prior Spouses'.
 *
 *   If user selected 'Divorced' OR 'Widowed' OR 'Marriage Annulled' for the question 'What is your current marital status?',
 *   then don't allow the user to delete the entire 'Prior Spouses' iteration.
 *
 *   (i.e) Hide the remove button, If there is only one iteration.
 *   Therfore don't allow user to remove all the iterations... This rule will handle this condition..
 *
 *   The 'Prior Spouses' susbsection will also get generated  after the 'Current Spouse' subsection if a currently married user answers  'Yes' to the question:
 *   'Where you married to anyone prior to this person?'.
 *
 *   This rule will check the  answers to the following questions from 'Marital Status' subsection
 *    a.   What is your current marital status? -> 'Divorced' OR 'Widowed' OR 'Marriage Annulled'
 *    b.   Where you married to anyone prior to this person? -> 'Yes'
 */

@Component
class PriorSpousesRepeatingGroupAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'PriorSpousesRepeatingGroupAttributeRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this)
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Map attributes = repeatingQuestionGroupNodeInstance.getAttributes()
        String attributeRuleParam = repeatingQuestionGroupNodeInstance.getAttributeRuleParam()

        String[] ruleParams = attributeRuleParam.split(",");
        String maritalStatusFieldPath = ruleParams[0];
        String previouslyMarriedFieldPath = ruleParams[1];

        Answer maritalStatusAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.getPackageId(),
                ruleEvaluationContext.getApplicantId(), maritalStatusFieldPath);
        if (Answer.isValidAnswer(maritalStatusAnswer)) {
            String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue());
            List<String> validPriorSpouseMaritalStatusList = [
                    RelationshipTypeConstants.DIVORCED.value,
                    RelationshipTypeConstants.WIDOWED.value,
                    RelationshipTypeConstants.MARRIAGE_ANULLED.value
            ]
            if (validPriorSpouseMaritalStatusList.contains(maritalStatusAnswerValue)) {
                Integer totalRepeatCount = repeatingQuestionGroupNodeInstance.getTotalRepeatCount();
                Integer answerIndex = repeatingQuestionGroupNodeInstance.getAnswerIndex(); //zero based value
                attributes[TemplateOptionAttributes.REMOVEREPEATINGBUTTON.getValue()] = (totalRepeatCount != 1);
            }
        }
    }
}
