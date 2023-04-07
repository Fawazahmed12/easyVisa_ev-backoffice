package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Relatives Who Will Experience Extreme Hardship if You Are Inadmissible to the United States
 * Question: (Q_6023) Do you have another relative (only a spouse or parent and they MUST be either a U.S. citizen or  LPR (Lawful Permanent Resident)) who would experience extreme hardship if you were refused admission to the United States?
 * Form: I-601A
 * Notes: This question does NOT appear if the user has already previously answered 'Yes' to this question
 *        (because ONLY 2 iterations of people are allowed in this 601A form).
 *        If user answered 'No', then when he clicks the 'Next' button, the Questionnaire goes to Section 'Statement of Applicant'.
 *
 * if user selected 'Yes', then repeat the questions in this section (EXCEPT FOR THE FIRST QUESTION)
 * for ONLY ONE additional person.
 * The location of where these responses go in the USCIS form is indicated in the Form Notes column for each
 * of the repeated questions.
 * */

@Component
class AnotherRelativeInForm601AWouldExperienceExtremeHardshipApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "AnotherRelativeInForm601AWouldExperienceExtremeHardshipApplicableRule"
    private static String REPEATING_GROUP_ID = "RQG_extremeHardshipForRelatives2"
    private static Integer REPEATING_GROUP_INDEX = 1;

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    AnswerService answerService


    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.getValue(), true);
    }


    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        Integer repeatingIndex = answer.getIndex();
        if (repeatingIndex != 0) {
            questionNodeInstance.setVisibility(false);
        }
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer anotherRelativeExperiencedExtremeHardshipAnswer = questionNodeInstance.getAnswer();
        String anotherRelativeExperiencedExtremeHardshipAnswerValue = EasyVisaNode.normalizeAnswer(anotherRelativeExperiencedExtremeHardshipAnswer.getValue())
        if (anotherRelativeExperiencedExtremeHardshipAnswerValue == RelationshipTypeConstants.YES.value) {
            this.autoPopulateFromExtremeHardshipforRelativeFields(ruleEvaluationContext);
            this.autoPopulateFromOtherPeopleWithTiesToUSFields(ruleEvaluationContext);
        } else if (anotherRelativeExperiencedExtremeHardshipAnswerValue == RelationshipTypeConstants.NO.value) {
            this.removeSuccessiveIterationsFromExtremeHardshipforRelativeFields(ruleEvaluationContext);
            this.removeOtherPeopleWithTiesToUSFields(ruleEvaluationContext);
        }
    }


    private void autoPopulateFromExtremeHardshipforRelativeFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        answerService.addRepeatingGroupInstance(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId,
                answer.sectionId, answer.subsectionId, REPEATING_GROUP_ID, nodeRuleEvaluationContext.currentDate);
    }


    void autoPopulateFromOtherPeopleWithTiesToUSFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        String sectionId = 'Sec_otherPeopleWithTiesToUS';
        String subsectionId = 'SubSec_otherPeopleWithTiesToUS';
        String repeatingGroupId = 'RQG_otherPeopleWithTiesToUS';
        // Create a new Default Set of answers
        this.answerService.addDefaultRepeatingGroupIfRequired(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId,
                sectionId, subsectionId, repeatingGroupId, nodeRuleEvaluationContext.currentDate);
    }


    private void removeSuccessiveIterationsFromExtremeHardshipforRelativeFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        answerService.removeRepeatingGroupInstance(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId,
                answer.sectionId, answer.subsectionId, REPEATING_GROUP_ID, REPEATING_GROUP_INDEX,
                nodeRuleEvaluationContext.currentDate);
    }

    void removeOtherPeopleWithTiesToUSFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        List<String> fieldsToRemove = this.getOtherPeopleWithTiesToUSFields();
        this.answerService.removeAutoFillFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToRemove);
    }

    private List<String> getOtherPeopleWithTiesToUSFields() {
        List<String> fieldsToRemove = [
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3701',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3711',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3712',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3713',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3714',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3721',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3722',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3723',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3724',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3725',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3726',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3727',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3728',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3729',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3730',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3741',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3742',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3751',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3752',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3753',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3754',
                'Sec_otherRelativesToBeConsidered/SubSec_otherRelativesToBeConsidered/Q_3755',

                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3801',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3802',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3803',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3811',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3812',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3813',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3814',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3815',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3816',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3817',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3818',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3819',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3820',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3831',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3832',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3841',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3842',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3843',
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3844'
        ];
        return fieldsToRemove;
    }
}
