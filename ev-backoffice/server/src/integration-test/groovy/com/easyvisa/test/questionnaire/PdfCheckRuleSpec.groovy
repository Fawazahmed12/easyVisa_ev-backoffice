package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.utils.*
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

@Integration
class PdfCheckRuleSpec extends TestMockUtils {

    static final PdfForm FORM_130 = PdfForm.I130
    static final String FORM_130_FILENAME = 'I130.pdf'
    static final PdfForm FORM_134 = PdfForm.I134
    static final String FORM_134_FILENAME = 'I134.pdf'

    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private PdfPopulationService pdfPopulationService
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

    private String pdfFolder = null

    void setup() {
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }

    void test130Empty() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130(), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerUnemployed() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer>  answerList = AnswerListPdfRulesStub.petitionerUnemploymentFirst(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Unemployed'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerEmployed() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer>  answerList = AnswerListPdfRulesStub.petitionerEmploymentFirst(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Google'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerEmployedDouble() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerEmploymentDouble(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Google', 'Amazon'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerUnemployedAndEmployed() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerUnemploymentFirstEmployedSecond(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Unemployed', 'Amazon'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerUnemployedAndRetired() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerUnemploymentRetire(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Unemployed', 'Retired'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerEmployedAndRetired() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerEmploymentFirstAndRetired(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Google', 'Retired'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerFianceToBeneficiary() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        Applicant beneficiaryApplicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerFianceToBeneficiaryIntro(aPackage.id,
                petitionerApplicant.id, beneficiaryApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130(), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerSpouseToBeneficiary() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        Applicant beneficiaryApplicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerSpouseToBeneficiaryIntro(aPackage.id,
                petitionerApplicant.id, beneficiaryApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('', '', 'Spouse'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerSiblingToBeneficiary() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        Applicant beneficiaryApplicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerSiblingToBeneficiaryIntro(aPackage.id,
                petitionerApplicant.id, beneficiaryApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('', '', 'Spouse'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerParentToBeneficiary() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        Applicant beneficiaryApplicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerParentToBeneficiaryIntro(aPackage.id,
                petitionerApplicant.id, beneficiaryApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('', '', 'Spouse'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134Empty() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134(), FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134PetitionerEmployed() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerEmploymentFirst(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id, sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('Google', 'IT specialist'), FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134PetitionerSelfEmployed() throws Exception {
        given:
        String sectionId = 'Sec_employmentHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerEmploymentFirst(aPackage.id, petitionerApplicant.id,
                true)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('Google', '',
                'IT specialist'), FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap130(String employer1 = '', String employer2 = '', String benRelationship = '') {
        [
                'form1[0].#subform[3].Pt2Line40_EmployerOrCompName[0]': employer1,
                'form1[0].#subform[3].Pt2Line44_EmployerOrOrgName[0]' : employer2,
                'form1[0].#subform[5].Pt4Line31_Relationship[0]'      : benRelationship
        ]
    }

    private Map<String, String> assertionMap134(String employerName = '', String occupation = '', String selfEmployed = '') {
        [
                'form1[0].#subform[2].Pt3Line1a_EmployedType[0]'    : occupation,
                'form1[0].#subform[2].Pt3Line1b_SelfEmployedType[0]': selfEmployed,
                'form1[0].#subform[2].Pt3Line1a1_NameOfEmployer[0]' : employerName,
        ]
    }

}
