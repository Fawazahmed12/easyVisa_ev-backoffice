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
class PdfMiscellaneousSpec extends TestMockUtils {

    static final PdfForm FORM_485 = PdfForm.I485
    static final String FORM_485_FILENAME = "I485.pdf"

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
        TestPdfUtils.assertPdfValues(result['file'], assertionBeneficiaryLastArrivalMap485(), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485BeneficiaryLastEntryAdmitted() throws Exception {
        given:
        String sectionId = "Sec_travelToTheUnitedStates"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLastEntryAdmitted(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionBeneficiaryLastArrivalMap485('yes',
                'I was entered to the US successfully'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485BeneficiaryLastEntryParoled() throws Exception {
        given:
        String sectionId = "Sec_travelToTheUnitedStates"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLastEntryParoled(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionBeneficiaryLastArrivalMap485('', '', 'yes',
                'I was entered to the US successfully'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485BeneficiaryLastEntryNotAdmittedNotParoled() throws Exception {
        given:
        String sectionId = "Sec_travelToTheUnitedStates"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLastEntryNotAdmittedNotParoled(aPackage.id,
                beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionBeneficiaryLastArrivalMap485('', '', '',
                '', 'yes'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485BeneficiaryLastEntryOther() throws Exception {
        given:
        String sectionId = "Sec_travelToTheUnitedStates"
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryLastEntryOther(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionBeneficiaryLastArrivalMap485('', '', '',
                '', '', 'yes', 'I was entered to the US successfully'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionBeneficiaryLastArrivalMap485(String admittedCheckbox = '',
                                String admittedDetails = '', String paroledCheckbox = '', String paroledDetails = '',
                                String noAdmissionNoParoled = '', String otherCheckbox = '', String otherDetails = '') {
        [
                'form1[0].#subform[1].Pt1Line22a_CB[0]'            : admittedCheckbox,
                'form1[0].#subform[1].Pt1Line22a_AdmissionEntry[0]': admittedDetails,
                'form1[0].#subform[1].Pt1Line22b_CB[0]'            : paroledCheckbox,
                'form1[0].#subform[1].Pt1Line22b_ParoleEntrance[0]': paroledDetails,
                'form1[0].#subform[1].Pt1Line22c_CB[0]'            : noAdmissionNoParoled,
                'form1[0].#subform[1].Pt1Line22d_CB[0]'            : otherCheckbox,
                'form1[0].#subform[1].Pt2Line22d_other[0]'         : otherDetails
        ]
    }

}
