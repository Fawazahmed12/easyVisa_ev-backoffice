package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class TerminalNode extends EasyVisaNode {

    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }


    @Override
    public EasyVisaNode copy() {
        TerminalNode terminalNode = new TerminalNode();
        this.copyBaseProps(terminalNode);
        return terminalNode;
    }
}
