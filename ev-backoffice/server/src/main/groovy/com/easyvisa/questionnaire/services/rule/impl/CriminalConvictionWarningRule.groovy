package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AlertService
import com.easyvisa.Applicant
import com.easyvisa.AsyncService
import com.easyvisa.Package
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Sec_criminalAndCivilHistory
 * SubSection: SubSec_criminalCivilConvictions
 * Question: (Q_1130) Have you ever been arrested or convicted of homicide, murder, manslaughter, rape, abusive
 *                       sexual contact, sexual exploitation, incest, torture, trafficking, peonage, holding hostage,
 *                       involuntary servitude, slave trade, kidnapping, abduction, unlawful criminal restraint,
 *                       false imprisonment or an attempt to commit any of these crimes?
 */

@Component
class CriminalConvictionWarningRule extends BaseComputeRule {

    private static String RULE_NAME = 'CriminalConvictionWarningRule'

    private static String CLIENTNAME_PLACEHOLDER = 'clientName'
    private static String DAYTIME_PHONENUMBER_PLACEHOLDER = 'daytimePhone'
    private static String MOBILE_PHONENUMBER_PLACEHOLDER = 'mobilePhone'
    private String criminalConvictionWarningTemplate = '/email/internal/packageWarningCriminalConviction'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry
    AsyncService asyncService

    AlertService alertService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluateInAdmissibilityWarningRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false)
    }


    private Boolean evaluateInAdmissibilityWarningRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer criminalConvictionAnswer = questionNodeInstance.getAnswer()
        if (!Answer.isValidAnswer(criminalConvictionAnswer)) {
            return false
        }

        String criminalConvictionAnswerValue = EasyVisaNode.normalizeAnswer(criminalConvictionAnswer.getValue())
        return (criminalConvictionAnswerValue == RelationshipTypeConstants.YES.value)
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext answerContext, Answer previousAnswer) {
        asyncService.runAsync({
            createPackageWarning(answerContext)
        }, "Send Package [${answerContext.packageId}] warning (CriminalConvictionWarningRule) for Answer(s) ${answerContext.answerList*.id} and previous Answer [${previousAnswer?.id}] of Applicant [${answerContext.applicantId}]")
    }

    /**
     *
     * Template placeholders
     * [Insert Client Name]
     * [Insert daytime phone number]
     * [Insert mobile phone number]
     */
    private void createPackageWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String attorneyWarningMessage = constructWarningMessage(packageObj.client)
        alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                attorneyWarningMessage, questionNodeInstance.id, questionNodeInstance.answer)
    }


    private String constructWarningMessage(Applicant applicant) {
        return alertService.renderTemplate(criminalConvictionWarningTemplate, [(CLIENTNAME_PLACEHOLDER):applicant.getName(),
                                                                               (DAYTIME_PHONENUMBER_PLACEHOLDER):applicant.homeNumber,
                                                                               (MOBILE_PHONENUMBER_PLACEHOLDER):applicant.mobileNumber])
    }

}
