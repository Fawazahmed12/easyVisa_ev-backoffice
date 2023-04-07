package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.CitizenshipStatus
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.utils.AnswerListPdfRulesStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestPdfUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Ignore
import spock.lang.Specification

@Integration
class PdfBenefitCategoryRuleSpec extends TestMockUtils {

    static final PdfForm FORM_129F = PdfForm.I129F
    static final String FORM_129F_FILENAME = 'I129F.pdf'
    static final PdfForm FORM_485 = PdfForm.I485
    static final String FORM_485_FILENAME = 'I485.pdf'

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

    void test129Empty() throws Exception {
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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129(), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerSingle() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerSingle(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('yes'), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerMarriageAnnulled() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerMarriageAnnulled(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('yes'), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerMarried() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerMarried(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('', 'yes'), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerDivorced() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDivorced(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id, sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('', '', 'yes'), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerWidowed() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerWidowed(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('', '', '', 'yes'),
                FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerSingleK2() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerSingle(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('yes'), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    //TODO:currently impossible to generate I-129F for Beneficiary and Petitioner of K2/K4 category
    @Ignore
    void test129PetitionerMarriageAnnulledK2() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerMarriageAnnulled(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129(), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerMarriedK2() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerMarried(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('', 'yes'), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerDivorcedK2() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerDivorced(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('', '', 'yes'),
                FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerWidowedK2() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerWidowed(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129('', '', '', 'yes'),
                FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485Empty() throws Exception {
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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('', '', ''), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485AutoLprSpouse() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.LPRSPOUSE)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('', '', 'yes'),
                FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485AutoLprChild() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.LPRCHILD)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('', '', 'yes'),
                FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485AutoIR1() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('yes'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485AutoIR2() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR2)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('yes'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485AutoIR5() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR5)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('yes'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485AutoF1() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F1_B)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('', 'yes'), FORM_485_FILENAME,
                pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485AutoF2() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F2_B,
                null, null, CitizenshipStatus.LPR)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('', 'yes'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485AutoF3() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F3_B)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('', 'yes'), FORM_485_FILENAME,
                pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485AutoF4() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F4_B)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('', 'yes'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap129(String single = '', String married = '', String divorced = '',
                                                String widowed = '') {
        [
                'form1[0].#subform[2].Pt1Line23_Checkbox[2]': single,
                'form1[0].#subform[2].Pt1Line23_Checkbox[3]': married,
                'form1[0].#subform[2].Pt1Line23_Checkbox[1]': divorced,
                'form1[0].#subform[2].Pt1Line23_Checkbox[0]': widowed
        ]
    }

    private Map<String, String> assertionMap485(String immediate = '', String other = '', String admitted = '') {
        [
                'form1[0].#subform[2].Pt2Line1_CB[0]': immediate,
                'form1[0].#subform[2].Pt2Line1_CB[1]': other,
                'form1[0].#subform[2].Pt2Line1_CB[2]': admitted
        ]
    }

}
