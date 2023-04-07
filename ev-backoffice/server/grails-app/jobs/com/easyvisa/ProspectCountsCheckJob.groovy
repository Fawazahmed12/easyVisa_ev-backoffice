package com.easyvisa

import org.springframework.beans.factory.annotation.Value

class ProspectCountsCheckJob {
    static concurrent = false

    @Value('${easyvisa.marketing.site.url:#{null}}')
    String marketingtUrl
    @Value('${easyvisa.marketing.site.authorization:#{null}}')
    String marketingApiKey

    AttorneyService attorneyService

    void execute() {
        log.info('Job: Started prospect counts check job')
        if (marketingtUrl && marketingApiKey) {
            attorneyService.checkProspectCounts()
        } else {
            log.warn('Job: easyvisa.marketing.site.url and/or easyvisa.marketing.site.authorization empty. The job aborted.')
        }
        log.info("Job: Finished prospect counts.")
    }

}
