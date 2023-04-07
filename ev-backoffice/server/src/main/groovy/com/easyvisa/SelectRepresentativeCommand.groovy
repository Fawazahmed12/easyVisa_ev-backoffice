package com.easyvisa

import com.easyvisa.enums.AttorneyType
import com.easyvisa.enums.RepresentativeType
import grails.validation.Validateable

class SelectRepresentativeCommand implements Validateable {

    RepresentativeType representativeType
    AttorneyType attorneyType

}
