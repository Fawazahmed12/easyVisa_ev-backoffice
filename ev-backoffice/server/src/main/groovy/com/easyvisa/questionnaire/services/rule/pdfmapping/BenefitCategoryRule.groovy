package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.FieldMappingDetail
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import com.easyvisa.utils.PdfUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule populates PDF filed based on benefit category, whether auto population or user answers.
 */
@Component
class BenefitCategoryRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "BenefitCategoryPrintRule"

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
        String[] benefitParams = splitParamsValue(getParam(ruleParams, 0))
        String value = getParam(ruleParams, 1)
        String expressions = getParam(ruleParams, 2)
        Boolean allowed = getCategories(benefitParams, pdfMappingEvaluationContext)
        if (allowed) {
            if (expressions) {
                AnswerValueObject answerValueObject = new AnswerValueObject(new Answer('value': value))
                FieldMappingDetail fieldMappingDetail = new FieldMappingDetail(
                        'fieldType': pdfFieldDetail.fieldMappingDetail.fieldType,
                        'fieldExpressions': [expressions])
                pdfMappingEvaluationContext.addPdfField(new PdfFieldDetail('answerValueObjectList': [answerValueObject],
                        'fieldMappingDetail': fieldMappingDetail))
            }
        } else {
            if (!expressions) {
                pdfFieldDetail.fieldMappingDetail.fieldExpressions = [PdfUtils.AUTO_ID]
            }
        }
    }

    private Boolean getCategories(String[] benefits, PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        Set<ImmigrationBenefitCategory> allowedCategories = []
        benefits.each {
            allowedCategories.add(it as ImmigrationBenefitCategory)
        }
        ImmigrationBenefitCategory currentCategory = pdfMappingEvaluationContext.aPackage.benefits.find {
            it.applicant.id = pdfMappingEvaluationContext.applicantId
        }.category
        allowedCategories.contains(currentCategory)
    }

}
