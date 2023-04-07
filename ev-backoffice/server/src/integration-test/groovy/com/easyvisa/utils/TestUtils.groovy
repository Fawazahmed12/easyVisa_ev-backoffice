package com.easyvisa.utils

import com.easyvisa.*
import com.easyvisa.document.DocumentCompletionStatus
import com.easyvisa.document.DocumentMilestone
import com.easyvisa.document.DocumentNote
import com.easyvisa.enums.OrganizationType
import com.easyvisa.enums.TransactionSource
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.PackageQuestionnaireVersion
import com.easyvisa.questionnaire.SectionCompletionStatus
import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import groovyx.net.http.ApacheHttpBuilder
import groovyx.net.http.HttpBuilder
import org.apache.commons.lang.RandomStringUtils

import java.security.SecureRandom
import java.text.MessageFormat

class TestUtils {

    public static final String PAID_BALANCE = 'Balance Paid'
    public static final String PAYMENT = 'Payment.'
    public static final String MONTHLY_FEE_MEMO = 'Monthly Maintenance Fee'
    public static final String CLOUD_STORAGE_FEE_MEMO = 'Monthly Cloud Storage Fee for {0} package(s)'
    public static final String REGISTRATION_FEE_MEMO = 'One-time Registration fee'
    static final String TEST_MEMO = 'test memo'
    public static final String TEST_REFERRAL_MEMO = 'Referral bonus'
    public static final String CARD_1111 = '1111'
    public static final int ACCOUNT_TRANSACTIONS_COUNT = 3
    private static final int ZERO = 0
    public static final String CUSTOMER_ID = '1400f525-ce4f-4141-b122-d95497a1cea4'
    private static final SecureRandom random = new SecureRandom()

    static User createUser(Map props) {
        Map defaultProps = ["username": "user1", "password": "password", email: "useremail@host.com"]
        new User(defaultProps << props).save(failOnError: true)
    }

    static void deleteUser(User user) {
        AuthenticationToken.findAllByUsername(user?.username)*.delete(failOnError: true)
        User.deleteAll(user?.refresh())
    }

    static void deleteUserAndPaymentDetails(User user) {
        AccountTransaction.withNewTransaction {
            PaymentMethod.findByUser(user)?.delete(failOnError: true)
            AccountTransaction.findAll().each {
                it?.tax?.delete(failOnError: true)
                it?.tax?.billingAddress?.delete(failOnError: true)
                it.delete(failOnError: true)
            }
            Profile.findByUser(user)?.delete(failOnError: true)
            deleteUser(user)
        }
    }

    static Organization createOrganization(String name, OrganizationType organizationType = OrganizationType.LAW_FIRM) {
        Organization organization = Organization.build(name: name ?: RandomStringUtils.randomAlphabetic(10), organizationType: organizationType,
                easyVisaId: "test-org-id-${RandomStringUtils.random(5)}")
        return organization
    }

    static UserRole createUserRole(User user, String role) {
        UserRole.build(user: user, role: Role.findByAuthority(role))
        /*new UserRole(user: user, role: Role.findByAuthority(role)).save(failOnError: true)*/
    }

