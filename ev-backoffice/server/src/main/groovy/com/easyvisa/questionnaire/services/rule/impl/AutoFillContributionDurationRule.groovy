package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/***
 *
 *  Section:  Support & Contributions
 *  SubSection: Nature of Contributions
 *  Question: (Q_1407) How frequently do you intend to make this contribution?
 *  Options: Drop Down (Lump Sum, Weekly, Monthly, Annually)
 *
 *  This rule will apply to the above question...
 *  If user select 'Lump Sum' to the above question then auto select 'One Time Only'
 *  to the below question
 *  Question: (Q_1408) For how long do you intend to continue making this contribution?
 *  Options: Radio Buttons (One Time Only, Months, Years)
 *  Programmer Notes: If they chose 'Lump Sum' answer in the previous question
 *                    'How frequently do you intend to make this contribution?', then
 *                    'One Time Only' is selected by default
 *
 *
 *
 *                    The Time period descriptor (in rectangular brackets [] in this question) is a variable and comes from the radio button answer to the  previous question
 *                    'For how long do you intend to continue making this contribution?' insert either 'Weeks' or 'Months' or 'Years'
 *                    If they chose 'Lump Sum' answer in the previous question 'How frequently do you intend to make this contribution?', then insert '1' into this numeric field
 */

@Component
class AutoFillContributionDurationRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoFillContributionDurationRule";
    private static String LUMP_SUM_VALUE = "Lump Sum";
    private static String ONE_TIME_VALUE = "1";

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
        if (this.evaluateAutoFillContributionDurationRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false);
    }


    private Boolean evaluateAutoFillContributionDurationRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return Answer.isValidAnswer(answer) && StringUtils.equals(answer.getValue(), LUMP_SUM_VALUE);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        this.autoSelectContributionDuration(nodeRuleEvaluationContext);
    }

    private void autoSelectContributionDuration(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer selectedFrequentContributionAnswer = questionNodeInstance.getAnswer()
        String ruleParam = questionNodeInstance.getDefinitionNode().getRuleParam();
        String contributionDurationPath = ruleParam + '/' + selectedFrequentContributionAnswer.getIndex();
        List<String> pathInfoList = contributionDurationPath.split("/")
        Answer dateMarriageEndedAnswer = new Answer(packageId: nodeRuleEvaluationContext.packageId, applicantId: nodeRuleEvaluationContext.applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: ONE_TIME_VALUE, path: contributionDurationPath, index: pathInfoList[3])
        answerService.saveAnswer(dateMarriageEndedAnswer);
    }
}
