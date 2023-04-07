package com.easyvisa

import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.RegistrationStatus
import grails.gsp.PageRenderer
import org.springframework.beans.factory.annotation.Value

class MonthlyPaymentJob {
    static concurrent = false

    protected static final int MAX_PAGE_DB_SIZE = 10000

    EvMailService evMailService
    AlertService alertService
    AdminService adminService
    AccountService accountService
    UserService userService
    AttorneyService attorneyService
    PageRenderer groovyPageRenderer

    @Value('${frontEndAppURL}')
    String frontEndAppURL

    void execute() {
        log.info('Job: Started monthly job for charging maintenance fees')
        BigDecimal monthlyFee = adminService.adminSettings.adminConfig.maintenanceFee
        BigDecimal cloudStorageFee = adminService.adminSettings.adminConfig.cloudStorageFee
        Date firstDay = firstDayOfTheMonth
        Integer offset = 0
        Integer total = 0
        Integer charged = 0
        List<Long> users = getUsers(firstDay, offset)
        //getting users in pagination way, due to there can be a lot of users in the db
        while (users) {
            total += users.size()
            users.each {
                log.info("Job: Started monthly charging for [${it}] attorney user")
                LegalRepresentative attorney = attorneyService.findAttorneyByUser(it)
                charged += applyMonthlyFee(attorney, monthlyFee, cloudStorageFee)
                log.info("Job: Finished monthly charging for [${it}] attorney user")
            }
            offset += MAX_PAGE_DB_SIZE
            users = getUsers(firstDay, offset)
        }
        log.info("Job: Finished monthly job for charging maintenance fees touched users = ${total} charged users = ${charged}")
    }

    private Integer applyMonthlyFee(LegalRepresentative attorney, BigDecimal monthlyFee, BigDecimal cloudStorageFee) {
        EasyVisaSystemMessageType messageType = EasyVisaSystemMessageType.MONTHLY_PAYMENT_FAILED_GRACE
        Integer result = 0
        try {
            //check if last month was payed, if not the attorney will be marked as unpaid if the charge is failed
            Calendar firstDay = Calendar.instance
            firstDay = firstDay.clearTime()
            firstDay.set(Calendar.DAY_OF_MONTH, 1)
            firstDay.add(Calendar.MONTH, -1)
            if ((attorney.profile.lastMonthlyPayment != null && firstDay.time > attorney.profile.lastMonthlyPayment)
                    || (attorney.profile.lastMonthlyPayment == null && attorney.profile.lastMonthlyCharge != null)) {
                messageType = EasyVisaSystemMessageType.MONTHLY_PAYMENT_FAILED
            }
            //add monthly fee
            log.info("Job: Adding monthly charges for [${attorney.id}] attorney account")
            AccountTransaction maintenance = accountService.addMaintenanceFee(monthlyFee, cloudStorageFee, attorney)
            // charging the balance if balance is positive
            log.info("Job: Charging monthly charges for [${attorney.id}] attorney")
            AccountTransaction charged = accountService.charge(attorney.user)
            log.info("Job: Updating [${attorney.id}] attorney account as paid in current month")
            userService.successMonthlyChargeActions(attorney.refresh().profile, (charged != null ? charged.date : maintenance.date))
            result = 1
            Thread.sleep(1000)
        } catch (EasyVisaException e) {
            if (messageType == EasyVisaSystemMessageType.MONTHLY_PAYMENT_FAILED) {
                //no payments for two months. The attorney will be marked as unpaid
                userService.markUserUnpaid(attorney.user)
            } 
            putAlert(attorney.user, messageType)
        }
        result
    }

    protected List<Long> getUsers(Date firstDay, Integer offset) {
        List<Long> result = []
        LegalRepresentative.withNewTransaction {
            result = LegalRepresentative.createCriteria().list {
                createAlias('profile', 'p')
                createAlias('p.user', 'u')
                projections {
                    property('u.id')
                }
                eq('registrationStatus', RegistrationStatus.COMPLETE)
                eq('u.activeMembership', true)
                or {
                    isNull("p.lastMonthlyCharge")
                    lt("p.lastMonthlyCharge", firstDay)
                }
                lt('u.dateCreated', firstDay)
                firstResult(offset)
                maxResults(MAX_PAGE_DB_SIZE)
                order('u.id')
            } as List<Long>
        }
        if (result) {
            List<Integer> evUsers
            UserRole.withTransaction {
                evUsers = UserRole.createCriteria().listDistinct {
                    createAlias('role', 'r')
                    projections {
                        property('user.id')
                    }
                    'in'('user.id', result)
                    'in'('r.authority', [Role.EV, Role.OWNER])
                } as List<Long>
            }
            if (evUsers) {
                result.removeAll(evUsers)
            }
        }
        result
    }

    protected void putAlert(User user, EasyVisaSystemMessageType messageType) {
        String body = groovyPageRenderer.render(template: messageType.templatePath,
                model: [profile: Profile.findByUser(user), url: frontEndAppURL])
        alertService.createAlert(messageType, user, EvSystemMessage.EASYVISA_SOURCE, body)
    }

    protected Date getFirstDayOfTheMonth() {
        Calendar firstDay = Calendar.instance
        firstDay.clearTime()
        firstDay.set(Calendar.DAY_OF_MONTH, 1)
        firstDay.time
    }

}
