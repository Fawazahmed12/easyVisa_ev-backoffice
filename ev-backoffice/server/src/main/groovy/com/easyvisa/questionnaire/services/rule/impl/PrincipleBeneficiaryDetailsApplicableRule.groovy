package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class PrincipleBeneficiaryDetailsApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "PrincipleBeneficiaryDetailsApplicableRule"

    //I-864
    //If user answered 'Yes', then no further questions appear on this page (section).
    //If user said 'No', then generate subsections called 'SubSec_nameOfPrincipalBeneficiary','SubSec_mailingAddressOfPrincipalBeneficiary','SubSec_otherInformationOfPrincipalBeneficiary','SubSec_derivativeBeneficiariesOfPrincipalBeneficiary' within the section 'Sec_principleAndTheirDerivativeBeneficiaries’.
    //Question: Were you (or are you) the Principle Beneficiary of the visa petition that was/(is being) filed on your behalf?
    private static String werePrincipleBeneficiaryPetitionFiledOnYourBehalf_FIELD_PATH = "Sec_principleAndTheirDerivativeBeneficiaries/SubSec_principleAndTheirDerivativeBeneficiariesFromInitialVisaPetition/Q_6063"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return this.evaluatePrincipleBeneficiaryDetailsApplicableRule(nodeRuleEvaluationContext);
    }

    //Do you intend to make specific contributions to support of the beneficiary (and their derivative beneficiairies, if there are any) in this application?
    private Boolean evaluatePrincipleBeneficiaryDetailsApplicableRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Answer werePrincipleBeneficiaryPetitionFiledOnYourBehalfAnswer = Answer.findByPackageIdAndApplicantIdAndPath(nodeRuleEvaluationContext.packageId,
                nodeRuleEvaluationContext.applicantId, werePrincipleBeneficiaryPetitionFiledOnYourBehalf_FIELD_PATH)
        if (!Answer.isValidAnswer(werePrincipleBeneficiaryPetitionFiledOnYourBehalfAnswer)) {
            return false
        }

        String werePrincipleBeneficiaryPetitionFiledOnYourBehalfAnswerValue = EasyVisaNode.normalizeAnswer(werePrincipleBeneficiaryPetitionFiledOnYourBehalfAnswer.getValue())
        if (werePrincipleBeneficiaryPetitionFiledOnYourBehalfAnswerValue == RelationshipTypeConstants.NO.value) {
            return true
        }
        return false
    }
}
