package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.*
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.converters.JSON
import grails.gorm.transactions.Rollback
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.JsonConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.path.json.config.JsonPathConfig
import io.restassured.specification.RequestSpecification
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.ResponseFieldsSnippet

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration

class AttorneyControllerSpec extends TestMockUtils {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private AdminService adminService
    @Autowired
    private ProfileService profileService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private AnswerService answerService
    @Autowired
    private OrganizationService organizationService
    @Autowired
    private PackageService packageService
    @Autowired
    private PackageQuestionnaireService packageQuestionnaireService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    private TaxService taxServiceMock = Mock(TaxService)
    @Autowired
    private SqlService sqlService

    protected RequestSpecification spec

    private ResponseFieldsSnippet responseFields

    def setup() {
        this.spec = new RequestSpecBuilder().setConfig(RestAssuredConfig.config().jsonConfig(JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.responseFields = responseFields(
                fieldWithPath('id').description('Id of the attorney'),
                fieldWithPath('firstName').description('First name of the created user'),
                fieldWithPath('middleName').description('middle name of the created user'),
                fieldWithPath('lastName').description('last name of the created user'),
                fieldWithPath('easyVisaId').description('EasyVisaId generated for the created user'),
                fieldWithPath('email').description('email of the created user'),
                fieldWithPath('officeEmail').description('officeEmail of the created user'),
                subsectionWithPath('officeAddress').description('officeAddress of the created user'),
                fieldWithPath('profilePhoto').description('URL for profile photo of user.'),
                fieldWithPath('registrationStatus').description('Registration Status of the user'),
                fieldWithPath('attorneyType').description('AttorneyType of the user'),
                fieldWithPath('representativeType').description('RepresentativeType of the user'),
                fieldWithPath('officePhone').description('Office phone of the user'),
                fieldWithPath('mobilePhone').description('Mobile phone of the user'),
                fieldWithPath('faxNumber').description('Fax number of the user'),
                fieldWithPath('facebookUrl').description('facebook URL of the user'),
                fieldWithPath('twitterUrl').description('twitter URL of the user'),
                fieldWithPath('youtubeUrl').description('youtube URL of the user'),
                fieldWithPath('linkedinUrl').description('linkedin URL of the user'),
                fieldWithPath('websiteUrl').description('website URL of the user'),
                fieldWithPath('practiceName').description('practice name of the user'),
                subsectionWithPath('organizations').description('Name and Ids of the organizations representative is associated with'),
                subsectionWithPath('feeSchedule').description('Fee Schedule of the representative'),
                fieldWithPath('newFirmInviteDetails').description('New Firm invite details if user has sent any invite'),
                fieldWithPath('newFirmJoinRequestDetails').description('New Firm Join request details if user has requested to join any organization'),
                fieldWithPath('creditBalance').description('Credit balance for the representative'),)

        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        updateToMock(attorneyService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)

    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
        updateToService(attorneyService.accountService, paymentService, taxService)
    }

    def "test register-attorney"() {
        given:
        Long userSeq
        String userName = "mrattorneyattorney${userSeq}"

        String randNum = TestUtils.randomNumber()
        Map attorneyData = [username     : "mrattorneyattorney${randNum}",
                            password     : "changeme",
                            firstName    : "Attr",
                            middleName   : "Att",
                            lastName     : "Attorney",
                            email        : "attorney${randNum}@lawoffice101.com",
                            phone        : "+639171111111",
                            language     : "fil",
                            officePhone  : "+639171111111",
                            faxNumber    : "+63917222222222",
                            mobilePhone  : "+639173333333",
                            officeAddress: ["country": "UNITED_STATES",
                                            "state"  : "ALABAMA",
                                            "line1"  : "line1 address",
                                            "line2"  : "line2 address",
                                            "zipCode": "98145",
                                            "city"   : "cityName"],
                            twitterUrl   : "https://twitter.com/easyvisa",
                            youtubeUrl   : "https://youtube.com/easyvisa",
                            facebookUrl  : "https://facebook.com/easyvisa",
                            linkedinUrl  : "https://linkedin.com/easyvisa",
                            websiteUrl   : "https://easyvisa.com",
                            practiceName : "Organization_Name"

        ]
        String attorneyJson = attorneyData as JSON
        //fixme Why do we need to get a sequence here?
        // We are only testing if attorney can be registered or not.
        // We can just validate if the api response contains relevant values.
        // Delete can happen based on the response values.

        AdminSettings.withNewTransaction {
            userSeq = sqlService.getNextSequenceId("legal_representative_ev_id_seq") + 7
        }


        expect:
        String respEasyVisaId = given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('register-attorney',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('username').description('Username of the user'),
                                fieldWithPath('password').description('Password of the user'),
                                fieldWithPath('firstName').description('First name of the user'),
                                fieldWithPath('middleName').description('Middle name of the user (optional)').optional(),
                                fieldWithPath('lastName').description('Last name of the user'),
                                fieldWithPath('email').description('Email of the user'),
                                fieldWithPath('phone').description('Phone of the user (optional)').optional(),
                                fieldWithPath('language').description('Language preference of the user: en, es, fil, zh-cn (optional)'),
                                subsectionWithPath('officeAddress').description('Office address').optional(),
                                fieldWithPath('officePhone').description('Office phone').optional(),
                                fieldWithPath('faxNumber').description('Fax number').optional(),
                                fieldWithPath('mobilePhone').description('Mobile phone').optional(),
                                fieldWithPath('twitterUrl').description('Twitter URL').optional(),
                                fieldWithPath('facebookUrl').description('Facebook URL').optional(),
                                fieldWithPath('linkedinUrl').description('Linkedin URL').optional(),
                                fieldWithPath('youtubeUrl').description('Youtube URL').optional(),
                                fieldWithPath('websiteUrl').description('Website URL').optional(),
                                fieldWithPath('practiceName').description('Practice Name of the user').optional()
                        ), this.responseFields.and(fieldWithPath('username').description('Username of the user'))))
                .body(attorneyJson)
                .when()
                .port(this.serverPort)
                .post('/api/public/attorneys')
                .then()
                .assertThat()
                .statusCode(is(201))
                .body("easyVisaId", startsWith("A000"))
                .body("officeAddress.state", equalTo("ALABAMA"))
                .body("officeAddress.city", equalTo("cityName"))
                .extract()
                .path("easyVisaId")



        cleanup:
        LegalRepresentative.withNewTransaction {
            LegalRepresentative attorney = LegalRepresentative.createCriteria().get {
                profile {
                    eq('easyVisaId', respEasyVisaId)
                }
            }
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    def "test /verify-attorney"() {
        expect:
        User user
        RegistrationCode registrationCode
        User.withTransaction {
            user = TestUtils.createUser(['username': 'myusername'])
            registrationCode = new RegistrationCode(username: user.username).save(failOnError: true)
        }

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('verify-attorney',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('token').description('Registration token')
                        )))
                .body('{ "token": "' + registrationCode.token + '" }')
                .when()
                .port(this.serverPort)
                .post('/api/public/verify-registration')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        User.withNewTransaction {
            user.refresh().delete(failOnError: true)
        }
    }

