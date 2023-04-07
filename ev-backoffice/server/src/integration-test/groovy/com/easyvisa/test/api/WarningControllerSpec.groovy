package com.easyvisa.test.api

import com.easyvisa.AttorneyService
import com.easyvisa.PackageService
import com.easyvisa.ProfileService
import com.easyvisa.Warning
import com.easyvisa.utils.PackageTestBuilder
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.hibernate.SessionFactory
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class WarningControllerSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort
    @Autowired
    private ProfileService profileService
    @Autowired
    SessionFactory sessionFactory
    @Autowired
    AttorneyService attorneyService
    @Autowired
    PackageService packageService

    protected RequestSpecification spec
    protected ResponseFieldsSnippet warningFields

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.warningFields = responseFields(
                fieldWithPath('id').description('id of the warning'),
                fieldWithPath('createdOn').description('Date when warning was created'),
                fieldWithPath('read').description('Boolean for if the warning is read'),
                fieldWithPath('starred').description('Boolean for if the warning is starred'),
                fieldWithPath('subject').description('Subject of warning'),
                fieldWithPath('source').description('Source of warning'),
                fieldWithPath('packageId').description('Id of Package warning belongs to'),
                fieldWithPath('questionId').description('Id of question warning belongs to'),
                fieldWithPath('answerId').description('Id of answer warning belongs to'),
                fieldWithPath('content').description('Content of warning'),
                fieldWithPath('clientName').description('Content of warning'),
                fieldWithPath('representativeName').description('Content of warning'))
    }

    def 'Can mark a warning as read '() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep()
                .buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .addWarnings()
                .logInPackageDirectBeneficiary()

        Warning warning = testHelper.warnings.first()
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageDirect)
                .filter(document('read-warning',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('read').description('Boolean to set warning as Read or Unread'),
                ),
                this.warningFields))
                .body("""{"read":true}""")
                .when()
                .port(this.serverPort)
                .put("/api/warnings/${warning.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("id", equalTo(warning.id as int))
                .body("read", equalTo(true))

        cleanup:
        testHelper.clean()
    }

    def 'Can mark a warning as starred'() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep()
                .buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .addWarnings()
                .logInPackageDirectBeneficiary()

        Warning warning = testHelper.warnings.first()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageDirect)
                .filter(document('star-warning',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('starred').description('Boolean to set warning as starred or Unstarred'),
                ),
                this.warningFields))
                .body("""{"starred":true}""")
                .when()
                .port(this.serverPort)
                .put("/api/warnings/${warning.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("id", equalTo(warning.id as int))
                .body("starred", equalTo(true))

        cleanup:
        testHelper.clean()
    }

    def 'Can delete multiple warnings'() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep()
                .buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .addWarnings()
                .logInPackageDirectBeneficiary()
        Long id = testHelper.warnings[0].id
        Long id1 = testHelper.warnings[1].id
        Long id2 = testHelper.warnings[2].id
        testHelper.warnings.clear()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageDirect)
                .filter(document('delete-warnings',
                preprocessResponse(prettyPrint()),
                requestParameters(
                        parameterWithName('ids').description('List of ids to delete'),
                ), responseFields(
                fieldWithPath('deletedWarningIds').description('List of ids deleted'),)))
                .when()
                .port(this.serverPort)
                .delete("/api/warnings?ids=${id}&ids=${id1}&ids=${id2}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    def 'Can get warnings for a legal rep'() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .addWarnings()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-user-warnings',
                preprocessResponse(prettyPrint()),
                requestParameters(
                        parameterWithName('representativeId').description('Representative id to fetch warnings for'),
                        parameterWithName('organizationId').description('Organization id to fetch warnings for')
                )))
                .when()
                .port(this.serverPort)
                .get("/api/warnings?representativeId=${testHelper.packageLegalRepresentative.id}&organizationId=${testHelper.organization.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('size()', equalTo(3))
                .header('X-total-count', equalTo(3 as String))

        cleanup:
        testHelper.clean()
    }

}
