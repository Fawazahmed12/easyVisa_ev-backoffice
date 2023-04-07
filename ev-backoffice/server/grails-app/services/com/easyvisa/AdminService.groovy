package com.easyvisa

import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import grails.gorm.transactions.Transactional

@Transactional
class AdminService {

    AttorneyService attorneyService
    EvMailService evMailService

    AdminSettings getAdminSettings() {
        AdminSettings.first()
    }

    AdminSettings getAdminSettingsForUpdate() {
        AdminSettings.first([lock: true])
    }

    AdminSettings updateAdminConfig(AdminConfigCommand feeCommand) {
        AdminSettings adminSettings = this.adminSettingsForUpdate
        AdminConfig adminConfig = adminSettings.adminConfig ?: new AdminConfig()
        adminConfig.with {
            signupFee = feeCommand.signupFee
            articleBonus = feeCommand.articleBonus
            signupDiscount = feeCommand.signupDiscount
            referralBonus = feeCommand.referralBonus
            membershipReactivationFee = feeCommand.membershipReactivationFee
            cloudStorageFee = feeCommand.cloudStorageFee
            maintenanceFee = feeCommand.maintenanceFee
            contactPhone = feeCommand.contactPhone
            supportEmail = feeCommand.supportEmail
        }
        adminSettings.adminConfig = adminConfig.save(failOnError: true)
        adminSettings.save(failOnError: true)
    }

    AdminSettings updateGovernmentFee(GovFeeCommand feeCommand) {
        AdminSettings adminSettings = this.adminSettingsForUpdate
        AdminConfig adminConfig = adminSettings.adminConfig ?: new AdminConfig()
        adminConfig.with {
            i129f = feeCommand.i129f
            i130 = feeCommand.i130
            i360 = feeCommand.i360
            n400 = feeCommand.n400
            i485 = feeCommand.i485
            i485_14 = feeCommand.i485_14
            i600_600a = feeCommand.i600_600a
            i601 = feeCommand.i601
            i601a = feeCommand.i601a
            i751 = feeCommand.i751
            i765 = feeCommand.i765
            n600_n600k = feeCommand.n600_n600k
            biometricServiceFee = feeCommand.biometricServiceFee
        }
        adminSettings.adminConfig = adminConfig
        adminSettings.save(failOnError: true)
    }

    AdminSettings updateFeeSchedule(FeeSchedulesCommand feeSchedulesCommand) {
        AdminSettings adminSettings = this.adminSettingsForUpdate
        AdminConfig adminConfig = adminSettings.adminConfig
        attorneyService.updateAttorneyFeeSchedule(adminConfig.attorney, feeSchedulesCommand.feeSchedule)
        adminSettings
    }

    BigDecimal findPerApplicantFee(ImmigrationBenefitCategory category) {
        AdminSettings adminSettings = this.adminSettingsForUpdate
        adminSettings.adminConfig.attorney.feeSchedule.find { it.benefitCategory == category }.amount
    }

    void sendEvAlerts(AdminAlertCommand adminAlertCommand) {
        Set<Profile> sendTo = []
        //TODO: need to handle send to groups in a better way in order to reduce DB calls to get list of recipients,
        // adding lots of alerts, etc. For instance similar to managing email distribution list
        adminAlertCommand.sendTo.each {
            sendTo.addAll(Profile.executeQuery(it.query) as List<Profile>)
        }
        if (sendTo) {
            List<Alert> alerts = []
            sendTo.each {
                alerts << new Alert(recipient: it.user, subject: adminAlertCommand.subject, body: adminAlertCommand.body,
                        messageType: EasyVisaSystemMessageType.EASYVISA_ALERT, source: adminAlertCommand.findSourceName())
                        .save(failOnError: true)
            }
            Thread.start {
                alerts.each {
                    Alert alert = it
                    User.withNewSession {
                        evMailService.sendAlertEmail(alert)
                    }
                }
            }
        }
    }
}
