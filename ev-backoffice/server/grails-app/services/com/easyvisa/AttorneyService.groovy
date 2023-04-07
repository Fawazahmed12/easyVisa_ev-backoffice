package com.easyvisa

import com.easyvisa.dto.*
import com.easyvisa.enums.*
import com.easyvisa.job.RankScore
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.ExceptionUtils
import com.easyvisa.utils.StringUtils
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.rest.token.AccessToken
import org.apache.http.HttpStatus
import org.hibernate.SQLQuery
import org.hibernate.SessionFactory

import java.math.RoundingMode
import java.security.SecureRandom

import static com.easyvisa.enums.Country.*
import static com.easyvisa.enums.EmployeePosition.ATTORNEY
import static com.easyvisa.enums.EmployeePosition.PARTNER

@SuppressWarnings(['FactoryMethodName'])
class AttorneyService {

    final static String RECENT = 'recent'
    final static String TOP = 'top'
    final static String BASE = 'base'

    Random random = new SecureRandom()

    EvMailService evMailService
    OrganizationService organizationService
    ProfileService profileService
    EmployeeService employeeService
    AccountService accountService
    ProcessService processService
    FileService fileService
    PackageService packageService
    SpringSecurityService springSecurityService
    SessionFactory sessionFactory
    PermissionsService permissionsService
    ArticleService articleService
    AdminService adminService
    EmailVariableService emailVariableService
    AsyncService asyncService

    /***
     * Create an attorney and send a registration email.
     * @param attorney - Attorney to be stored in the database.
     * @return Saved attorney object
     */
    @Transactional
    LegalRepresentative createAttorney(LegalRepresentative attorney) {
        User user = attorney.user
        user.accountLocked = true
        profileService.addEasyVisaId(attorney)
        //default attorney notification
        attorney.profile.emailPreferences.addAll([
                new EmailPreference(type: NotificationType.APPLICANT_REGISTRATION, preference: Boolean.TRUE)
                        .save(failOnError: true),
                new EmailPreference(type: NotificationType.QUESTIONNAIRE_COMPLETE, preference: Boolean.TRUE)
                        .save(failOnError: true),
                new EmailPreference(type: NotificationType.DOCUMENTATION_COMPLETE, preference: Boolean.TRUE)
                        .save(failOnError: true)])
        attorney.save(failOnError: true)
        Role attorneyRole = Role.findByAuthority(Role.ATTORNEY)
        if (attorneyRole) {
            UserRole.create(user, attorneyRole)
        }
        profileService.createUserVerifyTokenAndSendEmail(attorney.profile)
        attorney
    }

    @Transactional
    LegalRepresentative findAttorneyByUser(Long userId) {
        LegalRepresentative.createCriteria().get {
            'profile' {
                eq('user.id', userId)
            }
        } as LegalRepresentative
    }

    @Transactional
    Employee findEmployeeByUser(Long userId) {
        Employee.createCriteria().get {
            'profile' {
                eq('user.id', userId)
            }
        } as Employee
    }

    //Create Attorney if the attorney with this username and password does not exist
    //If it does, resend a confirmation email and lock his account
    @Transactional
    LegalRepresentative registerAttorney(LegalRepresentative attorney) {
        User user = attorney.user
        LegalRepresentative existingAttorney = LegalRepresentative.createCriteria().get {
            createAlias('profile', 'p')
            createAlias('p.user', 'u')
            eq('u.username', user.username)
        }
        if (existingAttorney) {
            if (existingAttorney.registrationStatus != RegistrationStatus.NEW) {
                throw ExceptionUtils.createUnProcessableDataException('attorney.registration.complete.registration')
            }
            if (!springSecurityService.passwordEncoder.matches(user.password,existingAttorney.user.password)) {
                throw ExceptionUtils.createUnProcessableDataException('attorney.registration.wrong.password')
            }
            existingAttorney.profile.email = attorney.profile.email
            existingAttorney.save(failOnError: true)
            RegistrationCode registrationCode = new RegistrationCode(username: user.username)
            registrationCode.save(failOnError: true)
            evMailService.sendAttorneyRegistrationEmail(existingAttorney.profile, registrationCode)
            existingAttorney
        } else {
            createAttorney(attorney)
        }
    }

    LegalRepresentative updateAttorneyOfficeAddress(LegalRepresentative attorney, Address address) {
        if (attorney.officeAddress) {
            attorney.officeAddress.with {
                line1 = address.line1
                line2 = address.line2
                city = address.city
                state = address.state
                province = address.province
                zipCode = address.zipCode
                postalCode = address.postalCode
                country = address.country
            }
        } else {
            attorney.profile.address = address
        }
        attorney
    }

    LegalRepresentative maybeUpdateAttorneyRegistrationStatus(LegalRepresentative attorney, RegistrationStatus registrationStatus) {
        if (attorney.registrationStatus == registrationStatus) {
            attorney
        } else if (registrationStatus in (RegistrationStatus.updatableStatuses[attorney.registrationStatus] as List)) {
            attorney.registrationStatus = registrationStatus
            attorney
        } else {
            throw ExceptionUtils.createUnProcessableDataException('registrationstatus.not.updatable')
        }
    }

