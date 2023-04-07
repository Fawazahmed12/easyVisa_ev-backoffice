package com.easyvisa.login

import com.easyvisa.UserService
import grails.plugin.springsecurity.rest.RestTokenCreationEvent
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.context.ApplicationListener
import org.springframework.web.context.request.RequestContextHolder

class TokenCreationEventHandler implements ApplicationListener<RestTokenCreationEvent> {

    UserService userService

    void onApplicationEvent(RestTokenCreationEvent event) {
        GrailsWebRequest request = RequestContextHolder.currentRequestAttributes()
        userService.checkUserDevice(request.getHeader('User-Agent'), event.principal.id)
    }
}
