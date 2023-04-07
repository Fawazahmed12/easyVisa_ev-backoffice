package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.*
import com.easyvisa.questionnaire.Answer
import com.easyvisa.utils.AnswerListPdfRulesStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.JsonConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.path.json.config.JsonPathConfig
import io.restassured.specification.RequestSpecification
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import grails.util.Holders
import org.apache.http.HttpStatus
import org.hibernate.SessionFactory
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import spock.lang.Ignore

import java.time.LocalDate

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class UserControllerDeactivationSpec extends TestMockUtils {

    private static final String CUSTOMER_ID = '1400f525-ce4f-4141-b122-d95497a1cea4'

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
    private OrganizationService organizationService
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
    private TaxService taxServiceMock = Mock(TaxService)

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().setConfig(RestAssuredConfig.config().jsonConfig(JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .addFilter(documentationConfiguration(this.restDocumentation)).build()
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }

    @Ignore
    def deactivateMembershipAttorney() {
        given:
        PackageTestBuilder testHelperLawFirm = PackageTestBuilder.init([serverPort     : serverPort,
                                                                        attorneyService: attorneyService,
                                                                        packageService : packageService,
                                                                        adminService   : adminService,
                                                                        profileService: profileService])
        PackageTestBuilder testHelperBlessedOrg = PackageTestBuilder.init([serverPort     : serverPort,
                                                                           attorneyService: attorneyService,
                                                                           profileService: profileService])
        testHelperLawFirm.buildPackageLegalRep(true)
                .buildPetitionerAndBeneficiaryClosedPackage()
                .buildNoPackageLegalRep(true)
                .buildSoloOrganization()
                .logInPackageLegalRep()
        testHelperBlessedOrg.buildOrganization()

        PackageTestBuilder testHelperSoloLead = PackageTestBuilder.init(testHelperLawFirm)
        testHelperSoloLead.buildPetitionerAndBeneficiaryLeadPackageSoloOrg()
        PackageTestBuilder testHelperSoloOpen = PackageTestBuilder.init(testHelperLawFirm)
        testHelperSoloOpen.buildPetitionerAndBeneficiaryOpenPackageSoloOrg()
        PackageTestBuilder testHelperSoloBlocked = PackageTestBuilder.init(testHelperLawFirm)
        testHelperSoloBlocked.buildPetitionerAndBeneficiaryBlockedPackageSoloOrg()
        PackageTestBuilder testHelperSoloClosed = PackageTestBuilder.init(testHelperLawFirm)
        testHelperSoloClosed.buildPetitionerAndBeneficiaryClosedPackageSoloOrg()

        Alert.withNewTransaction {
            testHelperBlessedOrg.organization.refresh().easyVisaId = Holders.config.easyvisa.blessedOrganizationEVId
            testHelperBlessedOrg.organization.save(failOnError:true, flush:true)

            LegalRepresentative representative = testHelperLawFirm.packageLegalRepresentative.refresh()
            organizationService.addAttorneyToOrganization(organizationService.blessedOrganization,representative)
            ProcessRequest processRequest = new InviteToCreateOrganizationRequest(requestedBy: representative.profile,
                    representative: representative)
            new Alert(recipient: representative.user, subject: 'test subject', processRequest: processRequest)
                    .save(failOnError: true)
            //help to prevent deleting payment method from Fattmerchant
            PaymentMethod payMethod = PaymentMethod.findByUser(testHelperLawFirm.packageLegalRepresentative.user)
            payMethod.fmPaymentMethodId = 'value'
            payMethod.save(failOnError: true)
        }

        String accessToken = testHelperLawFirm.accessTokenPackageLegalRep
        User user = testHelperLawFirm.packageLegalRepresentative.user
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('deactivate-membership',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('activeMembership').description('false - deactivate. true - reactivate')),
                responseFields(
                        fieldWithPath('id').description('id of the user'),
                        fieldWithPath('roles').description('Roles allowed to the user'),
                        fieldWithPath('accountLocked').description('is the account locked?'),
                        fieldWithPath('enabled').description('is the account enabled?'),
                        fieldWithPath('lastLogin').description('date of last successful login'),
                        subsectionWithPath('profile').description('Details of profile of user.'),
                        fieldWithPath('paid').description( 'Paid flag.'),
                        fieldWithPath('activeMembership').description('Active membership flag.')
                )))
                .header('Authorization', accessToken)
                .body('{"activeMembership": false}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('activeMembership', equalTo(false))

        //can get balance
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', accessToken)
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}/balance', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('subTotal', equalTo(BigDecimal.ZERO.intValue()))

        //can't get organization list
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', accessToken)
                .when()
                .port(this.serverPort)
                .get('/api/organizations/?status=ACTIVE')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_PAYMENT_REQUIRED))
                .body('errors.message[0]', notNullValue())
                .body('errors.errorCode[0]', equalTo('INACTIVE'))

        OrganizationEmployee organizationEmployeeLawOrg
        OrganizationEmployee organizationEmployeeSoloOrg
        PaymentMethod paymentMethod
        LegalRepresentative legalRepresentative
        User storedUser
        Set<PracticeArea> practiceAreas
        Profile profile
        Package packageLawFirmClosed
        Package packageSoloLead
        Package packageSoloOpen
        Package packageSoloBlocked
        Package packageSoloClosed
        List<Alert> alerts
        Alert adminAlert
        List<ProcessRequest> processRequests
        OrganizationEmployee.withNewTransaction {
            paymentMethod = PaymentMethod.findByUser(user)
            LegalRepresentative attorney = testHelperLawFirm.packageLegalRepresentative
            organizationEmployeeLawOrg = OrganizationEmployee.findByEmployeeAndOrganization(attorney,
                    testHelperLawFirm.organization)
            organizationEmployeeSoloOrg = OrganizationEmployee.findByEmployeeAndOrganization(attorney,
                    testHelperLawFirm.soloOrganization)
            legalRepresentative = LegalRepresentative.get(attorney.id)
            practiceAreas = legalRepresentative.practiceAreas
            practiceAreas.size()
            storedUser = User.get(user.id)
            profile = Profile.get(legalRepresentative.profile.id)
            packageLawFirmClosed = Package.get(testHelperLawFirm.aPackage.id)
            packageSoloLead = Package.get(testHelperSoloLead.aPackage.id)
            packageSoloOpen = Package.get(testHelperSoloOpen.aPackage.id)
            packageSoloBlocked = Package.get(testHelperSoloBlocked.aPackage.id)
            packageSoloClosed = Package.get(testHelperSoloClosed.aPackage.id)
            alerts = Alert.findAllByRecipient(user)
            adminAlert = Alert.findByRecipient(testHelperLawFirm.legalRepresentativeNoPackage.user)
            processRequests = ProcessRequest.findAllByRequestedBy(profile)
        }
        assert organizationEmployeeLawOrg.inactiveDate
        assert EmployeeStatus.INACTIVE == organizationEmployeeLawOrg.status
        assert !organizationEmployeeSoloOrg.inactiveDate
        assert EmployeeStatus.ACTIVE == organizationEmployeeSoloOrg.status
        assert paymentMethod == null
        assert !storedUser.activeMembership
        assert profile.email.startsWith('packageattorney')
        assert profile.email.endsWith('@easyvisa.com')
        assert 'Attorney First' == profile.firstName
        assert 'Attorney Middle' == profile.middleName
        assert 'Attorney Last' == profile.lastName
        assert '99999123123' == legalRepresentative.mobilePhone
        assert 1 == practiceAreas.size()
        assert practiceAreas.contains(PracticeArea.BUSINESS)
        assert PackageStatus.CLOSED == packageLawFirmClosed.status
        assert PackageStatus.LEAD == packageSoloLead.status
        assert PackageStatus.CLOSED == packageSoloOpen.status
        assert PackageStatus.CLOSED == packageSoloBlocked.status
        assert PackageStatus.CLOSED == packageSoloClosed.status
        Calendar today = Calendar.instance
        Calendar realDate = Calendar.instance
        realDate.setTime(packageSoloClosed.closed)
        assert realDate.before(today)
        assert 1 == alerts.size()
        Alert alert = alerts.first()
        assert 'test subject' == alert.subject
        assert ProcessRequestState.PENDING == processRequests.first().state
        assert adminAlert != null
        assert EasyVisaSystemMessageType.PACKAGE_ATTORNEY_LEFT == adminAlert.messageType

        cleanup:
        LegalRepresentative.withNewTransaction {
            Alert.findAllByRecipient(user)*.delete(failOnError: true)
            ProcessRequest.findAllByRequestedBy(profile)*.delete(failOnError: true)
        }
        testHelperBlessedOrg.deleteOrganization()
        testHelperSoloLead.deletePackageOnly()
        testHelperSoloOpen.deletePackageOnly()
        testHelperSoloBlocked.deletePackageOnly()
        testHelperSoloClosed.deletePackageOnly()
        testHelperLawFirm.clean()
    }

    def deactivateMembershipAttorneyWithNonSoloOrgPackages() {
        given:
        PackageTestBuilder testHelperLawFirm = PackageTestBuilder.init([serverPort     : serverPort,
                                                                        attorneyService: attorneyService,
                                                                        packageService : packageService,
                                                                        adminService   : adminService,
                                                                        profileService: profileService])
        testHelperLawFirm.buildPetitionerAndBeneficiaryOpenPackage()
                .buildNoPackageLegalRep(true)
                .logInPackageLegalRep()

        PaymentMethod.withNewTransaction {
            //help to prevent deleting payment method from Fattmerchant
            PaymentMethod payMethod = PaymentMethod.findByUser(testHelperLawFirm.packageLegalRepresentative.user)
            payMethod.fmPaymentMethodId = 'value'
            payMethod.save(failOnError: true)
        }

        String accessToken = testHelperLawFirm.accessTokenPackageLegalRep
        User user = testHelperLawFirm.packageLegalRepresentative.user
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', accessToken)
                .body('{"activeMembership": false}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('Before canceling your membership, you must ' +
                'transfer all packages currently at your law firm to another attorney or marked them as closed.'))

        OrganizationEmployee organizationEmployeeLawOrg
        PaymentMethod paymentMethod
        LegalRepresentative legalRepresentative
        User storedUser
        Set<PracticeArea> practiceAreas
        Profile profile
        Package packageFirm
        List<Alert> alerts
        Alert adminAlert
        OrganizationEmployee.withNewTransaction {
            LegalRepresentative attorney = testHelperLawFirm.packageLegalRepresentative
            organizationEmployeeLawOrg = OrganizationEmployee.findByEmployeeAndOrganization(attorney,
                    testHelperLawFirm.organization)
            legalRepresentative = LegalRepresentative.get(attorney.id)
            practiceAreas = legalRepresentative.practiceAreas
            practiceAreas.size()
            storedUser = User.get(user.id)
            profile = Profile.get(legalRepresentative.profile.id)
            packageFirm = Package.get(testHelperLawFirm.aPackage.id)
            alerts = Alert.findAllByRecipient(user)
            adminAlert = Alert.findByRecipient(testHelperLawFirm.legalRepresentativeNoPackage.user)
            paymentMethod = PaymentMethod.findByUser(user)
        }
        assert !organizationEmployeeLawOrg.inactiveDate
        assert EmployeeStatus.ACTIVE == organizationEmployeeLawOrg.status
        assert paymentMethod
        assert storedUser.activeMembership
        assert profile.email.startsWith('packageattorney')
        assert profile.email.endsWith('@easyvisa.com')
        assert 'Attorney First' == profile.firstName
        assert 'Attorney Middle' == profile.middleName
        assert 'Attorney Last' == profile.lastName
        assert '99999123123' == legalRepresentative.mobilePhone
        assert 1 == practiceAreas.size()
        assert practiceAreas.contains(PracticeArea.BUSINESS)
        assert PackageStatus.OPEN == packageFirm.status
        assert testHelperLawFirm.packageLegalRepresentative.id == packageFirm.attorney.id
        assert 0 == alerts.size()
        assert !adminAlert

        cleanup:
        testHelperLawFirm.clean()
    }

    def deactivateMembershipDoubleCall() {
        given:

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep(true)
                .buildNoPackageLegalRep(true)
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body('{"activeMembership": false}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('activeMembership', equalTo(false))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body('{"activeMembership": false}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('Your membership is already canceled.'))

        cleanup:
        testHelper.deleteNoPackageLegalRep()
                .deletePackageLegalRep()
                .deleteOrganization()
    }

    def deactivateMembershipAloneAdmin() {
        given:

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep(true)
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body('{"activeMembership": false}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        testHelper.deletePackageLegalRep()
                .deleteOrganization()
    }

    def deleteMembershipAloneAdmin() {
        given:

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep(true)
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .delete('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        testHelper.deletePackageLegalRep()
                .deleteOrganization()
    }

    def deactivateMembershipApplicantNotAllowed() {
        given:
        Profile profile
        Package.withNewTransaction {
            User user = new User(username: 'applicanttodeactivate', language: 'En/US',
                    password: 'applicantToDeactivatePassword', accountLocked: false).save(failOnError: true)
            TestUtils.createUserRole(user, Role.USER)
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'applicanttodeactivate@easyvisa.com', easyVisaId: 'ev-app-id').save(failOnError: true)
        }

        RestResponse resp = TestUtils.logInUser(serverPort, 'applicanttodeactivate', 'applicantToDeactivatePassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .body('{"activeMembership": false}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))
                .body('errors.message[0]', notNullValue())

        cleanup:
        Profile.withNewTransaction {
            TestUtils.deleteProfile(profile.refresh())
        }
    }

    //todo unignore once tests are fixed
    @Ignore
    def deleteAttorneyAfterDeactivateMembership() {
        given:
        User user
        Organization lawOrg
        LegalRepresentative attorney
        LegalRepresentative attorney1
        Package.withNewTransaction {
            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword',
                    activeMembership: true)
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true, flush: true)

            User user1 = new User(username: 'legalreptouser1', language: 'En/US', password: 'legalreptouserPassword1',
                    activeMembership: true)
            Profile profile1 = new Profile(user: user1, lastName: 'last1', firstName: 'First1', middleName: 'middle1',
                    email: 'legalreptouser1@easyvisa.com')
            attorney1 = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile1,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney1 = attorneyService.createAttorney(attorney1)
            attorney1.save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)
            lawOrg = TestUtils.createOrganization('Employee Org', OrganizationType.LAW_FIRM)
            organizationService.addAttorneyToOrganization(lawOrg, attorney, EmployeePosition.ATTORNEY)
            ProcessRequest processRequest = new InviteToCreateOrganizationRequest(requestedBy: profile,
                    representative: attorney1)
            new Alert(recipient: user, subject: 'test subject', processRequest: processRequest).save(failOnError: true)
        }

        RestResponse resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .body('{"activeMembership": false}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('activeMembership', equalTo(false))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .when()
                .port(this.serverPort)
                .delete('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(user.id as Integer))

        //can't get organization list
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .when()
                .port(this.serverPort)
                .get('/api/organizations/?status=ACTIVE')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNAUTHORIZED))
                .body('errors.message[0]', notNullValue())

        OrganizationEmployee organizationEmployee
        LegalRepresentative legalRepresentative
        Profile profile
        User storedUser
        Set<PracticeArea> practiceAreas
        List<Alert> alerts
        List<ProcessRequest> processRequests
        OrganizationEmployee.withNewTransaction {
            organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganization(attorney, lawOrg)
            legalRepresentative = LegalRepresentative.get(attorney.id)
            storedUser = User.get(user.id)
            practiceAreas = legalRepresentative.practiceAreas
            practiceAreas.size()
            profile = Profile.get(legalRepresentative.profile.id)
            alerts = Alert.findAllByRecipient(user)
            processRequests = ProcessRequest.findAllByRequestedBy(profile)
        }
        assert organizationEmployee.inactiveDate
        assert EmployeeStatus.INACTIVE == organizationEmployee.status
        assert storedUser == null
        assert profile.user == null
        assert !legalRepresentative.profile.email
        assert !legalRepresentative.mobilePhone
        assert 'First' == profile.firstName
        assert 'middle' == profile.middleName
        assert 'last' == profile.lastName
        assert !practiceAreas
        assert !alerts
        assert 1 == processRequests.size()
        assert ProcessRequestState.CANCELLED == processRequests.first().state

        cleanup:
        LegalRepresentative.withNewTransaction {
            Alert.findAll()*.delete(failOnError: true)
            ProcessRequest.findAll()*.delete(failOnError: true)
            TestUtils.deleteRepresentative(attorney.id)
            TestUtils.deleteRepresentative(attorney1.id)
            TestUtils.deleteOrganization(lawOrg)
        }
    }

    def deleteAttorneyAccount() {
        given:
        PackageTestBuilder testHelperLawFirm = PackageTestBuilder.init([serverPort     : serverPort,
                                                                        attorneyService: attorneyService,
                                                                        packageService : packageService,
                                                                        adminService   : adminService,
                                                                        answerService  : answerService,
                                                                        profileService: profileService])
        testHelperLawFirm.buildNoPackageLegalRep(true)
                .buildPetitionerAndBeneficiaryClosedPackage()
                .buildSoloOrganization()
                .logInPackageLegalRep()
        String username = testHelperLawFirm.packageLegalRepresentative.user.username

        PackageTestBuilder testHelperSoloLead = PackageTestBuilder.init(testHelperLawFirm)
        testHelperSoloLead.buildPetitionerAndBeneficiaryLeadPackageSoloOrg()
        Address address = testHelperSoloLead.aPackage.petitioner.profile.address
        PackageTestBuilder testHelperSoloOpen = PackageTestBuilder.init(testHelperLawFirm)
        testHelperSoloOpen.buildPetitionerAndBeneficiaryOpenPackageSoloOrg()
                .addAnswersFromAttorneyUser()
                .addAlertsToPackageLegalRep()
        PackageTestBuilder testHelperSoloBlocked = PackageTestBuilder.init(testHelperLawFirm)
        testHelperSoloBlocked.buildPetitionerAndBeneficiaryBlockedPackageSoloOrg()
        PackageTestBuilder testHelperSoloClosed = PackageTestBuilder.init(testHelperLawFirm)
        testHelperSoloClosed.buildPetitionerAndBeneficiaryClosedPackageSoloOrg()

        LegalRepresentative representative = testHelperLawFirm.packageLegalRepresentative
        Alert.withNewTransaction {
            //help to prevent deleting payment method from Fattmerchant
            PaymentMethod payMethod = PaymentMethod.findByUser(representative.refresh().user)
            payMethod.fmPaymentMethodId = 'value'
            payMethod.save(failOnError: true)
        }

        String accessToken = testHelperLawFirm.accessTokenPackageLegalRep
        User user = representative.user
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('delete-account',
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath('id').description('id of the user'))))
                .header('Authorization', accessToken)
                .when()
                .port(this.serverPort)
                .delete('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(user.id as Integer))

        //can't get balance
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', accessToken)
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}/balance', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNAUTHORIZED))
                .body('errors.message[0]', notNullValue())

        //can't get organization list
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', accessToken)
                .when()
                .port(this.serverPort)
                .get('/api/organizations/?status=ACTIVE')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNAUTHORIZED))
                .body('errors.message[0]', notNullValue())

        OrganizationEmployee organizationEmployeeLawOrg
        OrganizationEmployee organizationEmployeeSoloOrg
        PaymentMethod paymentMethod
        LegalRepresentative legalRepresentative
        User storedUser
        Set<PracticeArea> practiceAreas
        Profile profile
        Package packageFirmClosed
        Package packageSoloLead
        Package packageSoloOpen
        Package packageSoloBlocked
        Package packageSoloClosed
        List<Alert> alerts
        List<Alert> adminAlerts
        List<Warning> warnings
        List<Answer> attorneyAnswers
        OrganizationEmployee.withNewTransaction {
            paymentMethod = PaymentMethod.findByUser(user)
            organizationEmployeeLawOrg = OrganizationEmployee.findByEmployeeAndOrganization(representative,
                    testHelperLawFirm.organization)
            organizationEmployeeSoloOrg = OrganizationEmployee.findByEmployeeAndOrganization(representative,
                    testHelperLawFirm.soloOrganization)
            legalRepresentative = LegalRepresentative.get(representative.id)
            practiceAreas = legalRepresentative.practiceAreas
            practiceAreas.size()
            storedUser = User.get(user.id)
            profile = Profile.get(legalRepresentative.profile.id)
            packageFirmClosed = Package.get(testHelperLawFirm.aPackage.id)
            packageSoloLead = Package.get(testHelperSoloLead.aPackage.id)
            packageSoloOpen = Package.get(testHelperSoloOpen.aPackage.id)
            packageSoloBlocked = Package.get(testHelperSoloBlocked.aPackage.id)
            packageSoloClosed = Package.get(testHelperSoloClosed.aPackage.id)
            attorneyAnswers = Answer.findAllByUpdatedBy(user)
            alerts = Alert.findAllByRecipient(user)
            adminAlerts = Alert.findAllByRecipient(testHelperLawFirm.legalRepresentativeNoPackage.user)
            warnings = Warning.findAllByAPackage(testHelperSoloOpen.aPackage)
        }
        assert organizationEmployeeLawOrg.inactiveDate
        assert EmployeeStatus.INACTIVE == organizationEmployeeLawOrg.status
        assert organizationEmployeeSoloOrg.inactiveDate
        assert EmployeeStatus.INACTIVE == organizationEmployeeSoloOrg.status
        assert !paymentMethod
        assert !storedUser
        assert !profile.email
        assert 'Attorney First' == profile.firstName
        assert 'Attorney Middle' == profile.middleName
        assert 'Attorney Last' == profile.lastName
        assert !legalRepresentative.mobilePhone
        assert !practiceAreas.size()
        assert !packageSoloLead
        assert PackageStatus.CLOSED == packageFirmClosed.status
        assert PackageStatus.CLOSED == packageSoloOpen.status
        assert PackageStatus.CLOSED == packageSoloBlocked.status
        assert PackageStatus.CLOSED == packageSoloClosed.status
        Calendar today = Calendar.instance
        Calendar realDate = Calendar.instance
        realDate.setTime(packageSoloClosed.closed)
        assert realDate.before(today)
        assert attorneyAnswers.empty
        assert alerts.empty
        assert !adminAlerts.empty
        assert EasyVisaSystemMessageType.PACKAGE_ATTORNEY_LEFT == adminAlerts.first().messageType
        assert 1 == warnings.size()

        cleanup:
        RegistrationCode.withNewTransaction {
            RegistrationCode.findByUsername(username)?.delete(failOnError:true)
            address?.delete(failOnError:true)
        }
        testHelperSoloOpen.deletePackageOnly()
        testHelperSoloBlocked.deletePackageOnly()
        testHelperSoloClosed.deletePackageOnly()
        testHelperLawFirm.clean()
    }

    def deleteAttorneyAccountWithNonSoloOrgPackages() {
        given:
        PackageTestBuilder testHelperLawFirmLead = PackageTestBuilder.init([serverPort     : serverPort,
                                                                            attorneyService: attorneyService,
                                                                            packageService : packageService,
                                                                            adminService   : adminService,
                                                                            answerService  : answerService,
                                                                            profileService: profileService])
        testHelperLawFirmLead.buildPetitionerAndBeneficiaryOpenPackage()
                .buildNoPackageLegalRep(true)
                .addAnswersFromAttorneyUser()
                .logInPackageLegalRep()
        LegalRepresentative representative = testHelperLawFirmLead.packageLegalRepresentative

        String accessToken = testHelperLawFirmLead.accessTokenPackageLegalRep
        User user = representative.user
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', accessToken)
                .when()
                .port(this.serverPort)
                .delete('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('Before canceling your membership, you must ' +
                'transfer all packages currently at your law firm to another attorney or marked them as closed.'))

        OrganizationEmployee organizationEmployeeLawOrg
        PaymentMethod paymentMethod
        LegalRepresentative legalRepresentative
        User storedUser
        Set<PracticeArea> practiceAreas
        Profile profile
        Package packageFirmLead
        List<Alert> alerts
        List<Warning> warnings
        List<Answer> attorneyAnswers
        OrganizationEmployee.withNewTransaction {
            paymentMethod = PaymentMethod.findByUser(user)
            organizationEmployeeLawOrg = OrganizationEmployee.findByEmployeeAndOrganization(representative,
                    testHelperLawFirmLead.organization)
            legalRepresentative = LegalRepresentative.get(representative.id)
            practiceAreas = legalRepresentative.practiceAreas
            practiceAreas.size()
            storedUser = User.get(user.id)
            profile = Profile.get(legalRepresentative.profile.id)
            packageFirmLead = Package.get(testHelperLawFirmLead.aPackage.id)
            attorneyAnswers = Answer.findAllByUpdatedBy(user)
            alerts = Alert.findAllByRecipient(user)
            warnings = Warning.findAllByAPackage(testHelperLawFirmLead.aPackage)
        }
        assert !organizationEmployeeLawOrg.inactiveDate
        assert EmployeeStatus.ACTIVE == organizationEmployeeLawOrg.status
        assert paymentMethod
        assert storedUser
        assert profile.email
        assert 'Attorney First' == profile.firstName
        assert 'Attorney Middle' == profile.middleName
        assert 'Attorney Last' == profile.lastName
        assert '99999123123' == legalRepresentative.mobilePhone
        assert 1 == practiceAreas.size()
        assert PackageStatus.OPEN == packageFirmLead.status
        assert !attorneyAnswers.empty
        assert alerts.empty
        assert 1 == warnings.size()

        cleanup:
        testHelperLawFirmLead.clean()
    }

    def deleteApplicantAccount() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort         : serverPort,
                                                                 attorneyService    : attorneyService,
                                                                 packageService     : packageService,
                                                                 adminService       : adminService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()

        Long packageId = testHelper.aPackage.id
        Petitioner petitioner = testHelper.aPackage.petitioner
        Answer.withNewTransaction {
            def answerList = AnswerListPdfRulesStub.otherNamesUsed(packageId, petitioner.applicant.id)
            QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, packageId, petitioner.applicant.id, '',
                    answerList)
        }

        String accessTokenPetitioner = testHelper.accessTokenPetitioner
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', accessTokenPetitioner)
                .when()
                .port(this.serverPort)
                .delete('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(petitioner.applicant.user.id as Integer))

        //can't get balance
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', accessTokenPetitioner)
                .when()
                .port(this.serverPort)
                .get('/api/profile')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNAUTHORIZED))
                .body('errors.message[0]', notNullValue())

        TestUtils.delayCurrentThread(67000)
        User storedUser
        Profile profile
        Package aPackage
        List<Answer> answers
        List<UserRole> roles
        Warning warning
        OrganizationEmployee.withNewTransaction {
            storedUser = User.get(petitioner.profile.user.id)
            profile = Profile.get(petitioner.profile.id)
            answers = Answer.findAllByApplicantId(petitioner.applicant.id)
            roles = UserRole.findAllByUser(petitioner.profile.user)
            aPackage = Package.get(packageId)
            warning = Warning.findByAPackage(aPackage)
        }
        assert !storedUser
        assert 11 == answers.findAll { it.value }.size()
        assert !roles
        assert profile.email
        assert 'petitioner-first' == profile.firstName
        assert !profile.middleName
        assert 'petitioner-last' == profile.lastName
        assert PackageStatus.OPEN == aPackage.status
        EasyVisaSystemMessageType warningMessageType = EasyVisaSystemMessageType.PACKAGE_APPLICANT_DELETION
        assert warningMessageType == warning.messageType
        assert warning.subject == String.format(warningMessageType.subject, petitioner.name)

        cleanup:
        testHelper.clean()
    }

    def reactivateMembershipAttorney() {
        given:
        User user
        LegalRepresentative attorney
        BigDecimal reactivationFee = TestUtils.randomNumber()
        Profile profile
        Package.withNewTransaction {
            AdminSettings adminSettings = adminService.adminSettingsForUpdate
            adminSettings.adminConfig.membershipReactivationFee = reactivationFee
            adminSettings.save(failOnError: true)

            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword',
                    activeMembership: false)
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(attorneyType: AttorneyType.SOLO_PRACTITIONER, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)

            TestUtils.addPaidAccountTransaction(profile)
        }

        RestResponse resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')

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
    "customerId": "${CUSTOMER_ID}",
    "fmPaymentMethodId": "token"
}""")
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .when()
                .port(this.serverPort)
                .put('/api/users/{id}/payment-method', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .body('{"activeMembership": true}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('activeMembership', equalTo(true))

        //can get balance
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}/balance', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('subTotal', equalTo(BigDecimal.ZERO.intValue()))

        //can't get organization list
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .when()
                .port(this.serverPort)
                .get('/api/organizations/?status=ACTIVE')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        User storedUser
        List<AccountTransaction> transactions
        OrganizationEmployee.withNewTransaction {
            storedUser = User.get(user.id)
            transactions = AccountTransaction.findAllByProfile(profile)
        }
        assert storedUser.activeMembership
        Calendar now = Calendar.instance
        Calendar reactivationCalendar = Calendar.instance
        reactivationCalendar.setTime(storedUser.reactivationDate)
        assert now.get(Calendar.YEAR) == reactivationCalendar.get(Calendar.YEAR)
        assert now.get(Calendar.MONTH) == reactivationCalendar.get(Calendar.MONTH)
        assert now.get(Calendar.DAY_OF_MONTH) == reactivationCalendar.get(Calendar.DAY_OF_MONTH)
        TestUtils.assertPaidAccountTransaction(transactions, reactivationFee)
        TestUtils.assertCustomAccountTransactions(transactions, reactivationFee, TransactionSource.REACTIVATION,
                'Membership Reactivation Fee.')

        cleanup:
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    def reactivateMembershipAttorneyForActiveUser() {
        given:
        User user
        LegalRepresentative attorney
        Profile profile
        Package.withNewTransaction {
            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword',
                    activeMembership: true)
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(attorneyType: AttorneyType.SOLO_PRACTITIONER, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            new PaymentMethod(user: user, fmPaymentMethodId: 'token',
                    cardExpiration: '12' + (LocalDate.now().year + 3)).save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)
        }

        RestResponse resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .body('{"activeMembership": true}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('Your membership is already active.'))

        User storedUser
        List<AccountTransaction> transactions
        OrganizationEmployee.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert storedUser.activeMembership
        assert !transactions

        cleanup:
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    def reactivateMembershipAttorneyExpiredCard() {
        given:
        User user
        LegalRepresentative attorney
        Profile profile
        Package.withNewTransaction {
            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword',
                    activeMembership: false)
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(attorneyType: AttorneyType.SOLO_PRACTITIONER, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            LocalDate date = LocalDate.now().minusMonths(1)
            String monthValue = date.monthValue
            if (monthValue.size() == 1) {
                monthValue = "0${monthValue}"
            }
            new PaymentMethod(user: user, fmPaymentMethodId: 'token',
                    cardExpiration: "${monthValue}${date.year}").save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)
        }

        RestResponse resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .body('{"activeMembership": true}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('You first must add a payment method on the screen' +
                ' “My Account > Payments and Fee Schedule.”'))

        User storedUser
        List<AccountTransaction> transactions
        OrganizationEmployee.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert !storedUser.activeMembership
        assert !transactions

        cleanup:
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    def reactivateMembershipAttorneyNoPaymentMethod() {
        given:
        User user
        LegalRepresentative attorney
        Profile profile
        Package.withNewTransaction {
            user = new User(username: 'legalreptouser', language: 'En/US', password: 'legalreptouserPassword',
                    activeMembership: false)
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'legalreptouser@easyvisa.com')
            attorney = new LegalRepresentative(attorneyType: AttorneyType.SOLO_PRACTITIONER, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

            user.accountLocked = false
            user.save(failOnError: true)
        }

        RestResponse resp = TestUtils.logInUser(serverPort, 'legalreptouser', 'legalreptouserPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer ${resp.json['access_token']}")
                .body('{"activeMembership": true}')
                .when()
                .port(this.serverPort)
                .patch('/api/users/me')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('You first must add a payment method on the screen' +
                ' “My Account > Payments and Fee Schedule.”'))

        User storedUser
        List<AccountTransaction> transactions
        OrganizationEmployee.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert !storedUser.activeMembership
        assert !transactions

        cleanup:
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

}
