package com.easyvisa

import com.easyvisa.enums.ImmigrationBenefitCategory

class FeeScheduleCommand implements grails.validation.Validateable {

    BigDecimal amount
    ImmigrationBenefitCategory benefitCategory
}
