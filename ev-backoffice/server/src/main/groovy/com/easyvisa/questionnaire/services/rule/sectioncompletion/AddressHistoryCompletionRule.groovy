package com.easyvisa.questionnaire.services.rule.sectioncompletion

import com.easyvisa.Package
import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.PackageService
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.ISectionCompletionRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.SectionVisibilityRuleEvaluationContext
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.services.rule.impl.PreviousPhysicalAddressPopulateRule
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.util.DateUtil
import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.Duration
import java.time.LocalDate

/**
 * Section: Address History
 * Applicant: Petitioner / Beneficiary
 *
 * Notes:  This rule validates whether user has entered the 5 years of address history data or not.
 *  (i.e) This calculates the sum of all the days in the following 2 items,
 *        then check the total number of days against 5 years
 *  1. Current Physical Address - days range between move-in date and the current-date
 *  2. Previous Physical Address - In each iteration, sum the days range between move-in and the move-out dates
 *
 *
 * This rule also has the responsible to show the completion warning,
 * If user entered values in 'Address History' does not match the minimum requirement. (i.e) not have 5 years of data
 * */

@GrailsCompileStatic
@Component
class AddressHistoryCompletionRule implements ISectionCompletionRule {

    private static String RULE_NAME = 'AddressHistoryCompletionRule'

    private static int TOTAL_DURATION_IN_YEARS = 5
    private static int ACCEPTABLE_GAP_IN_DAYS = 30
    private static String NO_ERROR = ''

    private static String WARNING_POPUP_MESSAGE_5_YEARS = "questionnaire.address.history.completion.five.years.incomplete"
    private static String WARNING_POPUP_MESSAGE_30_DAYS = "questionnaire.address.history.completion.gap.more.than.thirty.days"
    private static String WARNING_POPUP_MESSAGE_MOVE_IN_OUT_DATE = "questionnaire.address.history.completion.missing.movein.moveout.date"

    private static String LEFT_BUTTON_LABEL = "Go back and edit 'Address History' Section"
    private static String RIGHT_BUTTON_LABEL = "Continue and leave the 'Address History' Section"