    def "test attorney can be updated"() {
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
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('update-attorney',
                        preprocessResponse(prettyPrint()), this.responseFields))
                .body('''
{
"firstName": "newFirst",
"lastName":"newLast",
"officeAddress":{
"country":"UNITED_STATES","state":"ALABAMA","line1":"line1 address","line2":"line2 address",
"zipCode":"98145","city":"cityNameUpdated"},
"feeSchedule":[{"amount":3500,"benefitCategory":"F1_A"},{"amount":5000,"benefitCategory":"IR1"}]
}
''')
                .when()
                .port(this.serverPort)
                .patch("/api/attorneys/${testHelper.packageLegalRepresentative.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("firstName", equalTo("newFirst"))
                .body("lastName", equalTo("newLast"))
                .body("officeAddress.city", equalTo("cityNameUpdated"))
                .body("feeSchedule[0].amount", equalTo(3500 as int))
                .body("feeSchedule[0].benefitCategory", equalTo('F1_A'))
                .body("feeSchedule[1].amount", equalTo(5000 as int))
                .body("feeSchedule[1].benefitCategory", equalTo('IR1'))

        cleanup:
        testHelper.clean()
    }

    def "test attorney address can be updated if he has an old address"() {
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
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('update-attorney',
                        preprocessResponse(prettyPrint()),
                        this.responseFields))
                .body('''
{
"firstName": "newFirst",
"lastName":"newLast",
"officeAddress":{"country":"UNITED_STATES","state":"ALASKA","line1":"line1 updated","zipCode":"54321","city":"city updated"}
}
''')
                .when()
                .port(this.serverPort)
                .patch("/api/attorneys/${testHelper.packageLegalRepresentative.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("officeAddress.line1", equalTo("line1 updated"))
                .body("officeAddress.city", equalTo("city updated"))
                .body("officeAddress.state", equalTo("ALASKA"))
                .body("officeAddress.zipCode", equalTo("54321"))

        cleanup:
        testHelper.clean()
    }

