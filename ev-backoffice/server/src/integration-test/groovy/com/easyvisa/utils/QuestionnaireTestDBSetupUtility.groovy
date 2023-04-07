package com.easyvisa.utils

import com.easyvisa.AnswerService
import com.easyvisa.Applicant
import com.easyvisa.Package
import com.easyvisa.SectionCompletionStatusService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.SectionCompletionStatus
import com.easyvisa.questionnaire.model.ApplicantType

class QuestionnaireTestDBSetupUtility {

    static void saveQuestionnaireAnswers(AnswerService answerService, Long packageId, Long applicantId,
                                         String sectionId, List<Answer> answerList, Boolean evaluateRule = true) {
        Answer.withNewTransaction {

            Answer.findAllByPackageIdAndApplicantIdAndSectionId(packageId, applicantId, sectionId)
                    .each { it.delete(flush: true, failOnError: true) }

            List<Answer> validAnswerList = removeDuplicateAnswers(answerList);
            validateAllAnswers(packageId, validAnswerList);
            validAnswerList.each { answer ->
                answerService.saveAnswer(answer, evaluateRule)
            }
        }
    }

    static void validateAllAnswers(Long packageId, List<Answer> validAnswerList) {
        Map<String, ApplicantType> sectionToApplicantTypeMapper = getSectionApplicantTypeMapper();

        Package aPackage = Package.findById(packageId);
        Applicant petitionerApplicant = aPackage.petitioner?.applicant;
        Applicant principalBeneficiary = aPackage.getPrincipalBeneficiary()

        validAnswerList.each {
            String questionFieldPath = it.path;
            List<String> pathInfoList = questionFieldPath.split("/");
            String sectionId = pathInfoList[0];
            Long applicantId = it.applicantId;
            ApplicantType sectionApplicantType = sectionToApplicantTypeMapper[sectionId];

            ApplicantType answerApplicantType = null;
            if (applicantId == petitionerApplicant?.id) {
                answerApplicantType = ApplicantType.Petitioner;
            } else if (applicantId == principalBeneficiary?.id) {
                answerApplicantType = ApplicantType.Beneficiary;
            }

            if (sectionApplicantType != answerApplicantType) {
                throw new UnsupportedOperationException("Unsupported attempt to save #${sectionApplicantType.uiValue} section (${sectionId}) data with invalid applicant");
            }
        }
    }

    private static Map<String, ApplicantType> getSectionApplicantTypeMapper() {
        Map<String, ApplicantType> sectionToApplicantTypeMapper = new HashMap<>();

        sectionToApplicantTypeMapper.put("Sec_1", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_2", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_addressHistory", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_contactInformation", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_birthInformation", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_biographicInformation", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_legalStatusInUS", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_incomeHistory", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_assets", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_employmentHistory", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_criminalAndCivilHistory", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_familyInformation", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_relationshipToPetitioner", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_supportAndContributions", ApplicantType.Petitioner);
        sectionToApplicantTypeMapper.put("Sec_jointSponsor1IncomeAndAssets", ApplicantType.Petitioner);

        sectionToApplicantTypeMapper.put("Sec_introQuestionsForBeneficiary", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_sponsoringPetitioner", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_basisPetitionToRemoveConditionsOnResidence", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_principleAndTheirDerivativeBeneficiaries", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_uscisLocationInformation", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_nameForBeneficiary", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_addressHistoryForBeneficiary", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_contactInformationForBeneficiary", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_birthInformationForBeneficiary", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_biographicInformationForBeneficiary", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_personelInformationForBeneficiary", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_incomeHistoryAndFeesPaid", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_employmentHistoryForBeneficiary", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_informationAboutEligibilityCategory", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_familyInformationForBeneficiary", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_travelToTheUnitedStates", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_inadmissibilityAndOtherLegalIssues", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_publicAssistance", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_extremeHardshipForRelatives", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_otherPeopleWithTiesToUS", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_statementFromApplicant", ApplicantType.Beneficiary);
        sectionToApplicantTypeMapper.put("Sec_admin", ApplicantType.Beneficiary);

        return sectionToApplicantTypeMapper;
    }


    static List<Answer> removeDuplicateAnswers(answerList) {
        Map<String, List<Answer>> answerByPath = new HashMap<>();
        answerList.inject(answerByPath) { Map<String, List<Answer>> data, Answer answer ->
            List<Answer> answerByPathList = data.get(answer.path) ?: [];           // reduce operation
            answerByPathList.add(answer);
            data.put(answer.path, answerByPathList);
            return data;
        };

        Map<String, List<Answer>> invalidAnswerMap = answerByPath.findAll { String path, List<Answer> answerByPathList ->
            return answerByPathList.size() > 1;
        }

        if (invalidAnswerMap.size() == 0) {
            return answerList;
        }

        Set<String> duplicateAnswerPaths = invalidAnswerMap.keySet();
        List<Answer> validAnswerList = answerList.findAll { !duplicateAnswerPaths.contains(it.path) }

        invalidAnswerMap.each {
            String questionFieldPath = it.key;
            List<Answer> answerByPathList = it.value;
            validAnswerList.add(answerByPathList[0]);
        }
        return validAnswerList;
    }

    static void updateCompletionSections(SectionCompletionStatusService completionService, Long packageId,
                                         Long applicantId, List<String> sectionIds) {
        SectionCompletionStatus.withNewTransaction {
            sectionIds.each {
                completionService.updateSectionCompletionStatus(packageId, applicantId, it)
            }
        }
    }

}
