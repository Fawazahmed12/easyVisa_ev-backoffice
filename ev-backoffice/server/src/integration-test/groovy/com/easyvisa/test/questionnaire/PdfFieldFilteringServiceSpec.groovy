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
class PdfFieldFilteringServiceSpec extends TestMockUtils {

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


    void testDerivativeCurrentSpousePdfFieldExpressions() throws Exception {
        given:
        String sectionId = 'Sec_familyInformationForBeneficiary'
        String formId = 'Form_864'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant principalBeneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.populateDerivativeFamilyInformationAnswerList(aPackage.id, principalBeneficiary.id, true, false);
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, principalBeneficiary.id,
                sectionId, answerList)

        when:
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: principalBeneficiary.id, formId: formId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        then:
        assertNotNull(pdfFieldExpressionInfo)
        List<PdfFieldDetail> pdfFieldDetailList = pdfFieldExpressionInfo.pdfFieldDetailList
        assertNotNull(pdfFieldDetailList)
        assertEquals(33, pdfFieldDetailList.size())
        assertCurrentSPousePdfFieldDetails(pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }

    private void assertCurrentSPousePdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        assertNotNull(pdfFieldDetailList)
        PdfFieldDetail spouseGivenNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2789') })
                .findFirst()
                .orElse(null)
        assertNotNull(spouseGivenNamePdfFieldDetail)
        assertEquals(spouseGivenNamePdfFieldDetail.questionName, "currentSpouse_GivenName");
        assertEquals(spouseGivenNamePdfFieldDetail.getAnswerValueObjectList().size(), 1);
        assertEquals(spouseGivenNamePdfFieldDetail.getAnswerValueObjectList()[0].printValue, "Stephen");
        assertEquals(spouseGivenNamePdfFieldDetail.getAnswerValueObjectList()[0].path, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2789");


        PdfFieldDetail spouseMiddleNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2790') })
                .findFirst()
                .orElse(null)
        assertNotNull(spouseMiddleNamePdfFieldDetail)
        assertEquals(spouseMiddleNamePdfFieldDetail.questionName, "currentSpouse_MiddleName");
        assertEquals(spouseMiddleNamePdfFieldDetail.getAnswerValueObjectList().size(), 1);
        assertEquals(spouseMiddleNamePdfFieldDetail.getAnswerValueObjectList()[0].printValue, "K");
        assertEquals(spouseMiddleNamePdfFieldDetail.getAnswerValueObjectList()[0].path, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2790");


        PdfFieldDetail spouseLastNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2791') })
                .findFirst()
                .orElse(null)
        assertNotNull(spouseLastNamePdfFieldDetail)
        assertEquals(spouseLastNamePdfFieldDetail.questionName, "currentSpouse_LastName");
        assertEquals(spouseLastNamePdfFieldDetail.getAnswerValueObjectList().size(), 1);
        assertEquals(spouseLastNamePdfFieldDetail.getAnswerValueObjectList()[0].printValue, "Peterson");
        assertEquals(spouseLastNamePdfFieldDetail.getAnswerValueObjectList()[0].path, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2791");


        PdfFieldDetail givenNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2743') })
                .findFirst()
                .orElse(null)
        assertEquals(givenNamePdfFieldDetail, null);

        PdfFieldDetail middleNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2746') })
                .findFirst()
                .orElse(null)
        assertEquals(middleNamePdfFieldDetail, null);

        PdfFieldDetail lastNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2749') })
                .findFirst()
                .orElse(null)
        assertEquals(lastNamePdfFieldDetail, null);

        PdfFieldDetail countryOfBirthPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2753') })
                .findFirst()
                .orElse(null)
        assertEquals(countryOfBirthPdfFieldDetail, null);

        PdfFieldDetail dateOfBirthPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2754') })
                .findFirst()
                .orElse(null)
        assertEquals(dateOfBirthPdfFieldDetail, null)
    }


    void testDerivativeChildrenInformationPdfFieldExpressions() throws Exception {
        given:
        String sectionId = 'Sec_familyInformationForBeneficiary'
        String formId = 'Form_864'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant principalBeneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListStub.populateDerivativeFamilyInformationAnswerList(aPackage.id, principalBeneficiary.id, false, true);
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, principalBeneficiary.id,
                sectionId, answerList)

        when:
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: principalBeneficiary.id, formId: formId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        then:
        assertNotNull(pdfFieldExpressionInfo)
        List<PdfFieldDetail> pdfFieldDetailList = pdfFieldExpressionInfo.pdfFieldDetailList
        assertNotNull(pdfFieldDetailList)
        assertEquals(34, pdfFieldDetailList.size())
        assertChildrenInformationPdfFieldDetails(pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }

    private void assertChildrenInformationPdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        PdfFieldDetail givenNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2743') })
                .findFirst()
                .orElse(null)
        assertNotNull(givenNamePdfFieldDetail)
        assertEquals(givenNamePdfFieldDetail.questionName, "childGivenName");
        assertEquals(givenNamePdfFieldDetail.getAnswerValueObjectList().size(), 1);
        assertEquals(givenNamePdfFieldDetail.getAnswerValueObjectList()[0].printValue, "Mohamed");
        assertEquals(givenNamePdfFieldDetail.getAnswerValueObjectList()[0].path, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/0");


        PdfFieldDetail middleNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2746') })
                .findFirst()
                .orElse(null)
        assertNotNull(middleNamePdfFieldDetail)
        assertEquals(middleNamePdfFieldDetail.questionName, "childMiddleName");
        assertEquals(middleNamePdfFieldDetail.getAnswerValueObjectList().size(), 1);
        assertEquals(middleNamePdfFieldDetail.getAnswerValueObjectList()[0].printValue, "A M");
        assertEquals(middleNamePdfFieldDetail.getAnswerValueObjectList()[0].path, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/0");


        PdfFieldDetail lastNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2749') })
                .findFirst()
                .orElse(null)
        assertNotNull(lastNamePdfFieldDetail)
        assertEquals(lastNamePdfFieldDetail.questionName, "childLastName");
        assertEquals(lastNamePdfFieldDetail.getAnswerValueObjectList().size(), 1);
        assertEquals(lastNamePdfFieldDetail.getAnswerValueObjectList()[0].printValue, "Rizwan");
        assertEquals(lastNamePdfFieldDetail.getAnswerValueObjectList()[0].path, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/0");


        PdfFieldDetail countryOfBirthPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2753') })
                .findFirst()
                .orElse(null)
        assertEquals(countryOfBirthPdfFieldDetail, null);

        PdfFieldDetail dateOfBirthPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2754') })
                .findFirst()
                .orElse(null)
        assertEquals(countryOfBirthPdfFieldDetail, null);


        PdfFieldDetail spouseGivenNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2789') })
                .findFirst()
                .orElse(null)
        assertEquals(spouseGivenNamePdfFieldDetail, null)

        PdfFieldDetail spouseMiddleNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2790') })
                .findFirst()
                .orElse(null)
        assertEquals(spouseMiddleNamePdfFieldDetail, null)

        PdfFieldDetail spouseLastNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_2791') })
                .findFirst()
                .orElse(null)
        assertEquals(spouseLastNamePdfFieldDetail, null)
    }
}
