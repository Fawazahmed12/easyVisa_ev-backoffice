package com.easyvisa.test.api

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.TransactionSource
import com.easyvisa.utils.NumberUtils
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.hibernate.SessionFactory
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.transaction.PlatformTransactionManager
import spock.lang.Ignore

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.Matchers.nullValue
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

@Integration
class PackageChargerControllerSpec extends TestMockUtils {

    private static final int TRANSACTIONS_COUNT_OPEN_PACKAGE = 4

    @Rule
    JUnitRestDocumentation restDocumentation =
            new JUnitRestDocumentation("build/generated-snippets")

    @Value('${local.server.port}')
    Integer serverPort

    @Autowired
    private ProfileService profileService
    @Autowired
    private PackageService packageService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private SessionFactory sessionFactory
    @Autowired
    private AdminService adminService
    @Autowired
    private AnswerService answerService
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

    }

    void cleanup() {
        updateToService(packageService.accountService, paymentService, taxService)
    }

    void testEditOpenPackage() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editNewBeneficiaryWithPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('messages[0].type', is(ErrorMessageType.PAYMENT_CHARGED.name()))
                .body('messages[0].text', equalTo(("\$${NumberUtils.formatMoneyNumber(testHelper.perApplicant2)} was " +
                        'successfully charged to your card on file.').toString()))

        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert TRANSACTIONS_COUNT_OPEN_PACKAGE == transactions.size()
        List<AccountTransaction> paid = transactions.findAll { it.source == TransactionSource.PAYMENT }.sort { it.date }
        TestUtils.assertAccountTransactionValues(paid[0], testHelper.perApplicant.negate(), TestUtils.PAYMENT)
        TestUtils.assertAccountTransactionValues(paid[1], testHelper.perApplicant2.negate(), TestUtils.PAYMENT)

        List<AccountTransaction> pack = transactions.findAll { it.source == TransactionSource.PACKAGE }.sort { it.date }
        TestUtils.assertAccountTransactionValues(pack[0], testHelper.perApplicant,
                "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary applicant-first applicant-last")
        TestUtils.assertAccountTransactionValues(pack[1], testHelper.perApplicant2,
                "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary new-applicant-first" +
                        " new-applicant-middle new-applicant-last")

        cleanup:
        testHelper.clean()
    }

    void testEditOpenNoPetitionerPackage() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerOpenPackage()
                .logInPackageLegalRep()
                .refreshPetitioner()
        String evid = testHelper.aPackage.client.profile.easyVisaId

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editNewBeneficiaryWithNoPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('messages[0].type', is(ErrorMessageType.PAYMENT_CHARGED.name()))
                .body('messages[0].text', equalTo(("\$${NumberUtils.formatMoneyNumber(testHelper.perApplicant2)}" +
                        ' was successfully charged to your card on file.').toString()))

        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert TRANSACTIONS_COUNT_OPEN_PACKAGE == transactions.size()
        List<AccountTransaction> paid = transactions.findAll { it.source == TransactionSource.PAYMENT }.sort { it.date }
        TestUtils.assertAccountTransactionValues(paid[0], testHelper.perApplicant.negate(), TestUtils.PAYMENT)
        TestUtils.assertAccountTransactionValues(paid[1], testHelper.perApplicant2.negate(), TestUtils.PAYMENT)

        List<AccountTransaction> pack = transactions.findAll { it.source == TransactionSource.PACKAGE }.sort { it.date }
        TestUtils.assertAccountTransactionValues(pack[0], testHelper.perApplicant,
                "Package P$testHelper.aPackage.id - benefit category: SIX01 - beneficiary applicant-first applicant-last")
        TestUtils.assertAccountTransactionValues(pack[1], testHelper.perApplicant2,
                "Package P$testHelper.aPackage.id - benefit category: SIX01 - beneficiary new-applicant-first" +
                        " new-applicant-middle new-applicant-last")

        cleanup:
        RegistrationCode.withNewTransaction {
            RegistrationCode.findByEasyVisaId(evid).delete(failOnError: true)
        }
        testHelper.clean()
    }

    @Ignore
    void testEditOpenPetitionerTwoBeneficiariesPackage() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerTwoBeneficiariesOpenPackage()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editNewBeneficiaryWithSamePetitionerAndDerivativePayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('messages[0].type', is(ErrorMessageType.PAYMENT_CHARGED.name()))
                .body('messages[0].text', equalTo(("\$${NumberUtils.formatMoneyNumber(testHelper.perApplicant2)} was " +
                        'successfully charged to your card on file.').toString()))

        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert 5 == transactions.size()
        List<AccountTransaction> paid = transactions.findAll { it.source == TransactionSource.PAYMENT }.sort { it.date }
        TestUtils.assertAccountTransactionValues(paid[0], testHelper.perApplicant.negate() * 2, TestUtils.PAYMENT)
        TestUtils.assertAccountTransactionValues(paid[1], testHelper.perApplicant2.negate(), TestUtils.PAYMENT)

        String memo = "Package P$testHelper.aPackage.id - benefit category: IR2 - derivative beneficiary " +
                'derivative-applicant-first derivative-applicant-last'
        String memo2 = "Package P$testHelper.aPackage.id - benefit category: F1 - beneficiary applicant-first " +
                'applicant-last'
        String memo3 = "Package P$testHelper.aPackage.id - benefit category: F1 - beneficiary new-applicant-first" +
                ' new-applicant-middle new-applicant-last'
        List<BigDecimal> amounts = [testHelper.perApplicant.setScale(2), testHelper.perApplicant2.setScale(2)]
        TestUtils.assertCustomAccountTransactions(transactions, amounts, TransactionSource.PACKAGE,
                [memo, memo2, memo3])

        cleanup:
        testHelper.clean()
    }

    void testEditOpenPackageWithInactiveAttorney() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildNoPackageLegalRep()
                .setInactiveFlagToPackageLegalRep()
                .logInNoPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenNoPackageLegalRep)
                .body(testHelper.editNewBeneficiaryWithPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_FORBIDDEN))
                .body('errors.message[0]', equalTo('Inactive user can\'t be charged'))

        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert 2 == transactions.size()
        AccountTransaction paid = transactions.find { it.source == TransactionSource.PAYMENT }
        TestUtils.assertAccountTransactionValues(paid, testHelper.perApplicant.negate(), TestUtils.PAYMENT)

        AccountTransaction pack = transactions.find { it.source == TransactionSource.PACKAGE }
        TestUtils.assertAccountTransactionValues(pack, testHelper.perApplicant,
                "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary applicant-first applicant-last")

        cleanup:
        testHelper.clean()
    }

    void testEditOpenPackageReducedCharge() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .addBonus2AccountTransactionToPackageLegalRep()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editNewBeneficiaryWithPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('messages[0].type', is(ErrorMessageType.PAYMENT_CHARGED.name()))
                .body('messages[0].text',
                        equalTo(("\$${NumberUtils.formatMoneyNumber(testHelper.toChargePerApplicant2)} was successfully " +
                                'charged to the card on file. It was reduced by the credits on your account.').toString()))

        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert 5 == transactions.size()
        List<AccountTransaction> paid = transactions.findAll { it.source == TransactionSource.PAYMENT }.sort { it.date }
        TestUtils.assertAccountTransactionValues(paid[0], testHelper.perApplicant.negate(), TestUtils.PAYMENT)
        TestUtils.assertAccountTransactionValues(paid[1], testHelper.toChargePerApplicant2.negate(),
                TestUtils.PAYMENT)

        List<AccountTransaction> pack = transactions.findAll { it.source == TransactionSource.PACKAGE }.sort { it.date }
        TestUtils.assertAccountTransactionValues(pack[0], testHelper.perApplicant,
                "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary applicant-first applicant-last")
        TestUtils.assertAccountTransactionValues(pack[1], testHelper.perApplicant2,
                "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary new-applicant-first " +
                        'new-applicant-middle new-applicant-last')

        cleanup:
        testHelper.clean()
    }

    void testEditOpenPackageNoCharge() {
        given:

        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .addFullBonus2AccountTransactionToPackageLegalRep()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editNewBeneficiaryWithPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('messages[0].type', is(ErrorMessageType.PAYMENT_CHARGED.name()))
                .body('messages[0].text', equalTo('Your fees were entirely covered by the credits on your ' +
                        'account. Nothing was charged to your card.'))

        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert 4 == transactions.size()
        List<AccountTransaction> paid = transactions.findAll { it.source == TransactionSource.PAYMENT }.sort { it.date }
        TestUtils.assertAccountTransactionValues(paid[0], testHelper.perApplicant.negate(), TestUtils.PAYMENT)

        List<AccountTransaction> pack = transactions.findAll { it.source == TransactionSource.PACKAGE }.sort { it.date }
        TestUtils.assertAccountTransactionValues(pack[0], testHelper.perApplicant,
                "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary applicant-first applicant-last")
        TestUtils.assertAccountTransactionValues(pack[1], testHelper.perApplicant2,
                "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary new-applicant-first " +
                        'new-applicant-middle new-applicant-last')

        cleanup:
        testHelper.clean()
    }

    void testChangePackageStatus() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndTwoBeneficiariesLeadPackage()
                .setPerApplicantFirst()
                .buildPaymentMethodPackageLegalRep()
                .logInPackageLegalRep()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .filter(document('change-package-status',
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName('id').description('id of the package')),
                        requestFields(
                                fieldWithPath('newStatus').description('Status to set for the package')),
                        responseFields(
                                subsectionWithPath('package').description('package data'),
                                subsectionWithPath('messages').description('list of messages'))))
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

        TestUtils.delayCurrentThread()
        List<AccountTransaction> transactions
        Profile benProfile
        Profile derBenProfile
        AccountTransaction.withNewTransaction {
            benProfile = testHelper.aPackage.refresh().directBenefit.applicant.profile
            benProfile.firstName
            benProfile.lastName
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.refresh().profile)
            derBenProfile = testHelper.aPackage.refresh().benefits.find { !it.direct }.applicant.refresh().profile.refresh()
        }

        TestUtils.assertPaidAccountTransaction(transactions, testHelper.perApplicant * 2, 3)
        String memo =
                "Package P${testHelper.aPackage.id} - benefit category: ${testHelper.aPackage.directBenefit.category}" +
                        " - beneficiary ${benProfile.firstName} ${benProfile.lastName}"
        String derivativeMemo =
                "Package P${testHelper.aPackage.id} - benefit category: ${testHelper.aPackage.orderedBenefits[1].category}" +
                        " - derivative beneficiary ${derBenProfile.firstName} ${derBenProfile.lastName}"

        TestUtils.assertCustomAccountTransactions(transactions, [testHelper.perApplicant, testHelper.perApplicant2],
                TransactionSource.PACKAGE, [memo, derivativeMemo])

        cleanup:
        testHelper.clean()
    }

    void testChangeNoPetitionerPackageStatus() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildNoPetitionerLeadPackage()
                .setPerApplicantFirst()
                .buildPaymentMethodPackageLegalRep()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
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
                .body('messages[0].text', equalTo(("\$${NumberUtils.formatMoneyNumber(testHelper.perApplicant)}" +
                        ' was successfully charged to your card on file.').toString()))

        TestUtils.delayCurrentThread()
        List<AccountTransaction> transactions
        Profile benProfile = testHelper.aPackage.directBenefit.applicant.profile
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }

        TestUtils.assertPaidAccountTransaction(transactions, testHelper.perApplicant, 2)
        String memo =
                "Package P${testHelper.aPackage.id} - benefit category: ${testHelper.aPackage.directBenefit.category} - " +
                        "beneficiary ${benProfile.firstName} ${benProfile.lastName}"
        TestUtils.assertCustomAccountTransactions(transactions, testHelper.perApplicant, TransactionSource.PACKAGE,
                memo)

        cleanup:
        testHelper.clean()
    }

    void testChangePetitionerAndTwoBeneficiariesPackageStatus() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndTwoBeneficiariesLeadPackage()
                .setPerApplicantFirst()
                .buildPaymentMethodPackageLegalRep()
                .logInPackageLegalRep()

        expect:
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

        TestUtils.delayCurrentThread()
        List<AccountTransaction> transactions
        Profile benProfile
        Profile derBenProfile
        ImmigrationBenefitCategory benCategory
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.refresh().profile)
            benProfile = testHelper.aPackage.refresh().directBenefit.applicant.profile
            derBenProfile = testHelper.aPackage.benefits.find { !it.direct }.applicant.profile
            derBenProfile.firstName
            derBenProfile.lastName
            benProfile.firstName
            benProfile.lastName
            benCategory = testHelper.aPackage.orderedBenefits[1].category
            testHelper.aPackage.directBenefit.category
        }

        TestUtils.assertPaidAccountTransaction(transactions, testHelper.perApplicant * 2, 3)
        String memo =
                "Package P${testHelper.aPackage.id} - benefit category: ${testHelper.aPackage.directBenefit.category}" +
                        " - beneficiary ${benProfile.firstName} ${benProfile.lastName}"
        String derivativeMemo =
                "Package P${testHelper.aPackage.id} - benefit category: ${benCategory}" +
                        " - derivative beneficiary ${derBenProfile.firstName} ${derBenProfile.lastName}"
        TestUtils.assertCustomAccountTransactions(transactions, testHelper.perApplicant, TransactionSource.PACKAGE,
                [memo, derivativeMemo])

        cleanup:
        testHelper.clean()
    }

    void testChangePackageStatusReducedCharge() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .setPerApplicantFirst()
                .buildPaymentMethodPackageLegalRep()
                .addBonusAccountTransactionToPackageLegalRep()
                .logInPackageLegalRep()

        expect:
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
                .body('messages[0].text',
                        equalTo(("\$${NumberUtils.formatMoneyNumber(testHelper.toChargePerApplicant)} was successfully " +
                                'charged to the card on file. It was reduced by the credits on your account.').toString()))

        TestUtils.delayCurrentThread()
        List<AccountTransaction> transactions
        Profile benProfile
        AccountTransaction.withNewTransaction {
            benProfile = testHelper.aPackage.refresh().directBenefit.applicant.profile
            benProfile.firstName
            benProfile.lastName
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert 3 == transactions.size()
        List<AccountTransaction> paid = transactions.findAll { it.source == TransactionSource.PAYMENT }.sort { it.date }
        TestUtils.assertAccountTransactionValues(paid[0], testHelper.toChargePerApplicant.negate(),
                TestUtils.PAYMENT)
        String memo =
                "Package P${testHelper.aPackage.id} - benefit category: ${testHelper.aPackage.directBenefit.category}" +
                        " - beneficiary ${benProfile.firstName} ${benProfile.lastName}"
        TestUtils.assertCustomAccountTransactions(transactions, testHelper.perApplicant, TransactionSource.PACKAGE,
                memo)

        cleanup:
        testHelper.clean()
    }

    void testChangePackageStatusNoCharge() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .setPerApplicantFirst()
                .buildPaymentMethodPackageLegalRep()
                .addFullBonusAccountTransactionToPackageLegalRep()
                .logInPackageLegalRep()

        expect:
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
                .body('messages[0].text',
                        equalTo('Your fees were entirely covered by the credits on your account. Nothing was charged ' +
                                'to your card.'))

        TestUtils.delayCurrentThread()
        List<AccountTransaction> transactions
        Profile benProfile
        AccountTransaction.withNewTransaction {
            benProfile = testHelper.aPackage.refresh().directBenefit.applicant.profile
            benProfile.firstName
            benProfile.lastName
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert 2 == transactions.size()
        String memo =
                "Package P${testHelper.aPackage.id} - benefit category: ${testHelper.aPackage.directBenefit.category}" +
                        " - beneficiary ${benProfile.firstName} ${benProfile.lastName}"
        TestUtils.assertCustomAccountTransactions(transactions, testHelper.perApplicant, TransactionSource.PACKAGE,
                memo)

        cleanup:
        testHelper.clean()
    }

    void testChangePackageStatusFailedToPay() {
        given:
        failedToPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildPaymentMethodPackageLegalRep()
                .setPerApplicantFirst()
                .logInPackageLegalRep()

        expect:
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
                .statusCode(is(HttpStatus.SC_BAD_REQUEST))
                .body('errors[0].type', is(ErrorMessageType.PAYMENT_FAILED.name()))

        TestUtils.assertNoAccountTransactions(testHelper.packageLegalRepresentative.user)

        cleanup:
        testHelper.clean()
    }

    void testChangeBenefitCategoryOpenPackage() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F1_B)
                .sumPerApplicantFee()
                .logInPackageLegalRep()
                .refreshPetitioner()

        BigDecimal charged = testHelper.perApplicant2 - testHelper.perApplicant


        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editNewBenefitCategoryBeneficiaryWithPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('messages[0].type', is(ErrorMessageType.PAYMENT_CHARGED.name()))
                .body('messages[0].text', equalTo(("\$${NumberUtils.formatMoneyNumber(charged)} was " +
                        'successfully charged to your card on file.').toString()))

        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert TRANSACTIONS_COUNT_OPEN_PACKAGE == transactions.size()
        List<AccountTransaction> paid = transactions.findAll { it.source == TransactionSource.PAYMENT }.sort { it.date }
        TestUtils.assertAccountTransactionValues(paid[0], testHelper.perApplicant.negate(), TestUtils.PAYMENT)
        TestUtils.assertAccountTransactionValues(paid[1], charged.negate(), TestUtils.PAYMENT)

        List<AccountTransaction> pack = transactions.findAll { it.source == TransactionSource.PACKAGE }.sort { it.date }
        TestUtils.assertAccountTransactionValues(pack[0], testHelper.perApplicant,
                "Package P$testHelper.aPackage.id - benefit category: F1_B - beneficiary applicant-first applicant-last")
        TestUtils.assertAccountTransactionValues(pack[1], charged,
                "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary applicant-first" +
                        " applicant-middle applicant-last")

        cleanup:
        testHelper.clean()
    }

    void testChangeBenefitCategoryOpenPackageNoCharge() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .reduceNextPerApplicantCharge()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.editNewBenefitCategoryBeneficiaryWithPetitionerPayload)
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
        }
        assert 2 == transactions.size()
        List<AccountTransaction> paid = transactions.findAll { it.source == TransactionSource.PAYMENT }.sort { it.date }
        TestUtils.assertAccountTransactionValues(paid[0], testHelper.perApplicant.negate(), TestUtils.PAYMENT)

        List<AccountTransaction> pack = transactions.findAll { it.source == TransactionSource.PACKAGE }.sort { it.date }
        TestUtils.assertAccountTransactionValues(pack[0], testHelper.perApplicant,
                "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary applicant-first applicant-last")

        cleanup:
        testHelper.clean()
    }

    void testChangeBeneficiaryOpenPackageCharged() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder beneficiaryHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                        attorneyService: attorneyService,
                                                                        packageService : packageService,
                                                                        profileService : profileService])
        beneficiaryHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .logInPackageDirectBeneficiary()
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F1_B)
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.getEditRegisteredBeneficiaryWithPetitionerPayload(
                        beneficiaryHelper.aPackage.principalBeneficiary))
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('messages', nullValue())

        Alert alert
        Alert.withNewTransaction {
            alert = Alert.findByRecipient(beneficiaryHelper.aPackage.principalBeneficiary.user)
        }
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', beneficiaryHelper.accessTokenPackageDirect)
                .body(testHelper.getEditRegisteredBeneficiaryWithPetitionerPayload(beneficiaryHelper.aPackage.principalBeneficiary))
                .when()
                .port(this.serverPort)
                .body('{"accept": "true"}')
                .put('/api/alerts/{id}/reply', alert.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        List<AccountTransaction> transactions
        Warning warning
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            alert = Alert.findByRecipientAndMessageTypeNotEqual(testHelper.aPackage.refresh().attorney.user,
                    EasyVisaSystemMessageType.PACKAGE_OPTIN_REQUEST_ACCEPTED)
            warning = Warning.findByAPackage(testHelper.aPackage)
            beneficiaryHelper.aPackage.principalBeneficiary
        }
        TestUtils.assertPaidAccountTransaction(transactions, testHelper.perApplicant2, TRANSACTIONS_COUNT_OPEN_PACKAGE)
        String memo1 = "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary applicant-first " +
                'applicant-last'
        String memo2 = "Package P$testHelper.aPackage.id - benefit category: F1_B - beneficiary applicant-first " +
                'applicant-last'
        TestUtils.assertCustomAccountTransactions(transactions, [testHelper.perApplicant, testHelper.perApplicant2],
                TransactionSource.PACKAGE, [memo1, memo2])
        testHelper.refreshPetitioner()
        assert alert == null
        assert warning == null
        assert testHelper.aPackage.directBenefit.applicant.id == beneficiaryHelper.aPackage.principalBeneficiary.id
        assert testHelper.aPackage.directBenefit.paid

        cleanup:
        beneficiaryHelper.clean(true, false)
        testHelper.clean()
    }

    void testChangeBeneficiaryOpenPackageFailedCharge() {
        given:
        successAndFailedToPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder beneficiaryHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                        attorneyService: attorneyService,
                                                                        packageService : packageService,
                                                                        profileService : profileService])
        beneficiaryHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .logInPackageDirectBeneficiary()
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, ImmigrationBenefitCategory.F1_B)
                .buildFailedPaymentMethodPackageLegalRep()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        //change package principle to a registered applicant
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.getEditRegisteredBeneficiaryWithPetitionerPayload(
                        beneficiaryHelper.aPackage.principalBeneficiary))
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('messages', nullValue())

        Alert alert
        Alert.withNewTransaction {
            alert = Alert.findByRecipient(beneficiaryHelper.aPackage.principalBeneficiary.user)
            testHelper.aPackage.refresh().attorney.id
            testHelper.aPackage.petitioner.profile.email
            testHelper.aPackage.petitioner.applicant.id
            testHelper.organization.refresh().id
        }
        //accept applicant opt-in and failed to pay
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', beneficiaryHelper.accessTokenPackageDirect)
                .body(testHelper.getEditRegisteredBeneficiaryWithPetitionerPayload(
                        beneficiaryHelper.aPackage.principalBeneficiary))
                .when()
                .port(this.serverPort)
                .body('{"accept": "true"}')
                .put('/api/alerts/{id}/reply', alert.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        //get list of attorney alerts to check correct alert content rendering
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/alerts/',)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        //get list of the package warnings to check correct warning content rendering
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .when()
                .port(this.serverPort)
                .get('/api/warnings/?representativeId={id}&organizationId={orgId}', testHelper.aPackage.attorney.id, testHelper.organization.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        List<AccountTransaction> transactions
        Warning warning
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            alert = Alert.findByRecipientAndMessageTypeNotEqual(testHelper.aPackage.refresh().attorney.user,
                    EasyVisaSystemMessageType.PACKAGE_OPTIN_REQUEST_ACCEPTED)
            warning = Warning.findByAPackage(testHelper.aPackage)
            testHelper.aPackage.directBenefit.applicant.refresh()
        }
        TestUtils.assertPaidAccountTransaction(transactions, testHelper.perApplicant)
        String memo1 = "Package P$testHelper.aPackage.id - benefit category: F1_A - beneficiary applicant-first " +
                'applicant-last'
        String memo2 = "Package P$testHelper.aPackage.id - benefit category: F1_B - beneficiary applicant-first " +
                'applicant-last'
        TestUtils.assertCustomAccountTransactions(transactions, [testHelper.perApplicant, testHelper.perApplicant2],
                TransactionSource.PACKAGE, [memo1, memo2])
        assert EasyVisaSystemMessageType.PACKAGE_ATTORNEY_CHARGE_FAILED_ALERT == alert.messageType
        assert EasyVisaSystemMessageType.PACKAGE_ATTORNEY_CHARGE_FAILED_ALERT.subject == alert.subject
        assert EvSystemMessage.EASYVISA_SOURCE == alert.source
        assert EasyVisaSystemMessageType.PACKAGE_ATTORNEY_CHARGE_FAILED == warning.messageType
        assert EasyVisaSystemMessageType.PACKAGE_ATTORNEY_CHARGE_FAILED.subject == warning.subject
        assert EvSystemMessage.EASYVISA_SOURCE == warning.source
        assert testHelper.aPackage.directBenefit.applicant.id == warning.applicant.id
        assert !testHelper.aPackage.directBenefit.paid

        cleanup:
        beneficiaryHelper.clean(true, false)
        testHelper.clean()
    }

    void testChangeBeneficiaryOpenPackageSecondAttemptToPay() {
        given:
        successAndFailedToPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder beneficiaryHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                        attorneyService: attorneyService,
                                                                        packageService : packageService,
                                                                        profileService : profileService])
        beneficiaryHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
                .logInPackageDirectBeneficiary()
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage()
                .buildFailedPaymentMethodPackageLegalRep()
                .logInPackageLegalRep()
                .refreshPetitioner()

        expect:
        //change package principle to a registered applicant
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body(testHelper.getEditRegisteredBeneficiaryWithPetitionerPayload(
                        beneficiaryHelper.aPackage.principalBeneficiary))
                .when()
                .port(this.serverPort)
                .put('/api/packages/{id}', testHelper.aPackage.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('messages', nullValue())

        Alert alert
        Alert.withNewTransaction {
            alert = Alert.findByRecipient(beneficiaryHelper.aPackage.principalBeneficiary.user)
            testHelper.aPackage.refresh().attorney.user.id
            testHelper.aPackage.petitioner.profile.email
            testHelper.aPackage.petitioner.applicant.id
        }
        //accept applicant opt-in and failed to pay
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', beneficiaryHelper.accessTokenPackageDirect)
                .body(testHelper.getEditRegisteredBeneficiaryWithPetitionerPayload(
                        beneficiaryHelper.aPackage.principalBeneficiary))
                .when()
                .port(this.serverPort)
                .body('{"accept": "true"}')
                .put('/api/alerts/{id}/reply', alert.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        //checks applicant doesn't have non paid package in the workload
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', beneficiaryHelper.accessTokenPackageDirect)
                .when()
                .port(this.serverPort)
                .get('/api/packages/find')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('.', is([]))
                .header('X-total-count', equalTo(0 as String))

        //update payment method to a success one and pay the balance.
        testHelper.updatePaymentMethodPackageLegalRepToSuccess()
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', testHelper.accessTokenPackageLegalRep)
                .body("""{"balance": ${testHelper.perApplicant2}}""")
                .when()
                .port(this.serverPort)
                .post('/api/account-transactions/user/{id}/payment', testHelper.aPackage.attorney.user.id)
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))

        //checks applicant have paid package in the workload
        given(this.spec)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .header('Authorization', beneficiaryHelper.accessTokenPackageDirect)
                .when()
                .port(this.serverPort)
                .get('/api/packages/find')
                .then()
                .assertThat()
                .statusCode(is(HttpStatus.SC_OK))
                .body('[0].id', equalTo(testHelper.aPackage.id as int))
                .body('[1]', nullValue())
                .header('X-total-count', equalTo(1 as String))

        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            testHelper.aPackage.refresh().directBenefit.refresh().applicant.refresh()
        }
        TestUtils.assertPaidAccountTransaction(transactions, testHelper.perApplicant2, TRANSACTIONS_COUNT_OPEN_PACKAGE, null, TestUtils.PAID_BALANCE)
        assert testHelper.aPackage.directBenefit.paid

        cleanup:
        beneficiaryHelper.clean(true, false)
        testHelper.clean()
    }

}
