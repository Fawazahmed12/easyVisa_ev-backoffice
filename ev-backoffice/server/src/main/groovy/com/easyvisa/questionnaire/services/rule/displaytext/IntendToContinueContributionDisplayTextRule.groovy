package com.easyvisa.questionnaire.services.rule.displaytext

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDisplayTextRule
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
class IntendToContinueContributionDisplayTextRule implements IDisplayTextRule  {

    private static String RULE_NAME = 'IntendToContinueContributionDisplayTextRule'
    private static String TIME_PERIOD_PLACEHOLDER = '\\[weeks, months, years\\]'
    private static String LUMP_SUM_VALUE = "Lump Sum";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDisplayTextRule(RULE_NAME, this);
    }

    @Override
    String generateDisplayText(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance(); \
        String displayTextRuleParam = questionNodeInstance.getDisplayTextRuleParam();
        Answer selectedFrequentContributionAnswer = questionNodeInstance.getAnswer()
        String intendToThisContributionFieldPath = displayTextRuleParam + '/' + questionNodeInstance.getRepeatingIndex();
        Answer intendToThisContributionAnswer = ruleEvaluationContext.findAnswerByPath(intendToThisContributionFieldPath);
        String displayText = questionNodeInstance.getDisplayText();
        if (Answer.isValidAnswer(intendToThisContributionAnswer) && (intendToThisContributionAnswer.value!=LUMP_SUM_VALUE)) {
            String timePeriodValue = this.getTimePeriodDisplayText(intendToThisContributionAnswer);
            String updatedDisplayText = displayText.replaceAll(TIME_PERIOD_PLACEHOLDER, timePeriodValue);
            return updatedDisplayText;
        }
        return displayText;
    }

    private String getTimePeriodDisplayText(Answer intendToThisContributionAnswer){
        String intendToThisContributionAnswerValue = intendToThisContributionAnswer.value;
        String timePeriodValue = intendToThisContributionAnswerValue;
        switch (intendToThisContributionAnswerValue){
            case "Weekly":
                timePeriodValue = 'weeks';
                break;
            case "Monthly":
                timePeriodValue = 'months';
                break;
            case "Annually":
                timePeriodValue = 'years';
                break;
        }
        return timePeriodValue;
    }
}
