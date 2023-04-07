package com.easyvisa.enums

enum RelationshipType {

    CHILD('Child'),
    SPOUSE('Spouse')

    final String displayName

    RelationshipType (String displayName) {
        this.displayName = displayName
    }

}
