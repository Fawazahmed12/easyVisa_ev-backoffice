package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class QuestionnaireCompletionWeightageSpec extends TestMockUtils {

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService
    @Autowired
    private SectionCompletionStatusService sectionCompletionStatusService
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

    void testBirthInformationQuestionnaireWeightage() throws Exception {
        given:
        String birthInformationSectionId = 'Sec_birthInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> birthInformationAnswerList = AnswerListStub.petitionerBirthInformationAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                birthInformationSectionId, birthInformationAnswerList)

        when:
        sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, petitionerApplicant.id,
                birthInformationSectionId)
        def packageSections = packageQuestionnaireService.fetchPackageSections(aPackage.id)

        then:
        assertCompletionWeightageOfK1K3PackageSections(packageSections)

        cleanup:
        testHelper.clean()
    }

    private void assertCompletionWeightageOfK1K3PackageSections(def packageSections) {
        assertNotNull(packageSections)
        def petitionerPackageSectionData = packageSections.stream()
                .filter({ packageSection -> packageSection.applicantTitle.equals(ApplicantType.Petitioner.name()) })
                .findFirst()
                .orElse(null)
        assertNotNull(petitionerPackageSectionData)

        assertEquals(new Double(6), petitionerPackageSectionData.completedWeightage, 0)
        def petitionerSections = petitionerPackageSectionData.sections
        assertNotNull(petitionerSections)
        assertEquals(petitionerSections.size(), 11)

        def introSectionData = findSectionById(petitionerSections, 'Sec_1')
        assertNotNull(introSectionData)
        assertEquals(introSectionData.completionState, false)
        assertEquals(introSectionData.weightageValue, new Double(8.11), 0)

        def nameSectionData = findSectionById(petitionerSections, 'Sec_2')
        assertNotNull(nameSectionData)
        assertEquals(nameSectionData.completionState, false)
        assertEquals(nameSectionData.weightageValue, new Double(3.6), 0)

        def addressHistorySectionData = findSectionById(petitionerSections, 'Sec_addressHistory')
        assertNotNull(addressHistorySectionData)
        assertEquals(addressHistorySectionData.completionState, false)
        assertEquals(addressHistorySectionData.weightageValue, new Double(18.02), 0)

        def contactInformationSectionData = findSectionById(petitionerSections, 'Sec_contactInformation')
        assertNotNull(contactInformationSectionData)
        assertEquals(contactInformationSectionData.completionState, false)
        assertEquals(contactInformationSectionData.weightageValue, new Double(3.15), 0)

        def birthInformationSectionData = findSectionById(petitionerSections, 'Sec_birthInformation')
        assertNotNull(birthInformationSectionData)
        assertEquals(birthInformationSectionData.completionState, true)
        assertEquals(birthInformationSectionData.completedPercentage, new Double(100), 0)
        assertEquals(birthInformationSectionData.weightageValue, new Double(2.7), 0)

        def biographicInformationSectionData = findSectionById(petitionerSections, 'Sec_biographicInformation')
        assertNotNull(biographicInformationSectionData)
        assertEquals(biographicInformationSectionData.completionState, false)
        assertEquals(biographicInformationSectionData.weightageValue, new Double(6.76), 0)

        def legalStatusInUSSectionData = findSectionById(petitionerSections, 'Sec_legalStatusInUS')
        assertNotNull(legalStatusInUSSectionData)
        assertEquals(legalStatusInUSSectionData.completionState, false)
        assertEquals(legalStatusInUSSectionData.weightageValue, new Double(5.86), 0)

//        def incomeHistorySectionData = findSectionById(petitionerSections, 'Sec_incomeHistory')
//        assertNotNull(incomeHistorySectionData)
//        assertEquals(incomeHistorySectionData.completionState, false)
//        assertEquals(incomeHistorySectionData.weightageValue, new Double(0.33), 0)

//        def assetsSectionData = findSectionById(petitionerSections, 'Sec_assets')
//        assertNotNull(assetsSectionData)
//        assertEquals(assetsSectionData.completionState, false)
//        assertEquals(assetsSectionData.weightageValue, new Double(12.21), 0)

        def employmentHistorySectionData = findSectionById(petitionerSections, 'Sec_employmentHistory')
        assertNotNull(employmentHistorySectionData)
        assertEquals(employmentHistorySectionData.completionState, false)
        assertEquals(employmentHistorySectionData.weightageValue, new Double(10.81), 0)

        def criminalAndCivilHistorySectionData = findSectionById(petitionerSections, 'Sec_criminalAndCivilHistory')
        assertNotNull(criminalAndCivilHistorySectionData)
        assertEquals(criminalAndCivilHistorySectionData.completionState, false)
        assertEquals(criminalAndCivilHistorySectionData.weightageValue, new Double(17.57), 0)

        def familyInformationSectionData = findSectionById(petitionerSections, 'Sec_familyInformation')
        assertNotNull(familyInformationSectionData)
        assertEquals(familyInformationSectionData.completionState, false)
        assertEquals(familyInformationSectionData.weightageValue, new Double(13.96), 0)

        def relationshipToPetitionerSectionData = findSectionById(petitionerSections, 'Sec_relationshipToPetitioner')
        assertNotNull(relationshipToPetitionerSectionData)
        assertEquals(relationshipToPetitionerSectionData.completionState, false)
        assertEquals(relationshipToPetitionerSectionData.weightageValue, new Double(9.46), 0)

//        def supportAndContributionsSectionData = findSectionById(petitionerSections, 'Sec_supportAndContributions')
//        assertNotNull(supportAndContributionsSectionData)
//        assertEquals(supportAndContributionsSectionData.completionState, false)
//        assertEquals(supportAndContributionsSectionData.weightageValue, new Double(2.31), 0)
    }

    private def findSectionById(sections, sectionId) {
        def sectionData = sections.stream()
                .filter({ section -> section.id.equals(sectionId) })
                .findFirst()
                .orElse(null)
        return sectionData
    }

    void testTravelToTheUnitedStatesQuestionnaireWeightage() throws Exception {
        given:
        String travelToTheUnitedStatesSectionId = 'Sec_travelToTheUnitedStates'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerOpenPackage()

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> travelToTheUnitedStatesAnswerList = AnswerListStub
                .travelToUSSectionPdfPrintTextRuleAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id,
                travelToTheUnitedStatesSectionId, travelToTheUnitedStatesAnswerList)

        when:
        sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, applicant.id,
                travelToTheUnitedStatesSectionId)
        def packageSections = packageQuestionnaireService.fetchPackageSections(aPackage.id)

        then:
        assertCompletionWeightageOfSIX01PackageSections(packageSections)

        cleanup:
        testHelper.clean()
    }

    private void assertCompletionWeightageOfSIX01PackageSections(def packageSections) {
        assertNotNull(packageSections)
        def beneficiaryPackageSectionData = packageSections.stream()
                .filter({ packageSection -> packageSection.applicantTitle.equals(ApplicantType.Beneficiary.name()) })
                .findFirst()
                .orElse(null)
        assertNotNull(beneficiaryPackageSectionData)

        assertEquals(new Double(24), beneficiaryPackageSectionData.completedWeightage, 0)
        def beneficiarySections = beneficiaryPackageSectionData.sections
        assertNotNull(beneficiarySections)
        assertEquals(beneficiarySections.size(), 12)

        def introSectionData = findSectionById(beneficiarySections, 'Sec_introQuestionsForBeneficiary')
        assertNotNull(introSectionData)
        assertEquals(introSectionData.completionState, false)
        assertEquals(introSectionData.weightageValue, new Double(2.02), 0)

        def nameSectionData = findSectionById(beneficiarySections, 'Sec_uscisLocationInformation')
        assertNotNull(nameSectionData)
        assertEquals(nameSectionData.completionState, false)
        assertEquals(nameSectionData.weightageValue, new Double(2.02), 0)

        def addressHistorySectionData = findSectionById(beneficiarySections, 'Sec_nameForBeneficiary')
        assertNotNull(addressHistorySectionData)
        assertEquals(addressHistorySectionData.completionState, false)
        assertEquals(addressHistorySectionData.weightageValue, new Double(4.04), 0)

        def contactInformationSectionData = findSectionById(beneficiarySections, 'Sec_addressHistoryForBeneficiary')
        assertNotNull(contactInformationSectionData)
        assertEquals(contactInformationSectionData.completionState, false)
        assertEquals(contactInformationSectionData.weightageValue, new Double(11.62), 0)

        def birthInformationSectionData = findSectionById(beneficiarySections, 'Sec_contactInformationForBeneficiary')
        assertNotNull(birthInformationSectionData)
        assertEquals(birthInformationSectionData.completionState, false)
        assertEquals(birthInformationSectionData.weightageValue, new Double(4.04), 0)

        def biographicInformationSectionData = findSectionById(beneficiarySections, 'Sec_birthInformationForBeneficiary')
        assertNotNull(biographicInformationSectionData)
        assertEquals(biographicInformationSectionData.completionState, false)
        assertEquals(biographicInformationSectionData.weightageValue, new Double(2.53), 0)

        def legalStatusInUSSectionData = findSectionById(beneficiarySections, 'Sec_biographicInformationForBeneficiary')
        assertNotNull(legalStatusInUSSectionData)
        assertEquals(legalStatusInUSSectionData.completionState, false)
        assertEquals(legalStatusInUSSectionData.weightageValue, new Double(7.58), 0)

        def incomeHistorySectionData = findSectionById(beneficiarySections, 'Sec_personelInformationForBeneficiary')
        assertNotNull(incomeHistorySectionData)
        assertEquals(incomeHistorySectionData.completionState, false)
        assertEquals(incomeHistorySectionData.weightageValue, new Double(3.03), 0)

        def assetsSectionData = findSectionById(beneficiarySections, 'Sec_travelToTheUnitedStates')
        assertNotNull(assetsSectionData)
        assertEquals(assetsSectionData.completionState, false)
        assertEquals(assetsSectionData.weightageValue, new Double(7.58), 0)

        def employmentHistorySectionData = findSectionById(beneficiarySections, 'Sec_inadmissibilityAndOtherLegalIssues')
        assertNotNull(employmentHistorySectionData)
        assertEquals(employmentHistorySectionData.completionState, false)
        assertEquals(employmentHistorySectionData.weightageValue, new Double(20.2), 0)

        def criminalAndCivilHistorySectionData = findSectionById(beneficiarySections, 'Sec_extremeHardshipForRelatives')
        assertNotNull(criminalAndCivilHistorySectionData)
        assertEquals(criminalAndCivilHistorySectionData.completionState, false)
        assertEquals(criminalAndCivilHistorySectionData.weightageValue, new Double(13.13), 0)

        def supportAndContributionsSectionData = findSectionById(beneficiarySections, 'Sec_statementFromApplicant')
        assertNotNull(supportAndContributionsSectionData)
        assertEquals(supportAndContributionsSectionData.completionState, false)
        assertEquals(supportAndContributionsSectionData.weightageValue, new Double(1.01), 0)
    }

    void testAddressHistoryQuestionnaireWeightage() throws Exception {
        given:
        String addressHistorySectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> addressHistoryAnswerList = AnswerListStub.addressHistoryAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                addressHistorySectionId, addressHistoryAnswerList)

        when:
        sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, petitionerApplicant.id,
                addressHistorySectionId)
        def packageSections = packageQuestionnaireService.fetchPackageSections(aPackage.id)

        then:
        assertCompletionWeightageOfAddressHistory(packageSections)

        cleanup:
        testHelper.clean()
    }

    private void assertCompletionWeightageOfAddressHistory(def packageSections) {
        assertNotNull(packageSections)
        def petitionerPackageSectionData = packageSections.stream()
                .filter({ packageSection -> packageSection.applicantTitle.equals(ApplicantType.Petitioner.name()) })
                .findFirst()
                .orElse(null)
        assertNotNull(petitionerPackageSectionData)

        assertEquals(new Double(20), petitionerPackageSectionData.completedWeightage, 0)
        def petitionerSections = petitionerPackageSectionData.sections
        assertNotNull(petitionerSections)
        assertEquals(petitionerSections.size(), 11)

        def introSectionData = findSectionById(petitionerSections, 'Sec_1')
        assertNotNull(introSectionData)
        assertEquals(introSectionData.completionState, false)
        assertEquals(introSectionData.weightageValue, new Double(8.11), 0)

        def nameSectionData = findSectionById(petitionerSections, 'Sec_2')
        assertNotNull(nameSectionData)
        assertEquals(nameSectionData.completionState, false)
        assertEquals(nameSectionData.weightageValue, new Double(3.6), 0)

        def addressHistorySectionData = findSectionById(petitionerSections, 'Sec_addressHistory')
        assertNotNull(addressHistorySectionData)
        assertEquals(addressHistorySectionData.completionState, false)
        assertEquals(addressHistorySectionData.completedPercentage, new Double(90), 0)
        assertEquals(addressHistorySectionData.weightageValue, new Double(18.02), 0)

        def contactInformationSectionData = findSectionById(petitionerSections, 'Sec_contactInformation')
        assertNotNull(contactInformationSectionData)
        assertEquals(contactInformationSectionData.completionState, false)
        assertEquals(contactInformationSectionData.weightageValue, new Double(3.15), 0)

        def birthInformationSectionData = findSectionById(petitionerSections, 'Sec_birthInformation')
        assertNotNull(birthInformationSectionData)
        assertEquals(birthInformationSectionData.completionState, false)
        assertEquals(birthInformationSectionData.weightageValue, new Double(2.7), 0)

        def biographicInformationSectionData = findSectionById(petitionerSections, 'Sec_biographicInformation')
        assertNotNull(biographicInformationSectionData)
        assertEquals(biographicInformationSectionData.completionState, false)
        assertEquals(biographicInformationSectionData.weightageValue, new Double(6.76), 0)

        def legalStatusInUSSectionData = findSectionById(petitionerSections, 'Sec_legalStatusInUS')
        assertNotNull(legalStatusInUSSectionData)
        assertEquals(legalStatusInUSSectionData.completionState, false)
        assertEquals(legalStatusInUSSectionData.weightageValue, new Double(5.86), 0)

//        def incomeHistorySectionData = findSectionById(petitionerSections, 'Sec_incomeHistory')
//        assertNotNull(incomeHistorySectionData)
//        assertEquals(incomeHistorySectionData.completionState, false)
//        assertEquals(incomeHistorySectionData.weightageValue, new Double(0.33), 0)

//        def assetsSectionData = findSectionById(petitionerSections, 'Sec_assets')
//        assertNotNull(assetsSectionData)
//        assertEquals(assetsSectionData.completionState, false)
//        assertEquals(assetsSectionData.weightageValue, new Double(12.21), 0)

        def employmentHistorySectionData = findSectionById(petitionerSections, 'Sec_employmentHistory')
        assertNotNull(employmentHistorySectionData)
        assertEquals(employmentHistorySectionData.completionState, false)
        assertEquals(employmentHistorySectionData.weightageValue, new Double(10.81), 0)

        def criminalAndCivilHistorySectionData = findSectionById(petitionerSections, 'Sec_criminalAndCivilHistory')
        assertNotNull(criminalAndCivilHistorySectionData)
        assertEquals(criminalAndCivilHistorySectionData.completionState, false)
        assertEquals(criminalAndCivilHistorySectionData.weightageValue, new Double(17.57), 0)

        def familyInformationSectionData = findSectionById(petitionerSections, 'Sec_familyInformation')
        assertNotNull(familyInformationSectionData)
        assertEquals(familyInformationSectionData.completionState, false)
        assertEquals(familyInformationSectionData.weightageValue, new Double(13.96), 0)

        def relationshipToPetitionerSectionData = findSectionById(petitionerSections, 'Sec_relationshipToPetitioner')
        assertNotNull(relationshipToPetitionerSectionData)
        assertEquals(relationshipToPetitionerSectionData.completionState, false)
        assertEquals(relationshipToPetitionerSectionData.weightageValue, new Double(9.46), 0)

//        def supportAndContributionsSectionData = findSectionById(petitionerSections, 'Sec_supportAndContributions')
//        assertNotNull(supportAndContributionsSectionData)
//        assertEquals(supportAndContributionsSectionData.completionState, false)
//        assertEquals(supportAndContributionsSectionData.weightageValue, new Double(2.31), 0)
    }

    void testEmploymentHistoryCompletionPercentage() throws Exception {
        given:
        String addressHistorySectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> addressHistoryAnswerList = AnswerListStub.employmentHistoryPercentageAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                addressHistorySectionId, addressHistoryAnswerList)

        when:
        sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, petitionerApplicant.id, addressHistorySectionId)
        def packageSections = packageQuestionnaireService.fetchPackageSections(aPackage.id)

        then:
        assertEmploymentHistoryCompletionPercentage(packageSections)

        cleanup:
        testHelper.clean()
    }

    private void assertEmploymentHistoryCompletionPercentage(def packageSections) {
        assertNotNull(packageSections)
        def petitionerPackageSectionData = packageSections.stream()
                .filter({ packageSection -> packageSection.applicantTitle.equals(ApplicantType.Petitioner.name()) })
                .findFirst()
                .orElse(null)
        assertNotNull(petitionerPackageSectionData)

        def petitionerSections = petitionerPackageSectionData.sections
        assertNotNull(petitionerSections)
        assertEquals(petitionerSections.size(), 11)

        def employmentHistorySectionData = findSectionById(petitionerSections, 'Sec_employmentHistory')
        assertNotNull(employmentHistorySectionData)
        assertEquals(employmentHistorySectionData.completionState, false)
        assertEquals(employmentHistorySectionData.completedPercentage, new Double(83), 0)
        assertEquals(employmentHistorySectionData.weightageValue, new Double(10.81), 0)
    }
}
