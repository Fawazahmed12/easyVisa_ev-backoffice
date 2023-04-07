package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.questionnaire.Answer
import com.easyvisa.utils.AnswerListPdfRulesStub
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.QuestionnaireTestDBSetupUtility
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
import spock.lang.Ignore

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class ApplicantControllerSpec extends TestMockUtils {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Autowired
    private PackageService packageService
    @Autowired
    private ProfileService profileService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private ApplicantService applicantService
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

    def "test find applicants by email"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .logInPackageLegalRep()

        Long orgId
        String beneficiaryEmail, beneficiaryEasyVisaId, petEmail, petEasyVisaId
        Package.withNewTransaction {
            Profile beneficiaryProfile = testHelper.aPackage.refresh().beneficiaries[0].profile
            beneficiaryEmail = beneficiaryProfile.email
            beneficiaryEasyVisaId = beneficiaryProfile.easyVisaId
            orgId = testHelper.aPackage.organization.id
            petEmail = testHelper.aPackage.petitioner.profile.email
            petEasyVisaId = testHelper.aPackage.petitioner.profile.easyVisaId
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('find-applicant',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('email').description('email to search'),
                                parameterWithName('organizationId').description('organization id')
                        ),
                        responseFields(
                                fieldWithPath('profile.id').description('id of applicant'),
                                fieldWithPath('profile.firstName').description('First name of applicant'),
                                fieldWithPath('profile.lastName').description('Last name of applicant'),
                                fieldWithPath('profile.middleName').description('Middle name of applicant'),
                                fieldWithPath('profile.mobileNumber').description('Mobile number of applicant'),
                                fieldWithPath('profile.dateOfBirth').description('DOB of applicant'),
                                fieldWithPath('profile.email').description('Email of applicant'),
                                fieldWithPath('profile.easyVisaId').description('beneficiaryEasyVisaId of applicant'),
                                fieldWithPath('profile.homeNumber').description('Home phone number of applicant'),
                                fieldWithPath('profile.workNumber').description('Work phone number of applicant'),
                                fieldWithPath('profile.homeAddress').description('Home address of applicant'),
                                fieldWithPath('profile.profilePhoto').description('URL for profile photo of user.'),
                                fieldWithPath('inBlockedPackage').description('Indicates if an applicant in Blocked package. (true/false)'),
                                fieldWithPath('inOpenPackage').description('Indicates if an applicant in Open package. (true/false)')
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/applicants/find?email={email}&organizationId={orgId}', beneficiaryEmail, orgId)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('profile.email', equalTo(beneficiaryEmail))
                .body('profile.easyVisaId', equalTo(beneficiaryEasyVisaId))
                .body('inBlockedPackage', is(false))
                .body('inOpenPackage', is(false))


        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/applicants/find?email={email}&organizationId={orgId}', petEmail, orgId)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('profile.email', equalTo(petEmail))
                .body('profile.easyVisaId', equalTo(petEasyVisaId))
                .body('inBlockedPackage', is(false))
                .body('inOpenPackage', is(false))

        cleanup:
        testHelper.clean()
    }

    def testFindApplicantInOpenPackage() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackageLegalRep()
        Long orgId
        String beneficiaryEmail, beneficiaryEasyVisaId
        Package.withNewTransaction {
            Profile beneficiaryProfile = testHelper.aPackage.refresh().beneficiaries[0].profile
            beneficiaryEmail = beneficiaryProfile.email
            beneficiaryEasyVisaId = beneficiaryProfile.easyVisaId
            orgId = testHelper.aPackage.organization.id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/applicants/find?email={email}&organizationId={orgId}', beneficiaryEmail, orgId)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('profile.email', equalTo(beneficiaryEmail))
                .body('profile.easyVisaId', equalTo(beneficiaryEasyVisaId))
                .body('inBlockedPackage', is(false))
                .body('inOpenPackage', is(true))

        cleanup:
        testHelper.clean()
    }

    //TODO:it's not expected to have questions/answers for Derivatives
    @Ignore
    def testDeleteApplicantData() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndTwoBeneficiariesOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackageDirectBeneficiary()
        Package aPackage = testHelper.aPackage
        Applicant beneficiary = aPackage.benefits.find { !it.direct }.applicant

        Profile.withNewTransaction {
            User userBen = beneficiary.user
            Profile profile = beneficiary.profile.refresh()
            profile.user = null
            profile.save(failOnError: true)
            UserRole.findAllByUser(userBen)*.delete(failOnError: true)
            userBen.delete(failOnError: true)
        }

        List<Answer> answerList = AnswerListPdfRulesStub.beneficiaryDirectDerivative485(aPackage.id, beneficiary.id)
        String sectionId = 'Sec_biographicInformation'
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id, beneficiary.id,
                sectionId, answerList)

        testHelper.addWarnings(beneficiary)

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageDirect)
                .filter(document('delete-applicant-data',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Applicant id to delete data')),
                        responseFields(
                                fieldWithPath('id').description('id of applicant that data was deleted'))))
                .when()
                .port(this.serverPort)
                .delete('/api/applicants/{id}', beneficiary.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(beneficiary.id as int))

        List<Answer> answers
        Warning warning
        Answer.withNewTransaction {
            answers = Answer.findAllByApplicantId(beneficiary.id)
            beneficiary.refresh()
            beneficiary.profile.email
            warning = Warning.findByAPackage(aPackage)
        }

        assert 0 == answers.findAll { it.value && it.value != 'false' }.size()
        assert !beneficiary.profile.email
        assert warning.messageType == EasyVisaSystemMessageType.PACKAGE_APPLICANT_DELETION

        cleanup:
        testHelper.clean()
    }

    def testTransferPackageByApplicant() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildNoPackageLegalRep()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()
        LegalRepresentative noPackageLegalRep = testHelper.legalRepresentativeNoPackage

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPetitioner)
                .filter(document('package-transfer-by-applicant',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('packageId').description('package id'),
                                fieldWithPath('representativeId').description('RepresentativeId of new assignee'),
                                fieldWithPath('organizationId').description('Id of organization where packages will be transferred')
                        ),
                        responseFields(
                                fieldWithPath('representativeId').description('New assignee id'),
                                fieldWithPath('firstName').description('New assignee first name'),
                                fieldWithPath('lastName').description('New assignee last name'),
                                fieldWithPath('middleName').description('New assignee middle name')
                        )))
                .body("""{"packageId":$testHelper.aPackage.id,
"representativeId":"${noPackageLegalRep.id}",
"organizationId":"${testHelper.organization.id}"
}""")
                .when()
                .port(this.serverPort)
                .post('/api/applicants/package/transfer')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('representativeId', equalTo(noPackageLegalRep.id as int))
                .body('firstName', equalTo(noPackageLegalRep.profile.firstName))
                .body('lastName', equalTo(noPackageLegalRep.profile.lastName))
                .body('middleName', equalTo(noPackageLegalRep.profile.middleName))

        Alert alert
        Warning warning
        ProcessRequest.withNewTransaction {
            alert = Alert.findByRecipient(noPackageLegalRep.user)
            alert.processRequest.state
            warning = Warning.findByAPackage(testHelper.aPackage)
        }

        assert ProcessRequestState.PENDING == alert.processRequest.state
        assert EasyVisaSystemMessageType.APPLICANT_PACKAGE_TRANSFER_REQUEST == alert.messageType
        assert EasyVisaSystemMessageType.APPLICANT_PACKAGE_TRANSFER_REQUEST_OLD_LEGAL_REP == warning.messageType

        cleanup:
        testHelper.clean()
    }

    def testTransferNoPetitionerPackageByApplicant() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 answerService  : answerService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerOpenPackage()
                .buildNoPackageLegalRep()
                .buildUsersForPackageApplicants()
                .logInPackageDirectBeneficiary()
        LegalRepresentative noPackageLegalRep = testHelper.legalRepresentativeNoPackage

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageDirect)
                .body("""{"packageId":$testHelper.aPackage.id,
"representativeId":"${noPackageLegalRep.id}",
"organizationId":"${testHelper.organization.id}"
}""")
                .when()
                .port(this.serverPort)
                .post('/api/applicants/package/transfer')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('representativeId', equalTo(noPackageLegalRep.id as int))
                .body('firstName', equalTo(noPackageLegalRep.profile.firstName))
                .body('lastName', equalTo(noPackageLegalRep.profile.lastName))
                .body('middleName', equalTo(noPackageLegalRep.profile.middleName))

        Alert alert
        Warning warning
        ProcessRequest.withNewTransaction {
            alert = Alert.findByRecipient(noPackageLegalRep.user)
            alert.processRequest.state
            warning = Warning.findByAPackage(testHelper.aPackage)
        }

        assert ProcessRequestState.PENDING == alert.processRequest.state
        assert EasyVisaSystemMessageType.APPLICANT_PACKAGE_TRANSFER_REQUEST == alert.messageType
        assert EasyVisaSystemMessageType.APPLICANT_PACKAGE_TRANSFER_REQUEST_OLD_LEGAL_REP == warning.messageType

        cleanup:
        testHelper.clean()
    }

    def testTransferPackageByNonPetitionerApplicant() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildNoPackageLegalRep()
                .buildUsersForPackageApplicants()
                .logInPackageDirectBeneficiary()
        LegalRepresentative noPackageLegalRep = testHelper.legalRepresentativeNoPackage

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageDirect)
                .body("""{"packageId":$testHelper.aPackage.id,
"representativeId":"${noPackageLegalRep.id}",
"organizationId":"${testHelper.organization.id}"
}""")
                .when()
                .port(this.serverPort)
                .post('/api/applicants/package/transfer')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        testHelper.clean()
    }

    def testTransferBlockedPackageByApplicant() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryBlockedPackage()
                .buildNoPackageLegalRep()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()
        LegalRepresentative noPackageLegalRep = testHelper.legalRepresentativeNoPackage

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPetitioner)
                .body("""{"packageId":$testHelper.aPackage.id,
"representativeId":"${noPackageLegalRep.id}",
"organizationId":"${testHelper.organization.id}"
}""")
                .when()
                .port(this.serverPort)
                .post('/api/applicants/package/transfer')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))

        cleanup:
        testHelper.clean()
    }

}
