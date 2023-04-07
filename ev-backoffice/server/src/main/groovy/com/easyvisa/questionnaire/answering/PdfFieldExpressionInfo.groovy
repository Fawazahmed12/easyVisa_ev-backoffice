package com.easyvisa.questionnaire.answering

class PdfFieldExpressionInfo {

    private List<PdfFieldDetail> pdfFieldDetailList;
    private ContinuationSheetHeaderInfo continuationSheetHeaderInfo;

    List<PdfFieldDetail> getPdfFieldDetailList() {
        return pdfFieldDetailList
    }

    void setPdfFieldDetailList(List<PdfFieldDetail> pdfFieldDetailList) {
        this.pdfFieldDetailList = pdfFieldDetailList
    }

    ContinuationSheetHeaderInfo getContinuationSheetHeaderInfo() {
        return continuationSheetHeaderInfo
    }

    void setContinuationSheetHeaderInfo(ContinuationSheetHeaderInfo continuationSheetHeaderInfo) {
        this.continuationSheetHeaderInfo = continuationSheetHeaderInfo
    }
}
