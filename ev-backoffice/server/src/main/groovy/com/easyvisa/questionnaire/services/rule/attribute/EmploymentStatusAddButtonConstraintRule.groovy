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
 * Section: Employment History
 * SubSection: Employment Status
 * RepeatingQuestionGroup: (RQG_employmentStatus/RQG_employmentStatusForBeneficiary) Employer/Employment Status #iterationCount
 *
 * Notes:  This rule applies to the above repeating-question-group, this rule will show/hide the 'Add Another Button'
 * based on the following values. (i.e) If all the history dates is less than 5 years in the past from today,
 * then the 'Add Another Employment Status' button appears at the end of this subsection
 *
 * This rule also updates its warningText using currentIterationCount
 *
 *  EMPLOYMENT_STATUS=="UnEmployed"
 *      1. UNEMPLOYMENT_START_DATE
 *      2. UNEMPLOYMENT_END_DATE (If iteration is 0, then take currentDate, as it is visible only if iteration!=0)
 *
 *  EMPLOYMENT_STATUS=="Retired"
 *      1. RETIRED_START_DATE
 *      2. RETIRED_END_DATE (If iteration is 0, then take currentDate, as it is visible only if iteration!=0)
 *
 *  EMPLOYMENT_STATUS=="Employed"
 *      1. EMPLOYMENT_START_DATE
 *          (If iteration is 0, then take currentDate as EMPLOYMENT_END_DATE, because following questions are visible only if iteration!=0)
 *      2. STILL_WORKING_AT_THIS_EMPLOYER
 *      3. EMPLOYMENT_END_DATE  (If STILL_WORKING_AT_THIS_EMPLOYER is 'Yes', then take currentDate as value)
 *      4. EMPLOYMENT_END_DATE  (TO PRESENT) (If STILL_WORKING_AT_THIS_EMPLOYER is 'Yes', then take currentDate as value)
 */

@Component
class EmploymentStatusAddButtonConstraintRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'EmploymentStatusAddButtonConstraintRule'
    private static String DELETE_WARNING_TEXT = 'Are you sure that you want to delete <strong>iteration #iterationCount for this period of your employment status history?</strong>'
    private static String DELETE_WARNING_PLACEHOLDER = '#iterationCount'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();

        Integer totalRepeatCount = repeatingQuestionGroupNodeInstance.getTotalRepeatCount();
        Integer answerIndex = repeatingQuestionGroupNodeInstance.getAnswerIndex(); //zero based value
        Integer currentIterationValue = answerIndex + 1;
        Map attributes = repeatingQuestionGroupNodeInstance.getAttributes();
        attributes[TemplateOptionAttributes.ADDREPEATINGBUTTON.getValue()] = (currentIterationValue == totalRepeatCount) ? true : false;
        attributes[TemplateOptionAttributes.REMOVEREPEATINGBUTTON.getValue()] = (totalRepeatCount != 1);

        this.addRepeatingGroupWarningText(attributes, currentIterationValue);
    }


    private void addRepeatingGroupWarningText(Map attributes, Integer currentIterationValue) {
        String updatedRepeatingGroupWarningText = DELETE_WARNING_TEXT.replaceAll(DELETE_WARNING_PLACEHOLDER, "${currentIterationValue}")
        attributes[TemplateOptionAttributes.REPEATINGGROUP_DELETE_TEXT.getValue()] = updatedRepeatingGroupWarningText;
    }
}
