package com.easyvisa.document

import com.easyvisa.Package
import com.easyvisa.User
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class DocumentMilestone {
    Package aPackage
    Date milestoneDate
    String milestoneTypeId

    Date dateCreated
    Date lastUpdated
    User createdBy
    User updatedBy

    static mapping = {
        id generator: 'native', params: [sequence: 'document_milestone_id_seq']
    }

}
