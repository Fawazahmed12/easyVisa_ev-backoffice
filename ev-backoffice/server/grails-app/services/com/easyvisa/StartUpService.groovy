package com.easyvisa

import com.easyvisa.enums.AppConfigType
import com.easyvisa.questionnaire.services.QuestionRelationshipMappingService
import com.easyvisa.questionnaire.services.QuestionnaireCompletionWeightageEvaluator
import com.easyvisa.questionnaire.services.QuestionnaireTranslationService
import com.easyvisa.questionnaire.util.DateUtil
import grails.compiler.GrailsCompileStatic
import grails.core.GrailsApplication
import groovy.sql.Sql
import groovy.transform.CompileDynamic
import org.springframework.util.ResourceUtils

import javax.sql.DataSource

@GrailsCompileStatic
class StartUpService {

    DataSource dataSource
    GrailsApplication grailsApplication
    QuestionnaireCompletionWeightageEvaluator questionnaireCompletionWeightageEvaluator
    BatchJobsService batchJobsService
    QuestionnaireTranslationService questionnaireTranslationService
    Neo4jPreFetchService neo4jPreFetchService
    QuestionRelationshipMappingService questionRelationshipMappingService

    /**
     * Performs start up actions.
     * @param all - performs all actions. By default is TRUE. FALSE is used to test env and currently skips jobs scheduling.
     */
    void starUpActions(Boolean all = Boolean.TRUE) {
        log.info('Started boot checks')
        log.info('Checking properties')
        checkProperties(all)
        log.info('Checking Neo4j changes')
        File dataFile = ResourceUtils.getFile('classpath:db/neo4j_update.txt')
        String questionnaireDateString = dataFile.text
        Date questionnaireDate = DateUtil.drupalDate(questionnaireDateString)
        AppConfig lastRun = AppConfig.findByType(AppConfigType.NEO4J_LAST_UPDATE)
        Date lastRunDate = DateUtil.drupalDate(lastRun?.value)
        Boolean hasNeo4jDataChanged = (!lastRunDate || questionnaireDate > lastRunDate)
        if (hasNeo4jDataChanged) {
            log.info("Questionnaire data should be updated. Current date is ${questionnaireDateString} Previous date is ${lastRun?.value}")
            generateQuestionnaireCompletionWeightageData()
            AppConfig config = lastRun ?: new AppConfig(type: AppConfigType.NEO4J_LAST_UPDATE)
            config.value = questionnaireDateString
            config.save(failOnError: true)
        } else {
            log.info('Questionnaire is up to date')
        }
        loadQuestionnaireTranslatorMeta();
        neo4jPreFetchService.preFetchAllNeo4jData();
        questionRelationshipMappingService.preFetchAllQuestionRelationshipMapData();
        log.info('Finished boot checks')

        if (all && Boolean.FALSE != batchJobsService.getBatchJobsStatus()) {
            log.info("Scheduling jobs")
            batchJobsService.unscheduleAllJobs()
            batchJobsService.scheduleJobs(hasNeo4jDataChanged)
        }
    }

    void generateQuestionnaireCompletionWeightageData() {
        log.info("Generating Questionnaire Completion Weightage Data")
        questionnaireCompletionWeightageEvaluator.generateCompletionWeightageMetaData()
    }

    void loadQuestionnaireTranslatorMeta() {
        log.info("Generating Questionnaire Translator Meta Data")
        questionnaireTranslationService.loadQuestionnaireTranslatorMeta()
    }

    @CompileDynamic
    private void checkProperties(Boolean all) {
        //PG
        checkProperty('dataSource.username', grailsApplication.config.dataSource.username)
        checkProperty('dataSource.password', grailsApplication.config.dataSource.password)
        checkProperty('dataSource.url', grailsApplication.config.dataSource.url)
        //Neo4j
        checkProperty('spring.data.neo4j.uri', grailsApplication.config.spring.data.neo4j.uri)
        checkProperty('spring.data.neo4j.username', grailsApplication.config.spring.data.neo4j.username)
        checkProperty('spring.data.neo4j.password', grailsApplication.config.spring.data.neo4j.password)
        //Fattmerchant
        checkProperty('payment.url', grailsApplication.config.payment.url)
        checkProperty('payment.api.key', grailsApplication.config.payment.api.key)
        //Avalara
        checkProperty('avalara.accountId', grailsApplication.config.avalara.accountId)
        checkProperty('avalara.key', grailsApplication.config.avalara.key)
        checkProperty('avalara.companyCode', grailsApplication.config.avalara.companyCode)
        checkProperty('avalara.taxCode', grailsApplication.config.avalara.taxCode)
        checkProperty('avalara.url', grailsApplication.config.avalara.url)
        checkProperty('avalara.machineName', grailsApplication.config.avalara.machineName)
        checkProperty('avalara.shipFrom.street', grailsApplication.config.avalara.shipFrom.street)
        checkProperty('avalara.shipFrom.city', grailsApplication.config.avalara.shipFrom.city)
        checkProperty('avalara.shipFrom.state', grailsApplication.config.avalara.shipFrom.state)
        checkProperty('avalara.shipFrom.postalCode', grailsApplication.config.avalara.shipFrom.postalCode)
        checkProperty('easyvisa.blessedOrganizationEVId', grailsApplication.config.easyvisa.blessedOrganizationEVId, Boolean.FALSE)
        if (all) {
            //Mail
            checkProperty('grails.mail.host', grailsApplication.config.grails.mail.host)
            checkProperty('grails.mail.port', grailsApplication.config.grails.mail.port)
            checkProperty('grails.mail.username', grailsApplication.config.grails.mail.username)
            checkProperty('grails.mail.password', grailsApplication.config.grails.mail.password)
            //File storage
            checkProperty('easyvisa.uploadDirectory', grailsApplication.config.easyvisa.uploadDirectory)
            //Marketing site
            checkProperty('easyvisa.marketing.site.url', grailsApplication.config.easyvisa.marketing.site.url, Boolean.FALSE)
            checkProperty('easyvisa.marketing.site.authorization', grailsApplication.config.easyvisa.marketing.site.authorization, Boolean.FALSE)
            checkProperty('easyvisa.marketing.site.article.path', grailsApplication.config.easyvisa.marketing.site.article.path, Boolean.FALSE)
        }
    }

    private void checkProperty(String key, Object value, Boolean fail = Boolean.TRUE) {
        if (!value) {
            if (fail) {
                throw new EasyVisaException(message: "Please, set ${key} property in config file")
            }
            log.error("${key} property should be set to correct work of the applicantion. It can be fine to miss it at the first start up")
        }
    }

    private loadSQL(String sqlFilePath) {
        try {
            def sql = new Sql(dataSource)
            sql.withTransaction {
                File sqlFile = ResourceUtils.getFile(sqlFilePath)
                sql.execute(sqlFile.text)
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load  seeddata(${sqlFilePath})", e)
        }
    }
}
