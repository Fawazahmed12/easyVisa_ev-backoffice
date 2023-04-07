package com.easyvisa.questionnaire.services.rule.sectioncompletion

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.SectionCompletionStatusService
import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.ISectionCompletionRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/***
 *
 *   Notes: Questions in 'Parent 1' and 'Parent 2' subsection of 'Family Information' section will be
 *          available only if the immigraion-category is Employment Authorization Document (BC_EAD), and also
 *          user has answered the following question as 'yes'
 *
 *
 *  Section:  Personal Information
 *  SubSection: Personal Information
 *  Question: (Q_2408) Do you want the SSA to issue you a Social Security card?
 */

@Component
class PersonalInformationCompletionRule implements ISectionCompletionRule {

    private static String RULE_NAME = 'PersonalInformationCompleteionRule'
    private static String DEPENDENT_SECTION_ID = 'Sec_familyInformationForBeneficiary'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

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
        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId);
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        if (immigrationBenefit.category==ImmigrationBenefitCategory.EAD) {
            this.sectionCompletionStatusService.updateSectionCompletionStatus(ruleEvaluationContext.packageId, ruleEvaluationContext.applicantId,
                    DEPENDENT_SECTION_ID, DisplayTextLanguage.defaultLanguage, ruleEvaluationContext.currentDate);
        }
    }
}
