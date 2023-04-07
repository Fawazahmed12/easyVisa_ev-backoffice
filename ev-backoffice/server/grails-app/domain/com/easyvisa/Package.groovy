package com.easyvisa

import com.easyvisa.dto.PackageApplicantResponseDto
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.ImmigrationBenefitGroups
import com.easyvisa.enums.NotificationType
import com.easyvisa.enums.PackageAssignmentStatus
import com.easyvisa.enums.PackageBlockedType
import com.easyvisa.enums.PackageStatus
import com.easyvisa.enums.QuestionnaireSyncStatus
import com.easyvisa.questionnaire.model.ApplicantType
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class Package {

    PackageStatus status
    QuestionnaireSyncStatus questionnaireSyncStatus
    PackageBlockedType blockedType

    Petitioner petitioner
    LegalRepresentative attorney
    BigDecimal owed
    Date opened
    Date lastActiveOn
    Date closed
    Organization organization
    Date welcomeEmailSentOn
    Boolean paidByLegalRep

    EasyVisaFile retainerAgreement
    Date dateCreated
    Date lastUpdated
    Set<ImmigrationBenefit> benefits = []
    Set<PackageAssignee> assignees = []
    Set<PackageReminder> packageReminders = []
    String title

    String easyVisaId

    Double questionnaireCompletedPercentage
    Double documentCompletedPercentage

    Profile transferredBy
    Date transferredOn
    Package transferredTo
    LegalRepresentative transferredAttorneyTo

    static hasMany = [benefits:ImmigrationBenefit, assignees:PackageAssignee, additionalFees:AdditionalFee, packageReminders:PackageReminder]

    static constraints = {
        closed nullable: true
        attorney nullable: true

        owed nullable: true
        lastActiveOn nullable: true
        opened nullable: true
        welcomeEmailSentOn nullable: true

        petitioner nullable: true
        benefits validator: { benefits, aPackage, errors ->
            Set<ImmigrationBenefitGroups> benefitGroups = benefits.collect { it.category?.group }
            if (!benefitGroups) {
                errors.rejectValue('benefits', 'benefits.empty')
            } else if (benefitGroups.size() > 1) {
                errors.rejectValue('benefits', 'benefits.morethanonecategory')
            }
        }
        retainerAgreement nullable: true
        organization nullable: true
        paidByLegalRep nullable: true
        title nullable: true
        easyVisaId nullable: true/*, unique: true*/
        blockedType nullable: true
        questionnaireSyncStatus nullable: true
        transferredTo nullable: true
        transferredAttorneyTo nullable: true
        transferredBy nullable: true
        transferredOn nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'package_seq']
    }

    List<Applicant> getBeneficiaries() {
        List<Applicant> applicants = orderedBenefits*.applicant
        applicants
    }

    /**
     * Returns all applicants including petitioner and all beneficiaries
     * @return list of all applicants in a package
     */
    List<Applicant> getApplicants() {
        List<Applicant> applicants = beneficiaries
        if (petitioner) {
            applicants << petitioner.applicant
        }
        applicants
    }

    Applicant getPrincipalBeneficiary() {
        directBenefit?.applicant
    }

    ImmigrationBenefit getDirectBenefit() {
        benefits.find { it.direct }
    }

    List<ImmigrationBenefit> getDerivativeBenefits() {
        orderedBenefits.findAll { !it.direct }
    }

    ImmigrationBenefit getBenefitForApplicant(Applicant applicant) {
        this.benefits.find { it.applicant == applicant }
    }

    ImmigrationBenefit getBenefitForApplicantId(Long applicantId) {
        this.benefits.find { it.applicant.id == applicantId }
    }

    Boolean doesUserBelongToPackage(Applicant applicant) {
        beneficiaries.contains(applicant) || petitioner?.applicant?.id == applicant.id
    }

    Email getWelcomeEmail() {
        Email.findByAPackageAndTemplateType(this, EmailTemplateType.NEW_CLIENT)
    }

    Email getApplicantInviteEmail() {
        Email.findByAPackageAndTemplateType(this, EmailTemplateType.INVITE_APPLICANT)
    }

    PackageReminder getPackageReminder(NotificationType notificationType) {
        packageReminders.find { it.notificationType == notificationType }
    }

    ImmigrationBenefit getImmigrationBenefitByApplicant(Applicant applicant) {
        if (petitioner?.applicant?.id == applicant.id) {
            return directBenefit
        }
        getBenefitForApplicant(applicant)
    }

    PackageAssignee getCurrentAssignee() {
        this.assignees.find { it.endDate == null && it.status == PackageAssignmentStatus.ACTIVE }
    }

    /**
     * Returns petitioner applicant. For no-petitioner packages available beneficiary applicant will be returned.
     * @return applicant
     */
    Applicant getClient() {
        if (petitioner) {
            //getting petitioner applicant
            return petitioner.applicant
        }
        //getting self-petitioner(no-petitioner) applicant
        orderedBenefits.first().applicant
    }

    /**
     * Returns Petitioner and Applicants combined into one list. Petitioner will be the first in the list.
     * Uses for endpoint response to communicated with the UI.
     * @return list
     */
    List<PackageApplicantResponseDto> getPackageApplicantsUi() {
        List<PackageApplicantResponseDto> result = []
        if (petitioner) {
            result << new PackageApplicantResponseDto(petitioner)
        }
        orderedBenefits.each {
            result << new PackageApplicantResponseDto(it)
        }
        result
    }

    /**
     * Detects applicant id for applicant id.
     * @param applicantId applicant id
     * @return applicant type
     */
    ApplicantType getApplicantType(Long applicantId) {
        if (applicantId == petitioner?.applicant?.id) {
            return ApplicantType.Petitioner
        } else if (getBenefitForApplicantId(applicantId).direct) {
            return ApplicantType.Beneficiary
        }
        ApplicantType.Derivative_Beneficiary
    }

    String getCategories() {
        orderedBenefits.sort {!it.direct }.collect { it.category.abbreviation }.join(', ')
    }

    /**
     *
     * This method orders the benefits based on 'sortPosition' and returns List of ImmigrationBenefit
     * @return list of ordered benefits
     */
    List<ImmigrationBenefit> getOrderedBenefits() {
        List<ImmigrationBenefit> orderedBenefits = benefits.sort { it['sortPosition'] }
        orderedBenefits
    }

    /**
     *
     * This method orders the assignees based on 'startDate' and returns List of PackageAssignee
     * @return list of ordered assignees
     */
    List<PackageAssignee> getOrderedAssignees() {
        List<PackageAssignee> orderedAssignees = assignees.sort { it['startDate'] }
        orderedAssignees
    }

    /**
     * Package copy for Transferring
     * @return new package
     */
    Package copy(LegalRepresentative newAttorney, Organization newOrg) {
        Package newPackage = new Package()
        newPackage.status = status
        newPackage.questionnaireSyncStatus = questionnaireSyncStatus
        newPackage.blockedType = blockedType
        newPackage.attorney = newAttorney
        //copy pet
        newPackage.petitioner = petitioner?.copy()
        newPackage.opened = opened
        newPackage.lastActiveOn = lastActiveOn
        newPackage.closed = closed
        newPackage.organization = newOrg
        newPackage.welcomeEmailSentOn = welcomeEmailSentOn
        newPackage.paidByLegalRep = paidByLegalRep

        newPackage.dateCreated = dateCreated
        newPackage.lastUpdated = lastUpdated
        //copy applicants
        benefits.each {
            newPackage.benefits.add(it.copy())
        }
        newPackage.addToAssignees(new PackageAssignee(aPackage: newPackage, representative: newAttorney,
                organization: newOrg, startDate: new Date(), status: PackageAssignmentStatus.ACTIVE))
        newPackage.title = title
        newPackage.easyVisaId = easyVisaId
        newPackage.questionnaireCompletedPercentage = questionnaireCompletedPercentage
        newPackage.documentCompletedPercentage = documentCompletedPercentage
        newPackage
    }

}
