package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.*
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.JsonConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.path.json.config.JsonPathConfig
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.restdocs.request.RequestParametersSnippet
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class FindAttorneySpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation('build/generated-snippets')

    @Value('${local.server.port}')
    Integer serverPort
    @Autowired
    ProfileService profileService
    @Autowired
    AttorneyService attorneyService
    @Autowired
    PackageService packageService

    protected RequestSpecification spec

    ResponseFieldsSnippet responseFields
    ResponseFieldsSnippet attorneyContactFields
    RequestParametersSnippet searchParams

    def setup() {
        this.spec = new RequestSpecBuilder().setConfig(RestAssuredConfig.config().jsonConfig(JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.responseFields = responseFields(
                fieldWithPath('states').description('List of states'),
                fieldWithPath('states[].id').description('Id of the state'),
                fieldWithPath('states[].name').description('Name of the state'),
                fieldWithPath('states[].active').description('Activity status'),
                fieldWithPath('countries').description('List of countries'),
                fieldWithPath('countries[].id').description('Id of the country'),
                fieldWithPath('countries[].name').description('Name of the country'),
                fieldWithPath('countries[].active').description('Activity status'),
                fieldWithPath('languages').description('List of languages'),
        fieldWithPath('languages[].id').description('Id of the language'),
        fieldWithPath('languages[].name').description('Name of the language'),
        fieldWithPath('languages[].active').description('Activity status'))
        this.attorneyContactFields = responseFields(
                fieldWithPath('id').description('Id of representative'),
                fieldWithPath('contactInfo').description('Contacts info of representative'),
                fieldWithPath('contactInfo.office').description('Office phone'),
                fieldWithPath('contactInfo.mobile').description('Mobile phone'),
                fieldWithPath('contactInfo.email').description('Email'),
                fieldWithPath('contactInfo.fax').description('Fax phone'),
                fieldWithPath('socialMedia').description('Social Media info of representative'),
                fieldWithPath('socialMedia.facebook').description('Facebook URL'),
                fieldWithPath('socialMedia.twitter').description('Twitter URL'),
                fieldWithPath('socialMedia.linkendin').description('Linkedin URL'),
                fieldWithPath('socialMedia.youtube').description('Youtube URL'),
                fieldWithPath('websiteUrl').description('Website URL of the representative'))

        this.searchParams = requestParameters(
                parameterWithName('states').description('States to search for').optional(),
                parameterWithName('countries').description('Countries to search for').optional(),
                parameterWithName('language').description('Language spoken by the representative').optional(),
                parameterWithName('max').description('Maximum items in the response. Default 25').optional(),
                parameterWithName('offset').description('Offset for the page. Default 0').optional())

    }

    def "test get-search-config"() {
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('search-attorney-config',
                preprocessResponse(prettyPrint()), this.responseFields))
                .when()
                .port(this.serverPort)
                .get('/api/public/marketing-config')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('states', is(notNullValue()))
                .body('countries', is(notNullValue()))
                .body('languages', is(notNullValue()))
    }

    def "can find Attorneys with states, countries and language"() {
        given:
        LegalRepresentative attorney
        LegalRepresentative attorney2
        LegalRepresentative attorney3
        LegalRepresentative.withNewTransaction {
            User user = new User(username: 'registeredattorney', language: 'En/US', password: 'registeredAttorneyPassword', paid: true)
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney.spokenLanguages = [Language.ENGLISH, Language.CHINESE]
            attorney = attorneyService.createAttorney(attorney)

            User user2 = new User(username: 'registeredattorney2', language: 'En/US', password: 'registeredAttorneyPassword2', paid: true)
            Profile profile2 = new Profile(user: user2, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney2@easyvisa.com')
            attorney2 = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile2, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney2.spokenLanguages = [Language.ENGLISH, Language.CHINESE]
            attorney2 = attorneyService.createAttorney(attorney2)

            User user3 = new User(username: 'registeredattorney3', language: 'En/US', password: 'registeredAttorneyPassword3', paid: true, activeMembership: false)
            Profile profile3 = new Profile(user: user3, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney3@easyvisa.com')
            attorney3 = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile3, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney3.spokenLanguages = [Language.ENGLISH, Language.CHINESE, Language.ARABIC]
            attorney3 = attorneyService.createAttorney(attorney3)

            attorney.profile.address = new Address(state: State.FLORIDA)
            attorney2.profile.address = new Address(country: Country.CANADA)
            attorney3.profile.address = new Address(country: Country.CANADA)

            attorney.save(failOnError: true)
            attorney2.save(failOnError: true)
            attorney3.save(failOnError: true)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('search-representatives',
                preprocessResponse(prettyPrint()),
                this.searchParams,
                responseFields(
                        fieldWithPath('[].id').description('Attorney Id'),
                        fieldWithPath('[].name').description('Attorney name'),
                        fieldWithPath('[].photoUrl').description('URL to Attorney photo'),
                        fieldWithPath('[].representativeType').description('Representative type'),
                        fieldWithPath('[].representativeTypeName').description('Representative type display name'),
                        fieldWithPath('[].languagesSpoken').description('List of spoken languages'),
                        fieldWithPath('[].numberOfReviews').description('Number of reviews'),
                        fieldWithPath('[].averageReviewRating').description('Average review rating'),
                        fieldWithPath('[].numberOfApprovedArticles').description('Count fo approved articles'),
                        fieldWithPath('[].licensedRegions').description('List of licensed regions'),
                        fieldWithPath('[].maxYearsLicensed').description('Licensed years'))))
                .when()
                .port(this.serverPort)
                .get('/api/public/attorneys/search?max=25&offset=0&countries=UNITED_STATES&countries=CANADA&states=FLORIDA&states=NEW_YORK&language=CHINESE')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('size()', equalTo(2))

        cleanup:
        LegalRepresentative.withNewTransaction {
            if (attorney) {
                TestUtils.deleteRepresentative(attorney.id)
            }
            if (attorney2) {
                TestUtils.deleteRepresentative(attorney2.id)
            }
            if (attorney3) {
                TestUtils.deleteRepresentative(attorney3.id)
            }
        }
    }

    def "can find Attorneys contact-info by attorney-id"() {
        given:
        LegalRepresentative attorney
        LegalRepresentative.withNewTransaction {
            User user = new User(username: 'registeredattorney', language: 'En/US', password: 'registeredAttorneyPassword', paid: true)
            Profile profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle', email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile, mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            attorney.save(failOnError: true)

        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-representative-contact-info',
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName('id').description('id of the attorney')),
                this.attorneyContactFields))
                .when()
                .port(this.serverPort)
                .get('/api/public/attorneys/{id}/contact-info', attorney.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', equalTo(attorney.id as int))

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .when()
                .port(this.serverPort)
                .get('/api/public/attorneys/{id}/contact-info', 404)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_NOT_FOUND))
        cleanup:
        if (attorney) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteRepresentative(attorney.id)
            }
        }
    }

    def "can find Attorneys ratings details by attorney-id"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService,])
        testHelper.buildPackageLegalRep()
                .buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()

        LegalRepresentative attorney = testHelper.packageLegalRepresentative
        LegalRepresentative.withNewTransaction {
            [ 5, 4, 4, 3, 2, 1 ].each {
                Integer rating = it
                testHelper.buildPackageLegalRepReviewByBeneficiary(rating)
            }
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-representative-ratings-info',
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName('id').description('id of the attorney')),
                responseFields(
                        fieldWithPath('total').description('Total ratings'),
                        fieldWithPath('ratings').description('Ratings details'),
                        fieldWithPath('ratings[].value').description('Rating values'),
                        fieldWithPath('ratings[].count').description('Number of ratings with this value'))))
                .when()
                .port(this.serverPort)
                .get('/api/public/attorneys/{id}/ratings', attorney.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('total', equalTo(6))
                .body('ratings[4].count', equalTo(1))
                .body('ratings[3].count', equalTo(1))
                .body('ratings[2].count', equalTo(1))
                .body('ratings[1].count', equalTo(2))
                .body('ratings[0].count', equalTo(1))

        cleanup:
        testHelper.clean()
    }

    def "can get public attorney details by attorney-id"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService,])
        testHelper.buildNoPetitionerLeadPackage()
                .buildUsersForPackageApplicants()
                .buildPackageLegalRepReviewByBeneficiary()
                .buildPackageLegalRepReviewByBeneficiary(4)
                .buildPackageLegalRepReviewByBeneficiary(4)
                .buildPackageLegalLicensedRegion()
                .buildPackageLegalRepArticleBonus()
                .buildPackageLegalRepArticleBonus()
                .buildPackageLegalRepArticleBonus(new Date(), Boolean.FALSE)

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .filter(document('get-representative-profile-info',
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName('id').description('id of the attorney')),
                responseFields(
                        fieldWithPath('lastName').description('Last name'),
                        fieldWithPath('firstName').description('first name '),
                        fieldWithPath('middleName').description('middle name'),
                        fieldWithPath('profilePhoto').description('URL for profile photo of attorney.'),
                        fieldWithPath('id').description('ID'),
                        fieldWithPath('numberOfReviews').description('Number of reviews'),
                        fieldWithPath('averageReviewRating').description('Average review rating'),
                        fieldWithPath('numberOfApprovedArticles').description('Count fo approved articles'),
                        fieldWithPath('maxYearsLicensed').description('Licensed years'),
                        fieldWithPath('officeEmail').description('Office Email'),
                        fieldWithPath('officeAddress').description('Office Address'),
                        fieldWithPath('officeAddress.line1').description('Line 1 of the address'),
                        fieldWithPath('officeAddress.line2').description('Line 2 of the address'),
                        fieldWithPath('officeAddress.city').description('City'),
                        fieldWithPath('officeAddress.country').description('Country'),
                        fieldWithPath('officeAddress.state').description('State'),
                        fieldWithPath('officeAddress.province').description('Province'),
                        fieldWithPath('officeAddress.zipCode').description('Zip code'),
                        fieldWithPath('officeAddress.postalCode').description('Postal code'),
                        fieldWithPath('attorneyType').description('AttorneyType (Solo Practitioner or Member of a law firm)'),
                        fieldWithPath('representativeType').description('Representative Type (Attorney or Accredited Representative)'),
                        fieldWithPath('representativeTypeName').description('Representative Type (Attorney or Accredited Representative) display name'),
                        fieldWithPath('organizationName').description('Organization Name (Not Solo Organization)'),
                        fieldWithPath('faxNumber').description('Fax Number'),
                        fieldWithPath('officePhone').description('Office Phone number'),
                        fieldWithPath('mobilePhone').description('Mobile Phone number'),
                        fieldWithPath('websiteUrl').description('website URL'),
                        fieldWithPath('linkedinUrl').description('Linkedin URL'),
                        fieldWithPath('twitterUrl').description('twiiter URL'),
                        fieldWithPath('facebookUrl').description('Facebook URL'),
                        fieldWithPath('youtubeUrl').description('Youtube URL'),
                        fieldWithPath('licensedRegions').description('List of regions where attorney is licensed.'),
                        fieldWithPath('licensedRegions[].dateLicensed').description('Licensed date'),
                        fieldWithPath('licensedRegions[].state').description('State of the license'),
                        fieldWithPath('licensedRegions[].barNumber').description('Bar number of the license'),
                        fieldWithPath('licensedRegions[].id').description('License id'),
                        fieldWithPath('summary').description('Profile summary'),
                        fieldWithPath('awards').description('Awards received'),
                        fieldWithPath('experience').description('Experience'),
                        fieldWithPath('workingHours').description('List of working hours for each weekday'),
                        fieldWithPath('languages').description('List of languages spoken'),
                        fieldWithPath('stateBarNumber').description('State Bar Number'),
                        fieldWithPath('uscisOnlineAccountNo').description('USCIS online account no'),
                        fieldWithPath('practiceAreas').description('List of practices'),
                        fieldWithPath('education').description('List of education qualifications'))))
                .when()
                .port(this.serverPort)
                .get('/api/public/attorneys/{id}', testHelper.packageLegalRepresentative.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('firstName', equalTo(testHelper.packageLegalRepresentative.profile.firstName))
                .body('numberOfReviews', equalTo(0))
                .body('averageReviewRating', equalTo(BigDecimal.ZERO.setScale(1)))
                .body('numberOfApprovedArticles', equalTo(0))
                .body('maxYearsLicensed', equalTo(0))

        cleanup:
        testHelper.deletePackageOnly()
                .deletePackageLegalRep()
                .deleteOrganization()
    }

}
