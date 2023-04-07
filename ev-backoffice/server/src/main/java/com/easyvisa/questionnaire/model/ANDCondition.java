package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ANDCondition extends EasyVisaNode {

    @Override
    public EasyVisaNode copy() {
        ANDCondition andCondition = new ANDCondition();
        this.copyBaseProps(andCondition);
        return andCondition;
    }
}
