package com.easyvisa

import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus

@Secured(['permitAll'])
class PdfController {

    PdfPopulationService pdfPopulationService

    @Secured(value = ['permitAll'])
    def generate(Long packageId, Long applicantId, String formId, String continuationSheetId, String sectionId, Integer continuationFileNumber) {
        if (formId && continuationSheetId) {
            throw new EasyVisaException('errorCode': HttpStatus.SC_BAD_REQUEST, 'message': "Only one of formId or continuationSheetId should be provided, but not both together")
        }
        Map<String, Object> result = pdfPopulationService.getPdf(packageId, applicantId, formId, continuationSheetId, continuationFileNumber)
        render fileName: result['filename'], contentType: result['mimetype'], file: result['file']
    }

}
