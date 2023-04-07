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
class PdfViceVersaRuleSpec extends TestMockUtils {

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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864(), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864PetitionerDoubleEmployerNoSelf() throws Exception {
        given:
        String sectionId = "Sec_employmentHistory"
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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('yes', 'Google', 'IT specialist',
                '', 'Amazon', 'Software Engineer'), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864PetitionerDoubleEmployerFirstSelf() throws Exception {
        given:
        String sectionId = "Sec_employmentHistory"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerEmploymentDouble(aPackage.id, petitionerApplicant.id,
                true)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('yes', 'Amazon', 'Software Engineer',
                'yes', 'Google', 'IT specialist'), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864PetitionerDoubleEmployerSecondSelf() throws Exception {
        given:
        String sectionId = "Sec_employmentHistory"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerEmploymentDouble(aPackage.id, petitionerApplicant.id,
                false, true)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('yes', 'Google', 'IT specialist',
                'yes', 'Amazon', 'Software Engineer',), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864PetitionerDoubleEmployerBothSelf() throws Exception {
        given:
        String sectionId = "Sec_employmentHistory"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerEmploymentDouble(aPackage.id, petitionerApplicant.id,
                true, true)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', 'Amazon', 'Software Engineer',
                'yes', 'Google', 'IT specialist'), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864PetitionerRetiredAndEmployed() throws Exception {
        given:
        String sectionId = "Sec_employmentHistory"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerEmploymentRetireAndEmployed(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('yes', 'Amazon', 'Software Engineer',
                '', '', ''), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap864(String employed = '', String employer1 = '', String occupation1 = '',
                                            String selfEmployed = '', String employer2 = '', String occupation2 = '') {
        [
                'form1[0].#subform[3].P6_Line1_Checkbox[0]'        : employed,
                'form1[0].#subform[3].P6_Line1a_NameofEmployer[0]' : occupation1,
                'form1[0].#subform[3].P6_Line1a1_NameofEmployer[0]': employer1,
                'form1[0].#subform[3].P6_Line1a2_NameofEmployer[0]': employer2,
                'form1[0].#subform[3].P6_Line4_Checkbox[0]'        : selfEmployed,
                'form1[0].#subform[3].P6_Line4a_SelfEmployedAs[0]' : occupation2
        ]
    }

}
