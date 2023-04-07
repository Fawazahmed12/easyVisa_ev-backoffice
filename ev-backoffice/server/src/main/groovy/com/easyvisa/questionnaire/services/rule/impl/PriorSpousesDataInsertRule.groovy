package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.Question
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.repositories.QuestionDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Sec_familyInformation
 * SubSection: SubSec_currentSpouse
 * Question: 1. (Q_1220) Were you married previously?
 * RuleParam: Sec_familyInformation/SubSec_currentSpouse/Q_1220,RQG_priorSpouses,SubSec_priorSpouses
 *
 * Section: Sec_familyInformationForBeneficiary
 * SubSection: SubSec_maritalStatusForBeneficiary
 * Question: 1. (Q_2783) Do you have any previous marriages?
 * RuleParam: Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2783,RQG_priorSpousesForBeneficiary,SubSec_priorSpousesForBeneficiary
 */

@Component
class PriorSpousesDataInsertRule extends BaseComputeRule {

    private static String RULE_NAME = "PriorSpousesDataInsertRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    QuestionDAO questionDAO;

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluatePriorSpousesDataInsertRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        this.addDependentDefaultRepeatingGroupIfRequired(nodeRuleEvaluationContext);
    }


    protected Boolean evaluatePriorSpousesDataInsertRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = ruleParam.split(",");
        String previouslyMarriedFieldPath = ruleParams[0];
        String repeatingGroupId = ruleParams[1];
        List<Answer> wereYouPreviouslyMarriedList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), previouslyMarriedFieldPath);
        if (!wereYouPreviouslyMarriedList.isEmpty()) {
            Answer wereYouPreviouslyMarriedAnswer = wereYouPreviouslyMarriedList[0];
            String wereYouPreviouslyMarriedAnswerValue = EasyVisaNode.normalizeAnswer(wereYouPreviouslyMarriedAnswer.getValue());
            if (wereYouPreviouslyMarriedAnswerValue == RelationshipTypeConstants.YES.value) {
                return true;
            }
        }
        return false;
    }


    private addDependentDefaultRepeatingGroupIfRequired(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = ruleParam.split(",");
        String previouslyMarriedFieldPath = ruleParams[0];
        String repeatingGroupId = ruleParams[1];
        String subsectionId = ruleParams[2];
        String questVersion = nodeRuleEvaluationContext.getQuestionnaireVersion();

        List<Question> questionList = questionDAO.findQuestionsOfRepeatingGroupByEasyVisaId(questVersion, repeatingGroupId)
        String questionId = questionList[0].id
        String sectionId = previouslyMarriedFieldPath.split("/")[0];

        // Check if it has a instance of repeatting group question
        Answer existingAnswer = Answer.findByPackageIdAndApplicantIdAndSectionIdAndSubsectionIdAndQuestionIdAndIndex(nodeRuleEvaluationContext.packageId,
                nodeRuleEvaluationContext.applicantId, sectionId, subsectionId, questionId, 0)
        if (existingAnswer) {
            return;
        }
        answerService.addRepeatingGroupInstance(nodeRuleEvaluationContext.packageId,
                nodeRuleEvaluationContext.applicantId, sectionId, subsectionId,
                repeatingGroupId, nodeRuleEvaluationContext.currentDate);
    }
}
