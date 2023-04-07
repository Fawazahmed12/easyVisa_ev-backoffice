package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.enums.TransactionSource
import com.easyvisa.utils.TestMockUtils
import com.easyvisa.utils.TestUtils
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired

@Integration
class AccountServiceSpec extends TestMockUtils {

    private static final BigDecimal MINUS_HUNDRED = new BigDecimal(-100)
    private static final BigDecimal MINUS_ONE_TWENTY = new BigDecimal(-120)
    private static final BigDecimal TWENTY = new BigDecimal(20)
    private static final String CHARGE_MEMO = 'Paid balance.'

    @Autowired
    private AccountService accountService
    @Autowired
    private PaymentService paymentService
    private PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    private TaxService taxService
    private TaxService taxServiceMock = Mock(TaxService)

    def setup() {
        updateToMock(accountService, paymentServiceMock, taxServiceMock)
        successPayMock(paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(accountService, paymentService, taxService)
    }

    void testCharge() {
        given:
        User user
        Profile profile
        BigDecimal chargeAmount = TestUtils.randomNumber()
        User.withNewTransaction {
            user = TestUtils.createUser('username': 'chargeaccounttransactions',
                    email: 'chargeaccounttransactions@easyvisa.com')
            new PaymentMethod(user: user, fmPaymentMethodId: 'token').save(failOnError: true)
            profile = new Profile(user: user, lastName: 'transaction', firstName: 'charge', middleName: 'account',
                    email: 'chargeaccounttransactions@easyvisa.com', easyVisaId: 'testId')
            new AccountTransaction(profile:profile, memo:TestUtils.MONTHLY_FEE_MEMO, amount:TestUtils.randomNumber(), source:TransactionSource.MAINTENANCE)
                    .save(failOnError: true)
            profile.save(failOnError: true)
        }

        accountService.charge(user)
        AccountTransaction at
        AccountTransaction.withNewTransaction {
            at = AccountTransaction.findByProfileAndSource(profile, TransactionSource.PAYMENT)
            user = User.get(user.id)
        }

        expect:
        assert chargeAmount, at.amount
        assert CHARGE_MEMO, at.memo

        cleanup:
        TestUtils.deleteUserAndPaymentDetails(user)
    }

    void testBalanceNoRecords() {
        given:
        User user
        User.withNewTransaction {
            user = TestUtils.createUser('username': 'noaccounttransactions',
                    email: 'noaccounttransactions@easyvisa.com')
            Profile profile = new Profile(user: user, lastName: 'transaction', firstName: 'charge', middleName: 'account',
                    email: 'chargeaccounttransactions@easyvisa.com', easyVisaId: 'testId')
            profile.save('onError': true)
        }
        BigDecimal balance = accountService.getBalance(user.id)

        expect:
        assert !balance

        cleanup:
        TestUtils.deleteUserAndPaymentDetails(user)
    }

    void testBalancePositive() {
        given:
        User user
        User.withNewTransaction {
            user = TestUtils.createUser('username': 'positiveaccounttransactions',
                    email: 'positiveaccounttransactions@easyvisa.com')
            Profile profile = new Profile(user: user, lastName: 'transaction', firstName: 'charge', middleName: 'account',
                    email: 'chargeaccounttransactions@easyvisa.com', easyVisaId: 'testId')
            profile.save('onError': true)
            AccountTransaction at = new AccountTransaction(profile: profile, amount: TWENTY, memo: 'test memo',
                    source: TransactionSource.ARTICLE)
            at.save('onError': true)
        }
        BigDecimal balance = accountService.getBalance(user.id)

        expect:
        assert TWENTY, balance

        cleanup:
        TestUtils.deleteUserAndPaymentDetails(user)
    }

    void testBalanceNegative() {
        given:
        User user
        User.withNewTransaction {
            user = TestUtils.createUser('username': 'negativeaccounttransactions',
                    email: 'negativeaccounttransactions@easyvisa.com')
            Profile profile = new Profile(user: user, lastName: 'transaction', firstName: 'charge', middleName: 'account',
                    email: 'chargeaccounttransactions@easyvisa.com', easyVisaId: 'testId')
            profile.save('onError': true)
            AccountTransaction at = new AccountTransaction(profile: profile, amount: TWENTY, memo: 'test memo',
                    source: TransactionSource.ARTICLE)
            at.save('onError': true)
            at = new AccountTransaction(profile: profile, amount: MINUS_ONE_TWENTY, memo: 'test maintanance',
                    source: TransactionSource.MAINTENANCE)
            at.save('onError': true)
        }
        BigDecimal balance = accountService.getBalance(user.id)

        expect:
        assert MINUS_HUNDRED, balance

        cleanup:
        TestUtils.deleteUserAndPaymentDetails(user)
    }

    void testBalanceZero() {
        given:
        User user
        User.withNewTransaction {
            user = TestUtils.createUser('username': 'zeroaccounttransactions',
                    email: 'zeroaccounttransactions@easyvisa.com')
            Profile profile = new Profile(user: user, lastName: 'transaction', firstName: 'charge', middleName: 'account',
                    email: 'chargeaccounttransactions@easyvisa.com', easyVisaId: 'testId')
            profile.save('onError': true)
            AccountTransaction at = new AccountTransaction(profile: profile, amount: MINUS_ONE_TWENTY.abs(),
                    memo: 'test memo', source: TransactionSource.ARTICLE)
            at.save('onError': true)
            at = new AccountTransaction(profile: profile, amount: MINUS_ONE_TWENTY, memo: 'test maintenance',
                    source: TransactionSource.MAINTENANCE)
            at.save('onError': true)
        }
        BigDecimal balance = accountService.getBalance(user.id)

        expect:
        assert !balance

        cleanup:
        TestUtils.deleteUserAndPaymentDetails(user)
    }

}
