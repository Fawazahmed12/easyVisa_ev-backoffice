package com.easyvisa

import com.easyvisa.enums.DocumentMilestoneType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.NotificationType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class GetMarried90DaysNotificationsJob extends AbstractAttorneyNotifications {

    static concurrent = false

    @Autowired
    PackageDocumentService packageDocumentService

    void execute() {
        execute('Get Married within 90 days of Arrival (Fiancé Visa Only)', NotificationType.MARRIAGE_IN_90_DAYS_OF_ARRIVAL,
                EasyVisaSystemMessageType.MARRIAGE_IN_90_DAYS_OF_ARRIVAL)
    }

    @Override
    protected List<Package> findPackages(EmailTemplate emailTemplate) {
        findAttorneyPackages(emailTemplate.attorney, ImmigrationBenefitCategory.K1K3)
    }

    @Override
    protected boolean isReadyToSend(Package aPackage) {
        if (packageDocumentService.getMilestoneDate(aPackage, DocumentMilestoneType.MARRIAGE_DATE) != null) {
            return false
        }
        Date expirationDate = packageReminderService.getMarriageExpiration(aPackage)
        if (expirationDate == null) {
            return false
        }
        Date currDate = new Date().clearTime()
        return currDate <= expirationDate && currDate >= packageDocumentService.getMilestoneDate(aPackage, DocumentMilestoneType.ARRIVAL_DATE_IN_US)
    }

    @Override
    protected boolean isCheckMaxPeriod() {
        return false
    }

    @Override
    protected Integer getLastActivityDays(Package aPackage) {
        getDaysPeriod(packageDocumentService.getMilestoneDate(aPackage, DocumentMilestoneType.ARRIVAL_DATE_IN_US))
    }

    @Override
    protected Map setExtraEmailParams(Map params, Integer passedDays, Package aPackage) {
        emailVariableService.addMarriageExpiration(params, packageReminderService.getMarriageExpiration(aPackage))
    }

}
