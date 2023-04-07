package com.easyvisa.questionnaire.services.rule.completionpercentage


import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.CompletionPercentageRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.ICompletionPercentageRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.services.rule.sectioncompletion.EmploymentHistoryCompletionRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class EmploymentHistoryCompletionPercentageRule implements ICompletionPercentageRule {

    private static String RULE_NAME = 'EmploymentHistoryCompletionPercentageRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    EmploymentHistoryCompletionRule employmentHistoryCompletionRule;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerCompletionPercentageRules(RULE_NAME, this);
    }


    @Override
    Double calculateCompletionPercentage(CompletionPercentageRuleEvaluationContext ruleEvaluationContext) {
        double completedPercentage = ruleEvaluationContext.completedPercentage;
        if (completedPercentage < new Double(100)) {
            return completedPercentage;
        }

        NodeRuleEvaluationContext nodeEvaluationContext = ruleEvaluationContext.getNodeEvaluationContext()
        if (this.employmentHistoryCompletionRule.validateAnswerCompletion(nodeEvaluationContext)) {
            return completedPercentage
        }

        SectionNodeInstance sectionNodeInstance = (SectionNodeInstance) ruleEvaluationContext.getSectionNodeInstance();
        SubSectionNodeInstance subSectionNodeInstance = this.employmentHistoryCompletionRule.getEmploymentStatusSubSectionInstance(sectionNodeInstance);
        if(!subSectionNodeInstance){
            return completedPercentage;
        }

        /* Handled the completion percentage if don't have repeatingQuestionGroupNodeInstance : EV-1438
        * For the Benefit Category: Remove Conditions  2-Year to 10-Year LPR,
        * SubSection(subSec_sponsorsCurrentEmployment) doesn't connect with the Form_751
        * This rule is belongs to Section
        */

        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = this.employmentHistoryCompletionRule.findRepeatingQuestionGroupNodeInstance(subSectionNodeInstance);
        Integer defaultQuestionNodeInstanceCount = repeatingQuestionGroupNodeInstance ? 1 : 0;
        if(defaultQuestionNodeInstanceCount==0) {
            return completedPercentage;
        }

        List<EasyVisaNodeInstance> questionNodeInstanceList = ruleEvaluationContext.questionNodeInstanceList;
        double currentCompletedPercentage = (ruleEvaluationContext.validAnswersCount / (questionNodeInstanceList.size() + defaultQuestionNodeInstanceCount)) * 100
        return Math.round(currentCompletedPercentage);
    }
}
