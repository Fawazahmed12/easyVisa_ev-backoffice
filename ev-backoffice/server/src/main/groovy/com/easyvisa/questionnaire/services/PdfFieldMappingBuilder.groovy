package com.easyvisa.questionnaire.services

import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.FieldMappingDetail
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.PdfMappingRuleInfo
import com.easyvisa.questionnaire.model.ContinuationSheet
import com.easyvisa.questionnaire.model.FormQuestion
import com.easyvisa.questionnaire.model.PdfField
import org.springframework.stereotype.Component

@Component
class PdfFieldMappingBuilder {

    List<PdfFieldDetail> buildPdfFieldMapping(List<PdfFieldDetail> pdfFieldDetailList,
                                              List<ContinuationSheet> continuationSheetList,
                                              Set<FormQuestion> formQuestionSetWithDetails) {

        Map<String, ContinuationSheet> continuationSheetHashMap = constructContinuationSheetIdMap(continuationSheetList);
        Map<PdfFieldRelationshipKey, PdfField> pdfFieldRelationshipMap = constructPdfFieldRelationshipMap(formQuestionSetWithDetails);
        List<PdfFieldDetail> validPdfFieldDetailList = [];
        pdfFieldDetailList.each { pdfFieldDetail ->
            PdfField pdfFieldNode = getPdfFieldNodeByRelationship(pdfFieldDetail, pdfFieldRelationshipMap);
            if (!pdfFieldNode) {
                return;
            }

            ContinuationSheet continuationSheet = continuationSheetHashMap[pdfFieldNode.continuationSheetNodeId]
            FieldMappingDetail fieldMappingDetail = new FieldMappingDetail(fieldType: pdfFieldNode.fieldType,
                    fieldExpressions: pdfFieldNode.fieldExpressions,
                    formFieldCount: pdfFieldNode?.formFieldCount,
                    continuationSheetRule: pdfFieldNode?.continuationSheetRule,
                    continuationSheetEasyVisaId: continuationSheet?.id,
                    continuationSheetId: continuationSheet?.sheetNumber,
                    continuationSheetName: continuationSheet?.sheetName,
                    continuationSheetPage: continuationSheet?.page,
                    continuationSheetPart: continuationSheet?.part,
                    continuationSheetItem: continuationSheet?.item,
                    continuationSheetDisplayName: continuationSheet?.displayName,
            )
            pdfFieldDetail.setFieldMappingDetail(fieldMappingDetail);

            if(pdfFieldNode.getRuleClassName()!=null){
                PdfMappingRuleInfo pdfMappingRuleInfo = new PdfMappingRuleInfo(
                        ruleClassName: pdfFieldNode.getRuleClassName(),
                        ruleParam: pdfFieldNode.getRuleParam());
                pdfFieldDetail.setPdfMappingRuleInfo(pdfMappingRuleInfo);
            }

            validPdfFieldDetailList.add(pdfFieldDetail)
        }
        return validPdfFieldDetailList;
    }


    private Map<PdfFieldRelationshipKey, PdfField> constructPdfFieldRelationshipMap(Set<FormQuestion> formQuestionSetWithDetails) {
        Map<PdfFieldRelationshipKey, PdfField> pdfFieldRelationshipMap = new HashMap<>();
        formQuestionSetWithDetails.each { FormQuestion formQuestion ->
            formQuestion.getOutgoingLinks().each { easyVisaNodeRelationship ->
                PdfFieldRelationshipKey pdfFieldRelationshipKey = new PdfFieldRelationshipKey(formQuestion.getQuestionNodeId(), easyVisaNodeRelationship.type)
                pdfFieldRelationshipMap[pdfFieldRelationshipKey] = (PdfField) easyVisaNodeRelationship.endNode
            }
        }
        return pdfFieldRelationshipMap;
    }


    private Map<String, ContinuationSheet> constructContinuationSheetIdMap(List<ContinuationSheet> continuationSheetList){
        Map<String, ContinuationSheet> continuationSheetHashMap = new HashMap<>();
        continuationSheetList.each { ContinuationSheet continuationSheet ->
            continuationSheetHashMap[continuationSheet.id] = continuationSheet;
        }
        return continuationSheetHashMap;
    }


    private PdfField getPdfFieldNodeByRelationship(PdfFieldDetail pdfFieldDetail, Map<PdfFieldRelationshipKey, PdfField> pdfFieldRelationshipMap) {
        if (pdfFieldDetail.getAnswerValueObjectList().isEmpty()) {
            return null;
        }

        def answer = pdfFieldDetail.getAnswerValueObjectList()[0]
        PdfFieldRelationshipKey uniqKeyByAnswer = new PdfFieldRelationshipKey(pdfFieldDetail.questionId, answer.value)
        PdfField pdfFieldNodeByAnswer = pdfFieldRelationshipMap[uniqKeyByAnswer];
        if(pdfFieldNodeByAnswer){
            return this.generatePdfFieldByAnswer(pdfFieldNodeByAnswer.copy(), pdfFieldDetail, pdfFieldRelationshipMap);
        }

        PdfFieldRelationshipKey uniqKeyByDefault = new PdfFieldRelationshipKey(pdfFieldDetail.questionId, "has")
        PdfField pdfFieldNode = pdfFieldRelationshipMap[uniqKeyByDefault];
        return pdfFieldNode;
    }


    private PdfField generatePdfFieldByAnswer(PdfField pdfFieldNodeByAnswer, PdfFieldDetail pdfFieldDetail, Map<PdfFieldRelationshipKey, PdfField> pdfFieldRelationshipMap){
        List<AnswerValueObject> answerValueObjectList = pdfFieldDetail.getAnswerValueObjectList();
        for (int i=1;i<answerValueObjectList.size();i++){
            AnswerValueObject answerValueObject = answerValueObjectList[i];
            PdfFieldRelationshipKey uniqKeyByAnswer = new PdfFieldRelationshipKey(pdfFieldDetail.questionId, answerValueObject.value)
            PdfField pdfField = pdfFieldRelationshipMap[uniqKeyByAnswer];
            if(pdfField && pdfField.fieldExpressions.size()==pdfFieldNodeByAnswer.fieldExpressions.size()){
                pdfFieldNodeByAnswer.fieldExpressions[i] = pdfField.fieldExpressions[i]
            }
        }
        return pdfFieldNodeByAnswer;
    }

    private class PdfFieldRelationshipKey {

        String questionId;
        String relationshipValue;

        PdfFieldRelationshipKey(String questionId, String relationshipValue) {
            this.questionId = questionId;
            this.relationshipValue = relationshipValue;
        }

        String getQuestionId() {
            return questionId;
        }

        String getRelationshipValue() {
            return relationshipValue;
        }

        int hashCode() {
            int hashcode = 0;
            hashcode = questionId.hashCode();
            hashcode += relationshipValue.hashCode();
            return hashcode;
        }

        public boolean equals(Object obj) {
            if (obj instanceof PdfFieldRelationshipKey) {
                PdfFieldRelationshipKey pdfFieldRelationshipKey = (PdfFieldRelationshipKey) obj;
                return pdfFieldRelationshipKey.questionId.equals(this.questionId) &&
                        pdfFieldRelationshipKey.relationshipValue.equals(this.relationshipValue);
            }
            return false;
        }
    }
}
