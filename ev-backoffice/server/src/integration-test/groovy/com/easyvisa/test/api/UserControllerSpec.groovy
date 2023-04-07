package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.NotificationType
import com.easyvisa.enums.PracticeArea
import com.easyvisa.enums.TransactionSource
import com.easyvisa.utils.NumberUtils
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.JsonConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.path.json.config.JsonPathConfig
import io.restassured.specification.RequestSpecification
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.hibernate.SessionFactory
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import grails.util.Holders

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
class UserControllerSpec extends TestMockUtils {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation('build/generated-snippets')

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private ProfileService profileService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private PackageService packageService
    @Autowired
    private AccountService accountService
    @Autowired
    private OrganizationService organizationService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    private TaxService taxServiceMock = Mock(TaxService)
    @Autowired
    private SessionFactory sessionFactory
    protected RequestSpecification spec
    protected ResponseFieldsSnippet responseFields
    protected ResponseFieldsSnippet loginResponseFields

    void setup() {
        this.spec = new RequestSpecBuilder().setConfig(RestAssuredConfig.config().jsonConfig(JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.responseFields = responseFields(
                fieldWithPath('lastName').description('Last name of user'),
                fieldWithPath('firstName').description('first name of user'),
                fieldWithPath('middleName').description('middle name of user'),
                fieldWithPath('easyVisaId').description('EasyVisaId of user'),
                fieldWithPath('email').description('email of User'),
                fieldWithPath('profilePhoto').description('URL for profile photo of user.'))

        this.loginResponseFields = responseFields(
                fieldWithPath('id').description('id of user'),
                fieldWithPath('roles').description('roles of user'),
                fieldWithPath('access_token').description('access_token of user'))
        updateToMock(attorneyService.accountService, paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(attorneyService.accountService, paymentService, taxService)
    }

    def "test /api/public/validate-username with new username"() {
        expect:
        given(this.spec)
                .filter(document('check-username',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('username').description('Username to be validated'),
                        )))
                .body('{\n' +
                        '"username": "testusername"\n' +
                        '}')
                .when()
                .port(this.serverPort)
                .post('/api/public/validate-username')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("valid", equalTo(true))
    }

    def "test /api/public/validate-username with existing username"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()

        expect:
        given(this.spec)
                .filter(document('check-username-invalid',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('username').description('Username to be validated'),
                        )))
                .body("""{
                        "username": "${testHelper.packageLegalRepresentative.user.username}"
                        }""")
                .when()
                .port(this.serverPort)
                .post('/api/public/validate-username')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("valid", equalTo(false))

        cleanup:
        testHelper.clean()
    }

    def "test GET /api/users/me gives the current logged in user"() {
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
                .filter(document('get-logged-in-user',
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath('id').description('id of the user'),
                                fieldWithPath('roles').description('Roles alloted to the user'),
                                subsectionWithPath('profile').description('Profile of the user'),
                                fieldWithPath('accountLocked').description('is the account locked?'),
                                fieldWithPath('enabled').description('is the account enabled?'),
                                fieldWithPath('lastLogin').description('date of last successful login'),
                                fieldWithPath('profile').description('Details of profile of user.'),
                                fieldWithPath('paid').description('Paid flag.'),
                                fieldWithPath('activeMembership').description('Active membership flag.')
                        )))
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    def "test POST /api/public/validate-email tells if the email is valid"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()

