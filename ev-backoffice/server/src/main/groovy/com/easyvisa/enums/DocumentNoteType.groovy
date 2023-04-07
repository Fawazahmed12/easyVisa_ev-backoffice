package com.easyvisa.enums

enum DocumentNoteType {

    PUBLIC_NOTE('PUBLIC_NOTE'),
    REPRESENTATIVE_NOTE('REPRESENTATIVE_NOTE')

    final String displayName

    DocumentNoteType(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}
