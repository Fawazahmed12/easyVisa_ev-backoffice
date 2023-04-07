package com.easyvisa.test.api

import com.easyvisa.AttorneyService
import com.easyvisa.ProfileService
import com.easyvisa.RegistrationCode
import com.easyvisa.utils.PackageTestBuilder
import grails.plugin.springsecurity.SpringSecurityService
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
@Rollback
class ForgotPasswordControllerSpec extends Specification {

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
        RegistrationCode.withNewTransaction {
            RegistrationCode registrationCode = new RegistrationCode(username: "easyvisauser")
            registrationCode.save()
        }
    }

    void cleanup() {
        RegistrationCode.withNewTransaction {
            RegistrationCode.findAllByUsername("easyvisauser")*.delete(failOnError: true)
        }
    }

    def "test /forgot-password with existing email"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService,])
        testHelper.buildPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('forgot-password',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('email').description('Email of the user')
                )))
                .body("""{ "email": "${testHelper.packageLegalRepresentative.profile.email}" }""")
                .when()
                .port(this.serverPort)
                .post('/api/public/forgot-password')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    def "test /forgot-password with non-existing email"() {
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('forgot-password-error',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('email').description('Email of the user')
                )))
                .body('{ "email": "noneexistingemail@easyvisa.com" }')
                .when()
                .port(this.serverPort)
                .post('/api/public/forgot-password')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_NOT_FOUND))
    }

    def "test /reset-password with correct token"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService,])
        testHelper.buildPackageLegalRep()

        RegistrationCode registrationCode
        RegistrationCode.withNewTransaction {

            registrationCode = new RegistrationCode(username: testHelper.username)
            registrationCode.save(failOnError: true)
            //registrationCode = RegistrationCode.findByUsername(testHelper.packageLegalRepresentative.user.username)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('reset-password',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('token').description('Reset password token'),
                        fieldWithPath('password').description('New password'),
                )))
                .body('{ "token": "' + registrationCode.token + '", "password": "nme3w4p5a6ssw5orwd1313" }')
                .when()
                .port(this.serverPort)
                .post('/api/public/reset-password')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

}
