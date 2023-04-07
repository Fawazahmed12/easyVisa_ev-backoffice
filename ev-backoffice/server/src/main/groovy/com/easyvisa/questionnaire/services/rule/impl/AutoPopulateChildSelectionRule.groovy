package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section: Family Information
 * SubSection: Dependents (Children)
 * Question: (Q_1269) Select Child to Auto-Fill data (If child was listed in the previous subsection)
 * Notes:  If user select the child to auto-fill data, then auto populate the answers from
 *         'Family Information / Children Information' to 'Family Information / Dependents (Children)'
 */
@CompileStatic
@Component
class AutoPopulateChildSelectionRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoPopulateChildSelectionRule";

    //Given Name (First name)
    private static String SELECT_NAME_FIELD_PATH = 'Sec_familyInformation/SubSec_childrenInformation/Q_1251';
    private static String NONE_VALUE = "--None--"
    private static Integer NONE_VALUE_INDEX = -1


    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {

        if (this.evaluateAutoPopulateChildSelectionRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }

        return new Outcome(RelationshipTypeConstants.NO.value, false);
    }


    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext) {

    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {

        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Integer sourceRepeatingIndex = findSelectedChildRepeatingIndex(nodeRuleEvaluationContext)

        Integer targetRepeatingIndex = questionNodeInstance.repeatingIndex
        Map<String, String> fieldsToTransfer = this.constructFieldsToTransferData(sourceRepeatingIndex, targetRepeatingIndex)

        if (sourceRepeatingIndex == NONE_VALUE_INDEX) {
            // Reset Values
            answerService.populateAutoFieldsWithEmptyValues(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToTransfer)
        } else {
            // auto-populate from existing values
            answerService.populateAutoFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToTransfer)
        }

    }

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    private Integer findSelectedChildRepeatingIndex(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        Answer selectedChildNameAnswer = questionNodeInstance.getAnswer()
        //EV-3330 Allow adding new child.
        // User selected --None-- thus no index
        if (selectedChildNameAnswer.value == NONE_VALUE) {
            return NONE_VALUE_INDEX
        } else {
            List<Answer> childrenFirstNameAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.packageId,
                    nodeRuleEvaluationContext.applicantId, "${SELECT_NAME_FIELD_PATH}%");

            Answer childNameAnswer = childrenFirstNameAnswerList.find { element -> element.value.equals(selectedChildNameAnswer.value) }
            return childNameAnswer?.index;
        }


    }


    private Map<String, String> constructFieldsToTransferData(Integer sourceRepeatingIndex, Integer targetRepeatingIndex) {
        Map<String, String> fieldsToTransfer = new HashMap<>()

        if (sourceRepeatingIndex != NONE_VALUE_INDEX) {
            fieldsToTransfer['Sec_familyInformation/SubSec_childrenInformation/Q_1251/' + sourceRepeatingIndex] = 'Sec_familyInformation/SubSec_dependentsChildren/Q_1270/' + targetRepeatingIndex
            fieldsToTransfer['Sec_familyInformation/SubSec_childrenInformation/Q_1252/' + sourceRepeatingIndex] = 'Sec_familyInformation/SubSec_dependentsChildren/Q_1271/' + targetRepeatingIndex
            fieldsToTransfer['Sec_familyInformation/SubSec_childrenInformation/Q_1253/' + sourceRepeatingIndex] = 'Sec_familyInformation/SubSec_dependentsChildren/Q_1272/' + targetRepeatingIndex

            fieldsToTransfer['Sec_familyInformation/SubSec_childrenInformation/Q_1256/' + sourceRepeatingIndex] = 'Sec_familyInformation/SubSec_dependentsChildren/Q_1276/' + targetRepeatingIndex
            // Using NotExistingSubsection due to the logic used in mapping values between source and target
            // Source does not have the following two fields, but we still want to reset those
            // Reset Whats this dependent's relationship to you
            fieldsToTransfer['Sec_familyInformation/SubSec_NotExistingSubsection/Q_0000/' + sourceRepeatingIndex] = 'Sec_familyInformation/SubSec_dependentsChildren/Q_1287/' + targetRepeatingIndex
            // Reset Degree of dependency
            fieldsToTransfer['Sec_familyInformation/SubSec_NotExistingSubsection/Q_0001/' + sourceRepeatingIndex] = 'Sec_familyInformation/SubSec_dependentsChildren/Q_1288/' + targetRepeatingIndex
        } else {

            // Target fields to reset
            fieldsToTransfer['Sec_familyInformation/SubSec_dependentsChildren/Q_1270/' + targetRepeatingIndex] = ''
            fieldsToTransfer['Sec_familyInformation/SubSec_dependentsChildren/Q_1271/' + targetRepeatingIndex] = ''
            fieldsToTransfer['Sec_familyInformation/SubSec_dependentsChildren/Q_1272/' + targetRepeatingIndex] = ''

            fieldsToTransfer['Sec_familyInformation/SubSec_dependentsChildren/Q_1276/' + targetRepeatingIndex] = ''

            fieldsToTransfer['Sec_familyInformation/SubSec_dependentsChildren/Q_1287/' + targetRepeatingIndex] = ''
            fieldsToTransfer['Sec_familyInformation/SubSec_dependentsChildren/Q_1288/' + targetRepeatingIndex] = ''
        }

        return fieldsToTransfer;
    }


    private Boolean evaluateAutoPopulateChildSelectionRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return Answer.isValidAnswer(answer);
    }
}
