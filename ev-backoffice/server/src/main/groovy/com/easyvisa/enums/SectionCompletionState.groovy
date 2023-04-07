package com.easyvisa.enums

enum SectionCompletionState {

    COMPLETED(true),
    PENDING(false)

    final Boolean value

    SectionCompletionState(Boolean value) {
        this.value = value
    }
}
