package com.easyvisa.questionnaire.services.rule.sectionvisibility

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.ISectionVisibilityRule
import com.easyvisa.questionnaire.answering.rule.SectionVisibilityRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Relationship to Petitioner
 * Notes: The subsection and the related questions ONLY get asked if given Immigration Benefit Category has a Form-129F
 *
 * */
@CompileStatic
@Component
class RelationshipToPetitionerApplicableRule implements ISectionVisibilityRule {

    private static String RULE_NAME = 'RelationshipToPetitionerApplicableRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerSectionVisibilityRules(RULE_NAME, this)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(SectionVisibilityRuleEvaluationContext ruleEvaluationContext) {
        Boolean isIncludedInForm129 = packageQuestionnaireService.isSectionIncluded(ruleEvaluationContext, PdfForm.I129F)
        if (!isIncludedInForm129) {
            SectionNodeInstance sectionNodeInstance = ruleEvaluationContext.getSectionNodeInstance()
            sectionNodeInstance.setVisibility(false)
        }
    }
}
