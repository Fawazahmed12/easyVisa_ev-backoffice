package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.*
import com.easyvisa.utils.NumberUtils
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.JsonConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.path.json.config.JsonPathConfig
import io.restassured.specification.RequestSpecification
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import spock.lang.Ignore
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
@Ignore
class FattmerchantRemoteCallsSpec extends Specification {

    private static final String BALANCE_FIELD = 'subTotal'
    private static final String AUTHORIZATION = 'Authorization'
    private static final String ACCESS_TOKEN = 'access_token'
    private static final String TEST_MEMO = 'Test memo'
    private static final String CARD_HOLDER = 'John Doe'
    private static final String ADDRESS1 = '208 Concord Ave'
    private static final String ADDRESS2 = null
    private static final String CITY = 'Cambridge'
    private static final String COUNTRY = 'UNITED_STATES'
    private static final String STATE = 'MA'
    private static final String ZIP = '02138'
    private static final String CARD_EXPIRATION = '122027'
    private static final String CARD_TYPE = 'mastercard'
    private static final String CUSTOMER_ID = 'ee22fdfb-bb27-444d-b179-ef3bed74430d'

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    ProfileService profileService
    @Autowired
    AttorneyService attorneyService
    @Autowired
    AdminService adminService
    @Autowired
    PackageService packageService
    @Value('${payment.url}')
    private String paymentUrl
    @Value('${payment.api.key}')
    private String paymentApiKey

    protected RequestSpecification spec

    void setup() {
        this.spec = new RequestSpecBuilder().setConfig(RestAssuredConfig.config().jsonConfig(JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .addFilter(documentationConfiguration(this.restDocumentation)).build()
    }

    void testPayPositiveBalance() {
        given:
        RestResponse resp
        LegalRepresentative attorney
        User user
        Profile profile
        BigDecimal amount = TestUtils.randomNumber()
        LegalRepresentative.withNewTransaction {
            user = new User(username: 'balancecheck', password: 'balanceCheckPassword', paid: false)
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false; user.paid = true
            user.save(failOnError: true,)
            TestUtils.getPaymentMethod(TestUtils.CARD_1111, user, paymentApiKey, paymentUrl)
            AccountTransaction ac = new AccountTransaction(profile: profile, amount: amount,
                    memo: TEST_MEMO, source: TransactionSource.MAINTENANCE)
            ac.save(failOnError: true,)
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true,)
        }
        resp = TestUtils.logInUser(serverPort, 'balancecheck', 'balanceCheckPassword')

        expect:
        TestUtils.delayCurrentThread(1000)
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header(AUTHORIZATION, "Bearer ${resp.json[ACCESS_TOKEN]}")
                .body("""{"balance": $amount}""")
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}/payment', user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body(BALANCE_FIELD, equalTo(BigDecimal.ZERO.intValue()))

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            storedUser = User.get(user.id)
        }
        TestUtils.assertPaidAccountTransaction(transactions, amount, 2, null, TestUtils.PAID_BALANCE)
        assert storedUser.paid

