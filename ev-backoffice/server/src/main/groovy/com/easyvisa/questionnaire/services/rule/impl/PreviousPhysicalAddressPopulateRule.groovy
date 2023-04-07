package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**

 * Section: Address History
 * SubSection: Current Physical Address
 * Question: (Q_2012)When did you move into this address?
 * Applicant: Beneficiary
 *
 * 1. enter current address
 * 2. enter date of move in
 * 3. if move in date is less than the 5 years from today then, ask for the previous residence
 * 4. EV-3325 In case of #751, if Movein Date is after PR Date then show Previous Residence
 */

@Component
class PreviousPhysicalAddressPopulateRule extends BaseComputeRule {

    private static String RULE_NAME = "PreviousPhysicalAddressPopulateRule"
    private static Integer TOTAL_DURATION_IN_YEARS = 5

    @Autowired
    WithInNumberOfYearsRule withInNumberOfYearsRule

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    AnswerService answerService

    @Autowired
    private HaveYouResidedAtOtherAddressRule haveYouResidedAtOtherAddressRule


    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {

        // **** Evaluate Outcome should work as is since triggerFormAction will do the actual check
        // **** Evaluate Outcome will return true if an answer is added causing triggerFormAction to fire that will
        // **** perform the check
        /*if (hasFormI751(ruleEvaluationContext)) {
            // can not use evaluateOutcome of haveYouResidedAtOtherAddressRule in this case
            // since context is different
            boolean residededAtOtherAddress = haveYouResidedAtOtherAddressRule.isResidedAtOtherAddressSincePR(ruleEvaluationContext)
            return new Outcome(RelationshipTypeConstants.YES.value, residededAtOtherAddress)

        } else {
            QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
            Answer answer = questionNodeInstance.getAnswer()
            Answer residedAtOtherAddressAnswer = ruleEvaluationContext.findAnswerByPath(answer.getPath())
            return new Outcome(answer.getValue(), Answer.isValidAnswer(residedAtOtherAddressAnswer))
        }*/

        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        Answer residedAtOtherAddressAnswer = ruleEvaluationContext.findAnswerByPath(answer.getPath())
        return new Outcome(answer.getValue(), Answer.isValidAnswer(residedAtOtherAddressAnswer))


    }


    // If the answer to  'When did you move into this address?' is AFTER the (greater than or equal to) the date 'What is the date that your Conditional Permanent Residence began (which is the date at the bottom of your Conditional Permanent Resident Card called 'Residence Since')?',
    // THEN ask for the previous residence
    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext, Answer previousAnswer) {

        if (showDefaultRepeatingGroup(ruleEvaluationContext)) {
            this.addDependentDefaultRepeatingGroupIfRequired(ruleEvaluationContext)
        } else {
            this.removeDependentRepeatingGroup(ruleEvaluationContext)
        }

    }

    private boolean showDefaultRepeatingGroup(NodeRuleEvaluationContext ruleEvaluationContext) {
        // check if its 751 else check hasCurrentMoveInLessThan5Years
        if (hasFormI751(ruleEvaluationContext)) {
            // EV-3325: Check both dates in case of 751
            if (haveYouResidedAtOtherAddressRule.isResidedAtOtherAddressSincePR(ruleEvaluationContext)) {
                return true
            }
            return false
        }

        // If not 751
        if (this.hasCurrentMoveInLessThan5YearsFromTodyDate(ruleEvaluationContext)) {
            return true
        }
        return false

    }


    private boolean hasFormI751(NodeRuleEvaluationContext ruleEvaluationContext) {
        if (packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I751)) {
            return true
        }
        return false


    }

    /**
     *
     * If the move-in date of the current address is less than 5 years from today,
     * then the 'Previous Physical Addresses' subsection gets generated (and the subsection 'Current Mailing Address' slides down)
     * and that new subsection 'Previous Physical Address(es)' appears below this section. Otherwise, proceed to subsection 'Current Mailing Address'
     */
    private Boolean hasCurrentMoveInLessThan5YearsFromTodyDate(NodeRuleEvaluationContext ruleEvaluationContext) {
        //When did you move into this address?
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return withInNumberOfYearsRule.evaluateWithInNumberOfYearsRule(ruleEvaluationContext, answer.getPath(), TOTAL_DURATION_IN_YEARS)
    }


    private addDependentDefaultRepeatingGroupIfRequired(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String[] ruleParams = questionNodeInstance.getRuleParam().split(",")
        String RQG_PREVIOUS_PHYSICAL_ADDRESS = ruleParams[0]
        String FIRST_RESIDEDAT_OTHER_ADDRESS_COUNTRY_PATH = ruleParams[1]

        // Check if it has a instance of repeatting group question
        Answer previousAddressMoveIntoAnswer = nodeRuleEvaluationContext.findAnswerByPath(FIRST_RESIDEDAT_OTHER_ADDRESS_COUNTRY_PATH)
        if (previousAddressMoveIntoAnswer) {
            return
        }

        String[] fieldPaths = FIRST_RESIDEDAT_OTHER_ADDRESS_COUNTRY_PATH.split("/")
        String sectionId = fieldPaths[0]
        String subsectionId = fieldPaths[1]
        answerService.addRepeatingGroupInstance(nodeRuleEvaluationContext.packageId,
                nodeRuleEvaluationContext.applicantId, sectionId, subsectionId,
                RQG_PREVIOUS_PHYSICAL_ADDRESS, nodeRuleEvaluationContext.currentDate)
    }


    private removeDependentRepeatingGroup(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        // dont use 'repeatIterationCount' value in 'removeRepeatingGroupInstance' method,
        // as 'removeRepeatingGroupInstance()' decrement the answer index greater than the removed one
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String[] ruleParams = questionNodeInstance.getRuleParam().split(",")
        String RQG_PREVIOUS_PHYSICAL_ADDRESS = ruleParams[0]
        String FIRST_RESIDEDAT_OTHER_ADDRESS_COUNTRY_PATH = ruleParams[1]

        String[] fieldPaths = FIRST_RESIDEDAT_OTHER_ADDRESS_COUNTRY_PATH.split("/")
        String sectionId = fieldPaths[0]
        String subsectionId = fieldPaths[1]
        String questionId = fieldPaths[2]
        int thresholdIterationCount = fieldPaths[3].toInteger()
        List<Answer> answerList = nodeRuleEvaluationContext.findAnswerListByPathILike([sectionId, subsectionId, questionId].join('/'))
        int maxIterationAnswerCount = (answerList.size() == 0) ? answerList.size() : (answerList.size() - 1)
        (thresholdIterationCount..maxIterationAnswerCount).each {
            this.answerService.removeRepeatingGroupInstance(nodeRuleEvaluationContext.packageId,
                    nodeRuleEvaluationContext.applicantId, sectionId, subsectionId,
                    RQG_PREVIOUS_PHYSICAL_ADDRESS, thresholdIterationCount, nodeRuleEvaluationContext.currentDate)
        }
    }
}
