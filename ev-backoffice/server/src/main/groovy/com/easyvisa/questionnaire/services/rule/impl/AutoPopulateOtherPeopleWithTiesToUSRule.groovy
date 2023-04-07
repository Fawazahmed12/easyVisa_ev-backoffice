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

/***
 *
 *  Section:  Relatives Who Will Experience Extreme Hardship if You Are Inadmissible to the United States
 *  SubSection: Relatives Who Will Experience Extreme Hardship if You Are Inadmissible to the United States
 *  Question: (Q_3658) Do you have another person, either your spouse, parent, child, fianc$e$(e), or child
 *  of a fianc$e$(e), different from the relative you identified above, who either has or will suffer an
 *  'extreme hardship' in the future if you are found inadmissible to the United States?
 *  This person MUST be either a U.S. citizen or  LPR (Lawful Permanent Resident).
 *
 *  Value of this question is 'Yes', then we need to display the following sectins
 *      1. Other Relatives to be Considered
 *      2. Other People With Ties to U.S.
 *
 *  Here section 'Other People With Ties to U.S.' has default RepeatingQuestionGroup.
 *  Inorder to display the questions from repeationgQuestionGroup, we need to have atleast empty ansers in our DB
 *  SO this rule will create default answers to the DB, if its triggering question has the answer 'Yes'
 */

@Component
class AutoPopulateOtherPeopleWithTiesToUSRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoPopulateOtherPeopleWithTiesToUSRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.getValue(), true);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        String answerValue = EasyVisaNode.normalizeAnswer(answer.getValue())
        if (answerValue == RelationshipTypeConstants.YES.value) {
            this.autoPopulateFromOtherPeopleWithTiesToUSFields(ruleEvaluationContext);
        } else {
            this.removeOtherPeopleWithTiesToUSFields(ruleEvaluationContext);
        }
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext) {

    }


    void autoPopulateFromOtherPeopleWithTiesToUSFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        String sectionId = 'Sec_otherPeopleWithTiesToUS';
        String subsectionId = 'SubSec_otherPeopleWithTiesToUS';
        String repeatingGroupId = 'RQG_otherPeopleWithTiesToUS';
        // Create a new Default Set of answers
        this.answerService.addDefaultRepeatingGroupIfRequired(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId,
                sectionId, subsectionId, repeatingGroupId, nodeRuleEvaluationContext.currentDate);
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
