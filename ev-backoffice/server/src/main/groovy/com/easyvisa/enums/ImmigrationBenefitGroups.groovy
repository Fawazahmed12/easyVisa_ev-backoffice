package com.easyvisa.enums

enum ImmigrationBenefitGroups {

    IMMEDIATE_RELATIVE_VISA('Immediate Relative Visa & Green Card on Arrival (AOS)', 'Immediate Relative'),
    FAMILY_PREFERENCE_VISA('Family Preference - Initial Application', 'Family Preference',  '(I-130 ONLY)'),
    FAMILY_PREFERENCE_VISA_GREEN_CARD('Family Preference Visa & Green Card', 'Family Preference', '(Priority Date Active)'),
    VISA_FIANCE_SPOUSE('Visa - Fiancé(e) of U.S. Citizen', 'Fiancé(e) Visa'),
    NATURALIZATION('Naturalization', 'Citizenship'),
    PERMANENT_RESIDENCE('Permanent Residence' , 'Permanent Residence'),
    MISCELLANEOUS('Miscellaneous', 'Miscellaneous'),
    REMOVE_CONDITIONS('Remove Conditions on Permanent Residence', 'Remove Conditions')

    final String displayName
    //it uses for search pop
    final String shortName
    final String note

    ImmigrationBenefitGroups(String displayName, String shortName, String note = null) {
        this.displayName = displayName
        this.shortName = shortName
        this.note = note
    }

    String getDisplayName() {
        this.displayName
    }

    static List<ImmigrationBenefitGroups> getNoPetitionerGroups() {
        [MISCELLANEOUS, NATURALIZATION]
    }

    static List<ImmigrationBenefitGroups> getBeneficiaryAndDerivativesGroups() {
        [FAMILY_PREFERENCE_VISA, VISA_FIANCE_SPOUSE]
    }

    static List<ImmigrationBenefitGroups> getBeneficiaryAndNoDerivativesGroups() {
        [FAMILY_PREFERENCE_VISA_GREEN_CARD, PERMANENT_RESIDENCE, IMMEDIATE_RELATIVE_VISA, REMOVE_CONDITIONS]
    }

    static List<ImmigrationBenefitGroups> getSearchGroups() {
        [IMMEDIATE_RELATIVE_VISA, FAMILY_PREFERENCE_VISA, FAMILY_PREFERENCE_VISA_GREEN_CARD, VISA_FIANCE_SPOUSE,
         MISCELLANEOUS, PERMANENT_RESIDENCE, REMOVE_CONDITIONS, NATURALIZATION]
    }

    static List<ImmigrationBenefitGroups> getPrincipalBeneficiaryGroups() {
        [FAMILY_PREFERENCE_VISA, VISA_FIANCE_SPOUSE]
    }

}