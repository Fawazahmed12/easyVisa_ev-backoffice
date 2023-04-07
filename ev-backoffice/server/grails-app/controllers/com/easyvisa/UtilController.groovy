package com.easyvisa

import grails.plugin.springsecurity.annotation.Secured
import grails.util.Environment
import org.apache.http.HttpStatus
import org.springframework.util.ResourceUtils

@Secured(['permitAll'])
class UtilController {


    @Secured(value = ['permitAll'])
    def apiDoc() {
        if (Environment.current == Environment.PRODUCTION) {
            response.status = HttpStatus.SC_NOT_FOUND
            render("Not found!")

        } else {
            File apiDocFile = ResourceUtils.getFile("classpath:apidoc/html5/index.html")
            render(text: apiDocFile.text)
        }

    }

}