        cleanup:
        LegalRepresentative.withNewTransaction {
            PaymentMethod.findByUser(storedUser).delete(failOnError: true,)
            AccountTransaction.findAllByProfile(profile)*.delete(failOnError: true,)
            TestUtils.deleteRepresentative(attorney.id)
        }
    }

    void testAttorneyRegistrationPayment() {
        given:
        LegalRepresentative attorney
        RestResponse resp
        User user
        BigDecimal signUpFee = TestUtils.randomNumber()
        Profile profile
        LegalRepresentative.withNewTransaction {
            AdminSettings conf = adminService.getAdminSettingsForUpdate()
            conf.adminConfig.signupFee = signUpFee
            conf.save(failOnError: true)
            user = new User(username: 'registeredattorney', language: 'En/US', password: 'registeredAttorneyPassword')
            profile = new Profile(user: user, lastName: 'last', firstName: 'First', middleName: 'middle',
                    email: 'registeredAttorney@easyvisa.com')
            attorney = new LegalRepresentative(position: EmployeePosition.ATTORNEY, profile: profile,
                    mobilePhone: '99999123123', practiceAreas: [PracticeArea.BUSINESS])
            attorney = attorneyService.createAttorney(attorney)
            user = attorney.user
            user.accountLocked = false
            user.save(failOnError: true)
            attorney.registrationStatus = RegistrationStatus.REPRESENTATIVE_SELECTED
            attorney.save(failOnError: true)
        }
        resp = TestUtils.logInUser(serverPort, 'registeredattorney', 'registeredAttorneyPassword')
        String paymentToken = TestUtils.getFmToken(paymentUrl, paymentApiKey, TestUtils.CARD_1111)

        expect:
        TestUtils.delayCurrentThread(1000)
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', "Bearer $resp.json.access_token")
                .filter(document('attorney-payment',
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath('address1').description('card address 1'),
                                fieldWithPath('address2').description('card address 2'),
                                fieldWithPath('addressCity').description('card city'),
                                fieldWithPath('addressCountry').description('card country'),
                                fieldWithPath('addressState').description('card state'),
                                fieldWithPath('addressZip').description('card zip'),
                                fieldWithPath('cardExpiration').description('card expiration'),
                                fieldWithPath('cardHolder').description('card holder'),
                                fieldWithPath('cardLastFour').description('card last four'),
                                fieldWithPath('cardType').description('card type'),
                                fieldWithPath('customerId').description('card customer id'),
                                fieldWithPath('fmPaymentMethodId').description('payment method')),
                        responseFields(
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
                                fieldWithPath('organizations').description('Name and Ids of the organizations representative is associated with'),
                                fieldWithPath('feeSchedule').description('Fee Schedule of the representative'),
                                fieldWithPath('newFirmInviteDetails').description('New Firm invite details if user has sent any invite'),
                                fieldWithPath('newFirmJoinRequestDetails').description('New Firm Join request details if user has requested to join any organization'),
                                fieldWithPath('creditBalance').description('Credit balance for the representative'))))
                .body("""{
                "address1": "$ADDRESS1",
                "address2": $ADDRESS2,
                "addressCity": "$CITY",
                "addressCountry": "$COUNTRY",
                "addressState": "$STATE",
                "addressZip": "$ZIP",
                "cardExpiration": "$CARD_EXPIRATION",
                "cardHolder": "$CARD_HOLDER",
                "cardLastFour": "$TestUtils.CARD_1111",
                "cardType": "$CARD_TYPE",
                "customerId": "$CUSTOMER_ID",
                "fmPaymentMethodId": "$paymentToken"
                }""")
                .when()
                .port(this.serverPort)
                .post('/api/attorneys/complete-payment')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('registrationStatus', equalTo('COMPLETE'))

        List<AccountTransaction> transactions
        PaymentMethod paymentMethod
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(profile)
            paymentMethod = PaymentMethod.findByUser(user)
            storedUser = User.get(user.id)
        }
        assert CUSTOMER_ID == storedUser.fmCustomerId

        TestUtils.assertPaidAccountTransaction(transactions, signUpFee, 2)
        TestUtils.assertCustomAccountTransactions(transactions, signUpFee, TransactionSource.REGISTRATION, TestUtils.REGISTRATION_FEE_MEMO)

        assert TestUtils.CARD_1111 == paymentMethod.cardLastFour
        assert CARD_TYPE == paymentMethod.cardType
        assert CARD_EXPIRATION == paymentMethod.cardExpiration
        assert CARD_HOLDER == paymentMethod.cardHolder
        assert paymentToken == paymentMethod.fmPaymentMethodId
        assert ADDRESS1 == paymentMethod.address1
        assert ADDRESS2 == paymentMethod.address2
        assert CITY == paymentMethod.addressCity
        assert STATE == paymentMethod.addressState
        assert COUNTRY == paymentMethod.addressCountry
        assert ZIP == paymentMethod.addressZip

        cleanup:
        if (attorney) {
            LegalRepresentative.withNewTransaction {
                AdminConfig config = adminService.adminSettingsForUpdate.adminConfig
                config.signupFee = 0
                config.save(failOnError: true)
                AccountTransaction.findAllByProfile(profile)*.delete()
                PaymentMethod.findByUser(user)?.delete()
                TestUtils.deleteRepresentative(attorney.id)
            }
        }
    }

    void testChangePackageStatus() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 paymentUrl     : paymentUrl,
                                                                 paymentApiKey  : paymentApiKey,
                                                                 profileService : profileService,
                                                                 cardLastFour   : TestUtils.CARD_1111])
        testHelper.buildPetitionerAndTwoBeneficiariesLeadPackage()
                .setPerApplicantFirst()
                .buildPaymentMethodPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        TestUtils.delayCurrentThread(1000)
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body('{"newStatus":"OPEN"}')
                .when()
                .port(this.serverPort)
                .post('/api/packages/{id}/change-status', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('package.status', equalTo('OPEN'))
                .body('messages[0].type', is(ErrorMessageType.PAYMENT_CHARGED.name()))
                .body('messages[0].text', equalTo(("\$${NumberUtils.formatMoneyNumber(testHelper.perApplicant * 2)}" +
                        ' was successfully charged to your card on file.').toString()))

        cleanup:
        TestUtils.delayCurrentThread()
        testHelper.clean()
    }


}
