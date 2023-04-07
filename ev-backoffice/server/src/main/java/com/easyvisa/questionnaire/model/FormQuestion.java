package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.ArrayList;

@NodeEntity
public class FormQuestion  extends EasyVisaNode{

    @Property
    private String formNodeId;

    @Property
    private String questionNodeId;


    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }

    @Override
    public EasyVisaNode copy() {
        FormQuestion formQuestion = new FormQuestion();
        this.copyBaseProps(formQuestion);
        return formQuestion;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        FormQuestion targetPdfField = (FormQuestion) target;
        targetPdfField.formNodeId = this.formNodeId;
        targetPdfField.questionNodeId = this.questionNodeId;
    }

    public String getFormNodeId() {
        return formNodeId;
    }

    public void setFormNodeId(String formNodeId) {
        this.formNodeId = formNodeId;
    }

    public String getQuestionNodeId() {
        return questionNodeId;
    }

    public void setQuestionNodeId(String questionNodeId) {
        this.questionNodeId = questionNodeId;
    }
}
