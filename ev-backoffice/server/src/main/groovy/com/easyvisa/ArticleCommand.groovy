package com.easyvisa

class ArticleCommand implements grails.validation.Validateable {
    String locationId
    String locationName
    String title
    String content
    Long organizationId

    Organization getOrganization() {
        Organization.get(organizationId)
    }
}
