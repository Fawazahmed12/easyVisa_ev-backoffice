package com.easyvisa.questionnaire.services.rule.displaytext

import com.easyvisa.Applicant
import com.easyvisa.Package
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDisplayTextRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section: Family Information
 * SubSection: Current Spouse
 * Question: 1. (Q_2788) Is [insert Beneficiary Name]'s spouse applying with the [insert Beneficiary Name]?
 *           2. (Q_2797) Does [insert this spouse's name] have a USCIS ELIS Account Number?
 *           3. (Q_2798) What is [insert this spouse's name]'s USCIS ELIS Account Number?
 *           4. (Q_2799) Does [insert this spouse's name] have an A-Number (Alien Registration Number)?
 *           5. (Q_2800) What is [insert this spouse's name]'s Alien Registration Number (A-number)?
 *
 */
@Component
class SpouseNameDisplayRule implements IDisplayTextRule  {

    private static String RULE_NAME = 'SpouseNameDisplayRule'

    private static String SPOUSE_NAME_PLACEHOLDER = "\\[insert this spouse's name\\]";
    private static String BENEFICIARY_NAME_PLACEHOLDER = "\\[insert Beneficiary Name\\]";

    // Given Name (First name)
    private static String SPOUSE_NAME_FIELD_PATH = "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2789";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDisplayTextRule(RULE_NAME, this);
    }

    @Override
    String generateDisplayText(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String displayText = questionNodeInstance.getDisplayText();
        String updatedDisplayText = displayText;

        Answer currentSpouseNameAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.getPackageId(),
                ruleEvaluationContext.getApplicantId(), SPOUSE_NAME_FIELD_PATH)
        if(Answer.isValidAnswer(currentSpouseNameAnswer)){
            updatedDisplayText = updatedDisplayText.replaceAll(SPOUSE_NAME_PLACEHOLDER, currentSpouseNameAnswer.getValue());
        }

        Package packageObj = Package.get(ruleEvaluationContext.packageId)
        Applicant beneficiary = packageObj.getPrincipalBeneficiary();
        updatedDisplayText = updatedDisplayText.replaceAll(BENEFICIARY_NAME_PLACEHOLDER, beneficiary.name);
        return updatedDisplayText
    }
}
