package com.easyvisa.questionnaire.services.rule.sectioncompletion

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.ISectionCompletionRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.util.DateUtil
import com.sun.org.apache.xpath.internal.operations.Bool
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

@Component
class FamilyInformationCompletionRule implements ISectionCompletionRule {

    private static String RULE_NAME = 'FamilyInformationCompletionRule'

    // Is [insert this child's name] applying with the Beneficiary, [insert Beneficiary Name]?
    private static String CHILD_AS_DERIVATIVE_PATH = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2742";
    // Is [insert Beneficiary Name]'s spouse applying with the [insert Beneficiary Name]?
    private static String SPOUSE_AS_DERIVATIVE_PATH = "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2788";

    private static String WARNING_POPUP_MESSAGE = "You have not reached the allowable number of beneficiaries in this package. You can continue to the the next or previous section or jump to a different section in the Questionnaire or even jump to a different part of EasyVisa. However, the status of the Family Information section in the index panel on the left side of the page will remain unchecked until you have to add all the derivatives data and then you must click either the 'Next' or 'Previous' button.";
    private static String LEFT_BUTTON_LABEL = "Go back and edit Family Information Section";
    private static String RIGHT_BUTTON_LABEL = "Continue and leave the Family Information Section";

    private static String CLIENTNAME_PLACEHOLDER = '\\[client name\\]'
    private
    static String WARNING_ATTORNEY_MESSAGE = "Attorney [client name] left the Employment History of the Questionnaire without covering the prior 5 years of time. You might want to contact your client to ensure that they have covered the appropriate time period and completed this section of the Questionnaire, as well as ensuring that there are no 30 or more gaps between iterations of employment status.";
    static String WARNING_CLIENT_MESSAGE = "Your client [client name] left the Employment History of the Questionnaire without covering the prior 5 years of time. You might want to contact your client to ensure that they have covered the appropriate time period and completed this section of the Questionnaire, as well as ensuring that there are no 30 or more gaps between iterations of employment status.";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    SpringSecurityService springSecurityService;

