package com.easyvisa.questionnaire.services.rule.sectioncompletion

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmployeeStatus
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
import com.easyvisa.utils.DateUtils
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate
import java.util.stream.Collectors

@Slf4j
@Component
class EmploymentHistoryCompletionRule implements ISectionCompletionRule {

    private static String RULE_NAME = 'EmploymentHistoryCompletionRule'

    private static int TOTAL_DURATION_IN_YEARS = 5
    private static int ACCEPTABLE_GAP_IN_DAYS = 30
    private static String NO_ERROR = ''

    private static String PETITIONER_SECTION = "Sec_employmentHistory"
    private static String BENEFICIARY_SECTION = "Sec_employmentHistoryForBeneficiary"

    private static String WARNING_POPUP_MESSAGE_5_YEARS = "questionnaire.employment.history.completion.five.years.incomplete"
    private static String WARNING_POPUP_MESSAGE_30_DAYS = "questionnaire.employment.history.completion.gap.more.than.thirty.days"
    private static String WARNING_POPUP_MESSAGE_START_END_DATE = "questionnaire.employment.history.completion.missing.start.end.date"


    private static String LEFT_BUTTON_LABEL = "Go back and edit Employment History Section"
    private static String RIGHT_BUTTON_LABEL = "Continue and leave the Employment History Section"

    private static String CLIENTNAME_PLACEHOLDER = 'clientName'
    private String warningAttorneyMessage = '/email/internal/packageWarningEmploymentAttorney'
    private String warningClientMessage = '/email/internal/packageWarningEmploymentClient'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    SpringSecurityService springSecurityService

