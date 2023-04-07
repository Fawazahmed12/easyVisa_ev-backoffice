package com.easyvisa.questionnaire.answering.benefitcategoryfeatures

import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType

class F1BBenefitCategoryFeature extends BaseBenefitCategoryFeature {

    InputSourceType getSponsorshipRelationshipInputSource(InputSourceType inputSourceType, NodeRuleEvaluationContext ruleEvaluationContext) {
        String inputType = 'SponsorshipRelationshipInputSourceRule'
        List<String> fianceF1BCategoryRelationshipValues = ['spouse','parent','sibling','child','substitutesponsor'];
        return filterInputSourceTypeValues(fianceF1BCategoryRelationshipValues, inputSourceType, inputType)
    }
}
