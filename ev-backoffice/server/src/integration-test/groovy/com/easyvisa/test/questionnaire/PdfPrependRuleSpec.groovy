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
class PdfPrependRuleSpec extends TestMockUtils {

    static final PdfForm FORM_601 = PdfForm.I601
    static final String FORM_601_FILENAME = "I601.pdf"

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

    void test601Empty() throws Exception {
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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_601.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap601(), FORM_601_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test601TravelToUS() throws Exception {
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
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryTravelToUs(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_601.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap601(),
                FORM_601_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test601TravelToUSNoCity() throws Exception {
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
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryTravelToUs(aPackage.id, beneficiary.id,
                Boolean.FALSE)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_601.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap601(), FORM_601_FILENAME,
                pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap601(String location = '') {
        [
                'form1[0].#subform[2].p2Line2dLocation[0]'  : location
        ]
    }

}
