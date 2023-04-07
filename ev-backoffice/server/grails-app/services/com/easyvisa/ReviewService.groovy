package com.easyvisa

import grails.gorm.transactions.Transactional

class ReviewService {

    @Transactional
    Review create(Applicant reviewer, Package aPackage, LegalRepresentative representative, Integer rating, String title, String reviewText) {

        Review review = new Review(reviewer: reviewer, aPackage: aPackage, representative: representative, rating: rating, title: title, review: reviewText)
        review.save()
    }

    def reviews(LegalRepresentative representative, FindReviewCommand findReviewCommand, boolean isCount = false) {
        List<Review> reviews = Review.createCriteria().list() {
            eq "representative", representative
            if (findReviewCommand.rating) {
                eq "rating", findReviewCommand.rating
            }
            if (!isCount) {
                maxResults(findReviewCommand.paginationParams.max.intValue())
                firstResult(findReviewCommand.paginationParams.offset.intValue())
                order(findReviewCommand.sortFieldName, findReviewCommand.getSortOrder())
            }
        }
    }

    @Transactional
    def updateReview(Review review, Integer rating, String title, String reviewText) {
        review.rating = rating
        review.title = title
        review.review = reviewText
        review.save()
    }

}
