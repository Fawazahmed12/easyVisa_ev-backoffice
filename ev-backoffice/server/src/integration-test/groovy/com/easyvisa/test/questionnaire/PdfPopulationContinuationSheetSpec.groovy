package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.answering.ContinuationSheetHeaderInfo
import com.easyvisa.questionnaire.model.Form
import com.easyvisa.questionnaire.services.ContinuationSheetService
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.*
import grails.testing.mixin.integration.Integration
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.ResourceUtils

import java.time.LocalDate
import java.time.Month

import static com.easyvisa.utils.TestPdfUtils.assertionMapContinuationSheetHeader
import static org.junit.Assert.*

@Integration
class PdfPopulationContinuationSheetSpec extends TestMockUtils {

    private static final String PETITIONER_PREVIOUS_ADDRESS_CONT_ID = 'CS_129F_previousPhysicalAddress'
    private static final String PETITIONER_PREVIOUS_ADDRESS_CONT_FILE = '129F Continuation Sheet - Page 2, Part 1, Items 13 - 20b (Petitioner Employment History) 2017-04-10.pdf'
    private static final String PETITIONER_OTHER_NAMES_CONT_ID = 'CS_129F_6'
    private static final String PETITIONER_OTHER_NAMES_CONT_FILE = '129F Continuation Sheet - Page 1, Part 1, Items 7.a -7.c (Petitioner - Other Names Used) 2017-04-10.pdf'
    private static final String PETITIONER_RESIDENCE_18_CONT_ID = 'CS_129F_residedSince18'
    private static final String PETITIONER_RESIDENCE_18_CONT_FILE = '129F Continuation Sheet - Page 4, Part 1, Items 50a-51b (U.S. States and Foreign Countries Resided in Since 18th Birthday) 2017-04-10.pdf'
    private static final String BENEFICIARY_OTHER_NAMES_CONT_ID = 'CS_765_otherNamesUsedForBeneficiary'
    private static final String BENEFICIARY_OTHER_NAMES_CONT_FILE = '765 Continuation Sheet - Page 1, Part (None), Items 2  (Other Names Used) 2017-07-17.pdf'
    private static final String RESIDENCE_COUNTRY_STATE = 'Residence 3 - State: New York, Country: United States\n' +
            'Residence 4 - State: N/A, Country: France\n' +
            'Residence 5 - State: N/A, Country: Germany\n'
    private static final String RESIDENCE_COUNTRY = 'Residence 3 - State: N/A, Country: Italy\n'
    private static final String RESIDENCE_STATE = 'Residence 3 - State: New Jersey, Country: United States\n'

    @Autowired
    private QuestionnaireService questionnaireService
    @Autowired
    private PackageQuestionnaireVersionService packageQuestionnaireVersionService
    @Autowired
    private ContinuationSheetService continuationSheetService
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

    void setup() {
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }

    private String pdfFolder = null

    void testEmpty() throws Exception {
        given:
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
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, PETITIONER_OTHER_NAMES_CONT_ID, null)

        then:
        assertEmptyPopulation(result['file'], PETITIONER_OTHER_NAMES_CONT_FILE)

