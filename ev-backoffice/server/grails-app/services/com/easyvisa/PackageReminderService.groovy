package com.easyvisa

import com.easyvisa.enums.DocumentMilestoneType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.NotificationType
import com.easyvisa.questionnaire.answering.rule.MilestoneReminderEvaluationContext
import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class PackageReminderService {

    PackageDocumentService packageDocumentService
    EvMailService evMailService
    EmailVariableService emailVariableService
    AlertService alertService
    AsyncService asyncService

    // called from document portal whenever any milestone date is changed
    // Instantly (when triggering date was entered)
    void sendReminderNotification(MilestoneReminderEvaluationContext ruleEvaluationContext, def notificationInfoList, Map params = [:]) {
        asyncService.runAsync({
            sendReminder(ruleEvaluationContext, notificationInfoList, params)
        }, "Send package reminder for Package [${ruleEvaluationContext.aPackage.id}]")
    }

    private void sendReminder(MilestoneReminderEvaluationContext ruleEvaluationContext, notificationInfoList, Map params) {
        ImmigrationBenefitCategory category = ruleEvaluationContext.milestoneType.reminderRuleParam as ImmigrationBenefitCategory
        Package aPackage = Package.get(ruleEvaluationContext.aPackage.id)
        if (category == aPackage.directBenefit.category) {
            notificationInfoList.each { notificationInfo ->
                NotificationType notificationType = notificationInfo['notificationType'] as NotificationType
                EmailTemplate emailTemplate = findEmailTemplate(aPackage.attorney, notificationType)
                if (emailTemplate) {
                    sendAlertAndEmailNotification(aPackage, emailTemplate, params)
                    PackageReminder packageReminder = new PackageReminder(notificationType: notificationType,
                            lastSent: new Date(), stopped: notificationInfo['oneTimeNotification'], aPackage: aPackage)
                    addPackageReminder(packageReminder)
                }
            }
        }
    }

    Integer sendAlertAndEmailNotification(Package aPackage, EmailTemplate emailTemplate, Map extraParams, EasyVisaSystemMessageType alertType = null) {
        Integer result = 0
        EasyVisaSystemMessageType alertMessageType = alertType ?: EasyVisaSystemMessageType.values().find { it.notificationType == emailTemplate.preference.type }
        Map params = evMailService.buildPackageEmailParams(aPackage)
        params.putAll(extraParams)
        aPackage.getApplicants().each { Applicant applicant ->
            if (applicant.user) {
                params = emailVariableService.addApplicant(params, applicant)
                String emailContent = evMailService.evaluateTemplate(emailTemplate.htmlContent, params)
                String subject = evMailService.evaluateTemplate(emailTemplate.subject, params)
                log.info("sending alert to [${applicant.user.id}] client from attorney [${emailTemplate.attorney.id}] of [${alertMessageType.notificationType.name()}] type to [${aPackage.id}] package")
                alertService.createAlert(alertMessageType, applicant.user, aPackage.attorney.profile.name, emailContent, subject)
                result++
            }
        }
        result
    }

    PackageReminder addPackageReminder(PackageReminder packageReminder) {
        PackageReminder savedPackageReminder = PackageReminder.findByAPackageAndNotificationType(packageReminder.aPackage, packageReminder.notificationType)
        if (savedPackageReminder) {
            savedPackageReminder.lastSent = packageReminder.lastSent
            savedPackageReminder.stopped = packageReminder.stopped
            return savedPackageReminder.save(failOnError: true)
        }
        packageReminder.save(failOnError: true)
    }

    void updatePackageReminder(Package aPackage, NotificationType notificationType, Boolean stopped) {
        PackageReminder savedPackageReminder = PackageReminder.findByAPackageAndNotificationType(aPackage, notificationType)
        if (savedPackageReminder) {
            savedPackageReminder.stopped = stopped
            savedPackageReminder.save(failOnError: true)
        }
    }

    boolean isGreenCardReminderActiveDate(Package aPackage) {
        Date date = packageDocumentService.getMilestoneDate(aPackage, DocumentMilestoneType.GREENCARD_PERMANENT_EXPIRATION)
        Integer daysBefore = getDaysPeriodBefore(date)
        daysBefore > 0 && daysBefore <= 100
    }

    Date getMarriageExpiration(Package aPackage) {
        Date date = packageDocumentService.getMilestoneDate(aPackage, DocumentMilestoneType.ARRIVAL_DATE_IN_US)
        if (date == null) {
            return null
        }
        use(TimeCategory) {
            date = date + 90.days
        }
    }

    private Integer getDaysPeriodBefore(Date futureDate) {
        if (futureDate == null) {
            return Integer.MAX_VALUE
        }
        use(TimeCategory) {
            (futureDate.clearTime() - new Date().clearTime()).days
        }
    }

    private EmailTemplate findEmailTemplate(LegalRepresentative attorney, NotificationType notificationType) {
        EmailTemplate.createCriteria().get {
            eq('attorney', attorney)
            preference {
                eq('type', notificationType)
                eq('preference', Boolean.TRUE)
            }
        } as EmailTemplate
    }

}
