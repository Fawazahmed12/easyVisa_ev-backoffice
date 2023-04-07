package com.easyvisa.enums

enum Degree {

    ASSOCIATE_OF_ARTS('Associate of Arts (AA)'),
    ASSOCIATE_OF_SCIENCE('Associate of Science (AS)'),
    ASSOCIATE_OF_APPLIED_SCIENCE('Associate of Applied Science (AAS)'),
    BACHELOR_OF_ARTS('Bachelor of Arts (BA)'),
    BACHELOR_OF_SCIENCE('Bachelor of Science (BS)'),
    BACHELOR_OF_FINE_ARTS('Bachelor of Fine Arts (BFA)'),
    BACHELOR_OF_APPLIED_SCIENCE('Bachelor of Applied Science (BAS)'),
    MASTER_OF_ARTS('Master of Arts (MA)'),
    MASTER_OF_SCIENCE('Master of Science (MS)'),
    MASTER_OF_BUSINESS_ADMINISTRATION('Master of Business Administration (MBA)'),
    MASTER_OF_FINE_ARTS('Master of Fine Arts (MFA)'),
    DOCTOR_OF_PHILOSOPHY('Doctor of Philosophy (PhD)'),
    JURIS_DOCTOR('Juris Doctor (JD)'),
    DOCTOR_OF_MEDICINE('Doctor of Medicine (MD)'),
    DOCTOR_OF_DENTAL_SURGERY('Doctor of Dental Surgery (DDS)')

    final String displayName

    Degree(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}
