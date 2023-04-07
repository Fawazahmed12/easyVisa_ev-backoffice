package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.ContinuationSheetHeaderInfo
import com.easyvisa.questionnaire.services.ContinuationSheetService
import com.easyvisa.utils.*
import grails.testing.mixin.integration.Integration
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

@Integration
@Ignore
// As of now we are removing Form_134.
class PdfSumEachRuleSpec extends TestMockUtils {

    static final String CONTINUATION_FILE_ID = 'CS_134_realEstate'
    static final String CONTINUATION_FILE_NAME = '134 Continuation Sheet - Page 3, Part 3, Items 8a - 9e - Affidavit' +
            ' of Support - Real Property (Real Estate) 2016-11-30 v02.pdf'

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

    @Ignore
    void test134DoubleRealEstate() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.realEstateDouble(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, CONTINUATION_FILE_ID, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134Continuation(aPackage, CONTINUATION_FILE_ID, '1 Drive bolivar', 'None',
                'New York City', '60,000', '130,000', '70,000'), CONTINUATION_FILE_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    /*@Ignore
    void test134DoubleRealEstateNoMortgage() throws Exception {
        given:
        String sectionId = 'Sec_biographicInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.realEstateDouble(aPackage.id, petitionerApplicant.id,
                Boolean.FALSE)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, CONTINUATION_FILE_ID, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap134Continuation(aPackage, CONTINUATION_FILE_ID, '1 Drive bolivar', 'None',
                'New York City', '', '130,000', '130,000'), CONTINUATION_FILE_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }*/

    private Map<String, String> assertionMap134Continuation(def aPackage, String continuationSheetId, String street = '', String address1 = '',
                                                            String city = '', String balance = '', String marketvalue = '',
                                                            String cashValue = '') {
        Map<String, String> result = [
                'StreetAddress1'  : street,
                'Apt1'            : '',
                'Ste1'            : '',
                'Flr1'            : '',
                'Address1'        : address1,
                'CityTownVillage1': city,
                //TODO: handle country deletion form questionnaire
//                'State1'          : 'New York',
//                'ZIP1'            : '12345',
                'Balance1'        : balance,
                'MarketValue1'    : marketvalue,
                'CashValue1'      : cashValue,
                'StreetAddress2'  : '',
                'Apt2'            : '',
                'Ste2'            : '',
                'Flr2'            : '',
                'Address2'        : '',
                'CityTownVillage2': '',
                'State2'          : '',
                'ZIP2'            : '',
                'Balance2'        : '',
                'MarketValue2'    : '',
                'CashValue2'      : '',
                'StreetAddress3'  : '',
                'Apt3'            : '',
                'Ste3'            : '',
                'Flr3'            : '',
                'Address3'        : '',
                'CityTownVillage3': '',
                'State3'          : '',
                'ZIP3'            : '',
                'Balance3'        : '',
                'MarketValue3'    : '',
                'CashValue3'      : '',
                'StreetAddress4'  : '',
                'Apt4'            : '',
                'Ste4'            : '',
                'Flr4'            : '',
                'Address4'        : '',
                'CityTownVillage4': '',
                'State4'          : '',
                'ZIP4'            : '',
                'Balance4'        : '',
                'MarketValue4'    : '',
                'CashValue4'      : ''
        ]
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = continuationSheetService.getContinuationSheetHeaderByContinuationSheetId(aPackage, continuationSheetId);
        result.putAll(TestPdfUtils.assertionMapContinuationSheetHeader(continuationSheetHeaderInfo, '3', '3', '8.a - 9.e'))
        result
    }

}
