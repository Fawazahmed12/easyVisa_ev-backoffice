package com.easyvisa

import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.enums.NotificationType
import com.easyvisa.enums.PackageStatus
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.utils.ExceptionUtils
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import org.apache.http.HttpStatus
import org.springframework.web.multipart.MultipartFile

@SuppressWarnings(['FactoryMethodName'])
class ProfileService {

    AlertService alertService
    ApplicantService applicantService
    AttorneyService attorneyService
    EvMailService evMailService
    OrganizationService organizationService
    SpringSecurityService springSecurityService
    TokenGenerator tokenGenerator
    TokenStorageService tokenStorageService
    FileService fileService
    ProcessService processService
    AsyncService asyncService

    @Transactional
    EasyVisaIdGeneratable addEasyVisaId(EasyVisaIdGeneratable easyVisaIdGeneratable) {
        easyVisaIdGeneratable.profile.easyVisaId = easyVisaIdGeneratable.easyVisaId
        easyVisaIdGeneratable
    }

    @Transactional
    Applicant createEasyVisaClient(Applicant applicant) {
        addEasyVisaId(applicant)
        applicant.save(failOnError: true)
        if (applicant.user) {
            Role userRole = Role.findByAuthority(Role.USER)
            if (userRole) {
                UserRole.create(applicant.user, userRole)
            }
            RegistrationCode registrationCode = new RegistrationCode(username: applicant.user.username)
            registrationCode.save(failOnError: true)
            evMailService.sendAttorneyRegistrationEmail(applicant.profile, registrationCode)
        }
        applicant
    }

    Profile profileFromApplicantCommand(ApplicantCommand applicantCommand, Profile profile = null) {
        profile = profile ?: new Profile()
        ApplicantProfileCommand profileCommand = applicantCommand.profile
        profile.with {
            firstName = profileCommand.firstName
            middleName = profileCommand.middleName
            lastName = profileCommand.lastName
            email = profileCommand.email
        }
        if (profileCommand.username) {
            User user = profile.user ?: new User()
            user.username = profileCommand.username
        }
        profile
    }

    Applicant populateApplicantFields(Applicant applicant, ApplicantCommand applicantCommand) {
        ApplicantProfileCommand profile = applicantCommand.profile
        applicant.with {
            it.profile.address = profile.homeAddress
            mobileNumber = profile.mobileNumber
            homeNumber = profile.homeNumber
            workNumber = profile.workNumber
            dateOfBirth = profile.dateOfBirth
        }
        applicant
    }

    Petitioner createPetitioner(ApplicantCommand applicantCommand, Package aPackage) {
        if (applicantCommand.id == null && !(applicantCommand?.profile?.email)) {
            throw ExceptionUtils.createUnProcessableDataException('petitioner.email.missing')
        }
        if (aPackage.petitioner) {
            ProcessRequest request = PackageOptInForPetitionerRequest.findByPetitioner(aPackage.petitioner)
            processService.deleteProcessRequest(request)
        }

        Petitioner petitioner = new Petitioner(citizenshipStatus: applicantCommand.citizenshipStatus, optIn: applicantCommand.optIn)
        Applicant applicant = applicantCommand.id ? Applicant.get(applicantCommand.id) : createApplicant(applicantCommand, aPackage)
        petitioner.applicant = applicant

        if (applicantCommand.id && applicant.user) {
            petitioner.optIn = ProcessRequestState.PENDING
        } else {
            petitioner.optIn = ProcessRequestState.ACCEPTED
        }
        return petitioner
    }

    Applicant createApplicant(ApplicantCommand applicantCommand, Package aPackage) {
        if (applicantCommand.inviteApplicant && !(applicantCommand?.profile?.email)) {
            throw ExceptionUtils.createUnProcessableDataException('invited.applicant.email.missing')
        }
        Profile profile = profileFromApplicantCommand(applicantCommand)
        Applicant applicant = new Applicant(profile: profile, inviteApplicant: applicantCommand.inviteApplicant, dateOfBirth: applicantCommand.profile.dateOfBirth)
        applicant = populateApplicantFields(applicant, applicantCommand)
        addEasyVisaId(applicant) as Applicant
        applicant.save(failOnError: true)
    }

