package com.easyvisa

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus

class ArticleController implements IErrorHandler {

    def messageSource
    def articleService
    def attorneyService
    SpringSecurityService springSecurityService

    @Secured([Role.ATTORNEY])
    def create(ArticleCommand articleCommand) {
        LegalRepresentative representative = attorneyService.findAttorneyByUser(springSecurityService.currentUserId as Long)
        Article article = articleService.createArticle(representative, articleCommand)
        render(template: '/article/article', model: [article: article], status: HttpStatus.SC_CREATED)
    }

}
