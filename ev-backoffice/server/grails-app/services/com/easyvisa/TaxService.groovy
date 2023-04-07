package com.easyvisa

import com.easyvisa.enums.Country
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PackageStatus
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.enums.State
import com.easyvisa.enums.TaxItemType
import com.easyvisa.enums.TransactionSource
import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import org.apache.http.HttpStatus

@GrailsCompileStatic
class TaxService {

    private static final String PAYMENT_METHOD_ERROR_CODE = 'payment.method.not.configured'

    boolean skipTaxes = false
    AvaTaxService avaTaxService
    AdminService adminService
    PermissionsService permissionsService
    AccountService accountService

    /**
     * Estimates taxes for requested type of EV fees.
     * Note: Package will be calculated from DB by packageId only. Should be used for package opening.
     * @param command endpoint parameters
     * @param user user
     * @return EV fee and estimated taxes
     */
    EstimatedTaxes estimateTax(TaxEstimationCommand command, User user) {
        Profile profile = user.profile
        User userToEstimate = user
        EstimatedTaxes taxes = new EstimatedTaxes(subTotal:BigDecimal.ZERO)
        List<AvaTaxService.TaxItem> items = []
        TaxItemType type = command.type
        switch (type) {
            case TaxItemType.SIGNUP_FEE:
                taxes.subTotal = adminService.adminSettings.adminConfig.signupFee
                items << new AvaTaxService.TaxItem(taxes.subTotal, type)
                break
            case TaxItemType.MEMBERSHIP_REACTIVATION_FEE:
                taxes.subTotal = adminService.adminSettings.adminConfig.membershipReactivationFee
                items << new AvaTaxService.TaxItem(taxes.subTotal, type)
                break
            case TaxItemType.IMMIGRATION_BENEFIT:
                Package aPackage = Package.get(command.packageId)
                if (!aPackage) {
                    throw ExceptionUtils.createUnProcessableDataException('package.not.found.with.id')
                }
                profile = aPackage.attorney.profile
                userToEstimate = profile.user
                permissionsService.validatePackageReadAccess(user, aPackage)
                aPackage.benefits.each {
                    ImmigrationBenefit benefit = it as ImmigrationBenefit
                    BigDecimal applicantFee = adminService.findPerApplicantFee(benefit.category)
                    taxes.subTotal += applicantFee
                    items << new AvaTaxService.TaxItem(applicantFee, type, benefit.category.abbreviation)
                }
                break
            default:
                throw ExceptionUtils.createUnProcessableDataException('wrong.estimation.item', null, [type])
        }
        estimateTaxes(userToEstimate, taxes, command, profile, items)
        taxes
    }

    /**
     * Estimates taxes for package update.
     * The package should be opened and not closed.
     * @param command endpoint params
     * @param user current user to check permissions for package editing
     * @return estimated taxes with detailed items
     */
    DetailedEstimatedTaxes estimatePackageUpdateTax(TaxEstimationCommand command, User user) {
        Package aPackage = Package.get(command.packageId)
        if (!aPackage) {
            throw ExceptionUtils.createUnProcessableDataException('package.not.found.with.id')
        }
        if (![PackageStatus.OPEN, PackageStatus.BLOCKED].contains(aPackage.status)) {
            throw ExceptionUtils.createUnProcessableDataException('tax.estimation.package.update.wrong.status')
        }
        Profile profile = aPackage.attorney.profile
        User userToEstimate = profile.user
        permissionsService.validatePackageReadAccess(user, aPackage)
        TaxItemType type = TaxItemType.IMMIGRATION_BENEFIT
        DetailedEstimatedTaxes taxes = new DetailedEstimatedTaxes(subTotal:BigDecimal.ZERO)
        List<AvaTaxService.TaxItem> items = []
        List<ApplicantCommand> applicants = command.packageObj.getApplicants()
        List<ApplicantCommand> petitioners = command.packageObj.getPetitioners()
        applicants.removeAll(petitioners)
        applicants.each {
            ImmigrationBenefit benefit = aPackage.getBenefitForApplicantId(it.id)
            if (!it.benefitCategory) {
                throw ExceptionUtils.createUnProcessableDataException('package.benefit.category.not.provided')
            }
            if (!benefit || (benefit && benefit.category != it.benefitCategory)) {
                BigDecimal applicantFee = adminService.findPerApplicantFee(it.benefitCategory)
                if ((it.id && !benefit) || (benefit && benefit.optIn != ProcessRequestState.ACCEPTED)) {
                    //applicant is registered and later charged will be applied
                    addToListOfMap(taxes.later, applicantFee, it.benefitCategory)
                } else {
                    //no id means applicant is not registered and immediate charge will be applied
                    if (benefit) {
                        applicantFee = accountService.calculateBenefitCategory(benefit, it.benefitCategory)['toCharge'] as BigDecimal
                    }
                    if (applicantFee > 0) {
                        addToListOfMap(taxes.immediate, applicantFee, it.benefitCategory)
                        taxes.subTotal += applicantFee
                        items << new AvaTaxService.TaxItem(applicantFee, type, it.benefitCategory.abbreviation)
                    }
                }
            }
        }
        estimateTaxes(userToEstimate, taxes, command, profile, items)
        taxes
    }

