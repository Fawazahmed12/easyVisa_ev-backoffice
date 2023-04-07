package com.easyvisa.enums

import com.easyvisa.Role

enum EvAlertSendTo {

    ACC_REPS(legalRepQuery(RepresentativeType.ACCREDITED_REPRESENTATIVE)),
    ATTORNEYS(legalRepQuery(RepresentativeType.ATTORNEY)),
    EMPLOYEES(nonLegalRepQuery(Role.EMPLOYEE)),
    CLIENTS(nonLegalRepQuery(Role.USER)),
    EV_EMPLOYEES(nonLegalRepQuery(Role.EV))

    private static String legalRepQuery(RepresentativeType type) {
        //getting legal representative profile list with active membership
        """select pr.profile from LegalRepresentative pr join pr.profile p join p.user u
                where pr.representativeType = '${type.toString()}' and u.activeMembership = true"""
    }

    private static String nonLegalRepQuery(String role) {
        //getting employees/applicant/ev profiles with registered and active membership users
        """select distinct p from Profile p join p.user u join UserRole ur on ur.user.id = u.id
            join Role r on ur.role.id = r.id where r.authority = '${role}' and u.activeMembership = true"""
    }

    final String query

    EvAlertSendTo(String query) {
        this.query = query
    }

    String getQuery() {
        this.query
    }
}