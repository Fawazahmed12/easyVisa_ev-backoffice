package com.easyvisa.login

import com.easyvisa.LoginAttemptCacheService
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent

class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    LoginAttemptCacheService loginAttemptCacheService

    @Override
    void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        loginAttemptCacheService.loginFailed(event.authentication.name)
    }

}