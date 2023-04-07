package com.easyvisa

import com.easyvisa.enums.Degree
import com.easyvisa.enums.Honors
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class Education {
    String school
    Degree degree
    Integer year
    Honors honors

    static constraints = {
        honors nullable: true
        year nullable: true
        school nullable: true
        degree nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'education_id_seq']
    }

}
