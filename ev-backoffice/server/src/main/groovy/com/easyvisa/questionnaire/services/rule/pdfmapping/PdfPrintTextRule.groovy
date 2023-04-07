package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.repositories.FormUIMetaDataDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class PdfPrintTextRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "PdfPrintTextRule"

    @Autowired
    private FormUIMetaDataDAO formUIMetaDataDAO

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        String inputTypeSource = params ?: '';
        InputSourceType inputSourceType = formUIMetaDataDAO.getInputSourceTypeModel(inputTypeSource) ?: new InputSourceType(inputTypeSource);
        List<InputSourceType.ValueMap> inputOptions = inputSourceType.getValues();
        pdfFieldDetail.answerValueObjectList.each {
            InputSourceType.ValueMap matchedValueMap = inputOptions.find { element -> element.value.equals(it.value) }
            it.printValue = matchedValueMap?.printValue ?: it.value;
        }
    }
}
