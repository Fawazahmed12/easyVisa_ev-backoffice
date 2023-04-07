package com.easyvisa

import com.easyvisa.enums.EasyVisaSystemMessageType
import groovy.transform.CompileStatic
import org.apache.commons.lang3.exception.ExceptionUtils

import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Service for running tasks in async way.
 */
@CompileStatic
class AsyncService {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5)
    OrganizationService organizationService
    AlertService alertService
    EvMailService evMailService

    /**
     * Executes runnable tasks that are not expected to return any data.
     * @param command runnable task
     * @param name meaningful task name for error logs
     * @param sendEvAdmins need to send email to EV admins if command is failed. Optional. Default is false
     */
    void runAsync(Runnable command, String name, Boolean sendEvAdmins = Boolean.FALSE) {
        executorService.execute({
            try {
                log.warn("Started async operations [$name]")
                Package.withNewTransaction {
                    command.run()
                }
                log.warn("Finished async operations [$name]")
            } catch (Exception e) {
                log.error("Failed to finish [${name}] async task", e)
                sendEmailsToEvAdmins(name, sendEvAdmins, e)
            }
        })
    }

    /**
     * Executes runnable tasks that are not expected to return any data with a delay.
     * @param command runnable task
     * @param name meaningful task name for error logs
     * @param delay delay in seconds. Default is 60 seconds
     * @param sendEvAdmins need to send email to EV admins if command is failed. Optional. Default is false
     */
    void runAsyncDelayed(Runnable command, String name, Long delay = 60, Boolean sendEvAdmins = Boolean.FALSE) {
        executorService.schedule({
            try {
                log.warn("Started async operations [$name]")
                Package.withNewTransaction {
                    command.run()
                }
                log.warn("Finished async operations [$name]")
            } catch (Exception e) {
                log.error("Failed to finish [${name}] async task", e)
                sendEmailsToEvAdmins(name, sendEvAdmins, e)
            }
        }, delay, TimeUnit.SECONDS)
    }

    private void sendEmailsToEvAdmins(String name, Boolean sendEvAdmins, Exception e) {
        if (sendEvAdmins) {
            //it calls from async task
            Alert.withNewTransaction {
                List<User> admins = organizationService.blessedOrgAdminsUsers
                if (admins) {
                    String emailBody = alertService.renderTemplate('/email/internal/asyncFailed',
                            [details: name, exception: e, stacktrace: ExceptionUtils.getStackTrace(e)])
                    admins.each {
                        Alert alert = new Alert(recipient: it, subject: 'Async Operation Failed', body: emailBody,
                                messageType: EasyVisaSystemMessageType.EASYVISA_ALERT, source: EvSystemMessage.EASYVISA_SOURCE)
                                .save(failOnError: true)
                        evMailService.sendAlertEmail(alert)
                    }
                }
            }
        }
    }

}
