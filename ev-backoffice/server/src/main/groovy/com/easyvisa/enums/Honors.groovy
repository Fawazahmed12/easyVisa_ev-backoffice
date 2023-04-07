package com.easyvisa.enums

enum Honors {

    SUMMA_CUM_LAUDE('SUMMA_CUM_LAUDE'),
    MAGNA_CUM_LAUDE('Magna cum laude'),
    CUM_LAUDE('Cum laude')

    final String displayName

    Honors(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}
