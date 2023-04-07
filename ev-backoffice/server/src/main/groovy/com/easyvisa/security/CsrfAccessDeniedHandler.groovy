package com.easyvisa.security

import grails.converters.JSON
import grails.web.mvc.FlashScope
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.context.MessageSource
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.web.context.request.RequestContextHolder

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class CsrfAccessDeniedHandler implements AccessDeniedHandler {

    MessageSource messageSource

    @Override
    void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException, ServletException {

        String requestedWithHeader = request.getHeader("X-Requested-With");
        String referrer = request.getHeader("referer");
        if(requestedWithHeader == null || !requestedWithHeader.equals("XMLHttpRequest")) {
            if(referrer) {
                /*
                Don't redirect. As it's not recoverable.
                Send the error code, So that UI can show appropriate message.
                 */
                log.warn("Csrf handler denied authorization X-Requested-With: $requestedWithHeader, referer: $referrer")
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response << (['errors': [['code': HttpServletResponse.SC_UNAUTHORIZED, 'message': messageSource.getMessage('login.token.expired', null, request.locale)]]] as JSON).toString(true)

            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        exc.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    exc.getMessage());
        }
    }
}
