package com.easyvisa

import com.easyvisa.enums.ProspectType
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class ProspectCounts {
    LegalRepresentative representative
    Date searchDate
    ProspectType prospectType

    Date dateCreated
    Date lastUpdated

    static constraints = {
        searchDate sqlType: 'date'
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'prospect_counts_id_seq']
    }

}
