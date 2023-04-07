package com.easyvisa

import com.easyvisa.enums.*
import com.easyvisa.utils.ExceptionUtils
import com.nulabinc.zxcvbn.Strength
import com.nulabinc.zxcvbn.Zxcvbn
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.rest.token.AccessToken
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.apache.http.HttpStatus
import ua_parser.Client
import ua_parser.Parser

import java.time.LocalDateTime

@Transactional
class UserService {

    AttorneyService attorneyService
    EmployeeService employeeService
    ApplicantService applicantService
    AccountService accountService
    ProfileService profileService
    AnswerService answerService
    EvMailService evMailService
    OrganizationService organizationService
    FileService fileService
    AlertService alertService
    SpringSecurityService springSecurityService
    Parser parser = new Parser()

    void changePassword(String password, String token) {
        RegistrationCode registrationCode = token ? RegistrationCode.findByToken(token) : null

        if (!registrationCode) {
            throw new EasyVisaException(errorCode:HttpStatus.SC_NOT_FOUND,
                    errorMessageCode:'reset.password.token.not.found')
        }

        use(TimeCategory) {
            if (new Date() > (registrationCode.dateCreated + 1.hours)) {
                throw ExceptionUtils.createUnProcessableDataException('reset.password.token.expired')
            }
        }

        measurePassword(password)
        User user = User.findByUsername(registrationCode.username)
        user.password = password
        user.passwordExpired = false
        user.save(failOnError:true)
        registrationCode.delete(failOnError:true)
        RegistrationCode.findAllByUsername(user.username)*.delete(failOnError:true)
    }

    /**
     * Changes user password from My Account page.
     * @param user user
     * @param changePasswordCommand parameters
     * @return new auth code
     */
    String changePassword(User user, ChangePasswordCommand changePasswordCommand) {
        //checking current password is correct
        if (springSecurityService.passwordEncoder.matches(changePasswordCommand.oldPassword, user.password)) {
            //checking old and new password are not the same
            if (changePasswordCommand.oldPassword == changePasswordCommand.newPassword) {
                throw ExceptionUtils.createUnProcessableDataException('user.previous.password')
            }
            measurePassword(changePasswordCommand.newPassword)
            user.password = changePasswordCommand.newPassword
            user.save(failOnError:true)
            AuthenticationToken.executeUpdate('delete from AuthenticationToken where username = :username', [username: user.username])
            AccessToken accessToken = profileService.loginUser(user.profile)
            alertService.createAlert(EasyVisaSystemMessageType.USER_PASSWORD_CHANGED, user)
            accessToken.accessToken
        } else {
            throw ExceptionUtils.createUnProcessableDataException('user.wrong.password')
        }
    }

    private void measurePassword(String password) {
        Zxcvbn zxcvbn = new Zxcvbn()
        Strength strength = zxcvbn.measure(password)

        if (password?.length() < 12 || strength.score < 3) {
            throw ExceptionUtils.createUnProcessableDataException('reset.password.validation.error')
        }
    }

