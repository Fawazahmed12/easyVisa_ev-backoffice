package com.easyvisa

import com.easyvisa.enums.TaxItemType
import com.easyvisa.utils.ExceptionUtils
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

@Secured([Role.EMPLOYEE])
@CompileStatic
class TaxController implements IErrorHandler {

    MessageSource messageSource
    TaxService taxService
    AccountService accountService
    SpringSecurityService springSecurityService

    @Secured([Role.EMPLOYEE])
    def estimate(TaxEstimationCommand command) {
        if (command.validate()) {
            User user = springSecurityService.getCurrentUser() as User
            BigDecimal credit = accountService.getBalance(user.id)
            if (credit > BigDecimal.ZERO) {
                credit = BigDecimal.ZERO
            }

            Map result = [:]
            TaxService.EstimatedTaxes tax
            if (command.packageObj) {
                if (TaxItemType.IMMIGRATION_BENEFIT != command.type) {
                    throw ExceptionUtils.createUnProcessableDataException('wrong.estimation.item', null, [command.type])
                }
                TaxService.DetailedEstimatedTaxes detailedTax = taxService.estimatePackageUpdateTax(command, user)
                result.putAll([immediateCharge:detailedTax.immediate, laterCharge:detailedTax.later])
                tax = detailedTax
            } else {
                tax = taxService.estimateTax(command, user)
            }
            result.putAll([subTotal:tax.subTotal, estTax:tax.estTax, grandTotal:tax.grandTotal, credit:credit])
            render(result as JSON)
        } else {
            respond command.errors, [status:HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

}
