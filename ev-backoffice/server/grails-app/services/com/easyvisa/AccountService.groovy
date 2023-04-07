package com.easyvisa

import com.easyvisa.dto.AddTransactionResponseDto
import com.easyvisa.dto.MessageResponseDto
import com.easyvisa.dto.PackageResponseDto
import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.dto.TimelineDecimalItemResponseDto
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.enums.TransactionSource
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.ExceptionUtils
import com.easyvisa.utils.NumberUtils
import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

import java.math.RoundingMode

/**
 * Service for managing user payment transactions.
 */
@Transactional
class AccountService {

    PaymentService paymentService
    AdminService adminService
    PermissionsService permissionsService
    UserService userService
    TaxService taxService

    /**
     * Charges user at a registration step.
     * Saves payment method info to DB.
     * Sign up fee collects from AdminConfig
     * @param user user to be charged
     * @param paymentMethodCommand Fattmerchant token for the user to be charged and other card details
     */
    void registrationCharge(User user, PaymentMethodCommand paymentMethodCommand) {
        AccountTransaction registerTransaction = AccountTransaction
                .findByProfileAndSource(user.profile, TransactionSource.REGISTRATION)
        if (registerTransaction) {
            throw new EasyVisaException(errorMessageCode:'registration.fee.paid')
        }
        paymentService.saveToken(user.id, paymentMethodCommand)
        BigDecimal amount = adminService.adminSettings.adminConfig.signupFee
        addTransaction(user, amount, 'One-time Registration fee', TransactionSource.REGISTRATION)
        charge(user)
        updateUserPaid(user)
    }

    /**
     * Charges user for beneficiaries in the package.
     * Non charged beneficiaries will be affected only.
     * @param packageResponseDto package response data
     */
    void packageCharge(PackageResponseDto packageResponseDto) {
        Package aPackage = packageResponseDto.aPackage
        User user = aPackage.attorney.profile.user
        permissionsService.assertIsActive(user, 'payment.inactive.user')
        BigDecimal balance = getBalance(user.id)
        assertBalance(balance, 'account.transaction.outstanding.balance.open.package')
        BigDecimal toCharge = 0
        String messageCode
        BigDecimal args
        ErrorMessageType messageType
        aPackage.benefits.each {
            toCharge += calculateBenefitCategoryToCharge(aPackage, it, user)
            if (it.optIn == ProcessRequestState.ACCEPTED) {
                it.paid = true
            }
        }
        if (toCharge) {
            messageCode = 'payment.charged.successful'
            messageType = ErrorMessageType.PAYMENT_CHARGED
            if (balance < 0) {
                toCharge += balance
                messageCode = 'payment.partially.charged.successful'
            }
            if (toCharge > 0) {
                AccountTransaction transaction = charge(user)
                args = transaction ? transaction.grandTotalPayedByUser : BigDecimal.ZERO
            } else {
                messageCode = 'payment.free.of.charge'
                args = null
            }
            packageResponseDto.messages << new MessageResponseDto(messageCode: messageCode,
                    errorMessageType: messageType, messageCodeArgs: [NumberUtils.formatMoneyNumber(args)])
        }
    }

    /**
     * Calculates benefit category charge.
     * @param aPackage package
     * @param benefit immigration benefit
     * @param user attorney user
     * @return amount to be charged
     */
    BigDecimal calculateBenefitCategoryToCharge(Package aPackage, ImmigrationBenefit benefit, User user) {
        BigDecimal toCharge = BigDecimal.ZERO
        if (!benefit.paid && benefit.optIn == ProcessRequestState.ACCEPTED) {
            Map res = calculateBenefitCategory(benefit)
            toCharge = res.toCharge
            benefit.perApplicantFee = res.perApplicant
            if (res.addAccountTransaction) {
                Profile profile = benefit.applicant.profile
                String memo = preparePackageMemo(benefit, profile, aPackage.id)
                AccountTransaction ac = addTransaction(user, res.immCharge, memo, TransactionSource.PACKAGE,
                        benefit)
                benefit.applicantTransactions.add(ac)
            }
        }
        toCharge
    }

