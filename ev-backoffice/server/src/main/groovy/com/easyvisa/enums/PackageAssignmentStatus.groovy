package com.easyvisa.enums

enum PackageAssignmentStatus {


    PENDING('Pending'),
    ACTIVE('Active'),
    INACTIVE('Inactive')

    final String displayName

    PackageAssignmentStatus(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}