    def "test username of attorney can not be updated"() {
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
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .body('''
{
"firstName": "newFirst",
"lastName":"newLast",
"username":"newtestattorney"
}
''')
                .when()
                .port(this.serverPort)
                .patch("/api/attorneys/${testHelper.packageLegalRepresentative.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))

        cleanup:
        testHelper.clean()
    }

    def "can validate an attorney by email and easyvisaId"() {
        given:
        LegalRepresentative attorney
        RestResponse attorneyResp
        RestResponse applicantResp
        Organization org
        Profile applicantProfile
        LegalRepresentative.withNewTransaction {
            User user = new User(username: 'registeredattorney', language: 'En/US', password: 'registeredAttorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true)
            attorney.registrationStatus = RegistrationStatus.REPRESENTATIVE_SELECTED
            attorney.save(failOnError: true)

            org = TestUtils.createOrganization('Org to Validate Attorney', OrganizationType.SOLO_PRACTICE)
            organizationService.addAttorneyToOrganization(org, attorney)

            User applicantUser = new User(username: 'applicantuser', language: 'En/US', password: 'applicantPassword',
                    accountLocked: false)
            applicantProfile = new Profile(user: applicantUser, lastName: 'Applicant Last', firstName: 'Applicant First',
                    middleName: 'Applicant Middle', email: 'applicantUser@easyvisa.com', easyVisaId: 'app-ev-id')
                    .save(failOnError: true)
            TestUtils.createUserRole(applicantUser, Role.USER)
        }
        attorneyResp = TestUtils.logInUser(serverPort, 'registeredattorney', 'registeredAttorneyPassword')
        applicantResp = TestUtils.logInUser(serverPort, 'applicantuser', 'applicantPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${attorneyResp.json['access_token']}")
                .filter(document('validate-attorney',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('email').description('Email of attorney'),
                                fieldWithPath('easyVisaId').description('EasyVisaId of attorney')),
                        responseFields(
                                fieldWithPath('representativeId').description('Id of the representative'),
                                fieldWithPath('firstName').description('First name of the representative'),
                                fieldWithPath('middleName').description('Middle name of the representative'),
                                fieldWithPath('lastName').description('Last name of the representative'),
                                subsectionWithPath('organizations').description('List of the attorney organizations'))))
                .body("""{
"email": "${attorney.profile.email}",
"easyVisaId": "${attorney.profile.easyVisaId}"
}""")
                .when()
                .port(this.serverPort)
                .post("/api/attorneys/validate")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('representativeId', equalTo(attorney.id as int))
                .body('organizations.id[0]', equalTo(org.id as int))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${applicantResp.json['access_token']}")
                .body("""{
"email": "${attorney.profile.email}",
"easyVisaId": "${attorney.profile.easyVisaId}"
}""")
                .when()
                .port(this.serverPort)
                .post("/api/attorneys/validate")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('representativeId', equalTo(attorney.id as int))
                .body('organizations.id[0]', equalTo(org.id as int))

        cleanup:
        if (attorney) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteProfile(applicantProfile.refresh())
                TestUtils.deleteRepresentative(attorney.id)
                TestUtils.deleteOrganization(org)
            }
        }
    }

