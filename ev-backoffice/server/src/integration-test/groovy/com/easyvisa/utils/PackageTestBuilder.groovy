package com.easyvisa.utils

import com.easyvisa.*
import com.easyvisa.document.DocumentNote
import com.easyvisa.enums.*
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.SectionCompletionStatus
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.util.DateUtil
import grails.buildtestdata.TestDataBuilder
import grails.compiler.GrailsCompileStatic
import grails.plugins.rest.client.RestResponse
import groovy.time.TimeCategory
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j

import org.hibernate.HibernateException
import org.hibernate.StaleObjectStateException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException
import org.springframework.transaction.support.TransactionSynchronizationManager

import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.CountDownLatch

import static com.easyvisa.enums.EmployeePosition.ATTORNEY
import static com.easyvisa.enums.EmployeePosition.PARTNER


/**
 * Helper class (aka Builder) that holds package building staff and other package support stuff,
 * like managing attorney, etc.
 */
@Slf4j
//@GrailsCompileStatic
class PackageTestBuilder implements TestDataBuilder {

    static final String ATTORNEY_PASSWORD = 'packageAttorneyPassword'
    private static final int TWO = 2

    AttorneyService attorneyService
    PackageService packageService
    AdminService adminService

    @Autowired
    ProfileService profileService

    AnswerService answerService
    PackageQuestionnaireService packageQuestionnaireService

    Integer serverPort
    String paymentUrl
    String paymentApiKey
    String cardLastFour
    String failedCardLastFour

    Package aPackage
    LegalRepresentative packageLegalRepresentative
    LegalRepresentative legalRepresentativeNoPackage
    Employee trainee
    Organization organization
    Organization soloOrganization
    String accessTokenPackageLegalRep
    String accessTokenNoPackageLegalRep
    String accessTokenTrainee
    String accessTokenPetitioner
    String accessTokenPackageDirect
    List<Alert> alerts = []
    List<Warning> warnings = []
    List<DocumentNote> documentNotes = []
    /**
     * To be charged at package OPEN time.
     */
    BigDecimal perApplicant = TestUtils.randomNumber()
    BigDecimal bonusPerApplicant = perApplicant.divide(new BigDecimal(TWO)).setScale(TWO, RoundingMode.HALF_UP)
    BigDecimal toChargePerApplicant = perApplicant - bonusPerApplicant
    /**
     * To be charged at edit OPEN package time.
     */
    BigDecimal perApplicant2 = TestUtils.randomNumber()
    BigDecimal bonusPerApplicant2 = perApplicant2.divide(new BigDecimal(TWO)).setScale(TWO, RoundingMode.HALF_UP)
    BigDecimal toChargePerApplicant2 = perApplicant2 - bonusPerApplicant2
    BigDecimal signUpDiscount = TestUtils.randomNumber()
    BigDecimal referralDiscount = TestUtils.randomNumber()
    String username

    /**
     * Initializes an object. Only required configs should be provided. It's not required to set the whole set.
     * @param attorneyService attorney service service
     * @return prepared object to use
     */
    static PackageTestBuilder init(Map<String, Object> configs) {
        new PackageTestBuilder(serverPort: configs['serverPort'] as Integer,
                attorneyService: configs['attorneyService'] as AttorneyService,
                packageService: configs['packageService'] as PackageService,
                adminService: configs['adminService'] as AdminService,
                profileService: configs['profileService'] as ProfileService,
                answerService: configs['answerService'] as AnswerService,
                packageQuestionnaireService: configs['packageQuestionnaireService'] as PackageQuestionnaireService,
                paymentUrl: configs['paymentUrl'] as String,
                paymentApiKey: configs['paymentApiKey'] as String,
                cardLastFour: configs['cardLastFour'] as String,
                failedCardLastFour: configs['failedCardLastFour'] as String)
    }

    /**
     * Initializes an object, based on another builder. Copies all services and organizations
     * @param builder another builder
     * @return prepared object to use
     */
    static PackageTestBuilder init(PackageTestBuilder builder) {
        new PackageTestBuilder(serverPort: builder.serverPort,
                attorneyService: builder.attorneyService,
                packageService: builder.packageService,
                adminService: builder.adminService,
                profileService: builder.profileService,
                answerService: builder.answerService,
                packageQuestionnaireService: builder.packageQuestionnaireService,
                paymentUrl: builder.paymentUrl,
                paymentApiKey: builder.paymentApiKey,
                cardLastFour: builder.cardLastFour,
                failedCardLastFour: builder.cardLastFour,
                organization: builder.organization,
                soloOrganization: builder.soloOrganization,
                packageLegalRepresentative: builder.packageLegalRepresentative)
    }

    /**
     * Creates a package with Petitioner and Beneficiary in LEAD status.
     * Also creates LegalRepresentative and Organization.
     * @param addBeneficiaryEmail true - beneficiary email will be added, otherwise not
     * @param benefitCategory benefit category for direct beneficiary. Default is F1_a
     * @param petitionerApplicantId petitioner applicant id to set
     * @param principleApplicantId principle beneficiary applicant id to set.
     * @param petitionerStatus petitioner citizenship status. Default is U_S_CITIZEN
     * @param derivativeCategory benefit category for derivative beneficiary. Default is F1_a
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndBeneficiaryLeadPackage(Boolean addBeneficiaryEmail = true,
                                                                ImmigrationBenefitCategory benefitCategory = ImmigrationBenefitCategory.F1_A,
                                                                Long petitionerApplicantId = null, Long principleApplicantId = null,
                                                                CitizenshipStatus petitionerStatus = CitizenshipStatus.U_S_CITIZEN,
                                                                ImmigrationBenefitCategory derivativeCategory = ImmigrationBenefitCategory.F1_A) {
        createPackage(addBeneficiaryEmail, false, false, null, benefitCategory, derivativeCategory,
                petitionerStatus, petitionerApplicantId, principleApplicantId)
        this
    }

    /**
     * Creates a package with No Petitioner in LEAD status.
     * Also creates LegalRepresentative and Organization.
     * @param category immigration benefit category. Default is SIX01 (601)
     * @param applicantId applicant id to set
     * @return PackageTestHelper
     */
    PackageTestBuilder buildNoPetitionerLeadPackage(ImmigrationBenefitCategory category =
                                                            ImmigrationBenefitCategory.SIX01, Long applicantId = null) {
        createPackage(true, true, false, null, category, ImmigrationBenefitCategory.F1_A,
                CitizenshipStatus.U_S_CITIZEN, null, applicantId)
        this
    }

