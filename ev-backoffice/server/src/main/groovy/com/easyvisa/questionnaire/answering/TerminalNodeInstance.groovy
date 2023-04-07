package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.TerminalNode
import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

class TerminalNodeInstance extends EasyVisaNodeInstance {

    @JsonIgnore
    TerminalNode terminalNode

    TerminalNodeInstance(TerminalNode terminalNode,
                         DisplayTextLanguage displayTextLanguage, LocalDate currentDate) {
        super(terminalNode, displayTextLanguage, currentDate)
        this.terminalNode = terminalNode
    }

    @Override
    EasyVisaNode getDefinitionNode() {
        this.actionNode
    }
}