    Map calculateBenefitCategory(ImmigrationBenefit benefit, ImmigrationBenefitCategory category = null) {
        BigDecimal toCharge = BigDecimal.ZERO
        BigDecimal perApplicant = adminService.findPerApplicantFee(category ?: benefit.category)
        BigDecimal immCharge = perApplicant
        boolean addAccountTransaction = true
        //benefit helps to detect edit benefit category costs.
        //if old category less than current we need to charge to difference
        //if less - free
        //one more benefit category change to higher rate require to charge difference against current
        //benefit category costs (based on the latest update)
        //e.g. initial 100, then 50 and 80. So, first change 100, then no charge and the last one is 30
        if (benefit.perApplicantFee == null) {
            toCharge += perApplicant
        } else if (perApplicant > benefit.perApplicantFee) {
            immCharge = perApplicant - benefit.perApplicantFee
            toCharge += immCharge
        } else {
            addAccountTransaction = false
        }
        [perApplicant: perApplicant, addAccountTransaction: addAccountTransaction, immCharge: immCharge, toCharge: toCharge]
    }

    /**
     * Charges user for current balance. Uses for UI call at pay button point.
     * @param payBalanceCommand balance info
     * @param user user to be charged
     */
    void payBalance(PayBalanceCommand payBalanceCommand, User user) {
        BigDecimal balance = getBalance(user.id)
        if (balance == payBalanceCommand.balance && balance > 0) {
            payBalance(user)
        } else {
            throw ExceptionUtils.createUnProcessableDataException('account.transaction.not.valid.balance')
        }
    }

    /**
     * Charges user balance and marks all required stuff as paid.
     * @param user user
     * @return charged amount value
     */
    BigDecimal payBalance(User user) {
        Profile profile = user.profile
        List<AccountTransaction> nonPaid = getNonPaidAccountTransactions(profile)
        BigDecimal charged = BigDecimal.ZERO
        AccountTransaction result = charge(user, 'Balance Paid')
        if (result != null) {
            nonPaid.each {
                if (it.immigrationBenefit) {
                    it.immigrationBenefit.paid = true
                    it.save('failOnError': true)
                }
            }
            updateUserPaid(user)
            charged = result.grandTotalPayedByUser
        }
        charged
    }

    /**
     * Adds monthly AccountTransaction record to all active and paid users.
     * For newly joined users maintenance fee will be prorated based on usage days of previous month.
     * @param monthlyFee monthly fee to be charged
     * @param cloudStorageFee cloud storage fee
     * @param attorney attorney to be charged
     * @return AccountTransaction with maintenance fee
     */
    AccountTransaction addMaintenanceFee(BigDecimal monthlyFee, BigDecimal cloudStorageFee, LegalRepresentative attorney) {
        BigDecimal toCharge = monthlyFee
        if (attorney.profile.maintenanceFee != null) {
            toCharge = attorney.profile.maintenanceFee
        }
        Calendar registerCalendar = Calendar.instance
        registerCalendar.time = attorney.user.dateCreated
        Calendar lastDay = Calendar.instance
        lastDay = lastDay.clearTime()
        lastDay.set(Calendar.DAY_OF_MONTH, 1)
        lastDay.add(Calendar.MILLISECOND, -1)
        if (lastDay.get(Calendar.MONTH) == registerCalendar.get(Calendar.MONTH)
                && lastDay.get(Calendar.YEAR) == registerCalendar.get(Calendar.YEAR)) {
            Integer duration = TimeCategory.minus(lastDay.time, attorney.user.dateCreated).days
            //if attorney joined at the last day of a month he will be charged for one day
            duration = duration ?: 1
            toCharge = (toCharge * (duration / lastDay.get(Calendar.DAY_OF_MONTH)))
            toCharge = toCharge.setScale(2, RoundingMode.HALF_UP)
        }
        AccountTransaction result = addTransaction(attorney.user, toCharge, 'Monthly Maintenance Fee', TransactionSource.MAINTENANCE)

        Integer packages = Package.createCriteria().count {
            eq('attorney', attorney)
        }
        BigDecimal cloudFee = cloudStorageFee
        if (attorney.profile.cloudStorageFee != null) {
            cloudFee = attorney.profile.cloudStorageFee
        }
        BigDecimal cloudCharge = packages * cloudFee
        addTransaction(attorney.user, cloudCharge, "Monthly Cloud Storage Fee for ${packages} package(s)", TransactionSource.CLOUD_STORAGE)
        Profile profile = attorney.profile
        profile.lastMonthlyCharge = result.date
        profile.save(failOnError: true)
        result
    }

