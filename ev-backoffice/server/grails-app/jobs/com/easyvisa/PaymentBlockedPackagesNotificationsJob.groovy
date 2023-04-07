package com.easyvisa

import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.NotificationType
import com.easyvisa.enums.PackageBlockedType
import groovy.transform.CompileStatic

@CompileStatic
class PaymentBlockedPackagesNotificationsJob extends AbstractAttorneyNotifications {

    static concurrent = false

    void execute() {
        execute('Payment (for blocked packages)', NotificationType.PAYMENT,
                EasyVisaSystemMessageType.PACKAGE_PAYMENT)
    }

    @Override
    protected List<Package> findPackages(EmailTemplate emailTemplate) {
        findAttorneyBlockedPackages(emailTemplate.attorney, PackageBlockedType.PAYMENT)
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
