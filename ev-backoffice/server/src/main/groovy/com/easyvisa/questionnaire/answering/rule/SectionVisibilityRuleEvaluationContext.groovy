package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.answering.SectionNodeInstance

class SectionVisibilityRuleEvaluationContext {
    Long packageId
    Long applicantId
    String benefitCategoryId
    SectionNodeInstance sectionNodeInstance

    SectionVisibilityRuleEvaluationContext(SectionNodeInstance sectionNodeInstance,
                                           Long packageId, Long applicantId,
                                           String benefitCategoryId){
        this.sectionNodeInstance = sectionNodeInstance
        this.packageId = packageId
        this.applicantId = applicantId
        this.benefitCategoryId = benefitCategoryId
    }
}
