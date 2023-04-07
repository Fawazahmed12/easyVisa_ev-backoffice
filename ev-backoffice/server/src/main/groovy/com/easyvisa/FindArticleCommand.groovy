package com.easyvisa

class FindArticleCommand extends PaginationCommand implements grails.validation.Validateable {

    Long representativeId
    Boolean isApproved
    Long organizationId

    String getSortFieldName() {
        String fieldName
        switch (sort) {
            case 'title': fieldName = 'title'; break
            case 'views': fieldName = 'views'; break
            case 'words': fieldName = 'wordsCount'; break
            case 'approved': fieldName = 'isApproved'; break
            default: fieldName = 'dateCreated'
        }
        fieldName
    }

    LegalRepresentative getRepresentative() {
        LegalRepresentative.get(representativeId)
    }

    Organization getOrganization() {
        Organization.get(organizationId)
    }
}