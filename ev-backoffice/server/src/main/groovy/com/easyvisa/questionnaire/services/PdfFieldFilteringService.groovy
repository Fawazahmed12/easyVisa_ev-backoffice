package com.easyvisa.questionnaire.services


import com.easyvisa.Package
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import org.springframework.stereotype.Service

/**
 *
 * This service is used to filter the user answers based on form-864
 *
 * In Family-Information section of applicant-type: Beneficiary, We need to include the children-information and current-spouse details
 * to form-print, only if user has answered child/spouse question as "YES".
 * Exclude all the answers from children-information and current-spouse subsection, if user has unasnswered or 'NO' to the child/spouse question
 * In Employment history we need to keep only current employment information used in Form 864. Remove all non-current
 * employment.
 *
 *
 * Section: Family Information
 * SubSection: SubSec_childrenInformationForBeneficiary
 * Question: (Q_2742) Is [insert this child's name] applying with the Beneficiary, [insert Beneficiary Name]?
 *
 * Section: Family Information
 * SubSection: SubSec_currentSpouseForBeneficiary
 * Question: (Q_2788) Is [insert Beneficiary Name]'s spouse applying with the [insert Beneficiary Name]?
 */

@Service
class PdfFieldFilteringService {
    // Is [insert this child's name] applying with the Beneficiary, [insert Beneficiary Name]?
    private static String CHILD_AS_DERIVATIVE_QUESTIONID = "Q_2742"

    // Is [insert Beneficiary Name]'s spouse applying with the [insert Beneficiary Name]?
    private static String SPOUSE_DERIVATIVE_QUESTIONID = "Q_2788"

    private static String STILL_WORKING_AT_THIS_EMPLOYER = "Q_1015"
    private static String CURRENT_EMPLOYMENT_STATUS = "Q_1008"
    private static String EMPLOYMENT_END_DATE = "Q_1016"


    public validateDerivativeInclusion(Package aPackage, Long applicantId, String formId,
                                       List<PdfFieldDetail> pdfFieldDetailList) {
        if (PdfForm.I864.getFormId() == formId) {
            this.validateSpouseDerivativeInclusion(pdfFieldDetailList)
            this.validateChildrenDerivativeInclusion(pdfFieldDetailList)
            validateCurrentEmployment(pdfFieldDetailList)
        }
    }

    /**
     *
     * In Current Employment subsection, remove all the non-current answers from this subsection
     * Get answer to question with ID Q_1015 (STILL_WORKING_AT_THIS_EMPLOYER)
     * removing answer DOES NOT remove them for all forms
     * answers are fetched for every printing each form - so this filtering does not impact any other form
     * Answers to remove 1008, 1009, 1011, 1013, 1019, 1029
     *
     * I am currently:
     * * Employed as (Q_1008)
     *      Employer 1 (Q_1019)
     *      Employer 2 (Q_1019)
     * * Self-Employed as (Q_1013)
     *      Occupation (Q_1029)
     * * Retired since (Q_1008)
     *      Date (start Q_1011, end Q_1012)
     * * Unemployed since (Q_1008)
     *      Date (start Q_1009, end Q_1010)
     *
     *  For form 864, printing is a little tricky
     * *    (Employed, Self-Employed), Retired, Unemployed are mutually exclusive
     * *    First one should always be current
     * *        - Unemployed or Retired should be determined by first iteration
     * *    Filter out all non-current employment
     * *        - Iteration > 1
     * *        - Status is employed (Q_1008)
     * *        - Still working is No (Q_1015)
     * *
     * *
     */
    private validateCurrentEmployment(List<PdfFieldDetail> pdfFieldDetailList) {

        PdfFieldDetail currentEmpStatus = pdfFieldDetailList.find { it.questionId == CURRENT_EMPLOYMENT_STATUS }
        PdfFieldDetail stillWorking = pdfFieldDetailList.find { it.questionId == STILL_WORKING_AT_THIS_EMPLOYER }

        //End date is not populated for the first iteration AND also where its not answered
        // Its not even shown for the first iteration
        // PdfFieldDetail empEndDate = pdfFieldDetailList.find { it.questionId == EMPLOYMENT_END_DATE }

        // Get size answerValueObjectList from currentEmpStatus
        // Only first value should determine Retired/Unemployed for current Employment Status
        // Subsequent iterations that contain Retired/unemployed should be deleted
        // In addition, status values as employed with answer to "Are you still working" as NO should also be deleted
        int numIterations = currentEmpStatus?.answerValueObjectList?.size()?:0
        List questionsToRemove = []

        //Skipping the first iteration for current Employment
        if (numIterations > 1) {
            // remove answers from Questions 1008, 1009, 1011, 1013, 1019, 1029
            List quesToFilterList = pdfFieldDetailList.findAll {
                ['Q_1008', 'Q_1009', 'Q_1011', 'Q_1011', 'Q_1013', 'Q_1019', 'Q_1029'].contains(it.questionId)

            }

            // If the first iteration is unemployed/retired remove the rest
            AnswerValueObject firstAnswer = currentEmpStatus?.answerValueObjectList?.find {
                it.index == 0 && it.value in ['unemployed', 'retired']
            }
            if (firstAnswer) {
                // Remove rest of the iterations since retired/unemployed is incompatible with employed
                // and we are not interested in history for 864
                filterAnswerObjectList(quesToFilterList, null, [0] as Set)

            } else {
                // First iteration is not unemployed/retired
                // Subsequent Unemployed/Retired should be removed as they are not current and not relevant for 864

                // Get index of  !employed where index is >0
                Set foi = currentEmpStatus?.answerValueObjectList?.findAll {
                    it.index > 0 && it.value != 'employed'
                }*.index

                // filter out answers where still working is no
                foi << stillWorking?.answerValueObjectList?.findAll {
                    it.index > 0 && it.value == 'no'
                }*.index

                Set filterOutIndex = foi.flatten() as Set

                // Remove rest of the iterations since retired/unemployed is incompatible with employed
                filterAnswerObjectList(quesToFilterList, filterOutIndex)
            }

        }
    }

