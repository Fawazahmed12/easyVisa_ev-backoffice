package com.easyvisa

import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.EmployeeStatus
import groovy.transform.ToString

@SuppressWarnings('GetterMethodCouldBeProperty')
@ToString(includeNames = true, includePackage = false)
class OrganizationEmployee {
    Organization organization
    Employee employee
    Date activeDate = new Date()
    Date inactiveDate
    EmployeeStatus status = EmployeeStatus.ACTIVE
    EmployeePosition position
    Boolean isAdmin = false

    def beforeUpdate() {
        if (position == EmployeePosition.PARTNER) {
            isAdmin = true
        }
        if (status == EmployeeStatus.INACTIVE && !inactiveDate) {
            inactiveDate = new Date()
        }
    }

    static constraints = {
        inactiveDate nullable: true
        isAdmin validator: { val, obj, errors ->
            if (obj.position == EmployeePosition.PARTNER && !val) {
                errors.rejectValue(['organization.partner.must.be.admin'])
            }
        }
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'organization_employee_id_seq']
    }

    /**
     * Convinience wrapper to get user associated with this OrgEmployee
     * @return User Object
     */
    User getUser() {
        employee.user
    }
}
