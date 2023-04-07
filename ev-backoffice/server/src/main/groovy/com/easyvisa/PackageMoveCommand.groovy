package com.easyvisa

import com.easyvisa.enums.PackageStatus

class PackageMoveCommand implements grails.validation.Validateable {

    PackageStatus newStatus
}