package com.easyvisa.questionnaire

class QuestionnaireCompletionStats {

    String benefitCategoryId
    String sectionId
    String applicantType
    Double weightageValue // section + Benefit Category combination
    Integer sectionQuestionsCount = 0
    Integer benefitCategoryQuestionsCount = 0
    QuestionnaireVersion questionnaireVersion

    static constraints = {
        benefitCategoryId nullable: false
        sectionId nullable: false
        applicantType nullable: false
        weightageValue nullable: false
        sectionQuestionsCount nullable: false
        benefitCategoryQuestionsCount nullable: false
        questionnaireVersion nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'questionnaire_completion_stats_id_seq']
    }

}
