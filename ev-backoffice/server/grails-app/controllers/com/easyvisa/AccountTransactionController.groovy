package com.easyvisa

import com.easyvisa.dto.AddTransactionResponseDto
import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

/**
 * API calls to manage account transactions.
 */
@GrailsCompileStatic
class AccountTransactionController implements IErrorHandler {

    MessageSource messageSource
    AccountService accountService
    PermissionsService permissionsService
    SpringSecurityService springSecurityService

    /**
     * Provides user balance. If balance zero or less then everything is ok. Positive user is required to pay.
     * @param id user id to check
     */
    @Secured(value = [Role.EMPLOYEE], httpMethod = 'GET')
    def getBalance(Long id) {
        userCheck(id)
        TaxService.EstimatedTaxes balance = accountService.getBalanceWithEstTaxes(id)
        render(balance as JSON)
    }

    /**
     * Pays current user balance if required.
     * @param id user id to pay
     * @param payBalanceCommand data to be paid
     */
    @Secured(value = [Role.EMPLOYEE], httpMethod = 'POST')
    def payBalance(Long id, PayBalanceCommand payBalanceCommand) {
        if (payBalanceCommand.validate()) {
            User user = userCheck(id, false)
            accountService.payBalance(payBalanceCommand, user)
            render(accountService.getBalanceWithEstTaxes(id) as JSON)
        } else {
            respond payBalanceCommand.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    /**
     * Returns list of an user's AccountTransactions.
     * @param id user id to get AccountTransactions
     * @param paginationCommand pagination parameters (max and offset will be used)
     */
    @Secured(value = [Role.ATTORNEY, Role.OWNER], httpMethod = 'GET')
    def getAccountTransactions(Long id, PaginationCommand paginationCommand) {
        if (!permissionsService.isBlessed() && id != springSecurityService.currentUserId) {
            throw ExceptionUtils.createAccessDeniedException('notpermitted.error')
        }
        PaginationResponseDto transactions = accountService.getAccountTransactions(id, paginationCommand)
        response.setIntHeader('X-total-count', transactions.totalCount)
        response.setHeader('Access-Control-Expose-Headers', 'X-total-count')
        render(view: '/accountTransactions/accountTransactions', model: [accountTransactionsList: transactions.result],
                status: HttpStatus.SC_OK)
    }

    /**
     * Adds an AccountTransaction to an User account.
     * @param id user id to add an AccountTransaction
     * @param accountTransactionCommand data to add an AccountTransaction
     */
    @Secured(value = [Role.OWNER], httpMethod = 'POST')
    def addAccountTransaction(Long id, AccountTransactionCommand accountTransactionCommand) {
        permissionsService.assertIsActive(springSecurityService.currentUser as User)
        if (accountTransactionCommand.validate()) {
            BigDecimal amount = accountTransactionCommand.amount
            if (amount.scale() > 2 || amount >= BigDecimal.ZERO) {
                throw new EasyVisaException(errorMessageCode: 'account.transaction.incorrect.amount')
            }
            User user = User.get(id)
            permissionsService.assertIsExist(user)
            permissionsService.assertIsActive(user)
            AddTransactionResponseDto response = accountService.addSiteTransaction(user, accountTransactionCommand)
            render(template: '/accountTransactions/addAccountTransaction',
                    model: [addTransactionResponseDto: response], status: HttpStatus.SC_OK)
        } else {
            respond accountTransactionCommand.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    private User userCheck(Long id, Boolean checkBlessed = true) {
        //asserts target user exists
        User user = User.get(id)
        permissionsService.assertIsExist(user)
        if ((checkBlessed && !permissionsService.isBlessed()) || !checkBlessed) {
            //asserts logged in user is an admin for target user
            permissionsService.assertEditAccess(user, null, Boolean.TRUE)
        }
        user
    }

}