    @Transactional
    LegalRepresentative updateAttorney(LegalRepresentative attorney, final AttorneyCommand attorneyCommand) {
        attorneyCommand.attorneyFields.each { String field ->
            if (attorneyCommand[field]) {
                attorney[field] = attorneyCommand[field]
            }
        }
        attorneyCommand.profileFields.each { String field ->
            if (attorneyCommand[field]) {
                attorney.profile[field] = attorneyCommand[field]
            }
        }
        if (attorneyCommand.officeAddress) {
            attorney = updateAttorneyOfficeAddress(attorney, attorneyCommand.officeAddress)
        }
        if (attorneyCommand.registrationStatus) {
            attorney = maybeUpdateAttorneyRegistrationStatus(attorney, attorneyCommand.registrationStatus)
        }
        attorney = updateAttorneyFeeSchedule(attorney, attorneyCommand.feeSchedule)
        profileService.updateProfileEmail(attorney.profile, attorneyCommand.email)
        attorney.save(failOnError: true)
    }

    @Transactional
    LegalRepresentative updateAttorneyFeeSchedule(LegalRepresentative representative, List<FeeScheduleCommand> feeScheduleList) {
        List<Fee> currentFeeSchedule = representative.feeSchedule
        List<Fee> feesToBeRemoved = currentFeeSchedule.findAll {
            !(it.benefitCategory in feeScheduleList*.benefitCategory)
        }
        feesToBeRemoved.each {
            representative.removeFromFeeSchedule(it)
            it.delete(failOnError: true)
        }
        feeScheduleList.each { FeeScheduleCommand newFeeSchedule ->
            Fee feeScheduleEntry = currentFeeSchedule.find {
                it.benefitCategory == newFeeSchedule.benefitCategory
            }
            if (feeScheduleEntry) {
                feeScheduleEntry.amount = newFeeSchedule.amount
                feeScheduleEntry.save()
            } else {
                Fee fee = new Fee(amount: newFeeSchedule.amount, representative: representative, benefitCategory: newFeeSchedule.benefitCategory)
                representative.addToFeeSchedule(fee)
            }
        }
        representative.save(failOnError: true)
    }

    /**
     * It combines complete operation of payments. Performs registration charge and set complete registration status.
     * @param attorney attorney to complete payments
     * @param paymentMethodCommand Fattmerchant payment method id and other card details
     * @return attorney
     */
    @Transactional
    LegalRepresentative completePayment(LegalRepresentative attorney, final PaymentMethodCommand paymentMethodCommand) {
        if (attorney.registrationStatus in [RegistrationStatus.NEW, RegistrationStatus.EMAIL_VERIFIED]) {
            throw ExceptionUtils.createUnProcessableDataException('email.not.verified')
        } else {
            accountService.registrationCharge(attorney.profile.user, paymentMethodCommand)
            Organization organization = organizationService.create("${attorney.profile.fullName} - ${OrganizationType.SOLO_PRACTICE.displayName}", OrganizationType.SOLO_PRACTICE)
            organizationService.addAttorneyToOrganization(organization, attorney, EmployeePosition.PARTNER, true)
            AdminConfig adminConfig = adminService.adminSettings.adminConfig
            attorney.profile.maintenanceFee = adminConfig.maintenanceFee
            attorney.profile.cloudStorageFee = adminConfig.cloudStorageFee
            attorney.registrationStatus = RegistrationStatus.COMPLETE
            attorney.save(failOnError: true)
        }
    }

    List<State> getAttorneyStates() {
        LegalRepresentative.createCriteria().list {
            projections {
                distinct('address.state')
            }
            createAlias('profile.address', 'address')
            profile {
                user {
                    eq('paid', true)
                    eq('enabled', true)
                }
            }
        } as List<State>
    }

    List<Country> getAttorneyCountries() {
        LegalRepresentative.createCriteria().list {
            projections {
                distinct('address.country')
            }
            createAlias('profile.address', 'address')
            profile {
                user {
                    eq('paid', true)
                    eq('enabled', true)
                }
            }
        } as List<Country>
    }

    List getAttorneyLanguages() {
        List<Language> languages = LegalRepresentative.createCriteria().list() {
            profile {
                user {
                    eq('paid', true)
                    eq('enabled', true)
                }
            }
        }*.spokenLanguages
        Language.values().findAll({
            return !(it == Language.ENGLISH)
        }).collect {
            [name: it.displayName, id: it.name(), active: languages.contains(it)]
        }
    }

    List searchableStates() {
        List<State> attorneyStates = getAttorneyStates() ?: []
        State.values().collect {
            [name: it.displayName, id: it.name(), active: attorneyStates.contains(it)]
        }
    }

    List searchableCountries() {
        List<Country> attorneyCountries = getAttorneyCountries() ?: []
        List<Country> searchableCountries = [CHINA, CHINA_TAIWAN, HONG_KONG, MEXICO, PHILIPPINES, THAILAND, VIETNAM]
        searchableCountries.collect {
            [name: it.displayName, id: it.name(), active: attorneyCountries.contains(it)]
        }
    }

    @Transactional
    Map getAttorneySearchConfig() {
        [states: searchableStates(), countries: searchableCountries(), languages: getAttorneyLanguages()]
    }

