package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.model.DocumentHelp
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

class DocumentHelpNodeInstance extends EasyVisaNodeInstance {
    private String tooltip

    @JsonIgnore
    private DocumentHelp documentHelp

    DocumentHelpNodeInstance(DocumentHelp documentHelp,
                             DisplayTextLanguage displayTextLanguage, LocalDate currentDate) {
        super(documentHelp, displayTextLanguage, currentDate)
        this.documentHelp = documentHelp
        this.tooltip = documentHelp.getTooltip()
    }

    String getTooltip() {
        return tooltip
    }

    void setTooltip(String tooltip) {
        this.tooltip = tooltip
    }

    void accept(INodeInstanceVisitor nodeInstanceVisitor) {

    }

    @Override
    EasyVisaNode getDefinitionNode() {
        this.documentHelp
    }
}
