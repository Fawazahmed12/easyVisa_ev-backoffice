import com.easyvisa.EasyVisaAuthTokenJsonRenderer
import com.easyvisa.TokenService
import com.easyvisa.User
import com.easyvisa.login.AuthenticationFailureListener
import com.easyvisa.login.LoginListener
import com.easyvisa.login.RestAuthenticationFailureHandler
import com.easyvisa.login.TokenCreationEventHandler
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.questionnaire.services.rule.displaytext.AdditionalIncomeLabelDisplayTextRule
import com.easyvisa.questionnaire.services.rule.impl.*
import com.easyvisa.questionnaire.services.rule.inputsource.BeneficiaryMaritalStatusInputSourceRule
import com.easyvisa.questionnaire.services.rule.inputsource.CitizenshipAcquiredInputSourceRule
import com.easyvisa.questionnaire.services.rule.inputsource.SponsorshipRelationshipInputSourceRule
import com.easyvisa.questionnaire.services.rule.milestonereminder.ArrivalDateMilestoneReminderRule
import com.easyvisa.questionnaire.services.rule.milestonereminder.MarriageDateMilestoneReminderRule
import com.easyvisa.questionnaire.services.rule.questionreset.PriorSpousesResetRule
import com.easyvisa.questionnaire.services.rule.questionreset.RepeatingQuestionsResetRule
import com.easyvisa.questionnaire.services.rule.repeatgrouplifecycle.ChildrenInformationLifeCycleRule
import com.easyvisa.questionnaire.services.rule.sectioncompletion.EmploymentHistoryCompletionRule
import com.easyvisa.questionnaire.services.rule.sectioncompletion.FamilyInformationCompletionRule
import com.easyvisa.questionnaire.services.rule.sectioncompletion.IntroQuestionsForBeneficiaryCompletionRule
import com.easyvisa.questionnaire.services.rule.sectioncompletion.PersonalInformationCompletionRule
import com.easyvisa.questionnaire.services.rule.sectionvisibility.RelationshipToPetitionerApplicableRule
import com.easyvisa.questionnaire.services.rule.visibility.EmploymentEndDateIterationVisibilityRule
import com.easyvisa.questionnaire.services.rule.visibility.QuestionnaireFormVisibilityOffRule
import com.easyvisa.questionnaire.services.rule.visibility.QuestionnaireFormVisibilityOnRule
import com.easyvisa.questionnaire.services.rule.visibility.QuestionnaireFormVisibilityWithIterationOnRule
import com.easyvisa.questionnaire.services.rule.visibility.RQGFormVisibilityOnRule
import com.easyvisa.security.CsrfAccessDeniedHandler
import com.easyvisa.security.CsrfRequestMatcher
import grails.rest.render.json.JsonRenderer
import grails.util.Environment
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository
import com.easyvisa.questionnaire.services.rule.sectioncompletion.AddressHistoryCompletionRule
import com.easyvisa.questionnaire.services.rule.answervisibilityvalidation.CurrentMailingAddressValidationRule
import com.easyvisa.questionnaire.services.rule.util.CurrentMailingAddressRuleUtil
import com.easyvisa.questionnaire.services.rule.inputsource.CountryUSOnlyInputSourceRule
import com.easyvisa.questionnaire.services.rule.inputsource.StatesInUSPovertyGuidelinesInputSourceRule
import com.easyvisa.questionnaire.services.rule.visibility.ExplainUsDomicileVisibilityRule

