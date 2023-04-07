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
class ExtremeHardshipStatementApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "ExtremeHardshipStatementApplicableRule"

    //I-601
    //If user answered 'No', then no further questions appear on this page (section).
    //If user said 'Yes', then generate subsection called 'Extreme Hardship Statement' within the section 'Statement from Applicant’.
    //Question: Do you have a spouse, parent, child, fianc$e$(e), or child of a fianc$e$(e) who either has or will experience 'extreme hardship' in the future if you are not granted a waiver of your inadmissibility to the United States? This person MUST be either a U.S. citizen or  LPR (Lawful Permanent Resident).
    private static String doYouHaveASpouseParentChildFianceOrChildOfAFiance_FIELD_PATH = "Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3602"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return this.evaluateRelativesWhoWillExperienceExtremeHardshipApplicableRule(nodeRuleEvaluationContext);
    }

    //Do you intend to make specific contributions to support of the beneficiary (and their derivative beneficiairies, if there are any) in this application?
    private Boolean evaluateRelativesWhoWillExperienceExtremeHardshipApplicableRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Answer doYouHaveASpouseParentChildFianceOrChildOfAFiance = Answer.findByPackageIdAndApplicantIdAndPath(nodeRuleEvaluationContext.packageId,
                nodeRuleEvaluationContext.applicantId, doYouHaveASpouseParentChildFianceOrChildOfAFiance_FIELD_PATH)
        if (!Answer.isValidAnswer(doYouHaveASpouseParentChildFianceOrChildOfAFiance)) {
            return true
        }

        String extremeHardshipAnswerValue = EasyVisaNode.normalizeAnswer(doYouHaveASpouseParentChildFianceOrChildOfAFiance.getValue())
        if (extremeHardshipAnswerValue == RelationshipTypeConstants.YES.value) {
            return true
        }
        return false
    }
}
