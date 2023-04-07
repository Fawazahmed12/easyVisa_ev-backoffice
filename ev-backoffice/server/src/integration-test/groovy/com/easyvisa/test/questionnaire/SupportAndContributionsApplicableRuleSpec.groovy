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
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import static org.junit.Assert.*

@Integration
@Ignore
//As of now we are removing Form_134.
class SupportAndContributionsApplicableRuleSpec extends TestMockUtils {

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

    void testSupportAndContributionsQuestionnaireForTheRelationFiance() throws Exception {
        given:
        String sectionId = 'Sec_supportAndContributions'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.form864ExclusionIncomeHistoryAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, petitionerApplicant.id, sectionId)
        def packageSections = packageQuestionnaireService.fetchPackageSections(aPackage.id)

        then:
        assertSupportAndContributionsVisibilityForTheRelationFiance(packageSections)

        cleanup:
        testHelper.clean()
    }

    private void assertSupportAndContributionsVisibilityForTheRelationFiance(def packageSections) {
        assertNotNull(packageSections)
        def petitionerPackageSectionData = packageSections.stream()
                .filter({ packageSection -> packageSection.applicantTitle.equals(ApplicantType.Petitioner.name()) })
                .findFirst()
                .orElse(null)
        assertNotNull(petitionerPackageSectionData)

        def petitionerSections = petitionerPackageSectionData.sections
        assertNotNull(petitionerSections)
        assertEquals(petitionerSections.size(), 14)

        def supportAndContributionsSectionData = findSectionById(petitionerSections, 'Sec_supportAndContributions')
        assertNotNull(supportAndContributionsSectionData)
    }

    private def findSectionById(sections, sectionId) {
        def sectionData = sections.stream()
                .filter({ section -> section.id.equals(sectionId) })
                .findFirst()
                .orElse(null)
        return sectionData
    }

    void testSupportAndContributionsQuestionnaireForTheRelationSpouse() throws Exception {
        given:
        String sectionId = 'Sec_supportAndContributions'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.form134ExclusionIntroSectionAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        sectionCompletionStatusService.updateSectionCompletionStatus(aPackage.id, petitionerApplicant.id, sectionId)
        def packageSections = packageQuestionnaireService.fetchPackageSections(aPackage.id)

        then:
        assertSupportAndContributionsVisibilityForTheRelationSpouse(packageSections)

        cleanup:
        testHelper.clean()
    }

    private void assertSupportAndContributionsVisibilityForTheRelationSpouse(def packageSections) {
        assertNotNull(packageSections)
        def petitionerPackageSectionData = packageSections.stream()
                .filter({ packageSection -> packageSection.applicantTitle.equals(ApplicantType.Petitioner.name()) })
                .findFirst()
                .orElse(null)
        assertNotNull(petitionerPackageSectionData)

        def petitionerSections = petitionerPackageSectionData.sections
        assertNotNull(petitionerSections)
        assertEquals(petitionerSections.size(), 13)

        def supportAndContributionsSectionData = findSectionById(petitionerSections, 'Sec_supportAndContributions')
        assertNull(supportAndContributionsSectionData)
    }
}
