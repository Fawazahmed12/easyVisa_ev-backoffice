package com.easyvisa.enums

enum TransactionSource {

    ARTICLE,
    REFERRAL,
    PAYMENT,
    PACKAGE(TaxItemType.IMMIGRATION_BENEFIT),
    REGISTRATION(TaxItemType.SIGNUP_FEE),
    REACTIVATION(TaxItemType.MEMBERSHIP_REACTIVATION_FEE),
    MAINTENANCE(TaxItemType.MAINTENANCE_FEE),
    CLOUD_STORAGE(TaxItemType.CLOUD_STORAGE_FEE),
    REFUND

    TaxItemType taxItemType

    TransactionSource(TaxItemType taxItemType = null) {
        this.taxItemType = taxItemType
    }

    static List<TransactionSource> getTaxable() {
        [PACKAGE, REGISTRATION, REACTIVATION, MAINTENANCE, CLOUD_STORAGE]
    }
}