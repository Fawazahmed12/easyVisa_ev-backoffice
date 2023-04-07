package com.easyvisa

import com.easyvisa.document.DocumentMilestone
import com.easyvisa.document.DocumentNote
import com.easyvisa.dto.EmailDto
import com.easyvisa.dto.MessageResponseDto
import com.easyvisa.dto.PackageResponseDto
import com.easyvisa.enums.*
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.PackageQuestionnaireVersion
import com.easyvisa.questionnaire.SectionCompletionStatus
import com.easyvisa.questionnaire.answering.benefitcategoryfeatures.BaseBenefitCategoryFeature
import com.easyvisa.questionnaire.answering.benefitcategoryfeatures.BenefitCategoryFeaturesFactory
import com.easyvisa.utils.ExceptionUtils
import com.easyvisa.utils.StringUtils
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileDynamic
import org.apache.http.HttpStatus
import org.hibernate.sql.JoinType

import java.time.LocalDate

import static com.easyvisa.enums.EasyVisaSystemMessageType.*
import static com.easyvisa.enums.PackageStatus.*

@SuppressWarnings('FactoryMethodName')
@Transactional
@GrailsCompileStatic
class PackageService {

    ProfileService profileService
    ApplicantService applicantService
    OrganizationService organizationService
    FileService fileService
    EvMailService evMailService
    EmailVariableService emailVariableService
    AlertService alertService
    AccountService accountService
    PermissionsService permissionsService
    PackageQuestionnaireService packageQuestionnaireService
    PackageQuestionnaireVersionService packageQuestionnaireVersionService
    PackageDocumentService packageDocumentService
    SpringSecurityService springSecurityService
    PageRenderer groovyPageRenderer
    SqlService sqlService
    AsyncService asyncService
    ProcessService processService

    PackageResponseDto validateAndCreatePackage(PackageCommand packageCommand) {
        Organization organization = packageCommand.organization
        PackageResponseDto packageResponseDto = create(packageCommand,
                LegalRepresentative.get(packageCommand.representativeId), organization)
        Package aPackage = packageResponseDto.aPackage
        sendOptInRequests(aPackage)
        packageResponseDto
    }

    private void sendOptInRequests(Package aPackage) {
        aPackage.benefits.each {
            ImmigrationBenefit immigrationBenefit = it as ImmigrationBenefit
            if (immigrationBenefit.optIn == ProcessRequestState.PENDING) {
                sendOptInAlertForImmigrationBenefit(aPackage, immigrationBenefit)
            }
        }
        if (aPackage.petitioner && aPackage.petitioner.optIn == ProcessRequestState.PENDING) {
            sendOptInAlertForPetitioner(aPackage, aPackage.petitioner)
        }
    }


    private void validatePackageCategory(Package aPackage) {
        CitizenshipStatus petitionerStatus = aPackage.petitioner?.citizenshipStatus
        //validate petitioner status
        if (aPackage.petitioner && petitionerStatus == CitizenshipStatus.ALIEN) {
            throw ExceptionUtils.createUnProcessableDataException('package.category.validation', null, null,
                    'package.category.validation.alien.petitioner', null,
                    ErrorMessageType.IMMIGRATION_CATEGORY_CONFLICT)
        }
        //validate direct beneficiary categories
        ImmigrationBenefit directBenefit = aPackage.directBenefit
        ImmigrationBenefitCategory directBenefitCategory = directBenefit.category
        if (directBenefit.citizenshipStatus == CitizenshipStatus.U_S_CITIZEN) {
            throw ExceptionUtils.createUnProcessableDataException('package.category.validation', null, null,
                    'package.category.validation.us.citizen.beneficiary', null,
                    ErrorMessageType.IMMIGRATION_CATEGORY_CONFLICT)
        }
        if ((!aPackage.petitioner && directBenefit.citizenshipStatus == CitizenshipStatus.LPR)
        /*|| (aPackage.petitioner && directBenefit.citizenshipStatus == CitizenshipStatus.LPR
        && !ImmigrationBenefitCategory.beneficiaryLprAllowedCategories.contains(directBenefitCategory))*/) {
            throw ExceptionUtils.createUnProcessableDataException('package.category.validation', null, null,
                    'package.category.validation.lpr.citizen.beneficiary', null,
                    ErrorMessageType.IMMIGRATION_CATEGORY_CONFLICT)
        }
        if ((!aPackage.petitioner && directBenefit.citizenshipStatus == CitizenshipStatus.U_S_NATIONAL)) {
            throw ExceptionUtils.createUnProcessableDataException('package.category.validation', null, null,
                    'package.category.validation.us_nation.citizen.beneficiary', null,
                    ErrorMessageType.IMMIGRATION_CATEGORY_CONFLICT)
        }
        if (!directBenefit.citizenshipStatus || directBenefit.citizenshipStatus == CitizenshipStatus.ALIEN) {
            if ((!ImmigrationBenefitCategory.beneficiaryAlienAllowedCategories.contains(directBenefitCategory)
                    && aPackage.petitioner && directBenefitCategory != ImmigrationBenefitCategory.REMOVECOND)
                    || (!ImmigrationBenefitCategory.beneficiaryAlienNoPetitionerAllowedCategories
                    .contains(directBenefitCategory)
                    && !aPackage.petitioner)) {
                throw ExceptionUtils.createUnProcessableDataException('package.category.validation', null, null,
                        'package.category.validation.alien.citizen.beneficiary', null,
                        ErrorMessageType.IMMIGRATION_CATEGORY_CONFLICT)
            }
            if (ImmigrationBenefitCategory.categoriesForUsCitizenPetitioner.contains(directBenefitCategory)
                    && petitionerStatus != CitizenshipStatus.U_S_CITIZEN) {
                throw ExceptionUtils.createUnProcessableDataException('package.category.validation', null, null,
                        'package.category.validation.wrong.petitioner.for.beneficiary',
                        [directBenefitCategory.abbreviation, CitizenshipStatus.U_S_CITIZEN.displayName],
                        ErrorMessageType.IMMIGRATION_CATEGORY_CONFLICT)
            }
            if (ImmigrationBenefitCategory.categoriesForLprPetitioner.contains(directBenefitCategory)
                    && (petitionerStatus != CitizenshipStatus.LPR && petitionerStatus != CitizenshipStatus.U_S_NATIONAL)) {
                throw ExceptionUtils.createUnProcessableDataException('package.category.validation', null, null,
                        'package.category.validation.wrong.petitioner.for.beneficiary',
                        [directBenefitCategory.abbreviation, CitizenshipStatus.LPR.displayName],
                        ErrorMessageType.IMMIGRATION_CATEGORY_CONFLICT)
            }

        }
        //validate derivative beneficiaries categories
        aPackage.derivativeBenefits.each {
            if ((it.citizenshipStatus && it.citizenshipStatus != CitizenshipStatus.ALIEN)
                    || !ImmigrationBenefitCategory.derivativeBeneficiaryAllowedCategories.contains(it.category)) {
                throw ExceptionUtils.createUnProcessableDataException('package.category.validation', null, null,
                        'package.category.validation.wrong.derivative.beneficiary.category', null,
                        ErrorMessageType.IMMIGRATION_CATEGORY_CONFLICT)
            }
            validateParticularDerivativeCategory(directBenefitCategory, it.category, ImmigrationBenefitCategory.F1_A,
                    ImmigrationBenefitCategory.F1_A)
            validateParticularDerivativeCategory(directBenefitCategory, it.category, ImmigrationBenefitCategory.F2_A,
                    ImmigrationBenefitCategory.F2_A)
            validateParticularDerivativeCategory(directBenefitCategory, it.category, ImmigrationBenefitCategory.F3_A,
                    ImmigrationBenefitCategory.F3_A)
            validateParticularDerivativeCategory(directBenefitCategory, it.category, ImmigrationBenefitCategory.F4_A,
                    ImmigrationBenefitCategory.F4_A)
            validateParticularDerivativeCategory(directBenefitCategory, it.category, ImmigrationBenefitCategory.K2K4,
                    ImmigrationBenefitCategory.K1K3)
        }
    }

