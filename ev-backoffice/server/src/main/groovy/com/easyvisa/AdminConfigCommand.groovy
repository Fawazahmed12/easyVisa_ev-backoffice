package com.easyvisa

class AdminConfigCommand implements grails.validation.Validateable {


    BigDecimal signupFee
    BigDecimal maintenanceFee
    BigDecimal cloudStorageFee
    BigDecimal membershipReactivationFee
    BigDecimal referralBonus
    BigDecimal signupDiscount
    BigDecimal articleBonus
    String contactPhone
    String supportEmail
}
