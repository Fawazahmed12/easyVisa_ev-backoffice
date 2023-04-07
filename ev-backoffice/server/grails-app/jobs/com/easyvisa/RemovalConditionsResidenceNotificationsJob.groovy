package com.easyvisa

import com.easyvisa.enums.DocumentMilestoneType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.NotificationType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class RemovalConditionsResidenceNotificationsJob extends AbstractAttorneyNotifications {

    static concurrent = false

    @Autowired
    PackageDocumentService packageDocumentService

    void execute() {
        execute('Removal of Conditions on Residence', NotificationType.REMOVAL_CONDITIONS_RESIDENCE,
                EasyVisaSystemMessageType.REMOVAL_CONDITIONS_RESIDENCE)
    }

    @Override
    protected List<Package> findPackages(EmailTemplate emailTemplate) {
        findAttorneyPackages(emailTemplate.attorney, ImmigrationBenefitCategory.REMOVECOND)
    }

    @Override
    protected boolean isReadyToSend(Package aPackage) {
        return packageReminderService.isGreenCardReminderActiveDate(aPackage)
    }

    @Override
    protected boolean isCheckMaxPeriod() {
        return false
    }

    @Override
    protected Integer getLastActivityDays(Package aPackage) {
        Date date = packageDocumentService.getMilestoneDate(aPackage, DocumentMilestoneType.GREENCARD_PERMANENT_EXPIRATION)
        date = subtractDays(date, -100)
        getDaysPeriod(date)
    }

    @Override
    protected Map setExtraEmailParams(Map params, Integer passedDays, Package aPackage) {
        params
    }

}