    PackageService packageService
    PackageQuestionnaireService packageQuestionnaireService

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @Autowired
    PreviousPhysicalAddressPopulateRule previousPhysicalAddressPopulateRule

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerScetionCompletionRules(RULE_NAME, this)
    }

    @CompileStatic
    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {

        // Idea is to not make duplicate DB calls, once data is fetched, reuse it.
        String errorMessage = validateAddressHistoryCompletion(ruleEvaluationContext)

        // If there is no error message then answer is complete and return true else return false
        // We do not need exact error here
        boolean retVal = (errorMessage==NO_ERROR) ? true : false
        return retVal

    }


    @Override
    void updatedDependentSectionCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {

    }


    /**
     *
     * This method only gets called, if user navigates from Questionnaire (Address History section) to other pages
     * We should validate this rule only if the move-in date of current address is AFTER the date of conditional permanent residence began
     * HHere we need to validate that history of Previous Physical Addresses is till the date that your Conditional Permanent Residence began (green card).
     */
    @Override
    CompletionWarningDto generateCompletionWarning(NodeRuleEvaluationContext ruleEvaluationContext) {
        String errorMessage = validateAddressHistoryCompletion(ruleEvaluationContext)
        // Do not show warning for incomplete dates
        if (errorMessage && errorMessage != WARNING_POPUP_MESSAGE_MOVE_IN_OUT_DATE) {
            return new CompletionWarningDto(errorMessage, LEFT_BUTTON_LABEL, RIGHT_BUTTON_LABEL)

        }
        return new CompletionWarningDto()
    }


    // Petitoner: Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52,Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64,Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65|SubSec_currentPhysicalAddress|Form_129F,Form_130
    // Benefiiary: Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2012,Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2050,Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2052|SubSec_currentPhysicalAddressForBeneficiary|Form_129F,Form_130A,Form_485
    private String validateAddressHistoryCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        if (!canValidateAddressHistoryFor5YearsData(ruleEvaluationContext)) {
            return NO_ERROR
        }

        SectionNodeInstance sectionNodeInstance = (SectionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String[] sectionCompletionRuleParams = sectionNodeInstance.getSectionCompletionRuleParam().split("\\|")
        String ADDRESS_QUESTIONS_PATH = sectionCompletionRuleParams[0]

        String[] addressQuestionPathParams = ADDRESS_QUESTIONS_PATH.split(",")
        String CURRENT_ADDRESS_MOVE_INTO_PATH = addressQuestionPathParams[0]
        String PREVIOUS_ADDRESS_MOVE_INTO_PATH = addressQuestionPathParams[1]
        String PREVIOUS_ADDRESS_MOVE_OUTOF_PATH = addressQuestionPathParams[2]

        Answer currentAddressMoveIntoAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, CURRENT_ADDRESS_MOVE_INTO_PATH)
        List<Answer> previousAddressMoveIntoAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, "${PREVIOUS_ADDRESS_MOVE_INTO_PATH}%")
        List<Answer> previousAddressMoveOutOfAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, "${PREVIOUS_ADDRESS_MOVE_OUTOF_PATH}%")


        // Validate complete date pairs
        if (!isDatePairComplete(previousAddressMoveIntoAnswerList, previousAddressMoveOutOfAnswerList) || !Answer.isValidAnswer(currentAddressMoveIntoAnswer)) {
            return WARNING_POPUP_MESSAGE_MOVE_IN_OUT_DATE
        }

        // Validate 5 year history
        // check it only when Form 751 is not Included in this Benefit Category (Remove Cond)
        // EV-3325
        if (!isIncludedForm751(ruleEvaluationContext)) {

            int numberOfDaysInCurrentAddress = this.inBetweenDaysFromToday(currentAddressMoveIntoAnswer, ruleEvaluationContext.currentDate)

            if (!isComplete5YearHistory(previousAddressMoveIntoAnswerList, previousAddressMoveOutOfAnswerList, numberOfDaysInCurrentAddress)) {
                return WARNING_POPUP_MESSAGE_5_YEARS
            }
        }

        // Validate gap not more than 30 days
        if (isGapMoreThan30Days(currentAddressMoveIntoAnswer, previousAddressMoveIntoAnswerList, previousAddressMoveOutOfAnswerList)) {
            return WARNING_POPUP_MESSAGE_30_DAYS
        }
        // All good, no error
        return NO_ERROR

    }


    private boolean isIncludedForm751(NodeRuleEvaluationContext nodeRuleEvaluationContext) {

        Package aPackage = Package.get(nodeRuleEvaluationContext.packageId)
        ImmigrationBenefitCategory directBenefitCategory = aPackage.directBenefit.category
        String benefitCategoryId = directBenefitCategory.getEasyVisaId()

        SectionNodeInstance sectionNodeInstance = (SectionNodeInstance) nodeRuleEvaluationContext.easyVisaNodeInstance
        SectionVisibilityRuleEvaluationContext sectionVisibilityRuleEvaluationContext = new SectionVisibilityRuleEvaluationContext(sectionNodeInstance,
                nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, benefitCategoryId)

        if (packageQuestionnaireService.isSectionIncluded(sectionVisibilityRuleEvaluationContext, PdfForm.I751)) {
            return true
        }
        return false

    }

    @CompileStatic
    private boolean isDatePairComplete(List<Answer> moveInDates, List<Answer> moveOutDates) {
        // size has to be the same for both the lists
        // no other check required

        if ((moveInDates?.size() == moveOutDates?.size())
                && moveInDates.every { it.value }
                && moveOutDates.every { it.value })
            return true

        return false

    }

    @CompileStatic
    private boolean isComplete5YearHistory(List<Answer> moveInDates, List<Answer> moveOutDates, int numberOfDaysInCurrentAddress) {

        int numberOfDaysFromPreviousAddressHistory = this.findTheNumberOfDaysFromAddressHistory(moveInDates, moveOutDates)
        int totalNumberOfDaysFromAddressHistory = numberOfDaysInCurrentAddress + numberOfDaysFromPreviousAddressHistory
        Duration DURATION_IN_YEARS = Duration.ofDays(TOTAL_DURATION_IN_YEARS * 365)

        return (totalNumberOfDaysFromAddressHistory >= DURATION_IN_YEARS.toDays())

    }

    @CompileStatic
    private boolean isGapMoreThan30Days(Answer currentAddressMoveinIntoAnswer, List<Answer> previousAddressMoveIntoAnswerList,
                                        List<Answer> previousAddressMoveOutOfAnswerList) {

        // First sort previousAddressMoveIntoAddressAnswerList into reverse chronological order
        previousAddressMoveIntoAnswerList.sort { a, b -> DateUtil.normalizeEasyVisaDateFormat(b.getValue()) <=> DateUtil.normalizeEasyVisaDateFormat(a.getValue()) }

        int sizeOfMoveInList = previousAddressMoveIntoAnswerList.size()

        // No previous history
        if (sizeOfMoveInList <= 0) return false

        // Check Current Movein with previous Address MoveOut
        // We get the previous moveout based on the index of the  first item in previousAddressMoveIntoAddressAnswerList
        int indexAtLastMoveInDate = previousAddressMoveIntoAnswerList[0]?.index

        // get Moveout date to compare with movin date of current address
        Answer lastMoveOutDate = previousAddressMoveOutOfAnswerList.find { it.index == indexAtLastMoveInDate }
        int dayDiff = (int) inBetweenDays(lastMoveOutDate, currentAddressMoveinIntoAnswer)

        if (dayDiff > ACCEPTABLE_GAP_IN_DAYS) return true

        // Iterate over the previous address history list
        if (sizeOfMoveInList > 1) {

            for (int ii = 0; ii < sizeOfMoveInList - 1; ii++) {
                Answer moveIn = previousAddressMoveIntoAnswerList[ii]

                // Return if we hit the end of the list
                if (ii == sizeOfMoveInList) return false
                // get Index of the prior record
                int indexOfPriorRecord = previousAddressMoveIntoAnswerList[ii + 1]?.index
                Answer previousMoveOut = previousAddressMoveOutOfAnswerList.find { it.index == indexOfPriorRecord }
                dayDiff = (int) inBetweenDays(previousMoveOut, moveIn)
                if (dayDiff > 30) return true
            }
        }
        return false

    }

    @CompileStatic
    private int findTheNumberOfDaysFromAddressHistory(List<Answer> previousAddressMoveIntoAnswerList,
                                                      List<Answer> previousAddressMoveOutOfAnswerList) {
        int totalNumberOfDaysFromAddressHistory = 0
        int iterationAnswerCount = previousAddressMoveIntoAnswerList.size() - 1
        (0..iterationAnswerCount).each {
            int repeatIterationCount = it
            Answer previousAddressMoveIntoAnswer = previousAddressMoveIntoAnswerList.find { answer -> answer.index == repeatIterationCount }
            Answer previousAddressMoveOutOfAnswer = previousAddressMoveOutOfAnswerList.find { answer -> answer.index == repeatIterationCount }
            if (Answer.isValidAnswer(previousAddressMoveIntoAnswer) && Answer.isValidAnswer(previousAddressMoveOutOfAnswer)) {
                totalNumberOfDaysFromAddressHistory += (int) this.inBetweenDays(previousAddressMoveIntoAnswer, previousAddressMoveOutOfAnswer)
            }
        }
        return totalNumberOfDaysFromAddressHistory
    }

    @CompileStatic
    private Long inBetweenDays(Answer startDateAnswer, Answer endDateAnswer) {
        String startDateAsString = DateUtil.normalizeEasyVisaDateFormat(startDateAnswer.getValue())
        String endDateAsString = DateUtil.normalizeEasyVisaDateFormat(endDateAnswer.getValue())
        LocalDate startDate = DateUtil.localDate(startDateAsString)
        LocalDate endDate = DateUtil.localDate(endDateAsString)
        return DateUtil.daysBetween(startDate, endDate)
    }

    @CompileStatic
    private int inBetweenDaysFromToday(Answer startDateAnswer, LocalDate currentDate) {
        if (!Answer.isValidAnswer(startDateAnswer)) {
            return 0
        }
        String startDateAsString = DateUtil.normalizeEasyVisaDateFormat(startDateAnswer.getValue())
        LocalDate startDate = DateUtil.localDate(startDateAsString)
        return DateUtil.daysBetween(startDate, currentDate)
    }

    /**
     * No need to check 5 years of address history data for the following condition
     *
     * Need this validation only if the Question: When did you move into this address? in 'Current Physical Address' subsection exists
     * as well as 'Previous Physical Address(es) within 5 Years' subsection exists.
     * These 2 subsections exists only for the following Forms
     * Petitioner: Form_129F & Form_130
     * Beneficiary: Form_129F, Form_130A & Form_485
     *
     * So through this method, I am checking the triggering move-in question should exists in the given Forms (passed via rule params)
     * */
    @CompileStatic
    private Boolean canValidateAddressHistoryFor5YearsData(NodeRuleEvaluationContext ruleEvaluationContext) {
        SectionNodeInstance sectionNodeInstance = (SectionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String[] sectionCompletionRuleParams = sectionNodeInstance.getSectionCompletionRuleParam().split("\\|")
        String ADDRESS_QUESTIONS_PATH = sectionCompletionRuleParams[0]
        String currentPhysicalAddressSubSecId = sectionCompletionRuleParams[1]
        String VALIDATION_INCLUDED_FORMS = sectionCompletionRuleParams[2]

        String[] addressQuestionPathParams = ADDRESS_QUESTIONS_PATH.split(",")
        String CURRENT_ADDRESS_MOVE_INTO_PATH = addressQuestionPathParams[0]

        SubSectionNodeInstance subSectionNodeInstance = sectionNodeInstance.getChildren().find { it.getId() == currentPhysicalAddressSubSecId } as SubSectionNodeInstance
        QuestionNodeInstance currentAddressModeInQuestion = subSectionNodeInstance?.getChildren()?.find {
            QuestionNodeInstance questionNodeInstance = it as QuestionNodeInstance
            questionNodeInstance.getAnswer().getPath() == CURRENT_ADDRESS_MOVE_INTO_PATH
        } as QuestionNodeInstance
        if (!currentAddressModeInQuestion) {
            return false;
        }

        List<PdfForm> addressValidationIncludedForms = VALIDATION_INCLUDED_FORMS.split(",")
                .collect { PdfForm.getByFormId(it) }.toList()
        NodeRuleEvaluationContext moveInQuestionRuleEvaluationContext = new NodeRuleEvaluationContext(ruleEvaluationContext.answerList, currentAddressModeInQuestion,
                ruleEvaluationContext.packageId, ruleEvaluationContext.applicantId)
        return packageQuestionnaireService.isQuestionIncludedInAnyForm(moveInQuestionRuleEvaluationContext, addressValidationIncludedForms)
    }
}