    private void validateParticularDerivativeCategory(ImmigrationBenefitCategory directBenefitCategory,
                                                      ImmigrationBenefitCategory derivativeCategory,
                                                      ImmigrationBenefitCategory expectedDerivativeCategory,
                                                      ImmigrationBenefitCategory expectedDirectCategory) {
        if (derivativeCategory == expectedDerivativeCategory && directBenefitCategory != expectedDirectCategory) {
            throw ExceptionUtils.createUnProcessableDataException('package.category.validation', null, null,
                    'package.category.validation.wrong.principle.beneficiary.category',
                    [expectedDirectCategory.abbreviation, derivativeCategory.abbreviation],
                    ErrorMessageType.IMMIGRATION_CATEGORY_CONFLICT)
        }
    }

    Package createNewApplicantWithBenefit(Package aPackage, ApplicantCommand applicantCommand, Long idx = 0) {
        Applicant applicant = applicantCommand.id ? Applicant.get(applicantCommand.id) : profileService.createApplicant(applicantCommand, aPackage)
        validateApplicantUser(applicantCommand, applicant, aPackage)
        ImmigrationBenefit benefit = new ImmigrationBenefit(applicant: applicant,
                fee: applicantCommand.fee,
                direct: applicantCommand.direct,
                relationshipToPrincipal: applicantCommand.relationshipToPrincipal,
                category: applicantCommand.benefitCategory,
                citizenshipStatus: applicantCommand.citizenshipStatus,
                sortPosition: idx)
        if (applicant.user) {
            benefit.optIn = ProcessRequestState.PENDING
        } else {
            benefit.optIn = ProcessRequestState.ACCEPTED
        }
        aPackage.benefits.add(benefit)
        aPackage
    }

    /**Validate if applicant can belong to a package,
     *
     * @param applicantCommand
     * @param applicant
     * @param aPackage
     */
    @CompileDynamic
    private void validateApplicantUser(ApplicantCommand applicantCommand, Applicant applicant, Package aPackage) {
        Boolean isNonUserApplicant = applicantCommand?.id && !applicant?.user
        Boolean isApplicantInvalid = isNonUserApplicant
        if (aPackage) {
            List<Package> applicantCurrentPackages = Package.createCriteria().listDistinct {
                createAlias('petitioner', 'p', JoinType.LEFT_OUTER_JOIN)
                createAlias('benefits', 'b')
                or {
                    eq('p.applicant', applicant)
                    eq('b.applicant', applicant)
                }
            } as List<Package>
            if (applicantCurrentPackages == [aPackage]) {
                isApplicantInvalid = false
            }
        }
        if (isApplicantInvalid) {
            log.warn("Hack attempt to provide non registered applicant [${applicant?.id}] to package [${aPackage?.id}] by user " +
                    "[${springSecurityService.currentUserId}]")
            throw ExceptionUtils.createPackageAccessDeniedException()
        }
    }

    void sendOptInAlertForPetitioner(Package aPackage, Petitioner petitioner) {
        PackageOptInForPetitionerRequest packageOptInRequest = new PackageOptInForPetitionerRequest(aPackage: aPackage, petitioner: petitioner)
        packageOptInRequest.requestedBy = aPackage.attorney.profile
        packageOptInRequest.save(failOnError: true)

        alertService.createProcessRequestAlert(packageOptInRequest, PACKAGE_OPTIN_REQUEST, petitioner.applicant.user, aPackage.attorney.profile.fullName)
    }

    void sendOptInAlertForImmigrationBenefit(Package aPackage, ImmigrationBenefit immigrationBenefit) {
        PackageOptInForImmigrationBenefitRequest packageOptInRequest = new PackageOptInForImmigrationBenefitRequest(aPackage: aPackage, immigrationBenefit: immigrationBenefit)
        packageOptInRequest.requestedBy = aPackage.attorney.profile
        packageOptInRequest.save(failOnError: true)
        alertService.createProcessRequestAlert(packageOptInRequest, PACKAGE_OPTIN_REQUEST, immigrationBenefit.applicant.user, aPackage.attorney.profile.fullName)
    }

    @CompileDynamic
    Boolean isApplicantInSpecificPackageStatus(PackageStatus packageStatus, Applicant applicant,
                                               Package skipPackage = null) {
        Package.createCriteria().count {
            createAlias('petitioner', 'p', JoinType.LEFT_OUTER_JOIN)
            createAlias('benefits', 'b')
            or {
                eq('p.applicant', applicant)
                eq('b.applicant', applicant)
            }
            eq('status', packageStatus)
            if (skipPackage?.id != null) {
                ne('id', skipPackage.id)
            }
        } > 0
    }

    Package createPackageAssignee(Package aPackage, LegalRepresentative assignee, Organization organization,
                                  PackageAssignmentStatus status) {
        PackageAssignee assignment = new PackageAssignee(aPackage: aPackage, representative: assignee,
                organization: organization, status: status, startDate: new Date())
        aPackage.addToAssignees(assignment)
        aPackage
    }


    Package addEasyVisaId(Package aPackage) {
        aPackage.easyVisaId = "P${StringUtils.padEasyVisaId(sqlService.getNextSequenceId('package_ev_id_seq'))}"
        aPackage
    }


    PackageResponseDto create(PackageCommand packageCommand, LegalRepresentative attorney, Organization organization) {
        permissionsService.assertHasPosition(organization, EmployeePosition.attorneyPositions, attorney.user)

        Package aPackage = new Package(status: LEAD, attorney: attorney, organization: organization,
                owed: packageCommand.owed, questionnaireCompletedPercentage: 0, documentCompletedPercentage: 0)

        //adding easyVisaId
        aPackage = addEasyVisaId(aPackage)
        List<ApplicantCommand> applicants = packageCommand.applicants
        //getting petitioners
        List<ApplicantCommand> petitioners = packageCommand.petitioners
        applicants.removeAll(petitioners)
        validatePackageCommandParams(petitioners, applicants)
        if (petitioners) {
            ApplicantCommand petitioner = petitioners.first()
            aPackage.petitioner = profileService.createPetitioner(petitioner, aPackage)
            validateApplicantUser(petitioner, aPackage.petitioner.applicant, aPackage)
        }
        //creating beneficiaries
        applicants.eachWithIndex { ApplicantCommand applicantCommand, Long idx ->
            if (!applicantCommand.benefitCategory) {
                throw ExceptionUtils.createUnProcessableDataException('package.benefit.category.not.provided')
            }
            aPackage = createNewApplicantWithBenefit(aPackage, applicantCommand, idx)
        }
        //invite client - petitioner or principle beneficiary of no-petitioner package
        if (!aPackage.client.user) {
            aPackage.client.inviteApplicant = true
        }
        aPackage = createPackageAssignee(aPackage, attorney, organization, PackageAssignmentStatus.ACTIVE)
        updatePackageTitle(aPackage)
        PackageResponseDto result = validatePackage(aPackage)
        aPackage.save(failOnError: true)
        result
    }

