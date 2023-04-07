package com.easyvisa.enums

enum DocumentMilestoneType {

    PUBLIC_NOTE(1, 'Package sent to USCIS', 'Date Sent', 'DocMilestone_1'),
    APPROVAL_DATE(2, 'Approval Date', 'Approval Date', 'DocMilestone_2'),
    VISA_INTERVIEW(3, 'Visa Interview', 'Interview Date', 'DocMilestone_3'),
    VISA_STAMP_IN_PASSPORT(4, 'Visa Stamp in Passport', 'Date Received', 'DocMilestone_4'),
    ARRIVAL_DATE_IN_US(5, 'Arrival Date in U.S.', 'Arrival Date', 'DocMilestone_5'),
    MARRIAGE_DATE(6, 'Marriage Date', 'Marriage Date', 'DocMilestone_6'),
    AOS_INTERVIEW(7, 'AOS Interview', 'Interview Date', 'DocMilestone_7'),
    GREENCARD_CONDITIONAL_ISSUANCE(8, 'Conditional Green Card Issuance Date', 'Resident Since Date', 'DocMilestone_8'),
    GREENCARD_CONDITIONAL_EXPIRATION(9, 'Conditional Green Card Expiration Date', 'Expiration Date', 'DocMilestone_9'),
    EXPIRATION_PRIOR_DATE(10, '90 Days Prior to Expiration of Conditional Residence Status', 'Date', 'DocMilestone_10'),
    GREENCARD_PERMANENT_EXPIRATION(11, 'Permanent Green Card Expiration Date', 'Expiration Date', 'DocMilestone_11'),

    final Integer order
    final String description
    final String dataLabel
    final String easyVisaId

    DocumentMilestoneType(Integer order, String description, String dataLabel, String easyVisaId) {
        this.order = order
        this.description = description
        this.dataLabel = dataLabel
        this.easyVisaId = easyVisaId
    }
}