    /**
     * Creates a package with Petitioner, Beneficiary and Derivative Beneficiary in LEAD status.
     * Also creates LegalRepresentative and Organization.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndTwoBeneficiariesLeadPackage(ImmigrationBenefitCategory benefitCategory = ImmigrationBenefitCategory.F1_A,
                                                                     ImmigrationBenefitCategory derivativeBenefitCategory = ImmigrationBenefitCategory.F1_A) {
        createPackage(true, false, true, null,
                benefitCategory, derivativeBenefitCategory)
        this
    }

    /**
     * Creates a package with Petitioner, Beneficiary and Derivative Beneficiary in OPEN status.
     * Also creates LegalRepresentative and Organization.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndTwoBeneficiariesOpenPackage(ImmigrationBenefitCategory benefitCategory = ImmigrationBenefitCategory.F1_A,
                                                                     ImmigrationBenefitCategory derivativeBenefitCategory = ImmigrationBenefitCategory.F1_A) {
        buildPetitionerAndTwoBeneficiariesLeadPackage(benefitCategory, derivativeBenefitCategory)
        createCommonOpenPackage()
        this
    }

    Applicant createRegisteredApplicant() {
        ApplicantCommand applicantCommand = createBeneficiaryCommand(true, false, ImmigrationBenefitCategory.F1_A)
        Applicant applicant = null
        Applicant.withNewTransaction {
            applicant = profileService.createApplicant(applicantCommand, null)
            applicant.profile.user = User.build(username: "registeredapplicant", password: "registeredapplicant")
            applicant.profile.save()
        }
        return applicant
    }

    PackageTestBuilder updateBeneficiaryBenefitCategory(ImmigrationBenefitCategory newBeneficiaryCategory) {
        PackageCommand packageCommand = new PackageCommand()
        packageCommand.representativeId = aPackage.attorney.id
        packageCommand.owed = aPackage.owed
        packageCommand.organizationId = organization.id
        List<ApplicantCommand> applicants = []
        if (aPackage.petitioner) {
            applicants << createPetitionerCommand(aPackage.petitioner)
        }
        for (ImmigrationBenefit benefit : aPackage.orderedBenefits) {
            applicants << createBeneficiaryCommand(benefit, benefit.direct ? newBeneficiaryCategory : null)
        }
        packageCommand.applicants = applicants
        Package.withNewTransaction {
            packageService.updatePackage(aPackage.refresh(), packageCommand)
        }
        this
    }

    private User createPackage(Boolean addBeneficiaryEmail = true, Boolean isNoPetitioner = false,
                               Boolean isDerivative = false, Organization org = null,
                               ImmigrationBenefitCategory benefitCategory = ImmigrationBenefitCategory.F1_A,
                               ImmigrationBenefitCategory derivativeBenefitCategory = ImmigrationBenefitCategory.F1_A,
                               CitizenshipStatus citizenshipStatus = CitizenshipStatus.U_S_CITIZEN,
                               Long petitionerApplicantId = null, Long principleApplicantId = null) {


        Package.withNewTransaction {
            buildPackageLegalRep()
            Organization orgValue = org ?: organization.refresh()
            //orgValue = orgValue ?: organization

            List<ApplicantCommand> applicants = []
            if (!isNoPetitioner) {
                applicants << createPetitionerCommand(citizenshipStatus, petitionerApplicantId)
            }
            applicants << createBeneficiaryCommand(addBeneficiaryEmail, false, benefitCategory,
                    principleApplicantId)
            PackageCommand packageCommand = new PackageCommand(applicants: applicants)
            if (isDerivative) {
                applicants << createBeneficiaryCommand(true, true, derivativeBenefitCategory)
            }
            aPackage = packageService.create(packageCommand, packageLegalRepresentative, orgValue.refresh())
                    .aPackage
            packageLegalRepresentative.user
        }
    }

    private ApplicantCommand createPetitionerCommand(CitizenshipStatus citizenshipStatus = CitizenshipStatus.U_S_CITIZEN,
                                                     Long petitionerApplicantId = null) {
        ApplicantProfileCommand petitionerProfileCommand =
                new ApplicantProfileCommand(firstName: 'petitioner-first', lastName: 'petitioner-last',
                        email: "petitioner-email${TestUtils.randomNumber()}@easyvisa.com",
                        homeAddress: new Address(state: State.ALABAMA), id: petitionerApplicantId)
        new ApplicantCommand(profile: petitionerProfileCommand, applicantType: ApplicantType.Petitioner.uiValue,
                inviteApplicant: false, citizenshipStatus: citizenshipStatus)
    }

    private ApplicantCommand createPetitionerCommand(String email, CitizenshipStatus citizenshipStatus = CitizenshipStatus.U_S_CITIZEN,
                                                     Long petitionerApplicantId = null) {
        ApplicantProfileCommand petitionerProfileCommand =
                new ApplicantProfileCommand(firstName: 'petitioner-first', lastName: 'petitioner-last',
                        email: email,
                        homeAddress: new Address(state: State.ALABAMA), id: petitionerApplicantId)
        new ApplicantCommand(profile: petitionerProfileCommand, applicantType: ApplicantType.Petitioner.uiValue,
                inviteApplicant: false, citizenshipStatus: citizenshipStatus)
    }

    /**
     * Creates applicant command to update petitioner.
     * @param petitioner petitioner
     * @return created command
     */
    private ApplicantCommand createPetitionerCommand(Petitioner petitioner) {
        Profile profile = petitioner.profile
        ApplicantProfileCommand petitionerProfileCommand = new ApplicantProfileCommand(id: petitioner.applicant.id,
                firstName: profile.firstName, lastName: profile.lastName, middleName: profile.middleName,
                email: profile.email)
        new ApplicantCommand(profile: petitionerProfileCommand, applicantType: ApplicantType.Petitioner.uiValue,
                inviteApplicant: petitioner.applicant.inviteApplicant, citizenshipStatus: petitioner.citizenshipStatus)
    }

    private ApplicantCommand createBeneficiaryCommand(Boolean addBeneficiaryEmail, Boolean isDerivative = false,
                                                      ImmigrationBenefitCategory benefitCategory = ImmigrationBenefitCategory.F1_A,
                                                      Long applicantId = null) {
        String derivativePrefix = ''
        if (isDerivative) {
            derivativePrefix = 'derivative-'
        }
        ApplicantProfileCommand applicantProfileCommand = new ApplicantProfileCommand(
                firstName: "${derivativePrefix}applicant-first", lastName: "${derivativePrefix}applicant-last",
                id: applicantId)
        if (addBeneficiaryEmail) {
            applicantProfileCommand.email = "${derivativePrefix}applicant-email${TestUtils.randomNumber()}@easyvisa.com"
        }
        ApplicantCommand applicantCommand = new ApplicantCommand(profile: applicantProfileCommand,
                applicantType: isDerivative ? ApplicantType.Derivative_Beneficiary.uiValue : ApplicantType.Beneficiary.uiValue,
                benefitCategory: benefitCategory, fee: new BigDecimal(100), inviteApplicant: false)
        if (isDerivative) {
            applicantCommand.relationshipToPrincipal = RelationshipType.CHILD
        }
        applicantCommand
    }

    private ApplicantCommand createBeneficiaryCommand(ImmigrationBenefit benefit, ImmigrationBenefitCategory category = null) {
        Profile profile = benefit.applicant.profile
        ApplicantProfileCommand applicantProfileCommand = new ApplicantProfileCommand(id: benefit.applicant.id,
                firstName: profile.firstName, lastName: profile.lastName, middleName: profile.middleName,
                email: profile.email)
        new ApplicantCommand(profile: applicantProfileCommand, applicantType:
                benefit.direct ? ApplicantType.Beneficiary.uiValue : ApplicantType.Derivative_Beneficiary.uiValue,
                benefitCategory: category ?: benefit.category, fee: benefit.fee,
                inviteApplicant: benefit.applicant.inviteApplicant)
    }

