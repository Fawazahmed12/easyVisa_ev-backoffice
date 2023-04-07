package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class FormSubSection extends EasyVisaNode {

    @Override
    public EasyVisaNode copy() {
        FormSubSection formSubSection = new FormSubSection();
        this.copyBaseProps(formSubSection);
        return formSubSection;
    }
}
