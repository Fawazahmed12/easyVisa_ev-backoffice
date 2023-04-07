package com.easyvisa

import com.easyvisa.enums.ArticleStatus
import groovyx.net.http.HttpException
import org.springframework.beans.factory.annotation.Value

class ArticleSubmitJob {
    static concurrent = false

    private static final int MAX = 20

    @Value('${easyvisa.marketing.site.url:#{null}}')
    String marketingtUrl
    @Value('${easyvisa.marketing.site.authorization:#{null}}')
    String marketingApiKey

    ArticleService articleService

    void execute() {
        log.info('Job: Started article submit job')
        Integer total = 0
        if (marketingtUrl && marketingApiKey) {
            log.info('Job: Getting articles to be submitted to Drupal')
            List<Long> articles = findArticles()
            List<Long> processed = []
            while (articles && !processed.containsAll(articles)) {
                articles.each {
                    try {
                        if (!processed.contains(it)) {
                            log.info("Job: Submitting [${it}] to Drupal")
                            articleService.submitArticle(it)
                            total++
                        }
                    } catch (HttpException e) {
                        log.error("Job: Failed to submit article to Drupal ${e.body}", e)
                    } catch (Exception e) {
                        log.error('Job: Failed to submit article to Drupal', e)
                    }
                    processed << it
                }
                articles = findArticles()
            }
        } else {
            log.warn('Job: easyvisa.marketing.site.url and/or easyvisa.marketing.site.authorization empty. The job aborted.')
        }
        log.info("Job: Finished article submit job. Submitted articles = ${total}")
    }

    private List<Long> findArticles() {
        Article.createCriteria().list([max:MAX]) {
            projections {
                property('id')
            }
            isNull('dateSubmitted')
            eq('status', ArticleStatus.SUBMITTED)
            order('id')
        } as List<Long>
    }

}
