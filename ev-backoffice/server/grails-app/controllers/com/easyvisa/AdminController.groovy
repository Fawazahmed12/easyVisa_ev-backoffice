package com.easyvisa

import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.utils.ExceptionUtils
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

@Secured(['ROLE_EV'])
@CompileStatic
class AdminController implements IErrorHandler {

    AdminService adminService
    BatchJobsService batchJobsService
    MessageSource messageSource

    @Secured([Role.OWNER])
    def updateAdminConfig(final AdminConfigCommand feeCommand) {
        AdminSettings settings = adminService.updateAdminConfig(feeCommand)
        render(view: '/admin/feeConfig', model: [feeConfig: settings.adminConfig], status: HttpStatus.SC_OK)
    }

    @Secured([Role.OWNER])
    def updateGovFee(final GovFeeCommand feeCommand) {
        AdminSettings adminSettings = adminService.updateGovernmentFee(feeCommand)
        response.status = HttpStatus.SC_OK
        render(template: '/admin/govFeeConfig', model: [feeConfig: adminSettings.adminConfig], status: HttpStatus.SC_OK)
    }

    @Secured([Role.OWNER])
    def updateFeeSchedule(final FeeSchedulesCommand feeSchedulesCommand) {
        ImmigrationBenefitCategory.values().each { category ->
            if (!feeSchedulesCommand.feeSchedule.find { it.benefitCategory == category }) {
                throw ExceptionUtils.createUnProcessableDataException('admin.fee.schedule.category.missed', null,
                        [category])
            }
        }
        AdminSettings settings = adminService.updateFeeSchedule(feeSchedulesCommand)
        render(view:'/admin/feeSchedule', model:[feeSchedule:settings.adminConfig.attorney.feeScheduleForUI],
                status:HttpStatus.SC_OK)
    }

    @Secured([Role.OWNER])
    def sendAlerts(final AdminAlertCommand adminAlertCommand) {
        if (adminAlertCommand.validate()) {
            adminService.sendEvAlerts(adminAlertCommand)
            response.status = HttpStatus.SC_NO_CONTENT
        } else {
            respond adminAlertCommand.errors, [status:HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    @Secured([Role.OWNER])
    def getBatchJobsStatus() {
        render([enable: batchJobsService.getBatchJobsStatus()] as JSON)
    }

    @Secured([Role.OWNER])
    def setBatchJobsStatus(final BatchJobsCommand batchJobsCommand) {
        if (batchJobsCommand.validate()) {
            render([enable: batchJobsService.setBatchJobsStatus(batchJobsCommand)] as JSON)
        } else {
            respond batchJobsCommand.errors, [status:HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

}
