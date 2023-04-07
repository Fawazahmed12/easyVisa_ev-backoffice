package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.*
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
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
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class OrganizationControllerSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort


    @Autowired
    AttorneyService attorneyService
    @Autowired
    OrganizationService organizationService
    @Autowired
    PackageService packageService
    @Autowired
    ProfileService profileService
    @Autowired
    AdminService adminService

    protected RequestSpecification spec
    protected ResponseFieldsSnippet organizationFields

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.organizationFields = responseFields(
                fieldWithPath('id').description('database Id of organization'),
                fieldWithPath('name').description('name of organization'),
                fieldWithPath('organizationType').description('Organization type'),
                fieldWithPath('summary').description('profile summary for organization'),
                fieldWithPath('awards').description('awards for organization'),
                fieldWithPath('experience').description('experience for organization'),
                fieldWithPath('officeAddress').description('office address for organization'),
                fieldWithPath('officePhone').description('officePhone for organization'),
                fieldWithPath('mobilePhone').description('mobilePhone for organization'),
                fieldWithPath('faxNumber').description('faxNumber for organization'),
                fieldWithPath('email').description('contact email for organization'),
                fieldWithPath('facebookUrl').description('facebook link for organization'),
                fieldWithPath('twitterUrl').description('twitter link for organization'),
                fieldWithPath('youtubeUrl').description('youtube link for organization'),
                fieldWithPath('linkedinUrl').description('linkedIn link for organization'),
                fieldWithPath('websiteUrl').description('website link for organization'),
                fieldWithPath('yearFounded').description('Year organization was founded'),
                fieldWithPath('easyVisaId').description('Easyvisa Id for organization'),
                fieldWithPath('languages').description('Languages spoken '),
                fieldWithPath('practiceAreas').description('practice areas for organization'),
                fieldWithPath('profilePhoto').description('Profile url for organization'),
                subsectionWithPath('workingHours').description('List of working hours for organization')
        )
    }


    @Ignore
    def "test get organization employees"() {
        given:
        LegalRepresentative attorney
        Organization organization
        RestResponse resp
        Employee.withNewTransaction {
            User user = new User(username: 'findpackageattorney', language: 'En/US', password: 'registeredAttorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true)

            organization = organizationService.create("Org ${attorney.profile.fullName}", OrganizationType.SOLO_PRACTICE)
            def organizationEmployee = organizationService.addAttorneyToOrganization(organization, attorney)
            organizationEmployee.save(failOnError: true)
            attorney.save(failOnError: true)
        }

        resp = TestUtils.logInUser(serverPort, 'findpackageattorney', 'registeredAttorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('get-organization-employees',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Organization id')
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/organization/{id}/employees', organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", equalTo(1))

        cleanup:
        if (attorney) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteRepresentative(attorney.id)
            }
        }
    }

    def "test get organization representatives"() {
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
                .filter(document('get-organization-representatives',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Organization id')
                        ),
                        requestParameters(
                                parameterWithName('view').description('view option to get a different view of this data, if view = menu then it only returns needed json for Menu').optional(),
                                parameterWithName('includeInactive').description('if it is passed as true, includes INACTIVE representatives too').optional()
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/organizations/{id}/representatives?view=menu&includeInactive=true', testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    def "test get organization data"() {
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
                .filter(document('get-organization',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Organization id')
                        ), this.organizationFields))
                .when()
                .port(this.serverPort)
                .get('/api/organizations/{id}', testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('name', equalTo(testHelper.organization.name))

        cleanup:
        testHelper.clean()
    }

    def "test update organization"() {
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
                .filter(document('edit-organization',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Organization id')
                        ), requestFields(
                        fieldWithPath('name').description('name for organization').optional().type(String),
                        fieldWithPath('summary').description('profile summary for organization').optional().type(String),
                        fieldWithPath('awards').description('awards for organization').optional().type(String),
                        fieldWithPath('experience').description('experience for organization').optional().type(String),
                        fieldWithPath('officeAddress').description('office address for organization').optional().type(Object),
                        fieldWithPath('officePhone').description('officePhone for organization').optional().type(String),
                        fieldWithPath('mobilePhone').description('mobilePhone for organization').optional().type(String),
                        fieldWithPath('faxNumber').description('faxNumber for organization').optional().type(String),
                        fieldWithPath('email').description('contact email for organization').optional().type(String),
                        fieldWithPath('facebookUrl').description('facebook link for organization').optional().type(String),
                        fieldWithPath('twitterUrl').description('twitter link for organization').optional().type(String),
                        fieldWithPath('youtubeUrl').description('youtube link for organization').optional().type(String),
                        fieldWithPath('linkedinUrl').description('linkedIn link for organization').optional().type(String),
                        fieldWithPath('websiteUrl').description('website link for organization').optional().type(String),
                        fieldWithPath('yearFounded').description('Year organization was founded').optional().type(Long),
                        fieldWithPath('languages').description('Languages spoken ').optional().type(List),
                        fieldWithPath('practiceAreas').description('practice areas for organization').optional().type(List),
                        fieldWithPath('workingHours').description('List of working hours for organization').optional().type(List),
                        fieldWithPath('workingHours[].dayOfWeek').description('Day of week').optional(),
                        fieldWithPath('workingHours[].start').description('Start').optional(),
                        fieldWithPath('workingHours[].start.hour').description('Hour').optional(),
                        fieldWithPath('workingHours[].start.minutes').description('Minutes').optional(),
                        fieldWithPath('workingHours[].end').description('End').optional(),
                        fieldWithPath('workingHours[].end.hour').description('Hour').optional(),
                        fieldWithPath('workingHours[].end.minutes').description('Minutes').optional(),
                ), this.organizationFields))
                .body('''
{"facebookUrl":"http://facebook.com/myorg-updated",
"twitterUrl":"http://twitter.com/myorg-updated",
"awards":"awards of organization",
"experience":"experience of organization",
"workingHours":[{"dayOfWeek":"MONDAY", "start":{"hour":8,"minutes":0},"end":{"hour":18,"minutes":30}}]
}
''')
                .when()
                .port(this.serverPort)
                .put('/api/organizations/{id}', testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('name', equalTo(testHelper.organization.name))
                .body('id', equalTo(testHelper.organization.id as int))
                .body('facebookUrl', equalTo('http://facebook.com/myorg-updated'))
                .body('twitterUrl', equalTo('http://twitter.com/myorg-updated'))
                .body('awards', equalTo('awards of organization'))
                .body('experience', equalTo('experience of organization'))
                .body('workingHours[0].dayOfWeek', equalTo('MONDAY'))
                .body('workingHours[0].start', equalTo([hour: 8, minutes: 0]))
                .body('workingHours[0].end', equalTo([hour: 18, minutes: 30]))

        cleanup:
        testHelper.clean()
    }

    /**
     * Test invite creation for both Admin and non-admin user
     * @return
     */
    @Unroll
    def "test can create Organization invite #label"() {
        given:
        Organization organization
        RestResponse resp
        User user
        LegalRepresentative requester
        LegalRepresentative invitee

        String reqEmailRand = "requesterAttorney${TestUtils.randomNumber()}@easyvisa.com"
        String invEmailRand = "inviteeAttorney${TestUtils.randomNumber()}@easyvisa.com"
        Organization.withNewTransaction {
            user = new User(username: 'requesterattorney', language: 'En/US', password: 'attorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: reqEmailRand)
            requester = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            requester = attorneyService.createAttorney(requester)
            user = requester.user
            user.accountLocked = false
            user.save(failOnError: true)
            requester.registrationStatus = RegistrationStatus.COMPLETE
            requester.save(failOnError: true)


            User user2 = new User(username: 'inviteattorney', language: 'En/US', password: 'attorneyPassword')
            Profile profile2 = new Profile(user: user2, lastName: 'last', firstName: 'First', middleName: 'middle', email: invEmailRand)
            invitee = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile2, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            invitee = attorneyService.createAttorney(invitee)
            user2 = invitee.user
            user2.accountLocked = false
            user2.save(failOnError: true)
            invitee.registrationStatus = RegistrationStatus.COMPLETE
            invitee.save(failOnError: true)


            user.accountLocked = false
            user.save(failOnError: true)
            organization = organizationService.create("My Org", OrganizationType.LAW_FIRM)
            organization = organization.save(failOnError: true)
            organizationService.addAttorneyToOrganization(organization, requester, null, isAdmin)
        }

        resp = TestUtils.logInUser(serverPort, 'requesterattorney', 'attorneyPassword')
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('join-organization-invite',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Organization id')
                        ),
                        requestFields(
                                fieldWithPath('evId').description('Easy Visa ID of employee to be invited').type(String),
                                fieldWithPath('email').description('Email of employee to be invited').type(String),
                        )))
                .body("""
                    {"evId":"${invitee.profile.easyVisaId}", "email" : "$invitee.profile.email"
                    }
                    """)
                .when()
                .port(this.serverPort)
                .put("/api/organizations/{id}/invitation", organization.id)
                .then()
                .assertThat()
                .statusCode(is(callResponse))

        cleanup:
        if (organization) {
            Organization.withNewTransaction {
                Alert.findAllByProcessRequest(ProcessRequest.last())*.delete(failOnError: true)
                InviteToOrganizationRequest.list()*.delete(failOnError: true)
                TestUtils.deleteOrganization(Organization.get(organization.id))
                TestUtils.deleteRepresentative(requester.id)
                TestUtils.deleteRepresentative(invitee.id)
            }
        }

        where:
        label            | isAdmin | callResponse
        "Admin User"     | true    | HttpStatus.SC_OK
        "Non Admin User" | false   | HttpStatus.SC_FORBIDDEN
    }


    @Unroll
    def "test can create Organization joining request #label"() {
        given:
        Organization adminOrg
        RestResponse resp

        LegalRepresentative requester
        LegalRepresentative adminAttorney
        Organization.withNewTransaction {
            User user = new User(username: 'requestertestattorney', language: 'En/US', password: 'attorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'requesterAttorneyprofile@easyvisa.com')
            requester = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            requester = attorneyService.createAttorney(requester)
            user = requester.user
            user.accountLocked = false
            user.enabled = true
            user.paid = true
            user.save()
            requester.registrationStatus = RegistrationStatus.COMPLETE
            requester.save()

            User adminUser = new User(username: 'orgadminattornery', language: 'En/US', password: 'attorneyPassword')
            Profile adminProfile = new Profile(user: adminUser, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'adminAttorneryprofile@easyvisa.com')
            adminAttorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: adminProfile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            adminAttorney = attorneyService.createAttorney(adminAttorney)
            adminUser = adminAttorney.user
            adminUser.accountLocked = false
            adminUser.save(failOnError: true)
            adminAttorney.registrationStatus = RegistrationStatus.COMPLETE
            adminAttorney.save()

            adminOrg = organizationService.create("My Org", OrganizationType.LAW_FIRM)
            organizationService.addAttorneyToOrganization(adminOrg, adminAttorney, null, isAdmin)
        }

        resp = TestUtils.logInUser(serverPort, 'requestertestattorney', 'attorneyPassword')

        expect:
        if (isAdmin) {
            // I dont like this but could not find a better way to have conditional responseFields
            given(this.spec)
                    .accept(MediaType.APPLICATION_JSON.toString())
                    .contentType(MediaType.APPLICATION_JSON.toString())
                    .header("Authorization", "Bearer " + resp.json.access_token)
                    .filter(document('join-organization-request',
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath('email').description('Email of an admin in the organization'),
                            ),
                            pathParameters(
                                    parameterWithName('evId').description('EV ID of the organization')
                            ),
                            responseFields(fieldWithPath('requestId').description('Request ID for the join request created'),
                                    fieldWithPath('lastName').description('Last name of employee on join request'),
                                    fieldWithPath('middleName').description('Middle name of employee on join request'),
                                    fieldWithPath('firstName').description('First name of employee on join request'),
                                    fieldWithPath('organizationName').description('Name of organization for which join request is made'),
                                    fieldWithPath('organizationId').description('EV ID of organization for which join request is made'),
                                    fieldWithPath('easyVisaId').description('EV ID employee on join request'))))
                    .body("""
                {
                "email": "${adminAttorney.profile.email}"
                }
                """)
                    .when()
                    .port(this.serverPort)
                    .put('/api/organizations/{evId}/join-request', adminOrg.easyVisaId)
                    .then()
                    .assertThat()
                    .statusCode(is(callResponse))

        } else {
            given(this.spec)
                    .accept(MediaType.APPLICATION_JSON.toString())
                    .contentType(MediaType.APPLICATION_JSON.toString())
                    .header("Authorization", "Bearer " + resp.json.access_token)
                    .filter(document('join-organization-request',
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath('email').description('Email of an admin in the organization'),
                            ),
                            pathParameters(
                                    parameterWithName('evId').description('EV ID of the organization')
                            )))
                    .body("""
                {
                "email": "${adminAttorney.profile.email}"
                }
                """)
                    .when()
                    .port(this.serverPort)
                    .put('/api/organizations/{evId}/join-request', adminOrg.easyVisaId)
                    .then()
                    .assertThat()
                    .statusCode(is(callResponse))

        }


        cleanup:
        if (adminOrg) {
            Organization.withNewTransaction {
                Alert.findAllByProcessRequest(ProcessRequest.last())*.delete(failOnError: true)
                JoinOrganizationRequest.list()*.delete(failOnError: true)
                TestUtils.deleteRepresentative(requester.id)

                TestUtils.deleteOrganization(Organization.get(adminOrg.id))
                TestUtils.deleteRepresentative(adminAttorney.id)
            }
        }

        where:
        label            | isAdmin | callResponse
        "Admin User"     | true    | HttpStatus.SC_OK
        "Non Admin User" | false   | HttpStatus.SC_FORBIDDEN
    }

    def testAttorneyLeavesLawFirm() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(true)
                .buildSoloOrganization()
                .buildNoPackageLegalRep(true)
                .buildPetitionerAndBeneficiaryClosedPackage()
                .logInPackageLegalRep()
        PackageTestBuilder testExtraHelper = PackageTestBuilder.init(testHelper)
        testExtraHelper.buildNoPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('leave-organization',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Organization id')
                        )))
                .when()
                .port(this.serverPort)
                .post('/api/organizations/{id}/leave', testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        OrganizationEmployee organizationEmployee
        List<Package> packages
        List<PackageAssignee> assignees
        Package firstPackage
        OrganizationEmployee.withNewTransaction {
            organizationEmployee = OrganizationEmployee.findByEmployeeAndStatus(testHelper.packageLegalRepresentative,
                    EmployeeStatus.ACTIVE)
            packages = Package.findAllByAttorney(testHelper.packageLegalRepresentative)
            firstPackage = packages.first()
            firstPackage.organization.id
            assignees = firstPackage.orderedAssignees
            assignees.first().status
            organizationEmployee.organization.id
        }
        assert testHelper.soloOrganization.id == organizationEmployee.organization.id
        assert 1 == packages.size()
        assert 1 == assignees.size()
        assert testHelper.organization.id == firstPackage.organization.id
        assert PackageAssignmentStatus.INACTIVE == assignees.first().status

        cleanup:
        testExtraHelper.deleteNoPackageLegalRep()
        testHelper.clean()
    }

    //todo Just a marker to Unignore once test is fixed
    @Ignore
    def testAttorneyLeavesLawFirmToSolo() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(true)
                .buildSoloOrganization()
                .buildNoPackageLegalRep(true)
                .buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()
        PackageTestBuilder testExtraHelper = PackageTestBuilder.init(testHelper)
        testExtraHelper.packageLegalRepresentative = testHelper.legalRepresentativeNoPackage
        testExtraHelper.soloOrganization = null
        testExtraHelper
                .buildPackageLegalRep()
                .buildPetitionerAndBeneficiaryLeadPackage()
                .buildSoloOrganization()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .post('/api/organizations/{id}/leave', testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        OrganizationEmployee organizationEmployeeFirst
        List<Package> packagesFirst
        List<PackageAssignee> assigneesFirst
        OrganizationEmployee organizationEmployeeSecond
        List<Package> packagesSecond
        List<PackageAssignee> assigneesSecond
        List<Package> lawFirmPacks
        OrganizationEmployee.withNewTransaction {
            lawFirmPacks = Package.findAllByOrganization(testHelper.organization)
            organizationEmployeeFirst = OrganizationEmployee.findByEmployeeAndStatus(testHelper.packageLegalRepresentative,
                    EmployeeStatus.ACTIVE)
            packagesFirst = Package.findAllByAttorney(testHelper.packageLegalRepresentative)
            assigneesFirst = packagesFirst.first().orderedAssignees
            assigneesFirst.first().organization.id
            organizationEmployeeSecond = OrganizationEmployee.findByEmployeeAndStatus(testHelper.legalRepresentativeNoPackage,
                    EmployeeStatus.ACTIVE)
            organizationEmployeeSecond.organization.id
            packagesSecond = Package.findAllByAttorney(testHelper.legalRepresentativeNoPackage)
            assigneesSecond = packagesSecond.first().orderedAssignees
            organizationEmployeeFirst.organization.id
        }
        assert lawFirmPacks.empty

        assert testHelper.soloOrganization.id == organizationEmployeeFirst.organization.id
        assert 1 == packagesFirst.size()
        assert 2 == assigneesFirst.size()
        PackageAssignee firstAssignment = assigneesFirst.first()
        assert PackageAssignmentStatus.INACTIVE == firstAssignment.status
        assert testHelper.organization.id == firstAssignment.organization.id
        PackageAssignee secondAssignment = assigneesFirst.last()
        assert PackageAssignmentStatus.ACTIVE == secondAssignment.status
        assert testHelper.soloOrganization.id == secondAssignment.organization.id

        assert testExtraHelper.soloOrganization.id == organizationEmployeeSecond.organization.id
        assert 1 == packagesSecond.size()
        assert 2 == assigneesSecond.size()
        PackageAssignee thirdAssignment = assigneesSecond.first()
        assert PackageAssignmentStatus.INACTIVE == thirdAssignment.status
        assert testHelper.organization.id == thirdAssignment.organization.id
        PackageAssignee fourthAssignment = assigneesSecond.last()
        assert PackageAssignmentStatus.ACTIVE == fourthAssignment.status
        assert testExtraHelper.soloOrganization.id == fourthAssignment.organization.id

        cleanup:

        testExtraHelper.deletePackageOnly()
                .deleteSoloOrg()

        testHelper.deletePackage()
                .deleteNoPackageLegalRep()
                .deleteOrganization()
    }

    def testAttorneyLeavesLawFirmAloneAdmin() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(true)
                .buildSoloOrganization()
                .buildNoPackageLegalRep()
                .buildPetitionerAndBeneficiaryClosedPackage()
                .logInPackageLegalRep()
        PackageTestBuilder testExtraHelper = PackageTestBuilder.init(testHelper)
        testExtraHelper.buildNoPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .post('/api/organizations/{id}/leave', testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))
                .body('errors[0].type', equalTo(ErrorMessageType.ALONE_ADMIN.name()))

        OrganizationEmployee organizationEmployee
        List<Package> packages
        Set<PackageAssignee> assignees
        OrganizationEmployee.withNewTransaction {
            organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganization(testHelper.packageLegalRepresentative,
                    testHelper.organization)
            packages = Package.findAllByAttorney(testHelper.packageLegalRepresentative)
            packages.first().organization.id
            assignees = packages.first().orderedAssignees
            assignees.first().status
        }
        assert EmployeeStatus.ACTIVE == organizationEmployee.status
        assert 1 == packages.size()
        assert 1 == assignees.size()
        assert testHelper.organization.id == packages.first().organization.id
        assert PackageAssignmentStatus.ACTIVE == assignees.first().status

        cleanup:
        testExtraHelper.deleteNoPackageLegalRep()
        testHelper.clean()
    }

    def testAttorneyLeavesSolo() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(true)
                .buildSoloOrganization()
                .buildNoPackageLegalRep()
                .buildPetitionerAndBeneficiaryClosedPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .post('/api/organizations/{id}/leave', testHelper.soloOrganization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))
                .body('errors[0].type', nullValue())

        OrganizationEmployee organizationEmployee
        List<Package> packages
        Set<PackageAssignee> assignees
        OrganizationEmployee.withNewTransaction {
            organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganization(testHelper.packageLegalRepresentative,
                    testHelper.soloOrganization)
            packages = Package.findAllByAttorney(testHelper.packageLegalRepresentative)
            packages.first().organization.id
            assignees = packages.first().orderedAssignees
            assignees.first().status
        }
        assert EmployeeStatus.ACTIVE == organizationEmployee.status
        assert 1 == packages.size()
        assert 1 == assignees.size()
        assert testHelper.organization.id == packages.first().organization.id
        assert PackageAssignmentStatus.ACTIVE == assignees.first().status

        cleanup:
        testHelper.clean()
    }

    def testAttorneyLeavesNotActiveInOrg() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(true)
                .buildSoloOrganization()
                .buildNoPackageLegalRep()
                .buildPetitionerAndBeneficiaryClosedPackage()
                .logInPackageLegalRep()
        PackageTestBuilder testExtraHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                      attorneyService: attorneyService,
                                                                      packageService : packageService,
                                                                      profileService : profileService])
        testExtraHelper.buildPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .post('/api/organizations/{id}/leave', testExtraHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors[0].type', nullValue())

        OrganizationEmployee organizationEmployee
        List<Package> packages
        Set<PackageAssignee> assignees
        OrganizationEmployee.withNewTransaction {
            organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganization(testHelper.packageLegalRepresentative,
                    testHelper.organization)
            packages = Package.findAllByAttorney(testHelper.packageLegalRepresentative)
            packages.first().organization.id
            assignees = packages.first().orderedAssignees
            assignees.first().status
        }
        assert EmployeeStatus.ACTIVE == organizationEmployee.status
        assert 1 == packages.size()
        assert 1 == assignees.size()
        assert testHelper.organization.id == packages.first().organization.id
        assert PackageAssignmentStatus.ACTIVE == assignees.first().status

        cleanup:
        testExtraHelper.clean()
        testHelper.clean()
    }

    def testNormalEmployeeLeave() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildNoPackageLegalRep()
                .buildTrainee()
                .logInTrainee()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenTrainee)
                .port(this.serverPort)
                .post('/api/organizations/{id}/leave', testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    def "can validate an attorney by email and easyvisaId before sending an invite"() {
        given:
        RestBuilder rest = new RestBuilder()
        LegalRepresentative attorney
        RestResponse resp
        Organization organization
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
            organization = organizationService.create("My Org", OrganizationType.SOLO_PRACTICE)
            organization = organization.save(failOnError: true)
            attorney.addToLinkedOrganizations(organization)
            attorney.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'registeredattorney', 'registeredAttorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('validate-attorney',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('email').description('Email of attorney'),
                                fieldWithPath('easyVisaId').description('EasyVisaId of attorney'),
                        ), responseFields(
                        fieldWithPath('representativeId').description('Id of the representative'),
                        fieldWithPath('firstName').description('First name of the representative'),
                        fieldWithPath('middleName').description('Middle name of the representative'),
                        fieldWithPath('lastName').description('Last name of the representative'))))
                .body("""
{
"email": "${attorney.profile.email}",
"easyVisaId": "${attorney.profile.easyVisaId}"
}
""")
                .when()
                .port(this.serverPort)
                .post("/api/organizations/${organization.id}/validate-invite-member")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("representativeId", equalTo(attorney.id as int))
        cleanup:
        if (attorney) {
            Organization.withNewTransaction {
                TestUtils.deleteRepresentative(attorney.id)
                TestUtils.deleteOrganization(organization)
            }
        }
    }

    def "test getOrganizationEmployees"() {
        given:
        List<LegalRepresentative> list = []
        Organization organization

        Employee.withNewTransaction {
            User user = new User(username: 'findpackageattorney', language: 'En/US', password: 'registeredAttorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            LegalRepresentative attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true)
            organization = organizationService.create("Org ${attorney.profile.fullName}", OrganizationType.SOLO_PRACTICE)
            organizationService.addAttorneyToOrganization(organization, attorney)
            organization.save(failOnError: true)
            attorney.save(failOnError: true)
            list.add(attorney)
        }

        Employee.withNewTransaction {
            (1..5).each {
                User user = new User(username: "findpackageattorney${it}", language: 'En/US', password: 'registeredAttorneyPassword')
                Profile profile = new Profile(user: user, lastName: "last-${it}", firstName: "First-${it}", middleName: 'middle', email: "registeredAttorney-${it}@easyvisa.com")
                LegalRepresentative a = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: "99999123123${it}", practiceAreas: [PracticeArea.BUSINESS])
                a = attorneyService.createAttorney(a)
                user = a.user
                user.accountLocked = false
                user.save(failOnError: true)
                a.registrationStatus = RegistrationStatus.COMPLETE
                a.save(failOnError: true)

                if ([1, 2].contains(it)) {
                    new OrganizationEmployee(organization: organization, employee: a, status: EmployeeStatus.PENDING, position: EmployeePosition.ATTORNEY).save(failOnError: true)
                } else if ([3, 4].contains(it)) {
                    new OrganizationEmployee(organization: organization, employee: a, status: EmployeeStatus.INACTIVE, position: EmployeePosition.ATTORNEY).save(failOnError: true)
                } else {
                    new OrganizationEmployee(organization: organization, employee: a, status: EmployeeStatus.ACTIVE, position: EmployeePosition.ATTORNEY).save(failOnError: true)
                }
                list.add(a)
            }
        }

        RestResponse resp = TestUtils.logInUser(serverPort, 'findpackageattorney', 'registeredAttorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('organization-employee-permissions',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('includeAll').description('if true includes inactive and pending employees else includes only active employees').optional(),
                                parameterWithName('offset').description('Start index').optional(),
                                parameterWithName('max').description('Max no. of records').optional(),
                                parameterWithName('sort').description('Column name (name/status) to sort').optional(),
                                parameterWithName('order').description('Order asc/desc').optional(),
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/organizations/${organization.id}/employee-permissions?includeAll=true&sort=status")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", equalTo(6))
                .body("[0].status", equalTo(EmployeeStatus.PENDING.toString()))

        cleanup:
        LegalRepresentative.withNewTransaction {
            list.each {
                TestUtils.deleteRepresentative(it.id)
            }
            TestUtils.deleteOrganization(organization)
        }
    }

    def "can invite a Solo practitioner by email and easyvisaId to create a new organization"() {
        given:
        RestBuilder rest = new RestBuilder()
        LegalRepresentative inviter
        LegalRepresentative invitee
        LegalRepresentative.withNewTransaction {
            User user = new User(username: 'registeredattorney', language: 'En/US', password: 'registeredAttorneyPassword', paid: true)
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            inviter = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            inviter = attorneyService.createAttorney(inviter)
            user = inviter.user
            user.accountLocked = false
            user.save(failOnError: true)
            inviter.registrationStatus = RegistrationStatus.REPRESENTATIVE_SELECTED
            inviter.attorneyType = AttorneyType.SOLO_PRACTITIONER

            User user2 = new User(username: 'inviteeeattorney', language: 'En/US', password: 'inviteeAttorneyPassword', paid: true)
            Profile profile2 = new Profile(user: user2, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'inviteeAttorney@easyvisa.com')
            invitee = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile2, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            invitee = attorneyService.createAttorney(invitee)
            user2 = invitee.user
            user2.accountLocked = false
            user2.save(failOnError: true)
            invitee.registrationStatus = RegistrationStatus.REPRESENTATIVE_SELECTED
            invitee.attorneyType = AttorneyType.SOLO_PRACTITIONER

            inviter.save(failOnError: true)
            invitee.save(failOnError: true)

        }
        RestResponse resp = rest.post("http://localhost:${serverPort}/api/login") {
            header("Accept", "application/json")
            header("Content-Type", "application/json")
            json('{ "username": "registeredattorney", "password": "registeredAttorneyPassword" }')
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('invite-attorney-to-create-new-legal-practice',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('email').description('Email of attorney to be invited'),
                                parameterWithName('easyVisaId').description('EasyVisaId of attorney to be invited')
                        ), responseFields(
                        fieldWithPath('firstName').description('First name of invited attorney'),
                        fieldWithPath('middleName').description('Middle name of invited attorney'),
                        fieldWithPath('lastName').description('Last name of invited attorney'),
                        fieldWithPath('requestId').description('ID of the request generated'))))
                .when()
                .port(this.serverPort)
                .put("/api/legal-practice-invitee/ev-id/{easyVisaId}/email/{email}", invitee.profile.easyVisaId, invitee.profile.email)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("requestId", is(notNullValue()))
                .body("firstName", is(notNullValue()))
        cleanup:
        if (invitee) {
            LegalRepresentative.withNewTransaction {
                Alert.findAllByRecipient(invitee.profile.user)*.delete(failOnError: true)
                InviteToCreateOrganizationRequest.findAllByRepresentative(invitee)*.delete(failOnError: true)
                TestUtils.deleteRepresentative(invitee.id)
            }
        }
        if (inviter) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteRepresentative(inviter.id)
            }
        }
    }

    def "can withdraw an invite for a legal rep #label"() {
        given:
        RestBuilder rest = new RestBuilder()
        LegalRepresentative inviter
        LegalRepresentative invitee
        InviteToCreateOrganizationRequest processRequest
        LegalRepresentative.withNewTransaction {
            User user = new User(username: 'registeredattorney', language: 'En/US', password: 'registeredAttorneyPassword', paid: true)
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            inviter = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            inviter = attorneyService.createAttorney(inviter)
            user = inviter.user
            user.accountLocked = false
            user.save(failOnError: true)
            inviter.registrationStatus = RegistrationStatus.REPRESENTATIVE_SELECTED
            inviter.attorneyType = AttorneyType.SOLO_PRACTITIONER

            User user2 = new User(username: 'inviteeeattorney', language: 'En/US', password: 'inviteeAttorneyPassword', paid: true)
            Profile profile2 = new Profile(user: user2, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'inviteeAttorney@easyvisa.com')
            invitee = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile2, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            invitee = attorneyService.createAttorney(invitee)
            user2 = invitee.user
            user2.accountLocked = false
            user2.save(failOnError: true)
            invitee.registrationStatus = RegistrationStatus.REPRESENTATIVE_SELECTED
            invitee.attorneyType = AttorneyType.SOLO_PRACTITIONER
            inviter.save(failOnError: true)
            invitee.save(failOnError: true)
        }
        ProcessRequest.withNewTransaction {
            processRequest = organizationService.validateAndCreateNewOrganizationCreationInvite(inviter, invitee.profile.email, invitee.profile.easyVisaId)
        }
        RestResponse resp = rest.post("http://localhost:${serverPort}/api/login") {
            header("Accept", "application/json")
            header("Content-Type", "application/json")
            json('{ "username": "registeredattorney", "password": "registeredAttorneyPassword" }')
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('withdraw-invite-to-create-new-legal-practice',
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath('requestId').description('ID of the request generated'))))
                .when()
                .port(this.serverPort)
                .delete("/api/legal-practice-invite")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
        cleanup:
        if (processRequest) {
            InviteToCreateOrganizationRequest.withNewTransaction {
                processRequest.refresh()
                Alert.findAllByProcessRequest(processRequest)*.delete(failOnError: true)
                processRequest.delete(failOnError: true)
            }
        }
        if (invitee) {
            LegalRepresentative.withNewTransaction {
                Alert.findAllByRecipient(invitee.profile.user)*.delete(failOnError: true)
                InviteToCreateOrganizationRequest.findAllByRepresentative(invitee)*.delete(failOnError: true)
                TestUtils.deleteRepresentative(invitee.id)
            }
        }
        if (inviter) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteRepresentative(inviter.id)
            }
        }

    }

    def "test organization list"() {
        given:
        LegalRepresentative attorney
        Organization organization
        RestResponse resp
        Employee.withNewTransaction {
            User user = new User(username: 'organizationattorney', language: 'En/US', password: 'registeredAttorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true)

            organization = organizationService.create("Org ${attorney.profile.fullName}", OrganizationType.SOLO_PRACTICE)
            def organizationEmployee = organizationService.addAttorneyToOrganization(organization, attorney)
            organizationEmployee.save(failOnError: true)
        }

        resp = TestUtils.logInUser(serverPort, 'organizationattorney', 'registeredAttorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('organization-list',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('status').description('user/employee status on organization').optional(),
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/organizations?status=ACTIVE')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", equalTo(1))

        cleanup:
        if (attorney) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteOrganization(organization)
                TestUtils.deleteRepresentative(attorney.id)
            }
        }
    }

    @Unroll
    def "test withdraw invitation to join Organization #label"() {
        given:
        Organization organization
        RestResponse resp
        User user
        LegalRepresentative requester
        Employee inviteeEmp
        String reqEmailRand = "requesterAttorney${TestUtils.randomNumber()}@easyvisa.com"
        String invEmailRand = "inviteeAttorney${TestUtils.randomNumber()}@easyvisa.com"
        String reqUserName = "requesterattorney${TestUtils.randomNumber()}"

        Organization.withNewTransaction {
            user = new User(username: reqUserName, language: 'En/US', password: 'attorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: reqEmailRand)
            requester = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            requester = attorneyService.createAttorney(requester)
            user.accountLocked = false
            user.save(failOnError: true)
            requester.registrationStatus = RegistrationStatus.COMPLETE
            requester.save(failOnError: true)

            organization = organizationService.create("My Org${TestUtils.randomNumber()}", OrganizationType.SOLO_PRACTICE)
            organizationService.addAttorneyToOrganization(organization, requester, null, isAdmin)


            Profile profile2 = new Profile(lastName: 'last', firstName: 'First', middleName: 'middle', email: invEmailRand)
            inviteeEmp = new Employee(profile: profile2, mobilePhone: '99999123123')
            inviteeEmp = profileService.addEasyVisaId(inviteeEmp)

            new OrganizationEmployee(employee: inviteeEmp, organization: organization, status: EmployeeStatus.PENDING, position: EmployeePosition.TRAINEE).save()
            new RegistrationCode(easyVisaId: inviteeEmp.profile.easyVisaId).save()
        }

        resp = TestUtils.logInUser(serverPort, reqUserName, 'attorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('withdraw-invitation-to-join-organization',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Organization Id'),
                                parameterWithName('employeeId').description('Employee Id')
                        )))
                .when()
                .port(this.serverPort)
                .delete("/api/organizations/{id}/employee/{employeeId}", organization.id, inviteeEmp.id)
                .then()
                .assertThat()
                .statusCode(is(callResponse))

        cleanup:
        if (organization) {
            Organization.withNewTransaction {

                Alert.findByRecipient(inviteeEmp?.getUser())?.delete(failOnError: false)
                // The following is already deleted by the service.
                //InviteToOrganizationRequest.findAllByOrganizationAndEmployee(organization, inviteeEmp)?.delete(failOnError: true)
                TestUtils.deleteOrganization(organization)
                TestUtils.deleteRepresentative(requester.id)
            }
        }

        where:
        label            | isAdmin | callResponse
        "Admin User"     | true    | HttpStatus.SC_OK
        "Non Admin User" | false   | HttpStatus.SC_FORBIDDEN
    }

    def "can withdraw a request for joining an organization"() {
        given:
        RestBuilder rest = new RestBuilder()
        LegalRepresentative requester
        LegalRepresentative invitee
        Organization organization
        JoinOrganizationRequest processRequest
        RestResponse resp
        LegalRepresentative.withNewTransaction {
            User user = new User(username: 'registeredattorney', language: 'En/US', password: 'registeredAttorneyPassword', paid: true)
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            requester = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            requester = attorneyService.createAttorney(requester)
            user = requester.user
            user.accountLocked = false
            user.save(failOnError: true)
            requester.registrationStatus = RegistrationStatus.REPRESENTATIVE_SELECTED
            requester.attorneyType = AttorneyType.SOLO_PRACTITIONER

            User user2 = new User(username: 'inviteeeattorney', language: 'En/US', password: 'inviteeAttorneyPassword', paid: true)
            Profile profile2 = new Profile(user: user2, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'inviteeAttorney@easyvisa.com')
            invitee = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile2, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            invitee = attorneyService.createAttorney(invitee)
            user2 = invitee.user
            user2.accountLocked = false
            user2.save(failOnError: true)
            invitee.registrationStatus = RegistrationStatus.REPRESENTATIVE_SELECTED
            invitee.attorneyType = AttorneyType.SOLO_PRACTITIONER
            organization = TestUtils.createOrganization("test Organziation")
            new OrganizationEmployee(employee: invitee, organization: organization, position: EmployeePosition.PARTNER, isAdmin: true, status: EmployeeStatus.ACTIVE).save(failOnError: true)
            requester.save(failOnError: true)
            invitee.save(failOnError: true)

            processRequest = new JoinOrganizationRequest(organization: organization, employee: requester)
            processRequest.requestedBy = requester.profile
            processRequest.save(failOnError: true)

        }
        resp = rest.post("http://localhost:${serverPort}/api/login") {
            header("Accept", "application/json")
            header("Content-Type", "application/json")
            json('{ "username": "registeredattorney", "password": "registeredAttorneyPassword" }')
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('withdraw-request-to-join-organization',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('organizationEvId').description('EV ID of the organization'),
                                parameterWithName('requestId').description('Request ID that needs to be withdrawn')
                        ),
                        responseFields(
                                fieldWithPath('requestId').description('Request ID for the join request created'),
                                fieldWithPath('lastName').description('Last name of employee on join request'),
                                fieldWithPath('middleName').description('Middle name of employee on join request'),
                                fieldWithPath('firstName').description('First name of employee on join request'),
                                fieldWithPath('organizationName').description('Name of organization for which join request is made'),
                                fieldWithPath('organizationId').description('EV ID of organization for which join request is made'),
                                fieldWithPath('easyVisaId').description('EV ID employee on join request'))))
                .when()
                .port(this.serverPort)
                .delete('/api/organizations/{organizationEvId}/join-request/{requestId}', organization.easyVisaId, processRequest.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
        cleanup:
        if (processRequest) {
            InviteToCreateOrganizationRequest.withNewTransaction {
                processRequest.refresh()
                Alert.findAllByProcessRequest(processRequest)*.delete(failOnError: true)
                processRequest.delete(failOnError: true)
            }
        }
        if (invitee) {
            LegalRepresentative.withNewTransaction {
                Alert.findAllByRecipient(invitee.profile.user)*.delete(failOnError: true)
                InviteToCreateOrganizationRequest.findAllByRepresentative(invitee)*.delete(failOnError: true)
                TestUtils.deleteRepresentative(invitee.id)
            }
        }
        if (requester) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteRepresentative(requester.id)
            }
        }
        if (organization) {
            Organization.withNewTransaction {
                organization.delete(flush: true, failOnError: true)
            }
        }
    }

}
