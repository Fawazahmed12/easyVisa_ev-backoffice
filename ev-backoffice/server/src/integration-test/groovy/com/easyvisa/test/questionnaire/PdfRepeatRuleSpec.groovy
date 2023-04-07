package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.ContinuationSheetHeaderInfo
import com.easyvisa.questionnaire.services.ContinuationSheetService
import com.easyvisa.utils.*
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import static com.easyvisa.utils.TestPdfUtils.assertionMapContinuationSheetHeader

@Integration
class PdfRepeatRuleSpec extends TestMockUtils {

    static final PdfForm FORM_864 = PdfForm.I864
    static final String FORM_864_FILENAME = "I864.pdf"
    static final String CONTINUATION_864_ID = 'CS_864_incomeHistory'
    static final String CONTINUATION_864_NAME = '864 Continuation Sheet - Page 5, Part 6, Item 25 - Affidavit of' +
            ' Support 2018-03-18.pdf'

    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private PdfPopulationService pdfPopulationService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private ContinuationSheetService continuationSheetService
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
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864(), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleTax() throws Exception {
        given:
        String sectionId = 'SubSec_incomeHistory '
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.singleTax(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', '2018', '150,000'),
                FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleTaxExplanation() throws Exception {
        given:
        String sectionId = 'SubSec_incomeHistory '
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.singleTaxExplanation(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('yes', '2018', '150,000'),
                FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864TripleTax() throws Exception {
        given:
        String sectionId = 'SubSec_incomeHistory '
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.tripleTax(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id, sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', '2018', '150,000', '2017',
                '130,000', '2016', '110,000'), FORM_864_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864SingleTaxExplanationContinuation() throws Exception {
        given:
        String sectionId = 'SubSec_incomeHistory '
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.singleTaxExplanation(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, CONTINUATION_864_ID,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864TaxContinuation(aPackage, CONTINUATION_864_ID, '2018',
                '2018 was not required to fill in explanation'), CONTINUATION_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864TripleTaxExplanationContinuation() throws Exception {
        given:
        String sectionId = 'SubSec_incomeHistory '
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.tripleTaxExplanation(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, CONTINUATION_864_ID,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864TaxContinuation(aPackage, CONTINUATION_864_ID, '2018',
                '2018 was not required to fill in explanation',
                '2017', '2017 was not required to fill in explanation',
                '2016', '2016 was not required to fill in explanation'),
                CONTINUATION_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap864(String explanation = '', String taxYear1 = '', String income1 = '',
                                                String taxYear2 = '', String income2 = '',
                                                String taxYear3 = '', String income3 = '') {
        [
                'form1[0].#subform[4].P6_Line20_Attached[0]'    : explanation,
                'form1[0].#subform[4].P6_Line19a_TaxYear[0]'    : taxYear1,
                'form1[0].#subform[4].P6_Line19a_TotalIncome[0]': income1,
                'form1[0].#subform[4].P6_Line19b_TaxYear[0]'    : taxYear2,
                'form1[0].#subform[4].P6_Line19b_TotalIncome[0]': income2,
                'form1[0].#subform[4].P6_Line19c_TaxYear[0]'    : taxYear3,
                'form1[0].#subform[4].P6_Line19c_TotalIncome[0]': income3
        ]
    }

    private Map<String, String> assertionMap864TaxContinuation(def aPackage, String continuationSheetId, String taxYear1 = '', String explain1 = '',
                                                               String taxYear2 = '', String explain2 = '',
                                                               String taxYear3 = '', String explain3 = '') {
        Map<String, String> result = [
                'TaxYear1': taxYear1,
                'Explain1': explain1,
                'TaxYear2': taxYear2,
                'Explain2': explain2,
                'TaxYear3': taxYear3,
                'Explain3': explain3
        ]
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = continuationSheetService.getContinuationSheetHeaderByContinuationSheetId(aPackage, continuationSheetId);
        result.putAll(assertionMapContinuationSheetHeader(continuationSheetHeaderInfo, '5', '6', '25'))
        result
    }

}