        expect:
        given(this.spec)
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('validate-email',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('email').description('Email to be validated'),
                        )))
                .body('{\n' +
                        '"email": "email@host.com"\n' +
                        '}')
                .when()
                .port(this.serverPort)
                .post('/api/public/validate-email')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("valid", equalTo(true))

        given(this.spec)
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('validate-email',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('email').description('Email to be validated'),
                        )))
                .body('{\n' +
                        '"email": "email@HOst.com"\n' +
                        '}')
                .when()
                .port(this.serverPort)
                .post('/api/public/validate-email')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("valid", equalTo(false))

        given(this.spec)
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("""{
                        "email": "${testHelper.packageLegalRepresentative.profile.email}"
                        }""")
                .when()
                .port(this.serverPort)
                .post('/api/public/validate-email')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("valid", equalTo(false))

        cleanup:
        testHelper.clean()
    }

    def "test /forgot-username with existing email"() {
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
                .filter(document('forgot-username',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('email').description('Email of the user')),
                        responseFields(
                                fieldWithPath('message').description('message'))))
                .body("""{ "email": "${testHelper.packageLegalRepresentative.profile.email}" }""")
                .when()
                .port(this.serverPort)
                .post('/api/public/forgot-username')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    def "test /forgot-username with non-existing email"() {
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('forgot-username-error',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('email').description('Email of the user')
                        )))
                .body('{ "email": "noneexistingemail@easyvisa.com" }')
                .when()
                .port(this.serverPort)
                .post('/api/public/forgot-username')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_NOT_FOUND))
    }

    def "test /show-username with correct token"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()

        expect:
        RegistrationCode registrationCode
        RegistrationCode.withNewTransaction {
            registrationCode = new RegistrationCode(username: testHelper.packageLegalRepresentative.user.username,
                    easyVisaId: testHelper.packageLegalRepresentative.profile.easyVisaId, dateCreated: new Date())
                    .save(failOnError: true)
        }
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('show-username',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('token').description('Reset password token')),
                        responseFields(
                                fieldWithPath('message').description('message'),
                                fieldWithPath('username').description('username for the user')
                        )))
                .body('{ "token": "' + registrationCode.token + '"}')
                .when()
                .port(this.serverPort)
                .post('/api/public/show-username')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('username', equalTo(testHelper.packageLegalRepresentative.user.username))

        cleanup:
        testHelper.clean()
    }

    def "test /show-username with incorrect token"() {
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('show-username-error',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('token').description('Reset password token')),
                        responseFields(
                                fieldWithPath('errors').description('list of errors'),
                                fieldWithPath('errors[].code').description('error code'),
                                fieldWithPath('errors[].message').description('error message'),
                                fieldWithPath('errors[].type').description('error type')
                        )))
                .body('{ "token":  "this-is-bad-token"}')
                .when()
                .port(this.serverPort)
                .post('/api/public/show-username')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_NOT_FOUND))
    }

    def "can get profile fields by registration token"() {
        given:
        RegistrationCode registrationCode
        ApplicantProfileCommand applicantProfileCommand = new ApplicantProfileCommand(firstName: 'applicant-first', lastName: 'applicant-last', email: 'applicant-email@easyvisa.com')
        ApplicantCommand applicantCommand = new ApplicantCommand(profile: applicantProfileCommand, applicantType: 'BENEFICIARY', benefitCategory: ImmigrationBenefitCategory.IR1, fee: 100, inviteApplicant: false)
        Applicant applicant

        RegistrationCode.withNewTransaction {
            applicant = profileService.createApplicant(applicantCommand, null)
            applicant.save(failOnError: true)
            registrationCode = new RegistrationCode(easyVisaId: applicant.profile.easyVisaId).save(failOnError: true)
        }
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('validate-registration-token',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('token').description('Registration token')),
                        this.responseFields))
                .body('{ "token": "' + registrationCode.token + '"}')
                .when()
                .port(this.serverPort)
                .post('/api/public/validate-token')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("easyVisaId", equalTo(applicant.profile.easyVisaId))

        cleanup:
        if (applicant && registrationCode) {
            RegistrationCode.withNewTransaction {
                registrationCode.delete(failOnError: true)
                TestUtils.deleteApplicant(applicant)
            }
        }
    }

    def "can create user by registration token"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()

        RegistrationCode regCode
        RegistrationCode.withNewTransaction {
            regCode = new RegistrationCode(easyVisaId: testHelper.aPackage.principalBeneficiary.profile.easyVisaId)
                    .save(failOnError: true)
        }
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('register-non-legal-rep-user',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('token').description('Registration token'),
                                fieldWithPath('lastName').description('Registration token'),
                                fieldWithPath('middleName').description('Registration token'),
                                fieldWithPath('firstName').description('Registration token'),
                                fieldWithPath('username').description('Registration token'),
                                fieldWithPath('password').description('Registration token')),
                        this.loginResponseFields))
                .body(
                        """
{"token": "${regCode.token}",
"firstName":"FirstName",
"lastName":"LastName",
"middleName":"MiddleName",
"username":"applicanttest",
"password":"applicantPasswordTest"
}
""")
                .when()
                .port(this.serverPort)
                .post('/api/public/register-user')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', is(notNullValue()))
                .body('access_token', is(notNullValue()))

        Alert alert
        Alert.withNewTransaction {
            alert = Alert.findByRecipient(testHelper.aPackage.refresh().attorney.user)
        }

        assert alert != null
        assert EasyVisaSystemMessageType.PACKAGE_APPLICANT_REGISTERED == alert.messageType

        cleanup:
        RegistrationCode.withNewTransaction {
            regCode.delete(failOnError: true)
        }
        testHelper.clean()
    }

    def "test GET /api/profile gives the profile of current user"() {
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
                .filter(document('get-user-profile',
                        preprocessResponse(prettyPrint())))
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/profile')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('easyVisaId', equalTo(testHelper.packageLegalRepresentative.profile.easyVisaId))
        cleanup:
        testHelper.clean()
    }

    def "test can update profile of current user with PUT /api/profile"() {
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
                .filter(document('edit-user-profile',
                        preprocessResponse(prettyPrint())))
                .body('''
            {"languages":["ENGLISH","CZECH","GERMAN"],
            "summary":"updated summary"}''')
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .put('/api/profile')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("easyVisaId", equalTo(testHelper.packageLegalRepresentative.profile.easyVisaId))
                .body("languages", equalTo(['ENGLISH', 'CZECH', 'GERMAN']))
                .body("summary", equalTo('updated summary'))
        cleanup:
        testHelper.clean()
    }

    def "test can update profile of current user who is an employee"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildTrainee()
                .logInTrainee()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('edit-current-employee-profile',
                        preprocessResponse(prettyPrint())))
                .body('''
            {"languages":["ENGLISH","CZECH","GERMAN"],
            "mobilePhone":"310-555-5678",
            "faxNumber":"310-555-1234",
            "officePhone":"(7 840)310-555-5555555"}''')
                .header('Authorization', testHelper.accessTokenTrainee)
                .when()
                .port(this.serverPort)
                .put('/api/profile')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("easyVisaId", equalTo(testHelper.trainee.profile.easyVisaId))
                .body("officePhone", equalTo('(7 840)310-555-5555555'))
                .body("faxNumber", equalTo('310-555-1234'))
                .body("mobilePhone", equalTo('310-555-5678'))
                .body("languages", equalTo(['ENGLISH', 'CZECH', 'GERMAN']))

        cleanup:
        testHelper.deleteOrganization()
                .deleteTrainee()
    }

    def testGetEmailPreference() {
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
                .filter(document('get-email-preference',
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath('taskQueue').description('Task Queue preferences'),
                                fieldWithPath('taskQueue[].id').description('Task Queue id'),
                                fieldWithPath('taskQueue[].type').description('Task Queue type'),
                                fieldWithPath('taskQueue[].preference').description('Task Queue preferences'),
                                fieldWithPath('clientProgress').description('Client progress preferences. It has the same array structure as Task Queue'),
                                fieldWithPath('clientProgress[].id').description('Id of the email preference'),
                                fieldWithPath('clientProgress[].type').description('Notification type. Enum value'),
                                fieldWithPath('clientProgress[].preference').description('Preference. true - enabled, otherwise false'))))
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/users/{id}/notifications', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('taskQueue.size()', equalTo(3))
                .body('taskQueue[0].id', nullValue())
                .body('taskQueue[0].type', anyOf(equalTo(NotificationType.APPLICANT_DOCUMENT.name()),
                        equalTo(NotificationType.ALERT.name()),
                        equalTo(NotificationType.WARNING.name())))
                .body('taskQueue[0].preference', equalTo(Boolean.FALSE))
                .body('clientProgress.size()', equalTo(3))
                .body('clientProgress[0].id', notNullValue())
                .body('clientProgress[0].type', anyOf(equalTo(NotificationType.APPLICANT_REGISTRATION.name()),
                        equalTo(NotificationType.QUESTIONNAIRE_COMPLETE.name()),
                        equalTo(NotificationType.DOCUMENTATION_COMPLETE.name())))
                .body('clientProgress[0].preference', equalTo(Boolean.TRUE))

        cleanup:
        testHelper.clean()
    }

    def testUpdateEmailPreference() {
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
                .filter(document('update-email-preference',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('activeOrganizationId').description('Active Organization Id'),
                                fieldWithPath('taskQueue').description('Task Queue preferences'),
                                fieldWithPath('clientProgress').description('Client progress preferences. It has the same array structure as Task Queue'),
                                fieldWithPath('clientProgress[].type').description('Notification type. Enum value'),
                                fieldWithPath('clientProgress[].preference').description('Preference. true - enabled, otherwise false')),
                        responseFields(
                                fieldWithPath('taskQueue').description('Task Queue preferences'),
                                fieldWithPath('taskQueue[].id').description('Task Queue id'),
                                fieldWithPath('taskQueue[].type').description('Task Queue type'),
                                fieldWithPath('taskQueue[].preference').description('Task Queue preferences'),
                                fieldWithPath('clientProgress').description('Client progress preferences. It has the same array structure as Task Queue'),
                                fieldWithPath('clientProgress[].id').description('Id of the email preference'),
                                fieldWithPath('clientProgress[].type').description('Notification type. Enum value'),
                                fieldWithPath('clientProgress[].preference').description('Preference. true - enabled, otherwise false'))))
                .body("""{
"activeOrganizationId":${testHelper.organization.id},
  "taskQueue": [],
  "clientProgress": [
    {
        "type": "DOCUMENTATION_COMPLETE",
        "preference": false
    },
    {
        "type": "APPLICANT_REGISTRATION",
        "preference": false
    },
    {
        "type": "QUESTIONNAIRE_COMPLETE",
        "preference": false
    }
]
}""")
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .put('/api/users/{id}/notifications', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('taskQueue.size()', equalTo(3))
                .body('taskQueue[0].id', nullValue())
                .body('taskQueue[0].type', anyOf(equalTo(NotificationType.APPLICANT_DOCUMENT.name()),
                        equalTo(NotificationType.ALERT.name()),
                        equalTo(NotificationType.WARNING.name())))
                .body('taskQueue[0].preference', equalTo(Boolean.FALSE))
                .body('clientProgress.size()', equalTo(3))
                .body('clientProgress[0].id', notNullValue())
                .body('clientProgress[0].type', anyOf(equalTo(NotificationType.APPLICANT_REGISTRATION.name()),
                        equalTo(NotificationType.QUESTIONNAIRE_COMPLETE.name()),
                        equalTo(NotificationType.DOCUMENTATION_COMPLETE.name())))
                .body('clientProgress[0].preference', equalTo(Boolean.FALSE))

        cleanup:
        testHelper.clean()
    }

    def testUpdateEmailPreferenceTrainee() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildTrainee()
                .logInTrainee()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("""{
"activeOrganizationId":${testHelper.organization.id},
  "taskQueue": [],
  "clientProgress": [
    {
        "type": "DOCUMENTATION_COMPLETE",
        "preference": false
    },
    {
        "type": "APPLICANT_REGISTRATION",
        "preference": false
    },
    {
        "type": "QUESTIONNAIRE_COMPLETE",
        "preference": false
    }
]
}""")
                .header('Authorization', testHelper.accessTokenTrainee)
                .when()
                .port(this.serverPort)
                .put('/api/users/{id}/notifications', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        testHelper.clean()
    }

    def testChangeUserPassword() {
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
                .filter(document('edit-user-password',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('oldPassword').description('Current user password.'),
                                fieldWithPath('newPassword').description('New user password')),
                        responseFields(
                                fieldWithPath('access_token').description('Updated token'))))
                .body("""{
                    "oldPassword": "${PackageTestBuilder.ATTORNEY_PASSWORD}",
                    "newPassword": "packageAttorneyPassword123"
                    }""")
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .put('/api/users/change-password')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('access_token', notNullValue())

        Alert alert
        Alert.withNewTransaction {
            alert = Alert.findByRecipientAndMessageType(testHelper.packageLegalRepresentative.user,
                    EasyVisaSystemMessageType.USER_PASSWORD_CHANGED)
        }

        assert alert != null

        cleanup:
        testHelper.clean()
    }

    def testChangeUserPasswordFailed() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        //same password
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("""{
                    "oldPassword": "${PackageTestBuilder.ATTORNEY_PASSWORD}",
                    "newPassword": "${PackageTestBuilder.ATTORNEY_PASSWORD}"
                    }""")
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .put('/api/users/change-password')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))

        //not strong password
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("""{
                    "oldPassword": "${PackageTestBuilder.ATTORNEY_PASSWORD}",
                    "newPassword": "123"
                    }""")
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .put('/api/users/change-password')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))

        Alert alert
        Alert.withNewTransaction {
            alert = Alert.findByRecipientAndMessageType(testHelper.packageLegalRepresentative.user,
                    EasyVisaSystemMessageType.USER_PASSWORD_CHANGED)
            true
        }

        assert alert == null

        cleanup:
        testHelper.clean()
    }

    void testUpdateAttorneyProfileEmail() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()

        String newEmail = 'newpetitioneremail@easyvia.com'
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('edit-profile-email',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('email').description('new email')),
                        responseFields(
                                fieldWithPath('email').description('new email'))))
                .body("{\"email\": \"${newEmail}\"}")
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .put('/api/profile/email')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('email', equalTo(newEmail))

        Profile profile
        Profile.withNewTransaction {
            profile = Profile.get(testHelper.packageLegalRepresentative.profile.id)
        }
        assert newEmail == profile.email

        cleanup:
        testHelper.clean()
    }

    void testUpdateEmployeeProfileEmail() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .buildTrainee()
                .logInPackageLegalRep()
                .logInTrainee()
                .logInPackagePetitioner()

        String newEmail = 'newemployeeemail@easyvia.com'
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("{\"email\": \"${newEmail}\"}")
                .header('Authorization', testHelper.accessTokenTrainee)
                .when()
                .port(this.serverPort)
                .put('/api/profile/email')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('email', equalTo(newEmail))

        Profile profile
        Profile.withNewTransaction {
            profile = Profile.get(testHelper.trainee.profile.id)
        }
        assert newEmail == profile.email

        cleanup:
        testHelper.clean()
    }

    void testUpdateApplicantProfileEmail() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()

        String newEmail = 'newclientemail@easyvia.com'
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("{\"email\": \"${newEmail}\"}")
                .header('Authorization', testHelper.accessTokenPetitioner)
                .when()
                .port(this.serverPort)
                .put('/api/profile/email')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('email', equalTo(newEmail))

        Profile profile
        Profile.withNewTransaction {
            profile = Profile.get(testHelper.aPackage.petitioner.profile.id)
        }
        assert newEmail == profile.email

        cleanup:
        testHelper.clean()
    }

    def getUserIdByLegalRepresentativeId() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPackageLegalRepEvRoles()
                .logInPackageLegalRep()

        Organization.withNewTransaction {
            testHelper.organization.refresh().easyVisaId = Holders.config.easyvisa.blessedOrganizationEVId
            testHelper.organization.save(failOnError: true)
        }

        expect:


        String responseString = given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-user-id-by-legal-representative-id',
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName('id').description('id of the legal representative')),
                        responseFields(
                                fieldWithPath('id').description('id of the user'))))
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/users/ev-id/{id}/id', testHelper.packageLegalRepresentative.profile.easyVisaId)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(testHelper.packageLegalRepresentative.user.id as Integer))


        cleanup:
        testHelper.clean()
    }

    def getUserIdByLegalRepresentativeIdNotFound() {
        given:
        RestResponse resp
        Long userId
        LegalRepresentative attorney
        Package.withNewTransaction {
            User user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            userId = attorney.user.id
            user.accountLocked = false
            user.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-user-id-by-legal-representative-id-not-found',
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName('id').description('id of the legal representative')),
                        responseFields(
                                fieldWithPath('errors').description('list of errors'),
                                fieldWithPath('errors[].code').description('error code'),
                                fieldWithPath('errors[].message').description('error message'),
                                fieldWithPath('errors[].type').description('error type'))))
                .header('Authorization', "Bearer " + resp.json.access_token)
                .when()
                .port(this.serverPort)
                .get('/api/users/ev-id/{id}/id', -1)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_NOT_FOUND))
                .body('errors.message[0]', equalTo('Representative not found with id'))

        cleanup:
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    def getPaymentMethod() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Package.withNewTransaction {
            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword',
                    fmCustomerId: '9a601cc7-5343-48f2-a607-b888bbe9cea8')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)
            new PaymentMethod(user: user, cardHolder: 'John White', cardExpiration: '072555',
                    cardLastFour: '1111', cardType: 'visa', address1: '1 Main street', address2: 'box 147',
                    addressCountry: 'USA', addressZip: '98568', addressCity: 'Las Vegas', addressState: 'NV',
                    fmPaymentMethodId: 'payment-id')
                    .save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-payment-method',
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName('id').description('id of the legal representative')),
                        responseFields(
                                fieldWithPath('customerId').description('Fattmerchant customer id'),
                                fieldWithPath('cardHolder').description('card holder name'),
                                fieldWithPath('cardLastFour').description('card last four digits'),
                                fieldWithPath('cardType').description('card type (visa, mastercard)'),
                                fieldWithPath('cardExpiration').description('card expiration'),
                                fieldWithPath('address1').description('billing address line 1'),
                                fieldWithPath('address2').description('billing address line 2'),
                                fieldWithPath('addressCity').description('billing address city'),
                                fieldWithPath('addressState').description('billing address state'),
                                fieldWithPath('addressCountry').description('billing address country'),
                                fieldWithPath('addressZip').description('billing address zip'))))
                .header('Authorization', "Bearer " + resp.json.access_token)
                .when()
                .port(this.serverPort)
                .get('/api/users/{id}/payment-method', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('customerId', equalTo('9a601cc7-5343-48f2-a607-b888bbe9cea8'))
                .body('cardHolder', equalTo('John White'))
                .body('cardLastFour', equalTo('1111'))
                .body('cardType', equalTo('Visa'))
                .body('cardExpiration', equalTo('07/2555'))
                .body('address1', equalTo('1 Main street'))
                .body('address2', equalTo('box 147'))
                .body('addressCity', equalTo('Las Vegas'))
                .body('addressState', equalTo('NV'))
                .body('addressCountry', equalTo('USA'))
                .body('addressZip', equalTo('98568'))
        cleanup:
        LegalRepresentative.withNewTransaction {
            PaymentMethod.findByUser(user).delete(failOnError: true)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    def getPaymentMethodUserNotFound() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Package.withNewTransaction {
            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-payment-method-user-not-found',
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName('id').description('id of the legal representative'))))
                .header('Authorization', "Bearer " + resp.json.access_token)
                .when()
                .port(this.serverPort)
                .get('/api/users/{id}/payment-method', 0)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_NOT_FOUND))
                .body('errors[0].message', equalTo('User not found with id'))
        cleanup:
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    def getNoPaymentMethod() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Package.withNewTransaction {
            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-no-payment-method',
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName('id').description('id of the legal representative')),
                        responseFields(
                                fieldWithPath('customerId').description('Fattmerchant customer id'),
                                fieldWithPath('cardHolder').description('card holder name'),
                                fieldWithPath('cardLastFour').description('card last four digits'),
                                fieldWithPath('cardType').description('card type (visa, mastercard)'),
                                fieldWithPath('cardExpiration').description('card expiration'),
                                fieldWithPath('address1').description('billing address line 1'),
                                fieldWithPath('address2').description('billing address line 2'),
                                fieldWithPath('addressCity').description('billing address city'),
                                fieldWithPath('addressState').description('billing address state'),
                                fieldWithPath('addressCountry').description('billing address country'),
                                fieldWithPath('addressZip').description('billing address zip'))))
                .header('Authorization', "Bearer " + resp.json.access_token)
                .when()
                .port(this.serverPort)
                .get('/api/users/{id}/payment-method', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('customerId', is(nullValue()))
                .body('cardHolder', is(nullValue()))
                .body('cardLastFour', is(nullValue()))
                .body('cardType', is(nullValue()))
                .body('cardExpiration', is(nullValue()))
                .body('address1', is(nullValue()))
                .body('address2', is(nullValue()))
                .body('addressCity', is(nullValue()))
                .body('addressState', is(nullValue()))
                .body('addressCountry', is(nullValue()))
                .body('addressZip', is(nullValue()))
        cleanup:
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    def savePaymentMethod() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)

        RestResponse resp
        LegalRepresentative attorney
        User user
        BigDecimal toCharge = TestUtils.randomNumber()
        Package.withNewTransaction {
            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword',
                    fmCustomerId: '9a601cc7-5343-48f2-a607-b888bbe9cea8')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            TestUtils.addPaidAccountTransaction(profile)
            new AccountTransaction(profile: profile, amount: toCharge, memo: TestUtils.TEST_MEMO,
                    source: TransactionSource.MAINTENANCE).save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')
        String fmToken = 'token'
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('save-payment-method',
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName('id').description('id of the legal representative')),
                        requestFields(
                                fieldWithPath('customerId').description('Fattmerchant customer id'),
                                fieldWithPath('fmPaymentMethodId').description('Fattmerchant payment method id'),
                                fieldWithPath('cardHolder').description('card holder name'),
                                fieldWithPath('cardLastFour').description('card last four digits'),
                                fieldWithPath('cardType').description('card type (visa, mastercard)'),
                                fieldWithPath('cardExpiration').description('card expiration'),
                                fieldWithPath('address1').description('billing address line 1'),
                                fieldWithPath('address2').description('billing address line 2'),
                                fieldWithPath('addressCity').description('billing address city'),
                                fieldWithPath('addressState').description('billing address state'),
                                fieldWithPath('addressCountry').description('billing address country'),
                                fieldWithPath('addressZip').description('billing address zip')),
                        responseFields(
                                fieldWithPath('paymentMethod.customerId').description('Fattmerchant customer id'),
                                fieldWithPath('paymentMethod.cardHolder').description('card holder name'),
                                fieldWithPath('paymentMethod.cardLastFour').description('card last four digits'),
                                fieldWithPath('paymentMethod.cardType').description('card type (visa, mastercard)'),
                                fieldWithPath('paymentMethod.cardExpiration').description('card expiration'),
                                fieldWithPath('paymentMethod.address1').description('billing address line 1'),
                                fieldWithPath('paymentMethod.address2').description('billing address line 2'),
                                fieldWithPath('paymentMethod.addressCity').description('billing address city'),
                                fieldWithPath('paymentMethod.addressState').description('billing address state'),
                                fieldWithPath('paymentMethod.addressCountry').description('billing address country'),
                                fieldWithPath('paymentMethod.addressZip').description('billing address zip'),
                                fieldWithPath('message.text').description('payment message'),
                                fieldWithPath('message.type').description('message type'),
                                fieldWithPath('balance').description('current user balance'),
                                fieldWithPath('balance.estTax').description('estimated taxes'),
                                fieldWithPath('balance.grandTotal').description('total amount to be paid'),
                                fieldWithPath('balance.subTotal').description('EV fees to be paid'))))
                .body("""{
    "address1": "208 Concord Ave",
    "address2": null,
    "addressCity": "Cambridge",
    "addressCountry": "UNITED_STATES",
    "addressState": "MA",
    "addressZip": "02138",
    "cardExpiration": "122027",
    "cardHolder": "Peter Smith",
    "cardLastFour": "1111",
    "cardType": "mastercard",
    "customerId": "9370bdc7-b5b9-415a-9fdd-4e54a86bb5c1",
    "fmPaymentMethodId": "${fmToken}"
}""")
                .header('Authorization', "Bearer " + resp.json.access_token)
                .when()
                .port(this.serverPort)
                .put('/api/users/{id}/payment-method', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('paymentMethod.customerId', equalTo('9370bdc7-b5b9-415a-9fdd-4e54a86bb5c1'))
                .body('paymentMethod.cardHolder', equalTo('Peter Smith'))
                .body('paymentMethod.cardLastFour', equalTo('1111'))
                .body('paymentMethod.cardType', equalTo('Mastercard'))
                .body('paymentMethod.cardExpiration', equalTo('12/2027'))
                .body('paymentMethod.address1', equalTo('208 Concord Ave'))
                .body('paymentMethod.address2', nullValue())
                .body('paymentMethod.addressCity', equalTo('Cambridge'))
                .body('paymentMethod.addressState', equalTo('MA'))
                .body('paymentMethod.addressCountry', equalTo('UNITED_STATES'))
                .body('paymentMethod.addressZip', equalTo('02138'))
                .body('message.type', equalTo(ErrorMessageType.PAYMENT_CHARGED.name()))
                .body('message.text', equalTo("\$${NumberUtils.formatMoneyNumber(toCharge)} was successfully charged to your card on file.".toString()))
                .body('balance.estTax', equalTo(BigDecimal.ZERO.intValue()))
                .body('balance.grandTotal', equalTo(BigDecimal.ZERO.intValue()))
                .body('balance.subTotal', equalTo(BigDecimal.ZERO.intValue()))

        PaymentMethod paymentMethod
        List<AccountTransaction> transactions
        PaymentMethod.withNewTransaction {
            paymentMethod = PaymentMethod.findByUser(user)
            transactions = AccountTransaction.findAllByProfile(user.profile)
        }
        assert fmToken == paymentMethod.fmPaymentMethodId
        TestUtils.assertPaidAccountTransaction(transactions, toCharge, TestUtils.ACCOUNT_TRANSACTIONS_COUNT, null, TestUtils.PAID_BALANCE)

        cleanup:
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    def savePaymentMethodFailedToCharge() {
        given:
        BigDecimal toCharge = new BigDecimal(750)
        failedToPayMock(paymentServiceMock, taxServiceMock)
        estimateTaxMock(taxServiceMock, toCharge, toCharge)

        RestResponse resp
        LegalRepresentative attorney
        User user
        Package.withNewTransaction {
            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword',
                    fmCustomerId: '9a601cc7-5343-48f2-a607-b888bbe9cea8')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            TestUtils.addPaidAccountTransaction(profile)
            new AccountTransaction(profile: profile, amount: toCharge, memo: TestUtils.TEST_MEMO,
                    source: TransactionSource.MAINTENANCE).save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')
        String fmToken = 'token'
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("""{
    "address1": "208 Concord Ave",
    "address2": null,
    "addressCity": "Cambridge",
    "addressCountry": "UNITED_STATES",
    "addressState": "MA",
    "addressZip": "02138",
    "cardExpiration": "122027",
    "cardHolder": "Peter Smith",
    "cardLastFour": "1111",
    "cardType": "mastercard",
    "customerId": "9370bdc7-b5b9-415a-9fdd-4e54a86bb5c1",
    "fmPaymentMethodId": "${fmToken}"
}""")
                .header('Authorization', "Bearer " + resp.json.access_token)
                .when()
                .port(this.serverPort)
                .put('/api/users/{id}/payment-method', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('paymentMethod.customerId', equalTo('9370bdc7-b5b9-415a-9fdd-4e54a86bb5c1'))
                .body('paymentMethod.cardHolder', equalTo('Peter Smith'))
                .body('paymentMethod.cardLastFour', equalTo('1111'))
                .body('paymentMethod.cardType', equalTo('Mastercard'))
                .body('paymentMethod.cardExpiration', equalTo('12/2027'))
                .body('paymentMethod.address1', equalTo('208 Concord Ave'))
                .body('paymentMethod.address2', nullValue())
                .body('paymentMethod.addressCity', equalTo('Cambridge'))
                .body('paymentMethod.addressState', equalTo('MA'))
                .body('paymentMethod.addressCountry', equalTo('UNITED_STATES'))
                .body('paymentMethod.addressZip', equalTo('02138'))
                .body('message.type', equalTo(ErrorMessageType.PAYMENT_FAILED.name()))
                .body('message.text', notNullValue())
                .body('balance.estTax', equalTo(BigDecimal.ZERO.intValue()))
                .body('balance.grandTotal', equalTo(toCharge.intValue()))
                .body('balance.subTotal', equalTo(toCharge.intValue()))

        PaymentMethod paymentMethod
        List<AccountTransaction> transactions
        PaymentMethod.withNewTransaction {
            paymentMethod = PaymentMethod.findByUser(user)
            transactions = AccountTransaction.findAllByProfile(user.profile)
        }
        assert fmToken == paymentMethod.fmPaymentMethodId
        assert 2 == transactions.size()

        cleanup:
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

}
