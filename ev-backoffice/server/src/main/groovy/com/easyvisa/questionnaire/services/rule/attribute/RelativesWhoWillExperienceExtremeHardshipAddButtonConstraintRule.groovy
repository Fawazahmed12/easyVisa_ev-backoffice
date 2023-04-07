package com.easyvisa.questionnaire.services.rule.attribute

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.util.DateUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate
import java.time.temporal.ChronoUnit



/**
 * Section: Relatives Who Will Experience Extreme Hardship if You Are Inadmissible to the United States
 * RepeatingQuestionGroup: (RQG_extremeHardshipForRelatives2) 601A - relativesWhoWillExperienceExtremeHardshipIfYouAreInadmissibleToTheUnitedStates
 * Form: I-601A
 * Notes: This question does NOT appear if the user has already previously answered 'Yes' to this question
 *        (because ONLY 2 iterations of people are allowed in this 601A form).
 *        If user answered 'No', then when he clicks the 'Next' button, the Questionnaire goes to Section ' Statement of Applicant'.
 * */

@Component
class RelativesWhoWillExperienceExtremeHardshipAddButtonConstraintRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'RelativesWhoWillExperienceExtremeHardshipAddButtonConstraintRule';

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;


    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Map attributes = repeatingQuestionGroupNodeInstance.getAttributes();
        attributes[TemplateOptionAttributes.ADDREPEATINGBUTTON.getValue()] = false;
        attributes[TemplateOptionAttributes.REMOVEREPEATINGBUTTON.getValue()] = false;
    }
}
