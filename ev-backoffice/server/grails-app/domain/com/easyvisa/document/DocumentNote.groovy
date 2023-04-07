package com.easyvisa.document


import com.easyvisa.Package
import com.easyvisa.Profile
import com.easyvisa.User
import com.easyvisa.enums.DocumentNoteType
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class DocumentNote {

    Package aPackage
    Profile creator
    String subject
    DocumentNoteType documentNoteType

    Date dateCreated
    Date lastUpdated
    User createdBy
    User updatedBy

    static mapping = {
        id generator: 'native', params: [sequence: 'document_note_id_seq']
    }
}
