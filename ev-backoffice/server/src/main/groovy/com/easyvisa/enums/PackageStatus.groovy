package com.easyvisa.enums

enum PackageStatus {

    LEAD('Lead'),
    OPEN('Open'),
    BLOCKED('Blocked'),
    CLOSED('Closed'),
    TRANSFERRED('Transferred'),
    DELETED('Deleted')

    final String displayName

    PackageStatus(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}
