package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.document.DocumentNote
import com.easyvisa.enums.DocumentType
import com.easyvisa.enums.PackageStatus
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.PackageTestBuilder
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
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.Matchers.nullValue
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class DocumentControllerSpec extends TestMockUtils {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private PackageService packageService
    @Autowired
    private AnswerService answerService
    @Autowired
    private ProfileService profileService
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

    protected RequestSpecification spec

    def setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(attorneyService.accountService, paymentService, taxService)
    }

    void testEmptyProgressDerivatives() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPetitioner)
                .filter(document('get-document-portal-progress',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('package id')
                        ),
                        responseFields(
                                fieldWithPath('[].name').description('Name of the document panel type'),
                                fieldWithPath('[].packageStatus').description('Status of the Package'),
                                fieldWithPath('[].percentComplete').description('Integer value of current progress'),
                                fieldWithPath('[].elapsedDays').description('Days from start of package opened. Populates when all document upload are not completed'),
                                fieldWithPath('[].totalDays').description('Integer value of spent days to upload all documents.'),
                                fieldWithPath('[].dateStarted').description('Date of the first document upload (MM/dd/yyyy)'),
                                fieldWithPath('[].dateCompleted').description('Date when all document uploads were completed (MM/dd/yyyy)'),
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/document/progress/package/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('[0].name', equalTo(DocumentType.REQUIRED_DOCUMENT.panelName))
                .body('[0].packageStatus', equalTo(PackageStatus.OPEN.displayName))
                .body('[0].percentComplete', is(0))
                .body('[0].elapsedDays', is(0))
                .body('[0].totalDays', is(nullValue()))
                .body('[0].dateStarted', equalTo(new Date().format(DateUtil.PDF_FORM_DATE_FORMAT)))
                .body('[0].dateCompleted', is(nullValue()))
                .body('[1].name', equalTo(DocumentType.DOCUMENT_SENT_TO_US.panelName))
                .body('[1].packageStatus', equalTo(PackageStatus.OPEN.displayName))
                .body('[1].percentComplete', is(0))
                .body('[1].elapsedDays', is(0))
                .body('[1].totalDays', is(nullValue()))
                .body('[1].dateStarted', equalTo(new Date().format(DateUtil.PDF_FORM_DATE_FORMAT)))
                .body('[1].dateCompleted', is(nullValue()))
                .body('[2].name', equalTo(DocumentType.DOCUMENT_RECEIVED_FROM_US.panelName))
                .body('[2].packageStatus', equalTo(PackageStatus.OPEN.displayName))
                .body('[2].percentComplete', is(0))
                .body('[2].elapsedDays', is(0))
                .body('[2].totalDays', is(nullValue()))
                .body('[2].dateStarted', equalTo(new Date().format(DateUtil.PDF_FORM_DATE_FORMAT)))
                .body('[2].dateCompleted', is(nullValue()))

        cleanup:
        testHelper.clean()
    }

    void testFetchPackageRequiredDocuments() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-package-required-documents',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('package id')
                        ),
                        responseFields(
                                fieldWithPath('[].applicantType').description('Applicant type: Petitioner, Beneficiary or Derivative Beneficiary'),
                                fieldWithPath('[].applicantTitle').description('Applicant title: Petitioner, Beneficiary or Derivative Beneficiary'),
                                fieldWithPath('[].applicantId').description('Applicant Id'),
                                fieldWithPath('[].applicantName').description('Applicant Name'),
                                fieldWithPath('[].direct').description('Direct Beneficiary'),
                                fieldWithPath('[].order').description('Applicant Display Order'),
                                subsectionWithPath('[].requiredDocuments').description('List of required documents which has '),
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/document/package/{id}/requireddocuments', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('[0].applicantType', equalTo(ApplicantType.Petitioner.value))
                .body('[0].applicantTitle', equalTo(ApplicantType.Petitioner.value))
                .body('[0].applicantId', equalTo(testHelper.aPackage.petitioner.applicant.id as int))
                .body('[0].applicantName', is('petitioner-first petitioner-last'))
                .body('[0].direct', is(false))
                .body('[0].order', equalTo(0))
                .body('[0].requiredDocuments.size()', equalTo(11))
                .body('[1].applicantType', equalTo(ApplicantType.Beneficiary.value))
                .body('[1].applicantTitle', equalTo(ApplicantType.Beneficiary.value))
                .body('[1].applicantId', equalTo(testHelper.aPackage.beneficiaries[0].id as int))
                .body('[1].applicantName', is('applicant-first applicant-last'))
                .body('[1].direct', is(true))
                .body('[1].order', equalTo(1))
                .body('[1].requiredDocuments.size()', equalTo(12))

        cleanup:
        testHelper.clean()
    }

    void testFetchPackageSentDocuments() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-package-sentdocuments',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('package id')
                        ),
                        responseFields(
                                fieldWithPath('[].applicantType').description('Applicant type: Beneficiary or Derivative Beneficiary'),
                                fieldWithPath('[].applicantId').description('Applicant Id'),
                                fieldWithPath('[].applicantName').description('Applicant Name'),
                                subsectionWithPath('[].sentDocuments').description('List of sent documents which has '),
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/document/package/{id}/sentdocuments', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('[0].applicantType', equalTo(ApplicantType.Beneficiary.value))
                .body('[0].applicantId', equalTo(testHelper.aPackage.beneficiaries[0].id as int))
                .body('[0].applicantName', is('applicant-first applicant-last'))
                .body('[0].sentDocuments.size()', equalTo(1))

        cleanup:
        testHelper.clean()
    }

    void testFetchPackageReceivedDocuments() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-package-received-documents',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('package id')
                        ),
                        responseFields(
                                fieldWithPath('applicantType').description('Applicant type: Beneficiary or Derivative Beneficiary'),
                                fieldWithPath('applicantId').description('Applicant Id'),
                                fieldWithPath('applicantName').description('Applicant Name'),
                                subsectionWithPath('receivedDocuments').description('List of received documents which has '),
                        )
                ))
                .when()
                .port(this.serverPort)
                .get('/api/document/package/{id}/receiveddocuments', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('applicantType', equalTo(ApplicantType.Beneficiary.value))
                .body('applicantId', equalTo(testHelper.aPackage.beneficiaries[0].id as int))
                .body('applicantName', is('applicant-first applicant-last'))
                .body('receivedDocuments.size()', equalTo(8))

        cleanup:
        testHelper.clean()
    }

    void testFindDocumentMilestones() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-package-document-milestones',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('packageId').description('Package Id'),
                        ),
                        responseFields(
                                fieldWithPath('[].milestoneTypeId').description('MileStone Type Id'),
                                fieldWithPath('[].description').description('Milestone Description'),
                                fieldWithPath('[].dataLabel').description('Milestone Date Display Label'),
                                fieldWithPath('[].milestoneDate').description('Date of the mileStone (MM/dd/yyyy)'),
                        )
                ))
                .when()
                .port(this.serverPort)
                .get("/api/document/milestone?packageId=$testHelper.aPackage.id")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('[0].milestoneTypeId', equalTo('DocMilestone_1'))
                .body('[0].description', equalTo('Package sent to USCIS'))
                .body('[0].dataLabel', equalTo('Date Sent'))
                .body('[0].milestoneDate', is(nullValue()))
                .body('[1].milestoneTypeId', equalTo('DocMilestone_2'))
                .body('[1].description', equalTo('Approval Date'))
                .body('[1].dataLabel', equalTo('Approval Date'))
                .body('[1].milestoneDate', is(nullValue()))

        cleanup:
        testHelper.clean()
    }

    void testSaveDocumentMilestones() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('post-package-document-milestones',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('packageId').description('Package Id'),
                                fieldWithPath('milestoneTypeId').description('MileStone Type Id'),
                                fieldWithPath('milestoneDate').description('Date of the mileStone (MM-dd-yyyy)')
                        ),
                        responseFields(
                                fieldWithPath('milestoneTypeId').description('MileStone Type Id'),
                                fieldWithPath('description').description('Milestone Description'),
                                fieldWithPath('dataLabel').description('Milestone Date Display Label'),
                                fieldWithPath('milestoneDate').description('Date of the mileStone (MM/dd/yyyy)'),
                        )
                ))
                .body("""
                    {"packageId":"${testHelper.aPackage.id}", "milestoneTypeId" : "DocMilestone_2", "milestoneDate":"03-26-2021"
                }
                    """)
                .when()
                .port(this.serverPort)
                .post("/api/document/milestone")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('milestoneTypeId', equalTo('DocMilestone_2'))
                .body('description', equalTo('Approval Date'))
                .body('dataLabel', equalTo('Approval Date'))
                .body('milestoneDate', equalTo('03/26/2021'))

        cleanup:
        testHelper.clean()
    }

    void testFetchPackageUSCISData() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-package-uscis-data',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('packageId').description('Package Id'),
                        ),
                        responseFields(
                                subsectionWithPath('packageForms').description('List of Package Forms'),
                                subsectionWithPath('packageContinuationSheets').description('List of Package ContinuationSheets'),
                        )
                ))
                .when()
                .port(this.serverPort)
                .get("/api/document/uscis?packageId=$testHelper.aPackage.id")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('packageForms.size()', equalTo(1))
                .body('packageForms[0].formId', equalTo('Form_130'))
                .body('packageContinuationSheets.size()', equalTo(10))
                .body('packageContinuationSheets[0].continuationSheetId', equalTo('CS_130_1'))
                .body('packageContinuationSheets[1].continuationSheetId', equalTo('CS_130_2'))
                .body('packageContinuationSheets[2].continuationSheetId', equalTo('CS_130_6'))
                .body('packageContinuationSheets[3].continuationSheetId', equalTo('CS_130_childrenInformationForBeneficiary'))
                .body('packageContinuationSheets[4].continuationSheetId', equalTo('CS_130_employmentStatus'))
                .body('packageContinuationSheets[5].continuationSheetId', equalTo('CS_130_otherNamesUsedForBeneficiary'))
                .body('packageContinuationSheets[6].continuationSheetId', equalTo('CS_130_previousPhysicalAddress'))
                .body('packageContinuationSheets[7].continuationSheetId', equalTo('CS_130_priorImmigrationProceedings'))
                .body('packageContinuationSheets[8].continuationSheetId', equalTo('CS_130_priorSpouses'))
                .body('packageContinuationSheets[9].continuationSheetId', equalTo('CS_130_priorSpousesForBeneficiary'))

        cleanup:
        testHelper.clean()
    }

    void testFindDocumentNotes() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .addDocumentNotes()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('get-package-uscis-notes',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('packageId').description('Package Id'),
                        ),
                        responseFields(
                                fieldWithPath('[].id').description('DocumentNote Id'),
                                fieldWithPath('[].subject').description('DocumentNote Subject'),
                                fieldWithPath('[].documentNoteType').description('DocumentNote Type (PUBLIC_NOTE / REPRESENTATIVE_NOTE)'),
                                fieldWithPath('[].createdDate').description('DocumentNote Created Date (MM/dd/yyyy)'),
                                fieldWithPath('[].createdTime').description('DocumentNote Created Time (hh:mm:ss aa)'),
                                subsectionWithPath('[].creator').description('DocumentNote Creator Name (firstName / middleName / lastName)'),
                        )
                ))
                .when()
                .port(this.serverPort)
                .get("/api/document/notes?packageId=$testHelper.aPackage.id")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }

    void testRemoveDocumentNote() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .addDocumentNotes()
                .logInPackageLegalRep()
        DocumentNote documentNote = testHelper.documentNotes.first()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('delete-package-document-note',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('packageId').description('Package Id'),
                                parameterWithName('documentNoteId').description('DocumentNote Id'),
                        ),
                        responseFields(
                                fieldWithPath('id').description('Removed DocumentNote Id')
                        )
                ))
                .when()
                .port(this.serverPort)
                .delete("/api/document/notes?packageId=$testHelper.aPackage.id&documentNoteId=$documentNote.id")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        cleanup:
        testHelper.clean()
    }
}
