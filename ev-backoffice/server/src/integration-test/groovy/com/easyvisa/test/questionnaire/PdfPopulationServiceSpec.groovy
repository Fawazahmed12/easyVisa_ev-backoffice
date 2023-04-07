package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.PdfFieldExpressionInfo
import com.easyvisa.questionnaire.services.PdfFieldPrintingParams
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.*
import grails.testing.mixin.integration.Integration
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Ignore

import java.time.LocalDate
import java.time.Month

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

@Integration
class PdfPopulationServiceSpec extends TestMockUtils {

    static final PdfForm PDF_FORM = PdfForm.I129F
    static final String CONT_ID = 'CS_129F_6'
    static final String CONT_NAME = '129F Continuation Sheet - Page 1, Part 1, Items 7.a -7.c (Petitioner - Other Names Used) 2017-04-10.pdf'

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService
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

    void testCorrectPopulation() throws Exception {
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
        Applicant beneficiaryApplicant = aPackage.principalBeneficiary

        List<Answer> answerList = AnswerList129FFullFormStub.form129FullFianceNoContinuationSheet(aPackage.id,
                petitionerApplicant.id, beneficiaryApplicant.id, false)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: petitionerApplicant.id, formId: PDF_FORM.formId);
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, PDF_FORM.formId, null, null)

        then:
        assertCorrectSimplePopulation(pdfFieldExpressionInfo.pdfFieldDetailList, result['file'])

        cleanup:
        testHelper.clean()
    }

    private void assertCorrectSimplePopulation(List<PdfFieldDetail> pdfFieldDetailList, InputStream resultPdf) {
        assertNotNull(resultPdf)
        TestPdfUtils.writeToFile(resultPdf, pdfFolder, '${PDF_FORM.formId}.pdf')

        PDDocument.load(resultPdf).withCloseable {
            PDAcroForm acroForm = it.getDocumentCatalog().getAcroForm()
            TestPdfUtils.assertAllDataPopulated(pdfFieldDetailList, acroForm)
            assertFormValues(acroForm)
            assertEdgeCases(acroForm)
        }
    }

    private void assertFormValues(PDAcroForm acroForm) {
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[8].Pt4Line2_Checkbox[2]', 'yes')
    }

    private void assertEdgeCases(PDAcroForm acroForm) {
        //checks State value converted to two letters and set to the dropdown
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[1].Pt1Line9_State[0]', 'MA')
        //checks date formatting to mm/dd/yyyy format
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[1].Pt1Line10a_DateFrom[0]', DateUtil.pdfFormDate(LocalDate.of(LocalDate.now().getYear() - 2, Month.JANUARY, 25)))
        //checks auto population of 'TO PRESENT' value
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[1].Pt1Line10b_ToFrom[0]', 'TO PRESENT')
        //checks children age calculation
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[3].Pt1Line49a_Age[0]', '2 years')
        //Checks N/A
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[1].Pt1Line9_Province[0]', 'N/A')
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[1].Pt1Line9_PostalCode[0]', 'N/A')
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[1].Pt1Line11_State[0]', 'N/A')
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[1].Pt1Line11_ZipCode[0]', 'N/A')
        //checks PWI population for without_inspection value in current entry to the US
        TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[5].Pt2Line38a_LastArrivedAs[0]', 'PWI')
    }

    void testCorrectPopulationContinuation() throws Exception {
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
        Applicant beneficiaryApplicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerList129FFullFormStub.form129FullFianceNoContinuationSheet(aPackage.id,
                petitionerApplicant.id, beneficiaryApplicant.id, true)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: petitionerApplicant.id, continuationSheetId: CONT_ID)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, CONT_ID, null)

        then:
        assertCorrectPopulationContinuation(pdfFieldExpressionInfo.pdfFieldDetailList, result['file'])

        cleanup:
        testHelper.clean()
    }

    private void assertCorrectPopulationContinuation(List<PdfFieldDetail> pdfFieldDetailList, InputStream resultPdf) {
        List<PdfFieldDetail> expectedPdfFieldDetailList = []
        pdfFieldDetailList.collect {
            if (it.fieldMappingDetail.continuationSheetName && CONT_NAME.contains(it.fieldMappingDetail.continuationSheetName)) {
                expectedPdfFieldDetailList << it
            }
        }
        assertNotNull(resultPdf)
        TestPdfUtils.writeToFile(resultPdf, pdfFolder, CONT_NAME)

        PDDocument.load(resultPdf).withCloseable {
            PDAcroForm acroForm = it.getDocumentCatalog().getAcroForm()
            expectedPdfFieldDetailList.each {
                TestPdfUtils.assertRepeatCase(it, acroForm, true)
            }
        }
    }
    
    @Ignore
    void testNullFilename() {
        given:
        List<PdfFieldDetail> pdfFieldDetailList = [new PdfFieldDetail('testId', 'test Q')]

        when:
        pdfPopulationService.getPdf(null, null, null, null, null)

        then:
        Exception e = thrown(RuntimeException)
        assertTrue(!e.getMessage().contains('i_129'))
    }

}
