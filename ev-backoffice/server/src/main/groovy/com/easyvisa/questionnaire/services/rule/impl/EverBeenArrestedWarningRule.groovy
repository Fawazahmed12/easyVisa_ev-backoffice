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
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
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

@CompileStatic
@Component
class EverBeenArrestedWarningRule extends BaseComputeRule {

    private static String RULE_NAME = 'EverBeenArrestedWarningRule'

    private static String CLIENTNAME_PLACEHOLDER = '\\[Insert client name\\]'

    private static String WARNING_TEMPLATE_Q_6209 = "Your client [Insert client name] answered 'Yes' to the question: '<span class='warning-question'>Have you EVER  been arrested for and/or convicted of any crime?</span>' which may require Special Filing. Follow Instructions for Those With Pending Asylum Applications (c)(8) of the Form I-765 Instructions for information about providing court dispositions."

    private static String WARNING_TEMPLATE_Q_6210 = "Your client [Insert client name] answered 'Yes' to the question: '<span class='warning-question'>Have you EVER  been arrested for and/or convicted of any crime?</span>'. Refer to Employment-Based Nonimmigrant Categories, Items 8. - 9., in the Who May File Form I-765 section of the Form I-765 Instructions for information about providing court dispositions.Follow Instructions for Those With Pending Asylum Applications (c)(8) of the Form I-765 Instructions for information about providing court dispositions.."

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    AlertService alertService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {

        if (this.evaluateArrestedWarningRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false)
    }


    private Boolean evaluateArrestedWarningRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer arrestedAnswer = questionNodeInstance.getAnswer()
        if (!Answer.isValidAnswer(arrestedAnswer)) {
            return false
        }

        String arrestedAnswerValue = EasyVisaNode.normalizeAnswer(arrestedAnswer.getValue())
        return (arrestedAnswerValue == RelationshipTypeConstants.YES.value)
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()

        String template
        if (questionNodeInstance.id == "Q_6209") {
            template = WARNING_TEMPLATE_Q_6209
        } else {
            template = WARNING_TEMPLATE_Q_6210
        }

        this.createPackageWarning(template, alertService, nodeRuleEvaluationContext)
    }

    /**
     *
     * Template placeholders
     * [Insert Client Name]
     * [Insert daytime phone number]
     * [Insert mobile phone number]
     */
    private void createPackageWarning(String templateMessage, AlertService alertService, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant petitionerApplicant = packageObj.client
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId);
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String warningMessage = templateMessage.replaceAll(CLIENTNAME_PLACEHOLDER, petitionerApplicant.getName());
        alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                warningMessage, questionNodeInstance.id, questionNodeInstance.answer)
    }

}
