	package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.ArrayList;
import java.util.List;

@NodeEntity
public class PdfField extends EasyVisaNode {

    @Property
    private String fieldType; // can be simple, repeat, split etc

    @Property
    private String continuationSheetNodeId;

    @Property
    private Integer formFieldCount;

    @Property
    private String continuationSheetRule;

    @Property
    private String ownFormRule;

    @Property
    private List<String> fieldExpressions;

    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }


    @Override
    public EasyVisaNode copy() {
        PdfField pdfField = new PdfField();
        this.copyBaseProps(pdfField);
        return pdfField;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        PdfField targetPdfField = (PdfField) target;
        targetPdfField.fieldType = this.fieldType;
        targetPdfField.continuationSheetNodeId = this.continuationSheetNodeId;
        targetPdfField.formFieldCount = this.formFieldCount;
        targetPdfField.continuationSheetRule = this.continuationSheetRule;
        targetPdfField.ownFormRule = this.ownFormRule;
        targetPdfField.fieldExpressions = new ArrayList<>(this.fieldExpressions);
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public List<String> getFieldExpressions() {
        return fieldExpressions;
    }

    public void setFieldExpressions(List<String> fieldExpressions) {
        this.fieldExpressions = fieldExpressions;
    }

    public String getContinuationSheetNodeId() {
        return continuationSheetNodeId;
    }

    public void setContinuationSheetNodeId(String continuationSheetNodeId) {
        this.continuationSheetNodeId = continuationSheetNodeId;
    }

    public Integer getFormFieldCount() {
        return formFieldCount;
    }

    public void setFormFieldCount(Integer formFieldCount) {
        this.formFieldCount = formFieldCount;
    }

    public String getContinuationSheetRule() {
        return continuationSheetRule;
    }

    public void setContinuationSheetRule(String continuationSheetRule) {
        this.continuationSheetRule = continuationSheetRule;
    }

    public String getOwnFormRule() {
        return ownFormRule;
    }

    public void setOwnFormRule(String ownFormRule) {
        this.ownFormRule = ownFormRule;
    }
}
