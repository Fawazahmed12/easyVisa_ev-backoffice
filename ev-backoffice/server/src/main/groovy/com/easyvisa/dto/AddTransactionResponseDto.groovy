package com.easyvisa.dto

import com.easyvisa.AccountTransaction
import groovy.transform.CompileStatic

@CompileStatic
class AddTransactionResponseDto {

    BigDecimal balance
    AccountTransaction accountTransaction

}
