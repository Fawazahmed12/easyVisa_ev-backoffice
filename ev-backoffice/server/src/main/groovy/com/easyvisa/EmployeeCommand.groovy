package com.easyvisa

import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.EmployeeStatus

class EmployeeCommand implements grails.validation.Validateable {

    String language
    String mobilePhone
    String email
    String lastName
    String middleName
    String firstName
    String password
    String username
    String officePhone
    EmployeePosition position
    boolean isAdmin
    EmployeeStatus status
    Long organizationId
    Long id

    static List getProfileFields() {
        ['language', 'lastName', 'middleName', 'firstName']
    }

    static List nonUpdateableFields() {
        ['username', 'password']
    }

    boolean isUpdatable() {
        EmployeeCommand x = this
        nonUpdateableFields().every {
            x[it] == null
        }
    }

    Organization getOrganization() {
        Organization.get(organizationId)
    }

    Employee getEmployee() {
        if (id) {
            Employee.get(id)
        }
    }
}