    AlertService alertService;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerScetionCompletionRules(RULE_NAME, this);
    }

    /**
     *
     * This method only gets called, if all the visible questions in this section has answered...
     * We should validate this rule only if the selected applicant comes under either ( Family Preference - Initial Application & Fiance Visa)
     * Here we need to validate that package derivatives count should be equal to the added derivative beneficiary data of children and spouse in a questionnaire
     */
    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        if(!this.hasApplicantComesUnderDervaitveBeneficiaryCategory(ruleEvaluationContext)) {
            return true;
        }
        boolean hasDerivativeAnswerEqualToPackageDerivatives = this.hasQuestionnaireDerivativesEqualToPackageDerivatives(ruleEvaluationContext);
        return hasDerivativeAnswerEqualToPackageDerivatives;
    }


    @Override
    void updatedDependentSectionCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {

    }


    /**
     *
     * This method only gets called, if user navigates from Questionnaire (Family Information section) to other pages
     * We should validate this rule only if the selected applicant comes under either ( Family Preference - Initial Application & Fiance Visa)
     * Here we need to validate that package derivatives count should be equal to the added derivative beneficiary data of children and spouse in a questionnaire
     */
    @Override
    CompletionWarningDto generateCompletionWarning(NodeRuleEvaluationContext ruleEvaluationContext) {
        if(!this.hasApplicantComesUnderDervaitveBeneficiaryCategory(ruleEvaluationContext)) {
            return new CompletionWarningDto();
        }

        //Display 'warming' popup, only if answer added to children and spouse questions is less than package derivatives count
        boolean hasDerivativeAnswerEqualToPackageDerivatives = this.hasQuestionnaireDerivativesEqualToPackageDerivatives(ruleEvaluationContext);
        if (!hasDerivativeAnswerEqualToPackageDerivatives) {
            // this.createPackageWarning(ruleEvaluationContext);
            return new CompletionWarningDto(WARNING_POPUP_MESSAGE, LEFT_BUTTON_LABEL, RIGHT_BUTTON_LABEL);
        }
        return new CompletionWarningDto();
    }


    //  This method validates that the selected applicant comes under either ( Family Preference - Initial Application & Fiance Visa)
    boolean hasApplicantComesUnderDervaitveBeneficiaryCategory(NodeRuleEvaluationContext ruleEvaluationContext) {
        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        ImmigrationBenefitCategory immigrationBenefit = this.getImmigrationBenefitCategory(aPackage, ruleEvaluationContext);
        List<ImmigrationBenefitCategory> derivativeBenefitCategories = ImmigrationBenefitCategory.getDerivativeBeneficiaryAllowedCategories();
        if(derivativeBenefitCategories.contains(immigrationBenefit)) {
            return true;
        }
        return false;
    }


    //  This method validates the package derivatives count with the added derivative beneficiary data of children and spouse in a questionnaire
    boolean hasQuestionnaireDerivativesEqualToPackageDerivatives(NodeRuleEvaluationContext ruleEvaluationContext) {
        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        List<Answer> selectedChildrenDerivativeAnswerList = this.getSelectedDerivativeAnswerList(SPOUSE_AS_DERIVATIVE_PATH, ruleEvaluationContext);
        List<Answer> selectedSpouseDerivativeAnswerList = this.getSelectedDerivativeAnswerList(CHILD_AS_DERIVATIVE_PATH, ruleEvaluationContext);
        int totalSelectedDerivativesCount = selectedChildrenDerivativeAnswerList.size() + selectedSpouseDerivativeAnswerList.size();
        List<ImmigrationBenefit> derivativeBenefits = aPackage.getDerivativeBenefits()
        boolean hasDerivativeAnswerEqualToPackageDerivatives = totalSelectedDerivativesCount == derivativeBenefits.size();
        return hasDerivativeAnswerEqualToPackageDerivatives;
    }


    private List<Answer> getSelectedDerivativeAnswerList(String derivativeFieldPath, NodeRuleEvaluationContext ruleEvaluationContext) {
        List<Answer> derivativeAnswerList = ruleEvaluationContext.findAnswerListByPathILike(derivativeFieldPath);
        List<Answer> selectedDerivativeAnswerList = derivativeAnswerList.findAll {
            if(!Answer.isValidAnswer(it)) {
                return false;
            }
            String answerValue = EasyVisaNode.normalizeAnswer(it.getValue())
            return (answerValue == RelationshipTypeConstants.YES.value)
        }
        return selectedDerivativeAnswerList;
    }

    private ImmigrationBenefitCategory getImmigrationBenefitCategory(Package aPackage, NodeRuleEvaluationContext ruleEvaluationContext) {
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId);
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        return immigrationBenefit.category;
    }

    @Transactional
    void createPackageWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaSystemMessageType warningMessageType = EasyVisaSystemMessageType.INCOMPLETE_DERIVATIVE_FAMILYINFORMATION_WARNING;
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId);
        Warning warning = Warning.findByAPackageAndApplicantAndSubject(packageObj, applicant, warningMessageType.subject);
        if (warning == null) {
            String warningMessage;
            final User currentUser = springSecurityService.currentUser as User
            if (currentUser.isRepresentative()){
                String currentUserName = currentUser.profile.getFullName();
                warningMessage = WARNING_ATTORNEY_MESSAGE.replaceAll(CLIENTNAME_PLACEHOLDER, currentUserName);
            }else {
                Applicant petitionerApplicant = packageObj.client;
                warningMessage = WARNING_CLIENT_MESSAGE.replaceAll(CLIENTNAME_PLACEHOLDER, petitionerApplicant.getName());
            }
            alertService.createPackageWarning(packageObj, applicant, warningMessageType, warningMessage)
        }
    }
}
