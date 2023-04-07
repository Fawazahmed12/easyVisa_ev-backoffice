package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.PdfFieldExpressionInfo
import com.easyvisa.questionnaire.services.PdfFieldPrintingParams
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.util.stream.Collectors

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

@Integration
class PreparerSectionPdfFieldExpressionSpec extends TestMockUtils {

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

    void testPreparerSectionPdfFieldExpressions() throws Exception {
        given:
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


        when:
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId: aPackage.id,
                applicantId: petitionerApplicant.id, formId: formId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        then:
        assertNotNull(pdfFieldExpressionInfo)
        List<PdfFieldDetail> pdfFieldDetailList = pdfFieldExpressionInfo.pdfFieldDetailList
        assertNotNull(pdfFieldDetailList)
        assertPreparerSectionPdfFieldDetails(pdfFieldDetailList)

        cleanup:
        testHelper.clean()
    }


    private void assertPreparerSectionPdfFieldDetails(List<PdfFieldDetail> pdfFieldDetailList) {
        List<PdfFieldDetail> filteredPdfFieldDetailList = pdfFieldDetailList.stream()
                .filter({ data -> data.questionId != null })
                .collect(Collectors.toList())
        assertEquals(18, filteredPdfFieldDetailList.size())

        PdfFieldDetail daytimePhoneNumberPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3951') })
                .findFirst()
                .orElse(null)
        assertNotNull(daytimePhoneNumberPdfFieldDetail)
        assertEquals(1, daytimePhoneNumberPdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('+639171111111', daytimePhoneNumberPdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail mobileNumberPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3954') })
                .findFirst()
                .orElse(null)
        assertNotNull(mobileNumberPdfFieldDetail)
        assertEquals(1, mobileNumberPdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('99999123123', mobileNumberPdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail lastNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3958') })
                .findFirst()
                .orElse(null)
        assertNotNull(lastNamePdfFieldDetail)
        assertEquals(1, lastNamePdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('Attorney Last', lastNamePdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail firstNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3959') })
                .findFirst()
                .orElse(null)
        assertNotNull(firstNamePdfFieldDetail)
        assertEquals(1, firstNamePdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('Attorney First', firstNamePdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail businessNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3960') })
                .findFirst()
                .orElse(null)
        assertNotNull(businessNamePdfFieldDetail)
        assertEquals(1, businessNamePdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('Package Organization', businessNamePdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail emailAddressPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3961') })
                .findFirst()
                .orElse(null)
        assertNotNull(emailAddressPdfFieldDetail)
        assertEquals(1, emailAddressPdfFieldDetail.getAnswerValueObjectList().size())
        assertTrue(emailAddressPdfFieldDetail.answerValueObjectList[0].value.startsWith('packageattorney'))
        assertTrue(emailAddressPdfFieldDetail.answerValueObjectList[0].value.endsWith('@easyvisa.com'))


        PdfFieldDetail countryPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3962') })
                .findFirst()
                .orElse(null)
        assertNotNull(countryPdfFieldDetail)
        assertEquals(1, countryPdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('United States', countryPdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail streetNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3963') })
                .findFirst()
                .orElse(null)
        assertNotNull(streetNamePdfFieldDetail)
        assertEquals(1, streetNamePdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('3474 Durham Court', streetNamePdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail secondaryAdrressDescPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3965') })
                .findFirst()
                .orElse(null)
        assertNotNull(secondaryAdrressDescPdfFieldDetail)
        assertEquals(1, secondaryAdrressDescPdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('apartment', secondaryAdrressDescPdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail apartmentNamePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3966') })
                .findFirst()
                .orElse(null)
        assertNotNull(apartmentNamePdfFieldDetail)
        assertEquals(1, apartmentNamePdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('Skywrapper ', apartmentNamePdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail cityPdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3967') })
                .findFirst()
                .orElse(null)
        assertNotNull(cityPdfFieldDetail)
        assertEquals(1, cityPdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('Los Angeles', cityPdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail statePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3968') })
                .findFirst()
                .orElse(null)
        assertNotNull(statePdfFieldDetail)
        assertEquals(1, statePdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('CA', statePdfFieldDetail.answerValueObjectList[0].value)


        PdfFieldDetail zipcodePdfFieldDetail = pdfFieldDetailList.stream()
                .filter({ x -> x.getQuestionId().equals('Q_3970') })
                .findFirst()
                .orElse(null)
        assertNotNull(zipcodePdfFieldDetail)
        assertEquals(1, zipcodePdfFieldDetail.getAnswerValueObjectList().size())
        assertEquals('90025', zipcodePdfFieldDetail.answerValueObjectList[0].value)
    }
}
