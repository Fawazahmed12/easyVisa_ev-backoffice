package com.easyvisa.questionnaire.services.rule.attribute

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section: Personal Information
 * RepeatingQuestionGroup: (RQG_personelInformationForBeneficiary) Personal Information
 * Notes:
 *
 * if USER is applying for the EAD (Employment Authorization Document) category, and also he is  a citizen or national of more than one country,
 * enter the name of the foreign country that issued your last passport into the second iteration.
 *
 * if USER is applying for OTHER category,  (i.e) not a EAD, then he can enter only ONE country of citizenship or nationality.
 * */

@Component
class PersonelInformationAddButtonConstraintRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'PersonelInformationAddButtonConstraintRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId);
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)

        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Map attributes = repeatingQuestionGroupNodeInstance.getAttributes();
        if (immigrationBenefit.category == ImmigrationBenefitCategory.EAD) {
            Integer totalRepeatCount = repeatingQuestionGroupNodeInstance.getTotalRepeatCount();
            Integer answerIndex = repeatingQuestionGroupNodeInstance.getAnswerIndex(); //zero based value
            Integer currentIterationValue = answerIndex + 1;
            attributes[TemplateOptionAttributes.ADDREPEATINGBUTTON.getValue()] = (currentIterationValue == totalRepeatCount);
            attributes[TemplateOptionAttributes.REMOVEREPEATINGBUTTON.getValue()] = (totalRepeatCount != 1);
        } else {
            attributes[TemplateOptionAttributes.ADDREPEATINGBUTTON.getValue()] = false;
            attributes[TemplateOptionAttributes.REMOVEREPEATINGBUTTON.getValue()] = false;
        }
    }
}
