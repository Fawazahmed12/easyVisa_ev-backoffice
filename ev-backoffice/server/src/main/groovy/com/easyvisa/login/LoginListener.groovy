package com.easyvisa.login

import com.easyvisa.PackageService
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent

class LoginListener implements ApplicationListener<AuthenticationSuccessEvent> {

    PackageService packageService

    @Override
    void onApplicationEvent(AuthenticationSuccessEvent event) {
        packageService.updateApplicantPackagesLastLogin(event.authentication.principal.id)
    }
}
