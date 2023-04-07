package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestPdfUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

@Integration
class PdfShiftRuleSpec extends TestMockUtils {

    private static final String NA = 'N/A'
    private static final String US = 'United States'

    private static final PdfForm FORM_129F = PdfForm.I129F
    private static final String FORM_129F_FILENAME = 'I129F.pdf'
    private static final PdfForm FORM_130 = PdfForm.I130
    private static final String FORM_130_FILENAME = 'I130.pdf'
    private static final PdfForm FORM_134 = PdfForm.I134
    private static final String FORM_134_FILENAME = 'I134.pdf'
    private static final PdfForm FORM_864 = PdfForm.I864
    private static final String FORM_864_FILENAME = 'I864.pdf'

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
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    @Autowired
    private ProfileService profileService
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

    /*void test129Empty() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129(), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129SingleState() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18StateSingle(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('WA', US), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129DoubleState() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18StateDouble(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('WA', US, 'VA', US),
                FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129TripleState() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18StateTriple(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('WA', US, 'VA', US,
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129SingleCountry() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18CountrySingle(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129(NA, 'France'), FORM_129F_FILENAME,
                pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129DoubleCountry() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18CountryDouble(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129(NA, 'France', NA, 'Germany'),
                FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129TripleCountry() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18CountryTriple(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129(NA, 'France', NA, 'Germany',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129SingleStateCountry() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18StateCountrySingle(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('WA', US, NA, 'France'),
                FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129DoubleStateCountry() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18StateCountryDouble(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('WA', US, 'VA', US,
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129SingleStateDoubleCountry() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18SingleStateDoubleCountry(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('WA', US, NA, 'France',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129DoubleStateSingleCountry() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18DoubleStateSingleCountry(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('WA', US, 'VA', US,
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134NoDependents() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134(), FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134ChildDependentsSingle() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsChildrenSingle(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('James', 'Junior', 'Green',
                '05/07/2018', 'child', 'yes'), FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134ChildDependentsDouble() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsChildrenDouble(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('James', 'Junior', 'Green',
                '05/07/2018', 'child', 'yes', '',
                'Peter', 'Jack', 'Pen', '09/17/2017', 'child', 'yes'),
                FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134ChildDependentsTriple() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsChildrenTriple(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('James', 'Junior', 'Green',
                '05/07/2018', 'child', 'yes', '',
                'Peter', 'Jack', 'Pen', '09/17/2017', 'child', 'yes', '',
                'Deb', 'Senior', 'White', '03/12/2015', 'child', 'yes'),
                FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134ChildDependentsQuadro() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsChildrenQuadro(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('James', 'Junior', 'Green',
                '05/07/2018', 'child', 'yes', '',
                'Peter', 'Jack', 'Pen', '09/17/2017', 'child', 'yes', '',
                'Deb', 'Senior', 'White', '03/12/2015', 'child', 'yes', '',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner),
                FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134NonChildDependentsSingle() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsNonChildrenSingle(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('Andrew', 'New', 'Black',
                '04/16/2000', 'cousin', '', 'yes'), FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134NonChildDependentsDouble() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsNonChildrenDouble(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('Andrew', 'New', 'Black',
                '04/16/2000', 'cousin', '', 'yes',
                'Judith', 'Sister', 'Rock', '08/02/2015', 'niece', '', 'yes'),
                FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134NonChildDependentsTriple() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsNonChildrenTriple(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('Andrew', 'New', 'Black',
                '04/16/2000', 'cousin', '', 'yes',
                'Judith', 'Sister', 'Rock', '08/02/2015', 'niece', '', 'yes',
                'Bob', 'Singer', 'Marley', '09/17/1965', 'uncle', '', 'yes'),
                FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134NonChildDependentsQuadro() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsNonChildrenQuadro(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('Andrew', 'New', 'Black',
                '04/16/2000', 'cousin', '', 'yes',
                'Judith', 'Sister', 'Rock', '08/02/2015', 'niece', '', 'yes',
                'Bob', 'Singer', 'Marley', '09/17/1965', 'uncle', '', 'yes',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134ChildrenNonChildDependentsSingle() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsChildrenNonChildrenSingle(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('James', 'Junior', 'Green',
                '05/07/2018', 'child', 'yes', '',
                'Andrew', 'New', 'Black', '04/16/2000', 'cousin', '', 'yes'),
                FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134TripleChildrenSingleSingleNonChildDependents() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList =
                AnswerListPdfRulesStub.petitionerDependentsChildrenTripleNonChildrenSingle(aPackage.id,
                        petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('James', 'Junior', 'Green',
                '05/07/2018', 'child', 'yes', '',
                'Peter', 'Jack', 'Pen', '09/17/2017', 'child', 'yes', '',
                'Deb', 'Senior', 'White', '03/12/2015', 'child', 'yes', '',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner),
                FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134QuadroChildrenSingleNonChildDependents() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList =
                AnswerListPdfRulesStub.petitionerDependentsChildrenQuadroNonChildrenSingle(aPackage.id,
                        petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('James', 'Junior', 'Green',
                '05/07/2018', 'child', 'yes', '',
                'Peter', 'Jack', 'Pen', '09/17/2017', 'child', 'yes', '',
                'Deb', 'Senior', 'White', '03/12/2015', 'child', 'yes', '',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test134EightChildrenSingleNonChildDependents() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDependentsChildrenSevenNonChildrenSingle(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('James', 'Junior', 'Green',
                '05/07/2018', 'child', 'yes', '',
                'Peter', 'Jack', 'Pen', '09/17/2017', 'child', 'yes', '',
                'Deb', 'Senior', 'White', '03/12/2015', 'child', 'yes', '',
                PdfUtils.SEE_CONTINUATIONS, aPackage.petitioner), FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864Empty() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864(), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleChildren() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndTwoBeneficiariesOpenPackage()
        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryChildrenSingle(aPackage.id, benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('2', 'James', 'Brown', 'Ricky',
                'Child', '04/01/2019', '123456789', '123456789012'), FORM_864_FILENAME,
                pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864DoubleChildren() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndTwoBeneficiariesOpenPackage()
        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryChildrenDouble(aPackage.id, benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('2', 'James', 'Brown', 'Ricky',
                'Child', '04/01/2019', '123456789', '123456789012',
                'Indira', 'Gandi', 'Senior', 'Child', '06/01/2018', '234567890',
                '234567890123'), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864FiveChildren() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryChildrenFive(aPackage.id, benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('1', 'James', 'Brown', 'Ricky',
                'Child', '04/01/2019', '123456789', '123456789012',
                'Indira', 'Gandi', 'Senior', 'Child', '06/01/2018', '234567890', '234567890123',
                'Anthony', 'Cook', 'Joshua', 'Child', '03/10/2018', '345678901', '345678901234',
                'Jacob', 'Williams', 'Noah', 'Child', '01/01/2018', '456789012', '456789012345',
                'Isabella', 'Hill', 'Olivia', 'Child', '07/15/2017', '567890123', '567890123456'),
                FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SixChildren() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryChildrenSix(aPackage.id, benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('1', 'James', 'Brown', 'Ricky', 'Child',
                '04/01/2019', '123456789', '123456789012',
                'Indira', 'Gandi', 'Senior', 'Child', '06/01/2018', '234567890', '234567890123',
                'Anthony', 'Cook', 'Joshua', 'Child', '03/10/2018', '345678901', '345678901234',
                'Jacob', 'Williams', 'Noah', 'Child', '01/01/2018', '456789012', '456789012345',
                'Isabella', 'Hill', 'Olivia', 'Child', '07/15/2017', '567890123', '567890123456',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864Spouse() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiarySpouse(aPackage.id, benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId,
                null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('1', 'Matthew', 'Jackson', 'Liam',
                'Spouse', '01/01/1970', '876543210', '109876543210'),
                FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleChildrenSpouse() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryDependentChildrenSingleSpouse(aPackage.id,
                benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('1', 'James', 'Brown', 'Ricky',
                'Child', '04/01/2019', '123456789', '123456789012',
                'Matthew', 'Jackson', 'Liam', 'Spouse', '01/01/1970', '876543210',
                '109876543210'), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864QuadroChildrenSpouse() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryDependentChildrenQuadroSpouse(aPackage.id,
                benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('1', 'James', 'Brown', 'Ricky', 'Child',
                '04/01/2019', '123456789', '123456789012',
                'Indira', 'Gandi', 'Senior', 'Child', '06/01/2018', '234567890', '234567890123',
                'Anthony', 'Cook', 'Joshua', 'Child', '03/10/2018', '345678901', '345678901234',
                'Jacob', 'Williams', 'Noah', 'Child', '01/01/2018', '456789012', '456789012345',
                'Matthew', 'Jackson', 'Liam', 'Spouse', '01/01/1970', '876543210', '109876543210'),
                FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864FiveChildrenSpouse() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryDependentChildrenFiveSpouse(aPackage.id,
                benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('1', 'James', 'Brown', 'Ricky', 'Child',
                '04/01/2019', '123456789', '123456789012',
                'Indira', 'Gandi', 'Senior', 'Child', '06/01/2018', '234567890', '234567890123',
                'Anthony', 'Cook', 'Joshua', 'Child', '03/10/2018', '345678901', '345678901234',
                'Jacob', 'Williams', 'Noah', 'Child', '01/01/2018', '456789012', '456789012345',
                'Isabella', 'Hill', 'Olivia', 'Child', '07/15/2017', '567890123', '567890123456',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SixChildrenSpouse() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryDependentChildrenSixSpouse(aPackage.id,
                benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('1', 'James', 'Brown', 'Ricky', 'Child',
                '04/01/2019', '123456789', '123456789012',
                'Indira', 'Gandi', 'Senior', 'Child', '06/01/2018', '234567890', '234567890123',
                'Anthony', 'Cook', 'Joshua', 'Child', '03/10/2018', '345678901', '345678901234',
                'Jacob', 'Williams', 'Noah', 'Child', '01/01/2018', '456789012', '456789012345',
                'Isabella', 'Hill', 'Olivia', 'Child', '07/15/2017', '567890123', '567890123456',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130Empty() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Petitioner(), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerSpouse() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerCurrentSpouse(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Petitioner('Pink', 'Teresa', 'Ane', 'TO PRESENT'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerPreviousSpouse() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDivorcedPreviousSpouse(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Petitioner('Perez', 'Addison', 'Ella',
                '01/01/2018'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerPreviousDoubleSpouse() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDivorcedDoublePreviousSpouse(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Petitioner('Perez', 'Addison', 'Ella', '01/01/2018',
                'Murphy', 'Lily', 'Avery', '01/01/2017'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerPreviousTripleSpouse() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDivorcedTriplePreviousSpouse(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Petitioner('Perez', 'Addison', 'Ella', '01/01/2018',
                'Murphy', 'Lily', 'Avery', '01/01/2017',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerSpousePreviousSpouse() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerMarriedPreviousSpouse(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Petitioner('Pink', 'Teresa', 'Ane', 'TO PRESENT',
                'Perez', 'Addison', 'Ella', '01/01/2018'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerSpouseDoublePreviousSpouse() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerMarriedDoublePreviousSpouse(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Petitioner('Pink', 'Teresa', 'Ane', 'TO PRESENT',
                'Perez', 'Addison', 'Ella', '01/01/2018',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130BeneficiaryEmpty() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Beneficiary(), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130BeneficiarySingleChildrenPreviousSpouse() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryDivorcedSingleChildrenPreviousSpouse(aPackage.id,
                benApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Beneficiary('Young', 'Alexander', 'Daniel',
                '05/01/2018', '', '', '', '',
                'Brown', 'James', 'Ricky', 'Child', '04/01/2019'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130BeneficiarySingleChildrenCurrentSpouse() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryMarriedSingleChildren(aPackage.id,
                benApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Beneficiary('Jackson', 'Matthew', 'Liam',
                'TO PRESENT', '', '', '', '',
                '', '', '', '', '', '',
                'Brown', 'James', 'Ricky', 'Child', '04/01/2019'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130BeneficiaryDoubleChildrenMarriedPreviousSpouse() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryMarriedDoubleChildrenPreviousSpouse(aPackage.id,
                benApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130Beneficiary('Jackson', 'Matthew', 'Liam',
                'TO PRESENT', 'Young', 'Alexander', 'Daniel', '05/01/2018',
                '', '', '', '', '', '',
                'Brown', 'James', 'Ricky', 'Child', '04/01/2019', '',
                'Gandi', 'Indira', 'Senior', 'Child', '06/01/2018', 'United States'),
                FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }
    */


