package com.easyvisa

import com.easyvisa.enums.Degree
import com.easyvisa.enums.Honors
import com.easyvisa.utils.StringUtils

class EducationCommand {
    String school
    String degree
    Integer year
    String honors
    Long id

    Honors getHonors() {
        StringUtils.asEnum(Honors, honors)
    }

    Degree getDegree() {
        StringUtils.asEnum(Degree, degree)
    }
}

