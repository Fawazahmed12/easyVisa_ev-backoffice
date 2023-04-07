package com.easyvisa

class UscisEditionDate {

    Long id
    String formId
    Date editionDate
    Date expirationDate
    Organization organization

    Date dateCreated
    Date lastUpdated
    User createdBy
    User updatedBy

    static constraints = {
        formId nullable: false
        editionDate nullable: false
        expirationDate nullable: false
        organization nullable: false
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'uscis_edition_date_id_seq']
    }
}
