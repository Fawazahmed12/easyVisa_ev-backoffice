package com.easyvisa

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includeFields = true)
@ToString(includes = 'username', includeNames = true, includePackage = false)
class User implements Serializable {

    private static final long serialVersionUID = -282705813471553006L

    transient springSecurityService

    String username
    String password
    String language
    Date dateCreated
    Date lastUpdated
    Date reactivationDate
    Date lastLogin
    Boolean enabled = Boolean.TRUE
    Boolean accountExpired = Boolean.FALSE
    Boolean accountLocked = Boolean.TRUE
    Boolean passwordExpired = Boolean.FALSE
    Boolean isEmailVerified = Boolean.FALSE
    Boolean activeMembership = Boolean.TRUE
    Boolean paid = Boolean.TRUE
    String fmCustomerId
    List<UserDevice> devices = []

    User(String username, String password) {
        this()
        this.username = username
        this.password = password
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this)*.role
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
        if (isDirty('username')) {
            username = username?.toLowerCase()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true, size: 8..64, matches: /^([a-z]|[0-9]|\.)*$/, validator: { val, obj ->
            final String currentUsername = obj.getPersistentValue('username')
            if (currentUsername && currentUsername != val) {
                ['noteditable']
            }
        }
        password blank: false
        language nullable: true, blank: true
        lastLogin nullable: true
        fmCustomerId nullable: true
        reactivationDate nullable: true
    }

    static mapping = {
        table 'ev_user'
        id generator: 'native', params: [sequence: 'ev_user_id_seq']
        password column: '`password`'
        autowire true
    }

    static hasMany = [devices:UserDevice]

    Boolean isApplicant() {
        authorities*.authority.contains(Role.USER)
    }

    Boolean isEmployee() {
        List roles = authorities*.authority
        roles.contains(Role.EMPLOYEE) || roles.contains(Role.ATTORNEY)
    }

    Boolean isEmployeeOnly() {
        List roles = authorities*.authority
        roles.contains(Role.EMPLOYEE)
    }

    Boolean isOwner() {
        authorities*.authority.contains(Role.OWNER)
    }

    Boolean isRepresentative() {
        List roles = authorities*.authority
        roles.contains(Role.ATTORNEY) || roles.contains(Role.OWNER)
    }

    Profile getProfile() {
        Profile.findByUser(this)
    }
}
