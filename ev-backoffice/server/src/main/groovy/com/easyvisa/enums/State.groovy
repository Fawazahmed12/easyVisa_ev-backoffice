package com.easyvisa.enums

enum State {

    ALABAMA("Alabama", "AL"),
    ALASKA("Alaska", "AK"),
    ARIZONA('Arizona', 'AZ'),
    ARKANSAS('Arkansas', 'AR'),
    CALIFORNIA('California', 'CA'),
    COLORADO('Colorado', 'CO'),
    CONNECTICUT('Connecticut', 'CT'),
    DELAWARE('Delaware', 'DE'),
    DC('D.C.', 'DC'),
    FLORIDA('Florida', 'FL'),
    GEORGIA('Georgia', 'GA'),
    HAWAII('Hawaii', 'HI'),
    IDAHO('Idaho', 'ID'),
    ILLINOIS('Illinois', 'IL'),
    INDIANA('Indiana', 'IN'),
    IOWA('Iowa', 'IA'),
    KANSAS('Kansas', 'KS'),
    KENTUCKY('Kentucky', 'KY'),
    LOUISIANA('Louisiana', 'LA'),
    MAINE('Maine', 'ME'),
    MARYLAND('Maryland', 'MD'),
    MASSACHUSETTS('Massachusetts', 'MA'),
    MICHIGAN('Michigan', 'MI'),
    MINNESOTA('Minnesota', 'MN'),
    MISSISSIPPI('Mississippi', 'MS'),
    MISSOURI('Missouri', 'MO'),
    MONTANA('Montana', 'MT'),
    NEBRASKA('Nebraska', 'NE'),
    NEVADA('Nevada', 'NV'),
    NEW_HAMPSHIRE('New Hampshire', 'NH'),
    NEW_JERSEY('New Jersey', 'NJ'),
    NEW_MEXICO('New Mexico', 'NM'),
    NEW_YORK('New York', 'NY'),
    NORTH_CAROLINA('North Carolina', 'NC'),
    NORTH_DAKOTA('North Dakota', 'ND'),
    OHIO('Ohio', 'OH'),
    OKLAHOMA('Oklahoma', 'OK'),
    OREGON('Oregon', 'OR'),
    PENNSYLVANIA('Pennsylvania', 'PA'),
    RHODE_ISLAND('Rhode Island', 'RI'),
    SOUTH_CAROLINA('South Carolina', 'SC'),
    SOUTH_DAKOTA('South Dakota', 'SD'),
    TENNESSEE('Tennessee', 'TN'),
    TEXAS('Texas', 'TX'),
    UTAH('Utah', 'UT'),
    VERMONT('Vermont', 'VT'),
    VIRGINIA('Virginia', 'VA'),
    WASHINGTON('Washington', 'WA'),
    WEST_VIRGINIA('West Virginia', 'WV'),
    WISCONSIN('Wisconsin', 'WI'),
    WYOMING('Wyoming', 'WY'),
    AMERICAN_SAMOA('American Samoa', 'AS'),
    GUAM('Guam', 'GU'),
    NORTHERN_MARIANA_ISLANDS('Northern Mariana Islands', 'MP'),
    PUERTO_RICO('Puerto Rico', 'PR'),
    US_VIRGIN_ISLANDS('U.S. Virgin Islands', 'VI'),
    FEDERATED_STATES_OF_MICRONESIA('Federated States of Micronesia', 'FM'),
    MARSHALL_ISLANDS('Marshall Islands', 'MH'),
    PALAU('Palau', 'PW'),
    ARMED_FORCES_AFRICA('Armed Forces Africa', 'AE'),
    ARMED_FORCES_AMERICAS('Armed Forces Americas', 'AA'),
    ARMED_FORCES_CANADA('Armed Forces Canada', 'AE'),
    ARMED_FORCES_EUROPE('Armed Forces Europe', 'AE'),
    ARMED_FORCES_MIDDLE_EAST('Armed Forces Middle East', 'AE'),
    ARMED_FORCES_PACIFIC('Armed Forces Pacific', 'AP')

    final String displayName
    final String code

    State(String displayName, String code) {
        this.displayName = displayName
        this.code = code
    }

    String getDisplayName() {
        this.displayName
    }

    String getCode() {
        this.code
    }

    static State valueOfDisplayName(String name) {
        return values().find { it.displayName.equals(name) }
    }

    static State valueOfCode(String code) {
        return values().find { it.code.equals(code) }
    }

}
