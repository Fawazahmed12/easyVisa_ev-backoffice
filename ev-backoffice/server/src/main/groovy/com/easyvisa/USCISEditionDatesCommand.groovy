package com.easyvisa

import com.easyvisa.utils.ExceptionUtils

class USCISEditionDatesCommand implements grails.validation.Validateable {
    List<USCISEditionDateCommand> uscisEditionDateList

    // default
    String sort = 'order'
    String order = 'asc'

    void validateUSCISEditionDates() {
        this.uscisEditionDateList.each { USCISEditionDateCommand uscisEditionDateCommand ->
            if (!uscisEditionDateCommand.editionDate || !uscisEditionDateCommand.expirationDate) {
                String dateType = !uscisEditionDateCommand.editionDate ? 'Edition' : 'Expiration'
                throw ExceptionUtils.createUnProcessableDataException('uscis.invalid.date', null,
                        [dateType,uscisEditionDateCommand.formId])
            }
        }
    }
}