    void verifyUser(String token) {
        RegistrationCode registrationCode = token ? RegistrationCode.findByToken(token) : null
        if (!registrationCode) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'registration.password.token.not.found')
        }

        TimeDuration timeDuration = TimeCategory.minus(new Date(), registrationCode.dateCreated)
        User user = User.findByUsername(registrationCode.username)
        //more then an hour
        if (timeDuration.toMilliseconds() > 3600000L) {
            throw new VerifyTokenExpiredException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'verify.email.resent', params: [user.profile.email], profile: user.profile)
        }
        user.isEmailVerified = true
        user.accountLocked = false
        LegalRepresentative representative = attorneyService.findAttorneyByUser(user.id)

        if (representative) {
            representative.registrationStatus = RegistrationStatus.EMAIL_VERIFIED
            //next 3 lines were moved from deleted registration step - select representative type
            //left other workflow as is in order to further possible reuse
            representative.attorneyType = AttorneyType.SOLO_PRACTITIONER
            representative.representativeType = RepresentativeType.ATTORNEY
            representative.registrationStatus = RegistrationStatus.REPRESENTATIVE_SELECTED
            representative.save(failOnError: true)
        }
        user.save(failOnError: true)
        registrationCode.delete()
    }

    Profile findProfileByToken(String token) {
        RegistrationCode registrationCode = RegistrationCode.findByToken(token)
        if (!registrationCode) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'registration.password.token.not.found')
        }
        Profile.findByEasyVisaId(registrationCode.easyVisaId)
    }

    List<String> getUserRoles(User user) {
        UserRole.findAllByUser(user)*.role*.authority
//        UserRole.createCriteria().list {
//            'role' {
//                projections {
//                    property('authority')
//                }
//            }
//            'user' {
//                eq('id', userId)
//            }
//        } as List<String>
    }

    void reactivateUser(User user) {
        User storedUser = User.get(user.id)
        accountService.reactivationCharge(storedUser)
        storedUser.activeMembership = true
        storedUser.reactivationDate = new Date()
        storedUser.save(failOnError: true)
    }

    void deactivateUser(User user) {
        User storedUser = User.get(user.id)
        storedUser.activeMembership = false
        storedUser.save(failOnError: true)
        accountService.deletePaymentMethod(storedUser)
        //deactivate legal representative stuff if exist
        if (storedUser.isRepresentative()) {
            LegalRepresentative legalRepresentative = attorneyService.findAttorneyByUser(storedUser.id)
            //setting packages in all solo-practice to closed
            setAllSoloNonClosedPackagesToClosed(legalRepresentative)
            checkNonSoloOrgPackages(legalRepresentative)
            //checks user is alone admin in any orgs
            employeeService.validateAndNotifyAdminOrgs(legalRepresentative)
            //setting user as inactive in all not solo-practice organizations
            OrganizationEmployee.executeUpdate(
                    '''update OrganizationEmployee oe set status = :status, inactive_date = now()
                        where employee_id = :employeeId and status <> :status and :orgType <>
                        (select o.organizationType from Organization o where o = oe.organization)''',
                    [status : EmployeeStatus.INACTIVE, employeeId: legalRepresentative.id,
                     orgType: OrganizationType.SOLO_PRACTICE])
        } else if (storedUser.isEmployee()) {
            Employee employee = attorneyService.findEmployeeByUser(storedUser.id)
            //checks user is alone admin in any orgs
            employeeService.validateAndNotifyAdminOrgs(employee)
            employeeService.deactivateOrganizationEmployee(employee)
        }
    }

    private void setAllSoloNonClosedPackagesToClosed(LegalRepresentative legalRepresentative) {
        Package.executeUpdate('''update Package p set p.status = :closed, p.closed = now()
                                    where p.status not in (:lead, :closed)
                                    and p.attorney = :attorney and p.organization in
                                    (select distinct o from OrganizationEmployee oe join oe.organization o
                                     where o.organizationType = :orgSolo and oe.employee = :attorney
                                     and oe.status = :active and oe.inactiveDate is null)''',
                [closed : PackageStatus.CLOSED, attorney: legalRepresentative, lead: PackageStatus.LEAD,
                 orgSolo: OrganizationType.SOLO_PRACTICE, active: EmployeeStatus.ACTIVE])
    }

    private void checkNonSoloOrgPackages(LegalRepresentative legalRepresentative) {
        //check active packages in non solo orgs
        List packages = Package.executeQuery('''select count(p.id) from Package p
                                    where p.attorney = :attorney
                                    and p.status <> :closed
                                    and p.organization = (select o from Organization o
                                    where o = p.organization and o.organizationType <> :orgSolo)''',
                [attorney: legalRepresentative, closed: PackageStatus.CLOSED, orgSolo: OrganizationType.SOLO_PRACTICE])
        if (packages && packages.first() > 0) {
            throw ExceptionUtils.createUnProcessableDataException('user.membership.transfer.packages')
        }
        //deactivate package assignments
        Date deactivationDate = new Date()
        Package.executeUpdate('''update PackageAssignee pa
                                    set pa.endDate = :endDate, pa.status = :inactive
                                    where pa.representative = :attorney
                                    and pa.status = :active
                                    and pa.organization = (select o from Organization o
                                    where o = pa.organization and o.organizationType <> :orgSolo)''',
                [endDate: deactivationDate, inactive: PackageAssignmentStatus.INACTIVE,
                 active: PackageAssignmentStatus.ACTIVE, attorney: legalRepresentative,
                 orgSolo: OrganizationType.SOLO_PRACTICE])
    }

    void deleteUser(User user) {
        User storedUser = User.get(user.id)
        //delete legal representative stuff
        if (storedUser.isRepresentative()) {
            LegalRepresentative legalRepresentative = attorneyService.findAttorneyByUser(storedUser.id)
            deleteSoloPackageRetainers(legalRepresentative)
            setAllSoloNonClosedPackagesToClosed(legalRepresentative)
            checkNonSoloOrgPackages(legalRepresentative)
            attorneyService.deleteAttorney(legalRepresentative)
        }
        if (storedUser.isEmployee() || storedUser.isRepresentative()) {
            employeeService.deleteEmployee(attorneyService.findEmployeeByUser(storedUser.id))
        }
        if (storedUser.isApplicant()) {
            Applicant applicant = applicantService.findApplicantByUser(storedUser.id)
            applicantService.deleteApplicant(applicant)
        }
        accountService.deletePaymentMethod(storedUser)
        Alert.executeUpdate('delete from Alert a where a.recipient = :user', [user: storedUser])
        if (!storedUser.isApplicant()) {
            profileService.nullifyProfileData(Profile.findByUser(storedUser))
        } else {
            profileService.nullifyClientProfileData(Profile.findByUser(storedUser))
        }
        UserRole.executeUpdate('delete from UserRole ur where ur.user = :user', [user: storedUser])
        answerService.nullifyUserInAnswers(storedUser)
        UserDevice.executeUpdate('delete from UserDevice ud where ud.user = :user', [user: storedUser])
        storedUser.devices.clear()
        storedUser.delete(failOnError:true)
        List<AuthenticationToken> authenticationTokens = AuthenticationToken.findAllByUsername(storedUser.username)
        authenticationTokens.each {
            it.delete(failOnError:true)
        }
    }

    private void deleteSoloPackageRetainers(LegalRepresentative legalRepresentative) {
        Organization organization = organizationService.getSoloOrganization(legalRepresentative)
        List<Package> packages = Package.findAllByOrganization(organization)
        packages.each {
            if (it.retainerAgreement) {
                fileService.deleteRetainer(it)
            }
        }
    }

    User getUserNameByToken(String token) {
        RegistrationCode registrationCode = token ? RegistrationCode.findByToken(token) : null
        if (!registrationCode) {
            throw ExceptionUtils.createNotFoundException('show.username.token.not.found')
        }
        TimeDuration timeDuration = TimeCategory.minus(new Date(), registrationCode.dateCreated)
        if (timeDuration.hours > 1) {
            throw ExceptionUtils.createUnProcessableDataException('show.username.token.expired')
        }
        final User user = User.findByUsername(registrationCode.username)
        registrationCode.delete()
        user
    }

    void createForgotPasswordTokenAndSendEmail(Profile profile) {
        if (profile?.user) {
            RegistrationCode registrationCode = new RegistrationCode(username: profile.user.username)
            registrationCode.save(failOnError: true)
            evMailService.sendForgotpasswordEmail(profile, registrationCode)
        } else {
            throw ExceptionUtils.createNotFoundException('forgot.password.email.not.found')
        }
    }

    /**
     * Assigns role if that role does not exist for the user and the organization is blessed/EasyVisa organization
     * @param organization Organization object which supposed to be a blessed/EasyVisa organization
     * @param currentPosition EmployeePosition
     * @param User user
     * @param Role role
     */
    def assignRoleForBlessedOrg(Organization organization, EmployeePosition currentPosition, User user, Role role) {
        if (organization.id == organizationService.getBlessedOrganization()?.id) {
            if (currentPosition == EmployeePosition.PARTNER) {
                addRoleIfNotAlreadyPresent(user, Role.findByAuthority(Role.OWNER))
            } else {
                UserRole.findByUserAndRole(user, Role.findByAuthority(Role.OWNER))?.delete()
                addRoleIfNotAlreadyPresent(user, Role.findByAuthority(Role.EV))
            }
        }
    }

    void addRoleIfNotAlreadyPresent(User user, Role role) {
        if (UserRole.countByUserAndRole(user, role) == 0) {
            new UserRole(user: user, role: role).save()
        }
    }
    void markUserUnpaid(User user) {
        if (user.paid) {
            changeUserPaidValue(user, false)
        }
    }

    void successMonthlyChargeActions(Profile profile, Date paymentDate) {
        profile.lastMonthlyPayment = paymentDate
        profile.save(failOnError: true)
        if (!profile.user.paid) {
            changeUserPaidValue(profile.user, true)
        }
    }

    void markUserPaid(User user) {
        if (!user.paid) {
            changeUserPaidValue(user, true)
        }
    }

    private void changeUserPaidValue(User user, Boolean paid) {
        user.paid = paid
        user.save(failOnError: true)
    }

    void checkUserDevice(String userAgent, Long userId) {
        LocalDateTime currDate = LocalDateTime.now()
        User user = User.get(userId)
        boolean knownDevice = false
        Client client = parser.parse(userAgent)
        if (user.devices) {
            for (UserDevice userDevice : user.devices) {
                if (userDevice.userAgent == userAgent) {
                    knownDevice = true
                    break
                }
                Client known = parser.parse(userDevice.userAgent)
                if (client.userAgent.family == known.userAgent.family
                        && (client.userAgent.major as Integer) >= (known.userAgent.major as Integer)
                        && (client.userAgent.minor as Integer) >= (known.userAgent.minor as Integer)
                        && client.os.family == known.os.family
                        && (client.os.major as Integer) >= (known.os.major as Integer)
                        && (client.os.minor as Integer) >= (known.os.minor as Integer)
                ) {
                    knownDevice = true
                    break
                }
            }
        }
        if (!knownDevice) {
            evMailService.sendNewDeviceEmail(user.profile, client, currDate)
        }
        new UserDevice(user: user, userAgent: userAgent).save(failOnError: true)
    }

    void clearUserDevices(Long userId, Date date) {
        User user = User.get(userId)
        List<UserDevice> toRemove = []
        user.devices.each {
            if (it.dateCreated < date) {
                toRemove << it
            }
        }
        user.devices.removeAll(toRemove)
        user.save(failOnError: true)
        UserDevice.deleteAll(toRemove)
    }

}
