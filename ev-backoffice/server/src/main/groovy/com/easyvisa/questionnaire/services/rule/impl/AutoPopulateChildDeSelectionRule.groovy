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
 * Section: Family Information
 * SubSection: Children Information
 * Question: (Q_1250) Do you have any children under 18 years of age?
 *
 * Notes:  If user selects 'No' to this question, then need to check is there any child
 *          is selected in 'Dependents (Children)' subsection for the question
 *          'Select Child to Auto-Fill data (If child was listed in the previous subsection)'
 *
 *
 *          If the child is selected for the above question, then need to remove its synced values...
 */

@Component
class AutoPopulateChildDeSelectionRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoPopulateChildDeSelectionRule";

    //Given Name (First name)
    private static String SELECT_NAME_FIELD_PATH = 'Sec_familyInformation/SubSec_childrenInformation/Q_1251';

    // Select Child to Auto-Fill data (If child was listed in the previous subsection)
    private static String SELECT_CHILD_TO_AUTOFILL_FIELD_PATH = 'Sec_familyInformation/SubSec_dependentsChildren/Q_1269';


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
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return new Outcome(answer.getValue(), Answer.isValidAnswer(answer));
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        Answer anyChildrenUnder18YearsAnswer = questionNodeInstance.answer;
        if (EasyVisaNode.normalizeAnswer(anyChildrenUnder18YearsAnswer.value) == RelationshipTypeConstants.YES.value) {
            return;
        }

        Boolean evaluateRule = false;
        List<Answer> selectedAutoFillFieldAnswerList = nodeRuleEvaluationContext.findAnswerListByPathILike(SELECT_CHILD_TO_AUTOFILL_FIELD_PATH);
        selectedAutoFillFieldAnswerList.stream()
                .forEach({ selectedAutoFillFieldAnswer ->
            selectedAutoFillFieldAnswer.value = '';
            this.answerService.saveAnswer(selectedAutoFillFieldAnswer, evaluateRule)
        });
    }
}
