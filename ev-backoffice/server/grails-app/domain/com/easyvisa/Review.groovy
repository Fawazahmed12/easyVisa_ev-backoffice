package com.easyvisa

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class Review {

    Applicant reviewer
    Package aPackage
    LegalRepresentative representative
    Integer rating
    String title
    String review

    Boolean read = Boolean.FALSE
    String reply

    Date dateCreated
    Date lastUpdated

    static constraints = {
        rating min: 1, max: 5
        reply nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'review_id_seq']
        review sqlType: 'text'
        reply sqlType: 'text'
        read defaultValue: "'false'"
    }
}
