package com.easyvisa.questionnaire.answering.benefitcategoryfeatures

import com.easyvisa.SectionCompletionStatusService
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import groovy.transform.CompileStatic

@CompileStatic
class BaseBenefitCategoryFeature {

    /**
     * ONLY display the following question (Q_1701) if the user answered 'Spouse' to the question,
     * Q_1615: 'What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States?'
     * from Section: 'Intro Questions For Beneficiary (The person who is immigrating to the United States)'
     *
     * Question: (Q_1701) Is the person you are currently married to, the same person who sponsored you for your conditional residence status (conditional 'green card')?
     * Section: Basis for Petition to Remove Conditions on Residence
     * */
    Boolean canTriggerRelationshipToPetitionerAction(NodeRuleEvaluationContext ruleEvaluationContext) {
        return false
    }

    /**
     * Do not ask the following questions if user selected 'Without Inspection' option in question
     * 'This person arrived in the United States under which legal status? '
     *
     *  Note: The following questions connceted only with the forms 129F & 130
     *
     * Q_2957: Date authorized stay expired or will expire as shown on I-94 (Arrival/Departure Record) or I-95
     * Q_2958: What is the I-94 record number?
     *
     * */
    Boolean canEvaluateLegalStatusDependentApplicableRule(NodeRuleEvaluationContext ruleEvaluationContext) {
        return false
    }


    void updatedDependentSectionCompletionOnRelationshipToPetitionerAction(
            NodeRuleEvaluationContext ruleEvaluationContext,
            SectionCompletionStatusService sectionCompletionStatusService) {

    }


    // We should not list the 'Fiancé' option to packages other than the K-1 Fiancé visa package (eg: IR-1/CR-1).
    InputSourceType getSponsorshipRelationshipInputSource(InputSourceType inputSourceType, NodeRuleEvaluationContext ruleEvaluationContext) {
        String inputType = 'SponsorshipRelationshipInputSourceRule'
        List<String> sponsorshipRelationshipExcludedValues = ['fiance']
        List<String> sponsorshipRelationshipValues = inputSourceType.getValues()
                .findAll { !sponsorshipRelationshipExcludedValues.contains(it.value) }
                .collect { it.value }
        return filterInputSourceTypeValues(sponsorshipRelationshipValues, inputSourceType, inputType)
    }

    // We should not list the 'Another Method' option to packages other than the K-1 Fiancé visa package (eg: IR-1/CR-1).
    //EV-3252: Options “Another Method” and “Explain how you acquired your citizenship” should only appear for K-1 packages, which require I-134 form
    InputSourceType getCitizenshipAcquiredInputSource(InputSourceType inputSourceType, NodeRuleEvaluationContext ruleEvaluationContext) {
        String inputType = 'CitizenshipAcquiredInputSourceRule'
        List<String> citizenshipAcquiredExcludedValues = ['another_method']
        List<String> citizenshipAcquiredValues = inputSourceType.getValues()
                .findAll { !citizenshipAcquiredExcludedValues.contains(it.value) }
                .collect { it.value }
        return filterInputSourceTypeValues(citizenshipAcquiredValues, inputSourceType, inputType)
    }


    protected InputSourceType filterInputSourceTypeValues(List<String> inputSourceValues, InputSourceType inputSourceType, String inputType) {
        List<InputSourceType.ValueMap> filteredInputSourceValues = inputSourceType.getValues().findAll { inputSourceValues.contains(it.value) }
        InputSourceType fianceInputSourceType = new InputSourceType(inputType, filteredInputSourceValues)
        return fianceInputSourceType
    }


    /**
     *
     * */
    Boolean canValidateAddressHistoryFor5YearsData(NodeRuleEvaluationContext ruleEvaluationContext) {
        return true
    }
}
