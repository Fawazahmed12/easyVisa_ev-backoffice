package com.easyvisa

import com.easyvisa.enums.PackageStatus
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

@GrailsCompileStatic
class ReviewController implements IErrorHandler {
    ApplicantService applicantService
    AttorneyService attorneyService
    MessageSource messageSource
    PermissionsService permissionsService
    ReviewService reviewService
    SpringSecurityService springSecurityService

    @Secured([Role.USER])
    def create(ReviewCommand reviewCommand) {
        User user = springSecurityService.currentUser as User
        Applicant reviewer = applicantService.findApplicantByUser(user.id)

        Package aPackage = Package.get(reviewCommand.packageId)
        if (![PackageStatus.OPEN, PackageStatus.CLOSED].contains(aPackage.status)) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'review.package.must.be.open.or.closed')
        }
        LegalRepresentative representative = LegalRepresentative.get(reviewCommand.representativeId)
        validatePackageAndRepresentative(aPackage, representative)
        permissionsService.validateCreateReview(reviewer, aPackage, representative)

        Review review = reviewService.create(reviewer, aPackage, representative, reviewCommand.rating, reviewCommand.title, reviewCommand.review)
        attorneyService.calculateAttorneyReview(reviewCommand.representativeId)
        render(template: '/review/review', model: [reviewInstance: review], status: HttpStatus.SC_CREATED)
    }

    @Secured([Role.USER])
    def get(final Long id) {
        final Review review = Review.get(id)
        if (review) {
            User user = springSecurityService.currentUser as User
            Applicant reviewer = applicantService.findApplicantByUser(user.id)
            permissionsService.validatePackageReadAccess(user, review.aPackage)
            render(template: '/review/review', model: [reviewInstance: review], status: HttpStatus.SC_OK)
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    @Secured([Role.USER])
    def update(Long id, ReviewCommand reviewCommand) {
        Review review = Review.get(id)
        if (review) {
            User user = springSecurityService.currentUser as User
            Applicant reviewer = applicantService.findApplicantByUser(user.id)

            permissionsService.isApplicantForPackage(reviewer.profile, review.aPackage)

            reviewService.updateReview(review, reviewCommand.rating, reviewCommand.title, reviewCommand.review)
            attorneyService.calculateAttorneyReview(reviewCommand.representativeId)

            render(template: '/review/review', model: [reviewInstance: review], status: HttpStatus.SC_OK)
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }

    }

    @Secured([Role.USER])
    def reviewByPackageAndRepresentative(final Long packageId, final Long representativeId) {
        User user = springSecurityService.currentUser as User
        Applicant reviewer = applicantService.findApplicantByUser(user.id)

        final Package packageInstance = Package.get(packageId)
        final LegalRepresentative legalRepresentative = LegalRepresentative.get(representativeId)

        validatePackageAndRepresentative(packageInstance, legalRepresentative)
        permissionsService.validateApplicantForPackage(reviewer.profile, packageInstance)
        permissionsService.validateRepresentativeForPackage(packageInstance, legalRepresentative)

        final Review review = Review.findByAPackageAndRepresentative(packageInstance, legalRepresentative)
        if (review) {
            render(template: '/review/review', model: [reviewInstance: review], status: HttpStatus.SC_OK)
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    @Secured([Role.ATTORNEY])
    def reviews(FindReviewCommand findReviewCommand) {
        LegalRepresentative currentAttorney = attorneyService.findAttorneyByUser(springSecurityService.currentUserId as Long)

        List<Review> reviews = reviewService.reviews(currentAttorney, findReviewCommand) as List<Review>
        List<Review> reviewsCount = reviewService.reviews(currentAttorney, findReviewCommand, true) as List<Review>

        List mappedData = reviews.collect {
            [id    : it.id, packageId: it.aPackage.id, reviewer: it.reviewer.name, rating: it.rating, title: it.title,
             review: it.review, read: it.read, reply: it.reply, dateCreated: it.dateCreated, lastUpdated: it.lastUpdated,]
        }

        response.setIntHeader('X-total-count', reviewsCount.size())
        response.setHeader('Access-Control-Expose-Headers', 'X-total-count')

        render(mappedData as JSON)
    }

    @Secured([Role.ATTORNEY])
    def updateReadReply(Long id, ReviewCommand reviewCommand) {
        Review review = Review.get(id)
        if (review) {
            Boolean read = reviewCommand.read
            String reply = reviewCommand.reply
            if ((reply == null || reply == '') && read == null) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'review.reply.or.read.must.be.provided')
            }

            LegalRepresentative currentAttorney = attorneyService.findAttorneyByUser(springSecurityService.currentUserId as Long)

            if (review.representative.id != currentAttorney.id) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'review.attorney.not.belongs.to.review')
            }
            if (read) {
                review.read = read
            }
            if (reply) {
                review.reply = reply
            }
            reviewService.updateReview(review, review.rating, review.title, review.review)
            attorneyService.calculateAttorneyReview(currentAttorney.id)

            render(template: '/review/review', model: [reviewInstance: review], status: HttpStatus.SC_OK)
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    private static void validatePackageAndRepresentative(Package packageInstance, LegalRepresentative legalRepresentative) {
        if (packageInstance == null) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'package.not.found.with.id')
        }
        if (legalRepresentative == null) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'attorney.not.found')
        }
    }
}
