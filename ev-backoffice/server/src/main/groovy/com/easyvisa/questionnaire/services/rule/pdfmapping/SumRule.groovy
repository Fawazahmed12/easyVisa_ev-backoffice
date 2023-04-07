package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import com.easyvisa.utils.NumberUtils
import com.easyvisa.utils.PdfUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule calculates number values, e.g. number of children or asset values
 * If set to non numeric value a count of iteration will be calculated.
 */
@Component
class SumRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "SumPrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        String[] paramArray = splitParamsSections(params)
        String[] expression = splitParamsValue(getParam(paramArray, 0))
        Long sum = sumPart(paramArray, pdfMappingEvaluationContext, pdfFieldDetail)
        sum -= minusPart(paramArray, pdfMappingEvaluationContext)
        setValue(expression, NumberUtils.formatUSNumber(sum), pdfFieldDetail, pdfMappingEvaluationContext)
    }

    private void setValue(String[] expression, String sum, PdfFieldDetail pdfFieldDetail, PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        String[] fields = expression
        if (!fields[0]) {
            fields = pdfFieldDetail.fieldMappingDetail.fieldExpressions
            pdfFieldDetail.fieldMappingDetail.fieldExpressions = [PdfUtils.AUTO_ID] * pdfFieldDetail.answerValueObjectList.size()
        }
        addAutoField(fields, sum, pdfMappingEvaluationContext)
    }

    private Long minusPart(String[] paramArray, PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        if (getParam(paramArray, 2)) {
            return manySum(splitParamsValue(paramArray[2]), null, pdfMappingEvaluationContext)
        }
        0
    }

    private Long sumPart(String[] paramArray, PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail) {
        if (getParam(paramArray, 1)) {
            return manySum(splitParamsValue(paramArray[1]), paramArray, pdfMappingEvaluationContext)
        } else {
            return simpleSum(pdfFieldDetail)
        }
    }

    private Long simpleSum(PdfFieldDetail pdfFieldDetail, List<Integer> indices = null) {
        Long sum = 0
        pdfFieldDetail.answerValueObjectList.each {
            if ((indices == null) || (indices != null && !indices.empty &&indices.contains(it.index))) {
                String value = it.value.replaceAll(',', '')
                if (value.isNumber()) {
                    sum += value as Long
                } else {
                    sum++
                }
            }
        }
        sum
    }

    private Long manySum(String[] questions, String[] paramArray,
                         PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        List<Integer> indices = getExtraIndices(paramArray, pdfMappingEvaluationContext)
        Long sum = 0
        questions.each {
            switch (it) {
                case 'petitionerMarriedToNotBeneficiary':
                    if (isPetitionerNotMarriedToPrincipleBeneficiary(pdfMappingEvaluationContext)) {
                        sum++
                    }
                    break
                case 'beneficiariesCount':
                    sum += getBeneficiariesCount(pdfMappingEvaluationContext)
                    break
                default:
                    String[] idAndValue = splitQuestionIdValue(it)
                    String[] valuesToCheck = null
                    if (idAndValue.size() > 1) {
                        valuesToCheck = [getParam(idAndValue, 1)]
                    }
                    PdfFieldDetail detail = pdfMappingEvaluationContext.getPdfFieldDetail(idAndValue[0], valuesToCheck)
                    if (detail) {
                        sum += simpleSum(detail, indices)
                    }
            }
        }
        sum
    }

    private List<Integer> getExtraIndices(String[] paramArray, PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        String questionIdToCheck = getParam(paramArray, 3)
        String[] extraValuesToCheck = splitParamsValue(getParam(paramArray, 4))
        List<Integer> indices = null
        PdfFieldDetail extraCheck = pdfMappingEvaluationContext.getPdfFieldDetail(questionIdToCheck)
        if (extraCheck) {
            indices = []
            extraCheck.answerValueObjectList.each {
                if (extraValuesToCheck.contains(it.value)) {
                    indices.add(it.index)
                }
            }
        }
        indices
    }

}