    /**
     * Creates a package with Petitioner and Beneficiary in OPEN status. Also creates LegalRepresentative, Organization,
     * Email(New Client) and PaymentMethod.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndBeneficiaryOpenPackage(Boolean addBeneficiaryEmail = true,
                                                                ImmigrationBenefitCategory benefitCategory = ImmigrationBenefitCategory.F1_A,
                                                                Long petitionerApplicantId = null, Long principleApplicantId = null,
                                                                CitizenshipStatus petitionerStatus = CitizenshipStatus.U_S_CITIZEN,
                                                                ImmigrationBenefitCategory derivativeCategory = ImmigrationBenefitCategory.F1_A) {
        buildPetitionerAndBeneficiaryLeadPackage(addBeneficiaryEmail, benefitCategory, petitionerApplicantId,
                principleApplicantId, petitionerStatus, derivativeCategory)
        if (petitionerApplicantId != null) {
            Petitioner.withNewTransaction {
                Petitioner petitioner = aPackage.petitioner
                petitioner.optIn = ProcessRequestState.ACCEPTED
                petitioner.save(failOnError: true)
            }
        }
        if (principleApplicantId != null) {
            ImmigrationBenefit.withNewTransaction {
                ImmigrationBenefit directBenefit = aPackage.directBenefit
                directBenefit.optIn = ProcessRequestState.ACCEPTED
                directBenefit.save(failOnError: true)
            }
        }
        createCommonOpenPackage()
        this
    }

    /**
     * Creates a package with Petitioner and Beneficiary in BLOCKED status. Also creates LegalRepresentative,
     * Organization, Email(New Client) and PaymentMethod, but no charges applied.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndBeneficiaryBlockedPackage() {
        buildPetitionerAndBeneficiaryLeadPackage()
        changePackageStatus(PackageStatus.BLOCKED)
        this
    }

    /**
     * Creates a package with Petitioner and Beneficiary in CLOSED status. Also creates LegalRepresentative,
     * Organization, Email(New Client) and PaymentMethod, but no charges applied.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndBeneficiaryClosedPackage() {
        buildPetitionerAndBeneficiaryLeadPackage()
        changePackageStatus(PackageStatus.CLOSED)
        this
    }

    /**
     * Creates a package with Petitioner and Beneficiary in LEAD status.
     * Also creates LegalRepresentative and Organization.
     * @param addBeneficiaryEmail true - beneficiary email will be added, otherwise not
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndBeneficiaryLeadPackageSoloOrg() {
        buildSoloOrganization()
        createPackage(true, false, false, soloOrganization)
        this
    }

    /**
     * Creates a package with Petitioner and Beneficiary in OPEN status.
     * Also creates LegalRepresentative and Organization.
     * @param addBeneficiaryEmail true - beneficiary email will be added, otherwise not
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndBeneficiaryOpenPackageSoloOrg() {
        buildPetitionerAndBeneficiaryLeadPackageSoloOrg()
        createCommonOpenPackage()
        this
    }

    /**
     * Creates a package with Petitioner and Beneficiary in BLOCKED status.
     * Also creates LegalRepresentative and Organization.
     * @param addBeneficiaryEmail true - beneficiary email will be added, otherwise not
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndBeneficiaryBlockedPackageSoloOrg() {
        buildPetitionerAndBeneficiaryLeadPackageSoloOrg()
        changePackageStatus(PackageStatus.BLOCKED)
        this
    }

    /**
     * Creates a package with Petitioner and Beneficiary in CLOSED status.
     * Also creates LegalRepresentative and Organization.
     * @param addBeneficiaryEmail true - beneficiary email will be added, otherwise not
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerAndBeneficiaryClosedPackageSoloOrg() {
        buildPetitionerAndBeneficiaryLeadPackageSoloOrg()
        changePackageStatus(PackageStatus.CLOSED)
        this
    }

    private void changePackageStatus(PackageStatus packageStatus) {
        Package.withNewTransaction {
            aPackage = packageService.maybeChangePackageStatus(aPackage.refresh(), aPackage.status, packageStatus)
                    .aPackage
            aPackage.petitioner?.applicant?.profile?.email
            aPackage.beneficiaries.each { it.profile.email }
        }

        if (PackageStatus.OPEN == packageStatus) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            def completionHandler = {
                countDownLatch.countDown()
            }
            packageService.prepareQuestionnaire(aPackage.id, DateUtil.today(), completionHandler)
            countDownLatch.await() // wait for the countdown called by completion-handler
        }
    }

    /**
     * Creates a package with No Petitioner in OPEN status. Also creates LegalRepresentative, Organization,
     * Email(New Client) and PaymentMethod.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildNoPetitionerOpenPackage(ImmigrationBenefitCategory category =
                                                            ImmigrationBenefitCategory.SIX01, Long applicantId = null) {
        buildNoPetitionerLeadPackage(category, applicantId)
        createCommonOpenPackage()
        this
    }

    /**
     * Creates a package with Petitioner, Beneficiary and Derivative Beneficiary in OPEN status.
     * Also creates LegalRepresentative, Organization,
     * Email(New Client) and PaymentMethod.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPetitionerTwoBeneficiariesOpenPackage() {
        buildPetitionerAndTwoBeneficiariesLeadPackage()
        createCommonOpenPackage()
        this
    }

    private void createCommonOpenPackage() {
        Package.withNewTransaction {
            buildNewClientEmail()
            setPerApplicantFee(perApplicant)
            buildPaymentMethodPackageLegalRep()
            aPackage.principalBeneficiary.id
        }
        changePackageStatus(PackageStatus.OPEN)
        setPerApplicantFee(perApplicant2)
    }

    PackageTestBuilder buildUsersForPackageApplicants() {
        User.withNewTransaction {
            aPackage.refresh()
            aPackage.petitioner?.refresh()
            aPackage.petitioner?.profile?.refresh()
            createApplicantUser(aPackage?.petitioner?.profile?.id)
            aPackage.beneficiaries.each {
                createApplicantUser(it.profile?.id)
            }
        }
        this
    }

    private void createApplicantUser(Long profileId) {
        if (profileId) {

            Profile stored = Profile.read(profileId)
            User user = User.build(username: stored.email.replaceAll('-|@', ''), language: 'En/US',
                    password: ATTORNEY_PASSWORD, accountLocked: false)

            TestUtils.createUserRole(user, Role.USER)

            stored.user = user

            stored.save(failOnError: true)


        }
    }


    /**
     * Adds default questions to the package as well as extra questions from the parameter.
     * @params answers list of answers to be added to the package.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildAnswers(List<Answer> answers = []) {
        Answer.withNewTransaction {
            answers.each { answerService.saveAnswer(it) }
        }
        this
    }

    /**
     * Adds PaymentMethod to package LegalRepresentative.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPaymentMethodPackageLegalRep() {
        PaymentMethod.withNewTransaction {
            if (!PaymentMethod.findByUser(packageLegalRepresentative.refresh().user)) {
                if (cardLastFour) {
                    TestUtils.getPaymentMethod(cardLastFour, packageLegalRepresentative.refresh().user, paymentApiKey,
                            paymentUrl)
                } else {
                    getPaymentMethod(null)
                }
            }
            packageLegalRepresentative.user.username
        }
        this
    }

    private PaymentMethod getPaymentMethod(PaymentMethod paymentMethod, Integer shift = 1) {
        PaymentMethod method = paymentMethod ?: new PaymentMethod(user: packageLegalRepresentative.refresh().user,
                fmPaymentMethodId: 'paymentMethod', address1: '1135 Ave C', addressCity: 'Ely', addressState: 'NV',
                addressZip: '89301', addressCountry: 'UNITED_STATES')
        method.cardExpiration = "01${Calendar.getInstance().get(Calendar.YEAR) + shift}"
        method.save(failOnError: true)
    }

    /**
     * Updates existing payment method to success payment data.
     * @return PackageTestHelper
     */
    PackageTestBuilder updatePaymentMethodPackageLegalRepToSuccess() {
        PaymentMethod.withNewTransaction {
            PaymentMethod paymentMethod = PaymentMethod.findByUser(packageLegalRepresentative.user)
            if (cardLastFour) {
                paymentMethod.fmPaymentMethodId = TestUtils.getFmToken(paymentUrl, paymentApiKey, cardLastFour)
                paymentMethod.save(failOnError: true)
            } else {
                getPaymentMethod(paymentMethod)
            }
            packageLegalRepresentative.user.username
        }
        this
    }

    /**
     * Adds PaymentMethod with failed card charge to package LegalRepresentative.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildFailedPaymentMethodPackageLegalRep() {
        PaymentMethod.withNewTransaction {
            PaymentMethod paymentMethod = PaymentMethod.findByUser(packageLegalRepresentative.refresh().user)
            if (failedCardLastFour) {
                paymentMethod = paymentMethod ?: new PaymentMethod(user: packageLegalRepresentative.user)
                paymentMethod.fmPaymentMethodId = TestUtils.getFmToken(paymentUrl, paymentApiKey, failedCardLastFour)
                paymentMethod.save(failOnError: true)
            } else {
                getPaymentMethod(paymentMethod, -1)
            }
            packageLegalRepresentative.user.username
        }
        this
    }

    /**
     * Sets perApplicant field value to per applicant admin settings. It will be used to charge package fees.
     * @return PackageTestHelper
     */
    PackageTestBuilder setPerApplicantFirst() {
        setPerApplicantFee(perApplicant)
    }

    /**
     * Sets referral discounts.
     * @return PackageTestHelper
     */
    PackageTestBuilder setReferralDiscount() {
        AdminSettings.withNewTransaction {
            AdminSettings settings = adminService.adminSettingsForUpdate
            settings.adminConfig.signupDiscount = signUpDiscount
            settings.adminConfig.referralBonus = referralDiscount
            settings.save(failOnError: true, flush: true)
        }
        this
    }


    /*
        Common Method
     */

