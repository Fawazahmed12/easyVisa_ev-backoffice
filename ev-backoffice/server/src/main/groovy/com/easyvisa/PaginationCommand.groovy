package com.easyvisa

class PaginationCommand implements grails.validation.Validateable {

    Long max
    Long offset
    String sort
    String order = 'asc'

    Map getPaginationParams() {
        [max:getMax(), offset:getOffset()]
    }

    Long getMax() {
        max ?: 25
    }

    Long getOffset() {
        offset ?: 0
    }

    String getSortOrder() {
        if (order?.toLowerCase() == 'desc') {
            'desc'
        } else {
            'asc'
        }
    }
}