    void test864Empty() throws Exception {
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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('1'), FORM_864_FILENAME, pdfFolder)

        cleanup:NameSectionApplicantTypeSpec
        testHelper.clean()
    }


    private Map<String, String> assertionMap129(String state1 = '', String country1 = '', String state2 = '',
                                                String country2 = '', String addPartValue = '', Petitioner petitioner = null) {
        Map<String, String> result = [
                'form1[0].#subform[3].Pt1Line50a_State[0]'                     : state1,
                'form1[0].#subform[3].Pt1Line50b_CountryOfCitzOrNationality[0]': country1,
                'form1[0].#subform[3].Pt1Line51a_State[0]'                     : state2,
                'form1[0].#subform[3].Pt1Line51b_CountryOfCitzOrNationality[0]': country2
        ]
        TestPdfUtils.assertionMapAdditionalPart(result, addPartValue, petitioner, 'form1[0].#subform[12].Line1_AdditionalInfo[0]',
                'form1[0].#subform[12].Pt1Line6a_FamilyName[1]', 'form1[0].#subform[12].Pt1Line6b_GivenName[1]',
                'form1[0].#subform[12].Pt1Line6c_MiddleName[1]', 'form1[0].#subform[12].Pt1Line1_AlienNumber[1]')
        result
    }

    private Map<String, String> assertionMap130Petitioner(String lName1 = '', String fName1 = '', String mName1 = '', String endDate1 = '',
                                                          String lName2 = '', String fName2 = '', String mName2 = '', String endDate2 = '',
                                                          String addPartValue = '', Petitioner petitioner = null) {
        Map<String, String> result = [
                'form1[0].#subform[2].PtLine20a_FamilyName[0]'       : lName1,
                'form1[0].#subform[2].Pt2Line20b_GivenName[0]'       : fName1,
                'form1[0].#subform[2].Pt2Line20c_MiddleName[0]'      : mName1,
                'form1[0].#subform[2].Pt2Line21_DateMarriageEnded[0]': endDate1,
                'form1[0].#subform[2].Pt2Line22a_FamilyName[0]'      : lName2,
                'form1[0].#subform[2].Pt2Line22b_GivenName[0]'       : fName2,
                'form1[0].#subform[2].Pt2Line22c_MiddleName[0]'      : mName2,
                'form1[0].#subform[2].Pt2Line23_DateMarriageEnded[0]': endDate2
        ]
        addPart130(result, addPartValue, petitioner)
        result
    }

    private Map<String, String> assertionMap130Beneficiary(String spLName1 = '', String spFName1 = '', String spMName1 = '', String spEndDate1 = '',
                                                           String spLName2 = '', String spFName2 = '', String spMName2 = '', String spEndDate2 = '',
                                                           String pLName1 = '', String pFName1 = '', String pMName1 = '', String pRel1 = '', String pBirthDate1 = '', String pBirthCountry1 = '',
                                                           String pLName2 = '', String pFName2 = '', String pMName2 = '', String pRel2 = '', String pBirthDate2 = '', String pBirthCountry2 = '',
                                                           String pLName3 = '', String pFName3 = '', String pMName3 = '', String pRel3 = '', String pBirthDate3 = '', String pBirthCountry3 = '',
                                                           String addPartValue = '', Petitioner petitioner = null) {
        Map<String, String> result = [
                'form1[0].#subform[5].Pt4Line16a_FamilyName[0]'      : spLName1,
                'form1[0].#subform[5].Pt4Line16b_GivenName[0]'       : spFName1,
                'form1[0].#subform[5].Pt4Line16c_MiddleName[0]'      : spMName1,
                'form1[0].#subform[5].Pt4Line17_DateMarriageEnded[0]': spEndDate1,
                'form1[0].#subform[5].Pt4Line18a_FamilyName[0]'      : spLName2,
                'form1[0].#subform[5].Pt4Line18b_GivenName[0]'       : spFName2,
                'form1[0].#subform[5].Pt4Line18c_MiddleName[0]'      : spMName2,
                'form1[0].#subform[5].Pt4Line17_DateMarriageEnded[1]': spEndDate2,
                'form1[0].#subform[5].Pt4Line30a_FamilyName[0]'      : pLName1,
                'form1[0].#subform[5].Pt4Line30b_GivenName[0]'       : pFName1,
                'form1[0].#subform[5].Pt4Line30c_MiddleName[0]'      : pMName1,
                'form1[0].#subform[5].Pt4Line31_Relationship[0]'     : pRel1,
                'form1[0].#subform[5].Pt4Line32_DateOfBirth[0]'      : pBirthDate1,
                'form1[0].#subform[5].Pt4Line49_CountryOfBirth[0]'   : pBirthCountry1,
                'form1[0].#subform[5].Pt4Line34a_FamilyName[0]'      : pLName2,
                'form1[0].#subform[5].Pt4Line34b_GivenName[0]'       : pFName2,
                'form1[0].#subform[5].Pt4Line34c_MiddleName[0]'      : pMName2,
                'form1[0].#subform[5].Pt4Line35_Relationship[0]'     : pRel2,
                'form1[0].#subform[5].Pt4Line36_DateOfBirth[0]'      : pBirthDate2,
                'form1[0].#subform[5].Pt4Line37_CountryOfBirth[0]'   : pBirthCountry2,
                'form1[0].#subform[5].Pt4Line38a_FamilyName[0]'      : pLName3,
                'form1[0].#subform[5].Pt4Line38b_GivenName[0]'       : pFName3,
                'form1[0].#subform[5].Pt4Line38c_MiddleName[0]'      : pMName3,
                'form1[0].#subform[5].Pt4Line39_Relationship[0]'     : pRel3,
                'form1[0].#subform[5].Pt4Line40_DateOfBirth[0]'      : pBirthDate3,
                'form1[0].#subform[5].Pt4Line41_CountryOfBirth[0]'   : pBirthCountry3,
                'form1[0].#subform[6].Pt4Line42a_FamilyName[0]'      : '',
                'form1[0].#subform[6].Pt4Line42b_GivenName[0]'       : '',
                'form1[0].#subform[6].Pt4Line42c_MiddleName[0]'      : '',
                'form1[0].#subform[6].Pt4Line43_Relationship[0]'     : '',
                'form1[0].#subform[6].Pt4Line44_DateOfBirth[0]'      : '',
                'form1[0].#subform[6].Pt4Line45_CountryOfBirth[0]'   : ''
        ]
        addPart130(result, addPartValue, petitioner)
        result
    }

    private void addPart130(Map<String, String> result, String addPartValue, Petitioner petitioner) {
        TestPdfUtils.assertionMapAdditionalPart(result, addPartValue, petitioner, 'form1[0].#subform[11].Pt9Line3d_AdditionalInfo[0]',
                'form1[0].#subform[11].Pt2Line4a_FamilyName[1]', 'form1[0].#subform[11].Pt2Line4b_GivenName[1]',
                'form1[0].#subform[11].Pt2Line4c_MiddleName[1]', 'form1[0].#subform[11].Pt2Line1_AlienNumber[1]')
    }

    private Map<String, String> assertionMap134(String fName1 = '', String mName1 = '', String lName1 = '', String dof1 = '',
                                                String rel1 = '', String wholly1 = '', String part1 = '',
                                                String fName2 = '', String mName2 = '', String lName2 = '', String dof2 = '',
                                                String rel2 = '', String wholly2 = '', String part2 = '',
                                                String fName3 = '', String mName3 = '', String lName3 = '', String dof3 = '',
                                                String rel3 = '', String wholly3 = '', String part3 = '',
                                                String addPartValue = '', Petitioner petitioner = null) {
        Map<String, String> result = [
                'form1[0].#subform[2].Pt3Line10b_GivenName[0]'  : fName1,
                'form1[0].#subform[2].Pt3Line10c_MiddleName[0]' : mName1,
                'form1[0].#subform[2].Pt3Line10a_FamilyName[0]' : lName1,
                'form1[0].#subform[2].Pt3Line12_DateofBirth[0]' : dof1,
                'form1[0].#subform[2].Pt3Line11_Relationship[0]': rel1,
                'form1[0].#subform[2].Pt3Line13[1]'             : wholly1,
                'form1[0].#subform[2].Pt3Line13[0]'             : part1,
                'form1[0].#subform[2].Pt3Line14b_GivenName[0]'  : fName2,
                'form1[0].#subform[2].Pt3Line14c_MiddleName[0]' : mName2,
                'form1[0].#subform[2].Pt3Line14a_FamilyName[0]' : lName2,
                'form1[0].#subform[2].Pt3Line16_DateofBirth[0]' : dof2,
                'form1[0].#subform[2].Pt3Line15_Relationship[0]': rel2,
                'form1[0].#subform[3].Pt3Line17[1]'             : wholly2,
                'form1[0].#subform[3].Pt3Line17[0]'             : part2,
                'form1[0].#subform[3].Pt3Line18b_GivenName[0]'  : fName3,
                'form1[0].#subform[3].Pt3Line18c_MiddleName[0]' : mName3,
                'form1[0].#subform[3].Pt3Line18a_FamilyName[0]' : lName3,
                'form1[0].#subform[3].Pt3Line20_DateofBirth[0]' : dof3,
                'form1[0].#subform[3].Pt3Line19_Relationship[0]': rel3,
                'form1[0].#subform[3].Pt3Line21[0]'             : wholly3,
                'form1[0].#subform[3].Pt3Line21[1]'             : part3
        ]
        TestPdfUtils.assertionMapAdditionalPart(result, addPartValue, petitioner, 'form1[0].#subform[7].P7_Line3d_AdditionalInfo[0]',
                'form1[0].#subform[7].Pt1Line1a_FamilyName[0]', 'form1[0].#subform[7].Pt1Line1b_GivenName[0]',
                'form1[0].#subform[7].Pt1Line1c_MiddleName[0]', 'form1[0].#subform[7].Pt1Line8_AlienNumber[0]')
        result
    }

    private Map<String, String> assertionMap864(String count = '',
                                                String fname1 = '', String lname1 = '', String mname1 = '', String rel1 = '', String dof1 = '', String aNum1 = '', String accNum1 = '',
                                                String fname2 = '', String lname2 = '', String mname2 = '', String rel2 = '', String dof2 = '', String aNum2 = '', String accNum2 = '',
                                                String fname3 = '', String lname3 = '', String mname3 = '', String rel3 = '', String dof3 = '', String aNum3 = '', String accNum3 = '',
                                                String fname4 = '', String lname4 = '', String mname4 = '', String rel4 = '', String dof4 = '', String aNum4 = '', String accNum4 = '',
                                                String fname5 = '', String lname5 = '', String mname5 = '', String rel5 = '', String dof5 = '', String aNum5 = '', String accNum5 = '',
                                                String addPartValue = '', Petitioner petitioner = null) {
        Map<String, String> result = [
                'form1[0].#subform[1].P3_Line3a_FamilyName[0]'              : lname1,
                'form1[0].#subform[1].P3_Line3b_GivenName[0]'               : fname1,
                'form1[0].#subform[1].P3_Line3c_MiddleName[0]'              : mname1,
                'form1[0].#subform[1].P3_Line4_Relationship[0]'             : rel1,
                'form1[0].#subform[1].P3_Line_DateOfBirth[0]'               : dof1,
                'form1[0].#subform[1].P2_Line5_AlienNumber[2]'              : aNum1,
                'form1[0].#subform[1].#area[4].P3_Line7_AcctIdentifier[0]'  : accNum1,
                'form1[0].#subform[1].P3_Line8a_FamilyName[0]'              : lname2,
                'form1[0].#subform[1].P3_Line8b_GivenName[0]'               : fname2,
                'form1[0].#subform[1].P3_Line8c_MiddleName[0]'              : mname2,
                'form1[0].#subform[1].P3_Line9_Relationship[0]'             : rel2,
                'form1[0].#subform[1].P3_Line10_DateOfBirth[0]'             : dof2,
                'form1[0].#subform[1].#area[5].P3_Line11_AlienNumber[0]'    : aNum2,
                'form1[0].#subform[1].#area[6].P3_Line12_AcctIdentifier[0]' : accNum2,
                'form1[0].#subform[1].P3_Line13a_FamilyName[0]'             : lname3,
                'form1[0].#subform[1].P3_Line13b_GivenName[0]'              : fname3,
                'form1[0].#subform[1].P3_Line13c_MiddleName[0]'             : mname3,
                'form1[0].#subform[1].P3_Line14_Relationship[0]'            : rel3,
                'form1[0].#subform[1].P3_Line15_DateOfBirth[0]'             : dof3,
                'form1[0].#subform[1].P2_Line5_AlienNumber[1]'              : aNum3,
                'form1[0].#subform[1].#area[8].P3_Line17_AcctIdentifier[0]' : accNum3,
                'form1[0].#subform[1].P3_Line18a_FamilyName[0]'             : lname4,
                'form1[0].#subform[1].P3_Line18b_GivenName[0]'              : fname4,
                'form1[0].#subform[1].P3_Line18c_MiddleName[0]'             : mname4,
                'form1[0].#subform[1].P3_Line19_Relationship[0]'            : rel4,
                'form1[0].#subform[1].P3_Line20_DateOfBirth[0]'             : dof4,
                'form1[0].#subform[1].#area[9].P3_Line21_AlienNumber[0]'    : aNum4,
                'form1[0].#subform[1].#area[10].P3_Line22_AcctIdentifier[0]': accNum4,
                'form1[0].#subform[1].P3_Line23a_FamilyName[0]'             : lname5,
                'form1[0].#subform[1].P3_Line23b_GivenName[0]'              : fname5,
                'form1[0].#subform[1].P3_Line23c_MiddleName[0]'             : mname5,
                'form1[0].#subform[1].P3_Line24_Relationship[0]'            : rel5,
                'form1[0].#subform[1].P3_Line25_DateOfBirth[0]'             : dof5,
                'form1[0].#subform[1].#area[11].P3_Line26_AlienNumber[0]'   : aNum5,
                'form1[0].#subform[1].#area[12].P3_Line27_AcctIdentifier[0]': accNum5,
                'form1[0].#subform[2].P3_Line28_TotalNumberofImmigrants[0]' : count,
                'form1[0].#subform[3].P5_Line1_Number[0]'                   : count
        ]
        TestPdfUtils.assertionMapAdditionalPart(result, addPartValue, petitioner, 'form1[0].#subform[26].P11_Line3d_AdditionalInfo[0]',
                'form1[0].#subform[26].P4_Line1a_FamilyName[1]', 'form1[0].#subform[26].P4_Line1b_GivenName[1]',
                'form1[0].#subform[26].P4_Line1c_MiddleName[1]', 'form1[0].#subform[26].#area[19].P4_Line12_AlienNumber[1]')
        result
    }

}
