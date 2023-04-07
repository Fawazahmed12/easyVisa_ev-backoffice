package com.easyvisa

import com.easyvisa.enums.AppConfigType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ProspectType
import com.easyvisa.questionnaire.util.DateUtil

import static groovyx.net.http.ContentTypes.JSON

import com.easyvisa.enums.ArticleStatus
import com.easyvisa.utils.StringUtils
import grails.gorm.transactions.Transactional
import groovyx.net.http.ApacheHttpBuilder
import groovyx.net.http.HttpBuilder
import org.springframework.beans.factory.annotation.Value

class ArticleService {

    @Value('${easyvisa.marketing.site.url}')
    String marketingtUrl
    @Value('${easyvisa.marketing.site.authorization}')
    String marketingApiKey
    @Value('${easyvisa.marketing.site.article.path}')
    String marketingArticlePath

    AccountService accountService
    AlertService alertService
    AdminService adminService
    AttorneyService attorneyService

    @Transactional
    Article createArticle(LegalRepresentative representative, ArticleCommand articleCommand) {
        Article article = new Article()
        String articleTextWithoutHTML = StringUtils.stripHtmlTags(articleCommand.content)
        article.with {
            body = articleCommand.content
            title = articleCommand.title
            author = representative
            categoryId = articleCommand.locationId
            categoryName = articleCommand.locationName
            organization = articleCommand.organization
            wordsCount = articleTextWithoutHTML.split(" ").size()
            status = ArticleStatus.SUBMITTED
        }
        article.save(failOnError: true)
    }

    /**
     * This method finds articles of All or One Attorney with or without organization filter
     * @param findArticleCommand This is the instance of FindArticleCommand which has query params
     * @return List < Article >  This returns matched Article List
     */
    @Transactional
    def getAttorneyArticles(FindArticleCommand findArticleCommand) {
        Article.createCriteria().list(findArticleCommand.paginationParams) {
            if (findArticleCommand.representative) {
                eq('author', findArticleCommand.representative)
            }

            if (findArticleCommand.organization) {
                eq('organization', findArticleCommand.organization)
            }
            order(findArticleCommand.sortFieldName, findArticleCommand.getSortOrder())
        }
    }

    /**
     * Checks article views on Drupal.
     */
    @Transactional
    void checkArticleViews() {
        HttpBuilder client = configureHttpClient(false)
        log.info("Calling Drupal to get article views count")
        Object response = client.get {
            request.uri.path = '/api/article-count'
        }
        List result = response as List
        log.info("${result.size()} articles in the response")
        result.each {
            Long article_id = it['field_article_ev_id'] as Long
            log.info("Checking [${article_id}] article")
            Article article = Article.findById(article_id)
            if (article) {
                article.views = it['totalcount'] as Long
                article.save(failOnError: true)
            } else {
                log.warn("Article with id [${article}] is not found in the system")
            }
        }
    }

    /**
     * Submits an Article to Drupal. The article status and dateSubmitted fields will be changed.
     * @param articleId id of article to submit
     */
    @Transactional
    void submitArticle(Long articleId) {
        HttpBuilder client = configureHttpClient()
        log.info("Calling Drupal to sumbit [${articleId}] article")
        Article article = Article.get(articleId)
        client.post {
            request.uri.path = '/api/node/create'
            request.body = prepareArticleSubmitBody(article)
            request.uri.query = [_format: 'json']
        }
        log.info("[${article.id}] article submitted to Drupal")
        Date date = new Date()
        article.dateSubmitted = date
        article.status = ArticleStatus.UNDER_REVIEW
        article.save(failOnError: true)
    }

    /**
     * Checks Article statuses in Drupal.
     */
    @Transactional
    Integer checkArticles() {
        Integer result = 0
        log.info("Getting article statutes from Drupal")
        Object response = callDrupalForCheck(AppConfigType.DRUPAL_LAST_ARTICLE_CHECK, '/api/publish_status')
        log.info("Parsing article statutes response from Drupal")
        if (response in List) {
            Set<Long> attorneys = []
            response.each {
                try {
                    Long id = it['article_ev_id'] as Long
                    Article article = Article.get(id)
                    if (article) {
                        EasyVisaSystemMessageType type = EasyVisaSystemMessageType.ARTICLE_APPROVED
                        if (it['moderation_state'] == 'approved' && article.status != ArticleStatus.APPROVED) {
                            article.status = ArticleStatus.APPROVED
                            article.dispositioned = DateUtil.drupalDate(it['status_updated_date'])
                            article.isApproved = Boolean.TRUE
                            article.url = "${marketingtUrl}${marketingArticlePath}/${it['article_id']}"
                            accountService.addArticleBonus(article)
                        } else if (it['moderation_state'] == 'rejected' && article.status != ArticleStatus.REJECTED) {
                            article.status = ArticleStatus.REJECTED
                            article.rejectedMessage = it['rejection_message']
                            article.isApproved = Boolean.FALSE
                            type = EasyVisaSystemMessageType.ARTICLE_REJECTED
                        }
                        article.save(failOnError: true, flush: true)
                        attorneys << article.author.id
                        String subject = String.format(type.subject, article.title)
                        BigDecimal discount = adminService.adminSettings.adminConfig.articleBonus
                        String content = alertService.renderTemplate(type.templatePath,
                                [article: article, credit: discount])
                        alertService.createAlert(type, article.author.user, EvSystemMessage.EASYVISA_SOURCE, content,
                                subject)
                        result++
                    } else {
                        log.warn("Article with id [${id}] not found in the system")
                    }
                } catch (Exception e) {
                    log.warn("Can't process article from Drupal response $it", e)
                }
            }
            attorneyService.calculateAttorneyArticles(attorneys)
        }
        result
    }

    Object callDrupalForCheck(AppConfigType appConfigType, String path, boolean auth = true) {
        HttpBuilder client = configureHttpClient(auth)
        AppConfig lastCall = AppConfig.findByType(appConfigType)
        lastCall = lastCall ?: new AppConfig(type: appConfigType, value: '1')
        Long millis = Calendar.getInstance(TimeZone.getTimeZone('UTC')).timeInMillis / 1000
        Object response = client.get {
            request.uri.path = path
            request.uri.query = [from: lastCall.value as Long, to: millis]
        }
        lastCall.value = millis as String
        lastCall.save(failOnError: true)
        response
    }

    private HttpBuilder configureHttpClient(boolean addAuth = true) {
        ApacheHttpBuilder.configure {
            request.uri = marketingtUrl
            if (addAuth) {
                request.headers.put('Authorization', marketingApiKey)
            }
            request.contentType = JSON[0]
            request.accept = [JSON[0]]
        }
    }

    private Object prepareArticleSubmitBody(Article article) {
        List articlesMap = []
        articlesMap << ['type'               : 'article',
                        'title'              : article.title,
                        'body'               : article.body,
                        'field_category'     : article.categoryId,
                        'field_app_author_id': article.author.id,
                        'article_ev_id'      : article.id,]
        ['data': articlesMap]
    }

}
