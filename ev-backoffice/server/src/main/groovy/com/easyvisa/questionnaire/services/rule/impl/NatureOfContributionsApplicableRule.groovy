package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.PackageService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Support & Contributions
 * SubSection:  Nature of Contributions
 *
 * Notes: This subsection (and its questions) ONLY appears if the user answered "Yes" to the question
 *  "Do you intend to make specific contributions to support of the beneficiary (and their derivative beneficiairies, if there are any)? "
 * */
@Component
class NatureOfContributionsApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "NatureOfContributionsApplicableRule"
    private static String CONTRIBUTION_SUPPORT_FIELD_PATH = "Sec_supportAndContributions/SubSec_supportAndContributions/Q_1402"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    PackageService packageService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return this.evaluateNatureOfContributionsApplicableRule(nodeRuleEvaluationContext);
    }


    //Do you intend to make specific contributions to support of the beneficiary (and their derivative beneficiairies, if there are any) in this application?
    private Boolean evaluateNatureOfContributionsApplicableRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Answer specificContributionsSupportBeneficiary = nodeRuleEvaluationContext.findAnswerByPath(CONTRIBUTION_SUPPORT_FIELD_PATH)
        if (!Answer.isValidAnswer(specificContributionsSupportBeneficiary)) {
            return false
        }

        String specificContributionsSupportBeneficiaryAnswerValue = EasyVisaNode.normalizeAnswer(specificContributionsSupportBeneficiary.getValue())
        if (specificContributionsSupportBeneficiaryAnswerValue == RelationshipTypeConstants.YES.value) {
            return true
        }
        return false
    }
}
