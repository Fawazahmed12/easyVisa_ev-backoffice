package com.easyvisa.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelOption {
    private String updateOn;

    public ModelOption(String questionType) {
        InputTypeConstant inputType = InputTypeConstant.INPUT;
        InputTypeConstant textArea = InputTypeConstant.TEXTAREA;
        if(questionType.equals(inputType.getValue()) || questionType.equals(textArea.getValue())){
            this.updateOn = "blur";
        }else{
            this.updateOn = "change";
        }
    }


    public String getUpdateOn() {
        return updateOn;
    }

    public void setUpdateOn(String updateOn) {
        this.updateOn = updateOn;
    }
}
