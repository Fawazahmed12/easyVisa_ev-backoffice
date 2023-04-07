package com.easyvisa

import grails.config.Config
import grails.core.GrailsApplication
import grails.core.support.GrailsConfigurationAware
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.http.HttpStatus

class PaidUserCheckInterceptor implements GrailsConfigurationAware {

    SpringSecurityService springSecurityService
    GrailsApplication grailsApplication

    int order = HIGHEST_PRECEDENCE + 200

    boolean before() {
        User user = springSecurityService.currentUser as User
        if (user?.paid) {
            true
        } else {
            response.status = HttpStatus.SC_PAYMENT_REQUIRED
            render(view: '/paymentRequired')
        }
    }

    @Override
    void setConfiguration(Config co) {
        def allUrlsMatcher = matchAll()
        co.urls.globalExcludes.each { String uri ->
            allUrlsMatcher.excludes(uri: uri)
        }
        allUrlsMatcher.excludes(controller: 'user')
                .excludes(uri: '/api/attorneys/complete-payment')
                .excludes(uri: '/api/account-transactions/user/**')
                .excludes(uri: '/api/organizations')
                .excludes(uri: '/api/organizations/*/representatives')
                .excludes(uri: '/api/taxes')
    }
}
