package com.easyvisa.questionnaire.services.rule.impl


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
 * Section: Employment History
 * SubSection: Employment Status
 * Question: (Q_1016) When did this employment end for this employer?  (dataType: string)
 *           (Q_1017) When did this employment end for this employer?  (dataType: date)
 *
 * Notes: This question ONLY appears for the SECOND (and successive) iteration of Employment History.
 * This rule applied to the above 2 questions... Based on the following question-answer here
 * we need to display one of the above 2 questions.. (i.e)
 *      (Q_1015) Are you still working at this employer?
 * If user SELECTED 'Yes'  to the above question then 'TO PRESENT' text gets populated into this date field
 * as dimmed out gray (b2b2b2) text AND this field (and the calendar pop up) become NON-editable AND
 * the question gets the check mark, indicating it was completed.
 */

@Component
class EmploymentEndDateVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = "EmploymentEndDateVisibilityConstraintRule";
    private static Integer ITERATION_COUNT = 2;

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

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
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Integer repeatingIndex = questionNodeInstance.getRepeatingIndex(); //it is zero based value
        Integer iterationCount = ++repeatingIndex;
        if (iterationCount < ITERATION_COUNT) {
            questionNodeInstance.setVisibility(false);
        }

        String ruleParam = questionNodeInstance.getDefinitionNode().getRuleParam();
        String ARE_YOU_STILL_WORKING_PATH = ruleParam.split(',')[0];
        String areYouStillWorkingAnswerValue = ruleParam.split(',')[1];

        String areYouStillWorkingFieldPath = ARE_YOU_STILL_WORKING_PATH + '/' + questionNodeInstance.getRepeatingIndex();
        Answer areYouStillWorkingAnswer = ruleEvaluationContext.findAnswerByPath(areYouStillWorkingFieldPath);

        //Here if user didn't answer areYouStillWorkingAnswer question, then need to display the question
        // with inputType date instead of readOnly input
        if (!Answer.isValidAnswer(areYouStillWorkingAnswer) && areYouStillWorkingAnswerValue== RelationshipTypeConstants.YES.value) {
            questionNodeInstance.setVisibility(false);
        }

        if (Answer.isValidAnswer(areYouStillWorkingAnswer) && areYouStillWorkingAnswer.getValue() != areYouStillWorkingAnswerValue) {
            questionNodeInstance.setVisibility(false);
        }
    }
}
