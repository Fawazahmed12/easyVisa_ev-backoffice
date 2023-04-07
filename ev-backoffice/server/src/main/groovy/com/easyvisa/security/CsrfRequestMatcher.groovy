package com.easyvisa.security

import org.springframework.security.web.util.matcher.RequestMatcher

import javax.servlet.http.HttpServletRequest
import java.util.regex.Pattern

class CsrfRequestMatcher implements RequestMatcher {

    // Always allow the HTTP GET method
    private Pattern allowedMethods = Pattern.compile(/^GET$/);

    private Pattern allowedPostMethods = Pattern.compile(/^POST$/);

    @Override
    boolean matches(HttpServletRequest request) {
        if (allowedMethods.matcher(request.getMethod()).matches()) {
            return false;
        }

        if (request.getRequestURI().startsWith('/console')) {
            return false;
        }

        if (request.getRequestURI().contains('public') &&
                allowedPostMethods.matcher(request.getMethod()).matches()) {
            return false;
        }

        return true;
    }

}
