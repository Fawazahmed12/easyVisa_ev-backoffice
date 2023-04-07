package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *   Apply this rule to 'SubSection'
 *
 *   This is the general rule, which is useful to dipslay the SubSection based on the
 *   specific answer for a Particular Question.
 *
 *   So here through ruleParam we will send two values
 *
 *   Value1: Question Path
 *   Value2: Expecting Answer
 *
 */

@Component
class SubSectionApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "SubSectionApplicableRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Boolean canShowSubSection = this.evaluateSubSectionApplicableRule(nodeRuleEvaluationContext)
        return canShowSubSection
    }

    private Boolean evaluateSubSectionApplicableRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam()
        String[] ruleParams = ruleParam.split(",")
        String questionFieldPath = ruleParams[0]
        String expectingAnswerValue = ruleParams[1]
        Answer answer = nodeRuleEvaluationContext.findAnswerByPath(questionFieldPath)
        if (Answer.isValidAnswer(answer)) {
            String answerValue = EasyVisaNode.normalizeAnswer(answer.getValue())
            return (expectingAnswerValue == answerValue)
        }
        return false
    }
}
