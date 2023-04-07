package com.easyvisa

import org.springframework.beans.factory.annotation.Value

class ArticleCheckJob {
    static concurrent = false

    @Value('${easyvisa.marketing.site.url:#{null}}')
    String marketingtUrl
    @Value('${easyvisa.marketing.site.authorization:#{null}}')
    String marketingApiKey

    ArticleService articleService

    void execute() {
        log.info('Job: Started article check job')
        Integer total = 0
        if (marketingtUrl && marketingApiKey) {
            total = articleService.checkArticles()
        } else {
            log.warn('Job: easyvisa.marketing.site.url and/or easyvisa.marketing.site.authorization empty. The job aborted.')
        }
        log.info("Job: Finished article check job. Changed article statuses = ${total}")
    }

}
