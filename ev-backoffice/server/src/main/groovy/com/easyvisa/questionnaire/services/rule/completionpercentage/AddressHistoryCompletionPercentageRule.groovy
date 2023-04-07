package com.easyvisa.questionnaire.services.rule.completionpercentage


import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.CompletionPercentageRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.ICompletionPercentageRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.services.rule.sectioncompletion.AddressHistoryCompletionRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.util.stream.Collectors



/**
 * Section: Address History
 * Applicant: Petitioner / Beneficiary
 *
 * If the 'Address History' has not enough data in a questionnaire, (i.e) not have 5 years of data
 * then we should NOT mark completion-percentage value as 100 even user has answered all the questions
 * */
@Component
class AddressHistoryCompletionPercentageRule implements ICompletionPercentageRule {
    private static String RULE_NAME = 'AddressHistoryCompletionPercentageRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @Autowired
    AddressHistoryCompletionRule addressHistoryCompletionRule

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerCompletionPercentageRules(RULE_NAME, this)
    }

    @Override
    Double calculateCompletionPercentage(CompletionPercentageRuleEvaluationContext ruleEvaluationContext) {
        double completedPercentage = ruleEvaluationContext.completedPercentage
        if (completedPercentage < new Double(100)) {
            return completedPercentage
        }

        NodeRuleEvaluationContext nodeEvaluationContext = ruleEvaluationContext.getNodeEvaluationContext()
        def hasValidAddressHistory = this.addressHistoryCompletionRule.validateAnswerCompletion(nodeEvaluationContext)
        if (hasValidAddressHistory) {
            return completedPercentage
        }

        Integer defaultQuestionNodeInstanceCount = this.getPreviousPhysicalAddressRepeatingQuestionsSize(ruleEvaluationContext)
        if (defaultQuestionNodeInstanceCount) {
            List<EasyVisaNodeInstance> questionNodeInstanceList = ruleEvaluationContext.questionNodeInstanceList
            double currentCompletedPercentage = (ruleEvaluationContext.validAnswersCount / (questionNodeInstanceList.size() + defaultQuestionNodeInstanceCount)) * 100
            return Math.round(currentCompletedPercentage)
        }

        return completedPercentage
    }


    private Integer getPreviousPhysicalAddressRepeatingQuestionsSize(CompletionPercentageRuleEvaluationContext ruleEvaluationContext) {
        SectionNodeInstance sectionNodeInstance = (SectionNodeInstance) ruleEvaluationContext.getSectionNodeInstance()
        SubSectionNodeInstance subSectionNodeInstance = this.getPreviousPhysicalAddressSubSectionInstance(sectionNodeInstance)
        if (!subSectionNodeInstance) {
            return 0
        }

        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = this.findRepeatingQuestionGroupNodeInstance(ruleEvaluationContext.nodeEvaluationContext, subSectionNodeInstance)
        Integer defaultQuestionNodeInstanceCount = repeatingQuestionGroupNodeInstance ? repeatingQuestionGroupNodeInstance.getChildren().size() : 0
        return defaultQuestionNodeInstanceCount
    }


    SubSectionNodeInstance getPreviousPhysicalAddressSubSectionInstance(SectionNodeInstance sectionNodeInstance) {
        String PREVIOUS_PHYSICAL_ADDRESS_SUBSECTION_ID = sectionNodeInstance.getCompletionPercentageRuleParam()

        SubSectionNodeInstance subSectionNodeInstance = (SubSectionNodeInstance) sectionNodeInstance.getChildren().stream()
                .filter({ easyVisaNodeInstance -> easyVisaNodeInstance.id == PREVIOUS_PHYSICAL_ADDRESS_SUBSECTION_ID })
                .findFirst().orElse(null)
        return subSectionNodeInstance
    }


    RepeatingQuestionGroupNodeInstance findRepeatingQuestionGroupNodeInstance(NodeRuleEvaluationContext ruleEvaluationContext,
                                                                              SubSectionNodeInstance subSectionNodeInstance) {
        RepeatingQuestionGroupNodeInstance emptyRepeatingQuestionGroupNodeInstance = null;
        List<RepeatingQuestionGroupNodeInstance> repeatingQuestionGroupNodeInstanceList = subSectionNodeInstance.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        return repeatingQuestionGroupNodeInstanceList.size() ? repeatingQuestionGroupNodeInstanceList.first() : emptyRepeatingQuestionGroupNodeInstance
    }
}
