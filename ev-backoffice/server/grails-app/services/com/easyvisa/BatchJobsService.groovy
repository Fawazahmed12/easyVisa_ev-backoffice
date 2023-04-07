package com.easyvisa

import com.easyvisa.enums.AppConfigType
import com.easyvisa.questionnaire.util.DateUtil
import grails.compiler.GrailsCompileStatic
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.plugins.quartz.GrailsJobClassConstants
import groovy.time.TimeCategory
import groovy.transform.CompileDynamic
import org.quartz.CronScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder

@GrailsCompileStatic
@Transactional
class BatchJobsService {

    private static final String QUESTIONNAIRE_INACTIVE_NOTIFICATIONS_JOB_NAME = 'Questionnaire_Inactive_Notifications_Job'
    private static final String DOCUMENTS_INACTIVE_NOTIFICATIONS_JOB_NAME = 'Documents_Inactive_Notifications_Job'
    private static final String ATTORNEY_RANKING_JOB_NAME = 'Attorney_Ranking_Job'
    private static final String MONTHLY_PAYMENT_JOB_NAME = 'Monthly_Payment_Job'
    private static final String ARTICLE_SUBMIT_JOB_NAME = 'Article_Submit_Job'
    private static final String ARTICLE_CHECK_JOB_NAME = 'Article_Check_Job'
    private static final String BLOCKED_PACKAGE_REMINDER_JOB_NAME = 'Blocked_Package_Reminder_Job'
    private static final String PERMANENT_RESIDENCE_CARD_JOB_NAME = 'Permanent_Residence_Card_Job'
    private static final String GET_MARRIED_90_DAYS_JOB_NAME = 'Get_Married_90_Days_Job'
    private static final String REMOVAL_CONDITIONS_RESIDENCE_NAME = 'Removal_Conditions_Residence'
    private static final String CHECK_INCONSISTENT_DATA_NAME = 'Check_Inconsistent_Data'
    private static final String ARTICLE_VIEW_CHECKS_NAME = 'Article_View_Checks'
    private static final String PROSPECT_COUNTS_CHECKS_NAME = 'Prospect_Counts_Checks'
    private static final String NOT_SUPPORTED_BENEFIT_NAME = 'Not_Supported_Benefit'
    private static final String QUARANTINE_FILES_CHECK_NAME = 'Quarantine_Files_Check'
    private static final String CALCULATE_ATTORNEY_PUBLIC_MEASURES_NAME = 'Calculate_Attorney_Public_Measures'
    private static final String USER_DEVICES_CLEAN_NAME = 'User_Devices_Clean'
    private static final String PACKAGE_QUESTIONNAIRE_VERSION_UPGRADE_NAME = 'Package_Questionnaire_Version_Upgrade'

    GrailsApplication grailsApplication

    /**
     * Returns batch jobs status.
     * @return true/false - on/off
     */
    Boolean getBatchJobsStatus() {
        AppConfig.findByType(AppConfigType.BATCH_JOBS_STATUS)?.value?.toBoolean()
    }

    /**
     * Sets batch jobs active/inactive based on user choice.
     * @param command command
     * @return true/false based on batch jobs status
     */
    Boolean setBatchJobsStatus(final BatchJobsCommand command) {
        Boolean curStatus = getBatchJobsStatus()
        //if current state is the same as requested then do not stress quartz jobs.
        if (curStatus != null && command.enable == curStatus) {
            return curStatus
        }
        if (command.enable) {
            scheduleJobs(Boolean.FALSE)
            return Boolean.TRUE
        } else {
            unscheduleAllJobs()
            return Boolean.FALSE
        }
    }

