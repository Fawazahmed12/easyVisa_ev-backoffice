package com.easyvisa

import com.nimbusds.jose.JOSEException
import com.nimbusds.jwt.JWT
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.rest.JwtService
import grails.plugin.springsecurity.rest.token.storage.TokenNotFoundException
import grails.plugin.springsecurity.rest.token.storage.jwt.JwtTokenStorageService
import groovy.time.TimeCategory
import org.hibernate.StaleStateException
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.NonceExpiredException

import java.text.ParseException

class TokenService extends JwtTokenStorageService {

    JwtService jwtService
    UserDetailsService userDetailsService
    GrailsApplication grailsApplication
    LoginAttemptCacheService loginAttemptCacheService

    @Transactional
    void updateAuthToken(AuthenticationToken token, Date date) {
        try {
            token.lastUsed = date
            token.save()
        }
        catch (HibernateOptimisticLockingFailureException h) {
            log.debug("cant set lastUsed for token because of optimisitc lock")
        }
        catch (StaleStateException e1) {
            log.debug("cant set lastUsed for token because of stale Object")
        }
        catch (Exception e) {
            log.debug("cant set lastUsed for token")
        }
    }

    @Override
    @Transactional
    UserDetails loadUserByToken(String tokenValue) throws TokenNotFoundException {
        log.debug "Inside the load token method..."
        Date now = new Date()
        int inactiveTimeout = grailsApplication.config.easyvisa.inactiveTokenTimeout
        try {
            JWT jwt = jwtService.parse(tokenValue)

            AuthenticationToken authenticationToken = AuthenticationToken.findByTokenValue(tokenValue, [lock: true])
            if (!authenticationToken) {
                throw new BadCredentialsException("User has logged out from this token")
            }
            use(TimeCategory) {
                if (authenticationToken.lastUsed) {
                    if (now > (authenticationToken.lastUsed + (inactiveTimeout).hours)) {
                        throw new NonceExpiredException("Token ${tokenValue} has expired")
                    }
                } else {
                    throw new NonceExpiredException("Token ${tokenValue} has expired")
                }
            }

            def roles = jwt.JWTClaimsSet.getStringArrayClaim('roles')?.collect { String role -> new SimpleGrantedAuthority(role) }

            log.debug "Trying to deserialize the principal object"
            try {
                UserDetails details = JwtService.deserialize(jwt.JWTClaimsSet.getStringClaim('principal'))
                log.debug "UserDetails deserialized: ${details}"
                if (details) {
                    updateAuthToken(authenticationToken, now)
                    return details
                }
            } catch (exception) {
                log.debug(exception.message)
            }
            log.debug "Returning a org.springframework.security.core.userdetails.User instance"
            updateAuthToken(authenticationToken, now)
            return new User(jwt.JWTClaimsSet.subject, 'N/A', roles)
        } catch (ParseException pe) {
            throw new TokenNotFoundException("Token ${tokenValue} is not valid")
        } catch (JOSEException je) {
            throw new TokenNotFoundException("Token ${tokenValue} has an invalid signature")
        }
    }


    @Override
    @Transactional
    void storeToken(String token, UserDetails principal) {
        User user = User.findByUsername(principal.username)
        loginAttemptCacheService.attempts.invalidate(principal.username)
        user.lastLogin = new Date()
        user.save(flush: true, failOnError: true)
        new AuthenticationToken(tokenValue: token, username: user.username, lastUsed: new Date()).save()
    }

    @Override
    @Transactional
    void removeToken(String token) {
        AuthenticationToken authenticationToken = AuthenticationToken.findByTokenValue(token)
        if (authenticationToken) {
            authenticationToken.delete(failOnError: true)
        } else {
            throw new TokenNotFoundException("Token ${token} not found")
        }
    }
}
