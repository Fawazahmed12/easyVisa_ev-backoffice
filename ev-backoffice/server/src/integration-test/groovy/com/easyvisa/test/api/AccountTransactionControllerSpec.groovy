package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.*
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.gorm.transactions.Rollback
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation

import java.math.RoundingMode

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Rollback
@Integration
class AccountTransactionControllerSpec extends TestMockUtils {

    private static final BigDecimal POSITIVE_BALANCE = new BigDecimal(1000).setScale(2)
    private static final String SUB_TOTAL_FIELD = 'subTotal'
    private static final String GRAND_TOTAL_FIELD = 'grandTotal'
    private static final String EST_TAX_FIELD = 'estTax'
    private static final String ID_FIELD = 'id'
    private static final String AUTHORIZATION = 'Authorization'
    private static final String ACCESS_TOKEN = 'access_token'
    private static final String TEST_MEMO = 'Test memo'

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private AttorneyService attorneyService

    @Autowired
    private ProfileService profileService

    @Autowired
    private AccountService accountService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    private TaxService taxServiceMock = Mock(TaxService)

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(this.restDocumentation)).build()
        updateToMock(attorneyService.accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(attorneyService.accountService, paymentService, taxService)
    }

    void testGetBalanceEmptyAccountTransactions() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}/balance', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body(SUB_TOTAL_FIELD, equalTo(BigDecimal.ZERO.intValue()))
                .body(GRAND_TOTAL_FIELD, equalTo(BigDecimal.ZERO.intValue()))
                .body(EST_TAX_FIELD, equalTo(BigDecimal.ZERO.intValue()))

        cleanup:
        testHelper.clean()
    }

    void testGetBalanceZero() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .addAccountTransactionToPackageLegalRep(POSITIVE_BALANCE, TransactionSource.MAINTENANCE, TEST_MEMO)
                .addPaymentAccountTransactionToPackageLegalRep(POSITIVE_BALANCE)
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}/balance', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body(SUB_TOTAL_FIELD, equalTo(BigDecimal.ZERO.intValue()))
                .body(GRAND_TOTAL_FIELD, equalTo(BigDecimal.ZERO.intValue()))
                .body(EST_TAX_FIELD, equalTo(BigDecimal.ZERO.intValue()))

        cleanup:
        testHelper.clean()
    }

    void testGetPositiveBalance() {
        given:
        estimateTaxMock(taxServiceMock, POSITIVE_BALANCE, POSITIVE_BALANCE)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .addAccountTransactionToPackageLegalRep(POSITIVE_BALANCE, TransactionSource.MAINTENANCE, TEST_MEMO)
                .addPaymentAccountTransactionToPackageLegalRep(POSITIVE_BALANCE)
                .addAccountTransactionToPackageLegalRep(POSITIVE_BALANCE, TransactionSource.MAINTENANCE, TEST_MEMO)
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, testHelper.accessTokenPackageLegalRep)
                .filter(document('get-balance',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName(ID_FIELD).description('id of the user')
                        ),
                        responseFields(
                                fieldWithPath(SUB_TOTAL_FIELD).description('user current balance'),
                                fieldWithPath(GRAND_TOTAL_FIELD).description('user current balance with taxes to be paid by.'),
                                fieldWithPath(EST_TAX_FIELD).description('taxes amount for user current balance'))))
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}/balance', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body(SUB_TOTAL_FIELD, equalTo(POSITIVE_BALANCE.floatValue()))
                .body(GRAND_TOTAL_FIELD, equalTo(POSITIVE_BALANCE.floatValue()))
                .body(EST_TAX_FIELD, equalTo(BigDecimal.ZERO.intValue()))

        cleanup:
        testHelper.clean()
    }

    void testGetNegativeBalance() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .addAccountTransactionToPackageLegalRep(POSITIVE_BALANCE.negate())
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}/balance', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body(SUB_TOTAL_FIELD, equalTo(POSITIVE_BALANCE.negate().floatValue()))
                .body(GRAND_TOTAL_FIELD, equalTo(BigDecimal.ZERO.intValue()))
                .body(EST_TAX_FIELD, equalTo(BigDecimal.ZERO.intValue()))

        cleanup:
        testHelper.clean()
    }

    void testPayBalanceEmptyAccountTransactions() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, testHelper.accessTokenPackageLegalRep)
                .body('{"balance": 100}')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}/payment', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))

        cleanup:
        testHelper.clean()
    }

    void testPayNegativeBalance() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .addAccountTransactionToPackageLegalRep(POSITIVE_BALANCE.negate())
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, testHelper.accessTokenPackageLegalRep)
                .body('{"balance": -1000}')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}/payment', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))

        cleanup:
        testHelper.clean()
    }

    void testPayPositiveBalance() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .addAccountTransactionToPackageLegalRep(POSITIVE_BALANCE, TransactionSource.MAINTENANCE, TEST_MEMO)
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, testHelper.accessTokenPackageLegalRep)
                .filter(document('pay-balance',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName(ID_FIELD).description('id of the user')
                        ),
                        requestFields(
                                fieldWithPath('balance').description('user current balance')),
                        responseFields(
                                fieldWithPath(SUB_TOTAL_FIELD).description('user current balance'),
                                fieldWithPath(GRAND_TOTAL_FIELD).description('user current balance with taxes to be paid by.'),
                                fieldWithPath(EST_TAX_FIELD).description('taxes amount for user current balance'))))
                .body("""{"balance": $POSITIVE_BALANCE}""")
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}/payment', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body(SUB_TOTAL_FIELD, equalTo(BigDecimal.ZERO.intValue()))
                .body(GRAND_TOTAL_FIELD, equalTo(BigDecimal.ZERO.intValue()))
                .body(EST_TAX_FIELD, equalTo(BigDecimal.ZERO.intValue()))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
        }
        TestUtils.assertPaidAccountTransaction(transactions, POSITIVE_BALANCE, 2, null, TestUtils.PAID_BALANCE)
        assert storedUser.paid

        cleanup:
        testHelper.clean()
    }

    void testPayPositiveBalanceAdmin() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .buildNoPackageLegalRep(true)
                .addPaymentAccountTransactionToPackageLegalRep(BigDecimal.ZERO)
                .addAccountTransactionToPackageLegalRep(POSITIVE_BALANCE, TransactionSource.MAINTENANCE, TEST_MEMO)
                .logInNoPackageLegalRep()


        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, testHelper.accessTokenNoPackageLegalRep)
                .body("""{"balance": $POSITIVE_BALANCE}""")
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}/payment', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
        }
        TestUtils.assertPaidAccountTransaction(transactions, POSITIVE_BALANCE, 3, null, TestUtils.PAID_BALANCE)
        assert storedUser.paid

        cleanup:
        testHelper.clean()
    }

    void testPayPositiveBalanceOwner() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .addPaymentAccountTransactionToPackageLegalRep(BigDecimal.ZERO)
                .addAccountTransactionToPackageLegalRep(POSITIVE_BALANCE, TransactionSource.MAINTENANCE, TEST_MEMO)

        PackageTestBuilder testHelperOwner = PackageTestBuilder.init([serverPort     : serverPort,
                                                                      attorneyService: attorneyService,
                                                                      profileService : profileService])
        testHelperOwner.buildPackageLegalRep()

        LegalRepresentative.withNewTransaction {
            testHelper.packageLegalRepresentative.refresh()
            testHelper.packageLegalRepresentative.user.paid = Boolean.FALSE
            testHelper.packageLegalRepresentative.user.save(failOnError: true)
            TestUtils.createUserRole(testHelperOwner.packageLegalRepresentative.user, Role.OWNER)
        }
        testHelperOwner.logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, testHelperOwner.accessTokenPackageLegalRep)
                .body("""{"balance": $POSITIVE_BALANCE}""")
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}/payment', testHelper.packageLegalRepresentative.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
        }
        assert 2 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, POSITIVE_BALANCE, TransactionSource.MAINTENANCE, TEST_MEMO)
        assert !storedUser.paid

        cleanup:
        testHelper.clean()
        testHelperOwner.clean()
    }

    void testGetNoAccountTransaction() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('.', is([]))

        cleanup:
        //todo Remove Created User
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testGetNoAccountTransactionAdmin() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        Organization org
        Employee employee
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            User adminUser = new User(username: 'adminuser', password: 'adminUser', accountLocked: false)
                    .save(failOnError: true)
            Profile adminProfile = new Profile(user: adminUser, lastName: 'admin last', firstName: 'admin first',
                    email: 'registeredAdmin@easyvisa.com', easyVisaId: 'adminEvId').save(failOnError: true)
            org = TestUtils.createOrganization('Account Organization', OrganizationType.LAW_FIRM)
            employee = new Employee(profile: adminProfile).save(failOnError: true)
            new OrganizationEmployee(employee: employee,
                    organization: org, isAdmin: true, position: EmployeePosition.EMPLOYEE).save(failOnError: true)
            new OrganizationEmployee(employee: attorney,
                    organization: org, isAdmin: false, position: EmployeePosition.ATTORNEY).save(failOnError: true)
            TestUtils.createUserRole(adminUser, Role.EMPLOYEE)
        }
        resp = TestUtils.logInUser(serverPort, 'adminuser', 'adminUser')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
            TestUtils.deleteEmployee(employee)
            OrganizationEmployee.findAllByOrganization(org)*.delete(failOnError: true)
            org.refresh().delete(failOnError: true)
        }
    }

    void testGetNoAccountTransactionOwner() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        Organization org
        Employee employee
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            User adminUser = new User(username: 'adminuser', password: 'adminUser', accountLocked: false)
                    .save(failOnError: true)
            Profile adminProfile = new Profile(user: adminUser, lastName: 'admin last', firstName: 'admin first',
                    email: 'registeredAdmin@easyvisa.com', easyVisaId: 'adminEvId').save(failOnError: true)
            org = TestUtils.createOrganization('Account Organization', OrganizationType.LAW_FIRM)
            employee = new Employee(profile: adminProfile).save(failOnError: true)
            new OrganizationEmployee(employee: employee,
                    organization: org, isAdmin: true, position: EmployeePosition.PARTNER).save(failOnError: true)
            TestUtils.createUserRole(adminUser, Role.EMPLOYEE)
        }
        resp = TestUtils.logInUser(serverPort, 'adminuser', 'adminUser')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
            TestUtils.deleteEmployee(employee)
            OrganizationEmployee.findAllByOrganization(org)*.delete(failOnError: true)
            org.refresh().delete(failOnError: true)
        }
    }

    void testOneAccountTransactions() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            AccountTransaction ac = new AccountTransaction(profile: profile, amount: POSITIVE_BALANCE,
                    memo: TEST_MEMO, source: TransactionSource.MAINTENANCE)
            ac.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('amount[0]', is(POSITIVE_BALANCE.floatValue()))
                .body('memo[0]', is(TEST_MEMO))

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testTwoAccountTransactions() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            AccountTransaction ac = new AccountTransaction(profile: profile, amount: POSITIVE_BALANCE,
                    memo: TEST_MEMO, source: TransactionSource.MAINTENANCE)
            ac.save(failOnError: true,)
            AccountTransaction ac1 = new AccountTransaction(profile: profile, amount: POSITIVE_BALANCE.negate(),
                    memo: TestUtils.PAYMENT, source: TransactionSource.PAYMENT)
            Tax tax = new Tax(avaTaxId: '1', total: BigDecimal.TEN, billingAddress: new Address())
            ac1.tax = tax
            ac1.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .filter(document('get-account-transactions',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName(ID_FIELD).description('id of the user')),
                        requestParameters(
                                parameterWithName('max').description('Max value to returning list. Optional. Default is 25'),
                                parameterWithName('offset').description('Start position to read/return the list. Optional. Default is 0')),
                        responseFields(
                                fieldWithPath('[].id').description('transaction id'),
                                fieldWithPath('[].amount').description('transaction amount value'),
                                fieldWithPath('[].memo').description('transaction memo'),
                                fieldWithPath('[].date').description('transaction date')
                        )))
                .when()
                .port(this.serverPort)
                .get('/api/account-transactions/user/{id}?max=5&offset=0', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('amount[0]', is((POSITIVE_BALANCE + BigDecimal.TEN).negate().floatValue()))
                .body('memo[0]', is(TestUtils.PAYMENT))
                .body('amount[1]', is(POSITIVE_BALANCE.floatValue()))
                .body('memo[1]', is(TEST_MEMO))
                .header('X-total-count', equalTo('2'))

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransaction() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.OWNER)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .filter(document('add-account-transaction',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName(ID_FIELD).description('id of the user')
                        ),
                        requestFields(
                                fieldWithPath('amount').description('amount to be added'),
                                fieldWithPath('memo').description('memo for the account transaction')),
                        responseFields(
                                fieldWithPath('balance').description('user current balance'),
                                fieldWithPath('accountTransaction.id').description('account transaction id'),
                                fieldWithPath('accountTransaction.date').description('account transaction date'),
                                fieldWithPath('accountTransaction.amount').description('account transaction amount value'),
                                fieldWithPath('accountTransaction.memo').description('account transaction memo'))))
                .body('''{
    "amount": -1000.00,
    "memo": "Test memo"
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('balance', equalTo(POSITIVE_BALANCE.negate().floatValue()))
                .body('accountTransaction.memo', equalTo(TEST_MEMO))
                .body('accountTransaction.amount', equalTo(POSITIVE_BALANCE.negate().floatValue()))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert storedUser.paid
        assert 1 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, POSITIVE_BALANCE.negate(), TransactionSource.REFUND,
                TEST_MEMO)

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransactionNotOwner() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.EMPLOYEE)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body('''{
    "amount": -1000.00,
    "memo": "Test memo"
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransactionWithDecimals() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.OWNER)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        BigDecimal amount = new BigDecimal(-100.55).setScale(2, RoundingMode.HALF_UP)
        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body('''{
    "amount": -100.55,
    "memo": "Test memo"
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('balance', equalTo(amount.floatValue()))
                .body('accountTransaction.memo', equalTo(TEST_MEMO))
                .body('accountTransaction.amount', equalTo(amount.floatValue()))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert user.paid
        assert 1 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, -100.55 as BigDecimal,
                TransactionSource.REFUND, TEST_MEMO)

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransactionSumPreviousTransactions() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            AccountTransaction ac = new AccountTransaction(profile: profile, amount: POSITIVE_BALANCE,
                    memo: TEST_MEMO, source: TransactionSource.MAINTENANCE)
            ac.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.OWNER)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body('''{
    "amount": -1000,
    "memo": "Test memo"
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('balance', equalTo(BigDecimal.ZERO.floatValue()))
                .body('accountTransaction.memo', equalTo(TEST_MEMO))
                .body('accountTransaction.amount', equalTo(POSITIVE_BALANCE.negate().intValue()))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert user.paid
        assert 2 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, POSITIVE_BALANCE.negate(), TransactionSource.REFUND,
                TEST_MEMO)

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransactionZeroAmount() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.OWNER)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body('''{
    "amount": 0,
    "memo": "Test memo"
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('Currently, we support refund amount value only (it should be with ' +
                        'an opposite sign) or amount should be with two decimal digits only'))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert storedUser.paid
        assert 0 == transactions.size()

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransactionThreeDecimalDigits() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword', paid: false)
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.OWNER)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body('''{
    "amount": -100.001,
    "memo": "Test memo"
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('Currently, we support refund amount value only (it should be ' +
                        'with an opposite sign) or amount should be with two decimal digits only'))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = user.refresh()
        }
        assert !storedUser.paid
        assert 0 == transactions.size()

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransactionPositiveAmount() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.OWNER)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body('''{
    "amount": 100,
    "memo": "Test memo"
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('Currently, we support refund amount value only (it should be ' +
                        'with an opposite sign) or amount should be with two decimal digits only'))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert storedUser.paid
        assert 0 == transactions.size()

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransactionLongMemo() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.OWNER)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body('''{
    "amount": 100,
    "memo": "This is long long and extremely long value. This is long long and extremely long value.
     This is long long and extremely long value. This is long long and extremely long value.
     This is long long and extremely long value. This is long long and extremely long value.
     This is long long and extremely long value. This is long long and extremely long value."
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_UNPROCESSABLE_ENTITY))
                .body('errors.message[0]', equalTo('[memo] exceeds the maximum size of [255]'))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert storedUser.paid
        assert 0 == transactions.size()

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransactionWrongUser() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.OWNER)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body('''{
    "amount": -100,
    "memo": "Test memo"
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', 0)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_NOT_FOUND))
                .body('errors.message[0]', equalTo('User not found with id'))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        assert storedUser.paid
        assert 0 == transactions.size()

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAddAccountTransactionToNonPaidUser() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        User userToAddTransaction
        Profile profile
        Profile profileToAddTransaction
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            userToAddTransaction = new User(username: 'addtransactionuser', password: 'addTransactionUserPassword', paid: false)
            userToAddTransaction.save(failOnError: true)
            profileToAddTransaction = new Profile(user: userToAddTransaction, lastName: 'add', easyVisaId: 'ev-id',
                    firstName: 'transaction', middleName: 'user', email: 'addTransactionUser@easyvisa.com')
            profileToAddTransaction.save(failOnError: true)
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false;
            user.save(failOnError: true,)
            new AccountTransaction(profile: profileToAddTransaction, memo: TEST_MEMO, amount: POSITIVE_BALANCE,
                    source: TransactionSource.MAINTENANCE).save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
            TestUtils.createUserRole(user, Role.OWNER)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body('''{
    "amount": -1000,
    "memo": "Test memo"
}''')
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}', userToAddTransaction.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('balance', equalTo(BigDecimal.ZERO.floatValue()))
                .body('accountTransaction.memo', equalTo(TEST_MEMO))
                .body('accountTransaction.amount', equalTo(POSITIVE_BALANCE.negate().intValue()))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profileToAddTransaction)
            storedUser = User.get(userToAddTransaction.id)
        }
        assert storedUser.paid
        assert 2 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, POSITIVE_BALANCE.negate(), TransactionSource.REFUND,
                TEST_MEMO)

        cleanup:
        LegalRepresentative.withNewTransaction {
            AccountTransaction.findAllByProfile(profileToAddTransaction)*.delete(failOnError: true,)
            profileToAddTransaction.delete(failOnError: true,)
            storedUser.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

}
