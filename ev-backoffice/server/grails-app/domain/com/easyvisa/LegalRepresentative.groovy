package com.easyvisa

import com.easyvisa.enums.*
import com.easyvisa.utils.DomainUtils

import java.time.DayOfWeek
import java.util.regex.Pattern
import java.util.stream.Collectors

@SuppressWarnings('GetterMethodCouldBeProperty')
class LegalRepresentative extends Employee {

    String stateBarNumber
    String uscisOnlineAccountNo
    Set<PracticeArea> practiceAreas
    String profileSummary
    String awards
    String experience
    RegistrationStatus registrationStatus = RegistrationStatus.NEW

    RepresentativeType representativeType = RepresentativeType.ATTORNEY
    AttorneyType attorneyType

    String facebookUrl
    String linkedinUrl
    String twitterUrl
    String youtubeUrl
    String websiteUrl
    BigDecimal creditBalance = 0

    LegalRepresentative referrer

    List<Fee> feeSchedule

    Integer topContributorScore = 0
    Integer recentContributorScore = 0
    Integer baseContributorScore = 0
    Float randomScore = 0

    Long publicNoOfReviews = 0
    BigDecimal publicAvgReviewRating = BigDecimal.ZERO
    Long publicNoOfApprovedArticles = 0
    Integer publicMaxYearsLicensed = 0

    static hasMany = [feeSchedule    : Fee,
                      licensedRegions: LicensedRegion,
                      workingHours   : WorkingHour,
                      degrees        : Education,
                      practiceAreas  : PracticeArea]

    static constraints = {
        stateBarNumber nullable: true, unique: true
        uscisOnlineAccountNo nullable: true, validator: {
            uscisOnlineAccountNo, legalRepresentative, errors ->
                String uscisPattern = "\\d{4}-\\d{4}-\\d{4}"
                if (uscisOnlineAccountNo == null || Pattern.matches(uscisPattern, uscisOnlineAccountNo)) {
                    return true
                }
                return errors.rejectValue('uscisOnlineAccountNo', 'uscis.number.format.error')
        }
        profileSummary nullable: true
        awards nullable: true
        experience nullable: true
        practiceAreas nullable: true
        attorneyType nullable: true
        facebookUrl nullable: true
        linkedinUrl nullable: true
        twitterUrl nullable: true
        youtubeUrl nullable: true
        websiteUrl nullable: true
        referrer nullable: true
        creditBalance nullable: true

        workingHours validator: { workingHours ->
            if (workingHours) {
                List<DayOfWeek> weekDays = workingHours*.dayOfWeek
                if ((weekDays as Set).size() != weekDays.size()) {
                    ['workingHours.contains.duplicates']
                }
            }
        }
    }

    boolean getIsAttorney() {
        representativeType == RepresentativeType.ATTORNEY
    }

    boolean getIsAccreditedRepresentative() {
        representativeType == RepresentativeType.ACCREDITED_REPRESENTATIVE
    }

    String getEasyVisaIdPrefix() {
        DomainUtils.EVID_LEGAL_REP_PREFIX
    }

    String getSequenceName() {
        'legal_representative_ev_id_seq'
    }

    boolean getIsRegistered() {
        registrationStatus == RegistrationStatus.COMPLETE
    }

    List<Fee> getCurrentOrganizationFeeSchedule() {
        if (isAttorney) {
            feeSchedule
        }
    }

    List<Fee> getFeeScheduleForUI() {
        List<Fee> result = new ArrayList<>(feeSchedule)
        Fee fee = result.find { it.benefitCategory == ImmigrationBenefitCategory.REMOVECOND }
        if (fee) {
            result.remove(fee)
            result.add(fee)
        }
        List<Fee> filteredResult = result.stream().filter({ it -> it.benefitCategory.active }).collect(Collectors.toList())
        filteredResult
    }

    Fee findFee(ImmigrationBenefitCategory category) {
        feeSchedule.find { it.benefitCategory = category }
    }

    String getAttorneyEmail() {
        profile.email
    }

}

