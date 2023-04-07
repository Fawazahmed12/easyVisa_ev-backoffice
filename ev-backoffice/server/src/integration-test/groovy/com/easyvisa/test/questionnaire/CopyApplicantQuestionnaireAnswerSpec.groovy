package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
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
class CopyApplicantQuestionnaireAnswerSpec extends TestMockUtils {

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

    void testPetitionerToPetitionerApplicantQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()
        Applicant petitionerApplicant = testHelper.aPackage.petitioner.applicant
        List<Answer> answers = AnswerListStub.addressHistoryAnswerList(testHelper.aPackage.id, petitionerApplicant.id)
        answers.addAll(AnswerListStub.petitionerBirthInformationAnswerList(testHelper.aPackage.id, petitionerApplicant.id))
        testHelper.buildAnswers(answers)
        Package aPackage = testHelper.aPackage

        PackageTestBuilder packageCopyHelper = PackageTestBuilder.init(testHelper)
        packageCopyHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3,
                aPackage.petitioner.applicant.id)
                .buildAnswers()

        Package petitionerCopiedPackage = packageCopyHelper.aPackage

        when:
        Applicant clonedPackagePetitionerApplicant = petitionerCopiedPackage.petitioner.applicant
        def sectionAnswerList = answerService.fetchAnswers(petitionerCopiedPackage.id, clonedPackagePetitionerApplicant.id)
        SectionNodeInstance petitionerSectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(petitionerCopiedPackage.id,
                        clonedPackagePetitionerApplicant.id, sectionId, sectionAnswerList)
        def packageSections = this.packageQuestionnaireService.fetchPackageSections(petitionerCopiedPackage.id)

        then:
        assertNotNull(aPackage)
        assertNotNull(aPackage.petitioner)
        assertNotNull(aPackage.petitioner.applicant)
        assertNotNull(petitionerCopiedPackage)
        assertNotNull(petitionerCopiedPackage.petitioner)
        assertNotNull(petitionerCopiedPackage.petitioner.applicant)
        assertNotNull(petitionerSectionInstance)
        assertAddressHistoryAnswers(petitionerSectionInstance)
        assertNotNull(packageSections)
        assertPetitionerToPetitionerSectionsWeightage(packageSections)

        cleanup:
        testHelper.deletePackageOnly(false)
        packageCopyHelper.clean()
    }


    void testBeneficiaryToBeneficiaryApplicantQuestionnaire() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformationForBeneficiary'
        PackageTestBuilder testHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
                .buildUsersForPackageApplicants()
        Package aPackage = testHelper.aPackage
        Applicant principalApplicant = aPackage.principalBeneficiary
        List<Answer> answers = AnswerListStub.addressHistoryBeneficaryAnswerList(aPackage.id, principalApplicant.id)
        answers.addAll(AnswerListStub.beneficiaryBirthInformationAnswerList(aPackage.id, principalApplicant.id))
        answers.addAll(AnswerListStub.imperialUnitBiographicInformationAnswerList(aPackage.id, principalApplicant.id))
        testHelper.buildAnswers(answers)

        PackageTestBuilder packageCopyHelper = PackageTestBuilder.init(testHelper)
        packageCopyHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1,
                null, aPackage.principalBeneficiary.id)
                .buildAnswers()
        Package beneficiaryCopiedPackage = packageCopyHelper.aPackage

        when:
        Applicant clonedPackageBeneficiaryApplicant = beneficiaryCopiedPackage.getPrincipalBeneficiary();
        def sectionAnswerList = answerService.fetchAnswers(beneficiaryCopiedPackage.id, clonedPackageBeneficiaryApplicant.id)
        SectionNodeInstance beneficiarySectionInstance =
                packageQuestionnaireService.questionGraphByBenefitCategoryAndSection(beneficiaryCopiedPackage.id,
                        clonedPackageBeneficiaryApplicant.id, sectionId, sectionAnswerList)
        def packageSections = this.packageQuestionnaireService.fetchPackageSections(beneficiaryCopiedPackage.id)

        then:
        assertNotNull(beneficiarySectionInstance)
        assertNotNull(aPackage)
        assertNotNull(aPackage.petitioner)
        assertNotNull(aPackage.petitioner.applicant)
        assertNotNull(beneficiaryCopiedPackage)
        assertNotNull(beneficiaryCopiedPackage.petitioner)
        assertNotNull(beneficiaryCopiedPackage.petitioner.applicant)
        assertNotNull(beneficiarySectionInstance)
        assertImperialUnitBiographicInformationAnswers(beneficiarySectionInstance)
        assertNotNull(packageSections)
        assertBeneficiaryToBeneficiarySectionsWeightage(packageSections)

        cleanup:
        testHelper.deletePackageOnly(true, false)
        packageCopyHelper.clean()
    }

    void testBeneficiaryToPetitionerApplicantQuestionnaire() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([
                serverPort                 : serverPort,
                adminService               : adminService,
                attorneyService            : attorneyService,
                packageService             : packageService,
                answerService              : answerService,
                packageQuestionnaireService: packageQuestionnaireService,
                profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
                .buildUsersForPackageApplicants()
        Package aPackage = testHelper.aPackage
        Applicant principalApplicant = aPackage.principalBeneficiary
        List<Answer> answers = AnswerListStub.addressHistoryBeneficaryAnswerList(aPackage.id, principalApplicant.id)
        answers.addAll(AnswerListStub.beneficiaryBirthInformationAnswerList(aPackage.id, principalApplicant.id))
        answers.addAll(AnswerListStub.beneficiaryContactInformationAnswerList(aPackage.id, principalApplicant.id))
        answers.addAll(AnswerListStub.imperialUnitBiographicInformationAnswerList(aPackage.id, principalApplicant.id))
        answers.addAll(AnswerListStub.employedStatusEmployeeHistoryForBeneficiaryAnswerList(aPackage.id, principalApplicant.id))
        testHelper.buildAnswers(answers)

        PackageTestBuilder packageCopyHelper = PackageTestBuilder.init(testHelper)
        packageCopyHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3,
                aPackage.principalBeneficiary.id)
                .buildAnswers()
        Package beneficiaryToPetitionerCopiedPackage = packageCopyHelper.aPackage

        when:
        def packageSections = this.packageQuestionnaireService.fetchPackageSections(beneficiaryToPetitionerCopiedPackage.id)

        then:
        assertNotNull(aPackage)
        assertNotNull(aPackage.petitioner)
        assertNotNull(aPackage.petitioner.applicant)
        assertNotNull(beneficiaryToPetitionerCopiedPackage)
        assertNotNull(beneficiaryToPetitionerCopiedPackage.petitioner)
        assertNotNull(beneficiaryToPetitionerCopiedPackage.petitioner.applicant)
        assertNotNull(packageSections)
        assertBeneficiaryToPetitionerSectionsWeightage(packageSections)

        cleanup:
        testHelper.deletePackageOnly(true, false)
        packageCopyHelper.clean()
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
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance)) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(4, countryCurrentmailingAddressQuestionInstanceList.size())

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

    ////////////////


    private void assertImperialUnitBiographicInformationAnswers(SectionNodeInstance sectionInstance) {
        List<EasyVisaNodeInstance> subsectionInstanceList = sectionInstance.getChildren()
        assertEquals(6, subsectionInstanceList.size())

        assertEthnicitySubsectionAndItsAnswers(subsectionInstanceList)
        assertRaceSubsectionAndItsAnswers(subsectionInstanceList)
        assertWeightSubsectionAndItsAnswersByImperialUnit(subsectionInstanceList)
        assertEyeColorSubsectionAndItsAnswers(subsectionInstanceList)
        assertHairColorSubsectionAndItsAnswers(subsectionInstanceList)
        assertHeightSubsectionAndItsAnswersByImperialUnit(subsectionInstanceList)
    }

    private void assertEthnicitySubsectionAndItsAnswers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(0)
        assertEquals("Ethnicity (Select only one box)", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance ethnicityQuestion = questionNodeInstanceList.get(0)
        assertEquals("ethnicity", ethnicityQuestion.getName())
        Answer ethnicityAnswer = ethnicityQuestion.getAnswer()
        assertEquals("Hispanic or Latino", ethnicityAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_ethnicityForBeneficiary/Q_2301", ethnicityAnswer.getPath())
    }


    private void assertRaceSubsectionAndItsAnswers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(1)
        assertEquals("Race (Select all applicable boxes)", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(5, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(5, questionNodeInstanceList.size())

        QuestionNodeInstance whiteQuestion = questionNodeInstanceList.get(0)
        assertEquals("white", whiteQuestion.getName())
        Answer whiteAnswer = whiteQuestion.getAnswer()
        assertEquals("False", whiteAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2303", whiteAnswer.getPath())

        QuestionNodeInstance asianQuestion = questionNodeInstanceList.get(1)
        assertEquals("asian", asianQuestion.getName())
        Answer asianAnswer = asianQuestion.getAnswer()
        assertEquals("False", asianAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2304", asianAnswer.getPath())

        QuestionNodeInstance blackOrAfricanAmericanQuestion = questionNodeInstanceList.get(2)
        assertEquals("blackOrAfricanAmerican", blackOrAfricanAmericanQuestion.getName())
        Answer blackOrAfricanAmericanAnswer = blackOrAfricanAmericanQuestion.getAnswer()
        assertEquals("False", blackOrAfricanAmericanAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2305", blackOrAfricanAmericanAnswer.getPath())

        QuestionNodeInstance americanIndianOrAlaskaNativeQuestion = questionNodeInstanceList.get(3)
        assertEquals("americanIndianOrAlaskaNative", americanIndianOrAlaskaNativeQuestion.getName())
        Answer americanIndianOrAlaskaNativeAnswer = americanIndianOrAlaskaNativeQuestion.getAnswer()
        assertEquals("True", americanIndianOrAlaskaNativeAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2306", americanIndianOrAlaskaNativeAnswer.getPath())

        QuestionNodeInstance nativeHawaiianOrOtherPacificIslanderQuestion = questionNodeInstanceList.get(4)
        assertEquals("nativeHawaiianOrOtherPacificIslander", nativeHawaiianOrOtherPacificIslanderQuestion.getName())
        Answer nativeHawaiianOrOtherPacificIslanderAnswer = nativeHawaiianOrOtherPacificIslanderQuestion.getAnswer()
        assertEquals("False", nativeHawaiianOrOtherPacificIslanderAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2307", nativeHawaiianOrOtherPacificIslanderAnswer.getPath())
    }

    private void assertWeightSubsectionAndItsAnswersByImperialUnit(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(3)
        assertEquals("Weight", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance unitsMeasureQuestion = questionNodeInstanceList.get(0)
        assertEquals("unitsMeasure", unitsMeasureQuestion.getName())
        Answer unitsMeasureAnswer = unitsMeasureQuestion.getAnswer()
        assertEquals("Imperial", unitsMeasureAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_2314", unitsMeasureAnswer.getPath())

        ///////////

        assertEquals(2, unitsMeasureQuestion.getChildren().size())
        List<QuestionNodeInstance> unitsMeasureQuestionInstanceList = unitsMeasureQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, unitsMeasureQuestionInstanceList.size())

        QuestionNodeInstance lbsWeightQuestion = unitsMeasureQuestion.getChildren()[0]
        assertEquals(true, lbsWeightQuestion.isVisibility())
        assertEquals("lbsWeight", lbsWeightQuestion.getName())
        Answer lbsWeightAnswer = lbsWeightQuestion.getAnswer()
        assertEquals("236", lbsWeightAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_2315", lbsWeightAnswer.getPath())

        QuestionNodeInstance metricWeightQuestion = unitsMeasureQuestion.getChildren()[1]
        assertEquals(false, metricWeightQuestion.isVisibility())
        assertEquals("metricWeight", metricWeightQuestion.getName())
        Answer metricWeightAnswer = metricWeightQuestion.getAnswer()
        assertEquals("107", metricWeightAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_6010", metricWeightAnswer.getPath())
    }


    private void assertEyeColorSubsectionAndItsAnswers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(4)
        assertEquals("Eye Color", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance eyeColorQuestion = questionNodeInstanceList.get(0)
        assertEquals("eyeColor", eyeColorQuestion.getName())
        Answer eyeColorAnswer = eyeColorQuestion.getAnswer()
        assertEquals("Hazel", eyeColorAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_eyeColorForBeneficiary/Q_2317", eyeColorAnswer.getPath())
    }


    private void assertHairColorSubsectionAndItsAnswers(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(5)
        assertEquals("Hair Color", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance hairColorQuestion = questionNodeInstanceList.get(0)
        assertEquals("hairColor", hairColorQuestion.getName())
        Answer hairColorAnswer = hairColorQuestion.getAnswer()
        assertEquals("Gray", hairColorAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_hairColorForBeneficiary/Q_2319", hairColorAnswer.getPath())
    }


    private void assertHeightSubsectionAndItsAnswersByImperialUnit(List<EasyVisaNodeInstance> subsectionInstanceList) {
        SubSectionNodeInstance subsection1Instance = (SubSectionNodeInstance) subsectionInstanceList.get(2)
        assertEquals("Height", subsection1Instance.getDisplayText())
        List<EasyVisaNodeInstance> subsectionChildren = subsection1Instance.getChildren()
        assertEquals(1, subsectionChildren.size())

        List<QuestionNodeInstance> questionNodeInstanceList = subsectionChildren.stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && nodeInstance.isVisibility()) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(1, questionNodeInstanceList.size())

        QuestionNodeInstance unitsMeasureQuestion = questionNodeInstanceList.get(0)
        assertEquals("unitsMeasure", unitsMeasureQuestion.getName())
        Answer unitsMeasureAnswer = unitsMeasureQuestion.getAnswer()
        assertEquals("Imperial", unitsMeasureAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2309", unitsMeasureAnswer.getPath())

        ///////////

        assertEquals(3, unitsMeasureQuestion.getChildren().size())
        List<QuestionNodeInstance> unitsMeasureQuestionInstanceList = unitsMeasureQuestion.getChildren().stream()
                .filter({ nodeInstance -> ((nodeInstance instanceof QuestionNodeInstance) && (nodeInstance.isVisibility())) })
                .map({ nodeInstance -> (QuestionNodeInstance) nodeInstance })
                .collect(Collectors.toList())
        assertEquals(2, unitsMeasureQuestionInstanceList.size())

        QuestionNodeInstance feetQuestion = unitsMeasureQuestionInstanceList.get(0)
        assertEquals("feet", feetQuestion.getName())
        Answer feetAnswer = feetQuestion.getAnswer()
        assertEquals("5", feetAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2310", feetAnswer.getPath())

        //Drop down list goes from 0-8
        InputSourceType feetInputSourceType = feetQuestion.getInputSourceType();
        assertNotNull(feetInputSourceType);
        assertEquals(9, feetInputSourceType.getValues().size());

        QuestionNodeInstance inchesQuestion = unitsMeasureQuestionInstanceList.get(1)
        assertEquals("inches ", inchesQuestion.getName())
        Answer inchesAnswer = inchesQuestion.getAnswer()
        assertEquals("10", inchesAnswer.getValue())
        assertEquals("Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2311", inchesAnswer.getPath())

        //Drop down list goes from 0-11
        InputSourceType inchesInputSourceType = inchesQuestion.getInputSourceType()
        assertNotNull(inchesInputSourceType)
        assertEquals(12, inchesInputSourceType.getValues().size())
    }

    ////////////////


    private void assertPetitionerToPetitionerSectionsWeightage(def packageSections) {
        assertNotNull(packageSections)
        def petitionerPackageSectionData = packageSections.stream()
                .filter({ packageSection -> packageSection.applicantTitle.equals(ApplicantType.Petitioner.name()) })
                .findFirst()
                .orElse(null)
        assertNotNull(petitionerPackageSectionData)

        def petitionerSections = petitionerPackageSectionData.sections
        assertNotNull(petitionerSections)
        assertEquals(petitionerSections.size(), 11)

        def nameSectionData = findSectionById(petitionerSections, 'Sec_2')
        assertNotNull(nameSectionData)
        assertEquals(nameSectionData.completionState, false)
        assertEquals(nameSectionData.completedPercentage, new Double(50), 0)

        def addressHistorySectionData = findSectionById(petitionerSections, 'Sec_addressHistory')
        assertNotNull(addressHistorySectionData)
        assertEquals(addressHistorySectionData.completionState, false)
        assertEquals(addressHistorySectionData.completedPercentage, new Double(90), 0)

        def contactInformationSectionData = findSectionById(petitionerSections, 'Sec_contactInformation')
        assertNotNull(contactInformationSectionData)
        assertEquals(contactInformationSectionData.completionState, false)
        assertEquals(contactInformationSectionData.completedPercentage, new Double(14), 0)

        def birthInformationSectionData = findSectionById(petitionerSections, 'Sec_birthInformation')
        assertNotNull(birthInformationSectionData)
        assertEquals(birthInformationSectionData.completionState, true)
        assertEquals(birthInformationSectionData.completedPercentage, new Double(100), 0)
    }


    private void assertBeneficiaryToBeneficiarySectionsWeightage(def packageSections) {
        assertNotNull(packageSections)
        def beneficiaryPackageSectionData = packageSections.stream()
                .filter({ packageSection -> packageSection.applicantTitle.equals(ApplicantType.Beneficiary.name()) })
                .findFirst()
                .orElse(null)
        assertNotNull(beneficiaryPackageSectionData)

        def beneficiarySections = beneficiaryPackageSectionData.sections
        assertNotNull(beneficiarySections)
        assertEquals(beneficiarySections.size(), 14)

        def nameSectionData = findSectionById(beneficiarySections, 'Sec_nameForBeneficiary')
        assertNotNull(nameSectionData)
        assertEquals(nameSectionData.completionState, false)
        assertEquals(nameSectionData.completedPercentage, new Double(50), 0)

        def birthInformationSectionData = findSectionById(beneficiarySections, 'Sec_birthInformationForBeneficiary')
        assertNotNull(birthInformationSectionData)
        assertEquals(birthInformationSectionData.completionState, true)
        assertEquals(birthInformationSectionData.completedPercentage, new Double(100), 0)

        def addressHistorySectionData = findSectionById(beneficiarySections, 'Sec_addressHistoryForBeneficiary')
        assertNotNull(addressHistorySectionData)
        assertEquals(addressHistorySectionData.completionState, false)
        assertEquals(addressHistorySectionData.completedPercentage, new Double(29), 0)

        def biographicInformationSectionData = findSectionById(beneficiarySections, 'Sec_biographicInformationForBeneficiary')
        assertNotNull(biographicInformationSectionData)
        assertEquals(biographicInformationSectionData.completionState, true)
        assertEquals(biographicInformationSectionData.completedPercentage, new Double(100), 0)
    }


    private void assertBeneficiaryToPetitionerSectionsWeightage(def packageSections) {
        assertNotNull(packageSections)
        def petitionerPackageSectionData = packageSections.stream()
                .filter({ packageSection -> packageSection.applicantTitle.equals(ApplicantType.Petitioner.name()) })
                .findFirst()
                .orElse(null)
        assertNotNull(petitionerPackageSectionData)

        def petitionerSections = petitionerPackageSectionData.sections
        assertNotNull(petitionerSections)
        assertEquals(petitionerSections.size(), 11)

        def nameSectionData = findSectionById(petitionerSections, 'Sec_2')
        assertNotNull(nameSectionData)
        assertEquals(nameSectionData.completionState, false)
        assertEquals(nameSectionData.completedPercentage, new Double(50), 0)

        def birthInformationSectionData = findSectionById(petitionerSections, 'Sec_birthInformation')
        assertNotNull(birthInformationSectionData)
        assertEquals(birthInformationSectionData.completionState, true)
        assertEquals(birthInformationSectionData.completedPercentage, new Double(100), 0)

        def bioInformationSectionData = findSectionById(petitionerSections, 'Sec_biographicInformation')
        assertNotNull(bioInformationSectionData)
        assertEquals(bioInformationSectionData.completionState, true)
        assertEquals(bioInformationSectionData.completedPercentage, new Double(100), 0)

        def addressHistorySectionData = findSectionById(petitionerSections, 'Sec_addressHistory')
        assertNotNull(addressHistorySectionData)
        assertEquals(addressHistorySectionData.completionState, false)
        assertEquals(addressHistorySectionData.completedPercentage, new Double(32), 0)

        def contactInformationSectionData = findSectionById(petitionerSections, 'Sec_contactInformation')
        assertNotNull(contactInformationSectionData)
        assertEquals(contactInformationSectionData.completionState, false)
        assertEquals(contactInformationSectionData.completedPercentage, new Double(71), 0)

        def employmentHistorySectionData = findSectionById(petitionerSections, 'Sec_employmentHistory')
        assertNotNull(employmentHistorySectionData)
        assertEquals(employmentHistorySectionData.completionState, true)
        assertEquals(employmentHistorySectionData.completedPercentage, new Double(100), 0)
    }


    private def findSectionById(sections, sectionId) {
        def sectionData = sections.stream()
                .filter({ section -> section.id.equals(sectionId) })
                .findFirst()
                .orElse(null)
        return sectionData
    }

}
