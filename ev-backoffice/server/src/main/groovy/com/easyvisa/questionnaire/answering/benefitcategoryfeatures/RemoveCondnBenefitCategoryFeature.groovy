package com.easyvisa.questionnaire.answering.benefitcategoryfeatures

import com.easyvisa.SectionCompletionStatusService
import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext

class RemoveCondnBenefitCategoryFeature extends BaseBenefitCategoryFeature {

    private static String SPOUSE_RELATIONSHIP = 'spouse'
    private static String SPOUSE_RELATIONSHIP_DEPENDENT_SECTION_ID = 'Sec_basisPetitionToRemoveConditionsOnResidence'
    // What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States? Help
    private static String RELATIONSHIP_TO_PETITIONER_FIELD_PATH = "Sec_introQuestionsForBeneficiary/SubSec_introQuestionsForBeneficiary/Q_1615"

    /**
     * ONLY display the following question (Qx_1701) if the user answered 'Spouse' to the question,
     * Q_1615: 'What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States?'
     * from Section: 'Intro Questions For Beneficiary (The person who is immigrating to the United States)'
     *
     * Question: (Q_1701) Is the person you are currently married to, the same person who sponsored you for your conditional residence status (conditional 'green card')?
     * Section: Basis for Petition to Remove Conditions on Residence
     *
     * Need to trigger the subsequent method if user answered than 'Spouse'
     * */
    Boolean canTriggerRelationshipToPetitionerAction(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance() as QuestionNodeInstance;
        Answer relationshipToPetitionerAnswer = questionNodeInstance.getAnswer()
        return relationshipToPetitionerAnswer.doesMatch(SPOUSE_RELATIONSHIP)
    }

    void updatedDependentSectionCompletionOnRelationshipToPetitionerAction(
            NodeRuleEvaluationContext ruleEvaluationContext,
            SectionCompletionStatusService sectionCompletionStatusService) {
        ruleEvaluationContext.matchAnswer(RELATIONSHIP_TO_PETITIONER_FIELD_PATH, { Answer answer ->
            sectionCompletionStatusService.updateSectionCompletionStatus(ruleEvaluationContext.packageId, ruleEvaluationContext.applicantId,
                    SPOUSE_RELATIONSHIP_DEPENDENT_SECTION_ID, DisplayTextLanguage.defaultLanguage, ruleEvaluationContext.currentDate);
        })
    }

    Boolean canValidateAddressHistoryFor5YearsData(NodeRuleEvaluationContext ruleEvaluationContext) {
        return false
    }
}
