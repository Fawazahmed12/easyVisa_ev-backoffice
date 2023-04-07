package com.easyvisa.questionnaire.services.rule.sectionvisibility


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.ISectionVisibilityRule
import com.easyvisa.questionnaire.answering.rule.SectionVisibilityRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section       : (Sec_supportAndContributions) Support & Contributions
 * ApplicantType : Petitioner
 * P.Notes       : The subsection and the related questions ONLY get asked if the Immigration Benefit Category selected
 *                 by the representative was K1/K3 visa and also answer for the below question is  NOT 'spouse'.
 *                'How is the Beneficiary related to you?'
 *
 * Notes:   Questions in this section has connected to the form 134 alone
 * */
@CompileStatic
@Component
class SupportAndContributionsApplicableRule implements ISectionVisibilityRule {

    private static String RULE_NAME = 'SupportAndContributionsApplicableRule'

    final private static String RELATED_BENEFICIARY_QUESTION_PATH = "Sec_1/SubSec_4/Q_27"
    final private static String SPOUSE_VALUE = "spouse"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerSectionVisibilityRules(RULE_NAME, this)
    }

    @Override
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    void updateVisibilityOnSuccessfulMatch(SectionVisibilityRuleEvaluationContext ruleEvaluationContext) {
        Answer beneficiaryRelationAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, RELATED_BENEFICIARY_QUESTION_PATH)
        Boolean canHideSupportAndContributionSection = Answer.isValidAnswer(beneficiaryRelationAnswer) && beneficiaryRelationAnswer.doesMatch(SPOUSE_VALUE)
        if (canHideSupportAndContributionSection) {
            SectionNodeInstance sectionNodeInstance = ruleEvaluationContext.getSectionNodeInstance()
            sectionNodeInstance.setVisibility(false)
        }
    }
}
