package com.easyvisa.questionnaire.answering.benefitcategoryfeatures

import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType

class F1ABenefitCategoryFeature extends BaseBenefitCategoryFeature {

    InputSourceType getSponsorshipRelationshipInputSource(InputSourceType inputSourceType, NodeRuleEvaluationContext ruleEvaluationContext) {
        String inputType = 'SponsorshipRelationshipInputSourceRule'
        List<String> fianceF1CategoryRelationshipValues = ['spouse','parent','sibling','child'];
        return filterInputSourceTypeValues(fianceF1CategoryRelationshipValues, inputSourceType, inputType)
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
        return true;
    }
}