    private PackageResponseDto validatePackage(Package aPackage) {
        validatePackageCategory(aPackage)
        validateApplicantsOpenBlockedPackages(aPackage)
    }

    private PackageResponseDto validateApplicantsOpenBlockedPackages(Package aPackage) {
        PackageResponseDto result = new PackageResponseDto(aPackage: aPackage)
        if ([OPEN, LEAD].contains(aPackage.status)) {
            Boolean showMessage = Boolean.FALSE
            aPackage.applicants.each {
                showMessage = showMessage || validateApplicantOpenBlockedPackage(it, aPackage)
            }
            if (showMessage) {
                result.messages << new MessageResponseDto(errorMessageType: ErrorMessageType.BLOCKED_OPEN_PACKAGES,
                        messageCode: 'package.applicant.in.blocked.open.package')
            }
        }
        result
    }

    private Boolean validateApplicantOpenBlockedPackage(Applicant applicant, Package aPackage) {
        Boolean result = isApplicantInSpecificPackageStatus(OPEN, applicant, aPackage)
        result || validateApplicantInBlockedPackage(applicant, aPackage)
    }

    private Boolean validateApplicantInBlockedPackage(Applicant applicant, Package aPackage,
                                                      int errorCode = HttpStatus.SC_UNPROCESSABLE_ENTITY,
                                                      ErrorMessageType errorMessageType = ErrorMessageType.BLOCKED_OPEN_PACKAGES) {
        if (isApplicantInSpecificPackageStatus(BLOCKED, applicant, aPackage)) {
            if (aPackage.status == OPEN) {
                throw new EasyVisaException(errorCode: errorCode, errorMessageType: errorMessageType,
                        errorMessageCode: 'package.applicant.in.blocked.open.package')
            }
            return Boolean.TRUE
        }
        Boolean.FALSE
    }

    private void validatePackageCommandParams(List<ApplicantCommand> petitioners, List<ApplicantCommand> applicants) {
        if (petitioners.size() > 1) {
            throw ExceptionUtils.createUnProcessableDataException('package.two.petitioners')
        }
        if (petitioners.size() == 0 && applicants.size() > 1) {
            throw ExceptionUtils.createUnProcessableDataException('package.two.self.petitioners')
        }
        if (petitioners && petitioners[0].citizenshipStatus == null) {
            throw ExceptionUtils.createUnProcessableDataException('package.category.validation.petitioner.no.' +
                    'citizenship.status')
        }
    }

    ImmigrationBenefitCategory getDirectBenefitCategory(Long packageId) {
        Package packageObj = Package.get(packageId)
        ImmigrationBenefit directBenefit = packageObj.directBenefit
        directBenefit.category
    }

    BaseBenefitCategoryFeature getBenefitCategoryFeature(Long packageId) {
        ImmigrationBenefitCategory immigrationBenefitCategory = getDirectBenefitCategory(packageId)
        BenefitCategoryFeaturesFactory
                .getBenefitCategoryFeatures(immigrationBenefitCategory)
    }

    PackageResponseDto movePackageToOpen(PackageResponseDto packageResponseDto) {
        Package aPackage = packageResponseDto.aPackage
        aPackage.status = OPEN
        aPackage.opened = new Date()
        toOpenValidation(aPackage)
        PackageResponseDto result = chargePackage(packageResponseDto)
        updateAttorneyRevenue(aPackage)
        sendAllApplicantsInvite(aPackage)
        result
    }

    private void addAttorneyRevenue(BigDecimal revenue, Package aPackage, ImmigrationBenefit ib) {
        (new LegalRepresentativeRevenue(revenue: revenue, aPackage: aPackage, organization: aPackage.organization, attorney: aPackage.attorney,
                memo: "${ib.category}: ${ib.applicant.profile.fullName} (${ib.applicant.profile.easyVisaId}) from ${aPackage.easyVisaId}: ${aPackage.title}"))
                .save(failOnError: true)
    }

    void prepareQuestionnaire(Long packageId, LocalDate currentDate, Closure completionHandler = null) {
        this.generateAsyncQuestionnaireData(packageId, currentDate, completionHandler)
    }

    @CompileDynamic
    void generateAsyncQuestionnaireData(Long packageId, LocalDate currentDate, Closure completionHandler = null) {
        Package aPackage = Package.get(packageId)
        if (aPackage.status != OPEN || [QuestionnaireSyncStatus.IN_PROGRESS, QuestionnaireSyncStatus.COMPLETED].contains(aPackage.questionnaireSyncStatus)) {
            if (completionHandler) {
                completionHandler(packageId)
            }
            log.info("QuestionnaireSync no need for the Package - ${packageId}")
            return
        }

        asyncService.runAsync({
            Package.withNewTransaction {
                Package.executeUpdate("update Package set questionnaireSyncStatus = :status where id = :id",
                        [status: QuestionnaireSyncStatus.IN_PROGRESS, id: packageId])
            }
            try {
                Package.withNewTransaction {
                    generateQuestionnaireData(Package.get(packageId), currentDate)
                    if (completionHandler) {
                        completionHandler(packageId)
                    }
                    log.info("QuestionnaireSync Completed for the Package - ${packageId}")
                }
            } catch (Exception e) {
                Package.withNewTransaction {
                    Package.executeUpdate("update Package set questionnaireSyncStatus = :status where id = :id",
                            [status: QuestionnaireSyncStatus.FAILED, id: packageId])
                }
                throw e
            }
        }, "Generate Package [${packageId}] initial data for Questionnaire", Boolean.TRUE)
    }

    private void toOpenValidation(Package aPackage) {
        //check applicant in blocked package
        aPackage.applicants.each {
            validateApplicantInBlockedPackage(it, aPackage, HttpStatus.SC_LOCKED,
                    ErrorMessageType.MEMBERS_OF_BLOCKED_PACKAGE)
        }
        //check not accepted optIn
        if (aPackage.petitioner && aPackage.petitioner.optIn != ProcessRequestState.ACCEPTED) {
            throw ExceptionUtils.createLockedException('package.applicant.not.opt.in',
                    ErrorMessageType.MEMBERS_WITH_PENDING_OR_DENY_STATUS)
        }
        aPackage.benefits.each {
            if ((it as ImmigrationBenefit).optIn != ProcessRequestState.ACCEPTED) {
                throw ExceptionUtils.createLockedException('package.applicant.not.opt.in',
                        ErrorMessageType.MEMBERS_WITH_PENDING_OR_DENY_STATUS)
            }
        }
    }

    private void generateQuestionnaireData(Package aPackage, LocalDate currentDate) {
        packageQuestionnaireVersionService.createNewVersion(aPackage)
        packageQuestionnaireService.syncAndCopyQuestionnaireAnswers(aPackage, currentDate)
        aPackage.questionnaireSyncStatus = QuestionnaireSyncStatus.COMPLETED
        this.savePackage(aPackage)
    }

