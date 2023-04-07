package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.Question
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.repositories.QuestionDAO
import com.easyvisa.questionnaire.util.DateUtil
import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate

/**
 * Section: Address History
 * SubSection: Previous Address of Beneficiary
 * ApplicantType: Beneficiary
 * Question: (Q_2028) What is the date that your Conditional Permanent Residence began (which is the date at the bottom of your Conditional Permanent Resident Card called 'Residence Since')?
 *           (Q_2029) Have you resided at any other address since you became a permanent resident?
 *
 *
 * Notes: This rule determines if the Previous Address History Repeating Group is to be shown or not
 *          In addition, this also sets the value of Q_2029 depending on Current Address Movein and Date of PR.
 *          It should be 2 years or more.
 *          In case its less than 2 Years, set the value of Q_2029 to Yes and show Previous Address History Fields
 */


// Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2012 - When did you move into this address
@CompileStatic
@Component
class HaveYouResidedAtOtherAddressRule extends BaseComputeRule {

    private static String RULE_NAME = "HaveYouResidedAtOtherAddressRule"
    private static String RELATIONSHIP_NAME = 'has'

    // Hardcoding path because this Rule may be called from within other Rules
    private static String currentMoveInDatePath ="Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2012"
    private static String PRDatePath = "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2028"


    @Autowired
    RuleComponentRegistry ruleComponentRegistry


    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        Answer otherResidence = questionNodeInstance.getAnswer()

        boolean isResidedAtOtherAddress = this.isResidedAtOtherAddressSincePR(ruleEvaluationContext)
        otherResidence.value = isResidedAtOtherAddress ? RelationshipTypeConstants.YES.value : RelationshipTypeConstants.NO.value
        return new Outcome(otherResidence.value, true)
    }

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    boolean isResidedAtOtherAddressSincePR(NodeRuleEvaluationContext ruleEvaluationContext) {

        // Need to hardcode answer path since this rule is called from other rules (different question/section context) as well
        Answer currentMoveInDateAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.getPackageId(),
                ruleEvaluationContext.getApplicantId(), currentMoveInDatePath)

        Answer PRDateAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.getPackageId(),
                ruleEvaluationContext.getApplicantId(), PRDatePath)

        if (Answer.isValidAnswer(currentMoveInDateAnswer) && Answer.isValidAnswer(PRDateAnswer)) {

            // convert both fields to date and compare
            // if currentMoveInDateAnswer <= PRDateAnswer then return false else return true
            // currentMoveInDate should be before or equal to PRDateAnswer
            // otherwise return true ie. beneficiary has resided at other addresses since PR Date
            // and needs to fill previous address history as well

            // Normalization happens in DateUtil.localDate
            LocalDate currentMoveInDateAsDate = DateUtil.localDate(currentMoveInDateAnswer.getValue())
            LocalDate PRDateAsDate = DateUtil.localDate(PRDateAnswer.getValue())

            if (currentMoveInDateAsDate <= PRDateAsDate) {
                // all good, no additional history required - return false
                return false
            } else {
                // additional addresses required - return true
                return true
            }
        }
        return false
    }


    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        questionNodeInstance.setVisibility(false)
    }
}
