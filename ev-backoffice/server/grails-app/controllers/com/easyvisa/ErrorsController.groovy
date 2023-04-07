package com.easyvisa


import grails.plugin.springsecurity.annotation.Secured
import grails.util.Environment

@Secured(['ROLE_EV'])
class ErrorsController {

    def serverError() {
        Exception exception = request.exception
        if (Environment.current == Environment.PRODUCTION) {
            render(view: '/error')
        } else {
            render(view: '/errorDev', model: [exception: exception])

        }
    }
}

