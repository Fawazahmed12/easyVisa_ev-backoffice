package com.easyvisa.test.questionnaire

import com.easyvisa.*
import com.easyvisa.dto.CardDetailsResponseDto
import com.easyvisa.utils.TestUtils
import grails.testing.mixin.integration.Integration
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification

@Integration
class PaymentServiceSpec extends Specification {

    private static final String CARD_1111 = '1111'
    private static final String CARD_1881 = '1881'
    private static final String CARD_HOLDER = 'John Doe'
    private static final String PAYMENT_ERROR_CODE = 'payment.failed'
    private static final String COUNTRY = 'UNITED_STATES'
    private static final String CARD_EXP = '112024'
    private static final String CARD_TYPE = 'visa'
    private static final String ADDRESS1 = '208 Concord Ave'
    private static final String ADDRESS2 = null
    private static final String STATE = 'MA'
    private static final String CITY = 'Cambridge'
    private static final String ZIP = '02138'
    private static final String CUSTOMER_ID = '1400f525-ce4f-4141-b122-d95497a1cea4'
    private static final String CONVERTED_EXP = '11/2024'
    private static final String CONVERTED_TYPE = 'Visa'

    @Value('${payment.url}')
    private String paymentUrl
    @Value('${payment.api.key}')
    private String paymentApiKey

    @Autowired
    private PaymentService paymentService

    void testChargeFailed() {
        given:
        User user
        User.withNewTransaction {
            user = TestUtils.createUser('username': 'chargefailed', email: 'chargefailed@easyvisa.com')
            new Profile(user: user, lastName: 'transaction', firstName: 'charge', middleName: 'account',
                    email: 'chargefailed@easyvisa.com', easyVisaId: 'testId').save('onError': true)
            TestUtils.getPaymentMethod(CARD_1881, user, paymentApiKey, paymentUrl).save(onError: true)
        }

        when:
        User.withNewTransaction {
            paymentService.charge(TestUtils.randomNumber(), user, BigDecimal.ZERO)
        }

        then:
        EasyVisaException e = thrown()
        assert HttpStatus.SC_BAD_REQUEST == e.errorCode
        assert PAYMENT_ERROR_CODE == e.errorMessageCode

        cleanup:
        TestUtils.deleteUserAndPaymentDetails(user)
    }

    void testGetPaymentDetailsVisa() {
        given:
        User user
        String token = 'token'
        User.withNewTransaction {
            user = TestUtils.createUser('username': 'paymentmethodvisa', email: 'paymentmethodvisa@easyvisa.com')
            new Profile(easyVisaId:'testEVId', firstName:'Success', lastName:'Payment', user:user)
                    .save(failOnError:true, flush:true)
            paymentService.saveToken(user.id, initPaymentMethodCommand(CARD_1111, token))
        }

        expect:
        CardDetailsResponseDto details = null
        PaymentMethod.withNewTransaction {
            details = paymentService.getCardDetails(user.id)
        }
        assertCardDetails(details, CARD_1111, CONVERTED_EXP, CONVERTED_TYPE)
        assertAddressFull(details, ADDRESS1, ADDRESS2, STATE, CITY, ZIP, COUNTRY)

        cleanup:
        TestUtils.deleteUserAndPaymentDetails(user)
    }

    void testSaveToken() {
        given:
        User user = null
        String testToken = 'token'
        PaymentMethod paymentMethod = null
        CardDetailsResponseDto cardDetails = null
        User.withNewTransaction {
            user = TestUtils.createUser('username': 'savepaymenttoken', email: 'savepaymenttoken@easyvisa.com')
            new Profile(easyVisaId:'testEVId', firstName:'Success', lastName:'Payment', user:user)
                    .save(failOnError:true, flush:true)
            cardDetails = paymentService.saveToken(user.id, initPaymentMethodCommand(CARD_1111, testToken))
            paymentMethod = PaymentMethod.findByUser(user)
        }

        expect:
        assert testToken == paymentMethod.fmPaymentMethodId
        assertCardDetails(cardDetails, CARD_1111, CONVERTED_EXP, CONVERTED_TYPE)
        assertAddressFull(cardDetails, ADDRESS1, ADDRESS2, STATE, CITY, ZIP, COUNTRY)

        cleanup:
        TestUtils.deleteUserAndPaymentDetails(user)
    }

    void testUpdateToken() {
        given:
        User user = null
        User.withNewTransaction {
            user = TestUtils.createUser('username': 'updatepaymenttoken', email: 'updatepaymenttoken@easyvisa.com')
            new PaymentMethod(user: user, fmPaymentMethodId: 'old_token').save(failOnError: true)
            new Profile(easyVisaId:'testEVId', firstName:'Success', lastName:'Payment', user:user)
                    .save(failOnError:true, flush:true)
        }
        String testToken = 'token'
        paymentService.saveToken(user.id, initPaymentMethodCommand(CARD_1111, testToken))

        expect:
        PaymentMethod paymentMethod = null
        PaymentMethod.withNewTransaction {
            paymentMethod = PaymentMethod.findByUser(user)
            user = User.get(user.id)
        }
        assert testToken == paymentMethod.fmPaymentMethodId

        cleanup:
        TestUtils.deleteUserAndPaymentDetails(user)
    }

    private void assertCardDetails(CardDetailsResponseDto details, String lastFour, String expiration, String type) {
        assert lastFour == details.cardLastFour
        assert expiration == details.cardExpiration
        assert type == details.cardType
        assert CARD_HOLDER == details.cardHolder
    }

    private void assertAddressFull(CardDetailsResponseDto details, String address1, String address2, String state,
                                   String city, String zip, String country) {
        assert address1 == details.address1
        assert address2 == details.address2
        assert state == details.addressState
        assert city == details.addressCity
        assert zip == details.addressZip
        assert country == details.addressCountry
    }

    private PaymentMethodCommand initPaymentMethodCommand(String cardLastFour, String token) {
        new PaymentMethodCommand(fmPaymentMethodId: token, customerId: CUSTOMER_ID,
                cardHolder: CARD_HOLDER, cardLastFour: cardLastFour, cardType: CARD_TYPE, cardExpiration: CARD_EXP,
                address1: ADDRESS1, address2: ADDRESS2, addressCity: CITY, addressState: STATE, addressCountry: COUNTRY,
                addressZip: ZIP)
    }

}