    PackageTestBuilder buildCommonPackageLegalRep(boolean isAdmin = false, EmployeePosition position = ATTORNEY, boolean solo = false) {
        if (!packageLegalRepresentative) {

            LegalRepresentative.withNewTransaction {

                username = 'packageattorney' + TestUtils.randomNumber()

                User user = User.build(username: username,
                        language: 'En/US',
                        password: ATTORNEY_PASSWORD,
                        accountLocked: false)

                Address profileAddress = Address.build(country: Country.UNITED_STATES,
                        state: State.CALIFORNIA,
                        zipCode: '90025',
                        city: 'Los Angeles',
                        line1: '3474 Durham Court',
                        line2: 'Skywrapper Apartment')

                packageLegalRepresentative = LegalRepresentative.build(
                        profile: Profile.build(
                                user: user,
                                lastName: 'Attorney Last',
                                firstName: 'Attorney First',
                                middleName: 'Attorney Middle',
                                email: username + '@easyvisa.com',
                                address: profileAddress,
                                emailPreferences: [EmailPreference.build(type: NotificationType.APPLICANT_REGISTRATION, preference: Boolean.TRUE),
                                                   EmailPreference.build(type: NotificationType.QUESTIONNAIRE_COMPLETE, preference: Boolean.TRUE),
                                                   EmailPreference.build(type: NotificationType.DOCUMENTATION_COMPLETE, preference: Boolean.TRUE)], save: false),
                        mobilePhone: '99999123123',
                        officePhone: '+639171111111',
                        faxNumber: '+63917222222222',
                        uscisOnlineAccountNo: '1234-1234-1234',
                        practiceAreas: [PracticeArea.BUSINESS].toSet(),
                        attorneyType: AttorneyType.MEMBER_OF_A_LAW_FIRM,
                        registrationStatus: RegistrationStatus.COMPLETE, save: false)

                profileService.addEasyVisaId(packageLegalRepresentative)

                packageLegalRepresentative.save(failOnError: true, flush: true)


                Role attorneyRole = Role.findByAuthority(Role.ATTORNEY)
                if (attorneyRole) {
                    UserRole.create(user, attorneyRole)
                }
                //@AG: TODO Do we need to send a Registration Email for Integration Tests?
                //profileService.createUserVerifyTokenAndSendEmail(packageLegalRepresentative?.profile)
                if (!solo) {
                    buildOrganization()
                } else {
                    buildOrganizationSolo()
                    // Create OrganizationEmployee
                    /*OrganizationEmployee organizationEmployee = OrganizationEmployee.build(organization: organization.refresh(),
                            employee: packageLegalRepresentative.refresh(), position: PARTNER, isAdmin: true)*/

                }
                addPackageRepresentativeToOrg(isAdmin, position)
            }


        }

        LegalRepresentative.withTransaction {
            packageLegalRepresentative.refresh()
            packageLegalRepresentative.user.username
            packageLegalRepresentative.profile.email
        }
        this

    }

    /**
     * Creates LegalRepresentative (aka attorney) with Organization for Package usage.
     * @params isAdmin if true sets user as an admin in the org, otherwise not. Default is false
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPackageLegalRep(boolean isAdmin = false, EmployeePosition position = ATTORNEY) {
        buildCommonPackageLegalRep(isAdmin, position, false)
    }


    /**
     * Creates a Legal Rep/PARTNER/Attorney for a SOLO Organization
     * @return
     */
    PackageTestBuilder buildPackageLegalRepSolo(boolean isAdmin = true, EmployeePosition position = PARTNER) {
        buildCommonPackageLegalRep(isAdmin, position, true)
    }


    /**
     * Creates LegalRepresentative (aka attorney) licensed regions.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPackageLegalLicensedRegion() {
        LegalRepresentative.withNewTransaction {
            packageLegalRepresentative.refresh()
            Calendar cal2 = Calendar.getInstance()
            cal2.add(Calendar.YEAR, -2)
            packageLegalRepresentative.licensedRegions = [new LicensedRegion(state: State.NEW_YORK,
                    barNumber: "bar ${TestUtils.randomNumber()}", dateLicensed: cal2.time).save(failOnError: true)].toSet()
        }
        this
    }

    /**
     * Creates EV and OWNER roles for package legal rep.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPackageLegalRepEvRoles() {
        LegalRepresentative.withNewTransaction {
            UserRole.create(packageLegalRepresentative.user, Role.findByAuthority(Role.OWNER))
        }
        this
    }

    /**
     * Reduces created date for package legal rep user.
     * @param days number of days to be reduced
     * @return PackageTestHelper
     */
    PackageTestBuilder reducePackageLegalRepUserCreatedDate(Integer days) {
        LegalRepresentative.withNewTransaction {
            LocalDate date = LocalDate.now()
            date = date.minusDays(days)
            User user = packageLegalRepresentative.refresh().user
            Date dateCreated = Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
            User.executeUpdate("update User u set dateCreated = :dateCreated where u = :user",
                    [dateCreated: dateCreated, user: user])
        }
        this
    }

    /**
     * Sets current date to last monthly payment date for package legal rep.
     * @return PackageTestHelper
     */
    PackageTestBuilder setLastPaidTodayForPackageLegalRep() {
        LegalRepresentative.withNewTransaction {
            packageLegalRepresentative.refresh()
            packageLegalRepresentative.profile.lastMonthlyCharge = new Date()
            packageLegalRepresentative.profile.save(failOnError: true)
        }
        this
    }

    /**
     * Sets date in past to last monthly payment date for package legal rep.
     * @param months months value to be subtracted
     * @return PackageTestHelper
     */
    @CompileDynamic
    PackageTestBuilder setLastPaidInPastForPackageLegalRep(Integer months) {
        Date date = new Date()
        use(TimeCategory) {
            date = date - months.month
        }
        LegalRepresentative.withNewTransaction {
            packageLegalRepresentative.refresh().profile.lastMonthlyCharge = date
            packageLegalRepresentative.profile.save(failOnError: true)
        }
        this
    }

    /**
     * Set created date for package legal rep user.
     * @param date date to set
     * @return PackageTestHelper
     */
    PackageTestBuilder setPackageLegalRepUserCreatedDate(Date date) {
        LegalRepresentative.withNewTransaction {
            User user = packageLegalRepresentative.refresh().user
            User.executeUpdate("update User u set dateCreated = :date where u = : user",
                    [date: date, user: user])
        }
        this
    }

    /**
     * Set package legal rep user as unpaid.
     * @return PackageTestHelper
     */
    PackageTestBuilder setPackageLegalRepUserUnpaid() {
        LegalRepresentative.withNewTransaction {
            User user = packageLegalRepresentative.refresh().user
            user.paid = false
            user.save(failOnError: true)
        }
        this
    }

    /**
     * Builds ProspectCounts data for package legal rep.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPackageLegalRepProspectInfo() {
        Calendar cal = Calendar.instance
        ProspectCounts.withNewTransaction {
            (1..7).each {
                new ProspectCounts(representative: packageLegalRepresentative, searchDate: cal.time,
                        prospectType: ProspectType.PROFILE).save(failOnError: true)
            }
            (1..2).each {
                new ProspectCounts(representative: packageLegalRepresentative, searchDate: cal.time,
                        prospectType: ProspectType.PHONE).save(failOnError: true)
            }
            (1..2).each {
                new ProspectCounts(representative: packageLegalRepresentative, searchDate: cal.time,
                        prospectType: ProspectType.OFFICE).save(failOnError: true)
            }
            new ProspectCounts(representative: packageLegalRepresentative, searchDate: cal.time,
                    prospectType: ProspectType.FAX).save(failOnError: true)
        }
        this
    }

    /**
     * Builds article bonus data for package legal rep.
     * @param submittedDate submitted date. Default is current.
     * @param approved approve value. Default it true
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPackageLegalRepArticleBonus(Date submittedDate = new Date(),
                                                        Boolean approved = Boolean.TRUE) {
        ProspectCounts.withNewTransaction {
            Article article = new Article(author: packageLegalRepresentative.refresh(), organization: organization,
                    categoryId: '1', categoryName: 'category 1', title: 'title EV integration test', body: 'body 1',
                    wordsCount: 600L, isApproved: approved, dateSubmitted: submittedDate, status: ArticleStatus.SUBMITTED)
                    .save(failOnError: true)
            new AccountTransaction(profile: packageLegalRepresentative.profile, memo: 'article 1', article: article,
                    amount: new BigDecimal(-100), source: TransactionSource.ARTICLE).save(failOnError: true)
            packageLegalRepresentative.user.username
        }
        this
    }

    /**
     * Builds article bonus data for no package legal rep.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildNoPackageLegalRepArticleBonus() {
        ProspectCounts.withNewTransaction {
            legalRepresentativeNoPackage.refresh()
            Article article = new Article(author: legalRepresentativeNoPackage, organization: organization,
                    categoryId: '1', categoryName: 'category 2', title: 'title 2', body: 'body 2', wordsCount: 600L,
                    status: ArticleStatus.SUBMITTED)
                    .save(failOnError: true)
            new AccountTransaction(profile: legalRepresentativeNoPackage.profile, memo: 'article 2', article: article,
                    amount: new BigDecimal(-200), source: TransactionSource.ARTICLE).save(failOnError: true)
            legalRepresentativeNoPackage.refresh()
            legalRepresentativeNoPackage.user.username
        }
        this
    }

    /**
     * Builds Attorney review.
     * @param rating expected rating. Default is 5(max)
     * @return PackageTestHelper
     */
    PackageTestBuilder buildPackageLegalRepReviewByBeneficiary(Integer rating = 5) {
        Review.withNewTransaction {
            new Review(representative: packageLegalRepresentative.refresh(), aPackage: aPackage.refresh(),
                    reviewer: aPackage.directBenefit.applicant, rating: rating,
                    title: "Review title ${TestUtils.randomNumber()}", review: "Review body ${TestUtils.randomNumber()}"
            ).save(failOnError: true)
        }
        this
    }