    Applicant updateApplicant(Applicant applicant, ApplicantCommand changes, Package aPackage) {
        Profile profileWithUser = null
        if (changes.inviteApplicant) {
            profileWithUser = findProfileWithUserByEmail(changes.profile.email)
        }

        if (aPackage.status == PackageStatus.LEAD && profileWithUser && !applicant.user && applicant.profile.email != changes.profile.email) {
            Applicant existingApplicant = Applicant.findByProfile(profileWithUser)
            if (!existingApplicant) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_LOCKED, errorMessageCode: 'user.exist.but.not.applicant')
            }
            applicantService.deleteApplicantWithOutUser(applicant)
            existingApplicant.inviteApplicant = false
            return existingApplicant
        }

        if (aPackage.status == PackageStatus.OPEN) {
            if (applicant.user && applicant.profile.email != changes.profile.email) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_FORBIDDEN, errorMessageCode: 'email.can.not.be.changed.for.registered.user')
            }

            if (attorneyService.findEmployeeByEmail(changes.profile.email)) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_FORBIDDEN, errorMessageCode: 'employee.accounts.can.not.be.added.as.applicant.in.package')
            }

            if (profileWithUser && applicant.profile.email != profileWithUser.email) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_FORBIDDEN, errorMessageCode: 'new.email.is.attached.to.an.existing.user', params: profileWithUser.fullName)
            }
        }

        applicant.profile = profileFromApplicantCommand(changes, applicant.profile)

        if (Petitioner.findByApplicant(applicant) || (aPackage.petitioner == null && aPackage.benefits.size() <= 1)) {
            applicant.inviteApplicant = true
        } else {
            applicant.inviteApplicant = changes.inviteApplicant
        }

        applicant
    }

    @Transactional
    Profile createUserForProfile(UserRegistrationCommand registrationCommand) {
        Profile profile = Profile.findByEasyVisaId(RegistrationCode.findByToken(registrationCommand.token).easyVisaId)
        if (profile.user) {
            throw ExceptionUtils.createUnProcessableDataException('user.already.registered.with.token')
        }
        if (profile) {
            profile.with {
                firstName = registrationCommand.firstName
                lastName = registrationCommand.lastName
                middleName = registrationCommand.middleName
                user = new User(username: registrationCommand.username, password: registrationCommand.password, accountLocked: false)
            }
            profile.user.save(failOnError: true)
            if (Applicant.countByProfile(profile) > 0) {
                new UserRole(user: profile.user, role: Role.findByAuthority(Role.USER)).save(failOnError: true)
                asyncService.runAsync({
                    sendApplicantRegisteredAlert(profile.id)
                }, "Send alerts of user registered for Profile [${profile.id}]")
            } else {
                new UserRole(user: profile.user, role: Role.findByAuthority(Role.EMPLOYEE)).save(failOnError: true)

                OrganizationEmployee organizationEmployee = OrganizationEmployee.findByEmployeeAndStatus(
                        Employee.findByProfile(profile), EmployeeStatus.PENDING
                )
                Organization evOrg = organizationService.getBlessedOrganization()
                if (organizationEmployee && evOrg) {
                    //if this user is an EasyVisa employee then adding extra role i.e. ROLE_EV
                    if (organizationEmployee.organization.id == evOrg.id) {
                        new UserRole(user: profile.user, role: Role.findByAuthority(Role.EV)).save()
                    }
                    //Making this employee ACTIVE
                    organizationEmployee.status = EmployeeStatus.ACTIVE
                    organizationEmployee.save()
                }
            }
            profile.save(failOnError: true)
        }
    }

    private void sendApplicantRegisteredAlert(Long profileId) {
        Profile profile = Profile.get(profileId)
        //sends alert to attorney if they configured so
        List<LegalRepresentative> attorneys = Package.createCriteria().listDistinct {
            createAlias('attorney', 'a')
            createAlias('a.profile', 'p')
            createAlias('p.emailPreferences', 'ep')

            projections {
                property 'attorney'
            }
            benefits {
                applicant {
                    eq('profile', profile)
                }
            }
            and {
                eq('ep.type', NotificationType.APPLICANT_REGISTRATION)
                eq('ep.preference', Boolean.TRUE)
            }
        } as List<LegalRepresentative>
        EasyVisaSystemMessageType alertType = EasyVisaSystemMessageType.PACKAGE_APPLICANT_REGISTERED
        String body = alertService.renderTemplate(alertType.templatePath, [applicantName: profile.name])
        attorneys.each {
            alertService.createAlert(alertType, it.user, Alert.EASYVISA_SOURCE, body)
        }
    }

    @Transactional
    AccessToken loginUser(Profile profile) {
        User user = profile.user
        try {
            springSecurityService.reauthenticate(user.username)
            AccessToken accessToken = tokenGenerator.generateAccessToken(springSecurityService.principal)
            tokenStorageService.storeToken(accessToken.accessToken, accessToken.principal)
            accessToken
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    @Transactional
    setProfilePicture(Profile profile, MultipartFile file) {
        profile.profilePhoto = fileService.addProfilePhoto(profile, file)
        profile.save(failOnError: true)
    }

    @Transactional
    Profile saveProfile(Profile profile) {
        profile.save(failOnError: true)
        return profile
    }

    @Transactional
    void createUserVerifyTokenAndSendEmail(Profile profile) {
        RegistrationCode registrationCode = new RegistrationCode(username: profile.user.username)
        registrationCode.save(failOnError: true)
        evMailService.sendAttorneyRegistrationEmail(profile, registrationCode)
    }

    @Transactional
    void nullifyProfileData(Profile profile) {
        if (profile) {
            Address addressToDelete = profile.address
            EasyVisaFile file = profile.profilePhoto
            profile.with {
                email = null
                language = null
                user = null
                profilePhoto = null
                address = null
            }
            profile.save(failOnError: true)
            addressToDelete?.delete(failOnError: true)
            fileService.deleteEasyVisaFile(file)
        }
    }

    @Transactional
    void nullifyClientProfileData(Profile profile) {
        if (profile) {
            EasyVisaFile file = profile.profilePhoto
            profile.with {
                user = null
                profilePhoto = null
            }
            profile.save(failOnError: true)
            fileService.deleteEasyVisaFile(file)
        }
    }

    /**
     * Updates profile's email and sends alert to the user.
     * @param profile profile
     * @param email new email
     * @param skipAudit skips audit log adding and sending alert (applied to non registers employees)
     */
    @Transactional
    void updateProfileEmail(Profile profile, String email, Boolean skipAudit = Boolean.FALSE) {
        String currEmail = profile.email
        if (email && !(currEmail == email)) {
            Profile exist = this.findProfileByEmail(email)
            if (exist) {
                throw ExceptionUtils.createUnProcessableDataException('profile.email.unique')
            }
            if (!skipAudit) {
                new AuditLog(username: profile.user.username, event: 'EMAIL_CHANGED', oldValue: currEmail, newValue: email)
                        .save(failOnError: true)
                alertService.createAlert(EasyVisaSystemMessageType.PROFILE_EMAIL_CHANGED,
                        profile.user)
            }
            profile.email = email
            profile.save(failOnError: true)
        }
    }

    /**
     * Updates profile's email preferences.
     * @param profile profile
     * @param emailPreferencesCommand new email preferences command
     * @return updated profile
     */
    @Transactional
    Profile updateProfileEmailPreferences(Profile profile, EmailPreferencesCommand emailPreferencesCommand) {
        profile.emailPreferences*.delete(failOnError: true)
        profile.emailPreferences.clear()
        Set<EmailPreference> emailPreferences = []
        emailPreferencesCommand.preferences.each {
            NotificationType currentType = it.type
            if (emailPreferences.find { it.type == currentType }) {
                throw ExceptionUtils.createUnProcessableDataException('profile.email.preferences.dupe', null, [it.type])
            }
            emailPreferences << new EmailPreference(type: currentType, preference: it.preference).save(failOnError: true)
        }
        profile.emailPreferences.addAll(emailPreferences)
        profile.save(failOnError: true)
    }

    /**
     * This method gets Profile object matching email
     * @param email Email to find Profile
     * @return Profile This returns Profile instance if found
     */
    Profile findProfileByEmail(String email) {
        Profile profile = Profile.createCriteria().get {
            eq('email', email, [ignoreCase: true])
        }
        profile
    }

    /**
     * This method gets Profile object matching email which has no User attached.
     * @param email Email to find Profile
     * @return Profile This returns Profile instance if found
     */
    Profile findProfileWithUserByEmail(String email) {
        Profile profile = Profile.createCriteria().get {
            eq('email', email, [ignoreCase: true])
            isNotNull('user')
        }
        profile
    }


    /**
     * This method gets Profile object matching email (which has no User attached) and easyVisaId.
     * @param email Email to find Profile
     * @param easyVisaId EasyVisaId to find Profile
     * @return Profile This returns Profile instance if found
     */
    Profile findProfileByEmailAndEasyVisaId(String email, String easyVisaId) {
        Profile profile = Profile.createCriteria().get {
            eq('email', email, [ignoreCase: true])
            eq('easyVisaId', easyVisaId)
        }
        profile
    }


    /**
     * Set blessed roles correctly based on blessed orgs. Makes sure that all users that are in blessed org
     * have EV role, and users that are not in blessed org do not have that role. It is safe to call this method
     * multiple times.
     */
    @Transactional
    void setBlessedOrgRoles() {
        Organization blessedOrganization = organizationService.blessedOrganization
        if (blessedOrganization) {
            log.info("*** Checking EV Access is started *** ")
            List<Role> evRoles = Role.findAllByAuthorityInList([Role.EV, Role.OWNER])
            List<UserRole> currentBlessedUserRoles = UserRole.createCriteria().list {
                'in'('role', evRoles)
            } as List<UserRole>
            List<OrganizationEmployee> currentBlessedOrgEmployees = OrganizationEmployee.createCriteria().list {
                eq('organization', blessedOrganization)
                eq('status', EmployeeStatus.ACTIVE)
            } as List<OrganizationEmployee>

            List<OrganizationEmployee> existingOrgEmployees = currentBlessedOrgEmployees.findAll { currentBlessedUserRoles*.user.contains(it.user) }
            List<UserRole> userRolesToDelete = currentBlessedUserRoles.findAll { !(existingOrgEmployees*.user.contains(it.user)) }
            if (!userRolesToDelete.isEmpty()) {
                log.info("*** Deleting EV Access to ${userRolesToDelete*.user*.id} *** ")
                userRolesToDelete*.delete(failOnError: true)
            }

            currentBlessedOrgEmployees.each { orgEmployee ->
                Role roleToSet = evRoles.find { it.authority == Role.EV }
                if (orgEmployee.position == EmployeePosition.PARTNER) {
                    roleToSet = evRoles.find { it.authority == Role.OWNER }
                }
                List<UserRole> roles = currentBlessedUserRoles.findAll { it.user.id == orgEmployee.user.id }
                UserRole existingRole = null
                if (roles.size() > 1) {
                    log.info("*** Multiple EV Access to ${orgEmployee.user.id}. Deleting redundand. *** ")
                    existingRole = roles.find { it.role.authority == roleToSet.authority }
                    roles.each {
                        if (it.role.authority != roleToSet.authority) {
                            UserRole.remove(it.user, it.role)
                        }
                    }
                } else if (roles.size() == 1) {
                    existingRole = roles.first()
                }
                if (existingRole && existingRole.role.authority != roleToSet.authority) {
                    log.info("*** Changing ${orgEmployee.user.id} EV Access to ${roleToSet.authority} *** ")
                    existingRole.delete(failOnError: true)
                    new UserRole(user: orgEmployee.employee.user, role: roleToSet).save(failOnError: true)
                } else if (!existingRole) {
                    log.info("*** Adding EV Access (${roleToSet.authority}) to ${orgEmployee.user.id} *** ")
                    new UserRole(user: orgEmployee.employee.user, role: roleToSet).save(failOnError: true)
                }
            }
            log.info("*** Checking EV Access is finished *** ")
        } else {
            log.error "No Blessed org set, so not setting any blessed Roles."
        }
    }

}
