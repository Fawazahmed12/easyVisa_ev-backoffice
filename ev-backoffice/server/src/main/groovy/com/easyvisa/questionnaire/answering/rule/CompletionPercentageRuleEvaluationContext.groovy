package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance

class CompletionPercentageRuleEvaluationContext {
    Long packageId
    Long applicantId
    List<Answer> allAnswerList
    SectionNodeInstance sectionNodeInstance
    Double completedPercentage
    int validAnswersCount
    List<EasyVisaNodeInstance> questionNodeInstanceList

    CompletionPercentageRuleEvaluationContext(List<Answer> allAnswerList,
                                              SectionNodeInstance sectionNodeInstance,
                                              Long packageId, Long applicantId,
                                              Double completedPercentage, int validAnswersCount,
                                              List<EasyVisaNodeInstance> questionNodeInstanceList) {
        this.allAnswerList = allAnswerList
        this.sectionNodeInstance = sectionNodeInstance
        this.packageId = packageId
        this.applicantId = applicantId
        this.completedPercentage = completedPercentage
        this.validAnswersCount = validAnswersCount
        this.questionNodeInstanceList = questionNodeInstanceList
    }


    NodeRuleEvaluationContext getNodeEvaluationContext() {
        NodeRuleEvaluationContext ruleEvaluationContext =
                new NodeRuleEvaluationContext(this.allAnswerList, this.sectionNodeInstance,
                        this.packageId, this.applicantId);
        return ruleEvaluationContext;
    }
}
