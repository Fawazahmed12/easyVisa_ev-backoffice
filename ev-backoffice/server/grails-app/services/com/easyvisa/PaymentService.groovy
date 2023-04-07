package com.easyvisa

import com.easyvisa.enums.ErrorMessageType

import static groovyx.net.http.ContentTypes.JSON

import groovyx.net.http.TransportingException

import com.easyvisa.dto.CardDetailsResponseDto
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import groovyx.net.http.ApacheHttpBuilder
import groovyx.net.http.HttpBuilder
import groovyx.net.http.HttpException
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Value

import java.text.DecimalFormat

/**
 * Performs operations with user credit card via Fattmerchant integration (charge, managing payment method, etc).
 */
@GrailsCompileStatic
class PaymentService {

    private static final String PAYMENT_METHOD_ERROR_CODE = 'payment.method.not.configured'
    private static final String PAYMENT_SYSTEM_NOT_AVAILABLE = 'payment.system.not.available'
    private static final String PAYMENT_DELAY = 'payment.delay'
    private final DecimalFormat decimalFormat = new DecimalFormat('#0.00')

    TaxService taxService

    @Value('${payment.url}')
    String paymentUrl
    @Value('${payment.api.key}')
    String paymentApiKey
    @Value('${payment.api.delay.message}')
    String fmDelayMessage

    /**
     * Sends request to Fattmerchnat for charging required amount fo money.
     * @param total amount of money to charge from credit card
     * @param user user to be charged
     * @param taxes taxes included to this charge
     * @return Fattmerchant transaction id
     */
    String charge(BigDecimal total, User user, BigDecimal taxes) {
        String result = null
        try {
            HttpBuilder client = configureHttpClient()
            String paymentToken = getPaymentToken(user)
            Object response = client.post {
                request.uri.path = '/charge'
                request.body = prepareChargeBody(paymentToken, total, taxes)
            }
            result = response['id']
        } catch (HttpException | TransportingException e) {
            handleException(e, 'payment.failed', HttpStatus.SC_UNPROCESSABLE_ENTITY, ErrorMessageType.PAYMENT_FAILED)
        }
        result
    }

    /**
     * Gets card details from Fattmerchant.
     * @param user user to get card details for
     * @return limited card details
     */
    CardDetailsResponseDto getCardDetails(Long userId) {
        User user = User.get(userId)
        CardDetailsResponseDto result = null
        try {
            PaymentMethod paymentMethod = getPaymentMethod(user)
            result = prepareCardDetails(user, paymentMethod)
        } catch (HttpException | TransportingException e) {
            handleException(e, PAYMENT_METHOD_ERROR_CODE)
        }
        result
    }

    /**
     * Saves Fattmerchant card details for the user.
     * @param userId user id to set payment details
     * @param paymentMethodCommand card details to save
     */
    @Transactional
    CardDetailsResponseDto saveToken(Long userId, PaymentMethodCommand paymentMethodCommand) {
        User user = User.get(userId)
        PaymentMethod paymentMethod = getPaymentMethod(user)
        paymentMethod = paymentMethod ?: new PaymentMethod(user: user)
        String oldToken = paymentMethod.fmPaymentMethodId
        populatePaymentMethodDetailsFromFM(paymentMethodCommand, paymentMethod)
        //validate address at Avalara
        taxService.validateBillingAddress(user. profile, paymentMethod)
        if (oldToken && oldToken != paymentMethod.fmPaymentMethodId) {
            deletePaymentToken(oldToken, user.id)
        }
        paymentMethod.save(failOnError:true, flush:true)

        String newCustomerId = paymentMethodCommand.customerId
        String oldCustomerId = user.fmCustomerId
        if (oldCustomerId && oldCustomerId != newCustomerId) {
            log.warn("User $userId has changed FM customer_id from ${oldCustomerId} to ${newCustomerId}")
        }
        user.fmCustomerId = newCustomerId
        user.save(failOnError: true)
        prepareCardDetails(user, paymentMethod)
    }

