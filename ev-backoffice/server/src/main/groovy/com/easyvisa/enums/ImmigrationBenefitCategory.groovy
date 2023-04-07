package com.easyvisa.enums

import com.easyvisa.questionnaire.model.ApplicantType

import static com.easyvisa.enums.ImmigrationBenefitGroups.*

import org.apache.commons.lang.StringUtils

enum ImmigrationBenefitCategory {

    IR1('BC_IR1', IMMEDIATE_RELATIVE_VISA, 'IR-1/CR-1', 'IR-1', 'Spouse of Citizen', true),
    IR2('BC_IR2', IMMEDIATE_RELATIVE_VISA, 'IR-2/CR-2', 'IR-2', 'Unmarried Children (under 21) of a Nat. Citizen', true),
    IR5('BC_IR5', IMMEDIATE_RELATIVE_VISA, 'IR-5', 'IR-5', 'Parent of Citizen (age 21 and over)', true),

    F1_A('BC_F1_A', FAMILY_PREFERENCE_VISA, 'F1', 'F1', '(Citizen Petitioner) - Unmarried (age 21 and over) Son/Daughter and Their Minor Children', true),
    F2_A('BC_F2_A', FAMILY_PREFERENCE_VISA, 'F2', 'F2', '(LPR Petitioner) - Spouse and Minor Children, Minor Children and Minor Children, and Unmarried Son/Daughter (age 21 and over) and Minor Children', true),
    F3_A('BC_F3_A', FAMILY_PREFERENCE_VISA, 'F3', 'F3', '(Citizen Petitioner) - Married Son/Daughter , and Their Spouses and Their Minor Children', true),
    F4_A('BC_F4_A', FAMILY_PREFERENCE_VISA, 'F4', 'F4', '(Citizen Petitioner) - Brother/Sister (age 21 and over), and Their Spouses & Minor Children', true),

    F1_B('BC_F1_B', FAMILY_PREFERENCE_VISA_GREEN_CARD, 'F1', 'F1', 'Unmarried (age 21 and over) Sons and Daughters of Citizens and Their Minor Children', true),
    F2_B('BC_F2_B', FAMILY_PREFERENCE_VISA_GREEN_CARD, 'F2', 'F2', 'Spouse, Minor Children, and Unmarried Sons and Daughters (age 21 and over) of LPRs', true),
    F3_B('BC_F3_B', FAMILY_PREFERENCE_VISA_GREEN_CARD, 'F3', 'F3', 'Married Sons and Daughters of Citizens, and Their Spouses and Their Minor Children', true),
    F4_B('BC_F4_B', FAMILY_PREFERENCE_VISA_GREEN_CARD, 'F4', 'F4', 'Brothers & Sisters of Citizens (age 21 and over), & Their Spouses & Minor Children', true),

    K1K3('BC_K1K3', VISA_FIANCE_SPOUSE, 'K-1', 'K-1', 'Fiancé(e)', true, 'The K-1 Fiancé(e) MUST occupy the dark gray panel!'),
    K2K4('BC_K2K4', VISA_FIANCE_SPOUSE, 'K-2', 'K-2', 'Fiancé(e) Children', true, 'K-2 Derivative Beneficiaries can ONLY occupy the 3rd and consecutive (light gray) panels.'),

    NATURALIZATION('BC_Citizenship', ImmigrationBenefitGroups.NATURALIZATION, 'Citizenship (Green Card Holder to U.S. Citizenship)', 'Naturalization', '', false),
    DISABILITY('BC_648', ImmigrationBenefitGroups.NATURALIZATION, '648', '648', 'Medical Certification for Disability Exception', false),

    LPRSPOUSE('BC_SpouseToLPR', PERMANENT_RESIDENCE, 'LPR', 'LPR - Spouse', 'Spouse to LPR', true),
    LPRCHILD('BC_SpouseChildrenToLPR', PERMANENT_RESIDENCE, 'LPR', 'LPR - Child', 'Spouse\'s Children to LPR', true),

    SIX01('BC_601', MISCELLANEOUS, '601', '601', 'Application for Waiver of Grounds on Inadmissibility (Client is OUTSIDE U.S.)', true),
    SIX01A('BC_601A', MISCELLANEOUS, '601A', '601A', 'Application for Provisional Unlawful Presence Waiver (Client is INSIDE U.S.)', true),
    EAD('BC_EAD', MISCELLANEOUS, '765', 'EAD', 'EAD (Employment Authorization Document)', false),

