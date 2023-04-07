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
class PdfPrintTextRuleSpec extends TestMockUtils {

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

    void testIntroSectionPdfPrintTextRule() throws Exception {
        given:
        String sectionId = 'Sec_1'
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
        List<Answer> answerList = AnswerListStub.form864ExclusionIncomeHistoryAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: petitionerApplicant.id, formId: formId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        then:
        assertNotNull(pdfFieldExpressionInfo.pdfFieldDetailList)
        assertIntroSectionPdfFieldDetails(pdfFieldExpressionInfo.pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }

    private void assertIntroSectionPdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        PdfFieldDetail beneficiaryRelatedToYouPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_27') })
                .findFirst()
                .orElse(null)
        assertNotNull(beneficiaryRelatedToYouPdfFieldDetail)
        assertEquals(1, beneficiaryRelatedToYouPdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('Fiancé(e)', beneficiaryRelatedToYouPdfFieldDetail.getAnswerValueObjectList()[0].printValue)
    }

    void testTravelToUSSectionPdfPrintTextRule() throws Exception {
        given:
        String sectionId = 'Sec_travelToTheUnitedStates'
        String formId = 'Form_601A'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                             attorneyService: attorneyService,
                                                                             packageService : packageService,
                                                                             adminService   : adminService,
                                                                             answerService  : answerService,
                                                                             profileService : profileService])
        testHelper.buildNoPetitionerOpenPackage(ImmigrationBenefitCategory.SIX01A)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.travelToUSSectionPdfPrintTextRuleAnswerList(aPackage.id, applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: applicant.id, formId: formId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        then:
        assertNotNull(pdfFieldExpressionInfo.pdfFieldDetailList)
        assertTravelToUSSectionPdfFieldDetails(pdfFieldExpressionInfo.pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }

    private void assertTravelToUSSectionPdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        PdfFieldDetail legalStatusWhenULastEnteredUSPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2951') })
                .findFirst()
                .orElse(null)
        assertNotNull(legalStatusWhenULastEnteredUSPdfFieldDetail)
        assertEquals(1, legalStatusWhenULastEnteredUSPdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('PWI', legalStatusWhenULastEnteredUSPdfFieldDetail.getAnswerValueObjectList()[0].printValue)
    }

    void testForm601ExtremeHardshipSectionPdfPrintTextRule() throws Exception {
        given:
        String sectionId = 'Sec_extremeHardshipForRelatives'
        String formId = 'Form_601'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerOpenPackage()

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.form601ExtremeHardshipSectionPdfPrintTextRuleAnswerList(aPackage.id,
                applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: applicant.id, formId: formId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        then:
        assertNotNull(pdfFieldExpressionInfo.pdfFieldDetailList)
        assertForm601ExtremeHardshipSectionPdfFieldDetails(pdfFieldExpressionInfo.pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }

    private void assertForm601ExtremeHardshipSectionPdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        PdfFieldDetail relationshipToRelativeWhoWillExperienceExtremeHardship1PdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3651') })
                .findFirst()
                .orElse(null)
        assertNotNull(relationshipToRelativeWhoWillExperienceExtremeHardship1PdfFieldDetail)
        assertEquals(1, relationshipToRelativeWhoWillExperienceExtremeHardship1PdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('U.S. Citizen Parent', relationshipToRelativeWhoWillExperienceExtremeHardship1PdfFieldDetail.getAnswerValueObjectList()[0].printValue)
    }

    void testForm601AExtremeHardshipSectionPdfPrintTextRule() throws Exception {
        given:
        String sectionId = 'Sec_extremeHardshipForRelatives'
        String formId = 'Form_601A'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerOpenPackage(ImmigrationBenefitCategory.SIX01A)

        Package aPackage = testHelper.aPackage
        Applicant applicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.form601AExtremeHardshipSectionPdfPrintTextRuleAnswerList(aPackage.id,
                applicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, applicant.id, sectionId,
                answerList)

        when:
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: applicant.id, formId: formId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        then:
        assertNotNull(pdfFieldExpressionInfo.pdfFieldDetailList)
        assertForm601AExtremeHardshipSectionPdfFieldDetails(pdfFieldExpressionInfo.pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }

    private void assertForm601AExtremeHardshipSectionPdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        PdfFieldDetail relationshipToRelativeWhoWillExperienceExtremeHardship1PdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3652') })
                .findFirst()
                .orElse(null)
        assertNotNull(relationshipToRelativeWhoWillExperienceExtremeHardship1PdfFieldDetail)
        assertEquals(1, relationshipToRelativeWhoWillExperienceExtremeHardship1PdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('U.S. Citizen Spouse', relationshipToRelativeWhoWillExperienceExtremeHardship1PdfFieldDetail.getAnswerValueObjectList()[0].printValue)
    }

}
