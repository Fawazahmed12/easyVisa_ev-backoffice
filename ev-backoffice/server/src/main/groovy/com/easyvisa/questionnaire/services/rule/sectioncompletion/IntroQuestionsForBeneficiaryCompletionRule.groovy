package com.easyvisa.questionnaire.services.rule.sectioncompletion

import com.easyvisa.PackageService
import com.easyvisa.SectionCompletionStatusService
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.answering.benefitcategoryfeatures.BenefitCategoryFeaturesFactory
import com.easyvisa.questionnaire.answering.rule.ISectionCompletionRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/***
 *
 *   Notes: ONLY display the following question if the user answered 'Spouse' to the question 'What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States?'
 *   Question: Q_1701 Is the person you are currently married to, the same person who sponsored you for your conditional residence status (conditional 'green card')?
 *
 *   Here the scenario is we need to compute the section-completion of other section
 *
 *  Section:  Intro Questions For Beneficiary (The person who is immigrating to the United States)
 */

@Component
class IntroQuestionsForBeneficiaryCompletionRule implements ISectionCompletionRule {

    private static String RULE_NAME = 'IntroQuestionsForBeneficiaryCompletionRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    PackageService packageService
    SectionCompletionStatusService sectionCompletionStatusService;


    @PostConstruct
    void register() {
        ruleComponentRegistry.registerScetionCompletionRules(RULE_NAME, this);
    }


    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        return true;
    }


    @Override
    CompletionWarningDto generateCompletionWarning(NodeRuleEvaluationContext ruleEvaluationContext) {
        return new CompletionWarningDto();
    }


    @Override
    void updatedDependentSectionCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        packageService
                .getBenefitCategoryFeature(ruleEvaluationContext.packageId)
                .updatedDependentSectionCompletionOnRelationshipToPetitionerAction(ruleEvaluationContext, this.sectionCompletionStatusService)
    }
}
