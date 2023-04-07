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
class PdfPopulationMultipleContinuationSheetsSpec extends TestMockUtils {

    static final PdfForm FORM_129F = PdfForm.I129F
    static final String PETITIONER_CRIMINAL_CONT_ID = 'CS_129F_criminalCivilConvictions'
    static final String PETITIONER_CRIMINAL_CONT_FILE = '129f_continuation_sheet_page_9_part_3_item_4a_item_4b_20170410.pdf'
    static final String PETITIONER_CRIMINAL_CONT_ZIP_FILE = 'forms.zip'
    static final String PETITIONER_CRIMINAL_CONT_FIRST_FILE = '129f_continuation_sheet_page_9_part_3_item_4a_item_4b_20170410_1.pdf'
    static final String PETITIONER_CRIMINAL_CONT_SECOND_FILE = '129f_continuation_sheet_page_9_part_3_item_4a_item_4b_20170410_2.pdf'

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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_129F.formId, null, null)

        then:
        String defaultFileName = 'i_129f_20181107.pdf';
        String currentFilename = defaultFileName.replaceAll('.pdf', "#${petitionerApplicant.id}.pdf")
        TestPdfUtils.assertPdfResult(result, currentFilename, assertionMap129F(PdfUtils.SEE_CONTINUATION,
                PdfUtils.SEE_CONTINUATION, testHelper.aPackage.petitioner), pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void testSingleContinuationSheetsPopulation129f() throws Exception {
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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, PETITIONER_CRIMINAL_CONT_ID,
                null)

        then:
        String defaultFileName = PETITIONER_CRIMINAL_CONT_FILE;
        String currentFilename = defaultFileName.replaceAll('.pdf', "#${petitionerApplicant.id}.pdf")
        TestPdfUtils.assertPdfResult(result, currentFilename,
                assertionMap129FContinuation(aPackage, PETITIONER_CRIMINAL_CONT_ID, 'Speed limit', '05/08/2000', 'payed'), pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void testFirstFromMultipleContinuationSheetsPopulation129f() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerCriminalQuadro(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null,
                PETITIONER_CRIMINAL_CONT_ID, 0)

        then:
        String defaultFileName = PETITIONER_CRIMINAL_CONT_FILE;
        String currentFilename = defaultFileName.replaceAll('.pdf', "#${petitionerApplicant.id}.pdf")
        TestPdfUtils.assertPdfResult(result, currentFilename,
                assertionMap129FContinuation(aPackage, PETITIONER_CRIMINAL_CONT_ID, 'Speed limit', '05/08/2000', 'payed',
                        'Speed limit 2016', '01/01/2016', 'payed in 2016',
                        'Speed limit 2017', '02/02/2017', 'payed in 2017'), pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void testSecondFromMultipleContinuationSheetsPopulation129f() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerCriminalQuadro(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null,
                PETITIONER_CRIMINAL_CONT_ID, 1)

        then:
        String defaultFileName = PETITIONER_CRIMINAL_CONT_FILE;
        String currentFilename = defaultFileName.replaceAll('.pdf', "#${petitionerApplicant.id}.pdf")
        TestPdfUtils.assertPdfResult(result, currentFilename,
                assertionMap129FContinuation(aPackage, PETITIONER_CRIMINAL_CONT_ID, 'Speed limit 2018', '03/03/2018', 'payed in 2018'), pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void testZipMultipleContinuationSheetsPopulation129f() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerCriminalQuadro(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, PETITIONER_CRIMINAL_CONT_ID, null)

        then:

        String current_PetitionerContFirstFile = PETITIONER_CRIMINAL_CONT_FIRST_FILE.replaceAll('.pdf', "#${petitionerApplicant.id}.pdf")
        String current_PetitionerContSecondFile = PETITIONER_CRIMINAL_CONT_SECOND_FILE.replaceAll('.pdf', "#${petitionerApplicant.id}.pdf")
        TestPdfUtils.assertZipResult(result, PETITIONER_CRIMINAL_CONT_ZIP_FILE, [current_PetitionerContFirstFile,
                                                                                 current_PetitionerContSecondFile],
                [assertionMap129FContinuation(aPackage, PETITIONER_CRIMINAL_CONT_ID, 'Speed limit', '05/08/2000', 'payed',
                        'Speed limit 2016', '01/01/2016', 'payed in 2016',
                        'Speed limit 2017', '02/02/2017', 'payed in 2017'),
                 assertionMap129FContinuation(aPackage, PETITIONER_CRIMINAL_CONT_ID, 'Speed limit 2018', '03/03/2018', 'payed in 2018')], pdfFolder)

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

    private Map<String, String> assertionMap129FContinuation(def aPackage, String continuationSheetId, String explain1, String date1, String outcome1,
                                                             String explain2 = '', String date2 = '', String outcome2 = '',
                                                             String explain3 = '', String date3 = '', String outcome3 = '') {
        Map<String, String> result = [
                'Explain1': explain1,
                'Explain2': explain2,
                'Explain3': explain3,
                'Date1'   : date1,
                'Date2'   : date2,
                'Date3'   : date3,
                'Outcome1': outcome1,
                'Outcome2': outcome2,
                'Outcome3': outcome3
        ]
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = continuationSheetService.getContinuationSheetHeaderByContinuationSheetId(aPackage, continuationSheetId);
        result.putAll(assertionMapContinuationSheetHeader(continuationSheetHeaderInfo, '9', '3', '4.a - 4.b'))
        result
    }

}