    void sendAllApplicantsInvite(Package aPackage) {
        aPackage.beneficiaries.each { Applicant applicant ->
            if (applicant.inviteApplicant) {
                sendApplicantInviteEmail(aPackage, applicant)
            }
        }
        if (aPackage.petitioner) {
            sendApplicantInviteEmail(aPackage, aPackage.petitioner.applicant)
        }
    }

    private void sendAllApplicantsInvitationReminder(Long packageId) {
        Package aPackage = Package.get(packageId)
        aPackage.beneficiaries.each { Applicant applicant ->
            if (applicant.inviteApplicant && applicant.profile.email && !applicant.user) {
                sendApplicantReminderInvitationEmail(aPackage, applicant)
            }
        }
        if (aPackage.petitioner && !aPackage.petitioner.applicant.user) {
            sendApplicantReminderInvitationEmail(aPackage, aPackage.petitioner.applicant)
        }
    }

    private void sendApplicantReminderInvitationEmail(Package aPackage, Applicant applicant) {
        RegistrationCode registrationCode = createRegistrationToken(applicant)
        Map params = emailVariableService.addApplicantRegistrationCode([:], registrationCode)

        String body = groovyPageRenderer.render(template: '/email/reminderApplicantToRegister',
                model: [applicant       : applicant, attorney: aPackage.attorney,
                        registrationLink: emailVariableService.registerApplicantLink(params)])
        String repEmail = aPackage.attorney.profile.email
        EmailDto emailDto = evMailService.buildEasyVisaEmailDto(applicant.profile.email,
                'Your Immigration Package on EasyVisa is OPEN!', body, null, repEmail)
        evMailService.sendEmail(emailDto)
    }

    PackageResponseDto maybeChangePackageStatus(Package aPackage, PackageStatus oldStatus, PackageStatus newStatus) {
        if ([TRANSFERRED, DELETED].contains(oldStatus)) {
            throw ExceptionUtils.createUnProcessableDataException('package.transferred.package.changes')
        }
        if ([TRANSFERRED, DELETED].contains(newStatus)) {
            throw ExceptionUtils.createUnProcessableDataException('package.status.cant.be.set.to.transferred')
        }
        PackageResponseDto result = new PackageResponseDto(aPackage: aPackage)
        if (oldStatus == newStatus) {
            aPackage
        } else if (newStatus == LEAD) {
            throw ExceptionUtils.createUnProcessableDataException('package.status.cant.be.set.to.lead')
        } else if ([oldStatus, newStatus] == [LEAD, OPEN]) {
            result = movePackageToOpen(result)
        } else if (newStatus == CLOSED && oldStatus != CLOSED) {
            aPackage.status = CLOSED
            aPackage.blockedType = null
            aPackage.closed = new Date()
        } else if (newStatus == BLOCKED && oldStatus != BLOCKED) {
            movePackageToBlocked(aPackage)
        } else {
            aPackage.status = newStatus
            if (newStatus == OPEN) {
                aPackage.blockedType = null
                result = chargePackage(result)
            }
        }
        aPackage.save(failOnError: true)
        result
    }

    void movePackageToBlocked(Package aPackage, PackageBlockedType packageBlockedType = PackageBlockedType.PAYMENT) {
        aPackage.status = BLOCKED
        aPackage.blockedType = packageBlockedType
        aPackage.save(failOnError: true)

        List<ApplicantPackageTransferRequest> requests = ApplicantPackageTransferRequest.findAllByAPackageAndState(aPackage, ProcessRequestState.PENDING)
        if (requests) {
            requests.each {
                processService.declineRequest(it)
            }
        }
    }

    void movePackageToBlockedNotification(Package aPackage, NotificationType notType = NotificationType.PAYMENT,
                                          PackageBlockedType packageBlockedType = PackageBlockedType.PAYMENT) {
        movePackageToBlocked(aPackage, packageBlockedType)
        sendEmailAsync({
            sendBlockedEmail(aPackage.id, notType)
        }, "Send blocked email for package [${aPackage.id}]")
    }

    @CompileDynamic
    private void sendBlockedEmail(Long aPackageId, NotificationType notType) {
        Package aPackage = Package.get(aPackageId)
        EmailTemplate emailTemplate = EmailTemplate.createCriteria().get {
            eq('attorney', aPackage.attorney)
            preference {
                eq('type', notType)
                eq('preference', Boolean.TRUE)
            }
        } as EmailTemplate
        String body, subject
        if (emailTemplate) {
            body = emailTemplate.htmlContent
            subject = emailTemplate.subject
        } else {
            EmailTemplateType emailTemplateType = EmailTemplateType.findByNotificationType(notType)
            body = alertService.renderTemplate(emailTemplateType.path, [:]).replaceAll("\n", '<br/>')
            subject = emailTemplateType.subject
        }
        EasyVisaSystemMessageType messageType = findByNotificationType(notType)
        PackageReminder reminder = aPackage.getPackageReminder(notType) ?: new PackageReminder(aPackage: aPackage, notificationType: notType)
        Map params = emailVariableService.addLegalRepresentative([:], aPackage.attorney)
        params = emailVariableService.addPackage(params, aPackage)
        body = evMailService.evaluateTemplate(body, params)
        aPackage.applicants*.user.each {
            alertService.createAlert(messageType, it, aPackage.attorney.profile.name, body, subject)
        }
        reminder.lastSent = new Date()
        reminder.save(failOnError: true)
    }

    private PackageResponseDto chargePackage(PackageResponseDto packageResponseDto) {
        PackageResponseDto result = packageResponseDto
        Package aPackage = packageResponseDto.aPackage
        if (aPackage.status == OPEN && !permissionsService.isBlessed(aPackage.attorney.user)) {
            if (!organizationService.doesEmployeeBelongToOrganization(aPackage.attorney.id, aPackage.organization.id)) {
                throw ExceptionUtils.createUnProcessableDataException('package.attorney.is.not.active.in.organization')
            }
            accountService.packageCharge(result)
        }
        result
    }

