package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.PdfFieldExpressionInfo
import com.easyvisa.questionnaire.services.PdfFieldPrintingParams
import com.easyvisa.utils.*
import grails.testing.mixin.integration.Integration
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import static org.junit.Assert.assertNotNull

@Integration
class PdfPopulationService693Spec extends TestMockUtils {

    static final PdfForm PDF_FORM = PdfForm.I693

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
        TestPdfUtils.writeToFile(resultPdf, pdfFolder, 'I693.pdf')

        PDDocument.load(resultPdf).withCloseable {
            PDAcroForm acroForm = it.getDocumentCatalog().getAcroForm()
            TestPdfUtils.assertAllDataPopulated(pdfFieldDetailList, acroForm)
            TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[0].Pt1Line1a_FamilyName[0]', 'Dubois')
            TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[2].Pt1Line1b_GivenName[2]', 'Francoise')
            TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[3].Pt1Line1c_MiddleName[3]', 'Valerie')
            TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[0].Pt1Line8_CityTownVillageofBirth[0]', 'Rouen')
            TestPdfUtils.assertCustomValue(acroForm, 'form1[0].#subform[1].Pt1Line3e_AlienNumber[1]', '569823140')
        }
    }

}
