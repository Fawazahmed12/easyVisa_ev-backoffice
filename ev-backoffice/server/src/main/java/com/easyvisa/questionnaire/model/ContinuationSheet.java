package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class ContinuationSheet extends EasyVisaNode {

    @Property
    private String applicantType;

    @Property
    private Integer sheetNumber;

    @Property
    private String sheetName;

    @Property
    private String page;

    @Property
    private String part;

    @Property
    private String item;

    @Property
    private String displayName;


    @Override
    public EasyVisaNode copy() {
        ContinuationSheet continuationSheet = new ContinuationSheet();
        this.copyBaseProps(continuationSheet);
        return continuationSheet;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        ContinuationSheet targetPdfField = (ContinuationSheet) target;
        targetPdfField.applicantType = this.applicantType;
        targetPdfField.sheetNumber = this.sheetNumber;
        targetPdfField.sheetName = this.sheetName;
        targetPdfField.page = this.page;
        targetPdfField.part = this.part;
        targetPdfField.item = this.item;
        targetPdfField.displayName = this.displayName;
    }

    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public Integer getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(Integer sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
