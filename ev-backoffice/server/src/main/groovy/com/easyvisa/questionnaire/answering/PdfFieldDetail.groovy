package com.easyvisa.questionnaire.answering

import com.easyvisa.questionnaire.Answer

class PdfFieldDetail {

    private String questionId
    private String questionName
    private List<AnswerValueObject> answerValueObjectList
    private FieldMappingDetail fieldMappingDetail
    private PdfMappingRuleInfo pdfMappingRuleInfo

    PdfFieldDetail() {
    }

    PdfFieldDetail(String questionId, String questionName) {
        this.questionId = questionId
        this.questionName = questionName
        this.answerValueObjectList = new ArrayList<>()
    }

    void addAnswer(Answer answer, String dataType, String pdfFieldRelationshipValue) {
        this.answerValueObjectList.add(new AnswerValueObject(answer, dataType, pdfFieldRelationshipValue))
    }

    String getQuestionId() {
        return questionId
    }

    void setQuestionId(String questionId) {
        this.questionId = questionId
    }

    String getQuestionName() {
        return questionName
    }

    void setQuestionName(String questionName) {
        this.questionName = questionName
    }

    List<AnswerValueObject> getAnswerValueObjectList() {
        return answerValueObjectList
    }

    void setAnswerValueObjectList(List<AnswerValueObject> answerValueObjectList) {
        this.answerValueObjectList = answerValueObjectList
    }

    FieldMappingDetail getFieldMappingDetail() {
        return fieldMappingDetail
    }

    void setFieldMappingDetail(FieldMappingDetail fieldMappingDetail) {
        this.fieldMappingDetail = fieldMappingDetail
    }

    PdfMappingRuleInfo getPdfMappingRuleInfo() {
        return pdfMappingRuleInfo
    }

    void setPdfMappingRuleInfo(PdfMappingRuleInfo pdfMappingRuleInfo) {
        this.pdfMappingRuleInfo = pdfMappingRuleInfo
    }
}
