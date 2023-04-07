package com.easyvisa.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.classic.spi.ThrowableProxy
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

/**
 *  The use of this filter to skip a log for 'Broken Pipe' issue
 *
 *  While downloading a large file, sometimes the user is either closing the browser tab or is navigating away to a different page before communication was complete.
 *  The server generates this exception because it is unable to send the remaining bytes.
 *
 *  If this happens frequently then, it will clog our logs. So need to filter out this ERROR from our logs
 *
 *
 */
class BrokenPipeFilter extends Filter<ILoggingEvent> {

    private Class<?> exceptionClass;

    BrokenPipeFilter() {
        String exceptionClassName = "org.apache.catalina.connector.ClientAbortException"
        this.exceptionClass = Class.forName(exceptionClassName);
    }

    @Override
    FilterReply decide(ILoggingEvent event) {
        final IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy == null || event.level != Level.ERROR) {
            return FilterReply.NEUTRAL;  // next filter, if any, will be invoked
        }

        if (this.hasBrokenPipeException(event) || this.hasClientAbortException(event)) {
            return FilterReply.DENY; // event will be dropped
        }

        return FilterReply.NEUTRAL; // next filter, if any, will be invoked
    }

    private Boolean hasBrokenPipeException(ILoggingEvent event) {
        String brokenPipeError = "Broken pipe";
        final IThrowableProxy throwableProxy = event.getThrowableProxy();
        Boolean result1 = throwableProxy.getMessage()?.contains(brokenPipeError)
        Boolean result2 = throwableProxy.getCause()?.message?.contains(brokenPipeError)
        Boolean result = result1 || result2
        return result
    }

    private Boolean hasClientAbortException(ILoggingEvent event) {
        final IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (!(throwableProxy instanceof ThrowableProxy)) {
            return false;
        }

        final ThrowableProxy throwableProxyImpl = (ThrowableProxy) throwableProxy;
        final Throwable throwable = throwableProxyImpl.getThrowable();
        if (this.exceptionClass.isInstance(throwable)) {
            return true;
        }
        return false;
    }
}
