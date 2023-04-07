package com.easyvisa.test.api

import com.easyvisa.AttorneyService
import com.easyvisa.Package
import com.easyvisa.PackageService
import com.easyvisa.ProfileService
import com.easyvisa.utils.PackageTestBuilder
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
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
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class PackageValidationControllerSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private ProfileService profileService
    @Autowired
    PackageService packageService
    @Autowired
    AttorneyService attorneyService

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
    }

    def testAddNonRegisteredApplicantToCreateNewPackage() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 packageService : packageService,
                                                                 attorneyService: attorneyService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryBlockedPackage()
                .logInPackageLegalRep()


        String email
        Long petId
        Package.withNewTransaction {
            email = testHelper.aPackage.refresh().petitioner.applicant.profile.email
            petId = testHelper.aPackage.petitioner.applicant.id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.getPackagePetitionerBeneficiaryCreatePayload(email, petId))
                .when()
                .port(this.serverPort)
                .post('/api/packages')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.getPackageNoPetitionerCreatePayload(petId))
                .when()
                .port(this.serverPort)
                .post('/api/packages')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        testHelper.clean()
    }

    def testAddNonRegisteredApplicantToEditPackage() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 packageService : packageService,
                                                                 attorneyService: attorneyService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryBlockedPackage()
                .logInPackageLegalRep()
        PackageTestBuilder petAndBenHelper = PackageTestBuilder.init(testHelper)
        petAndBenHelper.buildPetitionerAndBeneficiaryBlockedPackage()
        PackageTestBuilder noPetitionerHelper = PackageTestBuilder.init(testHelper)
        noPetitionerHelper.buildPetitionerAndBeneficiaryBlockedPackage()

        String email
        Long petId
        Package.withNewTransaction {
            email = testHelper.aPackage.refresh().petitioner.applicant.profile.email
            petId = testHelper.aPackage.petitioner.applicant.id
        }
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(petAndBenHelper.
                getPackagePetitionerBeneficiaryEditPayload(email, petId, testHelper.aPackage.id))
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', petAndBenHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(noPetitionerHelper.getPackageNoPetitionerCreatePayload(petId))
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', noPetitionerHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        testHelper.deletePackageOnly()
        petAndBenHelper.deletePackageOnly()
        noPetitionerHelper.clean()
    }

    def testEditClosedPackageForbidden() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 packageService : packageService,
                                                                 attorneyService: attorneyService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryClosedPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editNewBeneficiaryWithPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        testHelper.clean()
    }

}
