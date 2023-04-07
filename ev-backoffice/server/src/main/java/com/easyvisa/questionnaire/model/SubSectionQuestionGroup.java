package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class SubSectionQuestionGroup extends EasyVisaNode {

    @Override
    public EasyVisaNode copy() {
        SubSectionQuestionGroup subSectionQuestionGroup = new SubSectionQuestionGroup();
        this.copyBaseProps(subSectionQuestionGroup);
        return subSectionQuestionGroup;
    }
}
