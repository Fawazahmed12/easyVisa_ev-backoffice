package com.easyvisa.test.jobs

import com.easyvisa.AccountService
import com.easyvisa.AccountTransaction
import com.easyvisa.AdminConfig
import com.easyvisa.AdminService
import com.easyvisa.AdminSettings
import com.easyvisa.Alert
import com.easyvisa.AlertService
import com.easyvisa.AttorneyService
import com.easyvisa.EvMailService
import com.easyvisa.EvSystemMessage
import com.easyvisa.MonthlyPaymentJob
import com.easyvisa.PackageService
import com.easyvisa.PaymentService
import com.easyvisa.Profile
import com.easyvisa.ProfileService
import com.easyvisa.TaxService
import com.easyvisa.User
import com.easyvisa.UserService
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.TransactionSource
import com.easyvisa.quartz.EvQuartzInstanceIdGenerator
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.gsp.PageRenderer
import grails.testing.mixin.integration.Integration
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired

import java.math.RoundingMode
import java.text.MessageFormat
import java.time.LocalDate
import java.time.ZoneId

@Integration
class MonthlyPaymentJobSpec extends TestMockUtils {

    private static final Integer ONE_DAY = 1
    private static final Integer FIVE_DAYS = 5
    private static final Integer SEVENTY_DAYS = 70
    private static final BigDecimal CLOUD_FEE = new BigDecimal(1)

    @Autowired
    private AccountService accountService
    @Autowired
    private EvMailService evMailService
    @Autowired
    private AdminService adminService
    @Autowired
    private AlertService alertService
    @Autowired
    private UserService userService
    @Autowired
    private PageRenderer groovyPageRenderer
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private PackageService packageService
    @Autowired
    private PaymentService paymentService
    @Autowired
    private ProfileService profileService

    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    private TaxService taxServiceMock = Mock(TaxService)

    private MonthlyPaymentJob paymentJob

    void setup() {
        paymentJob = new MonthlyPaymentJob()
        paymentJob.accountService = accountService
        paymentJob.evMailService = evMailService
        paymentJob.adminService = adminService
        paymentJob.alertService = alertService
        paymentJob.userService = userService
        paymentJob.attorneyService = attorneyService
        paymentJob.groovyPageRenderer = groovyPageRenderer
        updateToMock(accountService, paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(accountService, paymentService, taxService)
    }

    void testCharge() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock, BigDecimal.TEN)
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .buildNoPackageLegalRep()
                .buildPaymentMethodPackageLegalRep()
                .buildUsersForPackageApplicants()

        PackageTestBuilder testExtraHelper = PackageTestBuilder.init(testHelper)
        testExtraHelper.buildPetitionerAndBeneficiaryLeadPackage()

        BigDecimal amount = updateMaintenanceFee()

        expect:
        runJob()

        List<AccountTransaction> attorneyTransactions
        List<AccountTransaction> petitionerTransactions
        List<AccountTransaction> beneficiaryTransactions
        User storedUser
        Profile profile
        AccountTransaction.withNewTransaction {
            attorneyTransactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            attorneyTransactions.each {
                it?.tax?.total
                it?.tax?.billingAddress?.id
            }
            petitionerTransactions = AccountTransaction.findAllByProfile(testHelper.aPackage.petitioner.profile)
            beneficiaryTransactions = AccountTransaction.findAllByProfile(testHelper.aPackage.principalBeneficiary.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.refresh().user.id)
            profile = storedUser.profile
        }
        TestUtils.assertPaidAccountTransaction(attorneyTransactions, amount + CLOUD_FEE * 2, 3, BigDecimal.TEN)
        TestUtils.assertCustomAccountTransactions(attorneyTransactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(attorneyTransactions, CLOUD_FEE * 2, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 2))
        Date paidDate = attorneyTransactions.find { it.source == TransactionSource.MAINTENANCE }.date
        assert storedUser.paid
        assert profile.lastMonthlyCharge == paidDate
        assert petitionerTransactions.isEmpty()
        assert beneficiaryTransactions.isEmpty()

