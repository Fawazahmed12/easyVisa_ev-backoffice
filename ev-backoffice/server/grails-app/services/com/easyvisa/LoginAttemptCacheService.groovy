package com.easyvisa

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import grails.gorm.transactions.Transactional

import java.util.concurrent.TimeUnit

@Transactional
class LoginAttemptCacheService {

    LoadingCache attempts
    private int allowedNumberOfAttempts
    def grailsApplication

    void init() {
        allowedNumberOfAttempts = grailsApplication.config.loginAttempts.allowedNumberOfAttempts
        int time = grailsApplication.config.loginAttempts.time

        attempts = CacheBuilder.newBuilder().expireAfterWrite(time, TimeUnit.MINUTES).build({ 0 } as CacheLoader)
    }

    def loginFailed(String login) {
        def numberOfAttempts = attempts.get(login)
        numberOfAttempts++

        if (numberOfAttempts >= allowedNumberOfAttempts) {
            blockUser(login)
            attempts.invalidate(login)
        } else {
            attempts.put(login, numberOfAttempts)
        }
    }

    private void blockUser(String login) {
        User user = User.findByUsername(login)
        if (user) {
            user.passwordExpired = true
            user.save(flush: true)
        }
    }
}
