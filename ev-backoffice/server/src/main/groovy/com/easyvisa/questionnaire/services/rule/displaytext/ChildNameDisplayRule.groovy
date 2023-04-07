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
 * SubSection: Children Information
 * Question: 1. (Q_2742) Is [insert this child's name] applying with the Beneficiary, [insert Beneficiary Name]?
 *           2. (Q_2755) Does [insert this child's name] have a Social Security Number?
 *           3. (Q_2757) Does [insert this child's name] have a USCIS ELIS Account Number?
 *           4. (Q_2758) What is [insert this child's name]'s USCIS ELIS Account Number?
 *           5. (Q_2760) What is [insert this child's name]'s Alien Registration Number (A-number)?
 *           6. (Q_2762) Does [insert this child's name] live with the Beneficiary,  [insert Beneficiary Name]?
 *           7. (Q_2763) Please provide [insert this child's name] complete address information
 *           8. (Q_2764) In what country does [insert this child's name] live?
 *           9. (Q_2765) What is [insert this child's name]'s Street Number and Name?
 */
@Component
class ChildNameDisplayRule implements IDisplayTextRule {

    private static String RULE_NAME = 'ChildNameDisplayRule'

    private static String CHILD_NAME_PLACEHOLDER = "\\[insert this child's name\\]";
    private static String BENEFICIARY_NAME_PLACEHOLDER = "\\[insert Beneficiary Name\\]";

    // Given Name (First name)
    private static String CHILD_NAME_FIELD_PATH = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743";

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

        String iteratedChildNameFieldPath = CHILD_NAME_FIELD_PATH +"/"+ questionNodeInstance.repeatingIndex;
        Answer childFirstNameAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.getPackageId(),
                ruleEvaluationContext.getApplicantId(), iteratedChildNameFieldPath)
        if(Answer.isValidAnswer(childFirstNameAnswer)){
             updatedDisplayText = updatedDisplayText.replaceAll(CHILD_NAME_PLACEHOLDER, childFirstNameAnswer.getValue());
        }

        Package packageObj = Package.get(ruleEvaluationContext.packageId)
        Applicant beneficiary = packageObj.getPrincipalBeneficiary();
        updatedDisplayText = updatedDisplayText.replaceAll(BENEFICIARY_NAME_PLACEHOLDER, beneficiary.name);
        return updatedDisplayText
    }
}