    /**
     * Sends request to Fattmerchnat for charging required amount of money.
     * @param userToCharge user to be charged
     * @param memo payment memo. Default is 'Payment'
     * @return AccountTransaction with all charge details. Can be null.
     */
    AccountTransaction charge(User userToCharge, String memo = 'Payment.') {
        BigDecimal total = getBalance(userToCharge.id)
        AccountTransaction result = null
        if (total > BigDecimal.ZERO) {
            Profile profile = userToCharge.profile
            //getting non paid transaction for tax items enumeration. Not all sources (Bonuses/Refunds) will be taxable
            List<AccountTransaction> nonPaid = getTaxableAccountTransactions(profile)
            //create payment transaction
            AccountTransaction paymentTransaction = addTransaction(userToCharge, total.negate(), memo,
                    TransactionSource.PAYMENT)
            //discount value
            BigDecimal discount = BigDecimal.ZERO
            BigDecimal transactionsAmount = nonPaid*.amount.sum() as BigDecimal
            if (transactionsAmount > total) {
                discount = total - transactionsAmount
            }
            //getting taxes
            Tax tax = taxService.createTransaction(paymentService.getPaymentMethod(userToCharge), profile.easyVisaId,
                    paymentTransaction.id as String, nonPaid, paymentTransaction.date, discount)
            paymentTransaction.tax = tax
            //calculation grand total
            BigDecimal toCharge = total + tax.total
            //charge user
            String transactionId = paymentService.charge(toCharge, userToCharge, tax.total)
            paymentTransaction.fmTransactionId = transactionId
            //commit transaction
            try {
                taxService.commitTransaction(tax.avaTaxId)
            } catch (Exception e) {
                log.error("AvaTax transaction [${tax.avaTaxId}] can't be committed. It shoud be commited manually, due to user [${userToCharge.id}] is already charged", e)
            }
            paymentTransaction.save(failOnError: true)
            result = paymentTransaction
        }
        result
    }

    /**
     * Returns list of an user's AccountTransactions records.
     * @param userId user id to get balance
     * @return list of available AccountTransaction. Can be empty if no records found.
     */
    PaginationResponseDto getAccountTransactions(Long userId, PaginationCommand paginationCommand) {
        PaginationResponseDto result = new PaginationResponseDto()
        result.result = AccountTransaction.createCriteria().list(paginationCommand.paginationParams) {
            profile {
                eq('user.id', userId)
            }
            order('date', 'desc')
        } as List<AccountTransaction>

        result.totalCount = AccountTransaction.createCriteria().get {
            projections {
                count('id')
            }
            profile {
                eq('user.id', userId)
            }
        } as Integer
        result
    }

    /**
     * Calculates and returns user's article bonuses in different current timelines.
     * 1. Month
     * 2. Quarter
     * 3. YTD
     * 4. All
     * @param userId user id
     * @param organizations organizations list
     * @return calculated article bonuses in different time frames
     */
    TimelineDecimalItemResponseDto timelineArticleTransactions(Long userId, List<Organization> organizations) {
        collectTimelineTransactions(userId, organizations, TransactionSource.ARTICLE)
    }

    /**
     * Calculates user balance based on available transaction.
     * Can be positive - user should be charged.
     * Negative or zero - everything is ok
     * @param userId user id to get balance
     * @return total balance
     */
    BigDecimal getBalance(Long userId) {
        BigDecimal result = AccountTransaction.createCriteria().get {
            projections {
                sum('amount')
            }
            profile {
                eq('user.id', userId)
            }
        } as BigDecimal
        result ?: BigDecimal.ZERO
    }

    /**
     * Asserts user balance. If user needs to be charged EasyVisaException will be thrown.
     * @param userId user id to get balance
     * @param errorCode error code if failed
     */
    void assertBalance(Long userId, String errorCode = 'account.transaction.outstanding.balance') {
        assertBalance(getBalance(userId), errorCode)
    }