    PaginationResponseDto findAttorneys(FindRepresentativeCommand findRepresentativeCommand) {
        List<Long> attorneyIds = []
        int totalCount = 0

        String query = '''from LegalRepresentative l 
  left join l.spokenLanguages sl
  left join l.profile prfl
  left join prfl.address addr
  left join prfl.user u
   where 1=1 '''
        Map params = [:]
        if (findRepresentativeCommand.hasCountryParam() && findRepresentativeCommand.hasStateParam()) {
            query += " And (${findRepresentativeCommand.getCountriesQuery(params)} OR addr.state in :states)"
            params['states'] = findRepresentativeCommand.getStateParams()
        } else {
            if (findRepresentativeCommand.hasCountryParam()) {
                query += " And ${findRepresentativeCommand.getCountriesQuery(params)}"
            }
            if (findRepresentativeCommand.hasStateParam()) {
                query += ' And addr.state in :states'
                params['states'] = findRepresentativeCommand.getStateParams()
            }
        }

        /*
        * All US immigration attorneys must speak English by default.
        * Therefore, if someone chooses English as a “foreign” language,
        * we need to ignore that filter altogether, like they had never selected a language to filter by.*/

        if (findRepresentativeCommand.language && findRepresentativeCommand.language != Language.ENGLISH) {
            query += ' And sl = :language'
            params['language'] = findRepresentativeCommand.language
        }
        query += ' AND u.paid=true AND u.activeMembership=true AND u.enabled=true'
        long count = Package.executeQuery("""select count(id) from LegalRepresentative where id in (select l.id ${query} group by l.id, prfl.dateCreated)"""
                .toString(), params).first()
        params.putAll(findRepresentativeCommand.paginationParams)
        attorneyIds = Package.executeQuery("""select l.id ${query} group by l.id, prfl.dateCreated
                order by l.recentContributorScore DESC, l.topContributorScore DESC,
                l.baseContributorScore DESC, l.randomScore DESC, l.id ASC""".toString(),
                params)
        new PaginationResponseDto(result: LegalRepresentative.getAll(attorneyIds), totalCount: count)
    }

    AttorneySearchResponseDto createResponseDto(LegalRepresentative legalRepresentative, Boolean full = Boolean.FALSE) {
        legalRepresentative.publicAvgReviewRating = legalRepresentative.publicAvgReviewRating.setScale(1)
        String lawFirmName = null
        if (full) {
            lawFirmName = organizationService.getLawFirmOrganization(legalRepresentative)?.name
        }
        new AttorneySearchResponseDto(legalRepresentative: legalRepresentative, lawFirmName: lawFirmName)
    }

    @Transactional
    Integer calculateAttorneyPublicMeasures() {
        Integer offset = 0
        Integer maxPageSize = 100
        Integer total = 0
        List<LegalRepresentative> attorneys = getAttorneysPageable(offset, maxPageSize)
        while (attorneys) {
            total += attorneys.size()
            attorneys.each {
                calculateAttorneyMaxYearsLicensed(it)
            }
            offset += maxPageSize
            attorneys = getAttorneysPageable(offset, maxPageSize)
        }
        total
    }

    void calculateAttorneyArticles(Set<Long> attorneyIds) {
        runAsync({
            attorneyIds.each {
                LegalRepresentative attorney = LegalRepresentative.findById(it);
                attorney.publicNoOfApprovedArticles = getArticlesCount(attorney)
                attorney.save(failOnError: true)
            }
        }, "Calculating attorney articles [${attorneyIds}]")

    }

    void calculateAttorneyReview(Long id) {
        runAsync({
            LegalRepresentative attorney = LegalRepresentative.findById(id)
            List<Review> reviews = getAttorneyReviews(attorney)
            attorney.publicNoOfReviews = reviews.size()
            BigDecimal ratingSum = (reviews.sum { it.rating } ?: 0) as BigDecimal
            BigDecimal ratingsCount = new BigDecimal(reviews.size() ?: 1)
            attorney.publicAvgReviewRating = ratingSum.divide(ratingsCount, 1, RoundingMode.HALF_UP)
            attorney.save(failOnError: true)
        }, "Calculating attorney reviews [${id}]")
    }

    void calculateAttorneyMaxYearsLicensed(Long id) {
        runAsync({
            calculateAttorneyMaxYearsLicensed(LegalRepresentative.findById(id))
        }, "Calculating attorney max year licensed [${id}]")
    }

    void calculateAttorneyMaxYearsLicensed(LegalRepresentative attorney) {
        Date currentDate = new Date()
        attorney.publicMaxYearsLicensed = attorney.licensedRegions.collect {
            (currentDate - it.dateLicensed) / 365 as Integer
        }.max() ?: 0
        attorney.save(failOnError: true)
    }

    private List<LegalRepresentative> getAttorneysPageable(Integer offset, Integer maxPageSize) {
        LegalRepresentative.createCriteria().list {
            firstResult(offset)
            maxResults(maxPageSize)
            order('id')
        } as List<LegalRepresentative>
    }

    @GrailsCompileStatic
    PaginationResponseDto findRepsForMarketingSite(FindRepresentativeCommand findRepresentativeCommand) {
        PaginationResponseDto responseDto = findAttorneys(findRepresentativeCommand)
        List<LegalRepresentative> legalRepresentatives = responseDto.result
        responseDto.result = legalRepresentatives.collect { createResponseDto(it as LegalRepresentative) }
        responseDto
    }

    AttorneySearchResponseDto getAttorneyPublicly(Long id) {
        LegalRepresentative legalRepresentative = LegalRepresentative.get(id)
        if (!legalRepresentative) {
            throw ExceptionUtils.createNotFoundException('attorney.not.found', null, [id])
        }
        createResponseDto(legalRepresentative, Boolean.TRUE)
    }

    List<Review> getAttorneyReviews(LegalRepresentative representative) {
        Review.findAllByRepresentative(representative)
    }

    /**
     * Gets Attorney's reviews in paginated way.
     * @param representative attorney
     * @param command filtering items
     * @return list of reviews
     */
    List<Review> getAttorneyReviews(LegalRepresentative representative, AttorneyReviewsCommand command) {
        Review.createCriteria().list(max: command.max, offset: command.offset) {
            eq('representative', representative)
            if (command.rating) {
                eq('rating', command.rating)
            }
            order(command.sortFieldName, command.sortOrder)
            order('id', 'asc')
        } as List<Review>
    }

