package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.model.DocumentActionNode
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

class DocumentActionNodeInstance extends EasyVisaNodeInstance {

    @JsonIgnore
    DocumentActionNode actionNode

    DocumentActionNodeInstance(DocumentActionNode actionNode,
                               DisplayTextLanguage displayTextLanguage, LocalDate currentDate) {
        super(actionNode, displayTextLanguage, currentDate)
        this.actionNode = actionNode
    }

    @Override
    EasyVisaNode getDefinitionNode() {
        this.actionNode
    }
}