    def "can check if an attorney is an admin of an organization by email and easyvisaId"() {
        given:
        RestBuilder rest = new RestBuilder()
        LegalRepresentative attorney
        RestResponse resp
        Organization organization
        LegalRepresentative.withNewTransaction {
            organization = TestUtils.createOrganization('test org')
            User user = new User(username: 'registeredattorney', language: 'En/US',
                    password: 'registeredAttorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true)
            new OrganizationEmployee(organization: organization, employee: attorney, status: EmployeeStatus.ACTIVE,
                    position: EmployeePosition.ATTORNEY, isAdmin: true).save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'registeredattorney', 'registeredAttorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('validate-attorney-isadmin',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('email').description('Email of attorney'),
                                fieldWithPath('easyVisaId').description('EasyVisaId of attorney'),
                                fieldWithPath('organizationId').description('Id of organization')
                        ), responseFields(
                        fieldWithPath('isAdmin').description('Id of the representative'),
                        fieldWithPath('position').description('Id of the representative')

                )))
                .body("""
{
"email": "${attorney.profile.email}",
"easyVisaId": "${attorney.profile.easyVisaId}",
"organizationId":${organization.id}
}
""")
                .when()
                .port(this.serverPort)
                .post("/api/employees/isadmin")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("isAdmin", equalTo(true))
        cleanup:
        if (attorney) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteOrganization(organization)
                TestUtils.deleteRepresentative(attorney.id)
            }
        }
    }

    def "can invite an attorney to create a new organization"() {
        given:
        RestBuilder rest = new RestBuilder()
        LegalRepresentative attorney
        LegalRepresentative invitee
        RestResponse resp
        LegalRepresentative.withNewTransaction {
            User user = new User(username: 'registeredattorney', language: 'En/US', password: 'registeredAttorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true)

            User invitedUser = new User(username: 'invitedattorney', language: 'En/US', password: 'invitedAttorneyPassword')
            Profile invitedProfile = new Profile(user: invitedUser, lastName: 'last', firstName: 'invited First', middleName: 'middle', email: 'invitedAttorney@easyvisa.com')
            invitee = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: invitedProfile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            invitee = attorneyService.createAttorney(invitee)
            invitedUser.accountLocked = false
            invitedUser.save(failOnError: true)
            invitee.registrationStatus = RegistrationStatus.COMPLETE
            invitee.profile.user.paid = true
            invitee.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'registeredattorney', 'registeredAttorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('invite-attorney-to-create-organization',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('representativeId').description('RepresentativeId of attorney to invite'),
                        ), responseFields(
                        fieldWithPath('message').description('Success message')

                )))
                .body("""
{
"representativeId": "${invitee.id}"
}
""")
                .when()
                .port(this.serverPort)
                .post("/api/attorneys/create-organization-invite")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("message", is(notNullValue()))
        cleanup:
        if (attorney) {
            Alert.withNewTransaction {
                Alert.last()?.delete(failOnError: true)
                ProcessRequest.last()?.delete(failOnError: true)
                TestUtils.deleteRepresentative(attorney.id)
                TestUtils.deleteRepresentative(invitee.id)
            }
        }
    }

    def "test list fee schedule"() {
        given:
        Organization organization
        RestResponse resp
        LegalRepresentative currentLegRep
        LegalRepresentative specifiedLegRep

        Organization.withNewTransaction {
            User user = new User(username: 'currentlegrep', language: 'En/US', password: 'attorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'currentlegrep@easyvisa.com')
            currentLegRep = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            currentLegRep = attorneyService.createAttorney(currentLegRep)
            user.accountLocked = false
            user.save(failOnError: true)
            currentLegRep.registrationStatus = RegistrationStatus.COMPLETE
            currentLegRep.save(failOnError: true)

            User user2 = new User(username: 'specifiedlegrep', language: 'En/US', password: 'attorneyPassword')
            Profile profile2 = new Profile(user: user2, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'specifiedlegrep@easyvisa.com')
            specifiedLegRep = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile2, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            specifiedLegRep = attorneyService.createAttorney(specifiedLegRep)
            user2.accountLocked = false
            user2.save(failOnError: true)
            specifiedLegRep.registrationStatus = RegistrationStatus.COMPLETE
            specifiedLegRep.save(failOnError: true)

            organization = organizationService.create("My Org", OrganizationType.LAW_FIRM)
            organizationService.addAttorneyToOrganization(organization, currentLegRep)
            organizationService.addAttorneyToOrganization(organization, specifiedLegRep)

            Fee fee = new Fee(amount: 110, representative: specifiedLegRep, benefitCategory: ImmigrationBenefitCategory.IR1)
            specifiedLegRep.addToFeeSchedule(fee)
        }

        resp = TestUtils.logInUser(serverPort, 'currentlegrep', 'attorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('get-fee-schedule',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Representative Id'),
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/attorneys/{id}/fee-schedule", specifiedLegRep.id)
                .then()
                .assertThat()
                .body("size()", equalTo(1))
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        if (organization) {
            Organization.withNewTransaction {
                TestUtils.deleteOrganization(organization)
                TestUtils.deleteRepresentative(currentLegRep.id)
                TestUtils.deleteRepresentative(specifiedLegRep.id)
            }
        }
    }

    def testGetMarketingData() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort                 : serverPort,
                                                                 attorneyService            : attorneyService,
                                                                 packageService             : packageService,
                                                                 adminService               : adminService,
                                                                 answerService              : answerService,
                                                                 packageQuestionnaireService: packageQuestionnaireService,
                                                                 profileService             : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildPackageLegalRepProspectInfo()
                .buildNoPackageLegalRep(true)
                .changePackageStatusToOpen()
                .logInPackageLegalRep()
                .logInNoPackageLegalRep()

        expect:
        //get data by user
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-marketing-data',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('representativeId').description('Legal representative id')),
                        requestParameters(

                                parameterWithName('organizationId').description('Id of legal representative organization. Optional parameter.')),
                        responseFields(
                                fieldWithPath('activeClients').description('active clients'),
                                fieldWithPath('activeClients.month').description('Count in current month.'),
                                fieldWithPath('activeClients.quarter').description('Counts in current quarter'),
                                fieldWithPath('activeClients.ytd').description('Count in current year'),
                                fieldWithPath('activeClients.lifeTime').description('Count of all period'),
                                fieldWithPath('phoneNumberClients').description('phone number views'),
                                fieldWithPath('phoneNumberClients.month').description('Sum in current month.'),
                                fieldWithPath('phoneNumberClients.quarter').description('Sum in current quarter'),
                                fieldWithPath('phoneNumberClients.ytd').description('Sum in current year'),
                                fieldWithPath('phoneNumberClients.lifeTime').description('Sum of all period'),
                                fieldWithPath('prospectiveClients').description('profile views'),
                                fieldWithPath('prospectiveClients.month').description('Sum in current month.'),
                                fieldWithPath('prospectiveClients.quarter').description('Sum in current quarter'),
                                fieldWithPath('prospectiveClients.ytd').description('Sum in current year'),
                                fieldWithPath('prospectiveClients.lifeTime').description('Sum of all period')
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/marketing?organizationId={organizationId}',
                        testHelper.packageLegalRepresentative.id, testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('activeClients.month', equalTo(1))
                .body('phoneNumberClients.month', equalTo(5))
                .body('prospectiveClients.month', equalTo(7))

        //get data by admin
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenNoPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/marketing',
                        testHelper.packageLegalRepresentative.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('activeClients.month', equalTo(1))
                .body('phoneNumberClients.month', equalTo(5))
                .body('prospectiveClients.month', equalTo(7))

        cleanup:
        testHelper.clean()
    }

    def testGetMarketingDataInInactiveOrg() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort                 : serverPort,
                                                                 attorneyService            : attorneyService,
                                                                 packageService             : packageService,
                                                                 adminService               : adminService,
                                                                 answerService              : answerService,
                                                                 packageQuestionnaireService: packageQuestionnaireService,
                                                                 profileService             : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildPackageLegalRepProspectInfo()
                .buildNoPackageLegalRep(true)
                .changePackageStatusToOpen()
                .makePackageRepresentativeInactiveInOrg()
                .logInPackageLegalRep()
                .logInNoPackageLegalRep()

        expect:
        //get data by user
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/marketing?organizationId={organizationId}',
                        testHelper.packageLegalRepresentative.id, testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('activeClients.month', equalTo(1))
                .body('phoneNumberClients.month', equalTo(5))
                .body('prospectiveClients.month', equalTo(7))

        //get data by admin
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenNoPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/marketing',
                        testHelper.packageLegalRepresentative.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('activeClients.month', equalTo(1))
                .body('phoneNumberClients.month', equalTo(5))
                .body('prospectiveClients.month', equalTo(7))

        cleanup:
        testHelper.clean()
    }

    def testFinancialDashboardData() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndTwoBeneficiariesOpenPackage()
                .sumPerApplicantFee()
                .updateBeneficiaryBenefitCategory()
                .buildPackageLegalRepArticleBonus()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-attorney-financial-data',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('representativeId').description('Legal representative id')),
                        requestParameters(

                                parameterWithName('organizationId').description('Id of legal representative organization. Optional parameter.')),
                        responseFields(
                                fieldWithPath('articleBonuses').description('Article bonuses.'),
                                fieldWithPath('articleBonuses.month').description('Sum in current month.'),
                                fieldWithPath('articleBonuses.quarter').description('Sum in current quarter'),
                                fieldWithPath('articleBonuses.ytd').description('Sum in current year'),
                                fieldWithPath('articleBonuses.lifeTime').description('Sum of all period'),
                                fieldWithPath('clientRevenue').description('Attorney revenue.'),
                                fieldWithPath('clientRevenue.month').description('Sum in current month.'),
                                fieldWithPath('clientRevenue.quarter').description('Sum in current quarter'),
                                fieldWithPath('clientRevenue.ytd').description('Sum in current year'),
                                fieldWithPath('clientRevenue.lifeTime').description('Sum of all period'),
                                fieldWithPath('referralBonuses').description('Referral bonuses.'),
                                fieldWithPath('referralBonuses.month').description('Sum in current month.'),
                                fieldWithPath('referralBonuses.quarter').description('Sum in current quarter'),
                                fieldWithPath('referralBonuses.ytd').description('Sum in current year'),
                                fieldWithPath('referralBonuses.lifeTime').description('Sum of all period'),
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/financial?organizationId={organizationId}',
                        testHelper.packageLegalRepresentative.id, testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('articleBonuses.month', equalTo(expectedBigDecimalValue(100)))
                .body('clientRevenue.month', equalTo(expectedBigDecimalValue(200)))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/financial',
                        testHelper.packageLegalRepresentative.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('articleBonuses.month', equalTo(expectedBigDecimalValue(100)))
                .body('clientRevenue.month', equalTo(expectedBigDecimalValue(200)))

        cleanup:
        testHelper.deletePackageOnly()
                .deletePackageLegalRep()
                .deleteOrganization()
    }

    def testFinancialDashboardDataAdmin() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])

        PackageTestBuilder testExtraHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                      attorneyService: attorneyService,
                                                                      packageService : packageService,
                                                                      adminService   : adminService,
                                                                      profileService : profileService])

        PackageTestBuilder testExtraAdminHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                           attorneyService: attorneyService,
                                                                           profileService : profileService])

        testHelper.buildPackageLegalRep(true)
                .buildNoPackageLegalRep()
                .buildNoPackageLegalRepArticleBonus()
                .buildPackageLegalRepArticleBonus()
                .logInPackageLegalRep()
                .logInNoPackageLegalRep()


        testExtraHelper.legalRepresentativeNoPackage = testHelper.legalRepresentativeNoPackage
        testExtraHelper.packageLegalRepresentative = testHelper.packageLegalRepresentative

        testExtraHelper.buildOrganization()
                .addNoPackageRepresentativeToOrg()
                .addPackageRepresentativeToOrg()
                .buildPetitionerAndBeneficiaryOpenPackage()
                .buildNoPackageLegalRepArticleBonus()
                .buildPackageLegalRepArticleBonus()


        testExtraAdminHelper.legalRepresentativeNoPackage = testHelper.legalRepresentativeNoPackage
        testExtraAdminHelper.packageLegalRepresentative = testHelper.packageLegalRepresentative

        testExtraAdminHelper.buildOrganization()
                .addNoPackageRepresentativeToOrg()
                .addPackageRepresentativeToOrg(true)
                .buildNoPackageLegalRepArticleBonus()
                .buildPackageLegalRepArticleBonus()


        expect:
        //admin inside admin org
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/financial?organizationId={organizationId}',
                        testHelper.legalRepresentativeNoPackage.id, testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('articleBonuses.month', equalTo(expectedBigDecimalValue(200)))
                .body('clientRevenue.month', equalTo(0))

        //admin through all orgs, but data for current org only
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/financial',
                        testHelper.legalRepresentativeNoPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('articleBonuses.month', equalTo(expectedBigDecimalValue(400)))
                .body('clientRevenue.month', equalTo(0))

        //user can see all available bonuses for all orgs
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenNoPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/financial',
                        testHelper.legalRepresentativeNoPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('articleBonuses.month', equalTo(expectedBigDecimalValue(600)))
                .body('clientRevenue.month', equalTo(0))

        //user can see all available revenue for all orgs
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/financial',
                        testHelper.packageLegalRepresentative.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('articleBonuses.month', equalTo(expectedBigDecimalValue(300)))
                .body('clientRevenue.month', equalTo(expectedBigDecimalValue(100)))

        cleanup:
        testExtraHelper.deletePackageOnly()
        testHelper.deletePackageLegalRep()
                .deleteNoPackageLegalRep()
                .deleteOrganization()
        testExtraHelper.deleteOrganization()
        testExtraAdminHelper.deleteOrganization()
    }

    def testFinancialDashboardDataDoubleTransactions() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .sumPerApplicantFee()
                .updateBeneficiaryBenefitCategory(ImmigrationBenefitCategory.F1_A)
                .buildPackageLegalRepArticleBonus()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/financial?organizationId={organizationId}',
                        testHelper.packageLegalRepresentative.id, testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('articleBonuses.month', equalTo(expectedBigDecimalValue(100)))
                .body('clientRevenue.month', equalTo(expectedBigDecimalValue(100)))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{representativeId}/financial',
                        testHelper.packageLegalRepresentative.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('articleBonuses.month', equalTo(expectedBigDecimalValue(100)))
                .body('clientRevenue.month', equalTo(expectedBigDecimalValue(100)))

        cleanup:
        testHelper.deletePackageOnly()
                .deletePackageLegalRep()
                .deleteOrganization()
    }

    def testGetAttorneyNotifications() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()
        LegalRepresentative attorney = testHelper.packageLegalRepresentative

        EmailTemplate email
        LegalRepresentative.withNewTransaction {
            EmailPreference preference = new EmailPreference(type: NotificationType.QUESTIONNAIRE_INACTIVITY,
                    preference: Boolean.TRUE, repeatInterval: 7).save(failOnError: true)
            email = new EmailTemplate(attorney: attorney.refresh(), templateType: EmailTemplateType.QUESTIONNAIRE_INACTIVITY,
                    content: 'content', subject: 'subject',
                    preference: preference).save(failOnError: true)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-attorney-notifications',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Legal representative id')),
                        requestParameters(
                                parameterWithName('types').optional().description('List of types to be returned')),
                        responseFields(
                                fieldWithPath('[].id').description('Id of the email notification').optional(),
                                fieldWithPath('[].subject').description('Template subject'),
                                fieldWithPath('[].content').description('Template content'),
                                fieldWithPath('[].repeatInterval').description('Repeat interval for notification sending. Null if disabled (Off value in UI select)').optional(),
                                fieldWithPath('[].templateType').description('Template type. Enum value.'))))
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/{id}/notifications', attorney.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('size()', equalTo(8))
                .body('[0].id', notNullValue())
                .body('[0].subject', equalTo('subject'))
                .body('[0].content', equalTo('content'))
                .body('[0].repeatInterval', equalTo(7))
                .body('[0].templateType', equalTo(EmailTemplateType.QUESTIONNAIRE_INACTIVITY.name()))
                .body('[1].id', nullValue())
                .body('[1].subject', equalTo(EmailTemplateType.DOCUMENT_PORTAL_INACTIVITY.subject))
                .body('[1].content', notNullValue())
                .body('[1].repeatInterval', nullValue())
                .body('[1].templateType', equalTo(EmailTemplateType.DOCUMENT_PORTAL_INACTIVITY.name()))

        cleanup:
        Email.withNewTransaction {
            EmailPreference preference = email.preference
            email.delete(failOnError: true)
            preference.delete(failOnError: true)
        }
        testHelper.clean()
    }

    def testUpdateEmailPreference() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()
        LegalRepresentative attorney = testHelper.packageLegalRepresentative

        EmailTemplate email
        LegalRepresentative.withNewTransaction {
            EmailPreference preference = new EmailPreference(type: NotificationType.QUESTIONNAIRE_INACTIVITY,
                    preference: Boolean.TRUE, repeatInterval: 7).save(failOnError: true)
            email = new EmailTemplate(attorney: attorney.refresh(), templateType: EmailTemplateType.QUESTIONNAIRE_INACTIVITY,
                    content: 'content', subject: EmailTemplateType.QUESTIONNAIRE_INACTIVITY.subject,
                    preference: preference).save(failOnError: true)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('update-attorney-notifications',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Legal representative id')),
                        requestFields(
                                fieldWithPath('attorneyNotifications').description('Preferences array'),
                                fieldWithPath('attorneyNotifications[].id').description('Id of the email notification'),
                                fieldWithPath('attorneyNotifications[].subject').description('Template subject'),
                                fieldWithPath('attorneyNotifications[].content').description('Template content'),
                                fieldWithPath('attorneyNotifications[].repeatInterval').description('Repeat interval for notification sending. Null if disabled (Off value in UI select)'),
                                fieldWithPath('attorneyNotifications[].templateType').description('Template type. Enum value.')),
                        responseFields(
                                fieldWithPath('[].id').description('Id of the email notification'),
                                fieldWithPath('[].subject').description('Template subject'),
                                fieldWithPath('[].content').description('Template content'),
                                fieldWithPath('[].repeatInterval').description('Repeat interval for notification sending. Null if disabled (Off value in UI select)'),
                                fieldWithPath('[].templateType').description('Template type. Enum value.'))))
                .body("""{
  "attorneyNotifications": [{
        "id": ${email.id},
        "subject": "subject 2",
        "content": "content 2",
        "repeatInterval": 14,
        "templateType": "${EmailTemplateType.QUESTIONNAIRE_INACTIVITY.name()}"
    }]}""")
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .patch('/api/attorneys/{id}/notifications', attorney.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('size()', equalTo(1))
                .body('[0].id', equalTo(email.id as int))
                .body('[0].subject', equalTo('subject 2'))
                .body('[0].content', equalTo('content 2'))
                .body('[0].repeatInterval', equalTo(14))
                .body('[0].templateType', equalTo(EmailTemplateType.QUESTIONNAIRE_INACTIVITY.name()))

        cleanup:
        Email.withNewTransaction {
            email.refresh()
            EmailPreference preference = email.preference
            email.delete(failOnError: true)
            preference.delete(failOnError: true)
        }
        testHelper.clean()
    }

    def testGetNotificationsTypes() {
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
                .filter(document('get-attorney-notifications-types',
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath('clientInactivity').description('Client inactivity reminders'),
                                subsectionWithPath('deadline').description('Deadline reminders'),
                                subsectionWithPath('importantDocuments').description('Important Client Documents to be Uploaded'),
                                subsectionWithPath('blocked').description('Reminders for Blocked Cases'),
                                fieldWithPath('clientInactivity[].displayName').description('Reminder\'s Display Name'),
                                fieldWithPath('clientInactivity[].value').description('Type of a reminder'))))
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/notifications/types')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('size()', equalTo(4))
                .body('clientInactivity[0].displayName', equalTo(NotificationType.QUESTIONNAIRE_INACTIVITY.displayName))
                .body('clientInactivity[0].value', equalTo(NotificationType.QUESTIONNAIRE_INACTIVITY.name()))

        cleanup:
        testHelper.clean()
    }


    def testGetUSCISEditionDates() {
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
                .filter(document('get-uscis-edition-dates',
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath('[].formId').description('Form Id'),
                                fieldWithPath('[].name').description('Form Name'),
                                fieldWithPath('[].displayText').description('Form DisplayText'),
                                fieldWithPath('[].editionDate').description('EditionDate in MM-dd-yyyy format'),
                                fieldWithPath('[].expirationDate').description('ExpirationDate in MM-dd-yyyy format'))))
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/attorneys/uscis-edition-dates')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('size()', equalTo(13))
                .body('[0].formId', equalTo('Form_129F'))
                .body('[0].editionDate', equalTo('11-07-2018'))
                .body('[0].expirationDate', equalTo('11-30-2020'))
                .body('[5].formId', equalTo('Form_601'))
                .body('[5].editionDate', equalTo('12-02-2019'))
                .body('[5].expirationDate', equalTo('07-31-2021'))
                .body('[10].formId', equalTo('Form_824'))
                .body('[10].editionDate', equalTo('11-08-2019'))
                .body('[10].expirationDate', equalTo('11-30-2021'))
                .body('[12].formId', equalTo('Form_400'))
                .body('[12].editionDate', equalTo('09-17-2019'))
                .body('[12].expirationDate', equalTo('09-30-2022'))


        cleanup:
        testHelper.clean()
    }

    def testReferralDiscount() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildNoPackageLegalRep()
                .setReferralDiscount()
        LegalRepresentative.withNewTransaction {
            testHelper.packageLegalRepresentative.refresh()
            testHelper.packageLegalRepresentative.registrationStatus = RegistrationStatus.CONTACT_INFO_UPDATED
            testHelper.packageLegalRepresentative.save(failOnError: true)
            testHelper.packageLegalRepresentative.user.username
            testHelper.legalRepresentativeNoPackage.refresh()
            testHelper.legalRepresentativeNoPackage.profile.email
        }
        testHelper.logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('referral-discount',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('email').description('Referral Attorney email')),
                        responseFields(
                                fieldWithPath('email').description('Referral Attorney email'))))
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body("""{"email": "${testHelper.legalRepresentativeNoPackage.profile.email}"}""")
                .when()
                .port(this.serverPort)
                .post('/api/attorneys/referral')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('email', equalTo(testHelper.legalRepresentativeNoPackage.profile.email))

        List<AccountTransaction> referralTransactions
        List<AccountTransaction> refereeTransactions
        AccountTransaction.withNewTransaction {
            referralTransactions = AccountTransaction.findAllByProfileAndSource(testHelper.legalRepresentativeNoPackage.profile, TransactionSource.REFERRAL)
            refereeTransactions = AccountTransaction.findAllByProfileAndSource(testHelper.packageLegalRepresentative.profile, TransactionSource.REFERRAL)
            referralTransactions.first().referral.id
            refereeTransactions.first().referral.id
        }

        assert referralTransactions.size() == 1
        AccountTransaction tr = referralTransactions.first()
        assert tr.amount == testHelper.referralDiscount.negate()
        assert tr.referral.id == testHelper.packageLegalRepresentative.profile.id
        assert tr.memo.contains(testHelper.packageLegalRepresentative.profile.name)

        assert refereeTransactions.size() == 1
        AccountTransaction tr1 = refereeTransactions.first()
        assert tr1.amount == testHelper.signUpDiscount.negate()
        assert tr1.referral.id == testHelper.legalRepresentativeNoPackage.profile.id
        assert tr1.memo.contains(testHelper.legalRepresentativeNoPackage.profile.name)

        cleanup:
        AccountTransaction.withNewTransaction {
            tr.delete(failOnError: true)
            tr1.delete(failOnError: true)
        }
        testHelper.clean()
    }

    def testReferralDiscountRegisteredAttorney() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildNoPackageLegalRep()
                .setReferralDiscount()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body("""{"email": "${testHelper.legalRepresentativeNoPackage.profile.email}"}""")
                .when()
                .port(this.serverPort)
                .post('/api/attorneys/referral')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))

        List<AccountTransaction> referralTransactions
        List<AccountTransaction> refereeTransactions
        AccountTransaction.withNewTransaction {
            referralTransactions = AccountTransaction.findAllByProfileAndSource(testHelper.legalRepresentativeNoPackage.profile, TransactionSource.REFERRAL)
            refereeTransactions = AccountTransaction.findAllByProfileAndSource(testHelper.packageLegalRepresentative.profile, TransactionSource.REFERRAL)
            true
        }

        assert referralTransactions.size() == 0
        assert refereeTransactions.size() == 0

        cleanup:
        testHelper.clean()
    }

    def testReferralDiscountEmployee() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildTrainee()
                .setReferralDiscount()
                .logInTrainee()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenTrainee)
                .body("""{"email": "${testHelper.packageLegalRepresentative.profile.email}"}""")
                .when()
                .port(this.serverPort)
                .post('/api/attorneys/referral')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('email', equalTo(testHelper.packageLegalRepresentative.profile.email))

        List<AccountTransaction> referralTransactions
        List<AccountTransaction> refereeTransactions
        AccountTransaction.withNewTransaction {
            referralTransactions = AccountTransaction.findAllByProfileAndSource(testHelper.trainee.profile, TransactionSource.REFERRAL)
            refereeTransactions = AccountTransaction.findAllByProfileAndSource(testHelper.packageLegalRepresentative.profile, TransactionSource.REFERRAL)
            referralTransactions.first().referral.id
            refereeTransactions.first().referral.id
        }

        assert referralTransactions.size() == 1
        AccountTransaction tr = referralTransactions.first()
        assert tr.amount == testHelper.signUpDiscount.negate()
        assert tr.referral.id == testHelper.packageLegalRepresentative.profile.id
        assert tr.memo.contains(testHelper.packageLegalRepresentative.profile.name)

        assert refereeTransactions.size() == 1
        AccountTransaction tr1 = refereeTransactions.first()
        assert tr1.amount == testHelper.referralDiscount.negate()
        assert tr1.referral.id == testHelper.trainee.profile.id
        assert tr1.memo.contains(testHelper.trainee.profile.name)

        cleanup:
        AccountTransaction.withNewTransaction {
            tr.delete(failOnError: true)
            tr1.delete(failOnError: true)
        }
        testHelper.clean()
    }

    private BigDecimal expectedBigDecimalValue(Integer value) {
        new BigDecimal(value).setScale(2)
    }

}
