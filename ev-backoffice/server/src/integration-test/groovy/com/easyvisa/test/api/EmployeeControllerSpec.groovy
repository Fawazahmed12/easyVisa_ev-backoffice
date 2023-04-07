package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.enums.TransactionSource
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper
import io.restassured.builder.RequestSpecBuilder
import io.restassured.mapper.TypeRef
import io.restassured.parsing.Parser
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.grails.datastore.mapping.core.DatastoreUtils
import org.grails.spring.GrailsApplicationContext
import org.hibernate.SessionFactory
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionSynchronizationUtils
import spock.lang.Unroll

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class EmployeeControllerSpec extends TestMockUtils {

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
    private AdminService adminService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    @Autowired
    SessionFactory sessionFactory

    private TaxService taxServiceMock = Mock(TaxService)

    protected RequestSpecification spec

    ResponseFieldsSnippet responseFields

    def setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        this.responseFields = responseFields(
                fieldWithPath('id').description('Id of the attorney'),
                fieldWithPath('firstName').description('First name of the created user'),
                fieldWithPath('middleName').description('middle name of the created user'),
                fieldWithPath('lastName').description('last name of the created user'),
                fieldWithPath('easyVisaId').description('EasyVisaId generated for the created user'),
                fieldWithPath('email').description('email of the created user'),
                fieldWithPath('officeEmail').description('officeEmail of the created user'),
                fieldWithPath('officeAddress').description('officeAddress of the created user'),
                fieldWithPath('profilePhoto').description('URL for profile photo of user.'),
                fieldWithPath('registrationStatus').description('Registration Status of the user'),
                fieldWithPath('attorneyType').description('AttorneyType of the user'),
                fieldWithPath('representativeType').description('RepresentativeType of the user'),
                fieldWithPath('officePhone').description('Office phone of the user'),
                fieldWithPath('mobilePhone').description('Mobile phone of the user'),
                fieldWithPath('faxNumber').description('Fax number of the user'),
                fieldWithPath('facebookUrl').description('facebook URL of the user'),
                fieldWithPath('twitterUrl').description('twitter URL of the user'),
                fieldWithPath('youtubeUrl').description('youtube URL of the user'),
                fieldWithPath('linkedinUrl').description('linkedin URL of the user'),
                fieldWithPath('websiteUrl').description('website URL of the user'),
                fieldWithPath('activeOrganizationId').description('Id of the active Organization of the user'),
                fieldWithPath('organizations').description('Name and Ids of the organizations representative is associated with'),
                fieldWithPath('feeSchedule').description('Fee Schedule of the representative'),
                fieldWithPath('creditBalance').description('Credit balance for the representative'))
        updateToMock(attorneyService.accountService, paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(attorneyService.accountService, paymentService, taxService)
    }

    @Unroll
    def "Test create-employee #Label"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(isAdmin)
                .logInPackageLegalRep()

        String reqBody = [firstName  : "Attr",
                          middleName : "Attr",
                          lastName   : "Attorney",
                          email      : "employee${TestUtils.randomNumber()}@lawoffice101.com",
                          officePhone: "+639171111111",
                          mobilePhone: "+639171111111",
                          position   : position.toString()] as JSON

        expect:
        Map<String, Object> responseBody = given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('create-employee',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the Organization')
                        ),
                        requestFields(
                                fieldWithPath('firstName').description('First name of the user'),
                                fieldWithPath('middleName').description('Middle name of the user (optional)').optional(),
                                fieldWithPath('lastName').description('Last name of the user'),
                                fieldWithPath('email').description('Office email i.e. employee.officeEmail'),
                                fieldWithPath('officePhone').description('Home phone').optional(),
                                fieldWithPath('mobilePhone').description('Mobile phone').optional(),
                                fieldWithPath('position').description('Employee position'),
                        )))
                .body(reqBody)
                .when()
                .port(this.serverPort)
                .post("/api/organizations/{id}/employees", testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(responseCode))
                .extract()
                .body()
                .as(new TypeRef<Map<String, Object>>() {})


        // validate default isAdmin flag
        if (isAdmin) {
            assert responseBody.containsKey('isAdmin')
            assert responseBody['isAdmin'] == respAdmin
        }

        cleanup:
        if (isAdmin) {
            Employee.withNewTransaction {
                TestUtils.deleteEmployee(OrganizationEmployee
                        .findByOrganizationAndPosition(testHelper.organization?.refresh(), position)?.employee)
            }

        }

        testHelper.clean()

        where:
        Label               | isAdmin | responseCode            | position                  | respAdmin
        "as Admin User"     | true    | HttpStatus.SC_CREATED   | EmployeePosition.EMPLOYEE | false
        "as Non-Admin User" | false   | HttpStatus.SC_FORBIDDEN | EmployeePosition.EMPLOYEE | false
        "Manager"           | true    | HttpStatus.SC_CREATED   | EmployeePosition.MANAGER  | true
        "Partner"           | true    | HttpStatus.SC_CREATED   | EmployeePosition.PARTNER  | true
        "Trainee"           | true    | HttpStatus.SC_CREATED   | EmployeePosition.TRAINEE  | false

    }

    @Unroll
    def "test update-employee #Label"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(isAdmin)
                .buildTrainee()
                .logInPackageLegalRep()

        String reqBody = [firstName  : "Attr",
                          middleName : "Attr",
                          lastName   : "Attorney",
                          email      : "employee${TestUtils.randomNumber()}@lawoffice101.com",
                          officePhone: "+639171111111",
                          mobilePhone: "+639171111111",
                          position   : position.toString(),
                          status     : "ACTIVE",
                          isAdmin    : false] as JSON

        expect:
        Map<String, Object> responseBody = given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('update-employee',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the Organization'),
                                parameterWithName('employeeId').description('id of the Employee')
                        ),
                        requestFields(
                                fieldWithPath('firstName').description('First name of the user'),
                                fieldWithPath('middleName').description('Middle name of the user (optional)').optional(),
                                fieldWithPath('lastName').description('Last name of the user'),
                                fieldWithPath('email').description('Office email i.e. employee.officeEmail').optional(),
                                fieldWithPath('officePhone').description('Home phone').optional(),
                                fieldWithPath('mobilePhone').description('Mobile phone').optional(),
                                fieldWithPath('status').description('status of employee'),
                                fieldWithPath('position').description('Position of employee'),
                                fieldWithPath('isAdmin').type(Boolean).description('is Admin role'),
                        )))
                .body(reqBody)
                .when()
                .port(this.serverPort)
                .put("/api/organizations/{id}/employees/{employeeId}", testHelper.organization.id, testHelper.trainee.id)
                .then()
                .assertThat()
                .statusCode(is(responseCode))
                .extract()
                .body()
                .as(new TypeRef<Map<String, Object>>() {})

        // validate default isAdmin flag
        if (isAdmin) {
            assert responseBody.containsKey('isAdmin')
            assert responseBody['isAdmin'] == respAdmin
        }

        cleanup:
        testHelper.clean()

        where:

        Label               | isAdmin | responseCode            | position                  | respAdmin
        "as Admin User"     | true    | HttpStatus.SC_OK        | EmployeePosition.EMPLOYEE | false
        "as Non-Admin User" | false   | HttpStatus.SC_FORBIDDEN | EmployeePosition.EMPLOYEE | false
        "To Manager"        | true    | HttpStatus.SC_OK        | EmployeePosition.MANAGER  | true
        "To Trainee"        | true    | HttpStatus.SC_OK        | EmployeePosition.TRAINEE  | false
    }

    @Unroll
    def "test show-employee #Label"() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep(isAdmin)
                .buildTrainee()
                .logInPackageLegalRep()
        String firstName = null
        if (isAdmin) {
            firstName = testHelper.trainee.profile.firstName
        }

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header("Authorization", testHelper.accessTokenPackageLegalRep)
                .filter(document('show-employee',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the Organization'),
                                parameterWithName('employeeId').description('id of the Employee')
                        )))
                .when()
                .port(this.serverPort)
                .get("/api/organizations/{id}/employees/{employeeId}", testHelper.organization.id, testHelper.trainee.id)
                .then()
                .assertThat()
                .statusCode(is(responseCode))
                .body('profile.firstName', equalTo(firstName))

        cleanup:
        testHelper.clean()

        where:
        Label            | isAdmin | responseCode
        "Admin User"     | true    | HttpStatus.SC_OK
        "Non-Admin User" | false   | HttpStatus.SC_FORBIDDEN
    }

    def testRegisterEmployeeAsAttorney() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort    : serverPort,
                                                                 adminService  : adminService,
                                                                 profileService: profileService])
        testHelper.buildTrainee()
                .setPerApplicantFirst()
                .logInTrainee()
        Employee trainee = testHelper.trainee

        expect:
        String newToken = given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenTrainee)
                .filter(document('convert-employee-to-attorney',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('paymentMethod.address1').description('card address 1'),
                                fieldWithPath('paymentMethod.address2').description('card address 2'),
                                fieldWithPath('paymentMethod.addressCity').description('card city'),
                                fieldWithPath('paymentMethod.addressCountry').description('card country'),
                                fieldWithPath('paymentMethod.addressState').description('card state'),
                                fieldWithPath('paymentMethod.addressZip').description('card zip'),
                                fieldWithPath('paymentMethod.cardExpiration').description('card expiration'),
                                fieldWithPath('paymentMethod.cardHolder').description('card holder'),
                                fieldWithPath('paymentMethod.cardLastFour').description('card last four'),
                                fieldWithPath('paymentMethod.cardType').description('card type'),
                                fieldWithPath('paymentMethod.customerId').description('card customer id'),
                                fieldWithPath('paymentMethod.fmPaymentMethodId').description('payment method'),
                                fieldWithPath('profile.email').description('email'),
                                fieldWithPath('profile.faxNumber').description('fax number'),
                                fieldWithPath('profile.firstName').description('first name'),
                                fieldWithPath('profile.lastName').description('last name'),
                                fieldWithPath('profile.middleName').description('middle name'),
                                fieldWithPath('profile.mobilePhone').description('mobile phone'),
                                fieldWithPath('profile.officePhone').description('office phone'),
                                fieldWithPath('profile.officeAddress.city').description('city'),
                                fieldWithPath('profile.officeAddress.country').description('country'),
                                fieldWithPath('profile.officeAddress.line1').description('line 1'),
                                fieldWithPath('profile.officeAddress.line2').description('line 2'),
                                fieldWithPath('profile.officeAddress.state').description('state'),
                                fieldWithPath('profile.officeAddress.zipCode').description('zip code'),
                        )))
                .body("""{
  "paymentMethod": {
    "address1": "123 Main Street",
    "address2": null,
    "addressCity": "Los Angeles CA",
    "addressCountry": "UNITED_STATES",
    "addressState": "CA",
    "addressZip": "90025",
    "cardExpiration": "042029",
    "cardHolder": "Jack Smith",
    "cardLastFour": "1111",
    "cardType": "visa",
    "customerId": "${TestUtils.CUSTOMER_ID}",
    "fmPaymentMethodId": "token"
  },
  "profile": {
    "email": "${trainee.profile.email}",
    "faxNumber": "${trainee.faxNumber}",
    "firstName": "${trainee.profile.firstName}",
    "lastName": "${trainee.profile.lastName}",
    "middleName": "${trainee.profile.middleName}",
    "mobilePhone": "${trainee.mobilePhone}",
    "officePhone": "${trainee.officePhone}",
    "officeAddress": {
      "city": "Los Angeles CA",
      "country": "UNITED_STATES",
      "line1": "123 Main Street",
      "line2": "",
      "state": "CALIFORNIA",
      "zipCode": "90025"
    }
  }
}""")
                .when()
                .port(this.serverPort)
                .post('/api/employees/convert-to-attorney')
                .then()
                .using()
                .defaultParser(Parser.JSON)
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .extract().path('access_token')

        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', 'Bearer ' + newToken)
                .when()
                .port(this.serverPort)
                .get('/api/users/{id}/payment-method', trainee.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        OrganizationEmployee soloOrgEmp
        Organization soloOrg
        List<AccountTransaction> transactions
        LegalRepresentative attorney
        OrganizationEmployee organizationEmployee
        Set<Role> authorities
        Organization.withNewTransaction {
            // Newly created Solo Org after conversion
            soloOrgEmp = OrganizationEmployee.findByEmployeeAndOrganizationNotEqual(trainee, testHelper.organization)
            soloOrg = soloOrgEmp?.organization
            soloOrg.refresh()
            transactions = AccountTransaction.findAllByProfile(trainee.profile)
            attorney = LegalRepresentative.get(trainee.id)
            // Original Org for the trainee
            organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganization(trainee, testHelper.organization)
            authorities = User.get(trainee.user.id).authorities
            authorities.first().authority
        }

        assert soloOrg != null
        //assert position is PARTNER in the new solo org after conversion EV-3409
        assert soloOrgEmp.position == EmployeePosition.PARTNER
        assert attorney != null
        assert EmployeeStatus.ACTIVE == organizationEmployee.status
        assert EmployeePosition.TRAINEE == organizationEmployee.position
        TestUtils.assertPaidAccountTransaction(transactions, testHelper.perApplicant, 2)
        TestUtils.assertCustomAccountTransactions(transactions, testHelper.perApplicant, TransactionSource.REGISTRATION,
                TestUtils.REGISTRATION_FEE_MEMO)
        assert 1 == authorities.size()
        assert Role.ATTORNEY == authorities.first().authority

        cleanup:
        testHelper.deleteOrganization()
        Organization.withNewTransaction {
            TestUtils.deleteOrganization(soloOrg)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }


    def testRegisterEmployeeAsAttorneyFailedToPay() {
        given:


        failedToPayMock(paymentServiceMock, taxServiceMock)

        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort    : serverPort,
                                                                 adminService  : adminService,
                                                                 profileService: profileService])
        testHelper.buildTrainee()
                .setPerApplicantFirst()
                .logInTrainee()
        Employee trainee = testHelper.trainee

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenTrainee)
                .body("""{
  "paymentMethod": {
    "address1": "123 Main Street",
    "address2": null,
    "addressCity": "Los Angeles CA",
    "addressCountry": "UNITED_STATES",
    "addressState": "CA",
    "addressZip": "90025",
    "cardExpiration": "042029",
    "cardHolder": "Jack Smith",
    "cardLastFour": "1111",
    "cardType": "visa",
    "customerId": "${TestUtils.CUSTOMER_ID}",
    "fmPaymentMethodId": "token"
  },
  "profile": {
    "email": "${trainee.profile.email}",
    "faxNumber": "${trainee.faxNumber}",
    "firstName": "${trainee.profile.firstName}",
    "lastName": "${trainee.profile.lastName}",
    "middleName": "${trainee.profile.middleName}",
    "mobilePhone": "${trainee.mobilePhone}",
    "officePhone": "${trainee.officePhone}",
    "officeAddress": {
      "city": "Los Angeles CA",
      "country": "UNITED_STATES",
      "line1": "123 Main Street",
      "line2": "",
      "state": "CALIFORNIA",
      "zipCode": "90025"
    }
  }
}""")
                .when()
                .port(this.serverPort)
                .post('/api/employees/convert-to-attorney')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_BAD_REQUEST))
                .body('errors[0].type', equalTo(ErrorMessageType.PAYMENT_FAILED.name()))

        Organization soloOrg
        List<AccountTransaction> transactions
        LegalRepresentative attorney
        OrganizationEmployee organizationEmployee
        Set<Role> authorities
        Organization.withNewTransaction {
            soloOrg = OrganizationEmployee.findByEmployeeAndOrganizationNotEqual(trainee, testHelper.organization)?.organization
            transactions = AccountTransaction.findAllByProfile(trainee.profile)
            attorney = LegalRepresentative.get(trainee.id)
            organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganization(trainee, testHelper.organization)
            authorities = User.get(trainee.user.id).authorities
            authorities.first().authority
        }

        assert soloOrg == null
        assert attorney == null
        assert EmployeeStatus.ACTIVE == organizationEmployee.status
        assert EmployeePosition.TRAINEE == organizationEmployee.position
        assert transactions.empty
        assert 1 == authorities.size()
        assert Role.EMPLOYEE == authorities.first().authority

        cleanup:
        testHelper.deleteTrainee()
                .deleteOrganization()
    }

    def testRegisterEmployeeAsAttorneyNonEmployeeCall() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()

        LegalRepresentative representative = testHelper.packageLegalRepresentative
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body("""{
  "paymentMethod": {
    "address1": "123 Main Street",
    "address2": null,
    "addressCity": "Los Angeles CA",
    "addressCountry": "UNITED_STATES",
    "addressState": "CA",
    "addressZip": "90025",
    "cardExpiration": "042029",
    "cardHolder": "Jack Smith",
    "cardLastFour": "1111",
    "cardType": "visa",
    "customerId": "${TestUtils.CUSTOMER_ID}",
    "fmPaymentMethodId": "token"
  },
  "profile": {
    "email": "${representative.profile.email}",
    "faxNumber": "${representative.faxNumber}",
    "firstName": "${representative.profile.firstName}",
    "lastName": "${representative.profile.lastName}",
    "middleName": "${representative.profile.middleName}",
    "mobilePhone": "${representative.mobilePhone}",
    "officePhone": "${representative.officePhone}"
  }
}""")
                .when()
                .port(this.serverPort)
                .post('/api/employees/convert-to-attorney')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        testHelper.clean()
    }

}
