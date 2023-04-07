package com.easyvisa.test.api

import com.easyvisa.AttorneyService
import com.easyvisa.LegalRepresentative
import com.easyvisa.Profile
import com.easyvisa.ProfileService
import com.easyvisa.User
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.PracticeArea
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.JsonFieldType
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class LoginControllerSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    ProfileService profileService
    @Autowired
    AttorneyService attorneyService

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
    }

    def "test /login url"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('login',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('username').description('Username of the user'),
                                fieldWithPath('password').description('Password of the user')
                        ),
                        responseFields(
                                fieldWithPath('id').description('Id of the uesr'),
                                fieldWithPath('access_token').description('Access Token'),
                                fieldWithPath('roles').type(JsonFieldType.ARRAY).description('List of User ROLES of this User'),
                        )))
                .body("""{ "username": "${testHelper.packageLegalRepresentative.user.username}", "password": "${PackageTestBuilder.ATTORNEY_PASSWORD}" }""")
                .when()
                .port(this.serverPort)
                .post('/api/login')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    def "test /api/login expired password after three failed attempts"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("""{ "username": "${testHelper.packageLegalRepresentative.user.username}", "password": "password1"}" }""")
                .when()
                .port(this.serverPort)
                .post('/api/login')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNAUTHORIZED))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("""{ "username": "${testHelper.packageLegalRepresentative.user.username}", "password": "password1"}" }""")
                .when()
                .port(this.serverPort)
                .post('/api/login')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNAUTHORIZED))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("""{ "username": "${testHelper.packageLegalRepresentative.user.username}", "password": "password1"}" }""")
                .when()
                .port(this.serverPort)
                .post('/api/login')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNAUTHORIZED))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .body("""{ "username": "${testHelper.packageLegalRepresentative.user.username}", "password": "${PackageTestBuilder.ATTORNEY_PASSWORD}"}" }""")
                .when()
                .port(this.serverPort)
                .post('/api/login')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        testHelper.clean()
    }

    def "test /api/logout Endpoint"() {
        given:
        RestResponse resp
        Long attorneyId
        LegalRepresentative.withNewTransaction {
            User user = new User(username: 'packageattorney', language: 'En/US', password: 'registeredAttorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            LegalRepresentative attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorneyId = attorney.id
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'packageattorney', 'registeredAttorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('user-logout'))
                .when()
                .port(this.serverPort)
                .post('/api/logout')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
        cleanup:
        if (attorneyId) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteRepresentative(attorneyId)
            }
        }
    }

}
