package com.easyvisa

import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class LegalRepresentativeRevenue {

    BigDecimal revenue
    String memo
    Package aPackage
    Organization organization
    LegalRepresentative attorney

    Date dateCreated
    Date lastUpdated

    static constraints = {
        aPackage nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'legal_representative_revenue_seq']
    }

}