    ErrorMessageType updateApplicants(Package aPackage, List<ApplicantCommand> applicantCommands) {
        ErrorMessageType infoType = null
        List<Long> applicantIdsToRetain = applicantCommands.collect { it.id }.grep { it }
        def newApplicants = applicantCommands.findAll {
            !(it.id) || (it.id && !aPackage.getBenefitForApplicantId(it.id))
        }
        def applicantsToDelete = aPackage.beneficiaries.findAll { !(applicantIdsToRetain.contains(it.id)) }

        applicantsToDelete.each {
            applicantService.removeApplicantFromPackage(it, aPackage)
        }

        applicantCommands.eachWithIndex { ApplicantCommand applicantCommand, Long idx ->
            if (newApplicants.contains(applicantCommand)) {
                //invite client - petitioner or principle beneficiary of no-petitioner package
                Boolean canInviteApplicant = (aPackage.petitioner == null && applicantCommands.size() == 1) ? true : applicantCommand.inviteApplicant
                applicantCommand.inviteApplicant = canInviteApplicant
                aPackage = createNewApplicantWithBenefit(aPackage, applicantCommand, idx)
                if (LEAD == aPackage.status && applicantCommand.profile.id) {
                    infoType = ErrorMessageType.REMINDER_APPLICANT_PERMISSION
                }
            } else {
                Applicant existingApplicant = Applicant.get(applicantCommand.id)
                if (LEAD == aPackage.status && existingApplicant.user && !existingApplicant.inviteApplicant
                        && applicantCommand.inviteApplicant) {
                    infoType = ErrorMessageType.REMINDER_APPLICANT_PERMISSION
                }
                ImmigrationBenefit benefit = aPackage.getBenefitForApplicant(existingApplicant)
                benefit.fee = applicantCommand.fee
                if (benefit.category != applicantCommand.benefitCategory) {
                    benefit.paid = Boolean.FALSE
                }
                benefit.category = applicantCommand.benefitCategory
                benefit.direct = applicantCommand.direct
                benefit.relationshipToPrincipal = applicantCommand.relationshipToPrincipal
                benefit.citizenshipStatus = applicantCommand.citizenshipStatus
                Applicant applicant = profileService.updateApplicant(existingApplicant, applicantCommand, aPackage)
                benefit.applicant = applicant
                if (existingApplicant.id != applicant.id && applicant.user) {
                    benefit.optIn = ProcessRequestState.PENDING
                }
                benefit.sortPosition = idx
                benefit.save(failOnError: true)
                applicant.save(failOnError: true)
            }
        }
        aPackage.save(failOnError: true)
        infoType
    }

    /**
     * Update just the Owed Amount
     * @param aPackage
     * @param changes
     * @return Package in PackageResponseDto to render updated owed Amount
     */
    PackageResponseDto updateOwedAmount(Package aPackage, AmountOwedCommand changes) {
        // Setting amount Owed to 0 in case we do not get a value from the UI
        // If the user has gone through the trouble of opening the form, removing the value
        // and saving the form, the intention most likely is to set the value to 0.

        aPackage.owed = (changes.owed != null) ? changes.owed : (0 as BigDecimal)
        aPackage.save(failOnError: true, flush: true)
        PackageResponseDto result = new PackageResponseDto(aPackage: aPackage)
        result
    }

    PackageResponseDto updatePackage(Package aPackage, PackageCommand changes) {
        updatePackageValidation(aPackage)
        List<Applicant> existingApplicants = aPackage.beneficiaries
        Petitioner existingPetitioner = aPackage.petitioner

        List<ApplicantCommand> applicants = changes.applicants
        List<ApplicantCommand> petitioners = changes.petitioners
        applicants.removeAll(petitioners)
        validatePackageCommandParams(petitioners, applicants)
        ApplicantCommand petitionerCommand = petitioners ? petitioners.first() : null
        Applicant petitionerApplicantOld = aPackage.petitioner?.applicant
        Petitioner petitioner = applicantService.updatePetitioner(aPackage.petitioner, petitionerCommand, aPackage)
        Boolean samePetitioner = petitioner?.applicant?.id == petitionerApplicantOld?.id
        ErrorMessageType infoType = checkInvitationReminder(aPackage, samePetitioner, applicants)
        if (LEAD == aPackage.status && petitioner?.applicant?.user && !samePetitioner) {
            infoType = ErrorMessageType.REMINDER_APPLICANT_PERMISSION
        }
        aPackage.petitioner = petitioner
        if (petitionerApplicantOld?.id != aPackage.petitioner?.applicant?.id) {
            validateApplicantUser(petitionerCommand, aPackage.petitioner?.applicant, aPackage)
        }
        ErrorMessageType infoTypeApplicants = updateApplicants(aPackage, applicants)
        infoType = infoTypeApplicants ?: infoType
        aPackage.owed = changes.owed ?: aPackage.owed
        updatePackageTitle(aPackage)
        PackageResponseDto result = validatePackage(aPackage)
        //invite client - petitioner or principle beneficiary of no-petitioner package
        if (!aPackage.client.user) {
            aPackage.client.inviteApplicant = true
        }
        aPackage.save(failOnError: true, flush: true)
        if (aPackage.status == OPEN) {
            if (aPackage.organization != organizationService.blessedOrganization) {
                accountService.packageCharge(result)
            }
            if (ErrorMessageType.REMINDER_APPLICANT_INVITATION == infoType && !changes.skipReminders) {
                sendEmailAsync({
                    sendAllApplicantsInvitationReminder(aPackage.id)
                }, "Send applicant invitation reminder for Package [${aPackage.id}]")
            }
        }
        if (infoType) {
            //currently additional info can be one, since existing checks are for different package statuses
            result.messages << new MessageResponseDto(errorMessageType: infoType)
        }
        sendOptin(aPackage, existingApplicants, existingPetitioner)
        updateAttorneyRevenue(aPackage)
        if (changes.skipReminders) {
            result.messages = []
        }
        result
    }

    private void updatePackageValidation(Package aPackage) {
        if (CLOSED == aPackage.status) {
            throw ExceptionUtils.createAccessDeniedException('package.closed.package.changes')
        }
        if (TRANSFERRED == aPackage.status) {
            throw ExceptionUtils.createAccessDeniedException('package.transferred.package.changes')
        }
    }

    private void sendOptin(Package aPackage, List<Applicant> existingApplicants, Petitioner existingPetitioner) {
        aPackage.benefits.each {
            ImmigrationBenefit immigrationBenefit = it as ImmigrationBenefit
            if (!existingApplicants.contains(immigrationBenefit.applicant) && immigrationBenefit.optIn == ProcessRequestState.PENDING) {
                sendOptInAlertForImmigrationBenefit(aPackage, immigrationBenefit)
            }
        }
        if (aPackage.petitioner && existingPetitioner && existingPetitioner.applicant.id != aPackage.petitioner.applicant.id && aPackage.petitioner.optIn == ProcessRequestState.PENDING) {
            sendOptInAlertForPetitioner(aPackage, aPackage.petitioner)
        }
    }

    private void updateAttorneyRevenue(Package aPackage) {
        if (aPackage.status != LEAD) {
            List<LegalRepresentativeRevenue> revenues =
                    LegalRepresentativeRevenue.findAllByAttorneyAndAPackageAndOrganization(aPackage.attorney, aPackage, aPackage.organization)
            aPackage.benefits.each {
                Applicant applicant = it.applicant
                LegalRepresentativeRevenue revenue = revenues.find { it.memo.contains(applicant.profile.easyVisaId) }
                if (revenue) {
                    revenue.revenue = it.fee
                    revenue.save(failOnError: true)
                } else {
                    addAttorneyRevenue(it.fee, aPackage, it)
                }
            }
        }
    }

    private ErrorMessageType checkInvitationReminder(Package aPackage, Boolean samePetitioner,
                                                     List<ApplicantCommand> applicants) {
        ErrorMessageType result = null
        if (OPEN == aPackage.status && samePetitioner && applicants.size() == aPackage.benefits.size()) {
            Boolean noChanges = Boolean.TRUE
            applicants.each {
                if (!aPackage.getBenefitForApplicantId(it.profile.id)) {
                    noChanges = Boolean.FALSE
                }
            }
            if (noChanges) {
                result = ErrorMessageType.REMINDER_APPLICANT_INVITATION
            }
        }
        result
    }

