package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.enums.State
import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * There are places in USCIS form where  need to print state code instead of its name...
 *
 * From Programmer Notes: So State should be formatted to two characters, e.g. i-129f, page 1, part 1, item 8.e.
 *
 * This rule will convert the saved state-name into state-code
 *
 * */
@Component
class StateCodeRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "StateCodePrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        List<AnswerValueObject> answerValueObjectList = pdfFieldDetail.getAnswerValueObjectList()
        answerValueObjectList.each {
            State selectedState = this.findStateByDisplayName(it.value)
            Integer formFieldsCount = pdfFieldDetail.fieldMappingDetail.formFieldCount
            if ((selectedState != null) && (!formFieldsCount || (formFieldsCount && it.index < formFieldsCount))) {
                String stateCode = selectedState.code
                it.setValue(stateCode) //update the state-name by state-code..
            }
        }
    }


    private State findStateByDisplayName(String displayName) {
        if (StringUtils.isEmpty(displayName)) {
            return null
        }
        return State.valueOfDisplayName(displayName)
    }

}
