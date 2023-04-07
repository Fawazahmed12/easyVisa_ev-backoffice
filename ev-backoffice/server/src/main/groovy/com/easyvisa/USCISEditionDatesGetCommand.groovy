package com.easyvisa

import com.easyvisa.utils.ExceptionUtils

class USCISEditionDatesGetCommand implements grails.validation.Validateable {

    String sort = 'order'
    String order = 'asc'

    String getSortOrder() {
        if (order?.toLowerCase() == 'desc') {
            'desc'
        } else {
            'asc'
        }
    }

    void validateUSCISEditionDateParam() {
        List<String> validSortByFields = ['order', 'displayText', 'editionDate', 'expirationDate'];
        if (!validSortByFields.contains(sort)) {
            throw ExceptionUtils.createUnProcessableDataException('search.query.not.valid')
        }
    }
}