    void sendWelcomeEmail(Package aPackage) {
        if (![LEAD, OPEN].contains(aPackage.status)) {
            throw ExceptionUtils.createUnProcessableDataException('package.transferred.welcome.email')
        }
        Email welComeEmail = Email.findByAPackageAndTemplateType(aPackage, EmailTemplateType.NEW_CLIENT)
        sendEmailAsync({
            sendWelcome(welComeEmail, aPackage.id)
        }, "Send welcome email for Package [${aPackage.id}]")
    }

    private void sendWelcome(Email welComeEmail, Long packageId) {
        Package aPackage = Package.get(packageId)
        Map params = evMailService.buildPackageEmailParams(aPackage)
        aPackage.applicants.findAll { it.inviteApplicant && it.profile.email }.each {
            emailVariableService.addApplicant(params, it)
            String mailContent
            if (welComeEmail) {
                mailContent = evMailService.evaluateTemplate(welComeEmail.htmlContent, params)
            } else {
                String template = StringUtils.textToHTML(evMailService.generateEmailContent(EmailTemplateType.NEW_CLIENT, params).content as String)
                mailContent = evMailService.evaluateTemplate(template, params)
            }

            String subject = welComeEmail?.subject ?: EmailTemplateType.NEW_CLIENT.subject
            subject = evMailService.evaluateTemplate(subject, params)
            evMailService.sendEmail(buildPackageEmailDto(it.profile.email, subject, mailContent, aPackage))
        }
        aPackage.welcomeEmailSentOn = new Date()
        aPackage.save(failOnError: true)
    }

    void sendPackageUpdatedEmail(Package aPackage) {
        Email updatedPackageEmail = Email.findByAPackageAndTemplateType(aPackage, EmailTemplateType.UPDATED_CLIENT)
        if (updatedPackageEmail) {
            sendEmailAsync({
                sendUpdateEmail(updatedPackageEmail, aPackage.id)
            }, "Send package update email for Package [${aPackage.id}]")
        } else {
            throw ExceptionUtils.createUnProcessableDataException('email.not.defined.for.package')
        }
    }

    private void sendUpdateEmail(Email updatedPackageEmail, Long packageId) {
        Package aPackage = Package.get(packageId)
        Map params = evMailService.buildPackageEmailParams(aPackage)
        String mailContent = evMailService.evaluateTemplate(updatedPackageEmail.htmlContent, params)
        String subject = evMailService.evaluateTemplate(updatedPackageEmail.subject, params)
        evMailService.sendEmail(buildPackageEmailDto(aPackage.client.profile.email, subject, mailContent, aPackage))
    }

    RegistrationCode createRegistrationToken(Applicant applicant) {
        new RegistrationCode(easyVisaId: applicant.profile.easyVisaId).save(failOnError: true)
    }

    void sendApplicantInviteEmail(Package aPackage, Applicant applicant) {
        if (aPackage.status != OPEN) {
            throw ExceptionUtils.createUnProcessableDataException('package.not.open.for.email')
        }
        if (aPackage.doesUserBelongToPackage(applicant)
                && (applicant.inviteApplicant || aPackage.petitioner?.applicant == applicant)) {
            if (!applicant.user) {
                sendEmailAsync({
                    sendApplicantInvite(applicant.id, aPackage.id)
                }, "Send invite email to Applicant [${applicant.id}] of Package [$aPackage.id]")
            } else {
                log.warn("Applicant ${applicant.id} is already registered")
            }
        } else {
            throw ExceptionUtils.createUnProcessableDataException('applicant.not.found.for.package')
        }
    }

    private void sendApplicantInvite(Long applicantId, Long packageId) {
        Applicant applicant = Applicant.get(applicantId)
        Package aPackage = Package.get(packageId)

        String content
        String subject

        RegistrationCode registrationCode = createRegistrationToken(applicant)
        Map params = evMailService.buildPackageEmailParams(aPackage)
        params = emailVariableService.addApplicant(params, applicant)
        params = emailVariableService.addApplicantRegistrationCode(params, registrationCode)

        Email email = Email.findByAPackageAndTemplateType(aPackage, EmailTemplateType.INVITE_APPLICANT)
        if (email) {
            content = StringUtils.textToHTML(email.content)
            subject = email.subject
        } else {
            Map emailInfo = evMailService.generateEmailContent(EmailTemplateType.INVITE_APPLICANT, params)
            content = emailInfo.content
            subject = emailInfo.subject
        }
        if (content) {
            String mailContent = evMailService.evaluateTemplate(content, params)
            evMailService.sendEmail(buildPackageEmailDto(applicant.profile.email, subject, mailContent,
                    aPackage))
        }
    }

    EmailDto buildPackageEmailDto(String to, String subject, String body, Package aPackage) {
        EmailDto emailDto = new EmailDto()
        String repEmail = aPackage.attorney.profile.email
        emailDto.with {
            toEmail = to
            fromName = evMailService.emailFromName
            fromEmail = evMailService.emailFromEmail
            replyTo = repEmail
            emailSubject = subject
            emailBody = body
            attachment = fileService.getPackageRetainer(aPackage)
            fileName = aPackage.retainerAgreement?.originalName
        }
        emailDto
    }

    Long deletePackage(Package aPackage) {
        this.deletePackageDependents(aPackage)

        Petitioner petitioner = aPackage.petitioner
        PackageStatus packageStatus = aPackage.status
        List<Applicant> applicants = aPackage.beneficiaries
        applicants.each {
            applicantService.removeApplicantFromPackage(it, aPackage)
        }

        PackageAssignee.executeUpdate('delete from PackageAssignee where aPackage = :pac', [pac: aPackage])

        aPackage.delete(failOnError: true, flush: true)
        applicantService.deletePetitioner(petitioner, aPackage)
        aPackage.id
    }

    List<Long> deleteLeads(Organization organization, Date startDate, Date endDate) {
        List<Package> packagesToDelete = Package.createCriteria().list() {
            or {
                between('lastActiveOn', startDate, endDate)
                between('lastUpdated', startDate, endDate)
            }
            eq('organization', organization)
            eq('status', LEAD)
        } as List<Package>
        deleteLeadPackages(packagesToDelete)
    }

    List<Long> deleteLeadPackages(List<Package> packagesToDelete) {
        List<Package> nonLeadPackages = packagesToDelete.findAll() { it.status != LEAD }
        if (nonLeadPackages) {
            throw ExceptionUtils.createUnProcessableDataException('package.status.cant.be.deleted', null,
                    [nonLeadPackages.collect { it.id }.join(', ')])
        }
        packagesToDelete.collect {
            deletePackage(it)
        }
    }

    List<Long> deleteTransferredPackages(List<Package> packagesToDelete, Organization org) {
        List<Package> nonTransferredPackages = packagesToDelete.findAll() { it.status != TRANSFERRED }
        if (nonTransferredPackages) {
            throw ExceptionUtils.createUnProcessableDataException('package.status.non.transferred.cant.be.deleted', null,
                    [nonTransferredPackages.collect { it.id }.join(', ')])
        }
        List<Organization> orgs = packagesToDelete.collect { it.organization }
        if (orgs.size() > 1 || orgs.get(0).id != org.id) {
            throw ExceptionUtils.createUnProcessableDataException('package.incorrect.organization.provided')
        }
        packagesToDelete.collect {
            deleteTransferredPackage(it)
        }
    }

