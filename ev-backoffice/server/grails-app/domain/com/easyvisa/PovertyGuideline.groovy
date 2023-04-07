package com.easyvisa

import com.easyvisa.enums.State

class PovertyGuideline {
    State state
    Double basePrice
    Double addOnPrice
    Integer year


    static constraints = {
        state nullable: false
        basePrice nullable: false
        addOnPrice nullable: false
        year nullable: false
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'poverty_guideline_id_seq']
    }
}
