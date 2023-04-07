package com.easyvisa.questionnaire.services.rule.pdfieldrelationship

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IPdfFieldRelationshipRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule is used to Print either Yes/No in the Form-751 based on the following question's answer values.
 * 1. move-in date of the current address
 * 2. What is the date that your Conditional Permanent Residence began (which is the date at the bottom of your Conditional Permanent Resident Card called 'Residence Since')?
 *
 * If the move-in date of the current address is AFTER the date they got their Permanent Residence (green) card,
 * then click the tick box representing the ‘Yes’ response in the form for that question
 *
 *
 * If the move-in date of the current address is BEFORE the date they got their Permanent Residence (green) card,
 * hen click the tick box representing the ‘No’ response in the form for that question
 *
 * */

@Component
class ResidedAtOtherAddressPdfFieldRelationshipRule implements IPdfFieldRelationshipRule {

    private static String RULE_NAME = 'ResidedAtOtherAddressPdfFieldRelationshipRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerPdfFieldRelationshipRule(RULE_NAME, this)
    }

    /**
     *
     * If the move-in date of the current address is AFTER the date they got their Permanent Residence (green) card
     * which is the date in question 'What is the date that your Conditional Permanent Residence began
     * (which is the date at the bottom of your Conditional Permanent Resident Card called 'Residence Since')?
     * then click the tick box representing the ‘Yes’ response in the form for that question,
     *
     * If the move-in date of the current address is BEFORE the date they got their Permanent Residence (green) card
     * which is the date in question 'What is the date that your Conditional Permanent Residence began
     * (which is the date at the bottom of your Conditional Permanent Resident Card called 'Residence Since')?
     * then click the tick box representing the ‘No’ response in the form for that question,
     */
    @Override
    String evaluateRelationshipType(NodeRuleEvaluationContext ruleEvaluationContext) {
        Package aPackage = Package.get(ruleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId)
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        if (immigrationBenefit.category != ImmigrationBenefitCategory.REMOVECOND) {
            QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
            Answer answer = questionNodeInstance.getAnswer()
            return answer?.value
        }
        Boolean result = true
        return (result == true) ? RelationshipTypeConstants.YES.value : RelationshipTypeConstants.NO.value
    }
}
