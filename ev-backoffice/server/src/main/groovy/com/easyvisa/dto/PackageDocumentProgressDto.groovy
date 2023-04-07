package com.easyvisa.dto

import groovy.transform.CompileStatic

@CompileStatic
class PackageDocumentProgressDto {
    String name
    String packageStatus
    Integer percentComplete
    Integer elapsedDays
    Integer totalDays
    String dateStarted
    String dateCompleted
}