    /**
     * Asserts user balance. If user needs to be charged EasyVisaException will be thrown.
     * @param balance current balance
     * @param errorCode error code if failed
     */
    void assertBalance(BigDecimal balance, String errorCode = 'account.transaction.outstanding.balance') {
        if (balance > 0) {
            throw ExceptionUtils.createUnProcessableDataException(errorCode)
        }
    }

    /**
     * Calculates user balance based on available transaction.
     * Can be positive - user should be charged.
     * Negative or zero - everything is ok
     * Estimated taxes will be calculated as well.
     * @param userId user id to get balance
     * @return total balance with estimated taxes
     */
    TaxService.EstimatedTaxes getBalanceWithEstTaxes(Long userId) {
        BigDecimal balance = getBalance(userId)
        if (balance <= 0) {
            return new TaxService.EstimatedTaxes(subTotal:balance, estTax:BigDecimal.ZERO, grandTotal:BigDecimal.ZERO)
        }
        try {
            Profile profile = User.get(userId).profile
            List<AccountTransaction> taxable = getTaxableAccountTransactions(profile)
            taxService.estimateTax(profile, PaymentMethod.findByUser(profile.user), taxable, balance)
        } catch (EasyVisaException e) {
            log.warn("Failed to get estimated taxes for user ${userId} balance")
            new TaxService.EstimatedTaxes(subTotal:balance, estTax:BigDecimal.ZERO, grandTotal:balance)
        }
    }

    /**
     * Adds transaction to the user account. This for site owners only for getting credits.
     * @param user user to add a transaction
     * @param accountTransactionCommand transaction details
     * @return added transaction and current balance
     */
    AddTransactionResponseDto addSiteTransaction(User user, AccountTransactionCommand accountTransactionCommand) {
        //currently we gives only a refund (amount is negative)
        AccountTransaction ac = addTransaction(user, accountTransactionCommand.amount, accountTransactionCommand.memo,
                TransactionSource.REFUND)
        //flush data to get accurate balance
        BigDecimal balance = getBalance(user.id)
        if (balance <= 0) {
            updateUserPaid(user)
            List<AccountTransaction> transactions = getNonPaidAccountTransactions(user.profile).findAll { it.source == TransactionSource.PACKAGE }
            transactions.each {
                it.immigrationBenefit.paid = true
                it.save(failOnError:true)
            }
        }
        new AddTransactionResponseDto(balance: balance, accountTransaction:ac)
    }

    /**
     * Charges reactivation fee together with outstanding balance.
     * @param user user
     */
    void reactivationCharge(User user) {
        BigDecimal reactivationFee = adminService.adminSettings.adminConfig.membershipReactivationFee
        addTransaction(user, reactivationFee, 'Membership Reactivation Fee.',
                TransactionSource.REACTIVATION)
        charge(user)
    }

    /**
     * Adds article bonus to the author
     * @param article article
     */
    void addArticleBonus(Article article) {
        if (!AccountTransaction.findByProfileAndArticle(article.author.profile, article)) {
            BigDecimal bonus = adminService.adminSettings.adminConfig.articleBonus
            addTransaction(article.author.user, bonus.negate(), "Article Credit - ${article.title}",
                    TransactionSource.ARTICLE, null, article)
        } else {
            log.info("${article.author.id} has already credited for ${article.id}")
        }
    }

    /**
     * Adds attorney referral bonus.
     * @param referral Attorney offered to register on EV
     * @param referee new Attorney
     */
    void addReferralBonus(LegalRepresentative referral, Employee referee) {
        if (AccountTransaction.findByProfileAndSource(referee.profile, TransactionSource.REFERRAL)) {
            throw ExceptionUtils.createUnProcessableDataException('attorney.referral.bonus.already.applied')
        }
        BigDecimal signupDiscount = adminService.adminSettings.adminConfig.signupDiscount.negate()
        addTransaction(referee.profile.user, signupDiscount, "Signup Discount - ${referral.profile.name}", TransactionSource.REFERRAL, null, null, referral.profile)
        if (!permissionsService.isBlessed(referral.user)) {
            BigDecimal refDiscount = adminService.adminSettings.adminConfig.referralBonus.negate()
            addTransaction(referral.profile.user, refDiscount, "Referral Fee - ${referee.profile.name}", TransactionSource.REFERRAL, null, null, referee.profile)
        }
    }

