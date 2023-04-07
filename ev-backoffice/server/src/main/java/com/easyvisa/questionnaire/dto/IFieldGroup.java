package com.easyvisa.questionnaire.dto;

import java.util.List;

public interface IFieldGroup {
    void addFieldGroup(IFieldGroup iFieldGroup);
    void setAnswerIndex(Integer answerIndex);
    String getFieldId();
    Integer getOrderIndex();
    void setFieldGroups(List<IFieldGroup> fieldGroups);
    List<IFieldGroup> getFieldGroups();
    String getFullyQualifiedKey();
}