    /**
     * Adds retainer file to the package.
     * @return PackageTestHelper
     */
    PackageTestBuilder addPackageRetainer() {
        Package.withNewTransaction {
            EasyVisaFile retainerFile = new EasyVisaFile(path: "/tmp/temp-retainer-agreement",
                    originalName: 'my-retainer.doc', uploader: packageLegalRepresentative.refresh().profile,
                    fileType: 'doc', thumbnailPath: "/tmp/temp-retainer-agreement",)
                    .save(failOnError: true,)
            aPackage.retainerAgreement = retainerFile
            organization.refresh()
            aPackage.save(failOnError: true,)
            packageLegalRepresentative.user.username
        }
        this
    }

    /**
     * Sets inactive flag to Package Legal Representatives.
     * @return PackageTestHelper
     */
    PackageTestBuilder setInactiveFlagToPackageLegalRep() {
        LegalRepresentative.withNewTransaction {
            packageLegalRepresentative.refresh()
            packageLegalRepresentative.user.activeMembership = false
            packageLegalRepresentative.user.save(failOnError: true)
        }
        this
    }

    /**
     * Sets invite flag to true for direct beneficiary
     * @return PackageTestHelper
     */
    PackageTestBuilder setInviteFlagToBeneficiary() {
        Package.withNewTransaction {
            Applicant applicant = aPackage.principalBeneficiary
            applicant.inviteApplicant = true
            applicant.save(failOnError: true)
            aPackage.refresh()
            aPackage.principalBeneficiary.id
        }
        this
    }

    /**
     * Creates Organization.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildOrganization() {
        if (!organization) {
            Organization.withTransaction {
                organization = TestUtils.createOrganization('Package Organization', OrganizationType.LAW_FIRM)
            }
        }
        this
    }

    /**
     * Build a Solo Organization
     * @return
     */
    PackageTestBuilder buildOrganizationSolo() {
        if (!organization) {
            Organization.withTransaction {
                organization = TestUtils.createOrganization('Employee Solo Org', OrganizationType.SOLO_PRACTICE)
            }
        }
        this
    }

    /**
     * @AG: This method is convoluted and creates LAW_FIRM before creating a SOLO org by calling buildPackageLegalRep.
     * This is not correct.
     *
     * Creates Organization of Solo Practice and add Package Legal Representative to the organization.
     *
     * @return PackageTestHelper
     */
    @Deprecated
    PackageTestBuilder buildSoloOrganization() {
        if (!soloOrganization) {
            //buildPackageLegalRep()
            Organization.withNewTransaction {
                soloOrganization = TestUtils.createOrganization('Employee Solo Org', OrganizationType.SOLO_PRACTICE)
                OrganizationEmployee organizationEmployee = new OrganizationEmployee(organization: soloOrganization,
                        employee: packageLegalRepresentative.refresh(), position: PARTNER, isAdmin: true).save(failOnError: true)
                soloOrganization.save(failOnError: true)
                organizationEmployee.save(failOnError: true)
            }
        }
        this
    }

    /**
     * Creates LegalRepresentative with no package in the same org.
     * todo Make it common with legalRep
     * @param isAdmin if true adds to organization as an admin, otherwise not. Default is false.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildNoPackageLegalRep(Boolean isAdmin = false) {

        if (!legalRepresentativeNoPackage) {
            LegalRepresentative.withNewTransaction {
                String username = 'nopackageattorney' + TestUtils.randomNumber()


                User user = User.build(username: username,
                        language: 'En/US',
                        password: ATTORNEY_PASSWORD,
                        accountLocked: false)

                Address profileAddress = Address.build(country: Country.UNITED_STATES,
                        state: State.CALIFORNIA,
                        zipCode: '90025',
                        city: 'Los Angeles',
                        line1: '3474 Durham Court',
                        line2: 'Skywrapper Apartment')

                legalRepresentativeNoPackage = LegalRepresentative.build(
                        profile: Profile.build(
                                user: user,
                                lastName: 'Attorney Last NP',
                                firstName: 'Attorney First NP',
                                middleName: 'Attorney Middle NP',
                                email: username + '@easyvisa.com',
                                address: profileAddress,
                                emailPreferences: [EmailPreference.build(type: NotificationType.APPLICANT_REGISTRATION, preference: Boolean.TRUE),
                                                   EmailPreference.build(type: NotificationType.QUESTIONNAIRE_COMPLETE, preference: Boolean.TRUE),
                                                   EmailPreference.build(type: NotificationType.DOCUMENTATION_COMPLETE, preference: Boolean.TRUE)], save: false),
                        mobilePhone: '99999123123',
                        officePhone: '+639171111111',
                        faxNumber: '+63917222222222',
                        uscisOnlineAccountNo: '1234-1234-1234',
                        practiceAreas: [PracticeArea.BUSINESS].toSet(),
                        attorneyType: AttorneyType.MEMBER_OF_A_LAW_FIRM,
                        registrationStatus: RegistrationStatus.COMPLETE, save: false)

                profileService.addEasyVisaId(legalRepresentativeNoPackage)

                legalRepresentativeNoPackage.save(failOnError: true, flush: true)
                Role attorneyRole = Role.findByAuthority(Role.ATTORNEY)
                if (attorneyRole) {
                    UserRole.create(user, attorneyRole)
                }
                buildOrganization()

                addNoPackageRepresentativeToOrg(isAdmin)
            }


        }
        LegalRepresentative.withNewTransaction {
            legalRepresentativeNoPackage.refresh()
            legalRepresentativeNoPackage.user.username
            legalRepresentativeNoPackage.profile.email
        }

        this
    }

    PackageTestBuilder addNoPackageRepresentativeToOrg(Boolean isAdmin = false, EmployeePosition position = ATTORNEY) {
        addRepresentativeToOrg(legalRepresentativeNoPackage, isAdmin, position)
        this
    }

    PackageTestBuilder addPackageRepresentativeToOrg(Boolean isAdmin = false, EmployeePosition position = ATTORNEY) {
        addRepresentativeToOrg(packageLegalRepresentative, isAdmin, position)
        this
    }


    private void addRepresentativeToOrg(LegalRepresentative legalRep, Boolean isAdmin = false, EmployeePosition position = ATTORNEY) {
        OrganizationEmployee.withTransaction {
            OrganizationEmployee organizationEmployee = OrganizationEmployee.build(organization: organization,
                    employee: legalRep.refresh(),
                    position: position,
                    isAdmin: isAdmin)

        }
    }


    /**
     * Deactivates package legal rep in appropriate OrganizationEmployee record.
     * @return PackageTestBuilder
     */
    PackageTestBuilder makePackageRepresentativeInactiveInOrg() {
        OrganizationEmployee.withNewTransaction {
            OrganizationEmployee organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganization(packageLegalRepresentative, organization)
            organizationEmployee.status = EmployeeStatus.INACTIVE
            organizationEmployee.inactiveDate = new Date()
            organizationEmployee.save(failOnError: true)
        }
        this
    }

    /**
     * Creates Employee with Trainee position in the Organization.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildTrainee() {
        Employee.withNewTransaction {
            buildOrganization()
            User traineeUser = User.build(username: "traineeuser${TestUtils.randomNumber()}",
                    language: 'En/US',
                    password: ATTORNEY_PASSWORD,
                    accountLocked: false)

            Profile traineeProfile = Profile.build(user: traineeUser,
                    lastName: 'Trainee Last',
                    firstName: 'Trainee First',
                    middleName: 'Trainee Middle',
                    email: "traineeuser${TestUtils.randomNumber()}@easyvisa.com",
                    easyVisaId: 'ev-id')

            trainee = Employee.build(profile: traineeProfile)

            OrganizationEmployee.build(employee: trainee, organization: organization, isAdmin: false,
                    position: EmployeePosition.TRAINEE)

            TestUtils.createUserRole(traineeUser, Role.EMPLOYEE)
        }
        this
    }

    /**
     * Adds AccountTransaction with a half of perApplicant field.
     * @return PackageTestHelper
     */
    PackageTestBuilder addBonusAccountTransactionToPackageLegalRep() {
        addAccountTransaction(bonusPerApplicant.negate())
        this
    }

