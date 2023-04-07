package com.easyvisa

import com.easyvisa.enums.DocumentMilestoneType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.NotificationType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class PermanentResidenceCardNotificationsJob extends AbstractAttorneyNotifications {

    static concurrent = false

    @Autowired
    PackageDocumentService packageDocumentService

    void execute() {
        execute('Apply for Permanent Residence Card (K Visa Applicants Only)', NotificationType.PERMANENT_RESIDENCE_CARD,
                EasyVisaSystemMessageType.PERMANENT_RESIDENCE_CARD)
    }

    @Override
    protected List<Package> findPackages(EmailTemplate emailTemplate) {
        findAttorneyPackages(emailTemplate.attorney, ImmigrationBenefitCategory.K1K3)
    }

    @Override
    protected boolean isReadyToSend(Package aPackage) {
        return packageDocumentService.getMilestoneDate(aPackage, DocumentMilestoneType.MARRIAGE_DATE) != null
    }

    @Override
    protected boolean isCheckMaxPeriod() {
        return false
    }

    @Override
    protected Integer getLastActivityDays(Package aPackage) {
        getDaysPeriod(packageDocumentService.getMilestoneDate(aPackage, DocumentMilestoneType.MARRIAGE_DATE))
    }

    @Override
    protected Map setExtraEmailParams(Map params, Integer passedDays, Package aPackage) {
        params
    }

}
