package com.easyvisa.questionnaire.services

class PdfFieldPrintingParams {
    Long packageId;
    Long applicantId;
    String formId;
    String continuationSheetId; //Should have either 'formId' or 'continuationSheetId'
}
