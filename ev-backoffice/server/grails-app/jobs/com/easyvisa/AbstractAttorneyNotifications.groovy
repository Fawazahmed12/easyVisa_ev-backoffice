package com.easyvisa

import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.NotificationType
import com.easyvisa.enums.PackageBlockedType
import com.easyvisa.enums.PackageStatus
import grails.compiler.GrailsCompileStatic
import groovy.time.TimeCategory
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j

@Slf4j
@GrailsCompileStatic
abstract class AbstractAttorneyNotifications {

    protected static final int MAX_PAGE_DB_SIZE = 100
    protected static final int MAX_PERIOD = 120

    EvMailService evMailService
    AlertService alertService
    EmailVariableService emailVariableService
    PackageReminderService packageReminderService

    /**
     * Common actions for each job.
     * @param name job friendly name
     * @param type notification type
     * @param alertType alert type
     */
    void execute(String name, NotificationType type, EasyVisaSystemMessageType alertType) {
        log.info("Job: Started nightly job (${name}) for sending attorneys notifications to clients")
        Long sentEmails = checkNotifications(type, alertType)
        log.info("Job: Finished nightly job (${name}) for sending attorneys notifications to clients. " +
                "Sent emails = ${sentEmails}")
    }

    /**
     * Returns packages for that should be checked for a particular notification and attorney.
     * @param emailTemplate email template type
     * @return packages
     */
    protected abstract List<Package> findPackages(EmailTemplate emailTemplate)

    /**
     * Checks specific package state readiness, e.g. questionnaire is not finished
     * @param aPackage package
     * @return true if reminder is expected to be sent, otherwise false
     */
    protected abstract boolean isReadyToSend(Package aPackage)

    /**
     * Is it expected to stop sending reminder after some period of time, e.g. in 120 days
     * @return true if we need to stop sending reminder in the future, otherwise not
     */
    protected abstract boolean isCheckMaxPeriod()

    /**
     * Returns last activities for a package of a particular reminder, e.g. days since last answer was added.
     * It uses to calculate past days since the reminder was triggered.
     * @param aPackage package
     * @return Integer
     */
    protected abstract Integer getLastActivityDays(Package aPackage)

    /**
     * Add extra parameters to email evaluation.
     * @param params map with parameters
     * @param passedDays passed days since notification was triggered
     * @param aPackage package
     * @return updated map with parameters
     */
    protected abstract Map setExtraEmailParams(Map params, Integer passedDays, Package aPackage)

    /**
     * Collects all configured reminders.
     * @param type notification type
     * @param alertType alert type
     * @return count of send reminders
     */
    private Long checkNotifications(NotificationType type, EasyVisaSystemMessageType alertType) {
        Long offset = 0
        Long sentEmails = 0
        List<EmailTemplate> templates = findActiveTemplate(type, offset)
        while (templates) {
            templates.each {
                try {
                    sentEmails += checkPackages(it, alertType)
                } catch (Exception e) {
                    log.error("Error to check/send $type for template $it.id", e)
                }
            }
            offset += MAX_PAGE_DB_SIZE
            templates = findActiveTemplate(type, offset)
        }
        sentEmails
    }

    /**
     * Checks/sends packages reminders for a particular reminder.
     * @param emailTemplate reminder
     * @param alertType alert type
     * @return count of sent reminders
     */
    private Long checkPackages(EmailTemplate emailTemplate, EasyVisaSystemMessageType alertType) {
        Long sentEmails = 0
        List<Package> packages = findPackages(emailTemplate)
        packages.each {
            Package aPackage = it
            log.info("Job: Started checking [${it.id}] package to send [${alertType.notificationType}]] reminder")
            if (isReadyToSend(it)) {
                Integer passedDays = getLastActivityDays(it)
                NotificationType notificationType = alertType.notificationType
                PackageReminder reminder = it.getPackageReminder(notificationType) ?: new PackageReminder(notificationType:notificationType, aPackage:it)
                Integer passedReminderDays = getDaysPeriod(reminder.lastSent)
                Integer interval = emailTemplate.preference.repeatInterval
                Integer nearestInterval = passedDays > passedReminderDays ? passedReminderDays : passedDays
                if (!reminder.stopped
                        && ((isCheckMaxPeriod() && passedDays < MAX_PERIOD) || !isCheckMaxPeriod())
                        && ((nearestInterval && nearestInterval % interval == 0) || (nearestInterval > interval))) {
                    log.info("Job: Sending [${alertType.notificationType}]] reminder to [${it.id}] package")
                    sentEmails += packageReminderService
                            .sendAlertAndEmailNotification(aPackage, emailTemplate, setExtraEmailParams([:], passedDays, aPackage), alertType)
                    reminder.lastSent = new Date()
                    packageReminderService.addPackageReminder(reminder)
                }
            }
            log.info("Job: Finished checking [${it.id}] package to send [${alertType.notificationType}]] reminder")
        }
        sentEmails
    }

    @CompileDynamic
    private List<EmailTemplate> findActiveTemplate(NotificationType type, Long offset) {
        EmailTemplate.createCriteria().list(max:MAX_PAGE_DB_SIZE, offset:offset) {
            preference {
                eq('type', type)
                eq('preference', Boolean.TRUE)
            }
            order('id', 'asc')
        } as List<EmailTemplate>
    }

    protected List<Package> findAttorneyPackages(LegalRepresentative attorney, PackageStatus status = PackageStatus.OPEN) {
        Package.findAllByAttorneyAndStatus(attorney, status)
    }

    protected List<Package> findAttorneyBlockedPackages(LegalRepresentative attorney, PackageBlockedType blockedType) {
        Package.findAllByAttorneyAndStatusAndBlockedType(attorney, PackageStatus.BLOCKED, blockedType)
    }

    @CompileDynamic
    protected List<Package> findAttorneyPackages(LegalRepresentative attorney, ImmigrationBenefitCategory category,
                                                 PackageStatus status = PackageStatus.OPEN) {
        Package.createCriteria().list {
            eq('status', status)
            eq('attorney', attorney)
            benefits{
                eq('category', category)
            }
        } as  List<Package>
    }

    @CompileDynamic
    protected Integer getDaysPeriod(Date lastDate) {
        if (lastDate == null) {
            return Integer.MAX_VALUE
        }
        use(TimeCategory) {
            (new Date().clearTime() - lastDate.clearTime()).days
        }
    }

    @CompileDynamic
    protected Integer getDaysPeriodBefore(Date futureDate) {
        if (futureDate == null) {
            return Integer.MAX_VALUE
        }
        use(TimeCategory) {
            (futureDate.clearTime() - new Date().clearTime()).days
        }
    }

    @CompileDynamic
    protected Date subtractDays(Date date, Integer days) {
        use(TimeCategory) {
            date = date + days.days
        }
    }

}
