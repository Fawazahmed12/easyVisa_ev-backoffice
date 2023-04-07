package com.easyvisa.questionnaire.services

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.INodeComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.Question
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.LocalDate

@Component
class RuleEvaluator {

    @Autowired
    RuleActionHandler ruleMatchHandler;

    public static String DEFAULT_PATH = "has";

    Outcome evaluateQuestion(List<Answer> answerList, QuestionNodeInstance questionNodeInstance) {
        Outcome defaultPath = new Outcome(DEFAULT_PATH, true);
        Answer answer = questionNodeInstance.answer;
        Question easyVisaNode = (Question) questionNodeInstance.getDefinitionNode();
        if (easyVisaNode.actionable) {
            if (StringUtils.isNotEmpty(easyVisaNode.getRuleClassName())) {
                NodeRuleEvaluationContext nodeRuleEvaluationContext = new NodeRuleEvaluationContext(answerList, questionNodeInstance,
                        answer.packageId, answer.applicantId);
                INodeComputeRule computeRule = ruleMatchHandler.getNodeComputeRule(nodeRuleEvaluationContext);
                return computeRule.evaluateOutcome(nodeRuleEvaluationContext);
            } else {
                // Match the direct answer
                return new Outcome(answer.getValue(), true);
            }
        }
        return defaultPath;
    }

    // This method is applicable for both Section and RepeatingQuestionGroup
    Boolean matchesVisibilityCondition(List<Answer> answerList, Long packageId, Long applicantId,
                                       EasyVisaNodeInstance easyVisaNodeInstance) {
        String ruleClassName = easyVisaNodeInstance.getRuleClassName();
        if (StringUtils.isNotEmpty(ruleClassName)) {
            NodeRuleEvaluationContext nodeRuleEvaluationContext = new NodeRuleEvaluationContext(answerList, easyVisaNodeInstance, packageId, applicantId);
            INodeComputeRule computeRule = ruleMatchHandler.getNodeComputeRule(nodeRuleEvaluationContext);
            return computeRule.matchesVisibilityCondition(nodeRuleEvaluationContext);
        }
        return true;
    }

}