    /**
     * Deletes payment method for user if it exists. Also, it will delete payment method on Fattmerchant side.
     * @param user user for payment method deletion
     */
    void deletePaymentMethod(User user) {
        PaymentMethod paymentMethod = PaymentMethod.findByUser(user)
        if (paymentMethod) {
            paymentMethod.delete(failOnError: true)
            paymentService.deletePaymentToken(paymentMethod.fmPaymentMethodId, user.id)
        }
    }

    /**
     * Adds AccountTransaction record.
     * @param user user for the record
     * @param amount amount of the transaction
     * @param memo memo of the transaction
     * @param source transaction source
     * @param benefit immigration benefit
     * @param article article
     * @param referral referral user profile
     */
    private AccountTransaction addTransaction(User user, BigDecimal amount, String memo, TransactionSource source,
                                              ImmigrationBenefit benefit = null, Article article = null, Profile referral = null) {
        Profile profile = Profile.findByUser(user)
        AccountTransaction accountTransaction
        accountTransaction = new AccountTransaction(amount: amount, memo: memo, profile: profile,
                source: source, immigrationBenefit: benefit, article: article, referral: referral)
        accountTransaction.save(failOnError: true, flush: true)
        accountTransaction
    }

    private List<AccountTransaction> getNonPaidAccountTransactions(Profile profile) {
        List<AccountTransaction> paidList = AccountTransaction.findAllByProfileAndSource(profile,
                TransactionSource.PAYMENT, [max: 1, sort: 'date', order: 'desc'])
        Date queryDate = paidList.empty ? null : paidList.first().date
        if (queryDate == null) {
            //registration case
            AccountTransaction.findAllByProfileAndAmountGreaterThan(profile, BigDecimal.ZERO)
        } else {
            AccountTransaction.findAllByProfileAndDateGreaterThanEqualsAndAmountGreaterThan(profile, queryDate, BigDecimal.ZERO)
        }
    }

    private List<AccountTransaction> getTaxableAccountTransactions(Profile profile) {
        List<TransactionSource> taxableSources = TransactionSource.taxable
        getNonPaidAccountTransactions(profile).findAll { taxableSources.contains(it.source) }
    }

    private void updateUserPaid(User user) {
        userService.markUserPaid(user)
    }

    /**
     * Build memo for an applicant of the package.
     * @param immigrationBenefit benefit category
     * @param profile applicant profile
     * @return string
     */
    private String preparePackageMemo(ImmigrationBenefit immigrationBenefit, Profile profile, Long packageId) {
        "Package P${packageId} - benefit category: " +
                "${immigrationBenefit.category} - ${immigrationBenefit.direct ? '' : 'derivative '}" +
                "beneficiary ${profile.firstName} ${profile.middleName ? "$profile.middleName " : ''}" +
                "${profile.lastName}"
    }

    /**
     * Calculates and returns user's account transactions in different current timelines.
     * 1. Month
     * 2. Quarter
     * 3. YTD
     * 4. All
     * @param userId user id
     * @param orgs organizations list
     * @param source account transaction source
     * @return calculated account transactions in different time frames
     */
    private TimelineDecimalItemResponseDto collectTimelineTransactions(Long userId, List<Organization> orgs,
                                                                TransactionSource source) {
        TimelineDecimalItemResponseDto result = new TimelineDecimalItemResponseDto()
        result.month = getTransactionsSum(userId, orgs, source, DateUtil.currentMonth)
        result.quarter = getTransactionsSum(userId, orgs, source, DateUtil.currentQuarter)
        result.ytd = getTransactionsSum(userId, orgs, source, DateUtil.currentYear)
        result.lifeTime = getTransactionsSum(userId, orgs, source)
        result
    }

    private BigDecimal getTransactionsSum(Long userId, List<Organization> orgs, TransactionSource source,
                                          Date date = null) {
        Object result = AccountTransaction.createCriteria().get {
            projections {
                sum('amount')
            }
            profile {
                eq('user.id', userId)
            }
            eq('source', source)
            if (date) {
                ge('date', date)
            }
            if (orgs) {
                article {
                    'in'('organization', orgs)
                }
            }
        }
        result ? (result as BigDecimal).setScale(2).negate() : 0
    }

}
