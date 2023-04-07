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
class PdfAutoPopulationSpec extends TestMockUtils {

    private static final PdfForm FORM_485 = PdfForm.I485
    private static final String FORM_485_FILENAME = 'I485.pdf'
    private static final PdfForm FORM_864 = PdfForm.I864
    private static final String FORM_864_FILENAME = 'I864.pdf'

    @Autowired
    ProfileService profileService

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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_485.formId,
                null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('yes'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485Beneficiary() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalName(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('yes'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485BeneficiaryMarried() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalNameMarried(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('yes'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485BeneficiaryDivorced() throws Exception {
        given:
        String sectionId = 'Sec_addressHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLegalNameDivorced(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485('yes', 'yes'), FORM_485_FILENAME, pdfFolder)

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
        testHelper.deletePackageAnswersOnly();
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864(), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864Petitioner() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalName(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('yes', '1'), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864PetitionerMarried() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalNameMarried(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('yes', '1', '1'),
                FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap485(String auto1 = '', String auto2 = '') {
        [
                'form1[0].#subform[2].Pt2Line2_CB[1]'  : auto1,
                'form1[0].#subform[6].Pt5Line2_YNNA[2]': auto2
        ]
    }

    private Map<String, String> assertionMap864(String auto1 = '', String auto2 = '', String auto3 = '') {
        [
                'form1[0].#subform[1].P3_Line1_Checkbox[0]': auto1,
                'form1[0].#subform[3].P5_Line2_Yourself[0]': auto2,
                'form1[0].#subform[3].P5_Line3_Married[0]' : auto3
        ]
    }

}