    private void addToListOfMap(List<Map> list, BigDecimal applicantFee, ImmigrationBenefitCategory category) {
        list.add([amount:applicantFee, benefitCategory:category.name()])
    }

    private void estimateTaxes(User userToEstimate, EstimatedTaxes taxes, TaxEstimationCommand command, Profile profile, List<AvaTaxService.TaxItem> items) {
        if (skipTaxes) {
            taxes.estTax = BigDecimal.ZERO
            taxes.grandTotal = taxes.subTotal
            return
        }
        BigDecimal discount = accountService.getBalance(userToEstimate.id)
        if (taxes.subTotal > discount.negate()) {
            if (discount > 0) {
                discount = BigDecimal.ZERO
            } else {
                taxes.subTotal += discount
            }
            Address address = command.type == TaxItemType.SIGNUP_FEE ? command.address : convertToAddress(PaymentMethod.findByUser(userToEstimate))
            taxes.estTax = avaTaxService.getEstimatedTax(address, profile.easyVisaId, null, items, new Date(), discount)
            taxes.grandTotal = taxes.subTotal + taxes.estTax
        } else {
            taxes.subTotal = BigDecimal.ZERO
            taxes.grandTotal = BigDecimal.ZERO
            taxes.estTax = BigDecimal.ZERO
        }
    }

    /**
     * Estimates taxes for current user balance.
     * @param profile profile
     * @param paymentMethod payment method
     * @param items items to be charged
     * @param balance user balance
     * @return balance with taxes
     */
    EstimatedTaxes estimateTax(Profile profile, PaymentMethod paymentMethod, List<AccountTransaction> items, BigDecimal balance) {
        if (!items || skipTaxes) {
            return new EstimatedTaxes(estTax:BigDecimal.ZERO, subTotal:balance, grandTotal:balance)
        }
        BigDecimal tax = avaTaxService.getEstimatedTax(convertToAddress(paymentMethod), profile.easyVisaId, null, createTaxItems(items))
        new EstimatedTaxes(estTax:tax, subTotal:balance, grandTotal:tax+balance)
    }

    /**
     * Estimates taxes for $1 of Maintenance Fee. It uses to validate billing address for tax calculation.
     * @param profile users' profile
     * @param paymentMethod payment method
     */
    void validateBillingAddress(Profile profile, PaymentMethod paymentMethod) {
        if (skipTaxes) {
            return
        }
        List<AccountTransaction> items = [new AccountTransaction(source:TransactionSource.MAINTENANCE, amount:BigDecimal.ONE)]
        avaTaxService.getEstimatedTax(convertToAddress(paymentMethod), profile.easyVisaId, null, createTaxItems(items))
    }

    /**
     * Creates taxable transaction and collect total taxes to be charged.
     * @param paymentMethod payment method
     * @param customerId customer id
     * @param purchaseId purchase id
     * @param items items
     * @param transactionDate transaction date
     * @param discount user's discount
     * @return tax stuff
     */
    Tax createTransaction(PaymentMethod paymentMethod, String customerId, String purchaseId, List<AccountTransaction> items,
                          Date transactionDate = new Date(), BigDecimal discount = null) {
        Address address = convertToAddress(paymentMethod)
        List<AvaTaxService.TaxItem> taxItems = createTaxItems(items)
        if (skipTaxes) {
            return new Tax(total: BigDecimal.ZERO, avaTaxId:"skipped", billingAddress:address)
        }
        avaTaxService.createTransaction(address, customerId, purchaseId, taxItems, transactionDate, discount)
    }

    /**
     * Commits AvaTax transaction. It means it will be available to official report.
     * @param avaTaxId AvaTax code
     */
    void commitTransaction(String avaTaxId) {
        if (skipTaxes) {
            return
        }
        avaTaxService.commitTransaction(avaTaxId)
    }

    private Address convertToAddress(PaymentMethod paymentMethod) {
        if (!paymentMethod || paymentMethod.expired) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: PAYMENT_METHOD_ERROR_CODE)
        }
        new Address(country: paymentMethod.addressCountry as Country, line1: paymentMethod.address1,
                line2: paymentMethod.address2, state: State.valueOfCode(paymentMethod.addressState),
                zipCode: paymentMethod.addressZip, city: paymentMethod.addressCity)
    }

    private List<AvaTaxService.TaxItem> createTaxItems(List<AccountTransaction> items) {
        List<AvaTaxService.TaxItem> taxItems = []
        items.each {
            TaxItemType taxItemType = it.source.taxItemType
            //getting item description param if possible
            String descriptionParam
            switch (taxItemType) {
                case TaxItemType.IMMIGRATION_BENEFIT:
                    descriptionParam = it.immigrationBenefit.category.abbreviation
                    break
                case TaxItemType.CLOUD_STORAGE_FEE:
                    descriptionParam = it.memo
                    break
                default:
                    descriptionParam = null
            }
            //adding items
            taxItems.add(new AvaTaxService.TaxItem(it.amount, taxItemType, descriptionParam))
        }
        taxItems
    }

    static class EstimatedTaxes {
        BigDecimal subTotal, estTax, grandTotal
    }

    static class DetailedEstimatedTaxes extends EstimatedTaxes {
        List<Map> immediate = []
        List<Map> later = []
    }
}
