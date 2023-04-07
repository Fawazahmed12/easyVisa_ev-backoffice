package com.easyvisa

class RegistrationCode {

    String username
    String token = UUID.randomUUID().toString().replaceAll('-', '')
    Date dateCreated
    String easyVisaId

    static constraints = {
        username nullable: true
        easyVisaId nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'registration_code_id_seq']
        version false
    }
}
