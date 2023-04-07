package com.easyvisa.questionnaire.services.rule.util

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.Country
import com.easyvisa.enums.PdfForm
import com.easyvisa.enums.State
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CurrentMailingAddressRuleUtil {

    //todo may want to pass the following two vars as a param
    private static String CURRENT_ADDRESS_COUNTRY_PATH = "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42"
    private static String CURRENT_ADDRESS_STATE_PATH = "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48"
    private static String MAILING_SAME_AS_PHYSICAL_ADDRESS = "Sec_addressHistory/SubSec_currentMailingAddress/Q_69"

    PackageQuestionnaireService packageQuestionnaireService


    Boolean showQuestionForPovertyGuideline(NodeRuleEvaluationContext ruleEvaluationContext) {
        Boolean showQuestion = true

        if (packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I864)) {
            // Get answers for Country and State
            Answer currentAddressCountry = ruleEvaluationContext.findAnswerByPath(CURRENT_ADDRESS_COUNTRY_PATH)

            if (Answer.isValidAnswer(currentAddressCountry) && currentAddressCountry.value == Country.UNITED_STATES.displayName) {
                // check if states is in the list of Poverty Guidelines List
                // List of States NOT in Guidelines
                List<State> statesNotInPovertyGuidelines = [State.AMERICAN_SAMOA,
                                                            State.FEDERATED_STATES_OF_MICRONESIA,
                                                            State.MARSHALL_ISLANDS,
                                                            State.PALAU,
                                                            State.ARMED_FORCES_AFRICA,
                                                            State.ARMED_FORCES_AMERICAS,
                                                            State.ARMED_FORCES_CANADA,
                                                            State.ARMED_FORCES_EUROPE,
                                                            State.ARMED_FORCES_MIDDLE_EAST,
                                                            State.ARMED_FORCES_PACIFIC]

                // Get Answer for State and check if its in the From the above list (statesNotInPovertyGuidelines)
                Answer currentAddressState = ruleEvaluationContext.findAnswerByPath(CURRENT_ADDRESS_STATE_PATH)

                if (Answer.isValidAnswer(currentAddressState)) {

                    if (statesNotInPovertyGuidelines*.displayName.contains(currentAddressState.value)) {

                        // We have a state that is not in the list of Poverty Guidelines
                        // Hide this question and set its value to false

                        showQuestion = false
                    } else {
                        // Show the question

                        showQuestion = true
                    }

                }

            } else {

                showQuestion = false
            }
        }

        return showQuestion
    }

    Boolean isQuestionVisible(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Boolean showQuestion = showQuestionForPovertyGuideline(nodeRuleEvaluationContext)
        // showQuestion will return true if Q_69 should be visible

        if (showQuestion) {
            // If Q_69 is visible then
            // check if the answer to Q_69 is NO then show this question else hide
            Answer mailingAddressSameAsCurrentAddress = nodeRuleEvaluationContext.findAnswerByPath(MAILING_SAME_AS_PHYSICAL_ADDRESS)
            String isCurrentMailingAddressSameAsPhysicalAddress = EasyVisaNode.normalizeAnswer(mailingAddressSameAsCurrentAddress?.getValue())
            if (isCurrentMailingAddressSameAsPhysicalAddress == RelationshipTypeConstants.NO.value) {
                // show this question
                return true
            }
            return false
        }
        // If it is not shown (returns false) then All Current Mailing Address components need to be shown

        return true

    }
}
