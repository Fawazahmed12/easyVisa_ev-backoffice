package com.easyvisa.test.questionnaire

import com.easyvisa.AdminService
import com.easyvisa.AnswerService
import com.easyvisa.Applicant
import com.easyvisa.AttorneyService
import com.easyvisa.Package
import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.PackageService
import com.easyvisa.PaymentService
import com.easyvisa.ProfileService
import com.easyvisa.TaxService
import com.easyvisa.User
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.SubSectionNodeInstance
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification

import java.time.LocalDate
import java.time.Month
import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class AddressHistorySpec extends TestMockUtils {

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
    @Autowired
    private ProfileService profileService

    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
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

    void testUSCitizenLegalStatusQuestionnaire() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        String sectionId = "Sec_addressHistory"
        Package aPackage = testHelper.aPackage;
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.addressHistoryAnswerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        List<Answer> sectionAnswerList = answerService.fetchAnswers(aPackage.id, petitionerApplicant.id)
        SectionNodeInstance addressHistorySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(aPackage.id,
                        petitionerApplicant.id, sectionId, sectionAnswerList)

        then:
        assertNotNull(addressHistorySectionInstance)
        assertAddressHistoryAnswers(addressHistorySectionInstance)

        cleanup:
        testHelper.clean()
    }

    private void assertAddressHistoryAnswers(SectionNodeInstance addressHistorySectionInstance) {
        List<EasyVisaNodeInstance> addressHistorySectionInstanceList = addressHistorySectionInstance.getChildren()
        assertEquals(4, addressHistorySectionInstanceList.size())

        List<EasyVisaNodeInstance> visibleAddressHistorySectionInstanceList = addressHistorySectionInstanceList.stream()
                .filter({ nodeInstance -> nodeInstance.isVisibility() })
                .collect(Collectors.toList())
        assertEquals(4, visibleAddressHistorySectionInstanceList.size())

        assertCurrentPhysicalAddressSubSectionInstanceAnswers(visibleAddressHistorySectionInstanceList)
        assertPreviousPhysicalAddressSubSectionInstanceAnswers(visibleAddressHistorySectionInstanceList)
        assertUSStatesAndForeignCountriesResidedSince18thBirthdaySubSectionInstanceAnswers(visibleAddressHistorySectionInstanceList)
        assertCurrentMailingAddressSubSectionInstanceAnswers(visibleAddressHistorySectionInstanceList)
    }


    private void assertCurrentPhysicalAddressSubSectionInstanceAnswers(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance legalStatusInUSndGovtIDNosSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals("SubSec_currentPhysicalAddress") })
                .findFirst()
                .orElse(null)
        assertNotNull(legalStatusInUSndGovtIDNosSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = legalStatusInUSndGovtIDNosSubSectionInstance.getChildren()
        assertEquals(6, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(6, questionNodeInstanceList.size())

        QuestionNodeInstance currentPhysicalAddressQuestion = questionNodeInstanceList.get(0)
        assertEquals("currentPhysicalAddress", currentPhysicalAddressQuestion.getName())
        Answer currentPhysicalAddressAnswer = currentPhysicalAddressQuestion.getAnswer()
        assertEquals("United States", currentPhysicalAddressAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42", currentPhysicalAddressAnswer.getPath())

        QuestionNodeInstance streetNumberAndNameQuestion = questionNodeInstanceList.get(1)
        assertEquals("streetNumberAndName", streetNumberAndNameQuestion.getName())
        Answer streetNumberAndNameAnswer = streetNumberAndNameQuestion.getAnswer()
        assertEquals("400 North Car Street", streetNumberAndNameAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_43", streetNumberAndNameAnswer.getPath())

        QuestionNodeInstance addressHaveASecondaryDescriptionQuestion = questionNodeInstanceList.get(2)
        assertEquals("addressHaveASecondaryDescription", addressHaveASecondaryDescriptionQuestion.getName())
        Answer addressHaveASecondaryDescriptionAnswer = addressHaveASecondaryDescriptionQuestion.getAnswer()
        assertEquals("Yes", addressHaveASecondaryDescriptionAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_44", addressHaveASecondaryDescriptionAnswer.getPath())

        QuestionNodeInstance cityTownVillageQuestion = questionNodeInstanceList.get(3)
        assertEquals("cityTownVillage", cityTownVillageQuestion.getName())
        Answer cityTownVillageAnswer = cityTownVillageQuestion.getAnswer()
        assertEquals("Aurora", cityTownVillageAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_47", cityTownVillageAnswer.getPath())

        QuestionNodeInstance moveIntoThisAddressQuestion = questionNodeInstanceList.get(4)
        assertEquals("moveIntoThisAddress", moveIntoThisAddressQuestion.getName())
        Answer moveIntoThisAddressAnswer = moveIntoThisAddressQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(DateUtil.today().year - 3, Month.JANUARY, 17)), moveIntoThisAddressAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52", moveIntoThisAddressAnswer.getPath())

        QuestionNodeInstance moveOutOfThisAddressQuestion = questionNodeInstanceList.get(5)
        assertEquals("moveOutOfThisAddress", moveOutOfThisAddressQuestion.getName())
        Answer moveOutOfThisAddressAnswer = moveOutOfThisAddressQuestion.getAnswer()
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_6000", moveOutOfThisAddressAnswer.getPath())


        assertEquals(2, addressHaveASecondaryDescriptionQuestion.getChildren().size())
        List<QuestionNodeInstance> addressHaveASecondaryDescriptionQuestionInstanceList = addressHaveASecondaryDescriptionQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, addressHaveASecondaryDescriptionQuestionInstanceList.size())

        QuestionNodeInstance secondaryAddressDescriptionQuestion = addressHaveASecondaryDescriptionQuestionInstanceList.get(0)
        assertEquals("secondaryAddressDescription", secondaryAddressDescriptionQuestion.getName())
        Answer secondaryAddressDescriptionAnswer = secondaryAddressDescriptionQuestion.getAnswer()
        assertEquals("Apartment", secondaryAddressDescriptionAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_45", secondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance apartmentSuiteFloorQuestion = addressHaveASecondaryDescriptionQuestionInstanceList.get(1)
        assertEquals("apartmentSuiteFloor", apartmentSuiteFloorQuestion.getName())
        Answer apartmentSuiteFloorAnswer = apartmentSuiteFloorQuestion.getAnswer()
        assertEquals("Samuel Apartments", apartmentSuiteFloorAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_46", apartmentSuiteFloorAnswer.getPath())


        assertEquals(4, currentPhysicalAddressQuestion.getChildren().size())
        List<QuestionNodeInstance> currentPhysicalAddressQuestionInstanceList = currentPhysicalAddressQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, currentPhysicalAddressQuestionInstanceList.size())

        QuestionNodeInstance stateQuestion = currentPhysicalAddressQuestionInstanceList.get(0)
        assertEquals("state", stateQuestion.getName())
        Answer stateAnswer = stateQuestion.getAnswer()
        assertEquals("Colorado", stateAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", stateAnswer.getPath())

        QuestionNodeInstance zipCodeQuestion = currentPhysicalAddressQuestionInstanceList.get(1)
        assertEquals("zipCode", zipCodeQuestion.getName())
        Answer zipCodeAnswer = zipCodeQuestion.getAnswer()
        assertEquals("80011", zipCodeAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentPhysicalAddress/Q_50", zipCodeAnswer.getPath())

    }

    private void assertPreviousPhysicalAddressSubSectionInstanceAnswers(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance previousPhysicalAddressSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals("SubSec_previousPhysicalAddress") })
                .findFirst()
                .orElse(null)
        assertNotNull(previousPhysicalAddressSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = previousPhysicalAddressSubSectionInstance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<RepeatingQuestionGroupNodeInstance> previousPhysicalAddressRepeatingInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof RepeatingQuestionGroupNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (RepeatingQuestionGroupNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, previousPhysicalAddressRepeatingInstanceList.size())


        RepeatingQuestionGroupNodeInstance previousPhysicalAddressFirstInstance = previousPhysicalAddressRepeatingInstanceList[0];
        List<EasyVisaNodeInstance> previousPhysicalAddressFirstInstanceChildren = previousPhysicalAddressFirstInstance.getChildren()
        assertEquals(6, previousPhysicalAddressFirstInstanceChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = previousPhysicalAddressFirstInstanceChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(6, questionNodeInstanceList.size())

        QuestionNodeInstance whatCountryWasThisPreviousPhysicalAddressQuestion = questionNodeInstanceList.get(0)
        assertEquals("whatCountryWasThisPreviousPhysicalAddress", whatCountryWasThisPreviousPhysicalAddressQuestion.getName())
        Answer whatCountryWasThisPreviousPhysicalAddressAnswer = whatCountryWasThisPreviousPhysicalAddressQuestion.getAnswer()
        assertEquals("Albania", whatCountryWasThisPreviousPhysicalAddressAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_54/0", whatCountryWasThisPreviousPhysicalAddressAnswer.getPath())

        QuestionNodeInstance streetNumberAndNameQuestion = questionNodeInstanceList.get(1)
        assertEquals("streetNumberAndName", streetNumberAndNameQuestion.getName())
        Answer streetNumberAndNameAnswer = streetNumberAndNameQuestion.getAnswer()
        assertEquals("23-A Mustafa Matohiti", streetNumberAndNameAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_55/0", streetNumberAndNameAnswer.getPath())

        QuestionNodeInstance addressHaveASecondaryDescriptionQuestion = questionNodeInstanceList.get(2)
        assertEquals("addressHaveASecondaryDescription", addressHaveASecondaryDescriptionQuestion.getName())
        Answer addressHaveASecondaryDescriptionAnswer = addressHaveASecondaryDescriptionQuestion.getAnswer()
        assertEquals("No", addressHaveASecondaryDescriptionAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_56/0", addressHaveASecondaryDescriptionAnswer.getPath())

        QuestionNodeInstance cityTownVillageQuestion = questionNodeInstanceList.get(3)
        assertEquals("cityTownVillage", cityTownVillageQuestion.getName())
        Answer cityTownVillageAnswer = cityTownVillageQuestion.getAnswer()
        assertEquals("Miladin", cityTownVillageAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_59/0", cityTownVillageAnswer.getPath())

        QuestionNodeInstance moveIntoThisAddressQuestion = questionNodeInstanceList.get(4)
        assertEquals("moveIntoThisAddress", moveIntoThisAddressQuestion.getName())
        Answer moveIntoThisAddressAnswer = moveIntoThisAddressQuestion.getAnswer()
        Integer currentYear = DateUtil.today().year
        assertEquals(DateUtil.fromDate(LocalDate.of(currentYear - 4, Month.MAY, 1)), moveIntoThisAddressAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64/0", moveIntoThisAddressAnswer.getPath())

        QuestionNodeInstance moveOutOfThisAddressQuestion = questionNodeInstanceList.get(5)
        assertEquals("moveOutOfThisAddress", moveOutOfThisAddressQuestion.getName())
        Answer moveOutOfThisAddressAnswer = moveOutOfThisAddressQuestion.getAnswer()
        assertEquals(DateUtil.fromDate(LocalDate.of(currentYear - 3, Month.JANUARY, 15)), moveOutOfThisAddressAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65/0", moveOutOfThisAddressAnswer.getPath())


        assertEquals(4, whatCountryWasThisPreviousPhysicalAddressQuestion.getChildren().size())
        List<QuestionNodeInstance> whatCountryWasThisPreviousPhysicalAddressQuestionInstanceList = whatCountryWasThisPreviousPhysicalAddressQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, whatCountryWasThisPreviousPhysicalAddressQuestionInstanceList.size())

        QuestionNodeInstance provinceTerritoryPrefectureParishQuestion = whatCountryWasThisPreviousPhysicalAddressQuestionInstanceList.get(0)
        assertEquals("provinceTerritoryPrefectureParish", provinceTerritoryPrefectureParishQuestion.getName())
        Answer provinceTerritoryPrefectureParishAnswer = provinceTerritoryPrefectureParishQuestion.getAnswer()
        assertEquals("Tirana", provinceTerritoryPrefectureParishAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_61/0", provinceTerritoryPrefectureParishAnswer.getPath())

        QuestionNodeInstance postalCodeQuestion = whatCountryWasThisPreviousPhysicalAddressQuestionInstanceList.get(1)
        assertEquals("postalCode", postalCodeQuestion.getName())
        Answer postalCodeAnswer = postalCodeQuestion.getAnswer()
        assertEquals("1031", postalCodeAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_previousPhysicalAddress/Q_63/0", postalCodeAnswer.getPath())
    }

    private void assertUSStatesAndForeignCountriesResidedSince18thBirthdaySubSectionInstanceAnswers(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance legalStatusInUSndGovtIDNosSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals("SubSec_residedSince18") })
                .findFirst()
                .orElse(null)
        assertNotNull(legalStatusInUSndGovtIDNosSubSectionInstance)
    }

    private void assertCurrentMailingAddressSubSectionInstanceAnswers(List<EasyVisaNodeInstance> visibleLegalStatusInUSSectionInstanceList) {
        SubSectionNodeInstance legalStatusInUSndGovtIDNosSubSectionInstance = (SubSectionNodeInstance) visibleLegalStatusInUSSectionInstanceList.stream()
                .filter({ x -> x.getDefinitionNode().getId().equals("SubSec_currentMailingAddress") })
                .findFirst()
                .orElse(null)
        assertNotNull(legalStatusInUSndGovtIDNosSubSectionInstance)

        List<EasyVisaNodeInstance> subsectionChildren = legalStatusInUSndGovtIDNosSubSectionInstance.getChildren()

        assertEquals(6, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> (nodeInstance instanceof QuestionNodeInstance) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(6, questionNodeInstanceList.size())

        QuestionNodeInstance careOfNameQuestion = questionNodeInstanceList.get(0)
        assertEquals("careOfName", careOfNameQuestion.getName())
        Answer careOfNameAnswer = careOfNameQuestion.getAnswer()
        assertEquals("Edi Rama", careOfNameAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_68", careOfNameAnswer.getPath())

        QuestionNodeInstance currentMailingAddressQuestion = questionNodeInstanceList.get(1)
        assertEquals("currentMailingAddress", currentMailingAddressQuestion.getName())
        Answer currentMailingAddressAnswer = currentMailingAddressQuestion.getAnswer()
        assertEquals("yes", currentMailingAddressAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_69", currentMailingAddressAnswer.getPath())


        QuestionNodeInstance countryCurrentmailingAddressQuestion = questionNodeInstanceList.get(2)
        assertEquals("countryCurrentmailingAddress", countryCurrentmailingAddressQuestion.getName())
        Answer countryCurrentmailingAddressAnswer = countryCurrentmailingAddressQuestion.getAnswer()
        assertEquals("United States", countryCurrentmailingAddressAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_70", countryCurrentmailingAddressAnswer.getPath())

        QuestionNodeInstance streetNumberNameQuestion = questionNodeInstanceList.get(3)
        assertEquals("streetNumberName", streetNumberNameQuestion.getName())
        Answer streetNumberNameAnswer = streetNumberNameQuestion.getAnswer()
        assertEquals("400 North Car Street", streetNumberNameAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_71", streetNumberNameAnswer.getPath())

        QuestionNodeInstance addressHaveASecondaryDescriptionQuestion = questionNodeInstanceList.get(4)
        assertEquals("addressSecondaryDescription", addressHaveASecondaryDescriptionQuestion.getName())
        Answer addressSecondaryDescriptionAnswer = addressHaveASecondaryDescriptionQuestion.getAnswer()
        assertEquals("Yes", addressSecondaryDescriptionAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_72", addressSecondaryDescriptionAnswer.getPath())

        QuestionNodeInstance cityTownVillageQuestion = questionNodeInstanceList.get(5)
        assertEquals("cityTownVillage", cityTownVillageQuestion.getName())
        Answer cityTownVillageAnswer = cityTownVillageQuestion.getAnswer()
        assertEquals("Aurora", cityTownVillageAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_75", cityTownVillageAnswer.getPath())


        assertEquals(4, countryCurrentmailingAddressQuestion.getChildren().size())
        List<QuestionNodeInstance> countryCurrentmailingAddressQuestionInstanceList = countryCurrentmailingAddressQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())


        QuestionNodeInstance stateQuestion = countryCurrentmailingAddressQuestionInstanceList.stream()
                .filter({ x -> x.getName().equals("state") })
                .findFirst()
                .orElse(null)

        assertNotNull(stateQuestion)
        Answer stateAnswer = stateQuestion.getAnswer()
        assertEquals("Colorado", stateAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_76", stateAnswer.getPath())

        QuestionNodeInstance zipCodeQuestion = countryCurrentmailingAddressQuestionInstanceList.stream()
                .filter({ x -> x.getName().equals("zipCode") })
                .findFirst()
                .orElse(null)
        assertNotNull(zipCodeQuestion)
        Answer zipCodeAnswer = zipCodeQuestion.getAnswer()
        assertEquals("80011", zipCodeAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_78", zipCodeAnswer.getPath())


        assertEquals(2, addressHaveASecondaryDescriptionQuestion.getChildren().size())
        List<QuestionNodeInstance> addressHaveASecondaryDescriptionQuestionInstanceList = addressHaveASecondaryDescriptionQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())

        assertEquals(2, addressHaveASecondaryDescriptionQuestionInstanceList.size())

        QuestionNodeInstance secondaryAddressDescriptionQuestion = addressHaveASecondaryDescriptionQuestionInstanceList.get(0)
        assertEquals("secondaryAddressDescription", secondaryAddressDescriptionQuestion.getName())
        Answer secondaryAddressDescriptionAnswer = secondaryAddressDescriptionQuestion.getAnswer()
        assertEquals("Apartment", secondaryAddressDescriptionAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_73", secondaryAddressDescriptionAnswer.getPath())

        QuestionNodeInstance apartmentSuiteFloorQuestion = addressHaveASecondaryDescriptionQuestionInstanceList.get(1)
        assertEquals("apartmentSuiteFloor", apartmentSuiteFloorQuestion.getName())
        Answer apartmentSuiteFloorAnswer = apartmentSuiteFloorQuestion.getAnswer()
        assertEquals("Samuel Apartments", apartmentSuiteFloorAnswer.getValue())
        assertEquals("Sec_addressHistory/SubSec_currentMailingAddress/Q_74", apartmentSuiteFloorAnswer.getPath())
    }
}
