package com.easyvisa

import org.springframework.beans.factory.annotation.Value

class EasyVisaTagLib {

    @Value('${frontEndAppURL}')
    String frontEndAppURL

    def acceptLink = { attrs, body ->
        Long alertId = attrs.alertId
        out << "<a class='font-weight-bold' href='${frontEndAppURL}/alerts/${alertId}/reply?accept=true'>ACCEPT</a>"
    }

    def denyLink = { attrs, body ->
        Long alertId = attrs.alertId
        out << "<a href='${frontEndAppURL}/alerts/${alertId}/reply?accept=false' class='text-danger font-weight-bold'>DENY</a>"
    }
    def dispositionsLink = { attrs, body ->
        out << "<a href='${frontEndAppURL}/task-queue/dispositions'>Dispositions page</a>"
    }

    def packageQuestionnaireLink = { attrs, body ->
        Long packageId = attrs.packageId
        Long applicantId = attrs.applicantId
        String sectionId = attrs.sectionId
        String packageName = attrs.packageName
        out << "<a href='${frontEndAppURL}/questionnaire/package/${packageId}/applicants/${applicantId}/sections/${sectionId}'>${packageName}</a>"
    }

    def documentPortalLink = { attrs, body ->
        Long packageId = attrs.packageId
        String packageName = attrs.packageName
        out << "<a href='${frontEndAppURL}/documents/package/${packageId}'>${packageName}</a>"
    }

}
