package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.utils.*
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Ignore

@Integration
class PdfSumRuleSpec extends TestMockUtils {

    static final PdfForm FORM_134 = PdfForm.I134
    static final String FORM_134_FILENAME = 'I134.pdf'
    static final PdfForm FORM_485 = PdfForm.I485
    static final String FORM_485_FILENAME = 'I485.pdf'
    static final PdfForm FORM_864 = PdfForm.I864
    static final String FORM_864_FILENAME = 'I864.pdf'

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

    @Ignore
    void test134AssetSingleValues() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.assetSingleValues(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('100,000', '59,000', '25,000', '75,000',
                '5,000', '20,000', '2,500'),
                FORM_134_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    @Ignore
    void test134AssetDoubleValues() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.assetDoubleValues(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_134.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134('250,000', '210,000', '85,000', '205,000',
                '20,000', '20,000', '2,500'),
                FORM_134_FILENAME, pdfFolder)

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
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485(), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485Single() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant beneficiaryApplicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryChildrenSingle(aPackage.id, beneficiaryApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiaryApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiaryApplicant.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('1'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864Empty() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864()
        mapToCheck.putAll(assertionMap864People())
        mapToCheck.putAll(assertionMap864Income())
        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    @Ignore
    void test864SingleAssets() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.assetSingleValues(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864('100,000', '50,000', '84,000', '234,000', '234,000')
        mapToCheck.putAll(assertionMap864People())
        mapToCheck.putAll(assertionMap864Income())
        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    @Ignore
    void test864SingleBankAssets() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.bankDeposits(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864('100,000', '', '', '100,000', '100,000')
        mapToCheck.putAll(assertionMap864People())
        mapToCheck.putAll(assertionMap864Income())
        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    @Ignore
    void test864SingleRealEstateAssets() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.realEstate(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864('', '50,000', '', '50,000', '50,000')
        mapToCheck.putAll(assertionMap864People())
        mapToCheck.putAll(assertionMap864Income())
        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    @Ignore
    void test864SingleStockAssets() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.lifeInsurance(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864('', '', '20,000', '20,000', '20,000')
        mapToCheck.putAll(assertionMap864People())
        mapToCheck.putAll(assertionMap864Income())
        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864PeopleAll() throws Exception {
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
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.peopleCalc864(aPackage.id, petitionerApplicant.id, benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('1', '1', '1', '2', '1', '2', '4', '12')
        mapToCheck.putAll(assertionMap864Income('150,000', '125,000', '150,000', '100,000', '70,000', '595,000'))
        mapToCheck.putAll(assertionMap864('100,000', '50,000', '25,000', '175,000', '369,500', '181,500', '1,000', '10,000', '2,000', '13,000'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864PeoplePetitionerMarriedOnBeneficiary() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F1_B)
                .deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        Applicant benApplicant = aPackage.principalBeneficiary
        Profile beneficiary = benApplicant.profile
        List<Answer> answerList = AnswerListPdfRulesStub.peopleCalc864(aPackage.id, petitionerApplicant.id, benApplicant.id,
                beneficiary.firstName, beneficiary.middleName, beneficiary.lastName)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('1', '1', '1', '2', '1', '2', '4', '12')
        mapToCheck.putAll(assertionMap864Income('150,000', '125,000', '150,000', '100,000', '70,000', '595,000'))
        mapToCheck.putAll(assertionMap864('100,000', '50,000', '25,000', '175,000', '369,500', '181,500', '1,000', '10,000', '2,000', '13,000'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864PeopleNotMarriedNoExtraSponsors() throws Exception {
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
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        Applicant benApplicant = aPackage.directBenefit.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.peopleCalc864NotMarriedNoExtraSponsors(aPackage.id,
                petitionerApplicant.id, benApplicant.id)
        answerList.addAll(AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, benApplicant.id))
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, benApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, benApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('1', '1', '', '2', '1', '2', '', '7')
        mapToCheck.putAll(assertionMap864Income())
        mapToCheck.putAll(assertionMap864())

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleExtraSponsor() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdSingle(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('', '', '', '', '', '', '1')
        mapToCheck.putAll(assertionMap864Income('', '125,000', '', '', '', '125,000'))
        mapToCheck.putAll(assertionMap864('', '', '', '', '54,000', '54,000', '0', '0', '0', '0'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleExtraSponsorBankSavingOnly() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdSingleBankSavingsOnly(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('', '', '', '', '', '', '1')
        mapToCheck.putAll(assertionMap864Income('', '125,000', '', '', '', '125,000'))
        mapToCheck.putAll(assertionMap864('', '', '', '', '3,000', '3,000', '0', '', '', '0'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleExtraSponsorRealEstateOnly() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdSingleRealEstateOnly(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('', '', '', '', '', '', '1')
        mapToCheck.putAll(assertionMap864Income('', '125,000', '', '', '', '125,000'))
        mapToCheck.putAll(assertionMap864('', '', '', '', '50,000', '50,000', '', '0', '', '0'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleExtraSponsorStocksOnly() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdSingleStocksOnly(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('', '', '', '', '', '', '1')
        mapToCheck.putAll(assertionMap864Income('', '125,000', '', '', '', '125,000'))
        mapToCheck.putAll(assertionMap864('', '', '', '', '1,000', '1,000', '', '', '0', '0'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleExtraSponsorWithOutDependents() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdSingleWithOutDependents(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('', '', '', '', '', '', '1')
        mapToCheck.putAll(assertionMap864Income('', '125,000', '', '', '', '125,000'))
        mapToCheck.putAll(assertionMap864('', '', '', '', '54,000', '0', '3,000', '50,000', '1,000', '54,000'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleExtraSponsorBankSavingOnlyWithOutDependents() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub
                .petitionerHouseholdSingleBankSavingsOnlyWithOutDependents(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('', '', '', '', '', '', '1')
        mapToCheck.putAll(assertionMap864Income('', '125,000', '', '', '', '125,000'))
        mapToCheck.putAll(assertionMap864('', '', '', '', '3,000', '0', '3,000', '', '', '3,000'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleExtraSponsorRealEstateOnlyWithOutDependents() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub
                .petitionerHouseholdSingleRealEstateOnlyWithOutDependents(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('', '', '', '', '', '', '1')
        mapToCheck.putAll(assertionMap864Income('', '125,000', '', '', '', '125,000'))
        mapToCheck.putAll(assertionMap864('', '', '', '', '50,000', '0', '', '50,000', '', '50,000'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleExtraSponsorStocksOnlyWithOutDependents() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub
                .petitionerHouseholdSingleStocksOnlyWithOutDependents(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People('', '', '', '', '', '', '1')
        mapToCheck.putAll(assertionMap864Income('', '125,000', '', '', '', '125,000'))
        mapToCheck.putAll(assertionMap864('', '', '', '', '1,000', '0', '', '', '1,000', '1,000'))

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    @Ignore
    void test864PetitionerIncome() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerIncome(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        Map<String, String> mapToCheck = assertionMap864People()
        mapToCheck.putAll(assertionMap864Income('150,000', '', '', '', '', '150,000'))
        mapToCheck.putAll(assertionMap864())

        TestPdfUtils.assertPdfValues(result['file'], mapToCheck, FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap134(String balance = '', String personal = '', String mortgages = '',
                                                String realEstate = '', String stocks = '', String lifeInsurance = '',
                                                String surrender = '') {
        [
                'form1[0].#subform[2].Pt3Line4_AccountBalance[0]'       : balance,
                'form1[0].#subform[2].Pt3Line5_ValuePersonalProperty[0]': personal,
                'form1[0].#subform[2].Pt3Line8b_AmountofMortgages[0]'   : mortgages,
                'form1[0].#subform[2].Pt3Line8a_ValueofRealEstate[0]'   : realEstate,
                'form1[0].#subform[2].Pt3Line6_MarketValue[0]'          : stocks,
                'form1[0].#subform[2].Pt3Line7a_ValueofRealEstate[0]'   : lifeInsurance,
                'form1[0].#subform[2].Pt3Line7b_AmountofMortgages[0]'   : surrender
        ]
    }

    private Map<String, String> assertionMap485(String childrenCount = '') {
        [
                'form1[0].#subform[7].Pt6Line1_TotalChildren[0]': childrenCount
        ]
    }

    private Map<String, String> assertionMap864(String balance = '', String realEstate = '', String stocks = '',
                                                String total = '', String allTotal = '', String houseAssets = '',
                                                String houseSavings = '', String houseReal = '', String houseStocks = '',
                                                String houseTotal = '') {
        [
                'form1[0].#subform[4].P7_Line1_BalanceofAccounts[0]': balance,
                'form1[0].#subform[4].P7_Line2_RealEstate[0]'       : realEstate,
                'form1[0].#subform[4].P7_Line3_StocksBonds[0]'      : stocks,
                'form1[0].#subform[4].P7_Line4_Total[0]'            : total,
                'form1[0].#subform[4].P7_Line5b_Assets[0]'          : houseAssets,
                'form1[0].#subform[4].P7_Line6_BalanceofAccounts[0]': houseSavings,
                'form1[0].#subform[4].P7_Line7_RealEstate[0]'       : houseReal,
                'form1[0].#subform[4].P7_Line8_StocksBonds[0]'      : houseStocks,
                'form1[0].#subform[5].P7_Line9_Total[0]'            : houseTotal,
                'form1[0].#subform[5].P7_Line10_TotalValueAssets[0]': allTotal
        ]
    }

    private Map<String, String> assertionMap864Income(String petitionerIncome = '', String house1 = '',
                                                      String house2 = '', String house3 = '', String house4 = '',
                                                      String incomeTotal = '') {
        [
                'form1[0].#subform[3].P6_Line2_TotalIncome[0]'          : petitionerIncome,
                'form1[0].#subform[3].P6_Line5_CurrentIncome[0]'        : house1,
                'form1[0].#subform[3].P6_Line8_CurrentIncome[0]'        : house2,
                'form1[0].#subform[3].P6_Line11_CurrentIncome[0]'       : house3,
                'form1[0].#subform[3].P6_Line14_CurrentIncome[0]'       : house4,
                'form1[0].#subform[4].P6_Line15_TotalHouseholdIncome[0]': incomeTotal
        ]
    }

    private Map<String, String> assertionMap864People(String derivative = '', String yourself = '',
                                                      String wife = '', String depChild = '',
                                                      String depNonChild = '', String sponsored = '', String household = '',
                                                      String totalPeople = '') {
        [
                'form1[0].#subform[2].P3_Line28_TotalNumberofImmigrants[0]': derivative,
                'form1[0].#subform[3].P5_Line1_Number[0]'                  : derivative,
                'form1[0].#subform[3].P5_Line2_Yourself[0]'                : yourself,
                'form1[0].#subform[3].P5_Line3_Married[0]'                 : wife,
                'form1[0].#subform[3].P5_Line4_DependentChildren[0]'       : depChild,
                'form1[0].#subform[3].P5_Line5_OtherDependents[0]'         : depNonChild,
                'form1[0].#subform[3].P5_Line6_Sponsors[0]'                : sponsored,
                'form1[0].#subform[3].P5_Line7_SameResidence[0]'           : household,
                'form1[0].#subform[3].Override[0]'                         : totalPeople
        ]
    }

}
