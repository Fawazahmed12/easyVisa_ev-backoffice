package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.FieldMappingDetail
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule will print predefined word in the parameter for the given answer/pdf form.
 */
@Component
class WordRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "WordPrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        String[] ruleParams = splitParamsSections(params)
        String[] values = splitData(ruleParams, 0)
        String[] expressions = splitData(ruleParams, 1)
        Integer repeats = null
        String repeatsStr = getParam(ruleParams, 2)
        if (repeatsStr) {
            repeats = repeatsStr as Integer
        }
        String valueToCheck = getParam(ruleParams, 3)
        String questionToCheck = getParam(ruleParams, 4)

        switch (pdfFieldDetail.fieldMappingDetail.fieldType) {
            case 'repeat':
                repeatAutoPopulation(expressions, values, repeats, pdfFieldDetail, valueToCheck, pdfMappingEvaluationContext, questionToCheck)
                break
            default: simpleAutoPopulation(expressions, values, pdfFieldDetail, valueToCheck, pdfMappingEvaluationContext, questionToCheck)
        }
    }

    private String[] splitData(String[] data, Integer index) {
        if (data.length > index && data) {
            return splitParamsValue(data[index])
        }
        null
    }

    private void simpleAutoPopulation(String[] expressions, String[] values, PdfFieldDetail pdfFieldDetail,
                                      String valueToCheck, PdfMappingEvaluationContext pdfMappingEvaluationContext,
                                      String questionToCheck) {
        Boolean exist = Boolean.TRUE
        PdfFieldDetail fieldDetail1ToCheck = pdfFieldDetail
        if (questionToCheck) {
            fieldDetail1ToCheck = pdfMappingEvaluationContext.getPdfFieldDetail(questionToCheck)
        }
        if (valueToCheck) {
            if (valueToCheck == 'petitionerMarriedToNotBeneficiary') {
                exist = isPetitionerNotMarriedToPrincipleBeneficiary(pdfMappingEvaluationContext)
            } else {
                AnswerValueObject answer = fieldDetail1ToCheck?.answerValueObjectList.find { it.value == valueToCheck }
                if (!answer) {
                    exist = Boolean.FALSE
                }
            }
        }
        if (exist) {
            if (expressions && expressions[0]) {
                AnswerValueObject answerValueObject = new AnswerValueObject(new Answer('value': values[0]))
                FieldMappingDetail fieldMappingDetail = new FieldMappingDetail(
                        'fieldType': pdfFieldDetail.fieldMappingDetail.fieldType,
                        'fieldExpressions': expressions.collect { it })
                pdfMappingEvaluationContext.addPdfField(new PdfFieldDetail('answerValueObjectList': [answerValueObject],
                        'fieldMappingDetail': fieldMappingDetail))
            } else {
                pdfFieldDetail.answerValueObjectList[0].value = values[0]
            }
        }
    }

    private void repeatAutoPopulation(String[] expressions, String[] values, Integer repeats, PdfFieldDetail pdfFieldDetail,
                                      String valueToCheck, PdfMappingEvaluationContext pdfMappingEvaluationContext,
                                      String questionToCheck) {
        String valueToCheckIn = valueToCheck
        if (questionToCheck) {
            PdfFieldDetail fieldDetail1ToCheck = pdfMappingEvaluationContext.getPdfFieldDetail(questionToCheck)
            AnswerValueObject answer = fieldDetail1ToCheck?.answerValueObjectList?.find { it.value == valueToCheck }
            if (answer) {
                return
            } else {
                valueToCheckIn = null
            }
        }
        Integer max = repeats
        List<AnswerValueObject> valueObjectList = pdfFieldDetail.answerValueObjectList
        if (max == null) {
            max = valueObjectList.max { it.index }.index
        } else {
            max -= 1
        }
        List<AnswerValueObject> answers = []
        Map<Integer, String> indices = valueObjectList.collectEntries { [it.index, it.value] }
        (0..max).each {
            if ((!valueToCheckIn && indices[it]) || (valueToCheckIn && indices[it] && indices[it] == valueToCheckIn)) {
                answers.add(new AnswerValueObject(new Answer('value': values[0], 'index': it)))
            }
        }
        if (answers) {
            addAutoFieldAdaptive(pdfFieldDetail.fieldMappingDetail, expressions, answers, pdfMappingEvaluationContext)
        }
    }

}
