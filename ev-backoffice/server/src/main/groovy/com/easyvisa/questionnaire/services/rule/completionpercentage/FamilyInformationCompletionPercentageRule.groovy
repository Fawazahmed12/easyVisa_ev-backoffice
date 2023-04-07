package com.easyvisa.questionnaire.services.rule.completionpercentage

import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.CompletionPercentageRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.ICompletionPercentageRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.services.rule.sectioncompletion.FamilyInformationCompletionRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 *
 * This rule will validate the completion-percengtage value of FamilyInformation section
 * Here we need to validate that package derivatives count should be equal to the added derivative beneficiary data of children and spouse in a questionnaire
 * If the package derivatives count is not be equal to the added derivative beneficiary data of children and spouse in a questionnaire, then
 * we should not mark completion-percengtage value as 100 even use has answered all the questions
 */
@Component
class FamilyInformationCompletionPercentageRule implements ICompletionPercentageRule {
    private static String RULE_NAME = 'FamilyInformationCompletionPercentageRule'


    private static String CHILDREN_SUBSECTION_ID = "SubSec_childrenInformationForBeneficiary";
    private static String CURRENTSPOUSE_SUBSECTION_ID = "SubSec_currentSpouseForBeneficiary";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    FamilyInformationCompletionRule familyInformationCompletionRule;

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
        if (this.familyInformationCompletionRule.validateAnswerCompletion(nodeEvaluationContext)) {
            return completedPercentage
        }

        SectionNodeInstance sectionNodeInstance = (SectionNodeInstance) ruleEvaluationContext.getSectionNodeInstance();
        SubSectionNodeInstance currentSpouseSubSectionNodeInstance = this.getSubSectionInstance(CURRENTSPOUSE_SUBSECTION_ID, sectionNodeInstance);
        SubSectionNodeInstance childrenInfoSubSectionNodeInstance = this.getSubSectionInstance(CHILDREN_SUBSECTION_ID, sectionNodeInstance);
        if (currentSpouseSubSectionNodeInstance || childrenInfoSubSectionNodeInstance) {
            int defaultQuestionNodeInstanceCount = 2;
            List<EasyVisaNodeInstance> questionNodeInstanceList = ruleEvaluationContext.questionNodeInstanceList;
            double currentCompletedPercentage = (ruleEvaluationContext.validAnswersCount / (questionNodeInstanceList.size() + defaultQuestionNodeInstanceCount)) * 100
            return Math.round(currentCompletedPercentage);
        }
        return completedPercentage;
    }


    private SubSectionNodeInstance getSubSectionInstance(String subsectionId, SectionNodeInstance sectionNodeInstance) {
        SubSectionNodeInstance subSectionNodeInstance = (SubSectionNodeInstance) sectionNodeInstance.getChildren().stream()
                .filter({ easyVisaNodeInstance -> easyVisaNodeInstance.id == subsectionId })
                .findFirst().orElse(null);
        return subSectionNodeInstance;
    }
}
