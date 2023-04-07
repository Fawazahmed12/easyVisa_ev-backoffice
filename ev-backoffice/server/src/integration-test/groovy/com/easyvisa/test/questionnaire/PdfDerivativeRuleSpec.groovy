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
class PdfDerivativeRuleSpec extends TestMockUtils {

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
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485Direct('applicant-first','','applicant-last'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test485Direct() throws Exception {
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
        List<Answer>  answerList = AnswerListPdfRulesStub.beneficiaryDirectDerivative485(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485Direct('Avery', 'Victoria', 'Martin', '07/01/1985',
                '8523697410', '06/25/2015', '569874123'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    //most probably we need t delete this rule at all, due to no questions will be addressed to Derivative Beneficiaries
    @Ignore
    void test485Derivative() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndTwoBeneficiariesOpenPackage(ImmigrationBenefitCategory.F1_B, ImmigrationBenefitCategory.F1_B)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.benefits.find { !it.direct }.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryDirectDerivative485(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId,
                answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, FORM_485.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap485Derivative('Avery', 'Victoria', 'Martin', '07/01/1985',
                '8523697410', '06/25/2015', '569874123'), FORM_485_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap485Direct(String given = '', String middle = '', String last = '',
                                                      String dob = '', String receipt = '', String receiptDate = '',
                                                      String aNumber = '') {
        [
                'form1[0].#subform[3].Pt2Line5b_GivenName[0]': '',
                'form1[0].#subform[3].Pt2Line5c_MiddleName[0]': '',
                'form1[0].#subform[3].Pt2Line5a_FamilyName[0]': '',
                'form1[0].#subform[3].Pt2Line7_Date[0]': '',
                'form1[0].#subform[3].Pt2Line8_ReceiptNumber[0]': '',
                'form1[0].#subform[3].Pt2Line9_Date[0]': '',
                'form1[0].#subform[3].Pt1Line8_AlienNumber[0]': '',
                'form1[0].#subform[0].Pt1Line1b_GivenName[0]': given,
                'form1[0].#subform[0].Pt1Line1c_MiddleName[0]': middle,
                'form1[0].#subform[0].Pt1Line1a_FamilyName[0]': last,
                'form1[0].#subform[0].Pt1Line5_DateofBirth[0]': dob,
                'form1[0].#subform[3].Pt2Line3_Receipt[0]': receipt,
                'form1[0].#subform[3].Pt2Line4_Date[0]': receiptDate,
                'form1[0].#subform[0].Pt1Line10_AlienNumber[0]': aNumber,
                'form1[0].#subform[1].Pt1Line10_AlienNumber[1]': aNumber,
                'form1[0].#subform[1].Pt1Line10_AlienNumber[2]': aNumber,
                'form1[0].#subform[2].Pt1Line10_AlienNumber[3]': aNumber,
                'form1[0].#subform[3].Pt1Line10_AlienNumber[4]': aNumber,
                'form1[0].#subform[4].Pt1Line10_AlienNumber[5]': aNumber,
                'form1[0].#subform[5].Pt1Line10_AlienNumber[6]': aNumber,
                'form1[0].#subform[6].Pt1Line10_AlienNumber[7]': aNumber,
                'form1[0].#subform[7].Pt1Line10_AlienNumber[8]': aNumber,
                'form1[0].#subform[8].Pt1Line10_AlienNumber[9]': aNumber,
                'form1[0].#subform[9].Pt1Line10_AlienNumber[10]': aNumber,
                'form1[0].#subform[10].Pt1Line10_AlienNumber[11]': aNumber,
                'form1[0].#subform[11].Pt1Line10_AlienNumber[12]': aNumber,
                'form1[0].#subform[12].Pt1Line10_AlienNumber[13]': aNumber,
                'form1[0].#subform[13].Pt1Line10_AlienNumber[14]': aNumber,
                'form1[0].#subform[14].Pt1Line10_AlienNumber[15]': aNumber,
                'form1[0].#subform[15].Pt1Line10_AlienNumber[16]': aNumber,
                'form1[0].#subform[16].Pt1Line10_AlienNumber[17]': aNumber,
                'form1[0].#subform[17].Pt1Line10_AlienNumber[19]': aNumber
        ]
    }

    private Map<String, String> assertionMap485Derivative(String given = '', String middle = '', String last = '',
                                                      String dob = '', String receipt = '', String receiptDate = '',
                                                      String aNumber = '') {
        [
                'form1[0].#subform[0].Pt1Line1b_GivenName[0]': '',
                'form1[0].#subform[0].Pt1Line1c_MiddleName[0]': '',
                'form1[0].#subform[0].Pt1Line1a_FamilyName[0]': '',
                'form1[0].#subform[0].Pt1Line5_DateofBirth[0]': '',
                'form1[0].#subform[3].Pt2Line3_Receipt[0]': '',
                'form1[0].#subform[3].Pt2Line4_Date[0]': '',
                'form1[0].#subform[0].Pt1Line10_AlienNumber[0]': '',
                'form1[0].#subform[3].Pt2Line5b_GivenName[0]': given,
                'form1[0].#subform[3].Pt2Line5c_MiddleName[0]': middle,
                'form1[0].#subform[3].Pt2Line5a_FamilyName[0]': last,
                'form1[0].#subform[3].Pt2Line7_Date[0]': dob,
                'form1[0].#subform[3].Pt2Line8_ReceiptNumber[0]': receipt,
                'form1[0].#subform[3].Pt2Line9_Date[0]': receiptDate,
                'form1[0].#subform[3].Pt1Line8_AlienNumber[0]': aNumber,
                'form1[0].#subform[1].Pt1Line10_AlienNumber[1]': aNumber,
                'form1[0].#subform[1].Pt1Line10_AlienNumber[2]': aNumber,
                'form1[0].#subform[2].Pt1Line10_AlienNumber[3]': aNumber,
                'form1[0].#subform[3].Pt1Line10_AlienNumber[4]': aNumber,
                'form1[0].#subform[4].Pt1Line10_AlienNumber[5]': aNumber,
                'form1[0].#subform[5].Pt1Line10_AlienNumber[6]': aNumber,
                'form1[0].#subform[6].Pt1Line10_AlienNumber[7]': aNumber,
                'form1[0].#subform[7].Pt1Line10_AlienNumber[8]': aNumber,
                'form1[0].#subform[8].Pt1Line10_AlienNumber[9]': aNumber,
                'form1[0].#subform[9].Pt1Line10_AlienNumber[10]': aNumber,
                'form1[0].#subform[10].Pt1Line10_AlienNumber[11]': aNumber,
                'form1[0].#subform[11].Pt1Line10_AlienNumber[12]': aNumber,
                'form1[0].#subform[12].Pt1Line10_AlienNumber[13]': aNumber,
                'form1[0].#subform[13].Pt1Line10_AlienNumber[14]': aNumber,
                'form1[0].#subform[14].Pt1Line10_AlienNumber[15]': aNumber,
                'form1[0].#subform[15].Pt1Line10_AlienNumber[16]': aNumber,
                'form1[0].#subform[16].Pt1Line10_AlienNumber[17]': aNumber,
                'form1[0].#subform[17].Pt1Line10_AlienNumber[19]': aNumber
        ]
    }

}
