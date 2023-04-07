package com.easyvisa.test.api

import com.easyvisa.AttorneyService
import com.easyvisa.Email
import com.easyvisa.Package
import com.easyvisa.PackageService
import com.easyvisa.ProfileService
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.utils.PackageTestBuilder
import grails.gorm.transactions.Transactional
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
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
//@Rollback
class EmailControllerSpec extends Specification {

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

    protected ResponseFieldsSnippet emailResponseFields

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.emailResponseFields = responseFields(
                fieldWithPath('content').description('Content of email'),
                fieldWithPath('representativeId').description('Id of representative for email'),
                fieldWithPath('packageId').description('Id of Package for email'),
                fieldWithPath('id').description('Id of email'),
                fieldWithPath('templateType').description('TemplateType for email'),
                fieldWithPath('subject').description('Subject of email'),
                fieldWithPath('responseMessage').description('Response message').optional().type(JsonFieldType.STRING))
    }


    def "test get email template content by templateType"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('get-email-template',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('representativeId').description('Representative Id for which templates should be fetched [Optional]').optional(),
                                parameterWithName('packageId').description('Package Id for which templates should be fetched [Optional]').optional(),
                                parameterWithName('defaultTemplate').description('Boolean to indicate if defaulte template should be returned. [Optional]').optional(),
                        ),
                        responseFields(
                                fieldWithPath('content').description('Content of the email template'),
                                fieldWithPath('subject').description('Content of the email template'),
                                fieldWithPath('templateType').description('Content of the email template'),
                                fieldWithPath('representativeId').description('Content of the email template'),
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/email-templates/NEW_CLIENT?representativeId=${testHelper.packageLegalRepresentative.id}&defaultTemplate=true")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('content', notNullValue())

        cleanup:
        testHelper.clean()
    }

    def "test get multiple email templates content by templateTypes"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('get-email-templates',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('templateType').description('Template types to fetch').optional(),
                                parameterWithName('representativeId').description('Representative Id for which templates should be fetched [Optional]').optional(),
                                parameterWithName('packageId').description('Package Id for which templates should be fetched [Optional]').optional(),
                                parameterWithName('defaultTemplate').description('Boolean to indicate if defaulte template should be returned. [Optional]').optional(),
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/email-templates?templateType=NEW_CLIENT&templateType=CLOSING_TEXT&representativeId=${testHelper.packageLegalRepresentative.id}")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('size()', equalTo(2))

        cleanup:
        testHelper.clean()
    }

    def "create a new email"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildNoPetitionerLeadPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('create-email',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('templateType').description('type of Email'),
                                fieldWithPath('content').description('content of email'),
                                fieldWithPath('packageId').optional().type(Long).description('package to be associated to email (optional)'),
                                fieldWithPath('representativeId').optional().type(Long).description('type of Email template to find.'),
                                fieldWithPath('subject').type(Long).description('Subject of the email template.'),
                                fieldWithPath('sendEmail').type(Long).description('Boolean to indicate if the email should be sent after creating').optional()
                        ), this.emailResponseFields))
                .body("""
{"templateType":"NEW_CLIENT",
"content":"This is the content for the template with |LEGAL_REP_EMAIL|",
"subject":"welcome to easyvisa",
"packageId":${testHelper.aPackage.id}
}
""")
                .when()
                .port(this.serverPort)
                .post('/api/email')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body('id', notNullValue())
                .body('content', equalTo('This is the content for the template with |LEGAL_REP_EMAIL|'))

        cleanup:
        testHelper.clean()
    }

    def "get an email by id"() {
        given:

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService,
                                                                 packageService : packageService])

        testHelper.buildPackageLegalRep()
                .buildNoPetitionerLeadPackage()
                .logInPackageLegalRep()
        Email email

        // Email has unique constraint - unique templateType for a Package
        // Initially, the test assumed that db is empty and created an Email record without package
        // which is not correct.
        // Refactored the test to associate an email with a package
        // now test is not dependent on empty db
        Email.withNewTransaction {
            email = new Email(content: "This is the content", templateType: EmailTemplateType.ADDITIONAL_FEES, subject: "New applicant email", aPackage: testHelper.aPackage.refresh())
            email.save(failOnError: true)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('get-email',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("id of the email to fetch")
                        ), this.emailResponseFields))
                .when()
                .port(this.serverPort)
                .get("/api/email/{id}", email.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', notNullValue())
                .body('content', equalTo('This is the content'))

        cleanup:
        Email.withNewTransaction {
            email.delete(failOnError: true)
        }
        testHelper.clean()
    }

    def "update an email"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService,
                                                                 packageService : packageService])

        testHelper.buildPackageLegalRep()
                .buildNoPetitionerLeadPackage()
                .logInPackageLegalRep()


        Email email

        Email.withNewTransaction {
            email = new Email(content: "This is the content", templateType: EmailTemplateType.ADDITIONAL_FEES, subject: "New applicant email", aPackage: testHelper.aPackage.refresh())
            email.save(failOnError: true)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('update-email',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("id of the email to update")
                        ),
                        requestFields(
                                fieldWithPath('content').description('content of email'),
                                fieldWithPath('subject').type(Long).description('Subject of the email template.'),
                                fieldWithPath('sendEmail').type(Long).description('Boolean to indicate if the email should be sent after creating').optional()
                        ),
                        this.emailResponseFields))
                .body('''{
"content":"updated content for email",
"subject":"email subject"
}''')
                .when()
                .port(this.serverPort)
                .put("/api/email/{id}", email.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', notNullValue())
                .body('content', equalTo('updated content for email'))

        cleanup:
        if (email) {
            Email.withNewTransaction {
                email.delete(failOnError: true)
            }
        }
        testHelper.clean()
    }


    def "preview an email"() {
        given:

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService,
                                                                 packageService : packageService])

        testHelper.buildPackageLegalRep()
                .buildNoPetitionerLeadPackage()
                .logInPackageLegalRep()


        Email email

        Email.withTransaction {
            email = new Email(content: "This is the content", templateType: EmailTemplateType.ADDITIONAL_FEES, subject: "New applicant email", aPackage: testHelper.aPackage.refresh())
            email.save(failOnError: true)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('preview-email',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("id of the email to preview")
                        ),
                        responseFields(
                                fieldWithPath('preview').description('Generated html for the html'),
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/email/{id}/preview", email.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('preview', equalTo('This is the content'))

        cleanup:
        if (email) {
            Email.withNewTransaction {
                email.delete(failOnError: true)
            }
        }
        testHelper.clean()
    }

    def "update an email template"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('update-email-template',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('content').description('content of email'),
                                fieldWithPath('subject').type(Long).description('Subject of the email template.'),
                                fieldWithPath('templateType').type(Long).description('Template type'),
                                fieldWithPath('representativeId').type(Long).description('Representative Id'),
                        ),
                        responseFields(
                                fieldWithPath('content').description('Content of the email template'),
                                fieldWithPath('representativeId').description('Representative Id'),
                                fieldWithPath('templateType').description('Template Type'),
                                fieldWithPath('subject').description('Subject of the email template'),
                        )))
                .body("""{
"content":"some content for closing text",
"subject":"email subject",
"representativeId":${testHelper.packageLegalRepresentative.id},
"templateType":"CLOSING_TEXT"}""")
                .when()
                .port(this.serverPort)
                .put("/api/email-templates")
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('content', equalTo('some content for closing text'))
                .body('templateType', equalTo('CLOSING_TEXT'))

        cleanup:
        testHelper.clean()
    }

    def "find an email by packageId,representativeId and templateType"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()

        Email email
        Email.withNewTransaction {
            email = new Email(content: "This is the content", templateType: EmailTemplateType.NEW_CLIENT,
                    subject: "New applicant email", aPackage: testHelper.aPackage.refresh(), attorney: testHelper.packageLegalRepresentative.refresh())
            email.save(failOnError: true)
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('find-email',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("templateType").description("Template type"),
                                parameterWithName("packageId").description("Id of package for email"),
                                parameterWithName("representativeId").description("Id of representative for email")
                        ), this.emailResponseFields))
                .when()
                .port(this.serverPort)
                .get("/api/emails/find?templateType=NEW_CLIENT&packageId=${testHelper.aPackage.id}&representativeId=${testHelper.packageLegalRepresentative.id}",)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('id', notNullValue())
                .body('content', equalTo('This is the content'))


        cleanup:
        testHelper.clean()
    }

    def "Preview email content by packageId,representativeId and templateType"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPetitionerAndBeneficiaryLeadPackage()
                .logInPackageLegalRep()
        String repName
        Package.withNewTransaction {
            repName = testHelper.aPackage.refresh().petitioner.profile.fullName
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('preview-email-template',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("templateType").description("Template type")
                        ),
                        requestFields(
                                fieldWithPath("packageId").description("Id of package for email"),
                                fieldWithPath("representativeId").description("Id of representative for email"),
                                fieldWithPath("content").description("Email content")),
                        responseFields(
                                fieldWithPath('preview').description('HTML Content for preview'),
                                fieldWithPath('subject').description('Subject of email'))))
                .body("""
{
"content":"some content for closing text with |PETITIONER_FULL_NAME|",
"representativeId":$testHelper.packageLegalRepresentative.id,
"packageId":$testHelper.aPackage.id
}
""")
                .when()
                .port(this.serverPort)
                .post("/api/email-templates/{templateType}/preview", EmailTemplateType.NEW_CLIENT)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('subject', notNullValue())
                .body('preview', equalTo("some content for closing text with $repName".toString()))

        cleanup:
        testHelper.clean()
    }

}
