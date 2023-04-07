package com.easyvisa

class ReviewCommand implements grails.validation.Validateable {
    Long packageId
    Long representativeId
    Integer rating
    String title
    String review
    String reply
    Boolean read
}
