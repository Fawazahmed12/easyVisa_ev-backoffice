package com.easyvisa.enums

enum TaxItemType {

    SIGNUP_FEE('Account signup fee'),
    MAINTENANCE_FEE('Monthly account maintenance fee'),
    CLOUD_STORAGE_FEE('{0}'),
    MEMBERSHIP_REACTIVATION_FEE('Reactivate canceled membership'),
    IMMIGRATION_BENEFIT('Processing of {0} benefit category')

    final String description

    TaxItemType(String description) {
        this.description = description
    }

}