        cleanup:
        testHelper.clean()
    }

    void testCommonHeaderPopulation() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.commonHeaderPopulation(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id, sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, PETITIONER_OTHER_NAMES_CONT_ID, null)

        then:
        assertCommonHeaderPopulation(result['file'], assertionMapOtherNamesUsed(aPackage, PETITIONER_OTHER_NAMES_CONT_ID), PETITIONER_OTHER_NAMES_CONT_FILE)

        cleanup:
        testHelper.clean()
    }

    void testResidence18HeaderPopulation() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.residence18HeaderPopulation(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id, sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, PETITIONER_RESIDENCE_18_CONT_ID, null)

        then:
        assertCommonHeaderPopulation(result['file'], assertionMapResidence18(aPackage, RESIDENCE_COUNTRY_STATE, PETITIONER_RESIDENCE_18_CONT_ID), PETITIONER_RESIDENCE_18_CONT_FILE)

        cleanup:
        testHelper.clean()
    }

    void testResidence18HeaderPopulationCountry() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.residence18CountryTriple(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null,
                PETITIONER_RESIDENCE_18_CONT_ID, null)

        then:
        assertCommonHeaderPopulation(result['file'], assertionMapResidence18(aPackage, RESIDENCE_COUNTRY, PETITIONER_RESIDENCE_18_CONT_ID),
                PETITIONER_RESIDENCE_18_CONT_FILE)

        cleanup:
        testHelper.clean()
    }

    void testResidence18HeaderPopulationState() throws Exception {
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
        List<Answer> answerList = AnswerListPdfRulesStub.residence18StateTriple(aPackage.id, petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id, sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, PETITIONER_RESIDENCE_18_CONT_ID, null)

        then:
        assertCommonHeaderPopulation(result['file'], assertionMapResidence18(aPackage, RESIDENCE_STATE, PETITIONER_RESIDENCE_18_CONT_ID),
                PETITIONER_RESIDENCE_18_CONT_FILE)

        cleanup:
        testHelper.clean()
    }

    void testBeneficiaryOtherNames765() throws Exception {
        given:
        String sectionId = 'Sec_nameForBeneficiary'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.principalBeneficiary
        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryOtherNamesQuadro(aPackage.id, beneficiary.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id, sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, beneficiary.id, null, BENEFICIARY_OTHER_NAMES_CONT_ID, null)

        then:
        assertCommonHeaderPopulation(result['file'], assertionMapOtherRel(aPackage, BENEFICIARY_OTHER_NAMES_CONT_ID), BENEFICIARY_OTHER_NAMES_CONT_FILE)

        cleanup:
        testHelper.clean()
    }

    void testPetitionerAddressHistory() throws Exception {
        given:
        String sectionId = 'Sec_nameForBeneficiary'
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.K1K3)

        Package aPackage = testHelper.aPackage
        Applicant petitionerApplicant = aPackage.petitioner.applicant
        List<Answer> answerList = AnswerListPdfRulesStub.petitionerAddressHistoryAnswerList(aPackage.id,
                petitionerApplicant.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, petitionerApplicant.id,
                sectionId, answerList)

        when:
        def result = pdfPopulationService.getPdf(aPackage.id, petitionerApplicant.id, null, PETITIONER_PREVIOUS_ADDRESS_CONT_ID, null)

        then:
        assertCommonHeaderPopulation(result['file'], assertionMapPetitionerAddressHistory(aPackage, PETITIONER_PREVIOUS_ADDRESS_CONT_ID),
                PETITIONER_PREVIOUS_ADDRESS_CONT_FILE)

        cleanup:
        testHelper.clean()
    }

    void testPdfFilesAvailability() throws Exception {
        when:
        QuestionnaireVersion questionnaireVersion = packageQuestionnaireVersionService.getLatestQuestionnaireVersion();
        Map<String, Set<String>> formsMap = [:]
        List<Form> forms = questionnaireService.findAllForms(questionnaireVersion.questVersion)

        forms = forms.findAll { it.pdfForm }

        forms.each {
            Set<String> continuations = continuationSheetService.fetchContinuationSheetsByForm(questionnaireVersion.questVersion, it.id).collect {
                it.sheetName
            }
            formsMap.put(it.pdfForm, continuations)
        }

        then:
        formsMap.each {
            assertTrue(ResourceUtils.getFile("classpath:pdf/forms/${questionnaireVersion.questVersion}/${it.key}").exists())
            it.value.sort().each {
                File contFile = ResourceUtils.getFile("classpath:pdf/continuations/${questionnaireVersion.questVersion}/${it}.pdf")
                assertTrue(contFile.exists())
                contFile.withInputStream {
                    assertCommonHeaderPopulation(it, new HashMap<>(), contFile.name)
                }
            }
        }
    }

    private void assertCommonHeaderPopulation(InputStream resultPdf, Map<String, String> assertionMap, String continuationFile) {
        assertNotNull(resultPdf)
        TestPdfUtils.writeToFile(resultPdf, pdfFolder, "${continuationFile}")

        PDDocument.load(resultPdf).withCloseable {

            PDAcroForm acroForm = it.getDocumentCatalog().getAcroForm()
            assertValues(acroForm, assertionMap)
        }
    }

    private void assertEmptyPopulation(InputStream resultPdf, String continuationFile) {
        assertNotNull(resultPdf)
        TestPdfUtils.writeToFile(resultPdf, pdfFolder, "${continuationFile}")

        PDDocument.load(resultPdf).withCloseable {
            PDAcroForm acroForm = it.getDocumentCatalog().getAcroForm()
            acroForm.getFields().each {
                assertTrue(it.valueAsString.isEmpty())
            }
        }
    }

    private void assertValues(PDAcroForm acroForm, Map<String, String> assertionMap) {
        assertionMap.each {
            TestPdfUtils.assertCustomValue(acroForm, it.key, it.value)
        }
        /*['Page', 'Part', 'Item'].each {
            assertEquals(PDTextField.QUADDING_CENTERED, ((PDTextField) acroForm .getField(it)).getQ())
        }*/
    }

    private Map<String, String> assertionMapOtherNamesUsed(def aPackage, String continuationSheetId) {
        Map<String, String> result =
                [
                        'FirstName2' : 'Thomas',
                        'MiddleName2': 'Harry',
                        'LastName2'  : 'Brown',
                        'FirstName3' : '',
                        'MiddleName3': '',
                        'LastName3'  : '',
                        'FirstName4' : '',
                        'MiddleName4': '',
                        'LastName4'  : '',
                        'FirstName5' : '',
                        'MiddleName5': '',
                        'LastName5'  : '',
                        'FirstName6' : '',
                        'MiddleName6': '',
                        'LastName6'  : '',
                        'FirstName7' : '',
                        'MiddleName7': '',
                        'LastName7'  : ''
                ]
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = continuationSheetService.getContinuationSheetHeaderByContinuationSheetId(aPackage, continuationSheetId);
        result.putAll(assertionMapContinuationSheetHeader(continuationSheetHeaderInfo, '1', '1', '7.a - 7.c'))
        result
    }

    private Map<String, String> assertionMapResidence18(def aPackage, String value, String continuationSheetId) {
        Map<String, String> result =
                [
                        'StatesCountriesResidedIn': value
                ]
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = continuationSheetService.getContinuationSheetHeaderByContinuationSheetId(aPackage, continuationSheetId);
        result.putAll(assertionMapContinuationSheetHeader(continuationSheetHeaderInfo, '4', '1', '50.a - 51.b'))
        result
    }

    private Map<String, String> assertionMapOtherRel(def aPackage, String continuationSheetId) {
        Map<String, String> result =
                [
                        'FirstName4' : 'Tom',
                        'MiddleName4': 'Jerry',
                        'LastName4'  : 'Small',
                        'FirstName5' : '',
                        'MiddleName5': '',
                        'LastName5'  : '',
                        'FirstName6' : '',
                        'MiddleName6': '',
                        'LastName6'  : '',
                        'FirstName7' : '',
                        'MiddleName7': '',
                        'LastName7'  : '',
                        'FirstName8' : '',
                        'MiddleName8': '',
                        'LastName8'  : '',
                        'FirstName9' : '',
                        'MiddleName9': '',
                        'LastName9'  : ''
                ]
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = continuationSheetService.getContinuationSheetHeaderByContinuationSheetId(aPackage, continuationSheetId);
        result.putAll(assertionMapContinuationSheetHeader(continuationSheetHeaderInfo, '1', 'None', '2'))
        result
    }

    private Map<String, String> assertionMapPetitionerAddressHistory(def aPackage, String continuationSheetId) {
        Integer currentYear = DateUtil.today().year
        Map<String, String> result =
                [
                        'StreetNumberAndName1'   : '10 Chkalova street',
                        'Apt1'                   : '',
                        'Ste1'                   : '',
                        'Flr1'                   : '',
                        'SecondaryAddress1'      : 'None',
                        'CityTownVillage1'       : 'Minsk',
                        'StateProvinceTerritory1': 'Minks area',
                        'ZIPPostalCode1'         : '220025',
                        'Country1'               : 'Belarus',
                        'From1'                  : DateUtil.pdfFormDate(LocalDate.of(currentYear - 10, Month.MAY, 1)),
                        'To1'                    : DateUtil.pdfFormDate(LocalDate.of(currentYear - 2, Month.JANUARY, 15)),
                        'StreetNumberAndName2'   : '',
                        'Apt2'                   : '',
                        'Ste2'                   : '',
                        'Flr2'                   : '',
                        'SecondaryAddress2'      : '',
                        'CityTownVillage2'       : '',
                        'StateProvinceTerritory2': '',
                        'ZIPPostalCode2'         : '',
                        'Country2'               : '',
                        'From2'                  : '',
                        'To2'                    : '',
                        'StreetNumberAndName3'   : '',
                        'Apt3'                   : '',
                        'Ste3'                   : '',
                        'Flr3'                   : '',
                        'SecondaryAddress3'      : '',
                        'CityTownVillage3'       : '',
                        'StateProvinceTerritory3': '',
                        'ZIPPostalCode3'         : '',
                        'Country3'               : '',
                        'From3'                  : '',
                        'To3'                    : '',
                        'StreetNumberAndName4'   : '',
                        'Apt4'                   : '',
                        'Ste4'                   : '',
                        'Flr4'                   : '',
                        'SecondaryAddress4'      : '',
                        'CityTownVillage4'       : '',
                        'StateProvinceTerritory4': '',
                        'ZIPPostalCode4'         : '',
                        'Country4'               : '',
                        'From4'                  : ''
                ]
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = continuationSheetService.getContinuationSheetHeaderByContinuationSheetId(aPackage, continuationSheetId);
        result.putAll(assertionMapContinuationSheetHeader(continuationSheetHeaderInfo, '2', '1', '9.a - 12.b'))
        result
    }

}