    /**
     * Gets Attorney's reviews count.
     * @param representative attorney
     * @param command filtering items
     * @return reviews count
     */
    Long getAttorneyReviewsCount(LegalRepresentative representative, AttorneyReviewsCommand command) {
        Review.createCriteria().count {
            eq('representative', representative)
            if (command.rating) {
                eq('rating', command.rating)
            }
        } as Long
    }

    LegalRepresentative updateRepresentativeWorkingHours(LegalRepresentative representative, List<WorkingHourCommand> workingHoursCommandList) {
        List<WorkingHour> workingHours = workingHoursCommandList.collect { it.asWorkingHour() }
        if (workingHours) {
            def updatedWorkingHourIds = workingHours.findAll { it.id }*.id
            def workingHoursToDelete = representative.workingHours.findAll {
                !(updatedWorkingHourIds.contains(it.id))
            }
            workingHoursToDelete.each {
                representative.removeFromWorkingHours(it)
                it.delete(failOnError: true)
            }
            workingHours.each { WorkingHour workingHour ->
                WorkingHour existingWorkingHour = representative.workingHours.find {
                    it.dayOfWeek == workingHour.dayOfWeek
                }
                if (existingWorkingHour) {
                    existingWorkingHour.startHour = workingHour.startHour
                    existingWorkingHour.startMinutes = workingHour.startMinutes
                    existingWorkingHour.endHour = workingHour.endHour
                    existingWorkingHour.endMinutes = workingHour.endMinutes
                    existingWorkingHour.save(failOnError: true)
                } else {
                    representative.addToWorkingHours(workingHour)
                }

            }
        } else {
            representative.workingHours.each {
                it.delete(failOnError: true)
            }
            representative.workingHours = []
        }
        representative.save(failOnError: true)
    }

    LegalRepresentative updateLicensedRegions(LegalRepresentative representative, List<LicensedRegion> licensedRegions) {
        if (licensedRegions) {
            def updatedItems = licensedRegions.findAll { it.id }*.id
            def itemsToDelete = representative.licensedRegions.findAll {
                !(updatedItems.contains(it.id))
            }
            itemsToDelete.each {
                representative.removeFromLicensedRegions(it)
            }
            licensedRegions.each { LicensedRegion licensedRegion ->
                if (licensedRegion.id) {
                    licensedRegion.save(failOnError: true)
                } else {
                    representative.addToLicensedRegions(licensedRegion)
                }
            }
        } else {
            representative.licensedRegions.each {
                it.delete(failOnError: true)
            }
            representative.licensedRegions = []
        }
        representative.save(failOnError: true)
    }

    LegalRepresentative updateEducation(LegalRepresentative representative, List<EducationCommand> educationList) {
        if (educationList) {
            def updatedItems = educationList.findAll { it.id }*.id
            def itemsToDelete = representative.degrees.findAll {
                !(updatedItems.contains(it.id))
            }
            itemsToDelete.each {
                representative.removeFromDegrees(it)
            }
            educationList.each { EducationCommand educationCommand ->
                Education education = educationCommand.id ? Education.get(educationCommand.id) : new Education()
                education.with {
                    school = educationCommand.school
                    year = educationCommand.year
                    degree = educationCommand.degree
                    honors = educationCommand.honors
                }
                if (education.id) {
                    education.save(failOnError: true)
                } else {
                    representative.addToDegrees(education)
                }
            }
        } else {
            representative.degrees.each {
                it.delete(failOnError: true)
            }
            representative.degrees = []
        }
        representative.save(failOnError: true)
    }

    @Transactional
    LegalRepresentative edit(LegalRepresentative representative, ProfileCommand changes) {
        representative = employeeService.updateEmployee(representative, changes)
        representative.with {
            awards = changes.awards
            experience = changes.experience
            facebookUrl = changes.facebookUrl
            twitterUrl = changes.twitterUrl
            youtubeUrl = changes.youtubeUrl
            linkedinUrl = changes.linkedinUrl
            websiteUrl = changes.websiteUrl
            stateBarNumber = changes.stateBarNumber
            uscisOnlineAccountNo = changes.uscisOnlineAccountNo
            practiceAreas = changes.practiceAreas
            profileSummary = changes.summary
        }
        representative = updateRepresentativeWorkingHours(representative, changes.workingHours)
        representative = updateLicensedRegions(representative, changes.licensedRegions)
        representative = updateEducation(representative, changes.education)
        representative.save(failOnError: true)
    }

