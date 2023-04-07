package com.easyvisa.enums

enum RepresentativeType {

    ATTORNEY("Attorney"),
    ACCREDITED_REPRESENTATIVE("Accredited Representative")

    final String displayName

    RepresentativeType(String displayName) {
        this.displayName = displayName
    }
}