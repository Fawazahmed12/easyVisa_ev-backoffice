package com.easyvisa

class ImmigrationBenefitAssetValue {

    String easyVisaId
    String benefitCategory
    Integer assetValue

    static constraints = {
        easyVisaId nullable: false
        benefitCategory nullable: false
        assetValue nullable: false
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'immigration_benefit_asset_value_id_seq']
    }

}
