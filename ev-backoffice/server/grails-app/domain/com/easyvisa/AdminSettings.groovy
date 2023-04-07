package com.easyvisa

class AdminSettings {

    AdminConfig adminConfig

    static constraints = {
        adminConfig nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'admin_settings_id_seq']
    }

}