    @Transactional
    LegalRepresentative findRepresentativeByEmailAndEasyVisaId(String email, String easyVisaId) {
        final Profile profile = profileService.findProfileByEmailAndEasyVisaId(email, easyVisaId)
        if (profile) {
            Long applicantCount = Applicant.countByProfile(profile)
            if (applicantCount > 0) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'user.is.applicant.not.attorney.cant.transfer.package')
            } else {
                LegalRepresentative legalRepresentative = LegalRepresentative.findByProfile(profile)
                if (legalRepresentative?.user?.enabled && legalRepresentative?.user?.paid) {
                    return legalRepresentative
                } else {
                    throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'user.is.not.active')
                }
            }
        } else {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'input.email.does.not.match.evid')
        }
    }

    @Transactional
    LegalRepresentative findRepresentativeByEasyVisaId(String easyVisaId) {
        LegalRepresentative.createCriteria().get() {
            profile {
                eq('easyVisaId', easyVisaId)
            }
        } as LegalRepresentative
    }

    @Transactional
    Employee findEmployeeByEmailAndEasyVisaId(String email, String easyVisaId) {
        List<Employee> employees = Employee.createCriteria().list() {
            profile {
                eq('easyVisaId', easyVisaId)
                eq('email', email)
            }
        }
        if (employees) {
            employees.first()
        }
    }

    Employee findEmployeeByEmail(String email) {
        List<Employee> employees = Employee.createCriteria().list() {
            profile {
                eq('email', email)
            }
        }
        if (employees) {
            employees.first()
        }
    }

    Employee findEmployeeByEmailWithIgnoreCase(String email) {
        Employee.createCriteria().get() {
            profile {
                eq('email', email, [ignoreCase: true])
            }
        } as Employee
    }

    /*

    TODO: Need to figure out if this can be more readable, maybe throw excpetions in different methods
    and call them here. Right now its very complicated, but it follows the business rules :) :(
     */

    @Transactional
    Employee validateOrganizationInvite(String email, String easyVisaId, Organization organization) {
        Profile profile = profileService.findProfileByEmailAndEasyVisaId(email, easyVisaId)
        if (profile) {
            Long applicantCount = Applicant.countByProfile(profile)
            if (applicantCount > 0) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'user.is.applicant.not.attorney')
            } else {
                Employee employee = Employee.findByProfile(profile)
                if (employee) {
                    if (OrganizationEmployee.countByEmployeeAndOrganization(employee, organization) > 0) {
                        throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'attorney.is.already.in.organization', params: [organization.organizationType.displayName])
                    } else {
                        if (employee.isLegalRepresentative() && (employee as LegalRepresentative).representativeType == RepresentativeType.ACCREDITED_REPRESENTATIVE) {
                            if (employee.linkedOrganizations.contains(organization)) {
                                employee
                            } else {
                                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'attorney.has.not.linked.organization')
                            }
                        } else {
                            employee
                        }
                    }
                } else {
                    throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'input.email.does.not.match.evid')
                }
            }
        } else {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'input.email.does.not.match.evid')
        }
    }

    /**
     * Deletes attorney and related stuff. Uses for delete account action.
     * @param attorney attorney
     */
    void deleteAttorney(LegalRepresentative attorney) {
        //workaround to not send Alerts to deleting user. Not ideal...
        attorney.profile.user.activeMembership = Boolean.FALSE
        declineRequests(attorney)
        deactivateOrganizationStuff(attorney)
        //nullify legal rep data
        attorney.with {
            if (officeAddress) {
                officeAddress.delete(failOnError: true)
                it.profile.address = null
            }
            stateBarNumber = null
            uscisOnlineAccountNo = null
            practiceAreas?.clear()
            profileSummary = null
            awards = null
            experience = null
            attorneyType = null
            creditBalance = null
            facebookUrl = null
            linkedinUrl = null
            twitterUrl = null
            youtubeUrl = null
            websiteUrl = null
            feeSchedule?.clear()
            licensedRegions?.clear()
            workingHours.clear()
            degrees.clear()
            practiceAreas.clear()
        }
        employeeService.validateAndNotifyAdminOrgs(attorney)
    }

    private void deactivateOrganizationStuff(LegalRepresentative attorney) {
        //getting solo orgs
        List<Organization> soloOrgs = Organization.executeQuery(
                '''select distinct o from OrganizationEmployee oe join oe.organization o
                    where o.organizationType = :orgSolo and oe.employee = :attorney and oe.status = :active
                    and oe.inactiveDate is null''',
                [attorney: attorney, active: EmployeeStatus.ACTIVE,
                 orgSolo : OrganizationType.SOLO_PRACTICE]) as List<Organization>
        if (soloOrgs) {
            //deleting packages in solo-practice org
            List<Package> packages = Package.findAllByStatusAndAttorneyAndOrganizationInList(PackageStatus.LEAD,
                    attorney, soloOrgs)
            packages.each {
                packageService.deletePackage(it)
            }
            //setting INACTIVE status to all persons in solo orgs
            OrganizationEmployee.executeUpdate('''
            update OrganizationEmployee oe set oe.status = :status, inactiveDate = now() where oe.status <> :status
            and oe.organization in (:orgs)''', [status: EmployeeStatus.INACTIVE, orgs: soloOrgs])
            soloOrgs.each {
                it.with {
                    profileSummary = null
                    awards = null
                    experience = null
                    officePhone = null
                    mobilePhone = null
                    faxNumber = null
                    email = null
                    facebookUrl = null
                    linkedinUrl = null
                    twitterUrl = null
                    youtubeUrl = null
                    websiteUrl = null
                    yearFounded = null

                    fileService.deleteEasyVisaFile(logoFile)
                    logoFile = null
                    if (address) {
                        address.delete(failOnError: true)
                    }
                    workingHours.clear()
                    spokenLanguages.clear()
                    practiceAreas.clear()
                    rosterNames.clear()
                }
                it.save(failOnError: true)
            }
        }
    }

    private void declineRequests(LegalRepresentative attorney) {
        //withdraw send requests
        List<InviteToCreateOrganizationRequest> invitesSend = InviteToCreateOrganizationRequest
                .findAllByRequestedByAndState(attorney.profile, ProcessRequestState.PENDING)
        invitesSend.each {
            processService.withDrawLegalRepInvite(it)
        }
        //deny received requests
        List<InviteToCreateOrganizationRequest> invitesReceived = InviteToCreateOrganizationRequest
                .findAllByRepresentativeAndState(attorney, ProcessRequestState.PENDING)
        invitesReceived.each {
            processService.denyInvitationToCreateOrganization(it)
        }
    }

    /**
     * Collects attorneys prospect counts and packages count for different timelines.
     * 1. Month
     * 2. Quarter
     * 3. YTD
     * 4. All
     * @param attorney legal representative
     * @param organizations list of organizations
     * @return calculated data
     */
    @Transactional
    MarketingResponseDto getAttorneyProspectInfo(LegalRepresentative attorney, List<Organization> organizations) {
        MarketingResponseDto result = new MarketingResponseDto()
        Date currentMonth = DateUtil.currentMonth
        Date currentQuarter = DateUtil.currentQuarter
        Date currentYear = DateUtil.currentYear
        collectProspects(result, attorney, currentMonth, currentQuarter, currentYear)
        collectClients(result, attorney, organizations, currentMonth, currentQuarter, currentYear)
        result
    }

    private void collectProspects(MarketingResponseDto result, LegalRepresentative attorney, Date currentMonth,
                                  Date currentQuarter, Date currentYear) {
        TimelineItemResponseDto profileViews = new TimelineItemResponseDto()
        TimelineItemResponseDto contactViews = new TimelineItemResponseDto()
        List<ProspectType> contacts = [ProspectType.PHONE, ProspectType.OFFICE, ProspectType.FAX]
        profileViews.month = getProspectInfo(attorney, [ProspectType.PROFILE], currentMonth)
        contactViews.month = getProspectInfo(attorney, contacts, currentMonth)
        profileViews.quarter = getProspectInfo(attorney, [ProspectType.PROFILE], currentQuarter)
        contactViews.quarter = getProspectInfo(attorney, contacts, currentQuarter)
        profileViews.ytd = getProspectInfo(attorney, [ProspectType.PROFILE], currentYear)
        contactViews.ytd = getProspectInfo(attorney, contacts, currentYear)
        profileViews.lifeTime = getProspectInfo(attorney, [ProspectType.PROFILE])
        contactViews.lifeTime = getProspectInfo(attorney, contacts)
        result.prospectiveClients = profileViews
        result.phoneNumberClients = contactViews
    }

    private void collectClients(MarketingResponseDto result, LegalRepresentative attorney,
                                List<Organization> organizations, Date currentMonth, Date currentQuarter,
                                Date currentYear) {
        TimelineItemResponseDto clients = new TimelineItemResponseDto()
        clients.month = getAttorneyPackagesCount(attorney, organizations, currentMonth)
        clients.quarter = getAttorneyPackagesCount(attorney, organizations, currentQuarter)
        clients.ytd = getAttorneyPackagesCount(attorney, organizations, currentYear)
        clients.lifeTime = getAttorneyPackagesCount(attorney, organizations)
        result.activeClients = clients
    }

    private Long getProspectInfo(LegalRepresentative attorney, List<ProspectType> types, Date date = null) {
        ProspectCounts.createCriteria().get {
            projections {
                count('id')
            }
            if (date) {
                ge('searchDate', date)
            }
            eq('representative', attorney)
            inList('prospectType', types)
        } as Long
    }

    private Long getAttorneyPackagesCount(LegalRepresentative attorney, List<Organization> orgs, Date date = null) {
        Map<String, Object> params = [attorney: attorney]
        String query = '''select count(distinct p.id)
                from Package p
                left join Answer a on a.packageId = p.id
                left join BaseDocument d on d.aPackage.id = p.id
                where p.attorney = :attorney'''
        if (orgs) {
            params['orgs'] = orgs
            query += ' and p.organization in (:orgs)'
        }
        if (date) {
            params['date'] = date
            query += ' and (a.lastUpdated > :date or d.dateCreated > :date)'
        }
        List packages = Package.executeQuery(query, params)
        packages[0] as Long
    }

    /**
     * Checks ProspectCounts in Drupal.
     */
    @Transactional
    void checkProspectCounts() {
        log.info("Getting prospect counts from Drupal")
        Object response = articleService.callDrupalForCheck(AppConfigType.DRUPAL_LAST_PROSPECT_COUNTS_CHECK, '/attorney-stat', false)
        log.info("Parsing prospect counts response from Drupal")
        if (response in List) {
            response.each {
                try {
                    Long id = it['aid'] as Long
                    LegalRepresentative attorney = LegalRepresentative.get(id)
                    if (attorney) {
                        ProspectType prospectType = ProspectType.findByName(it['request_type'])
                        if (prospectType) {
                            new ProspectCounts(representative: attorney, searchDate: DateUtil.drupalDate(it['created_time']),
                                    prospectType: prospectType).save(failOnError: true)
                        } else {
                            log.warn("Unknown request_type/prospect_type value [${it['request_type']}]")
                        }
                    } else {
                        log.warn("Attorney with id [${id}] not found in the system")
                    }
                } catch (Exception e) {
                    log.warn("Can't process article from Drupal response $it", e)
                }
            }
        }
    }

    /**
     * Collects attorneys revenue for different timelines.
     * 1. Month
     * 2. Quarter
     * 3. YTD
     * 4. All
     * @param attorney legal representative
     * @param organizations organizations
     * @return revenue in different timelines
     */
    @Transactional
    TimelineDecimalItemResponseDto attorneyRevenue(LegalRepresentative attorney, List<Organization> organizations) {
        TimelineDecimalItemResponseDto result = new TimelineDecimalItemResponseDto()
        result.month = calculateRevenue(attorney, organizations, DateUtil.currentMonth)
        result.quarter = calculateRevenue(attorney, organizations, DateUtil.currentQuarter)
        result.ytd = calculateRevenue(attorney, organizations, DateUtil.currentYear)
        result.lifeTime = calculateRevenue(attorney, organizations)
        result
    }

    private BigDecimal calculateRevenue(LegalRepresentative attorney, List<Organization> orgs, Date date = null) {
        String query = 'select sum(revenue) from LegalRepresentativeRevenue where attorney = :attorney'
        Map params = [attorney: attorney]
        if (orgs) {
            params['orgs'] = orgs
            query += ' and organization in (:orgs)'
        }
        if (date) {
            query += ' and dateCreated >= :date'
            params['date'] = date
        }
        List result = LegalRepresentativeRevenue.executeQuery(query, params)
        (result && result[0]) ? (result[0] as BigDecimal).setScale(2) : BigDecimal.ZERO
    }

    @Transactional
    TimelineDecimalItemResponseDto attorneyReferralBonuses(LegalRepresentative attorney) {
        TimelineDecimalItemResponseDto result = new TimelineDecimalItemResponseDto()
        result.month = calculateReferralBonus(attorney, DateUtil.currentMonth)
        result.quarter = calculateReferralBonus(attorney, DateUtil.currentQuarter)
        result.ytd = calculateReferralBonus(attorney, DateUtil.currentYear)
        result.lifeTime = calculateReferralBonus(attorney)
        result
    }

    private BigDecimal calculateReferralBonus(LegalRepresentative attorney, Date date = null) {
        String query = 'select sum(at.amount) from AccountTransaction at where at.profile = :profile and at.source = :source'
        Map params = [profile: attorney.profile, source: TransactionSource.REFERRAL]
        if (date) {
            query += ' and at.date >= :date'
            params['date'] = date
        }
        List result = AccountTransaction.executeQuery(query, params)
        BigDecimal referralBonus = (result && result[0]) ? (result[0] as BigDecimal).setScale(2) : BigDecimal.ZERO
        referralBonus.abs()
    }


    @Transactional
    AccessToken convertToAttorney(User user, ConvertEmployeeToAttorneyCommand convertCommand) {
        //only employees allowed to do the conversion
        if (!user.isEmployeeOnly()) {
            throw ExceptionUtils.createAccessDeniedException('current.user.is.not.an.employee')
        }
        //updating employee
        Employee employee = findEmployeeByUser(user.id)
        convertCommand.profile.languages = employee.spokenLanguages
        employeeService.updateEmployee(employee, convertCommand.profile)
        employee.save(failOnError: true)
        //converting to attorney. Not ideal...
        String query = "insert into legal_representative(id, registration_status, representative_type, attorney_type, base_contributor_score, recent_contributor_score, top_contributor_score, random_score) " +
                "values (${employee.id}, '${RegistrationStatus.COMPLETE}', '${RepresentativeType.ATTORNEY}', '${AttorneyType.SOLO_PRACTITIONER}', 0, 0, 0, 0)"
        SQLQuery sqlQuery = sessionFactory.currentSession.createSQLQuery(query)
        sqlQuery.executeUpdate()
        //create solo org - employee should be added as PARTNER in a SOLO org EV-3409
        Organization organization = organizationService.create("${employee.profile.fullName} - ${OrganizationType.SOLO_PRACTICE.displayName}", OrganizationType.SOLO_PRACTICE)
        new OrganizationEmployee(organization: organization, employee: employee, status: EmployeeStatus.ACTIVE,
                position: PARTNER, isAdmin: true).save(failOnError: true)
        //changing roles
        UserRole.create(user, Role.findByAuthority(Role.ATTORNEY))
        UserRole.remove(user, Role.findByAuthority(Role.EMPLOYEE), true)
        //charge registration fee if not in blessed org
        if (!permissionsService.isBlessed(user)) {
            accountService.registrationCharge(user, convertCommand.paymentMethod)
        }
        //refresh current session
        profileService.loginUser(employee.profile)
    }

    /**
     * Cleans attorney rankings details for all attorneys.
     * It should be used just prior calculation of current ranking values.
     */
    @Transactional
    void cleanAttorneyRanking() {
        LegalRepresentative.executeUpdate('UPDATE LegalRepresentative SET topContributorScore = 0, ' +
                'recentContributorScore = 0, baseContributorScore = 0, randomScore = 0')
    }

    /**
     * Calculates attorney rank.
     * @param attorneyId attorney id
     * @param rankScoreMap map with rank scores to calculate
     */
    @Transactional
    void calcRank(Long attorneyId, Map<String, RankScore> rankScoreMap) {
        log.info("Started rank calculation for [${attorneyId}] attorney")
        LegalRepresentative attorney = LegalRepresentative.get(attorneyId)
        attorney.randomScore = random.nextInt(2)
        Long total = getArticlesCount(attorney)
        Long less30 = getArticlesCount(attorney, getLowerDate(-30))
        Long between31and90 = getArticlesCount(attorney, getLowerDate(-90), getUpperDate(-31))
        Long between91and180 = getArticlesCount(attorney, getLowerDate(-180), getUpperDate(-91))
        attorney.with {
            baseContributorScore = calculateRank(rankScoreMap[BASE], total, less30, between31and90, between91and180)
            recentContributorScore = calculateRank(rankScoreMap[RECENT], total, less30, between31and90, between91and180)
            topContributorScore = calculateRank(rankScoreMap[TOP], total, less30, between31and90, between91and180)
        }
        attorney.save(failOnError: true)
        log.info("Finished rank calculation for [${attorneyId}] attorney")
    }

    /**
     * Gets attorney notifications. If a type is missed will populate it with default content.
     * @param attorney legal representative
     * @params command find command
     * @return templates
     */
    @Transactional
    List<EmailTemplate> findAttorneyNotifications(LegalRepresentative attorney,
                                                  FindAttorneyNotificationsCommand command) {
        List<EmailTemplate> result = []
        List<EmailTemplate> templates = EmailTemplate.createCriteria().list {
            eq('attorney', attorney)
            isNotNull('preference')
            if (command.types) {
                inList('templateType', command.types)
            }
        } as List<EmailTemplate>
        List<EmailTemplateType> types = command.types ?: EmailTemplateType.attorneyReminders
        types.each {
            checkTemplateType(result, templates, it, attorney)
        }
        result
    }

    private void checkTemplateType(List<EmailTemplate> result, List<EmailTemplate> templates, EmailTemplateType type,
                                   LegalRepresentative attorney) {
        EmailTemplate template = templates.find { it.templateType == type }
        result << (template ?: new EmailTemplate(templateType: type,
                preference: new EmailPreference(preference: Boolean.FALSE), subject: type.subject,
                content: evMailService.getRenderedTemplate(null, type, attorney, [:])['content']))
    }

    /**
     * Updates attorney's notifications that sends to clients.
     * @param attorney attorney
     * @param notifications notifications to be updated
     * @return list of EmailTemplates
     */
    @Transactional
    List<EmailTemplate> updateAttorneyNotifications(LegalRepresentative attorney,
                                                    List<AttorneyNotificationCommand> notifications) {
        List<EmailTemplate> result = []
        notifications.each {
            evMailService.validateVariables(it.templateType, it.subject, it.content)
            EmailTemplate email
            if (it.id) {
                email = EmailTemplate.get(it.id)
                if (!email) {
                    throw ExceptionUtils.createNotFoundException('attorney.notification.not.found')
                }
                if (email.attorney.id != attorney.id || email.templateType != it.templateType) {
                    throw ExceptionUtils.createUnProcessableDataException('attorney.wrong.notification')
                }
                updateTemplate(it, email)
            } else {
                email = EmailTemplate.findByAttorneyAndTemplateType(attorney, it.templateType)
                if (email) {
                    updateTemplate(it, email)
                } else {
                    EmailPreference preference = new EmailPreference(type: it.templateType.notificationType,
                            preference: it.repeatInterval ? Boolean.TRUE : Boolean.FALSE,
                            repeatInterval: it.repeatInterval)
                    email = new EmailTemplate(content: it.content, attorney: attorney, templateType: it.templateType,
                            subject: it.subject, preference: preference).save(failOnError: true)
                }
            }
            result << email
        }
        result
    }

    /**
     * Adds referral bonus to requester and an Attorney who send bring on another Attorney
     * @param email referral Attorney email
     */
    @Transactional
    void referral(String email) {
        Employee referee = findEmployeeByUser(springSecurityService.currentUserId)
        if ((referee instanceof LegalRepresentative) && referee.registrationStatus == RegistrationStatus.COMPLETE) {
            throw ExceptionUtils.createUnProcessableDataException('attorney.referee.registered')
        }
        Employee employee = findEmployeeByEmail(email)
        if (!employee || !(employee instanceof LegalRepresentative)) {
            throw ExceptionUtils.createUnProcessableDataException('attorney.referral.not.found', null, [email])
        }
        accountService.addReferralBonus(employee as LegalRepresentative, referee)
    }

    @Transactional
    void sendInviteToColleagues(InviteColleaguesCommand inviteColleaguesCommand, Long attorneyId) {
        evMailService.validateVariables(inviteColleaguesCommand.templateType, inviteColleaguesCommand.subject, inviteColleaguesCommand.content)
        runAsync({
            inviteColleaguesCommand.splitEmails.each {
                String emailId = it.trim();
                LegalRepresentative attorney = LegalRepresentative.findById(attorneyId);
                AdminConfig adminConfig = adminService.adminSettings.adminConfig

                Map params = [:]
                params = emailVariableService.addLegalRepresentative(params, attorney)
                params = emailVariableService.addAdminConfig(params, adminConfig)

                String content = StringUtils.textToHTML(inviteColleaguesCommand.content)
                String subject = evMailService.evaluateTemplate(inviteColleaguesCommand.subject, params)
                String mailContent = evMailService.evaluateTemplate(content, params)
                evMailService.sendEmail(evMailService.buildInviteToColleaguesEmailDto(attorney, emailId, subject, mailContent))
            }
        }, "Invite Colleagues of Attorney [${attorneyId}]")

    }

    private void updateTemplate(AttorneyNotificationCommand it, EmailTemplate email) {
        email.content = it.content
        email.subject = it.subject
        email.preference.repeatInterval = it.repeatInterval
        if (it.repeatInterval) {
            email.preference.preference = Boolean.TRUE
        } else {
            email.preference.preference = Boolean.FALSE
        }
    }

    private Long getArticlesCount(LegalRepresentative attorney, Date lower = null, Date upper = null) {
        Article.createCriteria().get {
            projections {
                count('id')
            }
            eq('isApproved', true)
            eq('author', attorney)
            if (lower) {
                ge('dateSubmitted', lower)
            }
            if (upper) {
                le('dateSubmitted', upper)
            }
        } as Long
    }

    private Integer calculateRank(RankScore base, Long total, Long less30, Long between31and90, Long between91and180) {
        base.pointsLifetime * total + base.points30 * less30 + base.points31To90 * between31and90 + base.points91To180 * between91and180
    }

    private Date getLowerDate(Integer shift) {
        Calendar cal = Calendar.getInstance()
        cal.clearTime()
        cal.add(Calendar.DAY_OF_MONTH, shift)
        cal.getTime()
    }

    private Date getUpperDate(Integer shift) {
        Calendar cal = Calendar.getInstance()
        cal.clearTime()
        cal.add(Calendar.DAY_OF_MONTH, shift + 1)
        cal.add(Calendar.MILLISECOND, -1)
        cal.getTime()
    }

    private void runAsync(Runnable command, String name) {
        asyncService.runAsync(command, name)
    }

}