    /**
     * Returns user's payment method.
     * @param user user
     * @return PaymentMethod
     */
    PaymentMethod getPaymentMethod(User user) {
        if (!user) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'user.not.found.with.id')
        }
        PaymentMethod.findByUser(user)
    }

    private CardDetailsResponseDto prepareCardDetails(User user, PaymentMethod paymentMethod) {
        CardDetailsResponseDto cardDetails = new CardDetailsResponseDto(customerId: user.fmCustomerId)
        if (paymentMethod) {
            cardDetails.with {
                cardHolder = paymentMethod.cardHolder
                cardType = paymentMethod.cardType
                cardLastFour = paymentMethod.cardLastFour
                cardExpiration = paymentMethod.cardExpiration
                address1 = paymentMethod.address1
                address2 = paymentMethod.address2
                addressCity = paymentMethod.addressCity
                addressState = paymentMethod.addressState
                addressCountry = paymentMethod.addressCountry
                addressZip = paymentMethod.addressZip
            }
        }
        cardDetails
    }

    private void populatePaymentMethodDetailsFromFM(PaymentMethodCommand paymentMethodCommand,
                                                    PaymentMethod paymentMethod) {
        paymentMethod.with {
            cardHolder = paymentMethodCommand.cardHolder
            cardType = paymentMethodCommand.cardType
            cardLastFour = paymentMethodCommand.cardLastFour
            cardExpiration = paymentMethodCommand.cardExpiration
            address1 = paymentMethodCommand.address1
            address2 = paymentMethodCommand.address2
            addressCity = paymentMethodCommand.addressCity
            addressState = paymentMethodCommand.addressState
            addressCountry = paymentMethodCommand.addressCountry
            addressZip = paymentMethodCommand.addressZip
            fmPaymentMethodId = paymentMethodCommand.fmPaymentMethodId
        }
    }

    void deletePaymentToken(String token, Long userId) {
        try {
            HttpBuilder client = configureHttpClient()
            client.delete {
                request.uri.path = "/payment-method/$token"
            }
        } catch (HttpException | TransportingException e) {
            log.info("Can't delete old payment token from User = $userId in Fattmerchant", e)
        }
    }

    private String getPaymentToken(User user) {
        PaymentMethod paymentMethod = getPaymentMethod(user)
        if (paymentMethod && paymentMethod.fmPaymentMethodId && !paymentMethod.isExpired()) {
            return paymentMethod.fmPaymentMethodId
        }
        throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: PAYMENT_METHOD_ERROR_CODE)
    }

    private HttpBuilder configureHttpClient() {
        ApacheHttpBuilder.configure {
            request.uri = paymentUrl
            //can't convert to Groovy usage
            request.headers.put('Authorization', "Bearer $paymentApiKey")
            request.contentType = JSON[0]
            request.accept = [JSON[0]]
        }
    }

    private Object prepareChargeBody(String paymentToken, BigDecimal total, BigDecimal taxes) {
        ['payment_method_id': paymentToken,
         'meta'             : ['tax': decimalFormat.format(taxes)],
         'total'            : decimalFormat.format(total),
         'pre_auth'         : 0,]
    }

    private void handleException(Exception e, String errorMsg, Integer httpCode = HttpStatus.SC_UNPROCESSABLE_ENTITY,
                                 ErrorMessageType errorMessageType = null) {
        String errorMessageCode = errorMsg
        Integer errorCode = httpCode
        ErrorMessageType typeToSet = null
        if ((e instanceof HttpException)) {
            if (e.statusCode % 100 == 5) {
                errorMessageCode = PAYMENT_SYSTEM_NOT_AVAILABLE
                errorCode = HttpStatus.SC_BAD_GATEWAY
            }
            if (e.statusCode == HttpStatus.SC_BAD_REQUEST) {
                errorCode = HttpStatus.SC_BAD_REQUEST
                if (e.body['message'] == fmDelayMessage) {
                    errorMessageCode = PAYMENT_DELAY
                    typeToSet = ErrorMessageType.PAYMENT_FAILED
                } else {
                    typeToSet = errorMessageType
                }
            }
        }
        throw new EasyVisaException(errorCode: errorCode, errorMessageCode: errorMessageCode,
                errorMessageType: typeToSet)
    }

}
