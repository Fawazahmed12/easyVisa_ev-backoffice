package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.Question
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.repositories.QuestionDAO
import grails.compiler.GrailsCompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Family Information
 * SubSection: Marital Status
 * ApplicantType: Petitioner / Beneficiary
 * Question: (Q_1202) How many times have you been married?
 *           (Q_2779) How many times have you been married?
 *
 * RuleParams:  (Sec_familyInformation/SubSec_maritalStatus/Q_1204) or
 *              (Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781)
 *              What is your current marital status?
 *
 *              (Sec_familyInformation/SubSec_priorSpouses/RQG_priorSpouses) or
 *              (Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/RQG_priorSpousesForBeneficiary)
 *              'PriorSpouses' repeatingGroup Info
 *
 * Notes: This question does not visible to the user. But it gets Printed in the Form-130 (Petitioner & Beneficiary) and Form-485 (Beneficiary).
 *        We have to get the answer value dynamically,
 *        using the sum of ‘Current Spouse’ plus the ‘Prior Spouses’ iterations
 */

@Component
@GrailsCompileStatic
class MarriedTimesVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = "MarriedTimesVisibilityConstraintRule"
    private static String RELATIONSHIP_NAME = 'has'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @Autowired
    QuestionDAO questionDAO

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        return new Outcome(RELATIONSHIP_NAME, true)
    }

    @Override
    String determineAnswer(NodeRuleEvaluationContext ruleEvaluationContext, Answer answer, Outcome outcome) {
        return this.getNumberOfTimeMarriedValue(ruleEvaluationContext)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        questionNodeInstance.setVisibility(false)
    }

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    private int getNumberOfTimeMarriedValue(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = questionNodeInstance.getDefinitionNode().getRuleParam()
        String[] ruleParams = ruleParam.split(",")
        String maritalStatusFieldPath = ruleParams[0]
        String prirSpouseRepeatingGroupPath = ruleParams[1]

        int noOfTimesMarried = 0
        Answer maritalStatusAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.getPackageId(),
                ruleEvaluationContext.getApplicantId(), maritalStatusFieldPath)
        if (Answer.isValidAnswer(maritalStatusAnswer)) {
            String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue())
            List<String> validCurrentSpouseMaritalStatusList = [
                    RelationshipTypeConstants.MARRIED.value,
                    RelationshipTypeConstants.LEGALLY_SEPERATED.value
            ]
            if (validCurrentSpouseMaritalStatusList.contains(maritalStatusAnswerValue)) {
                noOfTimesMarried++
            }
        }

        String[] prirSpouseRepeatingGroupPaths = prirSpouseRepeatingGroupPath.split("/")
        String sectionId = prirSpouseRepeatingGroupPaths[0]
        String subsectionId = prirSpouseRepeatingGroupPaths[1]
        String repeatingGroupId = prirSpouseRepeatingGroupPaths[2]
        String questVersion = ruleEvaluationContext.getQuestionnaireVersion()

        List<Question> questionList = questionDAO.findQuestionsOfRepeatingGroupByEasyVisaId(questVersion, repeatingGroupId)
        String questionId = questionList[0].id

        // Check if it has a instance of repeatting group question
        List<Answer> priorSpouseIterations = Answer.findAllByPackageIdAndApplicantIdAndSectionIdAndSubsectionIdAndQuestionId(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, sectionId, subsectionId, questionId)
        noOfTimesMarried += priorSpouseIterations.size()
        return noOfTimesMarried
    }
}