    private void filterAnswerObjectList(List<PdfFieldDetail> quesToFilterList, Set removeIndexes = null, Set removeAllExcept = null) {


        if (removeAllExcept) {
            quesToFilterList?.each { questObj ->
                // get AnswerListObject
                questObj?.answerValueObjectList?.removeAll {
                    !removeAllExcept?.contains(it.index)
                }
            }
        } else {
            quesToFilterList?.each { questObj ->
                // get AnswerListObject
                questObj?.answerValueObjectList?.removeAll {
                    removeIndexes?.contains(it.index)
                }
            }

        }
    }

    /**
     *
     * In Current-Spouse subsection, if user has answered 'NO' or unanswered the need to remove all the other answers from this subsection
     * For example, if we would not remove these answers, then it will be available in I-130 form,
     * as it means that an applicant addded his Spouse as derivative beneficiary but in reality applicant not added
     */
    private validateSpouseDerivativeInclusion(List<PdfFieldDetail> pdfFieldDetailList) {
        PdfFieldDetail spousePdfFieldDetail = pdfFieldDetailList.find { it.questionId == SPOUSE_DERIVATIVE_QUESTIONID };
        String answerValue = spousePdfFieldDetail?.answerValueObjectList?.first()?.value
        if (answerValue == "yes") {
            return pdfFieldDetailList;
        }

        List<String> spouseQuestionFields = [];
        (2789..2816).each { i ->
            String questionId = "Q_" + i;
            spouseQuestionFields.add(questionId);
        }
        List<PdfFieldDetail> currentSpousePdfFieldDetailList = pdfFieldDetailList.findAll {
            return spouseQuestionFields.contains(it.questionId);
        }
        pdfFieldDetailList.removeAll(currentSpousePdfFieldDetailList);
    }


    /**
     * In Children-Information subsection, we are displaying questions in RepeatingQuestion group
     * Here we need to print child information data, only if an applicant has answered 'Yes' to the question
     * So here all other answers from remaining repeating question group should be removed
     *
     * Here important one is need to update an repeating index of an exitsing repeating group answers and its path
     */
    private validateChildrenDerivativeInclusion(List<PdfFieldDetail> pdfFieldDetailList) {
        PdfFieldDetail childPdfFieldDetail = pdfFieldDetailList.find { it.questionId == CHILD_AS_DERIVATIVE_QUESTIONID };
        if (!childPdfFieldDetail || childPdfFieldDetail.answerValueObjectList?.size() == 0) {
            return pdfFieldDetailList;
        }

        List<Integer> yesAnsweredIndexes = [];
        childPdfFieldDetail.answerValueObjectList.each {
            String answerValue = it.value
            if (answerValue == "yes") {
                Integer iterationIndex = it.index;
                yesAnsweredIndexes.add(iterationIndex)
            }
        }

        List<String> childQuestionFields = [];
        (2743..2773).each { i ->
            String questionId = "Q_" + i;
            childQuestionFields.add(questionId);
        }

        List<PdfFieldDetail> currentSpousePdfFieldDetailList = pdfFieldDetailList.findAll {
            return childQuestionFields.contains(it.questionId);
        }

        currentSpousePdfFieldDetailList.each { PdfFieldDetail pdfFieldDetail ->
            List<AnswerValueObject> invalidAnswerValueObject = pdfFieldDetail.answerValueObjectList?.findAll {
                return !yesAnsweredIndexes.contains(it.index);
            }
            if (invalidAnswerValueObject.size() != 0) {
                pdfFieldDetail.answerValueObjectList.removeAll(invalidAnswerValueObject);
            }
        }

        // Update all indexes
        List<AnswerValueObject> yesAnsweredValueObjectList = childPdfFieldDetail.answerValueObjectList.findAll { it.value == "yes" };
        yesAnsweredValueObjectList.eachWithIndex { AnswerValueObject answerValueObject, Integer iterationIndex ->
            answerValueObject.setIndex(iterationIndex)
            List answerPathParts = [answerValueObject.sectionId, answerValueObject.subsectionId, answerValueObject.questionId, iterationIndex]
            answerValueObject.setPath(answerPathParts.join('/'))
        }
        currentSpousePdfFieldDetailList.each {
            it.answerValueObjectList?.eachWithIndex { AnswerValueObject answerValueObject, Integer iterationIndex ->
                answerValueObject.setIndex(iterationIndex)
                List answerPathParts = [answerValueObject.sectionId, answerValueObject.subsectionId, answerValueObject.questionId, iterationIndex]
                answerValueObject.setPath(answerPathParts.join('/'))
            }
        }

        List<PdfFieldDetail> emptyAnswerFields = currentSpousePdfFieldDetailList.findAll {
            return !it.answerValueObjectList || it.answerValueObjectList.size() == 0;
        }
        if (emptyAnswerFields.size() != 0) {
            pdfFieldDetailList.removeAll(emptyAnswerFields);
        }
    }
}
