package com.easyvisa

class AdminConfig {

    BigDecimal signupFee
    BigDecimal maintenanceFee
    BigDecimal cloudStorageFee
    BigDecimal membershipReactivationFee
    BigDecimal referralBonus
    BigDecimal signupDiscount
    BigDecimal articleBonus

    String contactPhone
    String supportEmail

    //Gov Fees
    BigDecimal i129f
    BigDecimal i130
    BigDecimal i360
    BigDecimal i485
    BigDecimal i485_14
    BigDecimal i600_600a
    BigDecimal i601
    BigDecimal i601a
    BigDecimal i751
    BigDecimal i765
    BigDecimal n400
    BigDecimal n600_n600k
    BigDecimal biometricServiceFee

    LegalRepresentative attorney

    static constraints = {
        contactPhone nullable: true
        supportEmail nullable: true
        i129f nullable: true
        i130 nullable: true
        i360 nullable: true
        i485 nullable: true
        i485_14 nullable: true
        i600_600a nullable: true
        i601 nullable: true
        i601a nullable: true
        i751 nullable: true
        i765 nullable: true
        n400 nullable: true
        n600_n600k nullable: true
        biometricServiceFee nullable: true
        attorney nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'admin_config_id_seq']
    }

}