    /**
     * Schedules all batch job.
     * @param hasNeo4jDataChanged - indicates if Neo4j structure has changes. If so, PG data will be rebuilt based on it.
     */
    //workaround to place cron expressions to config files
    void scheduleJobs(Boolean hasNeo4jDataChanged) {
        Date delay = getDelay()
        createAttorneyRankingJob(delay)
        createArticleCheckJob(delay)
        createArticleSubmitJob(delay)
        createMonthlyPaymentJob(delay)
        createDocumentsInactiveJob(delay)
        createQuestionnaireInactiveJob(delay)
//        createBlockedPackageReminderJob(delay)
        createPermanentResidenceCardJob(delay)
        createGetMarried90DaysJob(delay)
        createRemovalConditionsResidenceJob(delay)
        createCheckInconsistentDataJob(delay)
        createArticleCheckViewsJob(delay)
        createProspectCountsCheckJob(delay)
        createNotSupportedBenefitCategoryJob(delay)
        createQuarantineFilesCheckJob(delay)
        createCalculateAttorneyPublicMeasuresJob(delay)
        createUserDevicesCleanJob(delay)
        if(hasNeo4jDataChanged) { // Add Job to upgrade Package Questionnaire Version If Needed
            createPackageQuestionnaireVersionUpgradeJob(delay)
        }
        AppConfig batchJobsStatus = AppConfig.findByType(AppConfigType.BATCH_JOBS_STATUS) ?: new AppConfig(type: AppConfigType.BATCH_JOBS_STATUS)
        batchJobsStatus.value = Boolean.TRUE as String
        batchJobsStatus.save(failOnError: true)
    }

    /**
     * Unschedules all batch jobs.
     */
    void unscheduleAllJobs() {
        unscheduleQuestionnaireInactiveJob()
        unscheduleDocumentsInactiveJob()
        unscheduleAttorneyRankingJob()
        unscheduleMonthlyPaymentJob()
        unscheduleArticleSubmitJob()
        unscheduleArticleCheckJob()
        unscheduleBlockedPackageReminderJob()
        unschedulePermanentResidenceCardJob()
        unscheduleGetMarried90DaysJob()
        unscheduleRemovalConditionsResidenceJob()
        unscheduleCheckInconsistentDataJob()
        unscheduleArticleCheckViewsJob()
        unscheduleProspectCountsCheckJob()
        unscheduleNotSupportedBenefitCategoryJob()
        unscheduleQuarantineFilesCheckJob()
        unscheduleCalculateAttorneyPublicMeasuresJob()
        unscheduleUserDevicesCleanJob()
        unschedulePackageQuestionnaireVersionUpgradeJob()
        AppConfig batchJobsStatus = AppConfig.findByType(AppConfigType.BATCH_JOBS_STATUS) ?: new AppConfig(type: AppConfigType.BATCH_JOBS_STATUS)
        batchJobsStatus.value = Boolean.FALSE as String
        batchJobsStatus.save(failOnError: true, flush: true)
    }

    @CompileDynamic
    private Date getDelay() {
        Date delay = new Date()
        use(TimeCategory) {
            delay = delay + 30.seconds
        }
        delay
    }

    @CompileDynamic
    private void createQuestionnaireInactiveJob(Date delay) {
        QuestionnaireInactiveNotificationsJob.schedule(createJobTrigger(QUESTIONNAIRE_INACTIVE_NOTIFICATIONS_JOB_NAME, grailsApplication.config.easyvisa.job.questionnaire.inactive.cron, delay))
    }

    private boolean unscheduleQuestionnaireInactiveJob() {
        QuestionnaireInactiveNotificationsJob.unschedule(QUESTIONNAIRE_INACTIVE_NOTIFICATIONS_JOB_NAME)
    }

    @CompileDynamic
    private void createDocumentsInactiveJob(Date delay) {
        DocumentsInactiveNotificationsJob.schedule(createJobTrigger(DOCUMENTS_INACTIVE_NOTIFICATIONS_JOB_NAME, grailsApplication.config.easyvisa.job.documents.inactive.cron, delay))
    }

    private boolean unscheduleDocumentsInactiveJob() {
        DocumentsInactiveNotificationsJob.unschedule(DOCUMENTS_INACTIVE_NOTIFICATIONS_JOB_NAME)
    }

    @CompileDynamic
    private void createAttorneyRankingJob(Date delay) {
        AttorneyRankingJob.schedule(createJobTrigger(ATTORNEY_RANKING_JOB_NAME, grailsApplication.config.easyvisa.job.attorney.ranking.cron, delay))
    }

