package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PackageStatus
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.*
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class QuestionnaireControllerSpec extends TestMockUtils {
    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    protected RequestSpecification spec

    @Autowired
    private ProfileService profileService
    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private SectionCompletionStatusService completionService
    @Autowired
    private AdminService adminService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    private TaxService taxServiceMock = Mock(TaxService)

    @Value('${local.server.port}')
    Integer serverPort

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }

    def "test fetch-questionnaire"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
        Long packageId = testHelper.aPackage.id
        Long petitionerApplicantId
        Package.withNewTransaction {
            petitionerApplicantId = testHelper.aPackage.refresh().petitioner.applicant.id
        }
        def url = "/api/questionnaire/questions"
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('fetch-questionnaire',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('packageId').description('Package Id'),
                        fieldWithPath('applicantId').description('Applicant Id'),
                        fieldWithPath('sectionId').description('Section Id')
                )
        ))
                .body("""
                    {"packageId":"${packageId}", "applicantId" : "${petitionerApplicantId}", "sectionId":"Sec_1"
                }
                    """)
                .when()
                .port(this.serverPort)
                .post(url)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))


        cleanup:
        testHelper.clean()
    }

    def "test fetch-questionnaire-answers"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        String sectionId = "Sec_1"
        Long packageId = testHelper.aPackage.id
        Long petitionerId
        Package.withNewTransaction {
            petitionerId = testHelper.aPackage.refresh().petitioner.applicant.id
        }
        def answerList = AnswerListStub.answerList(packageId, petitionerId)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, packageId, petitionerId, sectionId,
                answerList);
        def url = "/api/questionnaire/packages/${packageId}/applicants/${petitionerId}/sections/${sectionId}"

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('fetch-questionnaire-answers',
                preprocessResponse(prettyPrint())))
                .body()
                .when()
                .port(this.serverPort)
                .get(url)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    def "test fetch-package-sections"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
        Long packageId = testHelper.aPackage.id
        def url = "/api/package/${packageId}/sections"

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('fetch-package-sections',
                preprocessResponse(prettyPrint())))
                .body()
                .when()
                .port(this.serverPort)
                .get(url)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    void testEmptyProgress() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPetitioner)
                .when()
                .port(this.serverPort)
                .get('/api/questionnaire/progress/package/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('[0].applicantType', equalTo(ApplicantType.Petitioner.value))
                .body('[0].dateCompleted', is(nullValue()))
                .body('[0].totalDays', is(nullValue()))
                .body('[0].dateStarted', is(nullValue()))
                .body('[0].elapsedDays', is(nullValue()))
                .body('[0].percentComplete', is(nullValue()))
                .body('[0].name', equalTo('petitioner-first petitioner-last'))
                .body('[0].packageStatus', equalTo(PackageStatus.LEAD.displayName))
                .body('[1].applicantType', equalTo(ApplicantType.Beneficiary.value))
                .body('[1].dateCompleted', is(nullValue()))
                .body('[1].totalDays', is(nullValue()))
                .body('[1].dateStarted', is(nullValue()))
                .body('[1].elapsedDays', is(nullValue()))
                .body('[1].percentComplete', is(nullValue()))
                .body('[1].name', equalTo('applicant-first applicant-last'))
                .body('[1].packageStatus', equalTo(PackageStatus.LEAD.displayName))
                .body('[2]', is(nullValue()))

        cleanup:
        testHelper.clean()
    }

    void testEmptyProgressDerivatives() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndTwoBeneficiariesOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()
        testHelper.deletePackageAnswersOnly();

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPetitioner)
                .when()
                .port(this.serverPort)
                .get('/api/questionnaire/progress/package/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('[0].applicantType', equalTo(ApplicantType.Petitioner.value))
                .body('[0].dateCompleted', is(nullValue()))
                .body('[0].totalDays', is(nullValue()))
                .body('[0].dateStarted', is(nullValue()))
                .body('[0].elapsedDays', is(nullValue()))
                .body('[0].percentComplete', is(nullValue()))
                .body('[0].name', equalTo('petitioner-first petitioner-last'))
                .body('[0].packageStatus', equalTo(PackageStatus.OPEN.displayName))
                .body('[1].applicantType', equalTo(ApplicantType.Beneficiary.value))
                .body('[1].dateCompleted', is(nullValue()))
                .body('[1].totalDays', is(nullValue()))
                .body('[1].dateStarted', is(nullValue()))
                .body('[1].elapsedDays', is(nullValue()))
                .body('[1].percentComplete', is(nullValue()))
                .body('[1].name', equalTo('applicant-first applicant-last'))
                .body('[1].packageStatus', equalTo(PackageStatus.OPEN.displayName))
                .body('[2]', is(nullValue()))

        cleanup:
        testHelper.clean()
    }

    void testProgress() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndTwoBeneficiariesOpenPackage(ImmigrationBenefitCategory.F1_A, ImmigrationBenefitCategory.F1_A)
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()

        Long packageId = testHelper.aPackage.id
        Answer.withNewTransaction {
            Petitioner petitioner = testHelper.aPackage.refresh().petitioner
            Long applicantId = petitioner.applicant.id
            List<Answer> answers = AnswerListPdfRulesStub.petitionerLegalName(packageId, applicantId)
            answers.addAll(AnswerListPdfRulesStub.otherNamesUsed(packageId, applicantId))
            answers.addAll(AnswerListPdfRulesStub.assetSingleValues(packageId, applicantId))
            QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, packageId, applicantId, '',
                    answers)
            QuestionnaireTestDBSetupUtility.updateCompletionSections(completionService, packageId, applicantId,
                    ['Sec_2', 'Sec_assets'])
            Long beneficiaryId = testHelper.aPackage.principalBeneficiary.id
            answers = AnswerListPdfRulesStub.beneficiaryLegalName(packageId, beneficiaryId)
            answers.addAll(AnswerListPdfRulesStub.beneficiaryOtherNamesTripleWithAnumber(packageId, beneficiaryId))
            QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, packageId, beneficiaryId, '',
                    answers)
            QuestionnaireTestDBSetupUtility.updateCompletionSections(completionService, packageId, beneficiaryId,
                    ['Sec_nameForBeneficiary'])
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPetitioner)
                .filter(document('get-package-progress',
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName('id').description('package id')
                ),
                responseFields(
                        fieldWithPath('[].name').description('Success message'),
                        fieldWithPath('[].applicantType').description('Applicant type: Petitioner, Beneficiary or Derivative Beneficiary'),
                        fieldWithPath('[].packageStatus').description('Package status'),
                        fieldWithPath('[].dateStarted').description('Date of the first answer (MM/dd/yyyy)'),
                        fieldWithPath('[].elapsedDays').description('Days from start of answering. Populates when all answers are not completed'),
                        fieldWithPath('[].percentComplete').description('Integer value of current progress'),
                        fieldWithPath('[].dateCompleted').description('Date when all answers were completed (MM/dd/yyyy)'),
                        fieldWithPath('[].totalDays').description('Integer value of spent days to answer all questions.')
                )))
                .when()
                .port(this.serverPort)
                .get('/api/questionnaire/progress/package/{id}', packageId)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('[0].applicantType', equalTo(ApplicantType.Petitioner.value))
                .body('[0].dateCompleted', is(nullValue()))
                .body('[0].totalDays', is(nullValue()))
                .body('[0].dateStarted', equalTo(new Date().format(DateUtil.PDF_FORM_DATE_FORMAT)))
                .body('[0].elapsedDays', equalTo(0))
                .body('[0].percentComplete', equalTo(7))
                .body('[0].name', equalTo('Andrew White'))
                .body('[0].packageStatus', equalTo(PackageStatus.OPEN.displayName))
                .body('[1].applicantType', equalTo(ApplicantType.Beneficiary.value))
                .body('[1].dateCompleted', is(nullValue()))
                .body('[1].totalDays', is(nullValue()))
                .body('[1].dateStarted', equalTo(new Date().format(DateUtil.PDF_FORM_DATE_FORMAT)))
                .body('[1].elapsedDays', equalTo(0))
                .body('[1].percentComplete', equalTo(14))
                .body('[1].name', equalTo('Avery Martin'))
                .body('[1].packageStatus', equalTo(PackageStatus.OPEN.displayName))
                .body('[2]', is(nullValue()))

        cleanup:
        testHelper.clean()
    }

}
