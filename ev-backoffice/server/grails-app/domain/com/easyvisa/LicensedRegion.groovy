package com.easyvisa

import com.easyvisa.enums.State
import grails.databinding.BindingFormat

class LicensedRegion {
    State state
    String barNumber
    @BindingFormat('MM-dd-yyyy')
    Date dateLicensed

    static mapping = {
        id generator: 'native', params: [sequence: 'licensed_region_id_seq']
        dateLicensed sqlType: 'date'
    }
}

