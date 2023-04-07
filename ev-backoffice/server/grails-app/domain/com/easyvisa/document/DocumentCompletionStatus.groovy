package com.easyvisa.document

import com.easyvisa.Package
import com.easyvisa.User
import com.easyvisa.enums.DocumentType

class DocumentCompletionStatus {
    Long id
    Package aPackage
    DocumentType documentType
    Double completedPercentage

    Date dateCreated
    Date lastUpdated
    User createdBy
    User updatedBy

    static mapping = {
        id generator: 'native', params: [sequence: 'document_completion_status_id_seq']
    }

}
