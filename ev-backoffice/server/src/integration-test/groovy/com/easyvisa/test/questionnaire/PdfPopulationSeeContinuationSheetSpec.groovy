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
class PdfPopulationSeeContinuationSheetSpec extends TestMockUtils {

    static final PdfForm FORM_129F = PdfForm.I129F
    static final String FORM_129F_FILENAME = "I129F.pdf"
    static final PdfForm FORM_765 = PdfForm.I765
    static final String FORM_765_FILENAME = "I765.pdf"

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

    void testSeeContinuationSheetPopulation129f() throws Exception {
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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId,
                null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129F(PdfUtils.SEE_CONTINUATION,
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void testSeeContinuationSheetsPopulation129f() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.seeContinuationSheets129f(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId,
                null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129F(PdfUtils.SEE_CONTINUATION,
                PdfUtils.SEE_CONTINUATIONS, aPackage.petitioner), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void testSeeContinuationSheetResidence18CountryAndStatePopulation129f() throws Exception {
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
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18StateCountryTriple(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129F('',
                PdfUtils.SEE_CONTINUATION, aPackage.petitioner), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void testSeeContinuationSheetResidence18CountryAndStateAndCriminalPopulation129f() throws Exception {
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
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.residence18StateCountryTripleCriminal(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap129F(PdfUtils.SEE_CONTINUATION,
                PdfUtils.SEE_CONTINUATIONS, aPackage.petitioner), FORM_129F_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    //TODO it failed sporadic due to getting only 3 iterations instead of 9
    @Ignore
    void testSeeContinuationSheetNoMiddlePopulation765() throws Exception {
        given:
        String sectionId = 'Sec_nameForBeneficiary'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryOtherNamesQuadro(aPackage.id,
                petitionerApplicant.id, false)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_765.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMapSeeContinuation765(), FORM_765_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    //TODO it failed sporadic due to getting only 3 iterations instead of 9
    @Ignore
    void testSeeContinuationSheetsNoMiddlePopulation765() throws Exception {
        given:
        String sectionId = 'Sec_nameForBeneficiary'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryOtherNamesNine(aPackage.id, petitionerApplicant.id,
                false)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_765.formId, null,
                null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMapSeeContinuations765(), FORM_765_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap129F(String addPartValueInside, String addPartValue, Petitioner petitioner) {
        Map<String, String> result = [
                'form1[0].#subform[8].Pt3Line4B_Describe[0]'   : addPartValueInside,
                'form1[0].#subform[12].Line2a_PageNumber[0]'   : '',
                'form1[0].#subform[12].Line2b_PartNumber[0]'   : '',
                'form1[0].#subform[12].Line2c_ItemNumber[0]'   : '',
                'form1[0].#subform[12].Line2a_PageNumber[1]'   : '',
                'form1[0].#subform[12].Line2b_PartNumber[1]'   : '',
                'form1[0].#subform[12].Line2c_ItemNumber[1]'   : '',
                'form1[0].#subform[12].Line1_AdditionalInfo[1]': '',
                'form1[0].#subform[12].Line2a_PageNumber[2]'   : '',
                'form1[0].#subform[12].Line2b_PartNumber[2]'   : '',
                'form1[0].#subform[12].Line2c_ItemNumber[2]'   : '',
                'form1[0].#subform[12].Line1_AdditionalInfo[2]': '',
                'form1[0].#subform[12].Line2a_PageNumber[3]'   : '',
                'form1[0].#subform[12].Line2b_PartNumber[3]'   : '',
                'form1[0].#subform[12].Line2c_ItemNumber[3]'   : '',
                'form1[0].#subform[12].Line1_AdditionalInfo[3]': '',
                'form1[0].#subform[12].Line2a_PageNumber[4]'   : '',
                'form1[0].#subform[12].Line2b_PartNumber[4]'   : '',
                'form1[0].#subform[12].Line2c_ItemNumber[4]'   : '',
                'form1[0].#subform[12].Line1_AdditionalInfo[4]': ''
        ]
        TestPdfUtils.assertionMapAdditionalPart(result, addPartValue, petitioner, 'form1[0].#subform[12].Line1_AdditionalInfo[0]',
                'form1[0].#subform[12].Pt1Line6a_FamilyName[1]', 'form1[0].#subform[12].Pt1Line6b_GivenName[1]',
                'form1[0].#subform[12].Pt1Line6c_MiddleName[1]', 'form1[0].#subform[12].Pt1Line1_AlienNumber[1]')
        result
    }

    private Map<String, String> assertionMapSeeContinuation765() {
        Map<String, String> result = assertionMapCommon765()
        result.putAll(
                [
                        'form1[0].#subform[0].Line1_FamilyName[9]' : PdfUtils.SEE_CONTINUATION,
                        'form1[0].#subform[0].Line1_FamilyName[10]': PdfUtils.SEE_CONTINUATION,
                        'form1[0].#subform[0].Line1_FamilyName[11]': PdfUtils.SEE_CONTINUATION
                ]
        )
        result
    }

    private Map<String, String> assertionMapSeeContinuations765() {
        Map<String, String> result = assertionMapCommon765()
        result.putAll(
                [
                        'form1[0].#subform[0].Line1_FamilyName[9]' : PdfUtils.SEE_CONTINUATIONS,
                        'form1[0].#subform[0].Line1_FamilyName[10]': PdfUtils.SEE_CONTINUATIONS,
                        'form1[0].#subform[0].Line1_FamilyName[11]': PdfUtils.SEE_CONTINUATIONS
                ]
        )
        result
    }

    private Map<String, String> assertionMapCommon765() {
        [
                'form1[0].#subform[0].Line1_FamilyName[3]': 'Brown',
                'form1[0].#subform[0].Line1_FamilyName[4]': 'Peter',
                'form1[0].#subform[0].Line1_FamilyName[5]': 'Junior',

                'form1[0].#subform[0].Line1_FamilyName[6]': 'Smith',
                'form1[0].#subform[0].Line1_FamilyName[7]': 'Peter',
                'form1[0].#subform[0].Line1_FamilyName[8]': 'Tom',
        ]
    }

}
