package com.easyvisa.test.api

import com.easyvisa.AdminService
import com.easyvisa.AnswerService
import com.easyvisa.AttorneyService
import com.easyvisa.OrganizationService
import com.easyvisa.Package
import com.easyvisa.PackageService
import com.easyvisa.PaymentService
import com.easyvisa.ProfileService
import com.easyvisa.TaxService
import com.easyvisa.enums.PdfForm
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.PdfUtils
import com.easyvisa.utils.TestMockUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.startsWith
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class PdfControllerSpec extends TestMockUtils {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Autowired
    private ProfileService profileService
    @Autowired
    private PackageService packageService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private OrganizationService organizationService
    @Autowired
    private AnswerService answerService
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

    def testFormGeneration() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        Long beneficiaryId
        Package.withNewTransaction {
            beneficiaryId = testHelper.aPackage.refresh().beneficiaries.first().id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('generate-form-pdf',
                preprocessResponse(prettyPrint()),
                requestParameters(
                        parameterWithName('packageId').description('package id'),
                        parameterWithName('applicantId').description('applicant id'),
                        parameterWithName('formId').description('form id'))))
                .body()
                .when()
                .port(this.serverPort)
                .get("/api/pdf?packageId=${testHelper.aPackage.id}" +
                "&applicantId=${beneficiaryId}&formId=${PdfForm.I129F.formId}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .header('Content-Type', startsWith(PdfUtils.PDF_MIMETYPE))

        cleanup:
        testHelper.clean()
    }

    def testContinuationSheetGeneration() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        Long beneficiaryId
        Package.withNewTransaction {
            beneficiaryId = testHelper.aPackage.refresh().beneficiaries.first().id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('generate-continuations-sheet-pdf',
                preprocessResponse(prettyPrint()),
                requestParameters(
                        parameterWithName('packageId').description('package id'),
                        parameterWithName('applicantId').description('applicant id'),
                        parameterWithName('continuationSheetId').description('continuation sheet id'),
                        parameterWithName('continuationFileNumber')
                                .description('number of continuation sheet to generate. optional').optional())))
                .body()
                .when()
                .port(this.serverPort)
                .get("/api/pdf?packageId=${testHelper.aPackage.id}" +
                "&applicantId=${beneficiaryId}&continuationSheetId=CS_129F_1" +
                "&continuationFileNumber=0")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .header('Content-Type', startsWith(PdfUtils.PDF_MIMETYPE))

        cleanup:
        testHelper.clean()
    }

}