    private boolean unscheduleAttorneyRankingJob() {
        AttorneyRankingJob.unschedule(ATTORNEY_RANKING_JOB_NAME)
    }

    @CompileDynamic
    private void createMonthlyPaymentJob(Date delay) {
        MonthlyPaymentJob.schedule(createJobTrigger(MONTHLY_PAYMENT_JOB_NAME, grailsApplication.config.easyvisa.job.monthly.payment.cron, delay))
    }

    private boolean unscheduleMonthlyPaymentJob() {
        MonthlyPaymentJob.unschedule(MONTHLY_PAYMENT_JOB_NAME)
    }

    @CompileDynamic
    private void createArticleSubmitJob(Date delay) {
        ArticleSubmitJob.schedule(createJobTrigger(ARTICLE_SUBMIT_JOB_NAME, grailsApplication.config.easyvisa.job.article.submit.cron, delay))
    }

    private boolean unscheduleArticleSubmitJob() {
        ArticleSubmitJob.unschedule(ARTICLE_SUBMIT_JOB_NAME)
    }

    @CompileDynamic
    private void createArticleCheckJob(Date delay) {
        ArticleCheckJob.schedule(createJobTrigger(ARTICLE_CHECK_JOB_NAME, grailsApplication.config.easyvisa.job.article.check.cron, delay))
    }

    private boolean unscheduleArticleCheckJob() {
        ArticleCheckJob.unschedule(ARTICLE_CHECK_JOB_NAME)
    }

    @CompileDynamic
    private void createBlockedPackageReminderJob(Date delay) {
        PaymentBlockedPackagesNotificationsJob.schedule(createJobTrigger(BLOCKED_PACKAGE_REMINDER_JOB_NAME, grailsApplication.config.easyvisa.job.blocked.package.payment.cron, delay))
    }

    private boolean unscheduleBlockedPackageReminderJob() {
        PaymentBlockedPackagesNotificationsJob.unschedule(BLOCKED_PACKAGE_REMINDER_JOB_NAME)
    }

    @CompileDynamic
    private void createPermanentResidenceCardJob(Date delay) {
        PermanentResidenceCardNotificationsJob.schedule(createJobTrigger(PERMANENT_RESIDENCE_CARD_JOB_NAME, grailsApplication.config.easyvisa.job.permanent.residence.card.cron, delay))
    }

    private boolean unschedulePermanentResidenceCardJob() {
        PermanentResidenceCardNotificationsJob.unschedule(PERMANENT_RESIDENCE_CARD_JOB_NAME)
    }

    @CompileDynamic
    private void createGetMarried90DaysJob(Date delay) {
        GetMarried90DaysNotificationsJob.schedule(createJobTrigger(GET_MARRIED_90_DAYS_JOB_NAME, grailsApplication.config.easyvisa.job.get.married90.days.cron, delay))
    }

    private boolean unscheduleGetMarried90DaysJob() {
        GetMarried90DaysNotificationsJob.unschedule(GET_MARRIED_90_DAYS_JOB_NAME)
    }

    @CompileDynamic
    private void createRemovalConditionsResidenceJob(Date delay) {
        RemovalConditionsResidenceNotificationsJob.schedule(createJobTrigger(REMOVAL_CONDITIONS_RESIDENCE_NAME, grailsApplication.config.easyvisa.job.removal.conditions.residence.cron, delay))
    }

    private boolean unscheduleRemovalConditionsResidenceJob() {
        RemovalConditionsResidenceNotificationsJob.unschedule(REMOVAL_CONDITIONS_RESIDENCE_NAME)
    }

    @CompileDynamic
    private void createCheckInconsistentDataJob(Date delay) {
        CheckInconsistentDataJob.schedule(createJobTrigger(CHECK_INCONSISTENT_DATA_NAME, grailsApplication.config.easyvisa.job.check.inconsistent.data.cron, delay))
    }

