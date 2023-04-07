package com.easyvisa.questionnaire.answering.benefitcategoryfeatures

import com.easyvisa.ImmigrationBenefit
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.model.EasyVisaNode
import groovy.transform.CompileStatic

@CompileStatic
class K1K3BenefitCategoryFeature extends BaseBenefitCategoryFeature {

    InputSourceType getSponsorshipRelationshipInputSource(InputSourceType inputSourceType, NodeRuleEvaluationContext ruleEvaluationContext) {
        String inputType = 'SponsorshipRelationshipInputSourceRule'
        List<String> fianceK1K3CategoryRelationshipValues = ['fiance', 'spouse']
        return filterInputSourceTypeValues(fianceK1K3CategoryRelationshipValues, inputSourceType, inputType)
    }

    InputSourceType getCitizenshipAcquiredInputSource(InputSourceType inputSourceType, NodeRuleEvaluationContext ruleEvaluationContext) {

        return inputSourceType
    }


    /**
     * Do not ask the following questions if user selected 'Without Inspection' option in question
     * 'This person arrived in the United States under which legal status? '
     *
     *  Note: The following questions connceted only with the forms 129F & 130
     *
     * Q_2957: Date authorized stay expired or will expire as shown on I-94 (Arrival/Departure Record) or I-95
     * Q_2958: What is the I-94 record number?
     *
     * */
    Boolean canEvaluateLegalStatusDependentApplicableRule(NodeRuleEvaluationContext ruleEvaluationContext) {
        return true
    }
}
