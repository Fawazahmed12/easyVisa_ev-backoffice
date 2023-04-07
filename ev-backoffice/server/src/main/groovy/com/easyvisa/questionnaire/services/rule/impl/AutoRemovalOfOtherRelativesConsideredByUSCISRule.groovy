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
 *  Question: (Q_3601) Do you have a spouse or parent that would suffer extreme hardship, and therefore you would like him/her to be considered by the USCIS in deciding whether or not you should be admitted to the U.S.? This person MUST be either a U.S. citizen or  LPR (Lawful Permanent Resident).
 *  Question: (Q_3602) Do you have a spouse, parent, child, fianc$e$(e), or child of a fianc$e$(e) who either has or will experience 'extreme hardship' in the future if you are not granted a waiver of your inadmissibility to the United States? This person MUST be either a U.S. citizen or  LPR (Lawful Permanent Resident).
 *
 */
@Component
class AutoRemovalOfOtherRelativesConsideredByUSCISRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoRemovalOfOtherRelativesConsideredByUSCISRule";

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
        if (answerValue == RelationshipTypeConstants.NO.value) {
            this.removeOtherPeopleWithTiesToUSFields(ruleEvaluationContext);
        }
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext) {

    }


    void removeOtherPeopleWithTiesToUSFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        List<String> fieldsToRemove = this.getOtherPeopleWithTiesToUSFields();
        this.answerService.removeAutoFillFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToRemove);
    }

    private List<String> getOtherPeopleWithTiesToUSFields() {
        List<String> fieldsToRemove = [
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3611',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3612',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3613',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3621',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3622',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3623',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3624',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3625',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3626',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3627',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3628',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3629',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3630',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3641',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3642',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3651',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3652',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3653',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3654',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3655',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3656',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3657',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_6023',
                'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3658',

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
                'Sec_otherPeopleWithTiesToUS/SubSec_otherPeopleWithTiesToUS/Q_3844',

                'Sec_statementFromApplicant/SubSec_extremeHardshipStatement/Q_3902',
        ];
        return fieldsToRemove;
    }

}
