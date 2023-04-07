package com.easyvisa

import org.springframework.beans.factory.annotation.Value

class ArticleCheckViewsJob {
    static concurrent = false

    @Value('${easyvisa.marketing.site.url:#{null}}')
    String marketingtUrl
    @Value('${easyvisa.marketing.site.authorization:#{null}}')
    String marketingApiKey

    ArticleService articleService

    void execute() {
        log.info('Job: Started article views checking job')
        if (marketingtUrl && marketingApiKey) {
            log.info('Job: Getting articles to be submitted to Drupal')
            articleService.checkArticleViews()
        } else {
            log.warn('Job: easyvisa.marketing.site.url and/or easyvisa.marketing.site.authorization empty. The job aborted.')
        }
        log.info("Job: Finished article views checking job.")
    }

}