        cleanup:
        testExtraHelper.deletePackageOnly()
        testHelper.clean()
    }

    void testChargeNoPackages() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock, BigDecimal.TEN)
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .buildPaymentMethodPackageLegalRep()

        BigDecimal amount = updateMaintenanceFee()

        expect:
        runJob()

        List<AccountTransaction> attorneyTransactions
        User storedUser
        Profile profile
        AccountTransaction.withNewTransaction {
            attorneyTransactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            attorneyTransactions.each {
                it?.tax?.total
                it?.tax?.billingAddress?.id
            }
            storedUser = User.get(testHelper.packageLegalRepresentative.refresh().user.id)
            profile = storedUser.profile
        }
        TestUtils.assertPaidAccountTransaction(attorneyTransactions, amount, TestUtils.ACCOUNT_TRANSACTIONS_COUNT, BigDecimal.TEN)
        TestUtils.assertCustomAccountTransactions(attorneyTransactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(attorneyTransactions, BigDecimal.ZERO, TransactionSource.CLOUD_STORAGE,
                TestUtils.getCloudStorageMemo(BigDecimal.ZERO))
        Date paidDate = attorneyTransactions.find { it.source == TransactionSource.MAINTENANCE }.date
        assert storedUser.paid
        assert profile.lastMonthlyCharge == paidDate

        cleanup:
        testHelper.clean()
    }

    void testNoChargeAlreadyChargedInThisMonth() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock, BigDecimal.TEN)
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 profileService: profileService])
        testHelper.buildPackageLegalRep()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .setLastPaidTodayForPackageLegalRep()
        Date lastCharged = testHelper.packageLegalRepresentative.profile.lastMonthlyCharge

        expect:
        runJob()

        List<AccountTransaction> attorneyTransactions
        User storedUser
        Profile profile
        AccountTransaction.withNewTransaction {
            attorneyTransactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.refresh().user.id)
            profile = storedUser.profile
        }
        assert storedUser.paid
        assert attorneyTransactions.isEmpty()
        assert profile.lastMonthlyCharge == lastCharged

        cleanup:
        testHelper.clean()
    }

    void testNoChargeEvUsers() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .buildPaymentMethodPackageLegalRep()
                .buildPackageLegalRepEvRoles()

        expect:
        runJob()

        List<AccountTransaction> attorneyTransactions
        User storedUser
        AccountTransaction.withNewTransaction {
            attorneyTransactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.refresh().user.id)
        }
        assert attorneyTransactions.isEmpty()
        assert storedUser.paid

        cleanup:
        testHelper.clean()
    }

    void testChargeUnpaidUser() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .setPackageLegalRepUserUnpaid()
                .buildPaymentMethodPackageLegalRep()
                .buildUsersForPackageApplicants()

        BigDecimal amount = updateMaintenanceFee()

        expect:
        runJob()

        List<AccountTransaction> attorneyTransactions
        List<AccountTransaction> petitionerTransactions
        List<AccountTransaction> beneficiaryTransactions
        User storedUser
        AccountTransaction.withNewTransaction {
            attorneyTransactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            petitionerTransactions = AccountTransaction.findAllByProfile(testHelper.aPackage.petitioner.profile)
            beneficiaryTransactions = AccountTransaction.findAllByProfile(testHelper.aPackage.principalBeneficiary.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.refresh().user.id)
        }
        TestUtils.assertPaidAccountTransaction(attorneyTransactions, amount + CLOUD_FEE)
        TestUtils.assertCustomAccountTransactions(attorneyTransactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(attorneyTransactions, CLOUD_FEE, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 1))
        assert storedUser.paid
        assert petitionerTransactions.isEmpty()
        assert beneficiaryTransactions.isEmpty()

        cleanup:
        testHelper.clean()
    }

    void testNoNeedToCharge() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        BigDecimal amount = updateMaintenanceFee()
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .buildPaymentMethodPackageLegalRep()
                .addAccountTransactionToPackageLegalRep((amount + CLOUD_FEE).negate())
                .buildUsersForPackageApplicants()

        expect:
        runJob()

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
        }
        assert 3 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, (amount + CLOUD_FEE).negate(), TransactionSource.REFERRAL,
                TestUtils.TEST_REFERRAL_MEMO)
        TestUtils.assertCustomAccountTransactions(transactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(transactions, CLOUD_FEE, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 1))
        assert storedUser.paid

        cleanup:
        testHelper.clean()
    }

    void testPartialCharge() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        BigDecimal amount = updateMaintenanceFee()
        BigDecimal bonus = amount.divide(new BigDecimal(2)).setScale(0, RoundingMode.HALF_UP)
        BigDecimal toCharge = amount - bonus
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .setLastPaidInPastForPackageLegalRep(1)
                .buildPaymentMethodPackageLegalRep()
                .addAccountTransactionToPackageLegalRep(bonus.negate())
                .buildUsersForPackageApplicants()
        Profile.withNewTransaction {
            Profile profile = Profile.get(testHelper.packageLegalRepresentative.profile.id)
            Date date = new Date()
            use(TimeCategory) {
                date = date - 1.month
            }
            profile.lastMonthlyPayment = date
            profile.save(failOnError: true)
        }

        expect:
        runJob()

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
        }
        TestUtils.assertCustomAccountTransactions(transactions, bonus.negate(), TransactionSource.REFERRAL,
                TestUtils.TEST_REFERRAL_MEMO)
        TestUtils.assertPaidAccountTransaction(transactions, toCharge + CLOUD_FEE, 4)
        TestUtils.assertCustomAccountTransactions(transactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(transactions, CLOUD_FEE, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 1))
        assert storedUser.paid

        cleanup:
        testHelper.clean()
    }

    void testChargeOneDay() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        BigDecimal amount = updateMaintenanceFee()
        LocalDate date = lastDate
        amount = prorateMonthlyFee(date, amount, ONE_DAY)
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .setPackageLegalRepUserCreatedDate(convertToDate(date))
                .buildPaymentMethodPackageLegalRep()
                .buildUsersForPackageApplicants()

        expect:
        runJob()

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
        }
        TestUtils.assertPaidAccountTransaction(transactions, amount + CLOUD_FEE)
        TestUtils.assertCustomAccountTransactions(transactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(transactions, CLOUD_FEE, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 1))
        assert storedUser.paid

        cleanup:
        testHelper.clean()
    }

    void testChargeFiveDays() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        BigDecimal amount = updateMaintenanceFee()
        LocalDate date = lastDate
        amount = prorateMonthlyFee(date, amount, FIVE_DAYS)
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .setPackageLegalRepUserCreatedDate(convertToDate(date, FIVE_DAYS))
                .buildPaymentMethodPackageLegalRep()
                .buildUsersForPackageApplicants()

        expect:
        runJob()

        List<AccountTransaction> transactions
        User storedUser
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
        }
        TestUtils.assertPaidAccountTransaction(transactions, amount + CLOUD_FEE)
        TestUtils.assertCustomAccountTransactions(transactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(transactions, CLOUD_FEE, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 1))
        assert storedUser.paid

        cleanup:
        testHelper.clean()
    }

    void testFailedCharge() {
        given:
        failedToPayMock(paymentServiceMock, taxServiceMock)
        BigDecimal amount = updateMaintenanceFee()
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .buildPaymentMethodPackageLegalRep()
                .buildUsersForPackageApplicants()

        expect:
        runJob()

        TestUtils.delayCurrentThread()
        List<AccountTransaction> transactions
        User storedUser
        List<Alert> alerts
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.refresh().profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
            alerts = Alert.findAllByRecipient(storedUser)
        }
        assert 2 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(transactions, CLOUD_FEE, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 1))
        assert storedUser.paid
        assert 1 == alerts.size()
        Alert alert = alerts[0]
        EasyVisaSystemMessageType type = EasyVisaSystemMessageType.MONTHLY_PAYMENT_FAILED_GRACE
        assert type == alert.messageType
        assert type.subject == alert.subject
        assert EvSystemMessage.EASYVISA_SOURCE == alert.source

        cleanup:
        testHelper.clean()
    }

    void testFailedChargeTwoMonth() {
        given:
        failedToPayMock(paymentServiceMock, taxServiceMock)
        BigDecimal amount = updateMaintenanceFee()
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .buildPaymentMethodPackageLegalRep()
                .buildUsersForPackageApplicants()
        Profile.withNewTransaction {
            Profile profile = Profile.get(testHelper.packageLegalRepresentative.profile.id)
            Date date = new Date()
            use(TimeCategory) {
                date = date - 2.month
            }
            profile.lastMonthlyPayment = date
            profile.save(failOnError: true)
        }

        expect:
        runJob()

        List<AccountTransaction> transactions
        User storedUser
        List<Alert> alerts
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.refresh().profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
            alerts = Alert.findAllByRecipient(storedUser)
        }
        assert 2 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(transactions, CLOUD_FEE, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 1))
        assert !storedUser.paid
        assert 1 == alerts.size()
        Alert alert = alerts[0]
        EasyVisaSystemMessageType type = EasyVisaSystemMessageType.MONTHLY_PAYMENT_FAILED
        assert type == alert.messageType
        assert type.subject == alert.subject
        assert EvSystemMessage.EASYVISA_SOURCE == alert.source

        cleanup:
        testHelper.clean()
    }

    void testFailedChargeExpiredCard() {
        given:
        failedToPayMock(paymentServiceMock, taxServiceMock)
        BigDecimal amount = updateMaintenanceFee()
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .buildFailedPaymentMethodPackageLegalRep()
                .buildUsersForPackageApplicants()

        expect:
        runJob()

        List<AccountTransaction> transactions
        User storedUser
        List<Alert> alerts
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.refresh().profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.user.id)
            alerts = Alert.findAllByRecipient(storedUser)
        }
        assert 2 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(transactions, CLOUD_FEE, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 1))
        assert storedUser.paid
        assert 1 == alerts.size()
        Alert alert = alerts[0]
        EasyVisaSystemMessageType type = EasyVisaSystemMessageType.MONTHLY_PAYMENT_FAILED_GRACE
        assert type == alert.messageType
        assert type.subject == alert.subject
        assert EvSystemMessage.EASYVISA_SOURCE == alert.source

        cleanup:
        testHelper.clean()
    }

    void testFailedChargeNoPaymentMethod() {
        given:
        failedToPayNoPaymentMethodMock(paymentServiceMock, taxServiceMock)
        BigDecimal amount = updateMaintenanceFee()
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .reducePackageLegalRepUserCreatedDate(SEVENTY_DAYS)
                .buildUsersForPackageApplicants()

        expect:
        runJob()

        List<AccountTransaction> transactions
        User storedUser
        List<Alert> alerts
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(testHelper.packageLegalRepresentative.profile)
            storedUser = User.get(testHelper.packageLegalRepresentative.refresh().user.id)
            alerts = Alert.findAllByRecipient(storedUser)
        }
        assert 2 == transactions.size()
        TestUtils.assertCustomAccountTransactions(transactions, amount, TransactionSource.MAINTENANCE,
                TestUtils.MONTHLY_FEE_MEMO)
        TestUtils.assertCustomAccountTransactions(transactions, CLOUD_FEE, TransactionSource.CLOUD_STORAGE,
                MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, 1))
        assert storedUser.paid
        Alert alert = alerts[0]
        EasyVisaSystemMessageType type = EasyVisaSystemMessageType.MONTHLY_PAYMENT_FAILED_GRACE
        assert type == alert.messageType
        assert type.subject == alert.subject
        assert EvSystemMessage.EASYVISA_SOURCE == alert.source

        cleanup:
        testHelper.clean()
    }

    void testInstanceIdGenerator() {
        given:
        EvQuartzInstanceIdGenerator generator = new EvQuartzInstanceIdGenerator()

        expect:
        String id = generator.generateInstanceId()

        String prefix = 'EasyVisa'
        String[] parts = id.split(':')
        assert 3 == parts.length
        assert prefix == parts[0]
        assert UUID.fromString(parts[1])
        Calendar today = Calendar.getInstance()
        Calendar calendar = Calendar.getInstance()
        calendar.setTimeInMillis(parts[2] as long)
        assert today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
        assert today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
        assert today.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
    }

    private BigDecimal prorateMonthlyFee(LocalDate date, BigDecimal amount, Integer daysToCharge) {
        new BigDecimal(amount * (daysToCharge / date.dayOfMonth)).setScale(2, RoundingMode.HALF_UP)
    }

    private Date convertToDate(LocalDate date, Integer reduceDays = null) {
        LocalDate newDate = date
        if (reduceDays) {
            newDate = newDate.minusDays(reduceDays)
        }
        Date.from(newDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    }

    private LocalDate getLastDate() {
        LocalDate date = LocalDate.now()
        LocalDate.of(date.year, date.month, 1).minusDays(ONE_DAY)
    }

    private void runJob() {
        AccountTransaction.withNewSession {
            paymentJob.execute()
            Boolean.TRUE
        }
    }

    private BigDecimal updateMaintenanceFee() {
        BigDecimal amount = TestUtils.randomNumber()
        AdminConfig.withNewTransaction {
            AdminSettings settings = adminService.adminSettingsForUpdate
            settings.adminConfig.maintenanceFee = amount
            settings.adminConfig.cloudStorageFee = CLOUD_FEE
            settings.save(onError: true)
        }
        amount
    }

}
