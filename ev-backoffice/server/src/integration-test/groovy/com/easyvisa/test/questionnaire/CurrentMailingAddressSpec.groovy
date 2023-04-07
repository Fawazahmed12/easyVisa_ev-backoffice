package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.Country
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.State
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.model.BenefitCategory
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Unroll

import java.time.LocalDate
import java.util.stream.Collectors

import static org.junit.Assert.*

@Integration
class CurrentMailingAddressSpec extends TestMockUtils {

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService

    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private AdminService adminService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    @Autowired
    private ProfileService profileService
    private TaxService taxServiceMock = Mock(TaxService)

    @Value('${local.server.port}')
    Integer serverPort

    void setup() {
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }

    @Unroll
    void "Test Mailing Address #label"() {

        /*
         - Test for different Benefit Category (with and without 864)
            - With 864 - IR1, IR5
                - Verify Question 69 is hidden if Current Physical Address NOT in Poverty Guidelines
                    - Verify Country is fixed to US for Mailing Address
                    - Verfiy States list does not contain AMERICAN SAMOA etc.
                    - Verify labels are state, zip code
                - Verify Question 69 is shown if Current Physical Address IN Poverty Guidelines
                    - Verify answers are correctly populated if Yes is selected for Q 69
        */

        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])

        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, benefitCategory)

        String sectionId = "Sec_addressHistory"
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant


        List<Answer> answerList = AnswerListStub.addressHistoryForMailingAddressPovertyGuidelines(
                aPackage.id,
                petitionerApplicant.id,
                physicalCountry,
                physicalState,
                q69Answer,
                domicileCountry)


        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)

        SectionNodeInstance addressHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(addressHistorySectionInstance)

        List<EasyVisaNodeInstance> addressHistorySectionInstanceList = addressHistorySectionInstance.children
        // get current Mailing Address
        SubSectionNodeInstance currentMailingAddressNode = (SubSectionNodeInstance) addressHistorySectionInstanceList.find { it.id == 'SubSec_currentMailingAddress' }
        assertNotNull(currentMailingAddressNode)

        // get current Mailing Address Children
        List<EasyVisaNodeInstance> mailingAddressChildrenList = currentMailingAddressNode.children

        //validate Q_69 visibility and answer
        QuestionNodeInstance q69 = mailingAddressChildrenList.find { it.id == 'Q_69' }
        assertNotNull(q69)

        // assert visibility
        assertEquals(q69show, q69.visibility)

        QuestionNodeInstance q70Country = mailingAddressChildrenList.find { it.id == 'Q_70' }
        assertNotNull(q70Country)


        QuestionNodeInstance q76State = q70Country.children?.find { it.id == 'Q_76' }
        assertNotNull(q76State)
        // Validate List of states DOES NOT contain states not in the Poverty Guidelines List
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

        if (stateFiltered) {
            assertFalse(q76State.inputSourceType.values*.value.containsAll(statesNotInPovertyGuidelines*.displayName))
        } else {
            assertTrue(q76State.inputSourceType.values*.value.containsAll(statesNotInPovertyGuidelines*.displayName))
        }

        // Assert Correct Rules are associated, to rule out neo4j data loading issue
        assertEquals('AutoPopulateCurrentMailingAddressRule', q69.ruleClassName)
        assertEquals('MailingAddressCountrySelectionRule', q70Country.ruleClassName)
        assertEquals('CountryUSOnlyInputSourceRule', q70Country.inputTypeSourceRule)
        assertEquals('StatesInUSPovertyGuidelinesInputSourceRule', q76State.inputTypeSourceRule)

        // assert Explain how you meet US Domicile Requirement? visibility
        // get current Physical Address
        SubSectionNodeInstance currentPhysicalAddressNode = (SubSectionNodeInstance) addressHistorySectionInstanceList.find { it.id == 'SubSec_currentPhysicalAddress' }
        List<EasyVisaNodeInstance> physicalAddressChildrenList = currentPhysicalAddressNode.children

        // domicile country is available only of Benefit Category requiring Form 864
        if (domicileCountry) {
            //validate Q_6041 visibility
            QuestionNodeInstance q6041 = physicalAddressChildrenList.find { it.id == 'Q_6041' }
            assertEquals(explainDomicileVisible, q6041.visibility)
        }


        if (q69.answer.value == 'no') {
            //Q69 is answered as No
            // Country dropdown is visible
            assertTrue(q70Country.visibility)

            // Country is not null
            assertNotNull(q70Country.answer?.value)

            // Country dropdown HAS value
            assertTrue(q70Country.inputSourceType?.values*.value?.contains(mailCountryExists.displayName))

            // Country dropdown does NOT have value
            if (mailCountryNotExists) {
                assertFalse(q70Country.inputSourceType?.values*.value?.contains(mailCountryNotExists.displayName))
            }

            // State dropdown has value
            if (mailStateExists)
                assertTrue(q76State.inputSourceType?.values*.value?.contains(mailStateExists.displayName))

            // State dropdown does NOT have value
            if (mailStateNotExists) {
                assertFalse(q76State.inputSourceType?.values*.value?.contains(mailStateNotExists.displayName))
            }

        } else if (q69.answer.value == 'yes') {
            // q69 is same as Current Physical Address
            // country should not be visible - No need to check for state etc.
            assertFalse(q70Country.visibility)

            // Validate Country is same
            QuestionNodeInstance q42ountry = physicalAddressChildrenList.find { it.id == 'Q_42' }
            assertEquals(q42ountry?.answer.value, q70Country.answer?.value)

            // Validate State is same
            QuestionNodeInstance q48State = q42ountry.children.find { it.id == 'Q_48' }
            assertEquals(q48State?.answer.value, q76State.answer?.value)


        } else {
            // q69 is not answered
            // country should not be visible
            assertFalse(q70Country.visibility)
            // No other check required

        }


        cleanup:
        testHelper.clean()

        where:
        label                                                          | benefitCategory                 | physicalCountry                   | physicalState                    | q69show | q69Answer | mailCountryExists     | mailStateExists      | mailCountryNotExists | mailStateNotExists   | stateFiltered | domicileCountry                   | explainDomicileVisible
        "IR1-Address Within PG - Q69 Unanswered"                       | ImmigrationBenefitCategory.IR1  | Country.UNITED_STATES.displayName | State.ALABAMA.displayName        | true    | ""        | null                  | null                 | null                 | null                 | true          | Country.UNITED_STATES.displayName | false
        "IR1-Address Within PG - Mailing NOT Same as Physical Address" | ImmigrationBenefitCategory.IR1  | Country.UNITED_STATES.displayName | State.ALABAMA.displayName        | true    | "no"      | Country.UNITED_STATES | State.ALABAMA        | Country.AFGHANISTAN  | State.AMERICAN_SAMOA | true          | Country.UNITED_STATES.displayName | false
        "IR1-Address Within PG - Mailing Same as Physical Address"     | ImmigrationBenefitCategory.IR1  | Country.UNITED_STATES.displayName | State.ALABAMA.displayName        | true    | "yes"     | Country.UNITED_STATES | State.ALABAMA        | Country.AFGHANISTAN  | State.AMERICAN_SAMOA | true          | Country.UNITED_STATES.displayName | false
        "IR1-State outside PG"                                         | ImmigrationBenefitCategory.IR1  | Country.UNITED_STATES.displayName | State.AMERICAN_SAMOA.displayName | false   | "no"      | Country.UNITED_STATES | State.ALABAMA        | Country.AFGHANISTAN  | State.AMERICAN_SAMOA | true          | Country.UNITED_STATES.displayName | false
        "IR1-Country not US, Domicile US, Explain Domicile Visible"    | ImmigrationBenefitCategory.IR1  | Country.AFGHANISTAN.displayName   | "Afghan State"                   | false   | "no"      | Country.UNITED_STATES | State.ALABAMA        | Country.AFGHANISTAN  | State.AMERICAN_SAMOA | true          | Country.UNITED_STATES.displayName | true
        "IR1-Country US, Domicile Outside, Explain Domicile Visible"   | ImmigrationBenefitCategory.IR1  | Country.AFGHANISTAN.displayName   | "Afghan State"                   | false   | "no"      | Country.UNITED_STATES | State.ALABAMA        | Country.AFGHANISTAN  | State.AMERICAN_SAMOA | true          | Country.UNITED_STATES.displayName | true
        "IR1-Both country outside, Explain Domicile Visible"           | ImmigrationBenefitCategory.IR1  | Country.AFGHANISTAN.displayName   | "Afghan State"                   | false   | "no"      | Country.UNITED_STATES | State.ALABAMA        | Country.AFGHANISTAN  | State.AMERICAN_SAMOA | true          | Country.AFGHANISTAN.displayName   | true
        "K1-Address outside PG"                                        | ImmigrationBenefitCategory.K1K3 | Country.AFGHANISTAN.displayName   | State.AMERICAN_SAMOA.displayName | true    | "no"      | Country.AFGHANISTAN   | null                 | null                 | null                 | false         | null                              | false
        "K1-Address inside PG"                                         | ImmigrationBenefitCategory.K1K3 | Country.UNITED_STATES.displayName | State.ALABAMA.displayName        | true    | "no"      | Country.UNITED_STATES | State.AMERICAN_SAMOA | null                 | null                 | false         | null                              | false

    }

}
