package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.PracticeArea
import com.easyvisa.enums.RegistrationStatus
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
//import grails.transaction.Rollback
import org.apache.http.HttpStatus
import org.hibernate.SessionFactory
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
//@Rollback
class ReviewControllerSpec extends TestMockUtils {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private ProfileService profileService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private PackageService packageService
    @Autowired
    private ReviewService reviewService
    @Autowired
    private SessionFactory sessionFactory
    @Autowired
    private AdminService adminService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    private TaxService taxServiceMock = Mock(TaxService)

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }

    def "test create a new review"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPetitioner)
                .filter(document('create-review',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('packageId').type(Long).description('package to be associated to Review'),
                                fieldWithPath('representativeId').type(Long).description('representativeId to be associated to Review'),
                                fieldWithPath('rating').type(Integer).description('rating of Review'),
                                fieldWithPath('title').description('title of Review'),
                                fieldWithPath('review').description('review text of Review')
                        )))
                .body('{\n' +
                        '"packageId":' + testHelper.aPackage.id + ',\n' +
                        '"representativeId":' + testHelper.packageLegalRepresentative.id + ',\n' +
                        '"rating": "2",\n' +
                        '"title": "Review Title",\n' +
                        '"review": "Review Text"\n' +
                        '}')
                .when()
                .port(this.serverPort)
                .post('/api/review')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body('id', not(0))

        cleanup:
        testHelper.clean()
    }

    def "get a review"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()
        Package aPackage = testHelper.aPackage
        Applicant reviewer = aPackage.petitioner.applicant

        LegalRepresentative attorney
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
        }

        Review review
        Review.withNewTransaction {
            review = reviewService.create(reviewer.refresh(), aPackage.refresh(), attorney, 4, "Title", "Review Text")
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPetitioner)
                .filter(document('get-a-review',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('Review id')
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/review/{id}", review.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("id", not(0))

        cleanup:
        Review.withNewTransaction {
            Review lastReview = Review.last()
            if (lastReview) {
                lastReview.delete()
            }
            TestUtils.deleteRepresentative(attorney.id)
        }
        testHelper.clean()
    }

    def "update a review"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()
        Package aPackage = testHelper.aPackage
        Applicant reviewer = aPackage.petitioner.applicant

        LegalRepresentative attorney
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
        }

        Review review
        Review.withNewTransaction {
            review = reviewService.create(reviewer.refresh(), aPackage.refresh(), attorney, 4, "Title", "Review Text")
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPetitioner)
                .filter(document('update-a-review',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the Review')
                        ),
                        requestFields(
                                fieldWithPath('rating').type(Integer).description('rating of Review'),
                                fieldWithPath('title').description('title of Review'),
                                fieldWithPath('review').description('review text of Review')
                        )))
                .body('{\n' +
                        '"rating": "2",\n' +
                        '"title": "Review Title",\n' +
                        '"review": "Review Text"\n' +
                        '}')
                .when()
                .port(this.serverPort)
                .put("/api/review/{id}/", review.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("id", not(0))

        cleanup:
        Review.withNewTransaction {
            Review lastReview = Review.last()
            if (lastReview) {
                lastReview.delete(failOnError: true)
            }
            TestUtils.deleteRepresentative(attorney.id)
        }
        testHelper.clean()
    }

    def "test Get Review of a Representative for a Package"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildUsersForPackageApplicants()
                .logInPackagePetitioner()
        Package aPackage = testHelper.aPackage
        Applicant reviewer = aPackage.petitioner.applicant
        Review reviewInstance
        LegalRepresentative attorney
        Review.withNewTransaction {
            aPackage.refresh()
            attorney = aPackage.orderedAssignees.first().representative
            reviewInstance = reviewService.create(reviewer.refresh(), aPackage, attorney, 5, "Title", "Review Text")
            attorney.id
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPetitioner)
                .filter(document('get-package-attorney-review',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('packageId').description('Package id'),
                                parameterWithName('representativeId').description('Representative id')
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/package/{packageId}/attorney/{representativeId}/review", aPackage.id, attorney.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("id", equalTo(reviewInstance.id.intValue()))

        cleanup:
        Review.withNewTransaction {
            Review review = Review.last()
            if (review) {
                review.delete(failOnError: true)
            }
        }
        testHelper.clean()
    }

    def "test update read and reply to the review"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep()
                .buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()

        Review review
        Review.withNewTransaction {
            review = reviewService.create(testHelper.aPackage.refresh().petitioner.applicant, testHelper.aPackage, testHelper.packageLegalRepresentative.refresh(), 3, "Title", "Review Text")
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('update-read-reply-review',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the Review')
                        ),
                        requestFields(
                                fieldWithPath('reply').description('reply to the Review'),
                                fieldWithPath('read').type(Boolean).description('read status of Review')
                        )))
                .body('{\n' +
                        '"reply": "Reply Text",\n' +
                        '"read": "true"\n' +
                        '}')
                .when()
                .port(this.serverPort)
                .patch("/api/attorneys/review/{id}", review.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(review?.id as int))
                .body('reply', equalTo('Reply Text'))
                .body('read', equalTo(true))

        cleanup:
            testHelper.clean()
    }

    def "getting a list of reviews"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep()
                .buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()

        Review review
        Review.withNewTransaction {
            review = reviewService.create(testHelper.aPackage.refresh().petitioner.applicant, testHelper.aPackage, testHelper.packageLegalRepresentative.refresh(), 5, "Title", "Review Text")
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('list-of-reviews',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('rating').description('Rating of Review').optional(),
                                parameterWithName('offset').description('Start index').optional(),
                                parameterWithName('max').description('Max no. of records').optional(),
                                parameterWithName('sort').description('Column name to sort').optional(),
                                parameterWithName('order').description('Order asc/desc').optional(),
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/attorneys/reviews?rating=5&offset=0&max=1&sort=rating&order=desc")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", equalTo(1))

        cleanup:
        testHelper.clean()
    }

}
