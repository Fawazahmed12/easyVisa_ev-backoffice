package com.easyvisa

class EasyVisaSystemMessageCommand extends PaginationCommand {

    Boolean read
    Boolean starred

    static constraints = {
        read nullable: true
    }

    String getSortFieldName() {
        String fieldName
        switch (sort) {
            case 'star': fieldName = 'isStarred'; break
            case 'source': fieldName = 'source'; break
            case 'date': fieldName = 'dateCreated'; break
            case 'read': fieldName = 'isRead'; break
            default: fieldName = 'dateCreated'
        }
        fieldName
    }
}
