package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DocumentActionNode extends EasyVisaNode {

    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }

    @Override
    public EasyVisaNode copy() {
        DocumentActionNode documentActionNode = new DocumentActionNode();
        this.copyBaseProps(documentActionNode);
        return documentActionNode;
    }
}
