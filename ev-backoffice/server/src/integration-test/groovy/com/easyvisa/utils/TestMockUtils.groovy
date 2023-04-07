package com.easyvisa.utils

import com.easyvisa.*
import com.easyvisa.enums.ErrorMessageType
import org.apache.http.HttpStatus
import spock.lang.Specification

class TestMockUtils extends Specification {

    void updateToMock(AccountService accountService, PaymentService paymentServiceMock, TaxService taxServiceMock) {
        accountService.paymentService = paymentServiceMock
        accountService.taxService = taxServiceMock
    }

    void updateToService(AccountService accountService, PaymentService paymentService, TaxService taxService) {
        accountService.paymentService = paymentService
        accountService.taxService = taxService
    }

    void successPayMock(PaymentService paymentServiceMock, TaxService taxServiceMock, BigDecimal taxAmount = BigDecimal.ZERO) {
        _ * paymentServiceMock.charge(_ as BigDecimal, _ as User, _ as BigDecimal) >> { 'transactionId' }
        _ * taxServiceMock.createTransaction(*_) >> { new Tax(avaTaxId:'1', total:taxAmount, billingAddress:new Address()) }
        _ * taxServiceMock.commitTransaction(*_)
    }

    void failedToPayMock(PaymentService paymentServiceMock, TaxService taxServiceMock) {
        _ * paymentServiceMock.charge(_ as BigDecimal, _ as User, _ as BigDecimal) >> {
            throw new EasyVisaException(errorMessageCode: 'payment.failed', errorMessageType: ErrorMessageType.PAYMENT_FAILED,
                    errorCode: HttpStatus.SC_BAD_REQUEST)
        }
        _ * taxServiceMock.createTransaction(*_) >> { new Tax(avaTaxId:'1', total:0, billingAddress:new Address()) }
    }

    void failedToPayNoPaymentMethodMock(PaymentService paymentServiceMock, TaxService taxServiceMock) {
        _ * paymentServiceMock.charge(_ as BigDecimal, _ as User, _ as BigDecimal) >> {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'payment.method.not.configured')
        }
        _ * taxServiceMock.createTransaction(*_) >> { new Tax(avaTaxId:'1', total:0, billingAddress:new Address()) }
    }

    void successAndFailedToPayMock(PaymentService paymentServiceMock, TaxService taxServiceMock) {
        paymentServiceMock.charge(_ as BigDecimal, _ as User, _ as BigDecimal) >> { 'transactionId' } >>  {
            throw new EasyVisaException(errorMessageCode: 'payment.failed', errorMessageType: ErrorMessageType.PAYMENT_FAILED,
                    errorCode: HttpStatus.SC_BAD_REQUEST)
        } >> { 'transactionId' }
        _ * taxServiceMock.createTransaction(*_) >> { new Tax(avaTaxId:'1', total:0, billingAddress:new Address()) }
        _ * taxServiceMock.commitTransaction(*_)
    }

    void estimateTaxMock(TaxService taxServiceMock, BigDecimal subTotal, BigDecimal grandTotal = BigDecimal.ZERO, BigDecimal estTax = BigDecimal.ZERO) {
        _ * taxServiceMock.estimateTax(*_) >> { new TaxService.EstimatedTaxes(subTotal:subTotal, grandTotal:grandTotal, estTax:estTax) }
    }

}