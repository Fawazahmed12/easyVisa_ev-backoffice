package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.model.RepeatingQuestionGroup

class RepeatingGroupInstanceList extends EasyVisaNodeInstance {
    private RepeatingQuestionGroup repeatingQuestionGroup

    RepeatingGroupInstanceList(RepeatingQuestionGroup repeatingQuestionGroup, DisplayTextLanguage displayTextLanguage) {
        super(repeatingQuestionGroup, displayTextLanguage)
        this.repeatingQuestionGroup = repeatingQuestionGroup
    }
}
