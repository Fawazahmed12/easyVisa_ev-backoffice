package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.repositories.QuestionDAO
import com.easyvisa.questionnaire.services.QuestionnaireService
import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *   Apply this rule to 'Question' called 'What is your current marital status?'.
 *   If user selected 'Married' OR 'Legally Separated' option and the Immigration Benefit category is K-1/K-3,
 *   then a Warning message goes to the attorney's Warnings tab in the Task Queue.
 *
 *   This rule will check the  answers to the following question from 'Marital Status' subsection
 *    a.   What is your current marital status? -> 'Married' OR 'Legally Separated'
 * This rule will generate two labels 'yes' and 'no'. If the above condition are true then the result is 'yes'.
 */

/**
 * Section: Sec_familyInformationForBeneficiary
 * SubSection: SubSec_maritalStatusForBeneficiary
 * Question: 1. (Q_2781) What is your current marital status?
 * RuleParam: Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781,RQG_priorSpousesForBeneficiary,SubSec_priorSpouses
 */
@CompileStatic
@Component
class BeneficiaryMaritalStatusAttorneyActionRule extends BaseComputeRule {

    private static String RULE_NAME = "BeneficiaryMaritalStatusAttorneyActionRule"

    private String legallySeparatedWithK1WarningTemplate = '/email/internal/packageWarningLegallySeparatedWithK1'
    private String legallySeparatedWithOtherWarningTemplate = '/email/internal/packageWarningLegallySeparatedWithOther'
    private String marriageAnnulledWarningTemplate = '/email/internal/packageWarningMarriageAnnulledAll'
    private String maritalStatusWith130WarningTemplate = '/email/internal/packageWarningMaritalStatusWith130'
    private String legallySeparatedWith134WarningTemplate = '/email/internal/packageWarningLegallySeparatedWith134'
    private String marriageAnnulledWith134WarningTemplate = '/email/internal/packageWarningMarriageAnnulledWith134'
    private String maritalStatusWith485WarningTemplate = '/email/internal/packageWarningMaritalStatusWith485'
    private String principalBeneficiaryRelationshipWarningTemplate = '/email/internal/packageWarningOtherThanMarried'

    private static String CLIENTNAME1_PLACEHOLDER = 'clientName'
    private static String PETITIONER_NAME_PLACEHOLDER = 'petitionerName'
    private static String BOX_PLACEHOLDER = 'textBoxValue'

    private static String LEGALLYSEPERATED_K1_CATEGORY = 'LegallySeparatedK1Category'
    private static String LEGALLYSEPERATED_OTHER_CATEGORY = 'LegallySeparatedOtherCategory'
    private static String LEGALLYSEPERATED_MARRIAGEANULLED_130_FORM = 'LegallySeparatedMarriageAnulled130Form'
    private static String LEGALLYSEPERATED_MARRIAGEANULLED_485_FORM = 'LegallySeparatedMarriageAnulled485Form'
    private static String LEGALLYSEPERATED_134_FORM = 'LegallySeparated134Form'
    private static String MARRIAGEANULLED_134_FORM = 'MarriageAnulled134Form'
    private static String MARRIAGEANULLED = 'MarriageAnulled'

    private static String PRINCIPAL_BENEFICIARY_RELATED_TO_YOU_PATH = "Sec_1/SubSec_4/Q_27"
    private static String MARRIED = "married"
    private static String SPOUSE = "spouse"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry
    AsyncService asyncService

    @Autowired
    QuestionDAO questionDAO

    @Autowired
    private QuestionnaireService questionnaireService

