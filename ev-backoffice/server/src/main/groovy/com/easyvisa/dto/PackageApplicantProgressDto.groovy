package com.easyvisa.dto

import groovy.transform.CompileStatic

@CompileStatic
class PackageApplicantProgressDto {

    String name
    String applicantType
    String packageStatus
    Integer percentComplete
    Integer elapsedDays
    Integer totalDays
    String dateStarted
    String dateCompleted
}
