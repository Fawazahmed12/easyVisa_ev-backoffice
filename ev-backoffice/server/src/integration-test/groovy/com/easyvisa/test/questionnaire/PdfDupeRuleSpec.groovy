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
class PdfDupeRuleSpec extends TestMockUtils {

    static final PdfForm FORM_130 = PdfForm.I130
    static final String FORM_130_FILENAME = 'I130.pdf'

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

    void test130Empty() throws Exception {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)
        testHelper.deletePackageAnswersOnly();
        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130(), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerOnly() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalNameBirth(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Andrew', 'James', 'White',
                '11/27/1975', 'Barbados'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerFianceToBeneficiary() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalNameFianceToBeneficiary(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Andrew', 'James', 'White',
                '11/27/1975', 'Barbados'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerSpouseToBeneficiary() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalNameSpouseToBeneficiary(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Andrew', 'James', 'White', '11/27/1975', 'Barbados',
                'Andrew', 'James', 'White', '11/27/1975', 'Barbados'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerSiblingToBeneficiaryWithoutSpouse() throws Exception {
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
        List<Answer> answerList =
                AnswerListPdfRulesStub.petitionerLegalNameSiblingToBeneficiaryWithoutSpouse(aPackage.id,
                        petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Andrew', 'James', 'White',
                '11/27/1975', 'Barbados'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerParentToBeneficiaryWithoutSpouse() throws Exception {
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
        List<Answer> answerList =
                AnswerListPdfRulesStub.petitionerLegalNameParentToBeneficiaryWithoutSpouse(aPackage.id,
                        petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Andrew', 'James', 'White',
                '11/27/1975', 'Barbados'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerSiblingToBeneficiaryWithSpouse() throws Exception {
        given:
        String sectionId = 'Sec_2'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        Applicant beneficiaryApplicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalNameSiblingToBeneficiaryWithSpouse(aPackage.id,
                petitionerApplicant.id, beneficiaryApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Andrew', 'James', 'White', '11/27/1975', 'Barbados',
                'Matthew', 'Liam', 'Jackson', '01/01/1970', 'United Kingdom',
                'Matthew', 'Liam', 'Jackson'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    void test130PetitionerParentToBeneficiaryWithSpouse() throws Exception {
        given:
        String sectionId = 'Sec_2'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.IR1)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        Applicant beneficiaryApplicant = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerLegalNameParentToBeneficiaryWithSpouse(aPackage.id,
                petitionerApplicant.id, beneficiaryApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, FORM_130.formId, null, null)

        then:
        TestPdfUtils.assertPdfValues(result['file'], assertionMap130('Andrew', 'James', 'White', '11/27/1975', 'Barbados',
                'Matthew', 'Liam', 'Jackson', '01/01/1970', 'United Kingdom',
                'Matthew', 'Liam', 'Jackson'), FORM_130_FILENAME, pdfFolder)

        cleanup:
        testHelper.clean()
    }

    private Map<String, String> assertionMap130(String petitionerFName = '', String petitionerMName = '', String petitionerLName = '',
                                                String petitionerDOB = '', String petitionerBirthCountry = '',
                                                String dupeFName = '', String dupeMName = '', String dupeLName = '',
                                                String dupeDOB = '', String dupeBirthCountry = '', String spouseFName = '',
                                                String spouseMName = '', String spouseLName = '') {
        [
                'form1[0].#subform[0].Pt2Line4b_GivenName[0]'     : petitionerFName,
                'form1[0].#subform[0].Pt2Line4c_MiddleName[0]'    : petitionerMName,
                'form1[0].#subform[0].Pt2Line4a_FamilyName[0]'    : petitionerLName,
                'form1[0].#subform[1].Pt2Line8_DateofBirth[0]'    : petitionerDOB,
                'form1[0].#subform[1].Pt2Line7_CountryofBirth[0]' : petitionerBirthCountry,
                'form1[0].#subform[5].Pt4Line30b_GivenName[0]'   : dupeFName,
                'form1[0].#subform[5].Pt4Line30c_MiddleName[0]'  : dupeMName,
                'form1[0].#subform[5].Pt4Line30a_FamilyName[0]'   : dupeLName,
                'form1[0].#subform[5].Pt4Line32_DateOfBirth[0]'   : dupeDOB,
                'form1[0].#subform[5].Pt4Line49_CountryOfBirth[0]': dupeBirthCountry,
                'form1[0].#subform[5].Pt4Line16b_GivenName[0]'    : spouseFName,
                'form1[0].#subform[5].Pt4Line16c_MiddleName[0]'   : spouseMName,
                'form1[0].#subform[5].Pt4Line16a_FamilyName[0]'   : spouseLName,
        ]
    }

}
