package com.easyvisa

import com.easyvisa.enums.PackageAssignmentStatus
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class PackageAssignee {

    Package aPackage
    LegalRepresentative representative
    Organization organization
    Date startDate
    Date endDate
    PackageAssignmentStatus status

    static constraints = {
        endDate nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'package_assignee_id_seq']
    }

}
