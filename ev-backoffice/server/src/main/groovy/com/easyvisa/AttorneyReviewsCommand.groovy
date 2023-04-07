package com.easyvisa

class AttorneyReviewsCommand extends PaginationCommand implements grails.validation.Validateable {

    Integer rating

    String getSortFieldName() {
        String result
        switch (sort) {
            case 'rating': result = 'rating'; break
            default: result = 'dateCreated'
        }
        result
    }
}