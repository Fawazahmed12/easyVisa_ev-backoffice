package com.easyvisa

import com.easyvisa.enums.EvAlertSendTo
import com.easyvisa.enums.EvAlertSource

class AdminAlertCommand implements grails.validation.Validateable {

    String subject
    List<EvAlertSendTo> sendTo
    String source
    String body

    String findSourceName() {
        EvAlertSource evSource = EvAlertSource.values().find { it.displayName == source }
        evSource ? evSource.displayName : source
    }

    static constraints = {
        subject nullable: false
        sendTo validator: { List<EvAlertSendTo> sendToList ->
            if (!sendToList || sendToList.empty || sendToList.contains(null) || sendToList.contains('')) {
                ['send.to.error']
            }
        }
        source nullable: false
        body nullable: false
    }

}
