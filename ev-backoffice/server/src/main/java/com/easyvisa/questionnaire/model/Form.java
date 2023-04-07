package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class Form extends EasyVisaNode {

    @Property
    private String applicantType;

    @Property
    private String pdfForm;

    @Property
    private String editionDate;

    @Property
    private String expirationDate;

    @Override
    public EasyVisaNode copy() {
        Form form = new Form();
        this.copyBaseProps(form);
        return form;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        Form targetPdfField = (Form) target;
        targetPdfField.applicantType = this.applicantType;
        targetPdfField.pdfForm = this.pdfForm;
        targetPdfField.editionDate = this.editionDate;
        targetPdfField.expirationDate = this.expirationDate;
    }


    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public String getPdfForm() {
        return pdfForm;
    }

    public void setPdfForm(String pdfForm) {
        this.pdfForm = pdfForm;
    }

    public String getEditionDate() {
        return editionDate;
    }

    public void setEditionDate(String editionDate) {
        this.editionDate = editionDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
