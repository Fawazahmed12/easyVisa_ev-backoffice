package com.easyvisa.document

import com.easyvisa.Applicant
import com.easyvisa.Package
import com.easyvisa.User
import com.easyvisa.enums.DocumentType
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
abstract class BaseDocument {

    Package aPackage
    Applicant applicant
    DocumentType documentType

    Date dateCreated
    Date lastUpdated
    User createdBy
    User updatedBy

    static mapping = {
        tablePerHierarchy false
        id generator: 'native', params: [sequence: 'base_document_id_seq']
    }

    abstract BaseDocument copy(Package newPackage, Applicant newApplicant)

    protected void copy(BaseDocument copy, Package newPackage, Applicant newApplicant) {
        copy.aPackage = newPackage
        copy.applicant = newApplicant
        copy.documentType = documentType
        copy.dateCreated = dateCreated
        copy.lastUpdated = lastUpdated
        copy.createdBy = createdBy
        copy.updatedBy = updatedBy
    }

}
