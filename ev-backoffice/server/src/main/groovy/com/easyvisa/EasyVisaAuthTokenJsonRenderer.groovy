package com.easyvisa

import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.rendering.AccessTokenJsonRenderer
import groovy.json.JsonBuilder
import org.springframework.security.core.userdetails.UserDetails

class EasyVisaAuthTokenJsonRenderer implements AccessTokenJsonRenderer {

    @Override
    String generateJson(AccessToken accessToken) {
        UserDetails userDetails = accessToken.principal
        User user
        User.withNewTransaction {
            user = User.findByUsername(userDetails.username)
            Map responseJson = ['roles'        : userDetails.authorities*.authority,
                                'id'           : user.id,
                                'access_token' : accessToken.accessToken]
            new JsonBuilder(responseJson).toPrettyString()
        }

    }
}
