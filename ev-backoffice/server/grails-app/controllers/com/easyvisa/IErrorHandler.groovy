package com.easyvisa

import com.easyvisa.enums.ErrorMessageType
import grails.converters.JSON
import grails.validation.ValidationException
import org.apache.http.HttpStatus
import org.grails.web.servlet.mvc.exceptions.ControllerExecutionException

trait IErrorHandler {

    void renderError(int errorCode, String messageCode, List params = [], ErrorMessageType type = null) {
        response.status = errorCode
        render(['errors': [['code': errorCode,
                            'message': messageSource.getMessage(messageCode, params as Object[], request.locale),
                            'type': type?.name()]]] as JSON)
    }

    /***
     * This is not unused, called dynamically by Grails controllers which implement
     * this trait
     * @param exception
     * @return
     */
    def easyVisaException(final EasyVisaException exception) {
        exception.printStackTrace()
        List params = exception.params
        if (exception.errorSubMessageCode) {
            params = [messageSource.getMessage(exception.errorSubMessageCode, exception.subParams as Object [], request.locale)]
        }
        renderError(exception.errorCode, exception.errorMessageCode, params, exception.errorMessageType)
    }

    /***
     * This is not unused, called dynamically by Grails controllers which implement
     * this trait
     * @param exception
     * @return
     */
    def validationException(final ValidationException exception) {
        exception.printStackTrace()
        respond exception.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
    }

    /**
     *  While downloading a large file, sometimes the user is either closing the browser tab or is navigating away to a different page
     *  before communication was complete.
     *  The server generates this exception because it is unable to send the remaining bytes.
     *
     *  If this happens frequently then, it will clog our logs. So need to skip out this ERROR from our logs
     */
    def handleFileDownloadError(final ControllerExecutionException cee) {
        String brokenPipeError = "Broken pipe";
        // ignore the exception if it happens for Broken Pipe
        if(cee.getMessage()?.contains(brokenPipeError)) {
            log.warn("Broken pipe Error when retrieving a file - User Cancelled File Download")
        } else {
            throw cee
        }
    }

}
