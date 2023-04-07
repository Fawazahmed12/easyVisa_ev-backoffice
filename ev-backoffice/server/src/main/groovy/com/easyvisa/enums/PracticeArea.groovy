package com.easyvisa.enums

enum PracticeArea {

    BANKRUPTCY_AND_DEBT('Bankruptcy and Debt'),
    BUSINESS('Business'),
    CIVIL_RIGHTS('Civil Rights'),
    CONSUMER_PROTECTION('Consumer Protection'),
    CRIMINAL_DEFENSE('Criminal Defense'),
    EMPLOYMENT_AND_LABOR('Employment and Labor'),
    ESTATE_PLANNING('Estate Planning'),
    FAMILY('Family'),
    GOVERNMENT('Government'),
    IMMIGRATION('Immigration'),
    INTELLECTUAL_PROPERTY('Intellectual Property'),
    LAWSUITS_AND_DISPUTES('Lawsuits and Disputes'),
    PERSONAL_INJURY('Personal Injury'),
    REAL_ESTATE('Real Estate')

    final String displayName

    PracticeArea(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}