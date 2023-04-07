package com.easyvisa

import com.easyvisa.enums.AttorneyType
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.utils.ExceptionUtils
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.http.HttpStatus

class PermissionsService {

    def attorneyService
    SpringSecurityService springSecurityService
    GrailsApplication grailsApplication

    def validateCreateReview(Applicant reviewer, Package aPackage, LegalRepresentative representative) {
        if (Review.findByAPackageAndRepresentative(aPackage, representative)) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'review.attorney.already.reviewed')
        }
        validateApplicantForPackage(reviewer.profile, aPackage)
        validateRepresentativeForPackage(aPackage, representative)
    }

    @Transactional
    void validatePackageReadAccess(User user, Package aPackage) {
        if (user.isApplicant()) {
            if (!isApplicantForPackage(user.profile, aPackage)) {
                throw ExceptionUtils.createPackageAccessDeniedException()
            }
        } else if (user.isRepresentative() || user.isEmployee()) {
            Employee employee = attorneyService.findEmployeeByUser(user.id)
            List<Organization> employeeActiveOrganizations = OrganizationEmployee.findAllByEmployeeAndStatus(employee, EmployeeStatus.ACTIVE)*.organization
            if (!employeeActiveOrganizations.contains(aPackage.organization)) {
                throw ExceptionUtils.createPackageAccessDeniedException()
            }
        }
    }

    @Transactional
    void validatePackageWriteAccess(User user, Package aPackage, String errorMessageCode = null) {
        if (user.isApplicant()) {
            if (!isApplicantForPackage(user.profile, aPackage)) {
                throw ExceptionUtils.createPackageAccessDeniedException()
            }
        } else if (user.isRepresentative() || user.isEmployee()) {
            validateEmployeeNonTraineePosition(user, aPackage.organization, errorMessageCode)
        }
    }

    @Transactional
    void validateEmployeeNonTraineePosition(User user, Organization organization, String errorMessageCode = null) {
        Employee employee = attorneyService.findEmployeeByUser(user.id)
        List<Organization> employeeActiveOrganizations =
                OrganizationEmployee.findAllByEmployeeAndStatusAndPositionNotEqual(employee, EmployeeStatus.ACTIVE,
                        EmployeePosition.TRAINEE)*.organization
        if (!employeeActiveOrganizations.find { it.id == organization.id }) {
            throw ExceptionUtils.createPackageAccessDeniedException(errorMessageCode)
        }
    }

    Boolean isApplicantForPackage(Profile profile, Package aPackage) {
        aPackage.beneficiaries.find { it.profile == profile } || aPackage.petitioner?.profile == profile
    }

    Boolean validateApplicantForPackage(Profile profile, Package aPackage) {
        if (!isApplicantForPackage(profile, aPackage)) {
            throw ExceptionUtils.createPackageAccessDeniedException()
        }
    }

    void assertIsAdmin(User user, Organization organization) {
        checkUserInOrgs(user, [organization], true)
    }

    /**
     * Asserts if current logged in user can manage targetUser data.
     * @param targetUser user to check for edit data
     * @param organizations optional list of organization to check against. If not provided will check against all
     * available organizations to users
     * @param skipActiveCheck skips checking if target user is active. ATTENTION: it should be used for get balance only.
     * to change the profile
     */
    void assertEditAccess(User targetUser, List<Organization> organizations = null,
                          Boolean skipActiveCheck = Boolean.FALSE, Boolean isAdmin = Boolean.TRUE) {
        if (!skipActiveCheck) {
            assertIsActive(targetUser)
        }
        User admin = springSecurityService.currentUser
        if (springSecurityService.currentUserId != targetUser.id) {
            List<Organization> orgs = organizations
            if (!orgs) {
                orgs = getUserOrgs(targetUser)
            }
            checkUserInOrgs(admin, orgs, isAdmin)
        }
    }

    private void checkUserInOrgs(User user, List<Organization> organizations, Boolean isAdmin = Boolean.FALSE) {
        Long count = 0

        if (organizations) {
            count = OrganizationEmployee.createCriteria().get {
                projections {
                    rowCount()
                }
                employee {
                    profile {
                        eq('user', user)
                    }
                }
                'in'('organization', organizations)
                eq('status', EmployeeStatus.ACTIVE)
                if (isAdmin) {
                    eq('isAdmin', true)
                }
                isNull('inactiveDate')
            } as Long
        }
        if (count == 0) {
            throw (ExceptionUtils.createAccessDeniedException('user.is.not.admin'))
        }
    }

    /**
     * Asserts if current logged in user belongs to one org with target user.
     * @param targetUser user to check
     * @param organizations optional list of organization to check against. If not provided will check against all
     * available organizations to users
     */
    void assertBelongToOneOrg(User targetUser, List<Organization> organizations = null) {
        User currentUSer = springSecurityService.currentUser
        if (springSecurityService.currentUserId != targetUser.id) {
            List<Organization> orgs = organizations
            if (!orgs) {
                orgs = getUserOrgs(targetUser)
            }
            checkUserInOrgs(currentUSer, orgs)
        }
    }

    private List<Organization> getUserOrgs(User targetUser) {
        OrganizationEmployee.createCriteria().list {
            projections {
                property('organization')
            }
            employee {
                profile {
                    eq('user', targetUser)
                }
            }
        } as List<Organization>
    }

    def validatePetitionerForPackage(Package packageInstance, Petitioner petitioner) {
        if (packageInstance.petitioner?.id != petitioner.id) {
            throw ExceptionUtils.createPackageAccessDeniedException()
        }
    }

    def validateRepresentativeForPackage(Package packageInstance, LegalRepresentative legalRepresentative) {
        PackageAssignee packageAssignee = packageInstance.assignees.find {
            it.representative.id == legalRepresentative.id
        }
        if (packageAssignee == null) {
            throw ExceptionUtils.createPackageAccessDeniedException('review.attorney.not.belongs.to.package')
        }
    }

    void assertIsActive(User user, Organization organization) {
        Long count = OrganizationEmployee.createCriteria().get {
            projections {
                rowCount()
            }
            employee {
                profile {
                    eq('user', user)
                }
            }
            eq('organization', organization)
            eq('status', EmployeeStatus.ACTIVE)
            isNull('inactiveDate')
        }
        if (count == 0) {
            throw (ExceptionUtils.createAccessDeniedException('user.is.not.active.in.organization'))
        }
    }

    /**
     * Asserts a particular user holds a position among list of positions in organization
     *
     * @param user User
     * @param organization Organization
     * @param requiredPostitions ist<EmployeePosition>
     * @throws EasyVisaException if user position not in required position list
     */
    @Transactional(readOnly = true)
    void assertHasPosition(Organization organization, List<EmployeePosition> requiredPositions, User user) {
        Employee employee = attorneyService.findEmployeeByUser(user.id)

        OrganizationEmployee oe = OrganizationEmployee.findByOrganizationAndEmployeeAndStatus(organization, employee, EmployeeStatus.ACTIVE)

        if (!requiredPositions.contains(oe.position)) {
            throw (ExceptionUtils.createAccessDeniedException('not.enough.permission', null, [employee.profile.title]))
        }
    }

    /**
     * Asserts a particular user has paid all required transaction
     *
     * @param user user to check
     * @throws EasyVisaException if not paid
     */
    void assertIsPaid(User user) {
        if (!user.paid) {
            throw (ExceptionUtils.createAccessDeniedException('user.is.not.paid'))
        }
    }

    /**
     * Asserts that current user has paid all required transaction.
     *
     * @throws EasyVisaException if not paid
     */
    void assertIsPaid() {
        User user = User.get(springSecurityService.currentUserId as Long)
        if (!user.paid) {
            throw (ExceptionUtils.createAccessDeniedException('user.is.not.paid'))
        }
    }


    void assertIsActive(Employee employee) {
        assertIsActive(employee.user)
    }

    void assertIsActive(User user, String errorCode = 'user.is.not.active') {
        if (!user.activeMembership) {
            throw (ExceptionUtils.createAccessDeniedException(errorCode))
        }
    }

    /**
     * Check Attorney is Solo Practitioner
     * @param representative LegalRepresentative to be checked as AttorneyType.SOLO_PRACTITIONER
     * @throws EasyVisaException if the representative is not AttorneyType.SOLO_PRACTITIONER
     */
    void assertSoloPractitioner(LegalRepresentative representative, String legalRepPlaceHolder = 'Representative') {
        if (representative.attorneyType != AttorneyType.SOLO_PRACTITIONER) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'representative.not.solo.practitioner', params: [legalRepPlaceHolder])
        }
    }

    /**
     * Checks if user exist. Otherwise throws exception.
     * @param user user to check
     */
    void assertIsExist(User user) {
        if (!user) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'user.not.found.with.id')
        }
    }

    /**
     * Asserts an user is in blessed organization.
     * @param user to check
     */
    Boolean isBlessed() {
        isBlessed(springSecurityService.currentUser as User)
    }

    /**
     * Asserts an user is in blessed organization.
     * @param user to check
     */
    Boolean isBlessed(User user) {
        String blessedOrgId = grailsApplication.config.easyvisa.blessedOrganizationEVId
        OrganizationEmployee.createCriteria().count {
            employee {
                profile {
                    eq('user', user)
                }

            }
            organization {
                eq('easyVisaId', blessedOrgId)
            }
            eq('status', EmployeeStatus.ACTIVE)
        } > 0
    }

}