    /**
     * Adds AccountTransaction with value equals perApplicant field.
     * @return PackageTestHelper
     */
    PackageTestBuilder addFullBonusAccountTransactionToPackageLegalRep() {
        addAccountTransaction(perApplicant.negate())
        this
    }

    /**
     * Adds AccountTransaction with a half of perApplicant2 field.
     * @return PackageTestHelper
     */
    PackageTestBuilder addBonus2AccountTransactionToPackageLegalRep() {
        addAccountTransaction(bonusPerApplicant2.negate())
        this
    }

    /**
     * Adds AccountTransaction with value equals perApplicant2 field.
     * @return PackageTestHelper
     */
    PackageTestBuilder addFullBonus2AccountTransactionToPackageLegalRep() {
        addAccountTransaction(perApplicant2.negate())
        this
    }

    /**
     * Adds AccountTransaction to package legal rep.
     * @param amount value
     * @param source transaction source. Default is REFERRAL
     * @param memo transaction description
     * @return PackageTestHelper
     */
    PackageTestBuilder addAccountTransactionToPackageLegalRep(BigDecimal amount,
                                                              TransactionSource source = TransactionSource.REFERRAL,
                                                              String memo = TestUtils.TEST_REFERRAL_MEMO) {
        addAccountTransaction(amount, source, memo)
        this
    }

    /**
     * Adds PAYMENT AccountTransaction to package legal rep.
     * @param amount amount to be tracked as paid
     * @return PackageTestHelper
     */
    PackageTestBuilder addPaymentAccountTransactionToPackageLegalRep(BigDecimal amount) {
        addAccountTransaction(amount.negate(), TransactionSource.PAYMENT, 'Test paid')
        this
    }

    /**
     * Add warnings to the package.
     * @param applicant applicant
     * @return PackageTestHelper
     */
    PackageTestBuilder addWarnings(Applicant applicant = null) {
        Applicant appWarning = aPackage.principalBeneficiary
        Answer answer = null
        Warning.withNewTransaction {
            if (applicant) {
                appWarning = applicant
                answer = Answer.findByApplicantId(applicant.id)
            }
            (1..3).each {
                Warning warn = new Warning(subject: "Warning ${it}", aPackage: aPackage, applicant: appWarning, answer: answer)
                        .save(failOnError: true)
                warnings.add(warn)
            }
        }
        this
    }

    PackageTestBuilder markWarningAsRead() {
        Warning.withNewTransaction {
            Warning warning = warnings.first().refresh()
            warning.isRead = Boolean.TRUE
            warning.save(failOnError: true)
        }
        this
    }

    /**
     * Add alerts to the package.
     * @return PackageTestHelper
     */
    PackageTestBuilder addAlertsToPackageLegalRep() {
        Alert.withNewTransaction {
            (1..3).each {
                alerts << new Alert(recipient: packageLegalRepresentative.refresh().user, subject: "Alert ${it}", aPackage: aPackage,
                        applicant: aPackage?.principalBeneficiary).save(failOnError: true)
            }
        }
        this
    }

    PackageTestBuilder markAlertAsRead() {
        Alert.withNewTransaction {
            Alert alert = alerts.first().refresh()
            alert.isRead = Boolean.TRUE
            alert.save(failOnError: true)
        }
        this
    }

    PackageTestBuilder addAnswersFromAttorneyUser() {
        Applicant petitioner = aPackage.petitioner?.applicant
        List<Answer> answers = AnswerListStub.answerList(aPackage.id, petitioner?.id)
        QuestionnaireTestDBSetupUtility.saveQuestionnaireAnswers(answerService, aPackage.id,
                aPackage.principalBeneficiary.id, 'Sec_1', answers)
        Answer.withNewTransaction {
            Answer answer = answers.first()
            answer.refresh()
            packageLegalRepresentative.user.refresh()
            answer.createdBy = packageLegalRepresentative.user
            answer.updatedBy = packageLegalRepresentative.user
            answer.save(failOnError: true)
        }
        this
    }
    /**
     * Deletes no package legal representative.
     * @return PackageTestHelper
     */
    PackageTestBuilder deleteNoPackageLegalRep() {
        if (legalRepresentativeNoPackage) {
            LegalRepresentative.withNewTransaction {
                TestUtils.deleteRepresentative(legalRepresentativeNoPackage.id)
            }
        }
        this
    }

