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
 * This rule is used to split the answer value using a delim
 * and populate multiple pdf fields from split values
 * Chars after | will be ignored - This is used in case of 765 form where categories have same keys
 *
 *
 * */
@Component
class splitUseOrIgnoreRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "splitUseOrIgnoreRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {

        String[] paramsArray = splitParamsValue(params)
        String delim = paramsArray[0]

        String ignoreAfterDelim = "\\|"

        List<AnswerValueObject> answersToSet = []

        pdfFieldDetail.answerValueObjectList.each {
            String ansStr = it.value
            // remove values after ignoreAfterDelim
            String useAnsStr = ansStr.split(ignoreAfterDelim)[0]

            String[] ansStrArr = useAnsStr.split(delim)

            ansStrArr.eachWithIndex{ String entry, int i ->
                AnswerValueObject valueToSet = it.copy()
                valueToSet.value = ansStrArr[i]
                valueToSet.index = i
                answersToSet << valueToSet
            }

        }
        pdfFieldDetail.answerValueObjectList = answersToSet
    }

}