// Place your Spring DSL code here
beans = {

    if (["aws_development", "qa", "production"].contains(Environment.current.name)) {
        log.info("Creating CsrfFilter bean for ${Environment.current} ENV")
        fnRequireCsrfProtectionMatcher(CsrfRequestMatcher)
        fnAccessDeniedHandler(CsrfAccessDeniedHandler) {
            messageSource = ref('messageSource')
        }

        csrfFilter(CsrfFilter, new HttpSessionCsrfTokenRepository()) {
            accessDeniedHandler = ref('fnAccessDeniedHandler')
            requireCsrfProtectionMatcher = ref('fnRequireCsrfProtectionMatcher')
        }

    }

    authenticationFailureListener(AuthenticationFailureListener) {
        loginAttemptCacheService = ref('loginAttemptCacheService')
    }

    restAuthenticationFailureHandler(RestAuthenticationFailureHandler) {
        messageSource = ref('messageSource')
    }

    loginListener(LoginListener) {
        packageService = ref('packageService')
    }

    tokenCreationEventHandler(TokenCreationEventHandler) {
        userService = ref('userService')
    }

    accessTokenJsonRenderer(EasyVisaAuthTokenJsonRenderer)


    tokenStorageService(TokenService) {
        jwtService = ref('jwtService')
        userDetailsService = ref('userDetailsService')
        grailsApplication = ref('grailsApplication')
        loginAttemptCacheService = ref('loginAttemptCacheService')
    }

    userRenderer(JsonRenderer, User) {
        excludes = ['password', 'lastUpdated']
    }

    householdIncomeApplicableRule(HouseholdIncomeApplicableRule) {
        packageService = ref('packageService')
    }

    povertyGuidelineCalculationRule(PovertyGuidelineCalculationRule) {
        packageService = ref('packageService')
        alertService = ref('alertService')
        asyncService = ref('asyncService')
    }

    autoPopulateCurrentMailingAddressRule(AutoPopulateCurrentMailingAddressRule) {
        answerService = ref('answerService')
        currentMailingAddressRuleUtil = ref('currentMailingAddressRuleUtil')
    }

    currentMailingAddressRuleUtil(CurrentMailingAddressRuleUtil) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    currentMailingAddressValidationRule(CurrentMailingAddressValidationRule) {
        currentMailingAddressRuleUtil = ref('currentMailingAddressRuleUtil')
    }

    currentMailingAddressVisibilityConstraintRule(CurrentMailingAddressVisibilityConstraintRule) {
        currentMailingAddressRuleUtil = ref('currentMailingAddressRuleUtil')
    }

    autoPopulateChildSelectionRule(AutoPopulateChildSelectionRule) {
        answerService = ref('answerService')
    }

    mailingAddressCountrySelectionRule(MailingAddressCountrySelectionRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
        currentMailingAddressRuleUtil = ref('currentMailingAddressRuleUtil')
    }

    countryUSOnlyInputSourceRule(CountryUSOnlyInputSourceRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    statesInUSPovertyGuidelinesInputSourceRule(StatesInUSPovertyGuidelinesInputSourceRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    explainUsDomicileVisibilityRule(ExplainUsDomicileVisibilityRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    maritalStatusAttorneyActionRule(MaritalStatusAttorneyActionRule) {
        answerService = ref('answerService')
        alertService = ref('alertService')
        asyncService = ref('asyncService')
    }

    priorSpousesDataInsertRule(PriorSpousesDataInsertRule) {
        answerService = ref('answerService')
    }

    autoFillDateMarriageEndedRule(AutoFillDateMarriageEndedRule) {
        answerService = ref('answerService')
    }

    autoFillContributionDurationRule(AutoFillContributionDurationRule) {
        answerService = ref('answerService')
    }

    additionalIncomeLabelDisplayTextRule(AdditionalIncomeLabelDisplayTextRule) {
        packageService = ref('packageService')
    }

    centimeterToFeetConversionRule(CentimeterToFeetConversionRule) {
        answerService = ref('answerService')
    }

    feetToCentimeterConversionRule(FeetToCentimeterConversionRule) {
        answerService = ref('answerService')
    }

    repeatingQuestionsResetRule(RepeatingQuestionsResetRule) {
        answerService = ref('answerService')
    }

    priorSpousesResetRule(PriorSpousesResetRule) {
        answerService = ref('answerService')
    }

    alienFianceWarningLifeTimeRule(AlienFianceWarningLifeTimeRule) {
        alertService = ref('alertService')
    }

    alienFianceWarningPrevYearRule(AlienFianceWarningPrevYearRule) {
        alertService = ref('alertService')
    }

    inAdmissibilityWarningRule(InAdmissibilityWarningRule) {
        alertService = ref('alertService')
        asyncService = ref('asyncService')
    }

    autoSyncCurrentPhysicalAddressRule(AutoSyncCurrentPhysicalAddressRule) {
        answerService = ref('answerService')
    }

    autoSyncUserProfileRule(AutoSyncUserProfileRule) {
        profileService = ref('profileService')
        packageService = ref('packageService')
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    autoSyncApplicantRule(AutoSyncApplicantRule) {
        applicantService = ref('applicantService')
    }

    areYouStillWorkingAtThisEmployerRule(AreYouStillWorkingAtThisEmployerRule) {
        answerService = ref('answerService')
    }

    kilogramToPoundConversionRule(KilogramToPoundConversionRule) {
        answerService = ref('answerService')
    }

    poundToKilogramConversionRule(PoundToKilogramConversionRule) {
        answerService = ref('answerService')
    }

    employmentHistoryCompletionRule(EmploymentHistoryCompletionRule) {
        alertService = ref('alertService')
        springSecurityService = ref('springSecurityService')
        asyncService = ref('asyncService')
    }

    contactInformationAvailabilityRule(ContactInformationAvailabilityRule) {
        answerService = ref('answerService')
    }

    autoPopulateOtherPeopleWithTiesToUSRule(AutoPopulateOtherPeopleWithTiesToUSRule) {
        answerService = ref('answerService')
    }

    anotherRelativeInForm601AWouldExperienceExtremeHardshipApplicableRule(AnotherRelativeInForm601AWouldExperienceExtremeHardshipApplicableRule) {
        answerService = ref('answerService')
    }

    autoRemovalOfOtherRelativesConsideredByUSCISRule(AutoRemovalOfOtherRelativesConsideredByUSCISRule) {
        answerService = ref('answerService')
    }

    autoSyncApplicantDOBRule(AutoSyncApplicantDOBRule) {
        applicantService = ref('applicantService')
    }

    questionnaireService(QuestionnaireService) {
        springSecurityService = ref('springSecurityService')
        applicantService = ref('applicantService')
        attorneyService = ref('attorneyService')
        organizationService = ref('organizationService')
    }

    criminalConvictionWarningRule(CriminalConvictionWarningRule) {
        alertService = ref('alertService')
        asyncService = ref('asyncService')
    }

    residedAtOtherAddressWarningRule(PreviousPhysicalAddressPopulateRule) {
        answerService = ref('answerService')
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    autoPopulateChildDeSelectionRule(AutoPopulateChildDeSelectionRule) {
        answerService = ref('answerService')
    }

    childrenInformationLifeCycleRule(ChildrenInformationLifeCycleRule) {
        answerService = ref('answerService')
    }

    autoSyncChildFirstName(AutoSyncChildFirstName) {
        answerService = ref('answerService')
    }

    conditionalResidenceStatusWarningRule(ConditionalResidenceStatusWarningRule) {
        alertService = ref('alertService')
        evMailService = ref('evMailService')
        packageService = ref('packageService')
        asyncService = ref('asyncService')
    }

    everBeenArrestedWarningRule(EverBeenArrestedWarningRule) {
        alertService = ref('alertService')
    }

    attorneyWarningRule(AttorneyWarningRule) {
        alertService = ref('alertService')
        asyncService = ref('asyncService')
    }

    autoSyncCitizenshipStatusRule(AutoSyncCitizenshipStatusRule) {
        applicantService = ref('applicantService')
        packageQuestionnaireService = ref('packageQuestionnaireService')
        alertService = ref('alertService')
        packageService = ref('packageService')
    }

    autoSyncApplicantEmailRule(AutoSyncApplicantEmailRule) {
        profileService = ref('profileService')
    }

    beneficiaryMaritalStatusAttorneyActionRule(BeneficiaryMaritalStatusAttorneyActionRule) {
        answerService = ref('answerService')
        alertService = ref('alertService')
        packageQuestionnaireService = ref('packageQuestionnaireService')
        asyncService = ref('asyncService')
    }

    personalInformationCompletionRule(PersonalInformationCompletionRule) {
        sectionCompletionStatusService = ref('sectionCompletionStatusService')
    }

    autoPopulatePhysicalAddressAbroadRule(AutoPopulatePhysicalAddressAbroadRule) {
        answerService = ref('answerService')
    }

    autoPopulateAddressWhereYouIntendToLiveInUSRule(AutoPopulateAddressWhereYouIntendToLiveInUSRule) {
        answerService = ref('answerService')
    }

    autoSyncPhysicalAddressCountrySelectionCompositeRule(AutoSyncPhysicalAddressCountrySelectionCompositeRule) {
        answerService = ref('answerService')
    }

    familyInformationCompletionRule(FamilyInformationCompletionRule) {
        alertService = ref('alertService')
        springSecurityService = ref('springSecurityService')
    }

    autoPopulateParentBirthNameRule(AutoPopulateParentBirthNameRule) {
        answerService = ref('answerService')
    }

    autoPopulateAddressOfChildLiveWithBeneficiaryRule(AutoPopulateAddressOfChildLiveWithBeneficiaryRule) {
        answerService = ref('answerService')
    }

    arrivalDateMilestoneReminderRule(ArrivalDateMilestoneReminderRule) {
        packageReminderService = ref('packageReminderService')
    }

    marriageDateMilestoneReminderRule(MarriageDateMilestoneReminderRule) {
        packageReminderService = ref('packageReminderService')
    }

    sponsorshipRelationshipRule(SponsorshipRelationshipInputSourceRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    relationshipToPetitionerApplicableRule(RelationshipToPetitionerApplicableRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    citizenshipAcquiredRule(CitizenshipAcquiredInputSourceRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    sponsorshipRelationshipWarningRule(SponsorshipRelationshipWarningRule) {
        alertService = ref('alertService')
        packageQuestionnaireService = ref('packageQuestionnaireService')
        asyncService = ref('asyncService')
    }

    birthInformationCountrySelectionRule(BirthInformationCountrySelectionRule) {
        packageService = ref('packageService')
    }

    fianceVisaIntroSectionVisibilityConstraintRule(FianceVisaIntroSectionVisibilityConstraintRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    fianceVisaAlienNumberVisibilityConstraintRule(FianceVisaAlienNumberVisibilityConstraintRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    beneficiaryMaritalStatusInputSourceRule(BeneficiaryMaritalStatusInputSourceRule) {
        packageService = ref('packageService')
    }

    autoSyncParentBirthNameRule(AutoSyncParentBirthNameRule) {
        answerService = ref('answerService')
    }

    relationshipToPetitionerSelectionRule(RelationshipToPetitionerSelectionRule) {
        answerService = ref('answerService')
        packageService = ref('packageService')
        sectionCompletionStatusService = ref('sectionCompletionStatusService')
    }

    introQuestionsForBeneficiaryCompletionRule(IntroQuestionsForBeneficiaryCompletionRule) {
        packageService = ref('packageService')
        sectionCompletionStatusService = ref('sectionCompletionStatusService')
    }

    legalStatusDependentApplicableRule(LegalStatusDependentApplicableRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    addressHistoryCompletionRule(AddressHistoryCompletionRule) {
        packageService = ref('packageService')
        packageQuestionnaireService = ref('packageQuestionnaireService')

    }

    previousPhysicalAddressPopulateRule(PreviousPhysicalAddressPopulateRule) {
        answerService = ref('answerService')
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    previousPhysicalAddressApplicableRule(PreviousPhysicalAddressApplicableRule) {
        packageService = ref('packageService')
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    questionnaireFormVisibilityOnRule(QuestionnaireFormVisibilityOnRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    questionnaireFormVisibilityOffRule(QuestionnaireFormVisibilityOffRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    rqgFormVisibilityOnRule(RQGFormVisibilityOnRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }

    questionnaireFormVisibilityWithIterationOnRule(QuestionnaireFormVisibilityWithIterationOnRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }
    employmentEndDateIterationVisibilityRule(EmploymentEndDateIterationVisibilityRule) {
        packageQuestionnaireService = ref('packageQuestionnaireService')
    }
}