    AnswerService answerService
    PackageQuestionnaireService packageQuestionnaireService
    AlertService alertService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        String maritalStatusFieldPath = getMartialStatusFieldPath(nodeRuleEvaluationContext)
        Answer maritalStatusAnswer = nodeRuleEvaluationContext.findAnswerByPath(maritalStatusFieldPath)
        if (Answer.isValidAnswer(maritalStatusAnswer)) {
            return new Outcome(maritalStatusAnswer.getValue(), true)
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false)
    }

    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        this.addPriorSpousesIfApplicable(nodeRuleEvaluationContext)
        this.sendMaritalStatusWarning(nodeRuleEvaluationContext, previousAnswer)
        this.sendPrincipalBeneficiaryRelationshipWarning(nodeRuleEvaluationContext)
    }

    private void sendPrincipalBeneficiaryRelationshipWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (evaluatePrincipalBeneficiaryRelationshipWithPetitioner(nodeRuleEvaluationContext)) {
            asyncService.runAsync({
                createPackageWarning(principalBeneficiaryRelationshipWarningTemplate, nodeRuleEvaluationContext)
            }, "Send Warning for Principal Beneficiary Relationship With Petitioner (BeneficiaryMaritalStatusAttorneyActionRule)")
        }
    }


    private void sendMaritalStatusWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        String maritalStatusFieldPath = getMartialStatusFieldPath(nodeRuleEvaluationContext)
        Answer maritalStatusAnswer = getAnswerByQuestionPath(nodeRuleEvaluationContext.getPackageId(), nodeRuleEvaluationContext.getApplicantId(), maritalStatusFieldPath)
        String warningType = findPackageWarningType(maritalStatusAnswer, nodeRuleEvaluationContext)
        asyncService.runAsync({
            createWarning(warningType, nodeRuleEvaluationContext)
        }, "Send Package [${nodeRuleEvaluationContext.packageId}] warning (BeneficiaryMaritalStatusAttorneyActionRule) for Answer(s) ${nodeRuleEvaluationContext.answerList*.id} and previous Answer [${previousAnswer?.id}] of Applicant [${nodeRuleEvaluationContext.applicantId}]")
    }


    private void addPriorSpousesIfApplicable(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluatePriorSpousesDataInsertRule(nodeRuleEvaluationContext)) {
            this.addDependentDefaultRepeatingGroupIfRequired(nodeRuleEvaluationContext)
        }
    }

    // If the user(s) selected other than 'Married' to the Question(Q_2781: What is your current marital status?) AND
    // the user had answered 'Spouse' to the Petitioner question 'How is the Beneficiary related to you?' (currently in cell D55),
    // then send this WARNING to the attorney:
    //      Your client [Principle Beneficiary First Name Principle Beneficiary Last Name] stated that they are currently not married,
    //      however, in the Petitioner section of the Questionnaire, the Petitioner [Petitioner First Name Petitioner Last Name]'s response
    //      to the question  'How is the Beneficiary related to you?' (currently in cell D55) was 'Spouse'. You may want to contact
    //      your clients to verify the Petitioner and Principle Beneficiary's marital statuses.
    private Boolean evaluatePrincipalBeneficiaryRelationshipWithPetitioner(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package aPackage = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = aPackage.getClient()
        Answer principalBeneficiaryRelationshipAnswer = getAnswerByQuestionPath(aPackage.id, applicant.id, PRINCIPAL_BENEFICIARY_RELATED_TO_YOU_PATH)
        if (!Answer.isValidAnswer(principalBeneficiaryRelationshipAnswer)) {
            return false
        }

        String maritalStatusFieldPath = getMartialStatusFieldPath(nodeRuleEvaluationContext)
        Answer maritalStatusAnswer = getAnswerByQuestionPath(nodeRuleEvaluationContext.getPackageId(), nodeRuleEvaluationContext.getApplicantId(), maritalStatusFieldPath)
        Boolean isQuestionIncludedIn130Form = this.packageQuestionnaireService.isQuestionIncluded(nodeRuleEvaluationContext, PdfForm.I130)
        Boolean isPrincipalBeneficiaryRelationshipAsSpouse = principalBeneficiaryRelationshipAnswer.getValue() == SPOUSE
        Boolean isMaritalStatusNotMarried = Answer.isValidAnswer(maritalStatusAnswer) && maritalStatusAnswer.getValue() != MARRIED
        return (isQuestionIncludedIn130Form && isPrincipalBeneficiaryRelationshipAsSpouse && isMaritalStatusNotMarried)
    }


    private void createWarning(String warningType, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        switch (warningType) {

            case LEGALLYSEPERATED_K1_CATEGORY:
                this.createPackageWarning(legallySeparatedWithK1WarningTemplate, nodeRuleEvaluationContext)
                break

            case LEGALLYSEPERATED_MARRIAGEANULLED_130_FORM:
                this.createPackageWarning(maritalStatusWith130WarningTemplate, nodeRuleEvaluationContext)
                break

            case LEGALLYSEPERATED_MARRIAGEANULLED_485_FORM:
                this.createPackageWarning(maritalStatusWith485WarningTemplate, nodeRuleEvaluationContext)
                break

            case LEGALLYSEPERATED_134_FORM:
                this.createPackageWarning(legallySeparatedWith134WarningTemplate, nodeRuleEvaluationContext)
                break

            case LEGALLYSEPERATED_OTHER_CATEGORY:
                this.createPackageWarning(legallySeparatedWithOtherWarningTemplate, nodeRuleEvaluationContext)
                break

            case MARRIAGEANULLED_134_FORM:
                this.createPackageWarning(marriageAnnulledWith134WarningTemplate, nodeRuleEvaluationContext)
                break

            case MARRIAGEANULLED:
                this.createPackageWarning(marriageAnnulledWarningTemplate, nodeRuleEvaluationContext)
                break
        }
    }

    private String findPackageWarningType(Answer maritalStatusAnswer, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluateRuleByLegallySeparatedInForm134(nodeRuleEvaluationContext, maritalStatusAnswer)) {
            return LEGALLYSEPERATED_K1_CATEGORY
        } else if (this.evaluateRuleByLegallySeparatedOrMarriageAnnulledInForm130(nodeRuleEvaluationContext, maritalStatusAnswer)) {
            return LEGALLYSEPERATED_MARRIAGEANULLED_130_FORM
        } else if (this.evaluateRuleByLegallySeparatedOrMarriageAnnulledInForm485(nodeRuleEvaluationContext, maritalStatusAnswer)) {
            return LEGALLYSEPERATED_MARRIAGEANULLED_485_FORM
        } else if (this.evaluateRuleByLegallySeparatedForm134(nodeRuleEvaluationContext, maritalStatusAnswer)) {
            return LEGALLYSEPERATED_134_FORM
        } else if (this.evaluateRuleByLegallySeparatedOtherCategory(nodeRuleEvaluationContext, maritalStatusAnswer)) {
            return LEGALLYSEPERATED_OTHER_CATEGORY
        } else if (this.evaluateRuleByMarriageAnnulledForm134(nodeRuleEvaluationContext, maritalStatusAnswer)) {
            return MARRIAGEANULLED_134_FORM
        } else if (this.evaluateRuleByMarriageAnnulled(maritalStatusAnswer)) {
            return MARRIAGEANULLED
        } else {
            return ""
        }
    }

    // Form-134 (Legally Separated): If the user selected this response 'Legally Separated' to the question 'What is your current marital status?', then send this Warning to the attorney:
    private Boolean evaluateRuleByLegallySeparatedInForm134(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer maritalStatusAnswer) {
        Boolean isQuestionIncludedInForm134 = this.packageQuestionnaireService.isQuestionIncluded(nodeRuleEvaluationContext, PdfForm.I134)
        return (isQuestionIncludedInForm134 && maritalStatusAnswer.doesMatch(RelationshipTypeConstants.LEGALLY_SEPERATED.value))
    }

    private Boolean evaluateRuleByLegallySeparatedOtherCategory(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer maritalStatusAnswer) {
        Boolean isQuestionIncludedInForm129F = this.packageQuestionnaireService.isQuestionIncluded(nodeRuleEvaluationContext, PdfForm.I129F)
        return (!isQuestionIncludedInForm129F && maritalStatusAnswer.doesMatch(RelationshipTypeConstants.LEGALLY_SEPERATED.value))
    }

    private Boolean evaluateRuleByMarriageAnnulled(Answer maritalStatusAnswer) {
        return Answer.isValidAnswer(maritalStatusAnswer) && maritalStatusAnswer.doesMatch(RelationshipTypeConstants.MARRIAGE_ANULLED.value)
    }

    // Form-130 (Legally Separated/ Marriage Annulled)
    private Boolean evaluateRuleByLegallySeparatedOrMarriageAnnulledInForm130(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer maritalStatusAnswer) {
        Boolean isQuestionIncludedInForm130 = this.packageQuestionnaireService.isQuestionIncluded(nodeRuleEvaluationContext, PdfForm.I130)
        String[] relationshipTypeConstantValues = [
                RelationshipTypeConstants.LEGALLY_SEPERATED.value,
                RelationshipTypeConstants.MARRIAGE_ANULLED.value
        ]
        return (isQuestionIncludedInForm130 && maritalStatusAnswerValueCheck(maritalStatusAnswer, relationshipTypeConstantValues))
    }

    // Form-485 (Legally Separated/ Marriage Annulled)
    private Boolean evaluateRuleByLegallySeparatedOrMarriageAnnulledInForm485(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer maritalStatusAnswer) {
        Boolean isQuestionIncludedInForm485 = this.packageQuestionnaireService.isQuestionIncluded(nodeRuleEvaluationContext, PdfForm.I485)
        String[] relationshipTypeConstantValues = [
                RelationshipTypeConstants.LEGALLY_SEPERATED.value,
                RelationshipTypeConstants.MARRIAGE_ANULLED.value
        ]
        return (isQuestionIncludedInForm485 && maritalStatusAnswerValueCheck(maritalStatusAnswer, relationshipTypeConstantValues))
    }

    private Boolean evaluateRuleByLegallySeparatedForm134(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer maritalStatusAnswer) {
        Boolean isQuestionIncludedInForm134 = this.packageQuestionnaireService.isQuestionIncluded(nodeRuleEvaluationContext, PdfForm.I134)
        return (isQuestionIncludedInForm134 && maritalStatusAnswer.doesMatch(RelationshipTypeConstants.LEGALLY_SEPERATED.value))
    }

    private Boolean evaluateRuleByMarriageAnnulledForm134(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer maritalStatusAnswer) {
        Boolean isQuestionIncludedInForm134 = this.packageQuestionnaireService.isQuestionIncluded(nodeRuleEvaluationContext, PdfForm.I134)
        return (isQuestionIncludedInForm134 && maritalStatusAnswer.doesMatch(RelationshipTypeConstants.MARRIAGE_ANULLED.value))
    }

    private Boolean maritalStatusAnswerValueCheck(Answer maritalStatusAnswer, String[] relationshipTypeConstantValues) {
        String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue())
        return relationshipTypeConstantValues.contains(maritalStatusAnswerValue)
    }

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    private Answer getAnswerByQuestionPath(Long packageId, Long applicantId, String questionFieldPath) {
        return Answer.findByPackageIdAndApplicantIdAndPathIlike(packageId, applicantId, questionFieldPath)
    }

    private String getMartialStatusFieldPath(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam()
        String[] ruleParams = ruleParam.split(",")
        return ruleParams[0]
    }

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    protected Boolean evaluatePriorSpousesDataInsertRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        String maritalStatusFieldPath = getMartialStatusFieldPath(nodeRuleEvaluationContext)
        List<Answer> maritalStatusAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), maritalStatusFieldPath)
        if (maritalStatusAnswerList.isEmpty()) {
            return false
        }

        Answer maritalStatusAnswer = maritalStatusAnswerList[0]
        String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue())
        String[] priorSpouseTriggeringAnswers = [
                RelationshipTypeConstants.DIVORCED.value,
                RelationshipTypeConstants.WIDOWED.value,
                RelationshipTypeConstants.MARRIAGE_ANULLED.value
        ]
        return priorSpouseTriggeringAnswers.contains(maritalStatusAnswerValue)
    }

    private void createPackageWarning(String templateMessage, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        InputSourceType inputSourceType = questionnaireService.getInputSourceType(questionNodeInstance.inputTypeSource, questionNodeInstance.questVersion, questionNodeInstance.displayTextLanguage)
        String readableAnswer = inputSourceType?.values.find { it.value == questionNodeInstance.answer.value }?.value
        String warningMessage
        if (templateMessage == principalBeneficiaryRelationshipWarningTemplate) {
            warningMessage = constructPrincipalBeneficiaryRelationshipWarningMessage(templateMessage, packageObj.principalBeneficiary, packageObj.petitioner)
        } else {
            warningMessage = constructWarningMessage(templateMessage, applicant, readableAnswer)
        }
        alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                warningMessage, questionNodeInstance.id, questionNodeInstance.answer)
    }

    private void addDependentDefaultRepeatingGroupIfRequired(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam()
        String[] ruleParams = ruleParam.split(",")
        String maritalStatusFieldPath = ruleParams[0]
        String repeatingGroupId = ruleParams[1]
        String subsectionId = ruleParams[2]
        String sectionId = maritalStatusFieldPath.split("/")[0]
        answerService.addDefaultRepeatingGroupIfRequired(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId,
                sectionId, subsectionId, repeatingGroupId, nodeRuleEvaluationContext.currentDate)
    }

    private String constructWarningMessage(String templateMessage, Applicant applicant, String answer) {
        return alertService.renderTemplate(templateMessage, [(CLIENTNAME1_PLACEHOLDER): applicant.getName(),
                                                             (BOX_PLACEHOLDER)        : answer])
    }

    private String constructPrincipalBeneficiaryRelationshipWarningMessage(String templateMessage, Applicant applicant, Petitioner petitioner) {
        return alertService.renderTemplate(templateMessage, [(CLIENTNAME1_PLACEHOLDER)    : applicant.getName(),
                                                             (PETITIONER_NAME_PLACEHOLDER): petitioner.getName()])
    }
}
