package com.easyvisa.questionnaire

import com.easyvisa.Package
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class PackageQuestionnaireVersion {
    Boolean latest
    Package aPackage
    QuestionnaireVersion questionnaireVersion

    Date dateCreated
    Date lastUpdated

    static constraints = {
        latest nullable: false
        aPackage nullable: false
        questionnaireVersion nullable: false
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'package_questionnaire_version_id_seq']
    }

    PackageQuestionnaireVersion copy(Package toPackage) {
        PackageQuestionnaireVersion copy = new PackageQuestionnaireVersion()
        copy.latest = latest
        copy.aPackage = toPackage
        copy.questionnaireVersion = questionnaireVersion
        copy
    }
}
