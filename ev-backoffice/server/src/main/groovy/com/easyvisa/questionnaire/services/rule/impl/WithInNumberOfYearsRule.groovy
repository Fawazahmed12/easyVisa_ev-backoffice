package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.util.DateUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate

/**
 * This rule will check whether the given answer is less than the number of years,
 * which are passed via ruleParam.
 * This rule will generate two labels 'yes' and 'no'.
 * The rule will check if the answer for the given path(param) is less than the noOfYears(param)
 * then return "yes" as the outcome, otherwise return 'no' as outcome
 * This question's child nodes have two links one with 'yes' and the other as 'no'
 *
 *  Note: This rule is reused in multiple sections
 */

/**
 * We are applying this rule to the following scenarios
 *
 * Sec_employmentHistory(Employment History)-->SubSec_32(Previous Employer For Employed)-->Q_196(startDateOfPreviousEmployment)
 * If this date is more than 5 years before today, then mark (with the blue check mark) this question is complete.
 * Also, if this date is more than 5 years before today, then no further questions appear in this section.
 *
 * Sec_employmentHistory*Employment History)-->SubSec_33(Previous Employer For Unemployed)-->Q_199(endDateOfPreviousEmployment)
 * This question only appears if user answered 'Unemployed' to the question 'What is your current employment status?' in the 'Employment Status' subsection.
 * ALSO, if the date entered is MORE THAN 5 years from today, then NONE of the subsequent questions in this subsection appear AND the 'Add Another' button is dimmed out.
 *
 * Sec_employmentHistory(Employment History)-->SubSec_32(Previous Employer For Employed)
 * Here we are going to apply this rule to 'SubSection' called 'Previous Employer For Employed'.
 * This rule will check the following condtion.
 *    a.   What date did you begin working for this employer?
 *         This question belongs to 'Current Employer' subsection. If user entered date is  less
 *         than 5 years from today, then the 'Previous Employer For Employed' subsection
 *         appears below.This rule will generate two labels 'yes' and 'no'. If the above two
 *         condition is true then the result is 'yes'.
 *
 */


@Component()
class WithInNumberOfYearsRule extends BaseComputeRule {

    private static String RULE_NAME = "WithInNumberOfYears";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    public Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = ruleParam.split(",");
        String QUESTION_FIELD_PATH = ruleParams[0];
        int noOfYears = ruleParams[1] as Integer;
        if (this.evaluateWithInNumberOfYearsRule(ruleEvaluationContext, QUESTION_FIELD_PATH, noOfYears)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false);
    }


    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = ruleParam.split(",");
        String QUESTION_FIELD_PATH = ruleParams[0];
        int noOfYears = ruleParams[1] as Integer;
        return this.evaluateWithInNumberOfYearsRule(ruleEvaluationContext, QUESTION_FIELD_PATH, noOfYears);
    }


    Boolean evaluateWithInNumberOfYearsRule(NodeRuleEvaluationContext ruleEvaluationContext, String QUESTION_FIELD_PATH, int noOfYears) {
        List<Answer> answerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, QUESTION_FIELD_PATH);
        if (answerList.isEmpty()) {
            return false;
        }

        Answer answer = answerList[0];
        String answerValueAsString = DateUtil.normalizeEasyVisaDateFormat(answer.getValue());
        LocalDate answerValueAsDate = DateUtil.localDate(answerValueAsString);
        return DateUtil.isWithInNumberOfYears(answerValueAsDate, ruleEvaluationContext.currentDate, noOfYears)
    }
}
