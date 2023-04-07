package com.easyvisa.questionnaire.services.rule.displayorder

import com.easyvisa.questionnaire.answering.rule.FormlyFieldEvaluationContext
import com.easyvisa.questionnaire.dto.FieldGroupSorterUtil
import com.easyvisa.questionnaire.dto.IFieldGroup
import org.springframework.stereotype.Component

@Component
class FormlyQuestionnaireDisplayOrderComponent {

    void orderDisplayQuestions(FormlyFieldEvaluationContext fieldDtoEvaluationContext) {
        String displayOrderChildren = fieldDtoEvaluationContext.displayOrderChildren;
        List<String> displayOrderIdList = displayOrderChildren.split(",");
        IFieldGroup parentFieldGroup = fieldDtoEvaluationContext.parentFieldGroup;
        FieldGroupSorterUtil.sortChildren(parentFieldGroup, displayOrderIdList);
    }
}
