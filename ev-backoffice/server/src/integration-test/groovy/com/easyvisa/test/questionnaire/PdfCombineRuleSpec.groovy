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
class PdfCombineRuleSpec extends TestMockUtils {

    private static final PdfForm FORM_864 = PdfForm.I864
    private static final String FORM_864_NAME = 'I864.pdf'

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

    void test864Empty() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864(), FORM_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864() throws Exception {
        given:
        String sectionId = 'Sec_2'
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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('Andrew James White'),
                FORM_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864NoMiddle() throws Exception {
        given:
        String sectionId = 'Sec_2'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalNameNoMiddle(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('Andrew White'), FORM_864_NAME,
                pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864Household() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdSingle(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', '', '',
                'Michael Mason Davis', 'Michael Mason Davis'), FORM_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864HouseholdNoMiddle() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdSingleNoMiddleNoAssets(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', '', '',
                'Michael Davis', 'Michael Davis'), FORM_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864HouseholdWithOutDependents() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdSingleWithOutDependents(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', 'yes', 'Michael Mason Davis',
                '', 'Michael Mason Davis'), FORM_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864HouseholdNoMiddleWithOutDependents() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList =
                AnswerListPdfRulesStub.petitionerHouseholdSingleWithOutDependentsNoMiddleNoAssets(aPackage.id,
                        petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', 'true',
                'Michael Davis', '', 'Michael Davis'), FORM_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864HouseholdDouble() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdDouble(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', '', '',
                'Refer to names on included contracts', 'Michael Mason Davis', 'Olivia Ella Reed'),
                FORM_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864HouseholdTriple() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdTriple(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', 'yes',
                'Alexander Daniel Rogers', 'Refer to names on included contracts',
                'Michael Mason Davis', 'Olivia Ella Reed', 'Alexander Daniel Rogers'),
                FORM_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test864HouseholdQuadro() throws Exception {
        given:
        String sectionId = 'Sec_familyInformation'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)
        testHelper.deletePackageAnswersOnly()
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerHouseholdQuadro(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_864.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap864('', 'yes',
                'Alexander Daniel Rogers', 'Refer to names on included contracts',
                'Michael Mason Davis', 'Olivia Ella Reed', 'Alexander Daniel Rogers',
                'Grace Samantha Butler'), FORM_864_NAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap864(String petitionerName = '', String noNeedCheckbox = '', String noNeedValue = '',
                                                String assetsName = '', String houseName1 = '', String houseName2 = '',
                                                String houseName3 = '', String houseName4 = '') {
        [
                'form1[0].#subform[0].P1_Line1_Name[0]'            : petitionerName,
                'form1[0].#subform[4].P6_Line17_NotNeedComplete[0]': noNeedCheckbox,
                'form1[0].#subform[4].P6_Line17_Name[0]'           : noNeedValue,
                'form1[0].#subform[4].P7_Line5a_NameofRelative[0]' : assetsName,
                'form1[0].#subform[3].P6_Line3_Name[0]'            : houseName1,
                'form1[0].#subform[3].P6_Line6_Name[0]'            : houseName2,
                'form1[0].#subform[3].P6_Line9_Name[0]'            : houseName3,
                'form1[0].#subform[3].P6_Line12_Name[0]'           : houseName4
        ]
    }

}
