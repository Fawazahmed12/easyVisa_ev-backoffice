package com.easyvisa

class AuthenticationToken implements Serializable {

    String username
    String tokenValue
    Date lastUsed = new Date()

    static mapping = {
        tokenValue sqlType: 'text'
        lastUsed nullable: true
        id generator: 'native', params: [sequence: 'authentication_token_id_seq']
    }
}
