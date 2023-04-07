package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class ContactInformationAvailabilityRule extends BaseComputeRule {

    private static String RULE_NAME = "ContactInformationAvailabilityRule";

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
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.getValue(), true);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        Answer contactInformationAvailabilityAnswer = questionNodeInstance.getAnswer();
        String ruleParam = questionNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = ruleParam.split(",");
        for (int i = 0; i < ruleParams.length; i += 2) {
            String fieldPath = ruleParams[i];
            String fieldValue = (ruleParams.length > (i + 1)) ? ruleParams[i + 1] : "";
            String answerValue = (contactInformationAvailabilityAnswer.value == 'true') ? fieldValue : "";
            String[] pathInfoList = fieldPath.split("/");
            Answer answer = new Answer(packageId: nodeRuleEvaluationContext.packageId, applicantId: nodeRuleEvaluationContext.applicantId,
                    sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                    value: answerValue, path: fieldPath)
            answerService.saveAnswer(answer);
        }
    }
}
