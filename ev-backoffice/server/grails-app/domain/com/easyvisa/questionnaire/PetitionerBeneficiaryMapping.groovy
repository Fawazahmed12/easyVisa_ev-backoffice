package com.easyvisa.questionnaire

class PetitionerBeneficiaryMapping {

    String petitionerSectionNodeid
    String petitionerSubsectionNodeid
    String petitionerQuestionNodeid
    Boolean petitionerRepeatingquestiongroup
    String beneficiarySectionNodeid
    String beneficiarySubsectionNodeid
    String beneficiaryQuestionNodeid
    Boolean beneficiaryRepeatingquestiongroup

    static constraints = {
        petitionerSectionNodeid nullable: false
        petitionerSubsectionNodeid nullable: false
        petitionerQuestionNodeid nullable: false
        petitionerRepeatingquestiongroup nullable: false
        beneficiarySectionNodeid nullable: false
        beneficiarySubsectionNodeid nullable: false
        beneficiaryQuestionNodeid nullable: false
        beneficiaryRepeatingquestiongroup nullable: false
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'petitioner_beneficiary_mapping_id_seq']
    }

}
