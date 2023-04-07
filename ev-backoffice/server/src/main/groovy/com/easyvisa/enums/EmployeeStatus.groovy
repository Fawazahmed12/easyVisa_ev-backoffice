package com.easyvisa.enums

enum EmployeeStatus {

    ACTIVE('Active'),
    INACTIVE('Inactive'),
    PENDING('Pending')

    final String displayName

    EmployeeStatus(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}