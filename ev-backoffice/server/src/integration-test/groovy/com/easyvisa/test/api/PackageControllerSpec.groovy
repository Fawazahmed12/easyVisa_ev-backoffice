package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PackageStatus
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.converters.JSON
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.hibernate.SessionFactory
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import spock.lang.Ignore
import spock.lang.Unroll

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.Matchers.containsInAnyOrder
import static org.hamcrest.Matchers.nullValue
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class PackageControllerSpec extends TestMockUtils {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Autowired
    private ProfileService profileService
    @Autowired
    private PackageService packageService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private SessionFactory sessionFactory
    @Autowired
    private AdminService adminService
    @Autowired
    private AnswerService answerService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService

    @Autowired
    private MessageSource messageSource

    private TaxService taxServiceMock = Mock(TaxService)

    @Value('${local.server.port}')
    Integer serverPort

    @Value('${frontEndAppURL}')
    String frontEndAppURL

    @Value('${loginUrl}')
    String loginUrl

    protected RequestSpecification spec
    protected ResponseFieldsSnippet responseFields
    protected ResponseFieldsSnippet packageResponseFields

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.responseFields = responseFields(
                fieldWithPath('status').description('status of the package'),
                fieldWithPath('questionnaireSyncStatus').description('status of the questionnaire-sync'),
                subsectionWithPath('applicants').description('Array of the applicants'),
                subsectionWithPath('representative').description('Attorney of the package'),
                fieldWithPath('representativeId').description('Attorney id of the package'),
                fieldWithPath('id').description('id of the package'),
                fieldWithPath('easyVisaId').description('EVID of the package'),
                subsectionWithPath('organization').description('organization of the package'),
                fieldWithPath('owed').description('Amount owed for the package'),
                fieldWithPath('welcomeEmailSentOn').description('Welcome email date sent out'),
                fieldWithPath('retainerAgreement').description('Details of retainer agreement for the package'),
                fieldWithPath('welcomeEmailId').description('Id of welcome email of package'),
                fieldWithPath('title').description('Title of package'),
                fieldWithPath('categories').description('Package Categories Abbreviation comma separated '),
                fieldWithPath('inviteApplicantEmailId').description('Id of invite-applicant email of package'),
                fieldWithPath('creationDate').description('Date when package was created'),
                fieldWithPath('lastActiveOn').description('Last activity of package applicants in the system'),
                subsectionWithPath('assignees').description('List of attorneys who were assigned this package'),
                fieldWithPath('questionnaireCompletedPercentage').description('Questionnaire Completion Percentage'),
                fieldWithPath('documentCompletedPercentage').description('Document upload percentage'))
        this.packageResponseFields = responseFields(
                fieldWithPath('package.status').description('status of the package'),
                fieldWithPath('package.questionnaireSyncStatus').description('status of the questionnaire-sync'),
                subsectionWithPath('package.applicants').description('Array of the applicants'),
                subsectionWithPath('package.representative').description('Attorney of the package'),
                fieldWithPath('package.representativeId').description('Attorney id of the package'),
                fieldWithPath('package.easyVisaId').description('EVID of the package'),
                fieldWithPath('package.id').description('id of the package'),
                subsectionWithPath('package.organization').description('organization of the package'),
                fieldWithPath('package.owed').description('Amount owed for the package'),
                fieldWithPath('package.welcomeEmailSentOn').description('Welcome email date sent out'),
                fieldWithPath('package.retainerAgreement').description('Details of retainer agreement for the package'),
                fieldWithPath('package.welcomeEmailId').description('Id of welcome email of package'),
                fieldWithPath('package.title').description('Title of package'),
                fieldWithPath('package.categories').description('Package Categories Abbreviation comma separated '),
                fieldWithPath('package.inviteApplicantEmailId').description('Id of invite-applicant email of package'),
                fieldWithPath('package.creationDate').description('Date when package was created'),
                subsectionWithPath('package.assignees').description('List of attorneys who were assigned this package'),
                fieldWithPath('package.lastActiveOn').description('Last activity of package applicants in the system'),
                fieldWithPath('package.questionnaireCompletedPercentage').description('Questionnaire Completion Percentage'),
                fieldWithPath('package.documentCompletedPercentage').description('Document upload percentage'),
                fieldWithPath('messages').description('Array of user messages'))
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)

    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }


    @Unroll
    def "test create-package #label"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(true, position)
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('create-package',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                subsectionWithPath('applicants').description('Details of the applicants'),
                                fieldWithPath('representativeId').description('Id of representative'),
                                fieldWithPath('organizationId').description('Id of organization')
                        ),
                        this.packageResponseFields))
                .body(testHelper.packagePetitionerBeneficiaryCreatePayload)
                .when()
                .port(this.serverPort)
                .post('/api/packages')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body('package.applicants[0].citizenshipStatus', equalTo('U_S_CITIZEN'))
                .body('package.applicants[0].profile.dateOfBirth', equalTo('12-03-1995'))
                .body('package.applicants[0].fee', nullValue())
                .body('package.applicants[1].profile.dateOfBirth', equalTo('12-03-2001'))
                .body('package.applicants[1].fee', equalTo(500))
                .body('package.applicants[2]', nullValue())
                .body('package.title', is('petitioner-last, petitioner-first + applicant-last, applicant-first'))

        cleanup:
        testHelper.clean()

        where:
        label      | position
        "PARTNER"  | EmployeePosition.PARTNER
        "ATTORNEY" | EmployeePosition.ATTORNEY
    }

    /**
     * This test simulates creating a package after the user is newly registered as a solo firm.
     */
    def "test create-package for Solo Org"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRepSolo()
                .logInPackageLegalRep()


        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.packagePetitionerBeneficiaryCreatePayload)
                .when()
                .port(this.serverPort)
                .post('/api/packages')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body('package.applicants[0].citizenshipStatus', equalTo('U_S_CITIZEN'))
                .body('package.applicants[0].profile.dateOfBirth', equalTo('12-03-1995'))
                .body('package.applicants[0].fee', nullValue())
                .body('package.applicants[1].profile.dateOfBirth', equalTo('12-03-2001'))
                .body('package.applicants[1].fee', equalTo(500))
                .body('package.applicants[2]', nullValue())
                .body('package.title', is('petitioner-last, petitioner-first + applicant-last, applicant-first'))

        cleanup:
        testHelper.clean()
    }

    def testCreateLeadPackageApplicantInBlocked() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 packageService : packageService,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryBlockedPackage()
                .buildUsersForPackageApplicants()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.
                        getPackagePetitionerBeneficiaryCreatePayload(testHelper.aPackage.petitioner.applicant.profile.email,
                                testHelper.aPackage.petitioner.applicant.id))
                .when()
                .port(this.serverPort)
                .post('/api/packages')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body('package.applicants[0].citizenshipStatus', equalTo('U_S_CITIZEN'))
                .body('package.applicants[0].fee', nullValue())
                .body('package.applicants[0].profile.id', is(testHelper.aPackage.petitioner.applicant.id as Integer))
                .body('package.applicants[0].inBlockedPackage', is(true))
                .body('package.applicants[0].inOpenPackage', is(false))
                .body('package.applicants[1].fee', equalTo(500))
                .body('package.applicants[2]', nullValue())
                .body('package.title', is('petitioner-last, petitioner-first + applicant-last, applicant-first'))
                .body('messages[0].type', is(ErrorMessageType.BLOCKED_OPEN_PACKAGES.name()))
                .body('messages[0].text', is('An applicant is in OPEN/BLOCKED package.'))

        cleanup:
        testHelper.deleteOtherAttorneyPackages().clean()
    }

    def testCreateLeadPackageApplicantInOpen() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.
                        getPackagePetitionerBeneficiaryCreatePayload(testHelper.aPackage.petitioner.applicant.profile.email,
                                testHelper.aPackage.petitioner.applicant.id))
                .when()
                .port(this.serverPort)
                .post('/api/packages')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body('package.applicants[0].citizenshipStatus', equalTo('U_S_CITIZEN'))
                .body('package.applicants[0].fee', nullValue())
                .body('package.applicants[0].profile.id', is(testHelper.aPackage.petitioner.applicant.id as Integer))
                .body('package.applicants[0].inBlockedPackage', is(false))
                .body('package.applicants[0].inOpenPackage', is(true))
                .body('package.applicants[1].fee', equalTo(500))
                .body('package.applicants[2]', nullValue())
                .body('package.title', is('petitioner-last, petitioner-first + applicant-last, applicant-first'))
                .body('messages[0].type', is(ErrorMessageType.BLOCKED_OPEN_PACKAGES.name()))
                .body('messages[0].text', is('An applicant is in OPEN/BLOCKED package.'))

        cleanup:
        testHelper.deleteOtherAttorneyPackages().clean()
    }

    def testEditOpenPackageApplicantInOtherOpen() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
        PackageTestBuilder openHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        openHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
        Petitioner petitioner = openHelper.aPackage.petitioner

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', openHelper.accessTokenPackageLegalRep)
                .body(openHelper.
                        getPackagePetitionerBeneficiaryEditPayload(testHelper.aPackage.petitioner.applicant.profile.email,
                                testHelper.aPackage.petitioner.applicant.id, openHelper.aPackage.id))
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', openHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('package.applicants[0].citizenshipStatus', equalTo('U_S_CITIZEN'))
                .body('package.applicants[0].fee', nullValue())
                .body('package.applicants[0].profile.id', is(testHelper.aPackage.petitioner.applicant.id as Integer))
                .body('package.applicants[1].fee', equalTo(500))
                .body('package.applicants[2]', nullValue())
                .body('package.title', is('petitioner-last, petitioner-first + applicant-last, applicant-first'))
                .body('messages[0].type', is(ErrorMessageType.BLOCKED_OPEN_PACKAGES.name()))
                .body('messages[0].text', is('An applicant is in OPEN/BLOCKED package.'))

        cleanup:
        testHelper.clean(false)
        openHelper.deleteOtherAttorneyPackages().clean()
    }

    def testEditOpenPackageApplicantInOtherBlocked() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryBlockedPackage()
                .buildUsersForPackageApplicants()
        PackageTestBuilder openHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        openHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', openHelper.accessTokenPackageLegalRep)
                .body(openHelper.
                        getPackagePetitionerBeneficiaryCreatePayload(testHelper.aPackage.petitioner.applicant.profile.email,
                                testHelper.aPackage.petitioner.applicant.id, openHelper.aPackage.id))
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', openHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.type[0]', is(ErrorMessageType.BLOCKED_OPEN_PACKAGES.name()))
                .body('errors.message[0]', is('An applicant is in OPEN/BLOCKED package.'))

        cleanup:
        testHelper.clean()
        openHelper.clean()
    }

    def testCreateNoPetitionerPackage() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('create-package-no-petitioner',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                subsectionWithPath('applicants').description('Details of the applicants'),
                                fieldWithPath('representativeId').description('Id of representative'),
                                fieldWithPath('organizationId').description('Id of organization')
                        ),
                        responseFields(
                                subsectionWithPath('package').description('Details of the package'),
                                subsectionWithPath('messages').description('Message to display for the user')
                        )))
                .body(testHelper.packageNoPetitionerCreatePayload)
                .when()
                .port(this.serverPort)
                .post('/api/packages')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body('package.applicants[0].profile.dateOfBirth', equalTo('12-03-2001'))
                .body('package.applicants[0].fee', equalTo(500))
                .body('package.applicants[1]', nullValue())
                .body('package.title', is('applicant-last, applicant-first'))

        cleanup:
        testHelper.clean()
    }

    def testCreatePetitionerAndTwoBeneficiariesPackage() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('create-package-no-petitioner',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                subsectionWithPath('applicants').description('Details of the applicants'),
                                fieldWithPath('representativeId').description('Id of representative'),
                                fieldWithPath('organizationId').description('Id of organization')
                        ),
                        responseFields(
                                subsectionWithPath('package').description('Details of the package'),
                                subsectionWithPath('messages').description('Message to display for the user')
                        )))
                .body(testHelper.packagePetitionerAndTwoBeneficiariesCreatePayload)
                .when()
                .port(this.serverPort)
                .post('/api/packages')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body('package.applicants[0].profile.dateOfBirth', equalTo('12-03-1995'))
                .body('package.applicants[0].profile.firstName', equalTo('petitioner-first'))
                .body('package.applicants[0].fee', is(nullValue()))
                .body('package.applicants[1].profile.dateOfBirth', equalTo('12-03-2001'))
                .body('package.applicants[1].fee', equalTo(500))
                .body('package.applicants[2].profile.dateOfBirth', equalTo('12-03-2001'))
                .body('package.applicants[2].fee', equalTo(500))
                .body('package.applicants[3]', nullValue())
                .body('package.title', is('petitioner-last, petitioner-first + applicant-last, applicant-first + derivative-applicant-last,' +
                        ' derivative-applicant-first'))

        cleanup:
        testHelper.clean()
    }

    def "test find packages by applicant and attorney"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('find-packages',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('email').description('Email id of applicant').optional(),
                                parameterWithName('easyVisaId').description('EasyVisaid of applicant').optional(),
                                parameterWithName('lastName').description('last name of applicant').optional(),
                                parameterWithName('mobileNumber').description('Mobile number of applicant').optional(),
                                parameterWithName('search').description('search query').optional(),
                                parameterWithName('status').description('Package status').optional(),
                                parameterWithName('closedDateStart').description('start Date range for when package was closed').optional(),
                                parameterWithName('closedDateEnd').description('end Date range for when package was closed').optional(),
                                parameterWithName('openedDateStart').description('start Date range for when package was opened').optional(),
                                parameterWithName('openedDateEnd').description('end Date range for when package was opened').optional(),
                                parameterWithName('lastAnsweredOnDateStart').description('start Date range for when package was last updated').optional(),
                                parameterWithName('lastAnsweredOnDateEnd').description('end Date range for when package was last updated').optional(),
                                parameterWithName('citizenshipStatus').description('Status of Petitioner').optional(),
                                parameterWithName('benefitCategory').description('Benefit category of any of the applicants').optional(),
                                parameterWithName('isOwed').description('Boolean to filter if package has money owed or not').optional(),
                                parameterWithName('states').description('List of states for any of the applicant').optional(),
                                parameterWithName('countries').description('List of countries for any of the applicant').optional(),
                                parameterWithName('representativeId').description('Id of Legal representative').optional(),
                                parameterWithName('organizationId').description('Id of Organization. Required for Attorney').optional(),
                                parameterWithName('sort').description('field to sort by').optional(),
                                parameterWithName('order').description('sort order (can be either asc or desc)').optional(),
                                parameterWithName('shrink').description('shrink parameter regulates how detailed is the response. true it will be light weight (limited data for UI to display). Otherwise all package data will be returned. Default is true.').optional()
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/packages/find?organizationId=$testHelper.organization.id&sort=status")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('[0].id', equalTo(testHelper.aPackage.id as int))
                .header('X-total-count', equalTo(1 as String))

        cleanup:
        testHelper.clean()
    }

    def "test get package by id"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-package',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the package')
                        ), this.responseFields))
                .when()
                .port(this.serverPort)
                .get('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("id", equalTo(testHelper.aPackage.id as int))

        cleanup:
        testHelper.clean()
    }

    void testEditLeadPackage() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('edit-package',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the package')
                        ),
                        requestFields(
                                subsectionWithPath('applicants').description('Details of the applicants'),
                                fieldWithPath('representativeId').description('Id of representative'),
                                fieldWithPath('owed').description('Amount owed for the package')
                        ),
                        responseFields(
                                subsectionWithPath('package').description('package data'),
                                subsectionWithPath('messages').description('list of messages'))))
                .body(testHelper.editNewBeneficiaryWithPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('package.id', equalTo(testHelper.aPackage.id as int))
                .body('package.applicants[0].profile.id', equalTo(testHelper.aPackage.petitioner.applicant.id as int))
                .body('package.applicants[1].profile.id', not(equalTo(testHelper.aPackage.beneficiaries[0].id as int)))
                .body('package.applicants[1].fee', equalTo(500))
                .body('package.owed', equalTo(500))
                .body('messages', nullValue())

        TestUtils.assertNoAccountTransactions(testHelper.packageLegalRepresentative.user)

        cleanup:
        testHelper.clean()
    }

    void testEditLeadNoPetitionerPackage() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerLeadPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editSameBeneficiaryWithNoPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('package.id', equalTo(testHelper.aPackage.id as int))
                .body('package.applicants[0].profile.id', equalTo(testHelper.aPackage.beneficiaries[0].id as int))
                .body('package.applicants[0].fee', equalTo(500))
                .body('package.owed', equalTo(500))
                .body('messages', nullValue())

        TestUtils.assertNoAccountTransactions(testHelper.packageLegalRepresentative.user)

        cleanup:
        testHelper.clean()
    }

    void testEditLeadPetitionerAndTwoBeneficiariesPackage() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndTwoBeneficiariesLeadPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editNewBeneficiaryWithSamePetitionerAndDerivativePayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('package.id', equalTo(testHelper.aPackage.id as int))
                .body('package.applicants[0].profile.id', equalTo(testHelper.aPackage.petitioner.applicant.id as int))
                .body('package.applicants[1].profile.id', not(equalTo(testHelper.aPackage.beneficiaries[0].id as int)))
                .body('package.applicants[1].fee', equalTo(500))
                .body('package.applicants[2].profile.id', equalTo(testHelper.aPackage.beneficiaries[1].id as int))
                .body('package.applicants[2].fee', equalTo(500))
                .body('package.owed', equalTo(500))
                .body('package.title', is('petitioner-last, petitioner-first + new-applicant-last, new-applicant-first + derivative-applicant-last, derivative-applicant-first'))
                .body('messages', nullValue())

        TestUtils.assertNoAccountTransactions(testHelper.packageLegalRepresentative.user)

        cleanup:
        testHelper.clean()
    }

    void testEditLeadPackageAddRegisteredApplicant() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerLeadPackage().logInPackageLegalRep()
        Applicant existingApplicant = testHelper.createRegisteredApplicant()
        Package.withNewTransaction {
            testHelper.aPackage.refresh().beneficiaries.first().id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.getEditRegisteredApplicantPayload(existingApplicant.profile.email))
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('package.id', equalTo(testHelper.aPackage.id as int))
                .body('package.applicants[0].profile.email', equalTo(existingApplicant.profile.email))

        TestUtils.assertNoAccountTransactions(testHelper.packageLegalRepresentative.user)

        cleanup:
        PackageOptInForImmigrationBenefitRequest.withNewTransaction {
            PackageOptInForImmigrationBenefitRequest.findAllByAPackage(testHelper.aPackage).each {
                Alert.findAllByProcessRequest(it)*.delete(failOnError: true)
                it.delete(failOnError: true)
            }
        }
        testHelper.clean()
    }

    def "test can edit package with beneficiaries without their email id"() {
        given:

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage(false)
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editBeneficiaryNoEmailWithPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('package.id', equalTo(testHelper.aPackage?.id as int))
                .body('package.applicants[1].profile.middleName', equalTo('new-applicant-middle'))
                .body('package.applicants[1].fee', equalTo(500))
                .body('package.owed', equalTo(500))
                .body('messages', nullValue())

        cleanup:
        testHelper.clean()
    }

    def "test send package welcome-email"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('send-package-welcome-email',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the package')
                        ), responseFields(fieldWithPath('message').description('Success message'))))
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/send-welcome-email', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('message', is(notNullValue()))

        cleanup:
        testHelper.clean()
    }

    def "test send package invite-applicant email"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .setInviteFlagToBeneficiary()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('send-package-applicant-invite-email',
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName('id').description('id of the package')),
                        responseFields(fieldWithPath('message').description('Success message'))))
                .body("""{"applicantId":${testHelper.aPackage.principalBeneficiary.id}}""")
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/send-applicant-invite', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('message', is(notNullValue()))

        cleanup:
        testHelper.clean()
    }

    def "test package delete-retainer agreement API"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .addPackageRetainer()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('delete-package-retainer-agreement',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the package')
                        ), responseFields(fieldWithPath('message').description('Success message'))))
                .when()
                .port(this.serverPort)
                .delete('/api/packages/{id}/retainer', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('message', is(notNullValue()))

        cleanup:
        testHelper.clean()
    }

    void testChangePackageStatusNotOptIn() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .logInPackageLegalRep()
                .refreshPetitioner()
        PackageTestBuilder noPetitionerHelper = PackageTestBuilder.init(testHelper)
        noPetitionerHelper.buildNoPetitionerLeadPackage(ImmigrationBenefitCategory.SIX01,
                testHelper.aPackage.petitioner.applicant.id)
        PackageTestBuilder petitionerAndBeneficiaryHelper = PackageTestBuilder.init(testHelper)
        petitionerAndBeneficiaryHelper.buildPetitionerAndBeneficiaryLeadPackage(true, ImmigrationBenefitCategory.IR1,
                testHelper.aPackage.principalBeneficiary.id)

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body('{"newStatus":"OPEN"}')
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/change-status', noPetitionerHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_LOCKED))
                .body('errors.type[0]', is(ErrorMessageType.MEMBERS_WITH_PENDING_OR_DENY_STATUS.name()))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body('{"newStatus":"OPEN"}')
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/change-status', petitionerAndBeneficiaryHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_LOCKED))
                .body('errors.type[0]', is(ErrorMessageType.MEMBERS_WITH_PENDING_OR_DENY_STATUS.name()))

        cleanup:
        testHelper.deletePackageOnly(false, false)
        noPetitionerHelper.deletePackageOnly()
        petitionerAndBeneficiaryHelper.clean()
    }

    void testChangePackageStatusApplicantInBlockedPackage() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryBlockedPackage()
                .buildUsersForPackageApplicants()
                .logInPackageLegalRep()
                .refreshPetitioner()
        PackageTestBuilder noPetitionerHelper = PackageTestBuilder.init(testHelper)
        noPetitionerHelper.buildNoPetitionerLeadPackage(ImmigrationBenefitCategory.SIX01,
                testHelper.aPackage.petitioner.applicant.id)
        PackageTestBuilder petitionerAndBeneficiaryHelper = PackageTestBuilder.init(testHelper)
        petitionerAndBeneficiaryHelper.buildPetitionerAndBeneficiaryLeadPackage(true, ImmigrationBenefitCategory.IR1,
                testHelper.aPackage.principalBeneficiary.id)

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body('{"newStatus":"OPEN"}')
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/change-status', noPetitionerHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_LOCKED))
                .body('errors.type[0]', is(ErrorMessageType.MEMBERS_OF_BLOCKED_PACKAGE.name()))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body('{"newStatus":"OPEN"}')
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/change-status', petitionerAndBeneficiaryHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_LOCKED))
                .body('errors.type[0]', is(ErrorMessageType.MEMBERS_OF_BLOCKED_PACKAGE.name()))

        cleanup:
        testHelper.deletePackageOnly(false, false)
        noPetitionerHelper.deletePackageOnly()
        petitionerAndBeneficiaryHelper.clean()
    }

    void testChangePackageStatusNotActiveAttorney() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildNoPackageLegalRep()
                .makePackageRepresentativeInactiveInOrg()
                .logInNoPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenNoPackageLegalRep)
                .body('{"newStatus":"OPEN"}')
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/change-status', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.type[0]', nullValue())
                .body('errors.message[0]', equalTo('You must first assign this package to an active legal representative before opening it'))

        cleanup:
        testHelper.clean()
    }

    void testChangePackageStatusTrainee() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildTrainee()
                .logInTrainee()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenTrainee)
                .body('{"newStatus":"OPEN"}')
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/change-status', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))
                .body('errors.message[0]',
                        equalTo('Trainees are not allowed to change the status of a package.'))

        List<AccountTransaction> transactions
        Package storedPackage
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedPackage = Package.get(testHelper.aPackage.id)
        }

        assert !transactions
        assert PackageStatus.LEAD == storedPackage.status

        cleanup:
        testHelper.clean()
    }

    def "test can delete old leads"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        PackageTestBuilder testHelperNoPetitioner = PackageTestBuilder.init([serverPort     : serverPort,
                                                                             attorneyService: attorneyService,
                                                                             packageService : packageService,
                                                                             profileService : profileService])
        PackageTestBuilder testHelperPetitionerTwoBeneficiaries = PackageTestBuilder.init([serverPort     : serverPort,
                                                                                           attorneyService: attorneyService,
                                                                                           packageService : packageService,
                                                                                           profileService : profileService])
        testHelperNoPetitioner.buildNoPetitionerLeadPackage()

        testHelperPetitionerTwoBeneficiaries.organization = testHelperNoPetitioner.organization
        testHelperPetitionerTwoBeneficiaries.packageLegalRepresentative =
                testHelperNoPetitioner.packageLegalRepresentative
        testHelperPetitionerTwoBeneficiaries.buildPetitionerAndTwoBeneficiariesLeadPackage()

        testHelper.organization = testHelperNoPetitioner.organization
        testHelper.packageLegalRepresentative = testHelperNoPetitioner.packageLegalRepresentative
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()

        List<Long> addresses = []
        Package.withNewTransaction {
            addresses.add(testHelper.aPackage.refresh().petitioner.applicant.profile.address.id)
            addresses.add(testHelperPetitionerTwoBeneficiaries.aPackage.refresh().petitioner.applicant.profile.address.id)
        }

        Date startDate = new Date() - 10
        Date endDate = new Date()
        String format = "MM-dd-yyyy"

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('delete-package-leads',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('startDate').description('start date from which leads should be deleted'),
                                parameterWithName('endDate').description('end date from which leads should be deleted'),
                                parameterWithName('organizationId').description('Id of organization')),
                        responseFields(
                                fieldWithPath('deletedPackageIds').description('List of packageIds deleted'))))
                .when()
                .port(this.serverPort)
                .delete("/api/packages?startDate=${startDate.format(format)}&endDate=${endDate.format(format)}" +
                        "&organizationId=${testHelper.organization.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("deletedPackageIds", containsInAnyOrder(testHelperNoPetitioner.aPackage.id as int,
                        testHelperPetitionerTwoBeneficiaries.aPackage.id as int,
                        testHelper.aPackage.id as int))

        cleanup:
        Address.withNewTransaction {
            Address.deleteAll(Address.findAllByIdInList(addresses))
        }
        testHelper.deletePackageLegalRep()
        testHelper.deleteOrganization()
    }

    def "test package transfer in same organization"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildNoPackageLegalRep()
                .logInPackageLegalRep()
        LegalRepresentative noPackageLegalRep = testHelper.legalRepresentativeNoPackage

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('package-transfer-same-organization',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('packageIds').description('list of package ids'),
                                fieldWithPath('representativeId').description('RepresentativeId of new assignee'),
                                fieldWithPath('organizationId').description('Id of organization where packages will be transferred')
                        ),
                        responseFields(
                                fieldWithPath('representativeId').description('New assignee id'),
                                fieldWithPath('firstName').description('New assignee first name'),
                                fieldWithPath('lastName').description('New assignee last name'),
                                fieldWithPath('middleName').description('New assignee middle name')
                        )))
                .body("""{"packageIds":[$testHelper.aPackage.id],
"representativeId":"${noPackageLegalRep.id}",
"organizationId":"${testHelper.organization.id}"
}
""")
                .when()
                .port(this.serverPort)
                .post('/api/packages/transfer')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('representativeId', equalTo(noPackageLegalRep.id as int))
                .body('firstName', equalTo(noPackageLegalRep.profile.firstName))
                .body('lastName', equalTo(noPackageLegalRep.profile.lastName))
                .body('middleName', equalTo(noPackageLegalRep.profile.middleName))

        PackageTransferRequest processRequest
        ProcessRequest.withNewTransaction {
            processRequest = PackageTransferRequest.findByRepresentative(noPackageLegalRep.refresh())
        }

        assert ProcessRequestState.ACCEPTED == processRequest.state

        cleanup:
        testHelper.deleteNoPackageLegalRepProcessRequests()
        testHelper.deletePackageLegalRep()
        testHelper.deletePackage()
    }

    //Doesnt work - status does not change to TRANSFERRED - will be tested manually
    @Ignore
    def "test delete package transfer"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(true)
                .buildPetitionerAndBeneficiaryLeadPackage()
                .buildNoPackageLegalRep()
                .logInPackageLegalRep()
        LegalRepresentative noPackageLegalRep = testHelper.legalRepresentativeNoPackage

        // transfer package
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body("""{"packageIds":[$testHelper.aPackage.id],
                                "representativeId":"${noPackageLegalRep.id}",
                                "organizationId":"${testHelper.organization.id}"
                                }
                            """)
                .when()
                .port(this.serverPort)
                .post('/api/packages/transfer')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        /*
        PackageTransferRequest processRequest
        ProcessRequest.withNewTransaction {
            processRequest = PackageTransferRequest.findByRepresentative(noPackageLegalRep.refresh())
        }
        */

        expect:

        Map requestBody = [
                "packageIds"    : [testHelper.aPackage.id],
                "organizationId": testHelper.organization.id
        ]
        println("requestBody = ${requestBody}")
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('delete-transferred-packages',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('packageIds').description('list of package ids'),
                                parameterWithName('organizationId').description('Id of organization where packages will be transferred')
                        )))
                .when()
                .port(this.serverPort)
                .queryParams(requestBody)
                .delete('/api/packages/transferred')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))


        cleanup:
        testHelper.deleteNoPackageLegalRepProcessRequests()
        testHelper.deletePackageLegalRep()
        testHelper.deletePackage()
    }


    void testSendBill() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('send-bill',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the package')
                        ),
                        requestFields(
                                fieldWithPath('email').description('Email content'),
                                fieldWithPath('charges[].description').description('A charge description'),
                                fieldWithPath('charges[].each').description('Cost'),
                                fieldWithPath('charges[].quantity').description('Count')
                        )))
                .body("""{"email":"Hello, |PETITIONER_NAME|,\\n\\nThese are the applicants in the package for which additional legal/governmental/service fees are being generated:\\n\\n|APPLICANT_LIST|\\n\\nHere are the additional legal/governmental/service fees we discussed for the below listed services:\\n\\n|FEE_TABLE|\\n\\nPlease make arrangements to pay these fees at your earliest possible convenience!\\n\\nThanks,\\n|LEGAL_REP_FULL_NAME|",
\t"charges" : [{"description" : "Translation fee", "each" : 100, "quantity" : 1},{"description" : "Photocopies", "each" : 3.45, "quantity" : 11}]}
                """)
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/send-bill', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        TestUtils.delayCurrentThread(3000)
        Alert alert
        Alert.withNewTransaction {
            alert = Alert.findByRecipient(testHelper.aPackage.attorney.user)
        }

        assert alert != null
        assert EasyVisaSystemMessageType.PACKAGE_ADDITIONAL_FEE == alert.messageType
        assert testHelper.aPackage.attorney.profile.name == alert.source

        cleanup:
        testHelper.clean()
    }

    def "test registerRedir"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()

        RegistrationCode registrationCode
        RegistrationCode.withNewTransaction {
            registrationCode = new RegistrationCode(easyVisaId: testHelper.aPackage.petitioner.profile.easyVisaId).save(failOnError: true)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('register-redir',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('token').description('Token for registration')
                        )))
                .when()
                .redirects().follow(false)
                .port(this.serverPort)
                .get('/api/public/register-redir/{token}', registrationCode.token)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_MOVED_TEMPORARILY))
                .headers("Location", frontEndAppURL + loginUrl)

        cleanup:
        testHelper.clean()
    }

    def testUpdateLastActiveOn() {
        given:
        Date curDate = new Date()
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
        PackageTestBuilder testHelperExtra = PackageTestBuilder.init([serverPort     : serverPort,
                                                                      attorneyService: attorneyService,
                                                                      packageService : packageService,
                                                                      profileService : profileService])
        testHelperExtra.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()

        expect:
        testHelper.logInPackagePetitioner()
        testHelperExtra.logInPackageDirectBeneficiary()

        Package.withNewTransaction {
            testHelper.aPackage.refresh()
            testHelperExtra.aPackage.refresh()
        }

        assert testHelper.aPackage.lastActiveOn
        assert curDate < testHelper.aPackage.lastActiveOn
        assert testHelperExtra.aPackage.lastActiveOn
        assert curDate < testHelperExtra.aPackage.lastActiveOn

        cleanup:
        testHelper.clean()
        testHelperExtra.clean()
    }

    @Unroll
    def "test Valid update Owed #label"() {
        given:

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackageLegalRep()


        expect:
        String bodyPayload = '{"owed":' + amountOwed + '}'
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(bodyPayload)
                .when()
                .port(this.serverPort)
                .patch('/api/packages/{id}/owed-amount', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('package.id', equalTo(testHelper.aPackage?.id as int))
                .body('package.owed', equalTo(expectedOwed))
                .body('messages', nullValue())


        cleanup:
        testHelper.clean()

        where:
        amountOwed | expectedOwed | label
        1001       | 1001         | "Positive Value"
        null       | 0            | "Null Value set to 0"

    }

    // todo unignore once tests are fixed
    @Ignore
    @Unroll
    def "test Invalid update Owed #label"() {
        given:

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackageLegalRep()


        expect:
        String bodyPayload = '{"owed":' + amountOwed + '}'
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(bodyPayload)
                .when()
                .port(this.serverPort)
                .patch('/api/packages/{id}/owed-amount', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))

        cleanup:
        testHelper.clean()

        where:
        amountOwed | label
        -100       | "- Negative"

    }
}
