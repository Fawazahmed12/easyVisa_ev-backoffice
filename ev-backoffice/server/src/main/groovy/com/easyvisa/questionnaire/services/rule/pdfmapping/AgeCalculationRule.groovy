package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import com.easyvisa.questionnaire.util.DateUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate
import java.time.Period

/**
 * There are places in USCIS form where need to print a person age instead of date of birth...
 *
 * This rule will calculate the saved date of birth into age
 *
 * */
@Component
class AgeCalculationRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "AgeCalculationPrintRule"

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
        String upperLimitToPrintString = getParam(paramsArray, 0)
        Integer upperLimitToPrint = upperLimitToPrintString ? upperLimitToPrintString as Integer : null
        List<AnswerValueObject> answersToSet = []
        pdfFieldDetail.answerValueObjectList.each {
            LocalDate birthDate = DateUtil.pdfLocalDate(it.value)
            it.value = DateUtil.getPeriod(birthDate)
            if ((upperLimitToPrint == null) ||
                (upperLimitToPrint != null && Period.between(birthDate, DateUtil.today()).years < upperLimitToPrint)) {
                AnswerValueObject valueToSet = it.copy()
                valueToSet.index = answersToSet.size()
                answersToSet << valueToSet
            }
        }
        pdfFieldDetail.answerValueObjectList = answersToSet
    }

}
