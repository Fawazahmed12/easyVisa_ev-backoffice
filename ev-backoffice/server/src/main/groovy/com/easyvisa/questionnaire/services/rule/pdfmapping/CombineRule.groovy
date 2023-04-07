package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule combines data via space from different questions into one text and populates result into a specified field.
 *
 */
@Component
class CombineRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "CombinePrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        String[] paramsArray = splitParamsSections(params)
        String[] questionIds = splitParamsValue(paramsArray[0])
        String[] expressions = splitParamsValue(paramsArray[1])
        String questionToCheck = getParam(paramsArray, 2)
        String[] valuesToCheck = splitParamsValue(getParam(paramsArray, 3))
        String[] extraExpressionsToSet = splitParamsValue(getParam(paramsArray, 4))
        String replaceToMultiple = getParam(paramsArray, 5)
        pdfFieldDetail.answerValueObjectList.eachWithIndex { AnswerValueObject entry, Integer i ->
            String result = combineData(entry, questionIds, pdfMappingEvaluationContext)
            checkExtraPopulation(questionToCheck, valuesToCheck, result, extraExpressionsToSet, replaceToMultiple,
                    entry.index, pdfMappingEvaluationContext)
            String expression = getParam(expressions, i)
            if (expression) {
                String[] expressionsToAuto = [expression]
                addAutoField(expressionsToAuto, result, pdfMappingEvaluationContext)
            }
        }
    }

    private String combineData(AnswerValueObject entry, String[] questionIds, PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        StringBuilder resultBuilder = new StringBuilder()
        Integer currentIndex = entry.index
        questionIds.each {
            PdfFieldDetail fieldToCombine = pdfMappingEvaluationContext.getPdfFieldDetail(it)
            if (fieldToCombine) {
                String value = fieldToCombine.answerValueObjectList.find { it.index == currentIndex }?.value
                if (value) {
                    resultBuilder << value
                    resultBuilder << ' '
                }
            }
        }
        resultBuilder.toString().trim()
    }

    private void checkExtraPopulation(String questionToCheck, String[] valuesToCheck, String result,
                                      String[] extraExpressionsToSet, String replaceToMultiple, Integer index,
                                      PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        PdfFieldDetail checkDetail = pdfMappingEvaluationContext.getPdfFieldDetail(questionToCheck, valuesToCheck, index)
        if (checkDetail) {
            String autoId = getAutoQuestionId(questionToCheck, valuesToCheck)
            PdfFieldDetail exist = pdfMappingEvaluationContext.getAutoPdfFieldDetail(autoId)
            if (exist) {
                exist.answerValueObjectList[0].value = replaceToMultiple
            } else {
                addAutoField(extraExpressionsToSet, result, pdfMappingEvaluationContext, autoId)
            }
        }
    }

    private String getAutoQuestionId(String question, String[] values) {
        "${RULE_NAME}_${question}_${values}"
    }

}
