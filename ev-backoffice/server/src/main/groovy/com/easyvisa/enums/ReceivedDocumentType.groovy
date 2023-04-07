package com.easyvisa.enums

enum ReceivedDocumentType {

    VISA_STICKER(1, 'Visa Sticker(s)', 'Fiance visa applicants only.'),
    MARRIAGE_CERTIFICATE(2, 'Marriage Certificate(s) (Enter Date of Marriage in Date Received)', 'Fiance visa applicants only.'),
    TRAVEL_DOCUMENT(3, 'Advance Parole/Travel Documents', 'Advance parole is most commonly\n' +
            'used when someone has a pending Form I-485, Application to Register\n' +
            'Permanent Residence or to Adjust Status.'),
    WORK_AUTHORIZATION(4, 'Work Authorization(s)', ''),
    PERMANENT_RESIDENCE(5, 'Permanent Residence Card(s)', ''),
    SOCIAL_SECURITY(6, 'Social Security Card(s)', ''),
    ACTION_LETTERS(7, 'I-797 Notice of Action Letters', ''),
    OTHERS(8, 'Other(s)', '')

    final Integer order
    final String description
    final String helpText

    ReceivedDocumentType(Integer order, String description, String helpText) {
        this.order = order
        this.description = description
        this.helpText = helpText
    }

}
