package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
//import grails.transaction.Rollback
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
//@Rollback
class AnswerControllerSpec extends TestMockUtils {
    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Autowired
    private PackageService packageService
    @Autowired
    private ProfileService profileService
    @Autowired
    private AnswerService answerService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private AdminService adminService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    private TaxService taxServiceMock = Mock(TaxService)

    @Value('${local.server.port}')
    Integer serverPort

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }

    def "test save-answer"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
        Long packageId = testHelper.aPackage.id
        Long petitionerApplicantId
        Package.withNewTransaction {
            petitionerApplicantId = testHelper.aPackage.refresh().petitioner.applicant.id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('save-answer',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('packageId').description('Package Id'),
                        fieldWithPath('applicantId').description('ApplicantI d'),
                        fieldWithPath('sectionId').description('Section Id'),
                        fieldWithPath('subsectionId').description('Subsection Id'),
                        fieldWithPath('questionId').description('question Id '),
                        fieldWithPath('value').description('value'),
                        fieldWithPath('index').description('repeating index')
                )))
                .body("""
                    {"packageId":"${packageId}", "applicantId" : "${petitionerApplicantId}", "sectionId":"Sec_1",
                    "subsectionId": "SubSec_1", "questionId": "Q_1", "value": "yes", "index": ""
                }
                    """)
                .when()
                .port(this.serverPort)
                .post('/api/answer')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()

    }

    def "test create-repeating-group-instance"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
        Long packageId = testHelper.aPackage.id
        Long petitionerApplicantId
        Package.withNewTransaction {
            petitionerApplicantId = testHelper.aPackage.refresh().petitioner.applicant.id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('create-repeating-group-instance',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('packageId').description('Package Id'),
                        fieldWithPath('applicantId').description('ApplicantI d'),
                        fieldWithPath('sectionId').description('Section Id'),
                        fieldWithPath('subsectionId').description('Subsection Id'),
                        fieldWithPath('repeatingGroupId').description('Repeating Group Id ')
                )))
                .body("""
                    {"packageId":${packageId}, "applicantId" : ${petitionerApplicantId}, "sectionId":"Sec_1",
                    "subsectionId": "SubSec_1", "repeatingGroupId": "RQG_1"
                }
                    """)
                .when()
                .port(this.serverPort)
                .post('/api/repeatinggroup')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()

    }

    def "test remove-repeating-group-instance"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
        Long packageId = testHelper.aPackage.id
        Long petitionerApplicantId
        Package.withNewTransaction {
            petitionerApplicantId = testHelper.aPackage.refresh().petitioner.applicant.id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('remove-repeating-group-instance',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('packageId').description('Package Id'),
                        fieldWithPath('applicantId').description('ApplicantI d'),
                        fieldWithPath('sectionId').description('Section Id'),
                        fieldWithPath('subsectionId').description('Subsection Id'),
                        fieldWithPath('repeatingGroupId').description('Repeating Group Id '),
                        fieldWithPath('index').description('Repeating Group Index ')
                )))
                .body("""
                    {"packageId":${packageId}, "applicantId" : ${petitionerApplicantId}, "sectionId":"Sec_1",
                    "subsectionId": "SubSec_1", "repeatingGroupId": "RQG_1", "index":1
                }
                    """)
                .when()
                .port(this.serverPort)
                .post('/api/repeatinggroup/remove')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()

    }


    def "test non-empty answer validation"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
        Long packageId = testHelper.aPackage.id
        Long petitionerApplicantId
        Package.withNewTransaction {
            petitionerApplicantId = testHelper.aPackage.refresh().petitioner.applicant.id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('validate-answer',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('packageId').description('Package Id'),
                                fieldWithPath('applicantId').description('Applicant Id'),
                                fieldWithPath('sectionId').description('Section Id'),
                                fieldWithPath('subsectionId').description('Subsection Id'),
                                fieldWithPath('questionId').description('question Id '),
                                fieldWithPath('value').description('value'),
                                fieldWithPath('index').description('repeating index')
                        )))
                .body("""
                    {"packageId":"${packageId}", "applicantId" : "${petitionerApplicantId}", "sectionId":"Sec_2",
                    "subsectionId": "SubSec_5", "questionId": "Q_32", "value": "", "index": ""
                }
                    """)
                .when()
                .port(this.serverPort)
                .post('/api/answer/validate')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('questionId', equalTo("Q_32"))
                .body('subsectionId', equalTo("SubSec_5"))
                .body('hasValidAnswer', equalTo(Boolean.FALSE))
                .body('errorMessage', equalTo("'First Name' of Profile cannot be empty, Please enter a valid value"))
                .body('resetValue', equalTo("petitioner-first"))

        cleanup:
        testHelper.clean()
    }

}
