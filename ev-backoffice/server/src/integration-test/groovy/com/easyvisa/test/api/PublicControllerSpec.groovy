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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
//@Rollback
class PublicControllerSpec extends TestMockUtils {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation('build/generated-snippets')

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

    def testGetAttorneyReviews() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort:serverPort,
                                                                 attorneyService:attorneyService,
                                                                 packageService:packageService,
                                                                 profileService: profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()

        LegalRepresentative attorney = testHelper.packageLegalRepresentative
        Package aPackage = testHelper.aPackage
        Applicant reviewer = aPackage.petitioner.applicant
        Applicant reviewer2 = aPackage.principalBeneficiary

        Review review
        Review review2
        Review.withNewTransaction {
            review = reviewService.create(reviewer, aPackage, attorney, 5, 'Title', 'Review Text')
            review2 = reviewService.create(reviewer2, aPackage, attorney, 5, 'Title 2', 'Review Text 2')
            review2.save(flush:true)
            review2.refresh()
            review2.reply = 'Reply 2'
            review2.save(failOnError:true)
            reviewService.create(reviewer, aPackage, attorney, 4, 'Title 3', 'Review Text 3')
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('attorney-public-reviews',
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName('id').description('id of the attorney')
                ),
                requestParameters(
                        parameterWithName('rating').description('Rating of Review').optional(),
                        parameterWithName('offset').description('Start index').optional(),
                        parameterWithName('max').description('Max no. of records').optional(),
                        parameterWithName('sort').description('Column name to sort: "rating" and "date"').optional(),
                        parameterWithName('order').description('Order asc/desc').optional(),
                ),
                responseFields(
                        fieldWithPath('[].id').description('Review id'),
                        fieldWithPath('[].reviewer').description('Reviewer First Name'),
                        fieldWithPath('[].rating').description('Rating value'),
                        fieldWithPath('[].title').description('Rating title'),
                        fieldWithPath('[].review').description('Review text'),
                        fieldWithPath('[].reply').description('Attorney reply').optional(),
                        fieldWithPath('[].dateCreated').description('Review date'))))
                .when()
                .port(this.serverPort)
                .get("/api/public/attorneys/{id}/reviews?rating=5&offset=0&max=10&sort=rating&order=desc", attorney.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .header('X-total-count', equalTo(2 as String))
                .body('size()', equalTo(2))
                .body('[0].title', equalTo('Title'))
                .body('[1].title', equalTo('Title 2'))

        cleanup:
        Review.withNewTransaction {
            Review.findAllByRepresentative(attorney)*.delete(failOnError:true)
        }
        testHelper.clean()
    }
}
