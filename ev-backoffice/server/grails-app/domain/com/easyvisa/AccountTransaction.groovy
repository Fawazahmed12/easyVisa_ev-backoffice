package com.easyvisa

import com.easyvisa.enums.TransactionSource
import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
@GrailsCompileStatic
class AccountTransaction {

    Profile profile
    String memo
    BigDecimal amount
    Date date = new Date()
    Profile referral
    Article article
    ImmigrationBenefit immigrationBenefit
    TransactionSource source
    String fmTransactionId
    Tax tax

    static constraints = {
        referral nullable: true
        article nullable: true
        fmTransactionId nullable: true
        immigrationBenefit nullable: true
        tax nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'account_transaction_id_seq']
    }

    BigDecimal getGrandTotal() {
        //ground total will be used to paid records (negative amount)
        //as a result we need to deduct tax in order to get grand total
        tax ? amount - tax.total : amount
    }

    /**
     * It uses to display payed amount for the end user. (source = PAYMENT)
     * @return payed value to be displayed
     */
    BigDecimal getGrandTotalPayedByUser() {
        grandTotal.negate()
    }
}
