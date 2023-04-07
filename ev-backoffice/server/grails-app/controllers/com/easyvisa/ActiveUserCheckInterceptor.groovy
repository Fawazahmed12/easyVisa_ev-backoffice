package com.easyvisa

import grails.config.Config
import grails.core.GrailsApplication
import grails.core.support.GrailsConfigurationAware
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.http.HttpStatus

class ActiveUserCheckInterceptor implements GrailsConfigurationAware {

    SpringSecurityService springSecurityService
    GrailsApplication grailsApplication

    int order = HIGHEST_PRECEDENCE + 100

    boolean before() {
        User user = springSecurityService.currentUser as User
        if (user?.activeMembership) {
            true
        } else {
            log.debug "Blocking Request for - ${request.requestURI} from ${request.remoteAddr} with agent - ${request.getHeader('User-Agent')}"
            response.status = HttpStatus.SC_FORBIDDEN
            render(view: '/userNotActive')
        }
    }

    @Override
    void setConfiguration(Config co) {
        def allUrlsMatcher = matchAll()
        co.urls.globalExcludes.each { String uri ->
            allUrlsMatcher.excludes(uri: uri)
        }
        allUrlsMatcher.excludes(uri: '/api/account-transactions/user/**')
                .excludes(uri: '/api/users/me**')
                .excludes(uri: '/api/users/*/payment-method/**')
                .excludes(uri: '/api/profile')
                .excludes(uri: '/api/organizations')
                .excludes(uri: '/api/organizations/*/representatives')
                .excludes(uri: '/api/taxes')
    }
}
