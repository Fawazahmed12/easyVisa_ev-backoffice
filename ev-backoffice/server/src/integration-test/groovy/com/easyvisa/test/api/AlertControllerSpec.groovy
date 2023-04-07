package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
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
import static org.hamcrest.CoreMatchers.notNullValue
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class AlertControllerSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort
    @Autowired
    ProfileService profileService
    @Autowired
    SessionFactory sessionFactory
    @Autowired
    AttorneyService attorneyService
    @Autowired
    PackageService packageService

    protected RequestSpecification spec
    protected ResponseFieldsSnippet alertFields

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.alertFields = responseFields(
                fieldWithPath('id').description('id of the alert'),
                fieldWithPath('createdOn').description('Date when alert was created'),
                fieldWithPath('read').description('Boolean for if the alert is read'),
                fieldWithPath('starred').description('Boolean for if the alert is starred'),
                fieldWithPath('userId').description('Id of the user alert belongs to'),
                fieldWithPath('subject').description('Subject of alert'),
                fieldWithPath('source').description('Source of alert'),
                fieldWithPath('content').description('Content of alert'),
                fieldWithPath('recipientName').description('Recipient Name'))
    }

    def 'Can get alerts for a user'() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .addAlertsToPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-user-alerts',
                preprocessResponse(prettyPrint())))
                .when()
                .port(this.serverPort)
                .get('/api/alerts')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('size()', equalTo(3))
                .header('X-total-count', equalTo('3'))

        cleanup:
        testHelper.clean()
    }

    def 'Can get alert by id'() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .addAlertsToPackageLegalRep()
                .logInPackageLegalRep()
        Alert alert = testHelper.alerts.first()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('alert-details',
                preprocessResponse(prettyPrint()),
                this.alertFields))
                .when()
                .port(this.serverPort)
                .get("/api/alerts/${alert.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(alert.id as int))
                .body('subject', equalTo(alert.subject))

        cleanup:
        testHelper.clean()
    }

    def 'Can mark an alert as read '() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .addAlertsToPackageLegalRep()
                .logInPackageLegalRep()
        Alert alert = testHelper.alerts.first()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('read-alert',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('read').description('Boolean to set alert as Read or Unread'),
                ),
                this.alertFields))
                .body("""
{"read":true}
""")
                .when()
                .port(this.serverPort)
                .put("/api/alerts/${alert.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(alert.id as int))
                .body('read', equalTo(true))

        cleanup:
        testHelper.clean()
    }

    def 'Can mark an alert as starred'() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .addAlertsToPackageLegalRep()
                .logInPackageLegalRep()
        Alert alert = testHelper.alerts.first()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('starred-alert',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('starred').description('Boolean to set alert as Read or Unread'),
                ),
                this.alertFields))
                .body("""
{"starred":true}
""")
                .when()
                .port(this.serverPort)
                .put("/api/alerts/${alert.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(alert.id as int))
                .body('starred', equalTo(true))

        cleanup:
        testHelper.clean()
    }

    def 'Can delete multiple alerts'() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .addAlertsToPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('delete-alerts',
                preprocessResponse(prettyPrint()),
                requestParameters(
                        parameterWithName('ids').description('List of ids to delete'),
                ), responseFields(
                fieldWithPath('deletedAlertIds').description('List of ids deleted'),)))
                .when()
                .port(this.serverPort)
                .delete("/api/alerts?ids=${testHelper.alerts[0].id}&ids=${testHelper.alerts[1].id}&ids=${testHelper.alerts[2].id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    def 'Can count unread alerts,warnings and dispositions for a user'() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 packageService : packageService,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryBlockedPackage()
                .addAlertsToPackageLegalRep()
                .markAlertAsRead()
                .addWarnings()
                .markWarningAsRead()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('unread-counts',
                preprocessResponse(prettyPrint()),
                requestParameters(
                    parameterWithName('representativeId').description('Id of the representative to fetch results for'),
                    parameterWithName('organizationId').description('Id of the organization to fetch results for'),
                ),
                responseFields(
                        fieldWithPath('alerts').description('read/unread counts for alerts'),
                        fieldWithPath('alerts.read').description('read counts for alerts'),
                        fieldWithPath('alerts.unread').description('unread counts for alerts'),
                        fieldWithPath('warnings').description('read/unread counts for warnings'),
                        fieldWithPath('warnings.read').description('read counts for warnings'),
                        fieldWithPath('warnings.unread').description('unread counts for warnings'),
                        fieldWithPath('dispositionsCount').description('Count of user dispositions'))))
                .when()
                .port(this.serverPort)
                .get("/api/unread/count?representativeId=${testHelper.packageLegalRepresentative.id}&organizationId=${testHelper.organization.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('alerts', equalTo(['read': 1, 'unread': 2]))
                .body('warnings', equalTo(['read': 1, 'unread': 2]))
                .body('dispositionsCount', equalTo(0))

        cleanup:
        testHelper.clean()
    }

    def 'Can process alerts to handle requests'() {
        given:

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildNoPackageLegalRep()
                .logInNoPackageLegalRep()

        Alert alert
        ApplicantPackageTransferRequest request
        LegalRepresentative.withNewTransaction {
            testHelper.packageLegalRepresentative.refresh()
            testHelper.legalRepresentativeNoPackage.refresh()
            testHelper.organization.refresh()
            request = new ApplicantPackageTransferRequest(aPackage: testHelper.aPackage,
                    representative: testHelper.legalRepresentativeNoPackage,
                    requestedBy: testHelper.packageLegalRepresentative.profile,
                    oldAssignee: testHelper.packageLegalRepresentative,
                    representativeOrganization: testHelper.organization,
                    oldOrganization: testHelper.organization).save(failOnError: true)
            alert = new Alert(processRequest: request, messageType: EasyVisaSystemMessageType.PACKAGE_TRANSFER_REQUEST,
                    recipient: testHelper.legalRepresentativeNoPackage.user,
                    subject: EasyVisaSystemMessageType.PACKAGE_TRANSFER_REQUEST.subject)
                    .save(failOnError: true)
        }
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenNoPackageLegalRep)
                .filter(document('reply-user-alerts',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('accept').description('Boolean to indicate if the alert request is accepted or not.')
                )))
                .body("""
                {"accept":"true"}
                """)
                .when()
                .port(this.serverPort)
                .put("/api/alerts/${alert.id}/reply")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('message', is(notNullValue()))

        cleanup:
        testHelper.deleteNoPackageLegalRepProcessRequests()
        testHelper.deletePackageLegalRep()
        testHelper.deletePackage()
    }

}
