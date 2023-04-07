package com.easyvisa.enums

enum EmployeePosition {

    PARTNER('Partner/Owner'),
    ATTORNEY('Attorney/Representative'),
    MANAGER('Manager'),
    EMPLOYEE('Employee'),
    TRAINEE('Trainee')

    final String displayName

    EmployeePosition(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
    static List<EmployeePosition> getAttorneyPositions() {
        [ATTORNEY, PARTNER]
    }

    static List<EmployeePosition> getEmployeePositions() {
        [MANAGER, EMPLOYEE, TRAINEE]
    }
}