package com.easyvisa.test.jobs

import com.easyvisa.*
import com.easyvisa.enums.ArticleStatus
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification

@Integration
class ArticleSubmitJobSpec extends Specification {

    @Autowired
    private ArticleService articleService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private OrganizationService organizationService
    @Autowired
    private ProfileService profileService

    @Value('${easyvisa.marketing.site.url:#{null}}')
    String marketingtUrl
    @Value('${easyvisa.marketing.site.authorization:#{null}}')
    String marketingApiKey

    private ArticleSubmitJob articleJob

    void setup() {
        articleJob = new ArticleSubmitJob()
        articleJob.articleService = articleService
        articleJob.marketingtUrl = marketingtUrl
        articleJob.marketingApiKey = marketingApiKey
    }

    void testArticleSubmitJob() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService:attorneyService,
                                                                 organizationService:organizationService,
                                                                 profileService : profileService])
        Date curDate = new Date()
        testHelper.buildPackageLegalRep()
                .buildPackageLegalRepArticleBonus(null)

        expect:
        runJob()

        Article article
        Article.withNewTransaction {
            article = Article.findByAuthor(testHelper.packageLegalRepresentative.refresh())
        }

        if (marketingtUrl && marketingApiKey) {
            assert article.dateSubmitted
            assert curDate < article.dateSubmitted
            assert ArticleStatus.UNDER_REVIEW == article.status
        } else {
            assert !article.dateSubmitted
            assert ArticleStatus.SUBMITTED == article.status
        }

        cleanup:
        testHelper.deletePackageLegalRep()
                .deleteOrganization()
    }

    private void runJob() {
        AccountTransaction.withNewTransaction {
            articleJob.execute()
            Boolean.TRUE
        }
    }

}