    private boolean unscheduleCheckInconsistentDataJob() {
        CheckInconsistentDataJob.unschedule(CHECK_INCONSISTENT_DATA_NAME)
    }

    @CompileDynamic
    private void createArticleCheckViewsJob(Date delay) {
        ArticleCheckViewsJob.schedule(createJobTrigger(ARTICLE_VIEW_CHECKS_NAME, grailsApplication.config.easyvisa.job.article.check.views.cron, delay))
    }

    private boolean unscheduleArticleCheckViewsJob() {
        ArticleCheckViewsJob.unschedule(ARTICLE_VIEW_CHECKS_NAME)
    }

    @CompileDynamic
    private void createProspectCountsCheckJob(Date delay) {
        ProspectCountsCheckJob.schedule(createJobTrigger(PROSPECT_COUNTS_CHECKS_NAME, grailsApplication.config.easyvisa.job.prospect.counts.check.cron, delay))
    }

    private boolean unscheduleProspectCountsCheckJob() {
        ProspectCountsCheckJob.unschedule(PROSPECT_COUNTS_CHECKS_NAME)
    }

    @CompileDynamic
    private void createNotSupportedBenefitCategoryJob(Date delay) {
        NotSupportedBenefitCategoryNotificationsJob.schedule(createJobTrigger(NOT_SUPPORTED_BENEFIT_NAME, grailsApplication.config.easyvisa.job.not.supported.benefit.cron, delay))
    }

    private boolean unscheduleNotSupportedBenefitCategoryJob() {
        NotSupportedBenefitCategoryNotificationsJob.unschedule(NOT_SUPPORTED_BENEFIT_NAME)
    }

    @CompileDynamic
    private void createQuarantineFilesCheckJob(Date delay) {
        QuarantineFilesCheckJob.schedule(createJobTrigger(QUARANTINE_FILES_CHECK_NAME, grailsApplication.config.easyvisa.job.quarantine.files.check.cron, delay))
    }

    private boolean unscheduleQuarantineFilesCheckJob() {
        QuarantineFilesCheckJob.unschedule(QUARANTINE_FILES_CHECK_NAME)
    }

    @CompileDynamic
    private void createCalculateAttorneyPublicMeasuresJob(Date delay) {
        CalculateAttorneyPublicMeasuresJob.schedule(createJobTrigger(CALCULATE_ATTORNEY_PUBLIC_MEASURES_NAME, grailsApplication.config.easyvisa.job.calculate.attorney.public.measures.cron, delay))
    }

    private boolean unscheduleCalculateAttorneyPublicMeasuresJob() {
        CalculateAttorneyPublicMeasuresJob.unschedule(CALCULATE_ATTORNEY_PUBLIC_MEASURES_NAME)
    }

    @CompileDynamic
    private void createUserDevicesCleanJob(Date delay) {
        UserDevicesCleanJob.schedule(createJobTrigger(USER_DEVICES_CLEAN_NAME, grailsApplication.config.easyvisa.job.user.devices.clean.cron, delay))
    }

    private boolean unscheduleUserDevicesCleanJob() {
        UserDevicesCleanJob.unschedule(USER_DEVICES_CLEAN_NAME)
    }

    private void createPackageQuestionnaireVersionUpgradeJob(Date delay) {
        String cronExpression = DateUtil.questionnaireUpgradeCronExpression()
        PackageQuestionnaireVersionUpgradeJob.schedule(createJobTrigger(PACKAGE_QUESTIONNAIRE_VERSION_UPGRADE_NAME, cronExpression, delay))
    }

    private boolean unschedulePackageQuestionnaireVersionUpgradeJob() {
        PackageQuestionnaireVersionUpgradeJob.unschedule(PACKAGE_QUESTIONNAIRE_VERSION_UPGRADE_NAME)
    }

    private Trigger createJobTrigger(String jobName, String cronExpression, Date delay) {
        TriggerBuilder.newTrigger()
                .withIdentity(jobName, GrailsJobClassConstants.DEFAULT_TRIGGERS_GROUP)
                .startAt(delay)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build()
    }

}
