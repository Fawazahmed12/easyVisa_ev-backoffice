package com.easyvisa

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class EasyVisaFile {

    String path
    String thumbnailPath
    String originalName
    String s3Key
    Profile uploader
    String fileType
    Boolean approved = Boolean.FALSE

    Date dateCreated
    Date lastUpdated

    static constraints = {
        s3Key nullable: true
        thumbnailPath nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'easy_visa_file_id_seq']
    }

    EasyVisaFile copy(String newPath, String newThumbnailPath) {
        EasyVisaFile copy = new EasyVisaFile()
        copy.path = newPath
        copy.thumbnailPath = newThumbnailPath
        copy.originalName = originalName
        copy.s3Key = s3Key
        copy.uploader = uploader
        copy.fileType = fileType
        copy.approved = approved
        copy.dateCreated = dateCreated
        copy.lastUpdated = lastUpdated
        copy
    }
}