    Long deleteTransferredPackage(Package aPackage) {
        aPackage.questionnaireSyncStatus = null
        aPackage.status = DELETED
        aPackage.save(failOnError: true)
        packageQuestionnaireService.deletePackageAnswers(aPackage)
        packageDocumentService.deleteDocumentPortalData(aPackage)
        fileService.deleteRetainer(aPackage, Boolean.TRUE)
        aPackage.id
    }

    @CompileDynamic
    private void deletePackageDependents(Package aPackage) {
        Email.findAllByAPackage(aPackage).each {
            it.delete(failOnError: true)
        }
        Warning.findAllByAPackage(aPackage).each {
            it.delete(failOnError: true)
        }
        Review.findAllByAPackage(aPackage).each {
            it.delete(failOnError: true)
        }
        PackageReminder.findAllByAPackage(aPackage).each {
            it.delete(failOnError: true)
        }
        DocumentMilestone.findAllByAPackage(aPackage).each {
            it.delete(failOnError: true)
        }
        DocumentNote.findAllByAPackage(aPackage).each {
            it.delete(failOnError: true)
        }
        if (aPackage.retainerAgreement) {
            Long fileId = aPackage.retainerAgreement.id
            aPackage.retainerAgreement = null
            EasyVisaFile file = EasyVisaFile.get(fileId)
            if (file) {
                file.delete(failOnError: true)
            }
        }
        PackageQuestionnaireVersion.findAllByAPackage(aPackage).each {
            it.delete(failOnError: true)
        }
        Answer.findAllByPackageId(aPackage.id).each {
            it.delete(failOnError: true)
        }
        SectionCompletionStatus.findAllByPackageId(aPackage.id).each {
            it.delete(failOnError: true)
        }

        sqlService.deletePackageTransferRequests(aPackage.id)
    }

/**
 * Validates package transfer parameters and transfer a package.
 * If it in the same org it happens immediately.
 * Otherwise new Legal Representative show apply the transfer request
 * @param packages packages to transfer
 * @param representative new Legal Representative to transfer
 * @param requestedBy requested employee.
 * @param toOrg new Organization to transfer
 */
    void validateAndTransferPackages(List<Package> packages, LegalRepresentative representative, Employee requestedBy,
                                     Organization toOrg) {
        Set<Organization> packageOrganizations = packages*.organization as Set
        Set<LegalRepresentative> packageRepresentatives = packages*.attorney as Set
        if (packageOrganizations.size() != 1) {
            throw ExceptionUtils.createUnProcessableDataException('packages.transfer.not.in.same.organization')
        }
        if (packageRepresentatives.size() != 1) {
            throw ExceptionUtils.createUnProcessableDataException('packages.transfer.not.of.same.representative')
        }
        Organization fromOrg = packageOrganizations.first()
        permissionsService.validateEmployeeNonTraineePosition(requestedBy.user, fromOrg)
        permissionsService.assertHasPosition(toOrg, [EmployeePosition.ATTORNEY, EmployeePosition.PARTNER], representative.profile.user)
        LegalRepresentative currentAssignee = packageRepresentatives.first()
        if (fromOrg.id == toOrg.id && currentAssignee.id == representative.id) {
            throw ExceptionUtils.createUnProcessableDataException('packages.transfer.to.itself')
        }
        if (fromOrg.id != toOrg.id) {
            List<PackageStatus> wrongStatuses = [TRANSFERRED, DELETED]
            List<String> nonTransferable = packages.findAll { wrongStatuses.contains(it.status) }.collect { it.easyVisaId }
            if (nonTransferable) {
                throw ExceptionUtils.createUnProcessableDataException('packages.transfer.wrong.status', null, [nonTransferable.join(', ')])
            }
        }
        Map<Long, List<Long>> activePackageRequests = sqlService.findPendingPackageTransferRequests(packages)
        PackageTransferRequest request = new PackageTransferRequest(packages: packages, representative: representative,
                requestedBy: requestedBy.profile)
        request.with {
            oldOrganization = fromOrg
            oldAssignee = currentAssignee
            representativeOrganization = toOrg
        }
        request.save(failOnError: true, flush: true)
        if (organizationService.doesEmployeeBelongToOrganization(requestedBy.id, fromOrg.id)) {
            if (organizationService.doesEmployeeBelongToOrganization(representative.id, fromOrg.id) && fromOrg.id == toOrg.id) {
                packages.each {
                    changePackageRepresentative(it, representative, packageRepresentatives.first(), fromOrg)
                }
                request.state = ProcessRequestState.ACCEPTED
                request.save(failOnError: true)
                alertService.createProcessRequestAlert(request, PACKAGE_TRANSFER_ACCEPTED_OWNER_SAME_ORG, packageRepresentatives.first().profile.user, requestedBy.profile.fullName)
                alertService.createProcessRequestAlert(request, PACKAGE_TRANSFER_ACCEPTED_RECIPIENT_SAME_ORG, representative.profile.user, requestedBy.profile.fullName)
            } else {
                alertService.createProcessRequestAlert(request, PACKAGE_TRANSFER_REQUEST, representative.user, requestedBy.profile.fullName)
                alertService.createProcessRequestAlert(request, PACKAGE_TRANSFER_REQUEST_OWNER, currentAssignee.profile.user, Alert.EASYVISA_SOURCE)
                organizationService.getOrganizationAdmins(fromOrg).each {
                    alertService.createProcessRequestAlert(request, PACKAGE_TRANSFER_REQUEST_ADMIN, it.user, Alert.EASYVISA_SOURCE)
                }
            }
        } else {
            throw ExceptionUtils.createUnProcessableDataException('employee.cannot.request.package.transfer.of.other.organization')
        }
        Map<String, String> activePackagesEmailInfo = [:]
        activePackageRequests.each {
            Long packageId = it.key
            List<Long> requestsId = it.value
            Package aPackage = Package.get(packageId)
            List<String> oldRequests = []
            requestsId.each {
                PackageTransferRequest transferRequest = PackageTransferRequest.get(it)
                if (transferRequest.packages.size() > 1) {
                    transferRequest.packages.remove(aPackage)
                    transferRequest.save(failOnError: true, flush: true)
                } else {
                    transferRequest.denyRequest()
                }
                oldRequests.add("${transferRequest.representative.profile.name} (${transferRequest.representativeOrganization.name})".toString())
            }
            activePackagesEmailInfo.put(aPackage.title, oldRequests.join(', '))
        }
        if (activePackagesEmailInfo) {
            sendEmailAsync({
                organizationService.getOrganizationAdmins(fromOrg).each {
                    EasyVisaSystemMessageType type = PACKAGE_TRANSFER_OVERRIDDEN
                    String body = alertService.renderTemplate(type.templatePath, [processRequest: request, info: activePackagesEmailInfo])
                    alertService.createAlert(type, it.user, null, body)
                }
            }, "Send package(s) overridden details by ${request.id}")
        }
    }

