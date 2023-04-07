package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class DocumentHelp extends EasyVisaNode {

    @Property
    private String tooltip;

    public DocumentHelp() {
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }


    @Override
    public EasyVisaNode copy() {
        DocumentHelp documentHelp = new DocumentHelp();
        this.copyBaseProps(documentHelp);
        return documentHelp;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        DocumentHelp targetSection = (DocumentHelp) target;
        targetSection.tooltip = this.tooltip;
    }
}