    REMOVECOND('BC_RemoveConditions', REMOVE_CONDITIONS, '751', '751', 'Remove Conditions (2-Year to 10-Year LPR)', true, '(If any applicants within this package were Derivative Beneficiaries AND acquired their conditional residence MORE THAN 90 days after the Principle Beneficiary received his/her conditional residence; OR if the conditional resident parent is deceased, then each of those applicants must apply to Remove Conditions separately in individual packages.)')

    final String easyVisaId //It is same as easyVisId from Neo4J
    final ImmigrationBenefitGroups group
    final String abbreviation
    final String searchAbbreviation
    final String description
    final String note
    final boolean active

    ImmigrationBenefitCategory(String easyVisaId, ImmigrationBenefitGroups group, String abbreviation,
                               String searchAbbreviation, String description, boolean active, String note = null) {
        this.easyVisaId = easyVisaId
        this.group = group
        this.abbreviation = abbreviation
        this.searchAbbreviation = searchAbbreviation
        this.description = description
        this.active = active
        this.note = note
    }

    String getEasyVisaId() {
        return easyVisaId
    }

    ImmigrationBenefitGroups getGroup() {
        this.group
    }

    String getAbbreviation() {
        this.abbreviation
    }

    String getDescription() {
        return description
    }

    boolean getActive() {
        this.active
    }

    boolean equals(ImmigrationBenefitCategory another) {
        if(!another){
            return false;
        }
        return StringUtils.equals(this.easyVisaId, another.easyVisaId);
    }

    ApplicantType detectBeneficiaryType() {
        if (getPrincipalBeneficiaryGroups().contains(this.getGroup())) {
            return ApplicantType.Principal_Beneficiary;
        }
        return ApplicantType.Beneficiary;
    }

    static List<ImmigrationBenefitCategory> getBeneficiaryLprAllowedCategories() {
        [REMOVECOND]
    }

    static List<ImmigrationBenefitCategory> getBeneficiaryAlienAllowedCategories() {
        [IR1, IR2, IR5, F1_A, F2_A, F3_A, F4_A, F1_B, F2_B, F3_B, F4_B, K1K3, LPRSPOUSE, LPRCHILD]
    }

    static List<ImmigrationBenefitCategory> getBeneficiaryAlienNoPetitionerAllowedCategories() {
        [SIX01, SIX01A, EAD]
    }

    static List<ImmigrationBenefitCategory> getCategoriesForUsCitizenPetitioner() {
        [IR1, IR2, IR5, F1_A, F3_A, F4_A, F1_B, F3_B, F4_B, K1K3, LPRSPOUSE, LPRCHILD, REMOVECOND]
    }

    static List<ImmigrationBenefitCategory> getCategoriesForLprPetitioner() {
        [F2_A, F2_B]
    }

    static List<ImmigrationBenefitCategory> getDerivativeBeneficiaryAllowedCategories() {
        [F1_A, F2_A, F3_A, F4_A, K2K4]
    }

    static List<ImmigrationBenefitCategory> getDisabledCategoriesForUSCitizenPetitioner() {
        [F2_A, F2_B, K2K4]
    }

    static List<ImmigrationBenefitCategory> getDisabledCategoriesForLPRPetitioner() {
        [F1_A, F1_B, F3_A, F3_B, F4_A, F4_B, K1K3, K2K4, IR1, IR2, IR5, LPRSPOUSE, LPRCHILD, REMOVECOND]
    }

    static List<ImmigrationBenefitCategory> getDisabledCategoriesForUSDerivativeApplicants() {
        [K1K3, F2_A]
    }

    static List<ImmigrationBenefitCategory> getDisabledCategoriesForLPRDerivativeApplicants() {
        [K1K3, K2K4, F1_A, F3_A, F4_A]
    }

    static List<ImmigrationBenefitCategory> getCategoriesForNativeAlphabetForms() {
        [IR1, IR2, IR5, F1_A, F2_A, F3_A, F4_A, K1K3, K2K4]
    }

    static ImmigrationBenefitCategory getImmigrationBenefitCategoryByEasyVisaId(String easyVisaId) {
        return values().find {
            it.easyVisaId==easyVisaId
        }
    }

}