    Package changePackageRepresentative(Package aPackage, LegalRepresentative representative,
                                        LegalRepresentative oldAssignee, Organization organization,
                                        Boolean sendEmail = true) {
        if (aPackage.attorney == oldAssignee) {
            Date currentDate = new Date()
            PackageAssignee currentAssignee = aPackage.currentAssignee
            aPackage.addToAssignees(new PackageAssignee(aPackage: aPackage, representative: representative,
                    organization: organization, startDate: currentDate, status: PackageAssignmentStatus.ACTIVE))
            aPackage.attorney = representative
            currentAssignee.endDate = currentDate
            currentAssignee.status = PackageAssignmentStatus.INACTIVE
            aPackage.organization = organization
            currentAssignee.save(failOnError: true)
            aPackage.save(failOnError: true)
            if (aPackage.organization != organization && aPackage.retainerAgreement) {
                fileService.deleteRetainer(aPackage)
            }
            if (sendEmail) {
                sendEmailAsync({
                    sendPackageTransferEmail(aPackage.id, oldAssignee.id, representative.id)
                }, "Send change package attorney email for Package [${aPackage.id}]")
            }
            aPackage
        } else {
            throw ExceptionUtils.createUnProcessableDataException('package.details.changed.for.assignment')
        }
    }

    Package transferPackage(Package aPackage, LegalRepresentative representative,
                            LegalRepresentative oldAssignee, Organization organization, Profile requestedBy,
                            Boolean sendEmail = true) {
        if (aPackage.attorney == oldAssignee) {
            Package copyPackage = aPackage.copy(representative, organization)
            copyPackage.save(failOnError: true, flush: true)
            packageQuestionnaireService.copyPackageAnswers(aPackage, copyPackage)
            packageDocumentService.copyPackageDocumentPortalData(aPackage, copyPackage)
            aPackage.status = TRANSFERRED
            aPackage.transferredTo = copyPackage
            aPackage.transferredOn = new Date()
            aPackage.transferredAttorneyTo = representative
            aPackage.transferredBy = requestedBy
            aPackage.save(failOnError: true)
            copyPackage.save(failOnError: true)
            if (sendEmail) {
                sendEmailAsync({
                    sendPackageTransferEmail(aPackage.id, oldAssignee.id, representative.id)
                }, "Send change package attorney email for Package [${aPackage.id}]")
            }
            PackageOptInForPetitionerRequest.findAllByAPackage(aPackage).each {
                processService.deleteProcessRequest(it as ProcessRequest)
            }
            PackageOptInForImmigrationBenefitRequest.findAllByAPackage(aPackage).each {
                processService.deleteProcessRequest(it as ProcessRequest)
            }
            sendOptInRequests(copyPackage)
            copyPackage
        } else {
            throw ExceptionUtils.createUnProcessableDataException('package.details.changed.for.assignment')
        }
    }

    private void sendPackageTransferEmail(Long packageId, Long oldAssigneeId, Long representativeId) {
        evMailService.sendPackageTransferredEmail(Package.get(packageId), LegalRepresentative.get(oldAssigneeId),
                LegalRepresentative.get(representativeId))
    }

    void updatePackageTitlesForApplicant(Applicant applicant) {
        String query = "select distinct p from Package p left join p.benefits b where b.applicant.id=${applicant.id} OR (p.petitioner is not null AND p.petitioner.applicant.id=${applicant.id})"
        List<Package> packages = Package.executeQuery(query)
        packages.each {
            updatePackageTitle(it as Package)
        }
    }

    Package updatePackageTitle(Package aPackage) {
        String title = aPackage.orderedBenefits.collect {
            it.applicant.profile.title
        }.join(' + ')
        if (aPackage.petitioner) {
            title = [aPackage.petitioner.profile.title, title].join(' + ')
        }
        aPackage.title = title
        aPackage.save()
    }

    List<Package> fetchPackagesByPetitioner(Petitioner petitioner) {
        List<Package> packages = Package.createCriteria().list() {
            'in'('petitioner', Petitioner.findAllByApplicant(petitioner.applicant))
        } as List<Package>
        return packages ?: []
    }

    List<Package> fetchPackagesByPetitionerAndStatus(Petitioner petitioner, PackageStatus packageStatus) {
        List<Package> packages = Package.createCriteria().list() {
            'in'('petitioner', Petitioner.findAllByApplicant(petitioner.applicant))
            'eq'('status', packageStatus)
        } as List<Package>
        return packages ?: []
    }

    List<Package> fetchPackagesByBeneficiary(Applicant applicant) {
        String query = "select distinct p from Package p left join p.benefits b where b.applicant.id=${applicant.id}"
        Package.executeQuery(query)
    }

    List<Package> fetchPackagesByApplicantAndStatus(Applicant applicant, PackageStatus packageStatus) {
        String query = "select distinct p from Package p left join p.benefits b where p.status='${packageStatus.name()}' AND (b.applicant.id=${applicant.id} OR (p.petitioner is not null AND p.petitioner.applicant.id=${applicant.id}))"
        Package.executeQuery(query)
    }

    /**
     * Sends bill to Petitioner
     * @param aPackage Package
     * @param content String email content
     * @param charges List of charges which holds key value map
     * @param total BigDecimal total charge
     */
    void sendBill(Package aPackage, String content, List charges, BigDecimal total) {
        evMailService.validateVariables(EmailTemplateType.ADDITIONAL_FEES, '', content)
        Long currentUserId = springSecurityService.currentUserId as Long
        sendEmailAsync({
            sendBillEmail(aPackage.id, currentUserId, charges, total, content)
        }, "Send bill email for Package [${aPackage.id}]")
    }

    private void sendBillEmail(Long packageId, Long currentUserId, List charges, BigDecimal total, String content) {
        Package aPackage = Package.get(packageId)
        Applicant applicant = aPackage.client

        Map params = evMailService.buildPackageEmailParams(aPackage)
        params['charges'] = charges
        params['total'] = total

        EmailTemplateType emailTemplateType = EmailTemplateType.ADDITIONAL_FEES
        String subject = emailTemplateType.subject
        String mailContent = evMailService.evaluateTemplate(content, params)

        evMailService.sendEmail(buildPackageEmailDto(applicant.profile.email, subject, mailContent, aPackage))

        Profile sender = User.get(currentUserId).profile
        EasyVisaSystemMessageType type = PACKAGE_ADDITIONAL_FEE
        String alertContent = groovyPageRenderer.render(template: type.templatePath, model: [sender: sender.name])
        alertContent = evMailService.evaluateTemplate(alertContent, params)
        alertService.createProcessRequestAlert(null, type, aPackage.attorney.user, sender.name, alertContent)
    }

    Package savePackage(Package aPackage) {
        aPackage.save(failOnError: true)
        return aPackage
    }
    /**
     * Updates packages last active value for current applicant, if logged in user is an applicant.
     * @param id user id
     */
    void updateApplicantPackagesLastLogin(Long id) {
        Applicant applicant = applicantService.findApplicantByUser(id)
        if (applicant) {
            List<PackageStatus> statuses = [OPEN, BLOCKED]
            // Update all Packages whose Petitioner's applicant matches with the given Applicant
            Package.executeUpdate('''update Package p set p.lastActiveOn = now()
                    where p in (select p from Package p join p.petitioner pet where pet.applicant = :applicant and p.status in (:statuses))''',
                    [applicant: applicant, statuses: statuses])

            // Update all Packages whose ImmigrationBenefit's applicant matches with the given Applicant
            Package.executeUpdate('''update Package p set p.lastActiveOn = now()
                    where p in (select p from Package p join p.benefits b where b.applicant = :applicant and p.status in (:statuses))''',
                    [applicant: applicant, statuses: statuses])
        }
    }

    private void sendEmailAsync(Runnable command, String name) {
        asyncService.runAsync(command, name)
    }

}
