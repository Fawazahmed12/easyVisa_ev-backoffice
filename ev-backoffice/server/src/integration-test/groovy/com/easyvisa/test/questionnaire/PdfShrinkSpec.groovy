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
class PdfShrinkSpec extends TestMockUtils {

    private static final PdfForm FORM_129 = PdfForm.I129F
    private static final String FORM_129_CONTINUATION_ID = 'CS_129F_criminalCivilConvictions'
    private static final String FORM_129_CONTINUATION_NAME = '129F Continuation Sheet - Page 9, Part 3, Items 4.a -' +
            ' 4.b - Criminal Information 2017-04-10.pdf'
    private static final String DA_VALUE = '/CourierNewPS-BoldMT 10.00 Tf 0 g'
    private static final String DA_VALUE_AUTO = '/CourierNewPS-BoldMT 0 Tf 0 g'
    private static final String DA_VALUE_CONT = '/Courier-Bold 10 Tf 0 g'
    private static final String DA_VALUE_AUTO_CONT = '/Courier-Bold 8.899996 Tf 0 g'

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

    void test129PetitionerNameNotShrunk() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalName(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129.formId, null, null)

        then:
        TestPdfUtils.assertPdfFields(result['file'], assertionMap129(DA_VALUE, 'White', 'Andrew', 'James'),
                'I129F.pdf', pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerNameShrunk() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalNameLong(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129.formId, null, null)

        then:
        TestPdfUtils.assertPdfFields(result['file'], assertionMap129(DA_VALUE_AUTO, 'White White White White White',
                'Andrew Andrew Andrew Andrew Andrew', 'James James James James James'),
                'I129F.pdf', pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerContinuationNotShrunk() throws Exception {
        given:
        String sectionId = 'Sec_criminalAndCivilHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerCriminalSingle(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, FORM_129_CONTINUATION_ID, null)

        then:
        TestPdfUtils.assertPdfFields(result['file'], assertionMap129Continuation(aPackage.petitioner, 'payed',
                '05/08/2000','Speed limit'), FORM_129_CONTINUATION_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test129PetitionerContinuationShrunk() throws Exception {
        given:
        String sectionId = 'Sec_criminalAndCivilHistory'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.seeContinuationSheet129fLongExplanation(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, FORM_129_CONTINUATION_ID,
                null)

        then:
        TestPdfUtils.assertPdfFields(result['file'], assertionMap129Continuation(aPackage.petitioner, 'payed',
                '05/08/2000',
                'Very very long text 1 Very very long text 2 Very very long text 3 Very very long text 4 ' +
                        'Very very long text 5 Very very long text 6 Very very long text 7 Very very long text 8 ' +
                        'Very very long text 9 Very very long text 10 Very very long text 1 Very very long text 2 ' +
                        'Very very long text 3 Very very long text 4 Very very long text 5 Very very long text 6 ' +
                        'Very very long text 7 Very very long text 8 Very very long text 9 Very very long text 20 ' +
                        'Very very long text 1 Very very long text 2 Very very long text 3 Very very long text 4 ' +
                        'Very very long text 5 Very very long text 6 Very very long text 7 Very very long text 8 ' +
                        'Very very long text 9 Very very long text 30 Very very long text 1 Very very long text 2 ' +
                        'Very very long text 3 Very very long text 4 Very very long text 5 Very very long text 6 ' +
                        'Very very long text 7 Very very long text 8 Very very long text 9 Very very long text 40',
                DA_VALUE_CONT, DA_VALUE_AUTO_CONT), FORM_129_CONTINUATION_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, TestPdfUtils.PdfAsserts> assertionMap129(String da, String lName = '', String fName = '', String mName = '') {
        [
                'form1[0].#subform[0].Pt1Line6a_FamilyName[0]': new TestPdfUtils.PdfAsserts(fieldValue: lName, defaultAppearance: da),
                'form1[0].#subform[0].Pt1Line6b_GivenName[0]' : new TestPdfUtils.PdfAsserts(fieldValue: fName, defaultAppearance: da),
                'form1[0].#subform[0].Pt1Line6c_MiddleName[0]': new TestPdfUtils.PdfAsserts(fieldValue: mName, defaultAppearance: da)
        ]
    }

    private Map<String, TestPdfUtils.PdfAsserts> assertionMap129Continuation(Petitioner petitioner, String outcome1 = '',
                                                                             String date1 = '', String explain1 = '',
                                                                             String da = DA_VALUE_CONT,
                                                                             String da_auto = DA_VALUE_CONT) {
        Map<String, TestPdfUtils.PdfAsserts> result = [
                'Outcome1': new TestPdfUtils.PdfAsserts(fieldValue: outcome1, defaultAppearance: da),
                'Date1'   : new TestPdfUtils.PdfAsserts(fieldValue: date1, defaultAppearance: da),
                'Explain1': new TestPdfUtils.PdfAsserts(fieldValue: explain1, defaultAppearance: da_auto),
                'Outcome2': new TestPdfUtils.PdfAsserts(fieldValue: '', defaultAppearance: DA_VALUE_CONT),
                'Date2'   : new TestPdfUtils.PdfAsserts(fieldValue: '', defaultAppearance: DA_VALUE_CONT),
                'Explain2': new TestPdfUtils.PdfAsserts(fieldValue: '', defaultAppearance: DA_VALUE_CONT),
                'Outcome3': new TestPdfUtils.PdfAsserts(fieldValue: '', defaultAppearance: DA_VALUE_CONT),
                'Date3'   : new TestPdfUtils.PdfAsserts(fieldValue: '', defaultAppearance: DA_VALUE_CONT),
                'Explain3': new TestPdfUtils.PdfAsserts(fieldValue: '', defaultAppearance: DA_VALUE_CONT)
        ]
        result.putAll(TestPdfUtils.assertionMapPdfAssertsContinuationSheetHeader(petitioner, '9', '3',
                '4.a - 4.b', DA_VALUE_CONT))
        result
    }

}
