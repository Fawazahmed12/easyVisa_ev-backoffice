package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.PdfFieldExpressionInfo
import com.easyvisa.questionnaire.services.PdfFieldPrintingParams
import com.easyvisa.utils.AnswerListStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Integration
class PdfFieldExpressionSpec extends TestMockUtils {

    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService
    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
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

    void setup() {
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }


    void testIntroSectionPdfFieldExpressions() throws Exception {
        given:
        String sectionId = 'Sec_1'
        String formId = 'Form_129F'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.answerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: petitionerApplicant.id, formId: formId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        then:
        assertNotNull(pdfFieldExpressionInfo)
        List<PdfFieldDetail> pdfFieldDetailList = pdfFieldExpressionInfo.pdfFieldDetailList
        assertNotNull(pdfFieldDetailList)
        assertEquals(39, pdfFieldDetailList.size())
        assertIntroSectionPdfFieldDetails(pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }

    private void assertIntroSectionPdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        PdfFieldDetail everFilledPetitionPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_1') })
                .findFirst()
                .orElse(null)
        assertNotNull(everFilledPetitionPdfFieldDetail)
        assertEquals(1, everFilledPetitionPdfFieldDetail.getAnswerValueObjectList().size())
        assertNotNull(everFilledPetitionPdfFieldDetail.fieldMappingDetail)
        assertEquals(1, everFilledPetitionPdfFieldDetail.fieldMappingDetail.getFieldExpressions().size())
        assertEquals('form1[0].#subform[3].Pt1Line43_Checkboxes[0]', everFilledPetitionPdfFieldDetail.fieldMappingDetail.getFieldExpressions()[0])

        PdfFieldDetail middleNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3') })
                .findFirst()
                .orElse(null)
        assertNotNull(middleNamePdfFieldDetail)
        assertEquals(2, middleNamePdfFieldDetail.getAnswerValueObjectList().size())
        assertNotNull(middleNamePdfFieldDetail.fieldMappingDetail)
        assertEquals(middleNamePdfFieldDetail.fieldMappingDetail.continuationSheetName, '129F Continuation Sheet - Page 4, Part 1, Items 44 - 47 (Prior Petitions) 2017-04-10')
        assertEquals(2, middleNamePdfFieldDetail.getAnswerValueObjectList().size())
    }

    private void assertAddressHistorySectionPdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        assertEquals(23, pdfFieldDetailList.size())

        PdfFieldDetail countryPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_66') })
                .findFirst()
                .orElse(null)
        assertNotNull(countryPdfFieldDetail)
        assertEquals(3, countryPdfFieldDetail.getAnswerValueObjectList().size())
        assertNotNull(countryPdfFieldDetail.fieldMappingDetail)
        assertEquals(countryPdfFieldDetail.fieldMappingDetail.fieldType, 'repeat')
        assertEquals(countryPdfFieldDetail.fieldMappingDetail.continuationSheetName, '129F Continuation Sheet - Page 4, Part 1, Items 50a-51b (U.S. States and Foreign Countries Resided in Since 18th Birthday) 2017-04-10')
        assertEquals(3, countryPdfFieldDetail.fieldMappingDetail.getFieldExpressions().size())
        assertEquals('form1[0].#subform[3].Pt1Line50b_CountryOfCitzOrNationality[0]', countryPdfFieldDetail.fieldMappingDetail.getFieldExpressions()[0])
        assertEquals('form1[0].#subform[3].Pt1Line51b_CountryOfCitzOrNationality[0]', countryPdfFieldDetail.fieldMappingDetail.getFieldExpressions()[1])
        assertEquals('StatesCountriesResidedIn', countryPdfFieldDetail.fieldMappingDetail.getFieldExpressions()[2])

        PdfFieldDetail statePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_67') })
                .findFirst()
                .orElse(null)
        assertNotNull(statePdfFieldDetail)
        assertEquals(3, statePdfFieldDetail.getAnswerValueObjectList().size())
        assertNotNull(statePdfFieldDetail.fieldMappingDetail)
        assertEquals(statePdfFieldDetail.fieldMappingDetail.fieldType, 'repeat')
        assertEquals(statePdfFieldDetail.fieldMappingDetail.continuationSheetName, '129F Continuation Sheet - Page 4, Part 1, Items 50a-51b (U.S. States and Foreign Countries Resided in Since 18th Birthday) 2017-04-10')
        assertEquals(3, statePdfFieldDetail.fieldMappingDetail.getFieldExpressions().size())
        assertEquals('form1[0].#subform[3].Pt1Line50a_State[0]', statePdfFieldDetail.fieldMappingDetail.getFieldExpressions()[0])
        assertEquals('form1[0].#subform[3].Pt1Line51a_State[0]', statePdfFieldDetail.fieldMappingDetail.getFieldExpressions()[1])
        assertEquals('StatesCountriesResidedIn', statePdfFieldDetail.fieldMappingDetail.getFieldExpressions()[2])
    }


    void testBiographicInformationSectionPdfFieldExpressions() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        String formId = 'Form_129F'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.biographicInformationAnswerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: petitionerApplicant.id, formId: formId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        then:
        assertNotNull(pdfFieldExpressionInfo.pdfFieldDetailList)
        assertBiographicInformationSectionPdfFieldDetails(pdfFieldExpressionInfo.pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }

    private void assertBiographicInformationSectionPdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        assertEquals(20, pdfFieldDetailList.size())

        PdfFieldDetail weightPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_106') })
                .findFirst()
                .orElse(null)
        assertNotNull(weightPdfFieldDetail)
        assertEquals(1, weightPdfFieldDetail.getAnswerValueObjectList().size())
        assertNotNull(weightPdfFieldDetail.fieldMappingDetail)
        assertEquals(weightPdfFieldDetail.fieldMappingDetail.fieldType, 'split')
        assertEquals(3, weightPdfFieldDetail.fieldMappingDetail.getFieldExpressions().size())
        assertEquals('form1[0].#subform[8].Pt4Line4_HeightInches1[0]', weightPdfFieldDetail.fieldMappingDetail.getFieldExpressions()[0])
        assertEquals('form1[0].#subform[8].Pt4Line4_HeightInches2[0]', weightPdfFieldDetail.fieldMappingDetail.getFieldExpressions()[1])
        assertEquals('form1[0].#subform[8].Pt4Line4_HeightInches3[0]', weightPdfFieldDetail.fieldMappingDetail.getFieldExpressions()[2])
    }


    void testIntroSectionPdfFieldExpressionsUsingSection() throws Exception {
        given:
        String sectionId = 'Sec_1'
        String formId = 'Form_129F'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListStub.answerList(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressionsBySection(aPackage.id, petitionerApplicant.id,
                        formId, sectionId)

        then:
        assertNotNull(pdfFieldExpressionInfo)
        List<PdfFieldDetail> pdfFieldDetailList = pdfFieldExpressionInfo.pdfFieldDetailList
        assertNotNull(pdfFieldDetailList)
        assertEquals(18, pdfFieldDetailList.size())
        assertIntroSectionPdfFieldDetails(pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }
}
