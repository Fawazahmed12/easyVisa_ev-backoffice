package com.easyvisa.login

import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.www.NonceExpiredException

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    Integer statusCode = HttpServletResponse.SC_FORBIDDEN
    MessageSource messageSource

    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.contentType = 'application/json'
        response.characterEncoding = 'UTF-8'

        if (exception instanceof BadCredentialsException) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response << (['errors': [['code': HttpServletResponse.SC_UNAUTHORIZED, 'message': messageSource.getMessage('login.bad.credentials', null, request.locale)]]] as JSON).toString(true)
        } else if (exception instanceof CredentialsExpiredException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response << (['errors': [['code': HttpServletResponse.SC_FORBIDDEN, 'message': messageSource.getMessage('login.password.expired', null, request.locale)]]] as JSON).toString(true)
        } else if (exception instanceof LockedException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response << (['errors': [['code': HttpServletResponse.SC_FORBIDDEN, 'message': messageSource.getMessage('email.not.verified', null, request.locale)]]] as JSON).toString(true)
        } else if (exception instanceof NonceExpiredException) {
            log.warn("RestAuthenticationFailureHandler denied", exception)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response << (['errors': [['code': HttpServletResponse.SC_UNAUTHORIZED, 'message': messageSource.getMessage('login.token.expired', null, request.locale)]]] as JSON).toString(true)
        } else {
            response.setStatus(statusCode)
        }
    }
}
