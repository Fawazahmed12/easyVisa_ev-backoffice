package com.easyvisa

class FindReviewCommand extends PaginationCommand implements grails.validation.Validateable {
    Integer rating

    String getSortFieldName() {
        String fieldName
        switch (sort) {
            case 'rating': fieldName = 'rating'; break
            case 'replied': fieldName = 'reply'; break
            default: fieldName = 'dateCreated'
        }
        fieldName
    }
}
