package com.easyvisa

import com.easyvisa.enums.Country
import com.easyvisa.enums.TaxItemType
import com.easyvisa.utils.ExceptionUtils
import net.avalara.avatax.rest.client.AvaTaxClient
import net.avalara.avatax.rest.client.TransactionBuilder
import net.avalara.avatax.rest.client.enums.DocumentType
import net.avalara.avatax.rest.client.enums.TextCase
import net.avalara.avatax.rest.client.enums.TransactionAddressType
import net.avalara.avatax.rest.client.models.AddressResolutionModel
import net.avalara.avatax.rest.client.models.AddressValidationInfo
import net.avalara.avatax.rest.client.models.CommitTransactionModel
import net.avalara.avatax.rest.client.models.TransactionModel
import org.springframework.beans.factory.annotation.Value

import java.text.MessageFormat

class AvaTaxService {

    private static final String UNKNOWN_ADDRESS_TYPE = 'UnknownAddressType'

    @Value('${avalara.accountId}')
    String accountId
    @Value('${avalara.key}')
    String key
    @Value('${avalara.machineName}')
    String machineName
    @Value('${avalara.url}')
    String url
    @Value('${avalara.companyCode}')
    String companyCode
    @Value('${avalara.taxCode}')
    String taxCode
    @Value('${avalara.shipFrom.street}')
    String shipFromLine1
    @Value('${avalara.shipFrom.city}')
    String shipFromCity
    @Value('${avalara.shipFrom.state}')
    String shipFromState
    @Value('${avalara.shipFrom.postalCode}')
    String shipFromPostalCode
    @Value('${info.app.version}')
    String appVersion
    @Value('${info.app.name}')
    String appName

    /**
     * Requests AvaTax for getting estimated tax.
     * @param address user's billing address
     * @param customerId user's id or org's id
     * @param purchaseId payment account transaction id
     * @param items lst of items to be calculates
     * @param transactionDate payment account transaction date
     * @param discount user's discount
     * @return estimated taxes
     */
    BigDecimal getEstimatedTax(Address address, String customerId, String purchaseId, List<TaxItem> items, Date transactionDate = new Date(), BigDecimal discount = null) {
        if (!items || items.size() != 0) {
            return BigDecimal.ZERO
        }
        TransactionModel result = createAvaTaxTransaction(DocumentType.SalesOrder, customerId, address, items, transactionDate, purchaseId, discount)
        result.totalTaxCalculated
    }

    /**
     * Creates AvaTax transaction. That will be user for getting/collecting/reporting taxes on Avalara side.
     * The Transaction won't be committed.
     * @param address user's billing address
     * @param customerId user's id or org's id
     * @param purchaseId payment account transaction id
     * @param items lst of items to be calculates
     * @param transactionDate payment account transaction date
     * @param discount user's discount
     * @return tax details
     */
    Tax createTransaction(Address address, String customerId, String purchaseId, List<TaxItem> items, Date transactionDate = new Date(), BigDecimal discount = null) {
        TransactionModel result = createAvaTaxTransaction(DocumentType.SalesInvoice, customerId, address, items, transactionDate, purchaseId, discount)
        new Tax(total: result.totalTaxCalculated, avaTaxId:result.code, billingAddress:address)
    }

    /**
     * Commits existing transaction.
     * @param avaTaxId AvaTax transaction code.
     */
    void commitTransaction(String avaTaxId) {
        CommitTransactionModel commitModel = new CommitTransactionModel(commit:Boolean.TRUE)
        try {
            client.commitTransaction(companyCode, avaTaxId, DocumentType.SalesInvoice, null, commitModel)
       } catch (Exception e) {
            log.warn('AvaTax request failed', e)
            throw ExceptionUtils.createUnProcessableDataException('avalara.bad.request', null, [e.message])
        }
    }

    private TransactionModel createAvaTaxTransaction(DocumentType type, String customerId, Address address,
                                                     List<TaxItem> items, Date transactionDate, String purchaseId,
                                                     BigDecimal discount = null) {
        if (!items || items.isEmpty()) {
            throw ExceptionUtils.createUnProcessableDataException('avalara.no.items')
        }
        try {
            TransactionBuilder builder = new TransactionBuilder(client, companyCode, type, customerId)
                    .withAddress(TransactionAddressType.ShipFrom, shipFromLine1, null, null, shipFromCity,
                            shipFromState, shipFromPostalCode, Country.UNITED_STATES.displayName)
                    .withAddress(TransactionAddressType.ShipTo, address.line1, address.line2, null, address.city,
                            getProvince(address), getPostalCode(address), getCountry(address))
                    .withDate(transactionDate)
                    .withReferenceCode(purchaseId)
            items.each {
                if (it.amount <= 0 || !it.type) {
                    throw ExceptionUtils.createUnProcessableDataException('avalara.items.not.completed')
                }
                String description = it.type.description
                if (it.descValue) {
                    description = MessageFormat.format(description, it.descValue)
                }
                builder.withLine(it.amount, new BigDecimal(1), taxCode, null, description)
            }
            if (discount != null && discount < 0) {
                builder.withLine(discount, new BigDecimal(1), taxCode,  null, 'Accounts Discount')
            }
            builder.Create()
        } catch (EasyVisaException ev) {
            log.warn('AvaTax request failed', ev)
            throw ev
        } catch (Exception e) {
            log.warn('AvaTax request failed', e)
            throw ExceptionUtils.createUnProcessableDataException('avalara.bad.request',)
        }
    }

    private AvaTaxClient getClient() {
        new AvaTaxClient(appName, appVersion, machineName, url).withSecurity(accountId, key)
    }

    private String getCountry(Address address) {
        (address.country ?: Country.UNITED_STATES).displayName
    }

    private String getProvince(Address address) {
        address.state ? address.state.code : address.province
    }

    private String getPostalCode(Address address) {
        address.zipCode ? address.zipCode : address.postalCode
    }

    static class TaxItem {
        BigDecimal amount
        TaxItemType type
        String descValue

        TaxItem(BigDecimal amount, TaxItemType type, String descValue = null) {
            this.amount = amount
            this.type = type
            this.descValue = descValue
        }

    }
}