    static void deletePackageWithDeps(Long packageId, Boolean deleteOrg = true, Boolean deleteRep = true,
                                      Boolean deletePetitionerApplicant = true, Boolean deleteBeneficiaryApplicant = true) {
        Package aPackage = Package.findById(packageId, [fetch: [applicants: 'eager', benefits: 'eager']])
        def representativeId = aPackage.attorney.id
        def benefits = aPackage.benefits
        Petitioner petitioner = aPackage.petitioner
        Organization organization = aPackage.organization
        Email.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }
        Warning.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }
        ProcessRequest.findAllByAPackage(aPackage).each {
            Alert.findAllByProcessRequest(it).each{Alert alert ->
                alert.delete(failOnError:false)
            }
            it.delete(failOnError:false)
        }
        Review.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }
        PackageAssignee.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }
        PackageReminder.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }
        DocumentMilestone.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }
        DocumentNote.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }
        SectionCompletionStatus.findAllByPackageId(packageId).each {
            it.delete(failOnError:false)
        }
        DocumentCompletionStatus.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }
        LegalRepresentativeRevenue.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }
        if (aPackage.retainerAgreement) {
            Long fileId = aPackage.retainerAgreement.id
            aPackage.retainerAgreement = null
            EasyVisaFile file = EasyVisaFile.get(fileId)
            if (file) {
                file.delete(failOnError:false)
            }
        }

        PackageQuestionnaireVersion.findAllByAPackage(aPackage).each {
            it.delete(failOnError:false)
        }

        Answer.findAllByPackageId(aPackage.id).each {
            it.delete(failOnError: true)
        }

        aPackage.delete(failOnError:false)

        benefits.each {
            deleteImmigrationBenefit(it, deleteBeneficiaryApplicant)
        }

        deletePetitioner(petitioner, deletePetitionerApplicant)

        if (deleteOrg) {
            deleteOrganization(organization)
        }
        if (deleteRep) {
            deleteRepresentative(representativeId)
        }
    }

    static void deleteApplicant(Applicant applicant) {
        Profile profile = applicant.profile
        applicant.delete(failOnError:false)
        deleteProfile(profile)
    }

    static void deletePetitioner(Petitioner petitioner, Boolean deletePetitionerApplicant = true) {
        if (!petitioner) {
            return
        }
        Applicant applicant = petitioner.applicant
        petitioner.delete(failOnError:false)
        if (deletePetitionerApplicant) {
            deleteApplicant(applicant)
        }
    }


    static void deleteImmigrationBenefit(ImmigrationBenefit immigrationBenefit, Boolean deleteBeneficiaryApplicant) {
        Applicant applicant = immigrationBenefit.applicant
        immigrationBenefit.applicantTransactions*.delete(failOnError:false)
        immigrationBenefit.delete(failOnError:false)
        if(deleteBeneficiaryApplicant){
            deleteApplicant(applicant)
        }
    }

    static void deleteAlert(Long alertId) {
        Alert.get(alertId).delete(failOnError:false)
    }

    static void deleteWarning(Long alertId) {
        Warning.get(alertId).delete(failOnError:false)
    }

    static void deleteRepresentative(Long representativeId) {
        LegalRepresentative attr = LegalRepresentative.findById(representativeId)
        Profile profile = attr.profile
        OrganizationEmployee.findAllByEmployee(attr).each {
            it.delete(failOnError:false)
        }
        PackageAssignee.findAllByRepresentative(attr)*.delete(failOnError:true)
        ProspectCounts.findAllByRepresentative(attr).each {
            it.delete(failOnError:false)
        }
        Article.findAllByAuthor(attr).each {
            it.delete(failOnError:false)
        }
        Review.findAllByRepresentative(attr).each {
            it.delete(failOnError:false)
        }
        attr.licensedRegions.each {
            it.delete(failOnError:false)
        }
        Email.findByAttorney(attr).each {
            it.delete(failOnError:false)
        }
        EmailTemplate.findByAttorney(attr).each {
            if (it.preference?.id) {
                EmailPreference.get(it.preference.id).delete(failOnError: true)
            }
            it.delete(failOnError:false)
        }
        attr.workingHours*.delete(failOnError:false)
        attr.delete(failOnError:false)
        deleteProfile(profile)
    }

    static void deleteEmployee(Employee employee) {
        employee.refresh()
        Profile profile = employee.profile
        OrganizationEmployee.findAllByEmployee(employee).each {
            it.delete(failOnError: true)
        }
        employee.delete(failOnError: true)
        deleteProfile(profile)
    }

    static void deleteProfile(Profile profile) {
        User user = profile.user
        deletePayments(profile)
        profile.emailPreferences*.delete(failOnError:false)
        profile.emailPreferences.clear()
        Alert.findAllByRecipient(profile.user)*.delete(failOnError:false)
        RegistrationCode.findAllByEasyVisaId(profile.easyVisaId)*.delete(failOnError:false)
        profile.user = null
        profile.delete(failOnError: true)
        profile.address?.delete(failOnError:false)
        if (user) {
            UserDevice.findAllByUser(user)*.delete(failOnError:false)
            user.devices.clear()
            UserRole.findAllByUser(user)*.delete(failOnError:false)
            user.delete(failOnError:false)
            RegistrationCode.findAllByUsername(user.username)*.delete(failOnError:false)
            AuditLog.findAllByUsername(user.username)*.delete(failOnError:false)
            AuthenticationToken.findAllByUsername(user.username)*.delete(failOnError:false)
        }
    }

    static void deletePayments(Profile profile) {
        PaymentMethod.findByUser(profile.user)?.delete(failOnError: true)
        AccountTransaction.findAllByProfile(profile).each {
            it.tax?.delete(failOnError: true)
            it.tax?.billingAddress?.delete(failOnError: true)
            it.delete(failOnError: true)
        }
    }

    static void deleteOrganization(Organization organization) {
        if (organization) {
            OrganizationEmployee.findAllByOrganization(organization)*.delete(failOnError: true)
            organization.delete(failOnError: true)
            organization.workingHours*.delete(failOnError: true)
            if (organization.address) {
                organization.address.delete(failOnError: true)
            }
        }
    }

    static AccountTransaction addPaidAccountTransaction(Profile profile) {
        new AccountTransaction(profile: profile, amount: new BigDecimal(0), memo: PAYMENT,
                source: TransactionSource.PAYMENT).save(failOnError: true)
    }

    static RestResponse logInUser(int serverPort, String username, String password) {
        RestBuilder rest = new RestBuilder()
        rest.post("http://localhost:${serverPort}/api/login") {
            header("Accept", "application/json")
            header("Content-Type", "application/json")
            json([username: username, password: password] as JSON)
        }
    }

    static Answer getAnswerInstance(Long packageId, Long applicantId, String path, String value) {
        String[] pathInfoList = path.split("/")
        Integer index = null // will hold repeating answers index
        if (pathInfoList.size() == 4) {
            index = pathInfoList[3] as int
        }

        Answer answer = new Answer(packageId: packageId, applicantId: applicantId, index: index,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: value, path: path)
        return answer
    }

    static BigDecimal randomNumber() {
        BigDecimal decimals = random.nextInt(99).toBigDecimal()
        decimals = decimals.divide(100 as BigDecimal)
        BigDecimal result = random.nextInt(2000).toBigDecimal()
        result += decimals
        result.setScale(2)
    }

    static void assertNoAccountTransactions(User user) {
        List<AccountTransaction> transactions
        AccountTransaction.withNewTransaction {
            transactions = AccountTransaction.findAllByProfile(Profile.findByUser(user))
            true
        }
        assert ZERO == transactions.size()
    }

    static void assertPaidAccountTransaction(List<AccountTransaction> transactions, BigDecimal amount,
                                             Integer transactionsCount = ACCOUNT_TRANSACTIONS_COUNT,
                                             BigDecimal taxAmount = null, String paidMemo = PAYMENT) {
        assert transactionsCount == transactions.size()
        AccountTransaction charged = transactions.findAll { it.source == TransactionSource.PAYMENT }
                .sort { it.date }.last()
        assertAccountTransactionValues(charged, amount.negate(), paidMemo, true)
        if (taxAmount != null) {
            Tax tax = charged.tax
            assert tax
            assert taxAmount == tax.total
            assert tax.avaTaxId
            assert tax.billingAddress
        }
    }

    static void assertCustomAccountTransactions(List<AccountTransaction> transactions, BigDecimal amount,
                                                TransactionSource source, String memo) {
        AccountTransaction packageTransaction = transactions.find { it.source == source }
        assertAccountTransactionValues(packageTransaction, amount, memo)
    }

    static void assertCustomAccountTransactions(List<AccountTransaction> transactions, BigDecimal amount,
                                                TransactionSource source, List<String> memos) {
        List<AccountTransaction> packageTransactions = transactions.findAll { it.source == source }
        List<String> checkedMemo = []
        packageTransactions.each {
            assert amount == it.amount
            assert memos.contains(it.memo)
            assert !checkedMemo.contains(it.memo)
            checkedMemo << it.memo
        }
    }

    static void assertCustomAccountTransactions(List<AccountTransaction> transactions, List<BigDecimal> amounts,
                                                TransactionSource source, List<String> memos) {
        List<AccountTransaction> packageTransactions = transactions.findAll { it.source == source }
        List<String> checkedMemo = []
        packageTransactions.each {
            assert amounts.contains(it.amount)
            assert memos.contains(it.memo)
            assert !checkedMemo.contains(it.memo)
            checkedMemo << it.memo
        }
    }

    static void assertAccountTransactionValues(AccountTransaction packageTransaction, BigDecimal amount, String memo,
                                               Boolean isCharged = false) {
        assert amount == packageTransaction.amount
        assert memo == packageTransaction.memo
        if (isCharged) {
            assert packageTransaction.fmTransactionId
        }
    }

    static String getCloudStorageMemo(BigDecimal packagesCount) {
        MessageFormat.format(TestUtils.CLOUD_STORAGE_FEE_MEMO, packagesCount)
    }

    static PaymentMethod getPaymentMethod(String lastFour, User user, String apiKey, String apiUrl) {
        HttpBuilder client = ApacheHttpBuilder.configure {
            request.uri = apiUrl
            request.contentType = groovyx.net.http.ContentTypes.JSON[0]
            request.accept = [groovyx.net.http.ContentTypes.JSON[0]]
            request.headers.put('Authorization', "Bearer $apiKey")
        }

        Object response = client.get {
            request.uri.path = "/customer/$CUSTOMER_ID/payment-method"
        }
        Map<String, String> apiMethod = response.find { it['card_last_four'] == lastFour }
        new PaymentMethod(user:user, fmPaymentMethodId:apiMethod['id'], cardExpiration:apiMethod['card_exp'],
                address1:'1135 Ave C', addressCity:'Ely', addressState:'NV', addressZip:'89301',
                addressCountry:'UNITED_STATES')
                .save(failOnError: true)
    }

    static String getFmToken(String paymentUrl, String paymentApiKey, String lastFour) {
        HttpBuilder client = ApacheHttpBuilder.configure {
            request.uri = paymentUrl
            request.contentType = groovyx.net.http.ContentTypes.JSON[0]
            request.accept = [groovyx.net.http.ContentTypes.JSON[0]]
            request.headers.put('Authorization', "Bearer $paymentApiKey")
        }

        Object response = client.get {
            request.uri.path = "/customer/$CUSTOMER_ID/payment-method"
        }
        response.find { it['card_last_four'] == lastFour }['id']
    }

    static void delayCurrentThread(long delay = 3000) {
        Thread.sleep(delay)
    }
}
