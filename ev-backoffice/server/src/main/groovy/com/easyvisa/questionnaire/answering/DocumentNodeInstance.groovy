package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.model.Document
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

class DocumentNodeInstance extends EasyVisaNodeInstance {
    private String description

    @JsonIgnore
    private Document document

    DocumentNodeInstance(Document document,
                         DisplayTextLanguage displayTextLanguage,
                         LocalDate currentDate) {
        super(document, displayTextLanguage, currentDate)
        this.document = document
        this.description = document.getDescription()
    }

    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    void accept(INodeInstanceVisitor nodeInstanceVisitor) {

    }

    @Override
    EasyVisaNode getDefinitionNode() {
        this.document
    }
}
