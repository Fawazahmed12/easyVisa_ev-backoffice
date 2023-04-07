package com.easyvisa

import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.NotificationType
import com.easyvisa.enums.PackageBlockedType
import groovy.transform.CompileStatic

@CompileStatic
class NotSupportedBenefitCategoryNotificationsJob extends AbstractAttorneyNotifications {

    static concurrent = false

    void execute() {
        execute('Not supported benefit category (for blocked packages)', NotificationType.NOT_SUPPORTED_IMMIGRATION_PROCESS,
                EasyVisaSystemMessageType.PACKAGE_NOT_SUPPORTED_IMMIGRATION_PROCESS)
    }

    @Override
    protected List<Package> findPackages(EmailTemplate emailTemplate) {
        findAttorneyBlockedPackages(emailTemplate.attorney, PackageBlockedType.NOT_SUPPORTED_IMMIGRATION)
    }

    @Override
    protected boolean isReadyToSend(Package aPackage) {
        return true
    }

    @Override
    protected boolean isCheckMaxPeriod() {
        return false
    }

    @Override
    protected Integer getLastActivityDays(Package aPackage) {
        getDaysPeriod(aPackage.lastUpdated)
    }

    @Override
    protected Map setExtraEmailParams(Map params, Integer passedDays, Package aPackage) {
        params
    }

}