    /**
     * Deletes package legal representative.
     * @return PackageTestHelper
     */
    PackageTestBuilder deletePackageLegalRep() {
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteRepresentative(packageLegalRepresentative.id)
        }
        this
    }

    /**
     * Deletes package organization.
     * @return PackageTestHelper
     */
    PackageTestBuilder deleteOrganization() {
        LegalRepresentative.withNewTransaction {
            TestUtils.deleteOrganization(organization.refresh())
        }
        this
    }

    /**
     * Deletes Trainee.
     * @return PackageTestHelper
     */
    PackageTestBuilder deleteTrainee() {
        if (trainee) {
            Employee.withNewTransaction {
                TestUtils.deleteEmployee(trainee)
            }
        }
        this
    }

    /**
     * Adds Email for new client type.
     * @return PackageTestHelper
     */
    PackageTestBuilder buildNewClientEmail() {
        Email.build(content: 'this is the email for $legal_rep_name',
                aPackage: aPackage.refresh(),
                templateType: EmailTemplateType.NEW_CLIENT,
                subject: 'New email invite')
        /*new Email(content: 'this is the email for $legal_rep_name', aPackage: aPackage.refresh(),
                templateType: EmailTemplateType.NEW_CLIENT, subject: 'New email invite').save(failOnError: true,)*/
        this
    }

    /**
     * Logs in Package LegalRepresentative.
     * Then accessTokenPackageLegalRep field can be used as a value in 'Authorization' header.
     * @return PackageTestHelper
     */
    PackageTestBuilder logInPackageLegalRep() {
        LegalRepresentative.withTransaction {
            packageLegalRepresentative.refresh()
            packageLegalRepresentative.user.username
        }
        RestResponse resp = TestUtils.logInUser(serverPort, packageLegalRepresentative.user.username, ATTORNEY_PASSWORD)
        accessTokenPackageLegalRep = formatAccessToken(resp)
        LegalRepresentative.withTransaction {
            packageLegalRepresentative.refresh()
            packageLegalRepresentative.user.refresh()
            aPackage?.refresh()?.organization?.id
        }
        this
    }

    /**
     * Logs in Package Direct Beneficiary.
     * Then accessTokenPackageLegalRep field can be used as a value in 'Authorization' header.
     * @return PackageTestHelper
     */
    PackageTestBuilder logInPackageDirectBeneficiary() {
        RestResponse resp = TestUtils.logInUser(serverPort, aPackage.principalBeneficiary.user.username,
                ATTORNEY_PASSWORD)
        accessTokenPackageDirect = formatAccessToken(resp)
        this
    }

    /**
     * Logs in No Package LegalRepresentative.
     * Then accessTokenPackageLegalRep field can be used as a value in 'Authorization' header.
     * @return PackageTestHelper
     */
    PackageTestBuilder logInNoPackageLegalRep() {
        LegalRepresentative.withNewTransaction {
            legalRepresentativeNoPackage.refresh()
            legalRepresentativeNoPackage.user.username
        }
        RestResponse resp = TestUtils.logInUser(serverPort, legalRepresentativeNoPackage.user.username,
                ATTORNEY_PASSWORD)
        accessTokenNoPackageLegalRep = formatAccessToken(resp)
        LegalRepresentative.withNewTransaction {
            legalRepresentativeNoPackage.refresh()
            legalRepresentativeNoPackage.user.refresh()
        }
        this
    }

    /**
     * Logs in Trainee.
     * Then accessTokenTrainee field can be used as a value in 'Authorization' header.
     * @return PackageTestHelper
     */
    PackageTestBuilder logInTrainee() {
        RestResponse resp = TestUtils.logInUser(serverPort, trainee.user.username, ATTORNEY_PASSWORD)
        accessTokenTrainee = formatAccessToken(resp)
        Employee.withNewTransaction {
            trainee.refresh()
            trainee.user.refresh()
        }
        this
    }

    /**
     * Logs in Package Petitioner.
     * Then accessTokenTrainee field can be used as a value in 'Authorization' header.
     * @return PackageTestHelper
     */
    PackageTestBuilder logInPackagePetitioner() {
        Package.withNewTransaction {
            aPackage.refresh().petitioner.profile.user.username
        }
        RestResponse resp = TestUtils.logInUser(serverPort, aPackage.petitioner.profile.user.username,
                ATTORNEY_PASSWORD)
        accessTokenPetitioner = formatAccessToken(resp)
        this
    }

    /**
     * Cleans data.
     * @params deletePetitionerApplicant true - delete petitioner applicant, false - otherwise. Default is true.
     * @params deletePrincipleBeneficiary true - delete principle applicant, false - otherwise. Default is true.
     * @return PackageTestHelper
     */
    PackageTestBuilder clean(Boolean deletePetitionerApplicant = true, Boolean deletePrincipleBeneficiary = true) {
        setPerApplicantFee(BigDecimal.ZERO)
        deletePackage(deletePetitionerApplicant, deletePrincipleBeneficiary)
        deleteNoPackageLegalRep()
        deleteTrainee()
        deleteSoloOrg()
        this
    }

    /**
     * Deletes the Package, Legal Representative and Organization.
     * @params deletePetitionerApplicant true - delete petitioner applicant, false - otherwise. Default is true.
     * @params deletePrincipleBeneficiary true - delete principle applicant, false - otherwise. Default is true.
     * @return PackageTestHelper
     */
    PackageTestBuilder deletePackage(Boolean deletePetitionerApplicant = true, Boolean deletePrincipleBeneficiary = true) {

        try {
            Package.withNewTransaction {
                aPackage = aPackage ?: Package.findByAttorney(packageLegalRepresentative?.refresh())
                if (aPackage) {
                    TestUtils.deletePackageWithDeps(aPackage.id, true, true, deletePetitionerApplicant,
                            deletePrincipleBeneficiary)
                } else {
                    deleteOrganization()
                    deletePackageLegalRep()
                }
            }

        } catch (HibernateException|HibernateOptimisticLockingFailureException hex) {
            log.warn("Error Deleting Package", hex)
        }


        this
    }

    /**
     * Deletes all non current packages for the legal rep.
     * @return PackageTestHelper
     */
    PackageTestBuilder deleteOtherAttorneyPackages() {
        Package.withNewTransaction {
            Package.findAllByIdNotEqualAndAttorney(aPackage.id, packageLegalRepresentative).each {
                TestUtils.deletePackageWithDeps((it as Package).id, false, false, false)
            }
        }
        this
    }

    /**
     * Deletes the Package only. Legal Representative and Organizations won't be touched.
     * @params deletePetitionerApplicant true - deletes petitioner applicant, otherwise not. Default is true.
     * @params deleteBeneficiaryApplicant true - deletes beneficiary applicant, otherwise not. Default is true.
     * @return PackageTestHelper
     */
    PackageTestBuilder deletePackageOnly(Boolean deletePetitionerApplicant = true,
                                         Boolean deleteBeneficiaryApplicant = true) {
        Package.withNewTransaction {
            TestUtils.deletePackageWithDeps(aPackage.id, false, false, deletePetitionerApplicant,
                    deleteBeneficiaryApplicant)
        }
        this
    }

    /**
     * Deletes the Solo Organization.
     * @return PackageTestHelper
     */
    PackageTestBuilder deleteSoloOrg() {
        Organization.withNewTransaction {
            TestUtils.deleteOrganization(soloOrganization?.refresh())
        }
        this
    }

    /**
     * Deletes ProcessRequests and Alerts for no Package Legal Representative.
     * @return PackageTestHelper
     */
    PackageTestBuilder deleteNoPackageLegalRepProcessRequests() {
        Alert.withNewTransaction {
            List<ApplicantPackageTransferRequest> requestList =
                    ApplicantPackageTransferRequest.findAllByRepresentative(legalRepresentativeNoPackage.refresh())
            requestList.each {
                List<Alert> alerts = Alert.findAllByProcessRequest(it)
                alerts.each {
                    it.delete(failOnError: true)
                }
                it.delete(failOnError: true)
            }
            List<PackageTransferRequest> requests = PackageTransferRequest.findAllByRepresentative(legalRepresentativeNoPackage.refresh())
            requests.each {
                List<Alert> alerts = Alert.findAllByProcessRequest(it)
                alerts.each {
                    it.delete(failOnError: true)
                }
                it.delete(failOnError: true)
            }
        }
        this
    }


    PackageTestBuilder deletePackageAnswersOnly() {
        Answer.withNewTransaction {

            List<Answer> answers = Answer.findAllByPackageId(aPackage.id)
            answers.each {
                it.delete(failOnError: true)
            }

            List<SectionCompletionStatus> sectionCompletionStatuses = SectionCompletionStatus.findAllByPackageId(aPackage.id)
            sectionCompletionStatuses.each {
                it.delete(failOnError: true)
            }
        }
        this
    }


    PackageTestBuilder changePackageStatusToOpen() {
        createCommonOpenPackage()
        this
    }


    private String formatAccessToken(RestResponse resp) {
        "Bearer ${resp.json['access_token']}"
    }

    private AccountTransaction addAccountTransaction(BigDecimal value,
                                                     TransactionSource source = TransactionSource.REFERRAL,
                                                     String memo = TestUtils.TEST_REFERRAL_MEMO) {
        AccountTransaction.withNewTransaction {
            packageLegalRepresentative.refresh()
            AccountTransaction res = new AccountTransaction(amount: value, profile: packageLegalRepresentative.profile, memo: memo, source: source)
                    .save(failOnError: true,)
            packageLegalRepresentative.refresh()
            packageLegalRepresentative.user.username
            res
        }
    }

    /**
     * Sets fee to per applicant. It will be used to charge package fees.
     * @param fee fee
     * @return PackageTestHelper
     */
    private PackageTestBuilder setPerApplicantFee(BigDecimal amount) {
        AdminSettings.withNewTransaction {
            if (adminService) {
                AdminSettings settings = adminService.adminSettingsForUpdate
                settings.adminConfig.signupFee = amount
                settings.adminConfig.attorney.feeSchedule.each {
                    it.amount = amount
                    it.save(failOnError: true)
                }
                settings.save(failOnError: true, flush: true)
            }
        }
        this
    }

    /**
     * Sums two per applicant fees and saves the result to perApplicantFee2.
     * @return PackageTestBuilder
     */
    PackageTestBuilder sumPerApplicantFee() {
        perApplicant2 += perApplicant
        setPerApplicantFee(perApplicant2)
        this
    }

    /**
     * Reduces per applicant 2 that next charge will less them per applicant.
     * @return PackageTestBuilder
     */
    PackageTestBuilder reduceNextPerApplicantCharge() {
        perApplicant2 = perApplicant - 0.01
        setPerApplicantFee(perApplicant2)
        this
    }

    PackageTestBuilder refreshPetitioner() {
        Package.withNewTransaction {
            aPackage.refresh()?.petitioner?.profile?.email
            aPackage?.petitioner?.applicant?.id
            aPackage?.petitioner?.applicant?.profile?.address?.id
            aPackage?.principalBeneficiary?.id
            aPackage?.principalBeneficiary?.profile?.email
            aPackage?.orderedBenefits[1]?.applicant?.profile?.email
            aPackage?.orderedBenefits[1]?.applicant?.profile?.id
        }
        this
    }

    String getPackagePetitionerBeneficiaryCreatePayload(String petitionerEmail = null, Long petitionerId = null,
                                                        Long packageId = null) {
        String petitionerEmailToSet = petitionerEmail ?: "petitioner-email${TestUtils.randomNumber()}@easyvisa.com"
        """{
${getPackageCommonCreatePayload(true, packageId)}
"applicants": [
${getPetitionerPayload(petitionerEmailToSet, petitionerId)}
,
${getBeneficiaryPayload("applicant-email${TestUtils.randomNumber()}@easyvisa.com", false)}
]}"""
    }

    String getPackagePetitionerBeneficiaryEditPayload(String petitionerEmail = null, Long petitionerId = null,
                                                      Long packageId = null) {
        String petitionerEmailToSet = petitionerEmail ?: "petitioner-email${TestUtils.randomNumber()}@easyvisa.com"
        """{
${getPackageCommonCreatePayload(false, packageId)}
"applicants": [
${getPetitionerPayload(petitionerEmailToSet, petitionerId)}
,
${getBeneficiaryPayload("applicant-email${TestUtils.randomNumber()}@easyvisa.com", false)}
]}"""
    }

    String getPackageNoPetitionerCreatePayload(Long beneficiaryId = null) {
        """{
${getPackageCommonCreatePayload()}
"applicants": [
${getBeneficiaryPayload("applicant-email${TestUtils.randomNumber()}@easyvisa.com", false, beneficiaryId, false,
                ImmigrationBenefitCategory.SIX01)
        }
]}"""
    }

    String getPackagePetitionerAndTwoBeneficiariesCreatePayload() {
        """{
${getPackageCommonCreatePayload()}
"applicants": [
${getPetitionerPayload("petitioner-email${TestUtils.randomNumber()}@easyvisa.com")}
,
${getBeneficiaryPayload("applicant-email${TestUtils.randomNumber()}@easyvisa.com", false)}
,
${getBeneficiaryPayload("derivative-applicant-email${TestUtils.randomNumber()}@easyvisa.com", false, null, true,
                ImmigrationBenefitCategory.F1_A)
        }
]}"""
    }

    String getEditBeneficiaryNoEmailWithPetitionerPayload() {
        """{
${getPackageCommonCreatePayload(false)}
"applicants": [
${getPetitionerPayload(aPackage.petitioner.profile.email, aPackage.petitioner.applicant.id)}
,
${getBeneficiaryPayload("applicant-email${TestUtils.randomNumber()}@easyvisa.com", true, aPackage.beneficiaries[0].id)}
]}"""
    }

    String getEditNewBeneficiaryWithPetitionerPayload() {
        """{
${getPackageCommonCreatePayload(false)}
"applicants": [
${getPetitionerPayload(aPackage.petitioner.profile.email, aPackage.petitioner.applicant.id)}
,
${getBeneficiaryPayload("new-applicant-email${TestUtils.randomNumber()}@easyvisa.com")}
]}"""
    }

    String getEditRegisteredBeneficiaryWithPetitionerPayload(Applicant applicant) {
        """{
${getPackageCommonCreatePayload(false)}
"applicants": [
${getPetitionerPayload(aPackage.petitioner.profile.email, aPackage.petitioner.applicant.id)}
,
${getBeneficiaryPayload(applicant)}
]}"""
    }

    String getEditNewBenefitCategoryBeneficiaryWithPetitionerPayload(ImmigrationBenefitCategory category = ImmigrationBenefitCategory.F1_A) {
        """{
${getPackageCommonCreatePayload(false)}
"applicants": [
${getPetitionerPayload(aPackage.petitioner.profile.email, aPackage.petitioner.applicant.id)}
,
${getBeneficiaryPayload(aPackage.directBenefit.applicant.profile.email, false, aPackage.directBenefit.applicant.id, false, category)}
]}"""
    }

    String getEditNewBeneficiaryWithNoPetitionerPayload() {
        """{
${getPackageCommonCreatePayload(false)}
"applicants": [
${getBeneficiaryPayload("new-applicant-email${TestUtils.randomNumber()}@easyvisa.com", true, null, false,
                ImmigrationBenefitCategory.SIX01)
        }
]}"""
    }

    String getEditSameBeneficiaryWithNoPetitionerPayload() {
        """{
${getPackageCommonCreatePayload(false)}
"applicants": [
${getBeneficiaryPayload(aPackage.beneficiaries.first().profile.email, false, aPackage.beneficiaries.first().id, false,
                ImmigrationBenefitCategory.SIX01)
        }
]}"""
    }

    String getEditRegisteredApplicantPayload(String existingEmail) {
        """{
${getPackageCommonCreatePayload(false)}
"applicants": [
${getBeneficiaryPayload(existingEmail, false, aPackage.beneficiaries.first().id, false,
                ImmigrationBenefitCategory.SIX01)
        }
]}"""
    }

    String getEditNewBeneficiaryWithSamePetitionerAndDerivativePayload() {
        """{
${getPackageCommonCreatePayload(false)}
"applicants": [
${getPetitionerPayload(aPackage.petitioner.profile.email, aPackage.petitioner.applicant.id)}
,
${getBeneficiaryPayload("new-applicant-email${TestUtils.randomNumber()}@easyvisa.com")}
,
${
            getBeneficiaryPayload(aPackage.orderedBenefits[1].applicant.profile.email, false, aPackage.orderedBenefits[1].applicant.id, true,
                    ImmigrationBenefitCategory.F1_A)}
]}"""
    }

    private String getPackageCommonCreatePayload(Boolean isCreate = true, Long packageId = null) {
        if (isCreate) {
            return """
                "representativeId": ${packageLegalRepresentative.id},
                "organizationId":${organization.id},"""
        }
        String result = """"representativeId": ${packageLegalRepresentative.id},
        "owed":500,"""
        if (packageId) {
            result = """${result} "id": ${packageId}, """
        }
        result
    }

    private String getPetitionerPayload(String email, Long id = null) {
        String idValue = ''
        if (id) {
            idValue = """, "id":${id}"""
        }
        """{
"profile": {
"firstName": "petitioner-first",
"lastName": "petitioner-last",
"middleName": "petitioner-last-middle",
"email": "${email}",
"dateOfBirth": "12-03-1995",
"mobileNumber": "123132312",
"homeNumber": "123132312",
"workNumber": "123132312",
"homeAddress":{"country":"UNITED_STATES","state":"ALABAMA","line1":"line1 address2","line2":"line2 address2",
"zipCode":"98145","city":"cityName"}
${idValue}
},
"citizenshipStatus": "${CitizenshipStatus.U_S_CITIZEN}",
"applicantType":"${ApplicantType.Petitioner.uiValue}",
"inviteApplicant":true
}"""
    }

    private String getBeneficiaryPayload(Applicant applicant) {
        getBeneficiaryPayload(applicant.profile.email, false, applicant.id, false, ImmigrationBenefitCategory.F1_A)
    }

    private String getBeneficiaryPayload(String email, Boolean isNew = true, Long id = null,
                                         Boolean isDerivative = false,
                                         ImmigrationBenefitCategory category = ImmigrationBenefitCategory.F1_A) {
        String idValue = ''
        String emailValue = ''
        String inviteValue = 'false'
        String prefixValue = ''
        String relationshipValue = ''
        if (id) {
            idValue = """, "id":${id}"""
        }
        if (email) {
            emailValue = """"email":"${email}","""
            inviteValue = 'true'
        }
        if (isNew) {
            prefixValue = 'new-'
        }
        if (isDerivative) {
            prefixValue = 'derivative-'
            relationshipValue = '"relationshipToPrincipal": "SPOUSE",'
        }
        """{
"profile": {
"firstName": "${prefixValue}applicant-first",
"lastName": "${prefixValue}applicant-last",
"middleName": "${prefixValue}applicant-middle",
${emailValue}
"dateOfBirth":"12-03-2001",
"mobileNumber": "123132312",
"homeNumber": "123132312",
"workNumber": "123132312",
"homeAddress":{"country":"UNITED_STATES","state":"ALABAMA","line1":"line1 address1","line2":"line2 address1",
"zipCode":"98145","city":"cityName"}
$idValue
},
"fee":500,
"applicantType":"${isDerivative ? ApplicantType.Derivative_Beneficiary.uiValue : ApplicantType.Beneficiary.uiValue}",
${relationshipValue}
"inviteApplicant":${inviteValue},
"benefitCategory": "${category}"
}"""
    }


    /**
     * Add DocumentNote to the package.
     * @param applicant applicant
     * @return PackageTestHelper
     */
    PackageTestBuilder addDocumentNotes() {
        DocumentNote.withNewTransaction {
            Applicant applicant = aPackage.principalBeneficiary
            User currentUser = applicant.user;
            DocumentNote documentNote = new DocumentNote(
                    aPackage: aPackage,
                    subject: "This is new public note",
                    documentNoteType: DocumentNoteType.PUBLIC_NOTE,
                    creator: currentUser.profile,
                    createdBy: currentUser,
                    updatedBy: currentUser
            ).save(failOnError: true, flush: true)
            documentNotes.add(documentNote)
        }
        this
    }
}
