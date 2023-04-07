package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class Document extends EasyVisaNode {

    @Property
    private String description;

    public Document() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }


    @Override
    public EasyVisaNode copy() {
        Document document = new Document();
        this.copyBaseProps(document);
        return document;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        Document targetSection = (Document) target;
        targetSection.description = this.description;
    }
}
