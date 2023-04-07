package com.easyvisa.questionnaire.services.formly


import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.dto.TemplateOption
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.repositories.FormUIMetaDataDAO
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@CompileStatic
@Component
class TemplateOptionsBuilder {

    @Autowired
    private FormUIMetaDataDAO formUIMetaDataDAO

    private static Integer ROW_COUNT = 5;// Apply this to the templateOption for the inputType:'textarea'
    private static String DATE_TYPE = 'date'
    private static String NUMBER_TYPE = 'number'

    TemplateOption generateTemplateOption(Long packageId, Long applicantId, QuestionNodeInstance questionNodeInstance) {
        String inputType = questionNodeInstance.getInputType()
        switch (inputType) {
            case 'radio':
            case 'select':
            case 'ev-conditionalresidencestatus':
            case 'ev-ngselect':
                return this.buildRadioTemplate(questionNodeInstance)
            case 'input':
                return this.buildInputText(questionNodeInstance)
            case 'textarea':
                return this.buildTextAreaTemplate(questionNodeInstance)
        }
        //default
        buildOptionsWithCommonProperties(questionNodeInstance)
    }

    private TemplateOption buildRadioTemplate(QuestionNodeInstance questionNodeInstance) {
        TemplateOption templateOption = this.buildOptionsWithCommonProperties(questionNodeInstance)
        InputSourceType inputSourceType = questionNodeInstance.getInputSourceType();

        templateOption.setOptions(inputSourceType.getValues())

        templateOption
    }

    private TemplateOption buildInputText(QuestionNodeInstance questionNodeInstance) {
        TemplateOption templateOption = this.buildOptionsWithCommonProperties(questionNodeInstance)
        if (questionNodeInstance.getDataType().equals(DATE_TYPE)) {
            templateOption.setType(DATE_TYPE)
        } else if (questionNodeInstance.getDataType().equals(NUMBER_TYPE)) {
            templateOption.setType(NUMBER_TYPE)
        }
        templateOption
    }

    private TemplateOption buildTextAreaTemplate(QuestionNodeInstance questionNodeInstance) {
        TemplateOption templateOption = this.buildOptionsWithCommonProperties(questionNodeInstance)
        templateOption.setRows(ROW_COUNT);
        templateOption
    }

    private TemplateOption buildOptionsWithCommonProperties(QuestionNodeInstance questionNodeInstance) {
        Integer repeatingIndex = questionNodeInstance.repeatingIndex ?: 0;
        TemplateOption templateOption = new TemplateOption()
        templateOption.setLabel(questionNodeInstance.getDisplayText());
        templateOption.setToolTip(questionNodeInstance.getTooltip());
        templateOption.setRequired(questionNodeInstance.getRequired());
        templateOption.setPlaceholder(questionNodeInstance.getContextualClue());
        templateOption.setName(questionNodeInstance.getId() +"_"+ repeatingIndex);
        Map attributes = questionNodeInstance.getAttributes();
        Map templateOptionAttributes = templateOption.getAttributes();
        templateOptionAttributes[TemplateOptionAttributes.ERRORMESSAGE.getValue()] = questionNodeInstance.getErrorMessage();
        templateOptionAttributes[TemplateOptionAttributes.HASVALIDATIONREQUIRED.getValue()] = StringUtils.isNotEmpty(questionNodeInstance.getAnswerValidationRule());
        attributes.each { key, value ->
            templateOptionAttributes[key] = value;
        }
        if(questionNodeInstance.isReadOny()) {
            templateOption.setDisabled(true);
        }
        templateOption
    }
}