    AlertService alertService
    AsyncService asyncService

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerScetionCompletionRules(RULE_NAME, this)
    }


    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {


        String errorMessage = validateEmploymentHistoryCompletion(ruleEvaluationContext)

        // If there is no error message then answer is complete and return true else return false
        boolean retVal = (errorMessage) ? false : true
        return retVal

    }

    @Override
    void updatedDependentSectionCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {

    }

    @Override
    CompletionWarningDto generateCompletionWarning(NodeRuleEvaluationContext ruleEvaluationContext) {

        String errorMessage = validateEmploymentHistoryCompletion(ruleEvaluationContext)

        // Show error message only for 5 years and 30 day gap
        if (errorMessage && errorMessage!=WARNING_POPUP_MESSAGE_START_END_DATE) {

            Long currentUserId = springSecurityService.currentUserId as Long
            asyncService.runAsync({
                createPackageWarning(ruleEvaluationContext, currentUserId)
            }, "Send Employment History Incomplete for Package [${ruleEvaluationContext.packageId}] and Applicant [${ruleEvaluationContext.applicantId}]")

            return new CompletionWarningDto(errorMessage, LEFT_BUTTON_LABEL, RIGHT_BUTTON_LABEL)
        }
        return new CompletionWarningDto()
    }

    /**
     * Approach: Split Data Completion, 5 year and Acceptable Gap check - each returns a boolean false for failure
     * Common method fetches relevant data, and calls each of the above checks.
     * Based on the boolean response this method returns appropriate MessageId
     */
    private String validateEmploymentHistoryCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        SectionNodeInstance sectionNodeInstance = (SectionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        SubSectionNodeInstance subSectionNodeInstance = this.getEmploymentStatusSubSectionInstance(sectionNodeInstance)

        if (!subSectionNodeInstance) {
            return NO_ERROR
        }
        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = this.findRepeatingQuestionGroupNodeInstance(subSectionNodeInstance)

        if (!repeatingQuestionGroupNodeInstance) {
            return NO_ERROR
        }

        Map<String, String> employmentStatusFieldPaths = (sectionNodeInstance.id == PETITIONER_SECTION) ? this.getPetitionerEmploymentStatusFieldPaths() : this.getBeneficiaryEmploymentStatusFieldPaths()

        List dateStruct = collectDatesForAnswers(ruleEvaluationContext, repeatingQuestionGroupNodeInstance, employmentStatusFieldPaths)


        // Check if all data is complete
        boolean allDataFilled = areEmploymentDatesFilledInAllIterations(dateStruct)
        if (!allDataFilled) {
            return WARNING_POPUP_MESSAGE_START_END_DATE

        }
        // check if history has 5 years data
        boolean has5YearsData = doesEmploymentHistoryHave5YearsData(dateStruct, ruleEvaluationContext.currentDate)
        if (!has5YearsData) {
            return WARNING_POPUP_MESSAGE_5_YEARS
        }

        boolean hasAcceptableGapBetweenDates = hasNoGapGreaterThan30Days(dateStruct)
        if (!hasAcceptableGapBetweenDates) {
            return WARNING_POPUP_MESSAGE_30_DAYS
        }

        return NO_ERROR
    }

    private List collectDatesForAnswers(NodeRuleEvaluationContext ruleEvaluationContext,
                                        RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance,
                                        Map employmentStatusFieldPaths) {

        Integer totalRepeatCount = repeatingQuestionGroupNodeInstance.getTotalRepeatCount() - 1// It's not zero based..

        List answerStruct = []
        // Collect a structure of start and end dates
        (0..totalRepeatCount).each {
            int repeatIterationCount = it
            // Get status, one of Employed/Unemployed/Retrired
            String EMPLOYMENT_STATUS_FIELD = "${employmentStatusFieldPaths.EMPLOYMENT_STATUS_FIELD_PATH}/${repeatIterationCount}"
            Answer employmentStatusAnswer = ruleEvaluationContext.findAnswerByPath(EMPLOYMENT_STATUS_FIELD)

            if (Answer.isValidAnswer(employmentStatusAnswer)) {
                String normStatus = EasyVisaNode.normalizeAnswer(employmentStatusAnswer?.value)
                // Collect only if there's a status
                // Unwrap list of date fields
                def (EMPLOYMENT_START_DATE_FIELD, EMPLOYMENT_END_DATE_FIELD, EMPLOYMENT_STILL_WORKING_FIELD) = findEmploymentStartDatePathFromCurrentIteration(employmentStatusAnswer,
                        employmentStatusFieldPaths,
                        repeatIterationCount)

                Answer employmentStartDateAnswer = ruleEvaluationContext.findAnswerByPath(EMPLOYMENT_START_DATE_FIELD)
                // Convert start date to LocalDate for easy comparing
                LocalDate empStartDate = Answer.isValidAnswer(employmentStartDateAnswer)
                        ? DateUtil.localDate(DateUtil.normalizeEasyVisaDateFormat(employmentStartDateAnswer.value))
                        : null

                
                Answer employmentEndDateAnswer = ruleEvaluationContext.findAnswerByPath(EMPLOYMENT_END_DATE_FIELD)
                LocalDate empEndDate = Answer.isValidAnswer(employmentEndDateAnswer)
                        ? DateUtil.localDate(DateUtil.normalizeEasyVisaDateFormat(employmentEndDateAnswer.value))
                        : null

                // For status = Employed, if still working is true then end date is captured in a different field
                Answer employmentStillWorking
                String normStillWorking

                // There will be a value for stillWorking if status is EMPLOYED
                // What matters is the YES value because then endDate will be null
                if (normStatus == RelationshipTypeConstants.EMPLOYED.value) {
                    employmentStillWorking = ruleEvaluationContext.findAnswerByPath(EMPLOYMENT_STILL_WORKING_FIELD)
                    if (Answer.isValidAnswer(employmentStillWorking)) {
                        normStillWorking = EasyVisaNode.normalizeAnswer(employmentStillWorking?.value)

                    }
                }

                // We have all the required values - Add them to the list
                Map tempStruct = [
                        "iteration"   : repeatIterationCount, // capturing iteration to differentiate first (current employment) as this will never have an end date
                        "status"      : EasyVisaNode.normalizeAnswer(employmentStatusAnswer?.value),
                        "startDate"   : empStartDate,
                        "stillWorking": normStillWorking,
                        "endDate"     : empEndDate
                ]

                answerStruct << tempStruct
            }
        }
        if (log.isDebugEnabled()) log.debug("Unsorted Date Structure dump: ${answerStruct.dump()}")
        // sort on start date
        answerStruct.sort { a, b ->
            a.startDate <=> b.startDate
        }

        return answerStruct
    }

    /**
     * Check if start date and end dates are filled in for all previous employments
     * Does Current employment (Iteration 0) have an end date ?
     * For previous employment, Q1015 - Are you still working for this employer will determine if last date has a date value or a string "TO PRESENT"
     *
     *
     * @param ruleEvaluationContext
     * @return
     */
    private boolean areEmploymentDatesFilledInAllIterations(List dateStruct) {

        // Iterate over the dateStruct and ensure every start and endDate has value
        // with the exception of current Employment and continued employment

        // dateStruct is ordered chronologically by startDate
        boolean notAllFilled = dateStruct.any { Map entry ->
            // No end date in the first iteration (Current Employment)
            // And also when Employed and Still Working
            boolean isEmployedAndStillWorking = (entry.stillWorking == RelationshipTypeConstants.YES.value && entry.status == RelationshipTypeConstants.EMPLOYED.value)
            boolean isNotCurrentEmployment = (entry.iteration > 0)
            if (isNotCurrentEmployment && !isEmployedAndStillWorking) {
                boolean both = !(entry.startDate && entry.endDate)
                return both
            } else {
                boolean one = !(entry.startDate)
                return one
            }
        }

        return !notAllFilled
    }


    // Check if any start date is less than (current date - 5 years)
    // Idea is to ensure there is a record for atleast 5 years, irrespective of the status (Employed, Unemployed or Retired)

    private boolean doesEmploymentHistoryHave5YearsData(List dateStruct, LocalDate currentDate) {

        boolean hasHistoryForMoreThan5Years = dateStruct.any { Map entry ->
            return DateUtil.isExceedTheNumberOfYears(entry.startDate, currentDate, TOTAL_DURATION_IN_YEARS)
        }
        return hasHistoryForMoreThan5Years
    }

    // Check if any gap between start and previous end is greater than 30 days
    private boolean hasNoGapGreaterThan30Days(List dateStruct) {
        int structSize = dateStruct.size()
        boolean hasNoGap = true
        for (int ii = 0; ii < structSize - 1; ii++) {
            // compare currentEndDate with next startDate
            // First iteration is for the current employment which will not have an end date
            boolean isEmployedAndStillWorking = (dateStruct[ii].stillWorking == RelationshipTypeConstants.YES.value && dateStruct[ii].status == RelationshipTypeConstants.EMPLOYED.value)
            boolean isNotCurrentEmployment = (dateStruct[ii].iteration > 0)
            if (isNotCurrentEmployment && !isEmployedAndStillWorking) {
                int dayDiff = DateUtil.daysBetween(dateStruct[ii].endDate, dateStruct[ii + 1].startDate)
                if (dayDiff > ACCEPTABLE_GAP_IN_DAYS) {
                    if (log.isDebugEnabled()) log.debug("Iteration#${dateStruct[ii].iteration}: Days between ${curEndDate} and ${dateStruct[ii + 1].startDate}= ${dayDiff}")
                    hasNoGap = false
                    break
                }
            }
        }

        return hasNoGap
    }


    public SubSectionNodeInstance getEmploymentStatusSubSectionInstance(SectionNodeInstance sectionNodeInstance) {
        String employmentStatusId = (sectionNodeInstance.id == PETITIONER_SECTION) ? "SubSec_employmentStatus" : "SubSec_employmentStatusForBeneficiary"
        SubSectionNodeInstance subSectionNodeInstance = (SubSectionNodeInstance) sectionNodeInstance.getChildren().stream()
                .filter({ easyVisaNodeInstance -> easyVisaNodeInstance.id == employmentStatusId })
                .findFirst().orElse(null)
        return subSectionNodeInstance
    }


    public RepeatingQuestionGroupNodeInstance findRepeatingQuestionGroupNodeInstance(SubSectionNodeInstance subSectionNodeInstance) {
        RepeatingQuestionGroupNodeInstance emptyRepeatingQuestionGroupNodeInstance = null
        List<RepeatingQuestionGroupNodeInstance> repeatingQuestionGroupNodeInstanceList = subSectionNodeInstance.getChildren().stream()
                .filter({ nodeInstance -> (nodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        return repeatingQuestionGroupNodeInstanceList.size() ? repeatingQuestionGroupNodeInstanceList[0] : emptyRepeatingQuestionGroupNodeInstance
    }

    private List findEmploymentStartDatePathFromCurrentIteration(Answer employmentStatusAnswer,
                                                                 def employmentStatusFieldPaths, int repeatIterationCount) {
        String EMPLOYMENT_START_DATE_FIELD = null, EMPLOYMENT_END_DATE_FIELD = null
        String EMPLOYMENT_STILL_WORKING_FIELD = null

        String employmentStatusAnswerValue = EasyVisaNode.normalizeAnswer(employmentStatusAnswer.getValue())
        if (employmentStatusAnswerValue == RelationshipTypeConstants.EMPLOYED.value) {
            // Employed
            EMPLOYMENT_START_DATE_FIELD = "${employmentStatusFieldPaths.EMPLOYED_START_DATE_PATH}/${repeatIterationCount}"
            EMPLOYMENT_END_DATE_FIELD = "${employmentStatusFieldPaths.EMPLOYED_END_DATE_PATH}/${repeatIterationCount}"
            EMPLOYMENT_STILL_WORKING_FIELD = "${employmentStatusFieldPaths.STILL_WORKING_AT_THIS_EMPLOYER_PATH}/${repeatIterationCount}"
            /*EMPLOYMENT_END_DATE_FIELD_2 = "${employmentStatusFieldPaths.EMPLOYED_END_DATE_PATH_2}/${repeatIterationCount}"*/

        } else if (employmentStatusAnswerValue == RelationshipTypeConstants.UNEMPLOYED.value) {
            // Unemployed
            EMPLOYMENT_START_DATE_FIELD = "${employmentStatusFieldPaths.UNEMPLOYMENT_START_DATE_PATH}/${repeatIterationCount}"
            EMPLOYMENT_END_DATE_FIELD = "${employmentStatusFieldPaths.UNEMPLOYMENT_END_DATE_PATH}/${repeatIterationCount}"

        } else if (employmentStatusAnswerValue == RelationshipTypeConstants.RETIRED.value) {
            // Retired
            EMPLOYMENT_START_DATE_FIELD = "${employmentStatusFieldPaths.RETIRED_START_DATE_PATH}/${repeatIterationCount}"
            EMPLOYMENT_END_DATE_FIELD = "${employmentStatusFieldPaths.RETIRED_END_DATE_PATH}/${repeatIterationCount}"

        }
        return [EMPLOYMENT_START_DATE_FIELD, EMPLOYMENT_END_DATE_FIELD, EMPLOYMENT_STILL_WORKING_FIELD]
    }

    def getPetitionerEmploymentStatusFieldPaths() {
        return [
                'EMPLOYMENT_STATUS_FIELD_PATH'       : "Sec_employmentHistory/SubSec_employmentStatus/Q_1008",
                'UNEMPLOYMENT_START_DATE_PATH'       : "Sec_employmentHistory/SubSec_employmentStatus/Q_1009",
                'UNEMPLOYMENT_END_DATE_PATH'         : "Sec_employmentHistory/SubSec_employmentStatus/Q_1010",
                'RETIRED_START_DATE_PATH'            : "Sec_employmentHistory/SubSec_employmentStatus/Q_1011",
                'RETIRED_END_DATE_PATH'              : "Sec_employmentHistory/SubSec_employmentStatus/Q_1012",
                'EMPLOYED_START_DATE_PATH'           : "Sec_employmentHistory/SubSec_employmentStatus/Q_1014",
                'STILL_WORKING_AT_THIS_EMPLOYER_PATH': "Sec_employmentHistory/SubSec_employmentStatus/Q_1015",
                'EMPLOYED_END_DATE_PATH'             : "Sec_employmentHistory/SubSec_employmentStatus/Q_1016",
                //'EMPLOYED_END_DATE_PATH_2'           : "Sec_employmentHistory/SubSec_employmentStatus/Q_1017" //is valid only if "Q_1015" is TRUE.. Here treat current date as answer
        ]
    }


    def getBeneficiaryEmploymentStatusFieldPaths() {
        return [
                'EMPLOYMENT_STATUS_FIELD_PATH'       : "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2608",
                'UNEMPLOYMENT_START_DATE_PATH'       : "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2609",
                'UNEMPLOYMENT_END_DATE_PATH'         : "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2610",
                'RETIRED_START_DATE_PATH'            : "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2611",
                'RETIRED_END_DATE_PATH'              : "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2612",
                'EMPLOYED_START_DATE_PATH'           : "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2614",
                'STILL_WORKING_AT_THIS_EMPLOYER_PATH': "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2615",
                'EMPLOYED_END_DATE_PATH'             : "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2616",
                //'EMPLOYED_END_DATE_PATH_2'           : "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2617"// is valid only if "Q_2615" is TRUE.. Here treat current date as answer
        ]
    }

    @Transactional
    void createPackageWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext, Long currentUserId) {
        EasyVisaSystemMessageType warningMessageType = EasyVisaSystemMessageType.INCOMPLETE_EMPLOYMENTHISTORY_WARNING
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        Warning warning = Warning.findByAPackageAndApplicantAndSubject(packageObj, applicant, warningMessageType.subject)
        if (warning == null) {
            String warningMessage
            User currentUser = User.get(currentUserId)
            if (currentUser.isRepresentative()) {
                String currentUserName = currentUser.profile.getFullName()
                warningMessage = alertService.renderTemplate(warningAttorneyMessage, [(CLIENTNAME_PLACEHOLDER): currentUserName])
            } else {
                Applicant petitionerApplicant = packageObj.client
                warningMessage = alertService.renderTemplate(warningClientMessage, [(CLIENTNAME_PLACEHOLDER): petitionerApplicant.getName()])
            }
            alertService.createPackageWarning(packageObj, applicant, warningMessageType, warningMessage)
        }
    }
}
