package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.PracticeArea
import com.easyvisa.enums.RegistrationStatus
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
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
class ArticleControllerSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private ProfileService profileService
    @Autowired
    AttorneyService attorneyService
    @Autowired
    OrganizationService organizationService

    protected RequestSpecification spec
    protected ResponseFieldsSnippet responseFields

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.responseFields = responseFields(
                fieldWithPath('id').description('id of the article'),
                fieldWithPath('articleId').description('Article Id used by the marketing site'),
                fieldWithPath('author').description('Name of the author'),
                fieldWithPath('organizationName').description('name of organization'),
                fieldWithPath('location').description('location/category of article'),
                fieldWithPath('title').description('Title/heading of article'),
                fieldWithPath('content').description('content body of article, is HTML'),
                fieldWithPath('views').description('no of views of article'),
                fieldWithPath('words').description('no of words in the article'),
                fieldWithPath('approved').description('is Article approved or not'),
                fieldWithPath('url').description('Link for the article'),
                fieldWithPath('dateSubmitted').description('Date when article was submitted'),
                fieldWithPath('dateDispositioned').description('Date when article was dispositioned'))
    }

    def 'Can create articles for a representative'() {
        RestResponse resp
        LegalRepresentative attorney
        Organization organization

        LegalRepresentative.withNewTransaction {
            User user = new User(username: 'testattorney', language: 'En/US', password: 'registeredAttorneyPassword')
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true)
            organization = TestUtils.createOrganization('test org')
            def organizationEmployee = organizationService.addAttorneyToOrganization(organization, attorney)
            organizationEmployee.save(failOnError: true)
            attorney.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'testattorney', 'registeredAttorneyPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + resp.json.access_token)
                .filter(document('create-user-article',
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath('locationId').type(String).description('Location/category id for the article'),
                        fieldWithPath('locationName').type(String).description('Location/category name for the article'),
                        fieldWithPath('title').type(String).description('Title for the article'),
                        fieldWithPath('content').description('Content body of the article'),
                        fieldWithPath('organizationId').description('Id of organization'),
                )))
                .body("""{
"locationId":"1",
"locationName":"location for the article",
"title":"title for the article",
"content":"${(1..601).collect { "word ${it}" }.join(' ')}",
"organizationId":${organization.id}
}
""")
                .when()
                .port(this.serverPort)
                .post('/api/articles')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body("locationName", equalTo('location for the article'))
                .body("title", equalTo('title for the article'))

        cleanup:
        if (attorney) {
            Article.withNewTransaction {
                Article.findAllByAuthor(LegalRepresentative.get(attorney.id)).each {
                    it.delete(failOnError: true)
                }
                TestUtils.deleteOrganization(organization)
                TestUtils.deleteRepresentative(attorney.id)
            }
        }
    }

    def 'get articles for an Attorney'() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPackageLegalRepArticleBonus()
                .buildPackageLegalRepArticleBonus()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-attorney-articles',
                preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('representativeId').description('Filter by RepresentativeId ID').optional(),
                                parameterWithName('organizationId').description('Filter by Organization ID').optional(),
                                parameterWithName('offset').description('Start index').optional(),
                                parameterWithName('max').description('Max no. of records').optional(),
                                parameterWithName('sort').description('Column name to sort').optional(),
                                parameterWithName('order').description('Order asc/desc').optional(),
                        )
                ))
                .when()
                .port(this.serverPort)
                .get("/api/public/attorneys/articles?representativeId=$testHelper.packageLegalRepresentative.id")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", equalTo(2))
                .header("X-TOTAL-COUNT", equalTo("2"))

        cleanup:
        testHelper.deletePackageLegalRep()
                .deleteOrganization()
    }

}
