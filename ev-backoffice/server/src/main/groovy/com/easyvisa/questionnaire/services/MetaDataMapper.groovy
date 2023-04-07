package com.easyvisa.questionnaire.services

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.meta.UIStyleMeta
import com.easyvisa.questionnaire.repositories.FormUIMetaDataDAO
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@CompileStatic
class MetaDataMapper {

    public static final String SECTION = 'Section'
    public static final String SUBSECTION = 'SubSection'
    public static final String QUESTION = 'Question'
    public static final String LABEL = 'Label'
    public static final String REPEATING_QUESTION_GROUP = 'RepeatingQuestionGroup'
    public static final String EXPRESSION = 'Expression'

    @Autowired
    private FormUIMetaDataDAO formUIMetaDataDAO

    @Autowired
    RuleActionHandler ruleActionHandler;

    @Autowired
    QuestionnaireService questionnaireService;

    UIStyleMeta getUIStyleMeta(String styleType) {
        return formUIMetaDataDAO.getUIStyleMeta(styleType)
    }

    void populateInputSourceType(List<Answer> answerList, Long packageId, Long applicantId,
                                 QuestionNodeInstance questionNodeInstance) {
        if (StringUtils.isNotEmpty(questionNodeInstance.inputTypeSourceRule)) {
            NodeRuleEvaluationContext nodeRuleEvaluationContext = new NodeRuleEvaluationContext(answerList, questionNodeInstance, packageId, applicantId)
            InputSourceType inputSourceType = this.ruleActionHandler.generateDynamicInputSource(questionNodeInstance.inputTypeSourceRule,
                    nodeRuleEvaluationContext)
            questionNodeInstance.setInputSourceType(inputSourceType);
        }
        if (StringUtils.isNotEmpty(questionNodeInstance.inputTypeSource) && !questionNodeInstance.inputSourceType) {
            InputSourceType inputSourceType = questionnaireService.getInputSourceType(questionNodeInstance.inputTypeSource, questionNodeInstance.questVersion,
                    questionNodeInstance.displayTextLanguage)
            questionNodeInstance.setInputSourceType(inputSourceType);
        }
    }

    void populateToolTip(List<Answer> answerList, Long packageId, Long applicantId,
                         QuestionNodeInstance questionNodeInstance) {
        if (StringUtils.isNotEmpty(questionNodeInstance.getDynamicTooltipRule())) {
            NodeRuleEvaluationContext nodeRuleEvaluationContext = new NodeRuleEvaluationContext(answerList, questionNodeInstance, packageId, applicantId)
            questionNodeInstance.setTooltip(this.ruleActionHandler.generateToolTip(questionNodeInstance.getDynamicTooltipRule(), nodeRuleEvaluationContext));
        }
    }

    void populateDisplayText(List<Answer> answerList, Long packageId, Long applicantId,
                             EasyVisaNodeInstance easyVisaNodeInstance) {
        String displayTextRule = easyVisaNodeInstance['displayTextRule'];
        if (StringUtils.isNotEmpty(displayTextRule)) {
            NodeRuleEvaluationContext nodeRuleEvaluationContext = new NodeRuleEvaluationContext(answerList, easyVisaNodeInstance, packageId, applicantId)
            easyVisaNodeInstance.setDisplayText(this.ruleActionHandler.generateDisplayText(displayTextRule, nodeRuleEvaluationContext));
        }
    }

    void populateDynamicAttribute(List<Answer> answerList, Long packageId, Long applicantId,
                                  EasyVisaNodeInstance easyVisaNodeInstance) {
        String attributeRule = easyVisaNodeInstance['attributeRule'];
        if (StringUtils.isNotEmpty(attributeRule)) {
            NodeRuleEvaluationContext nodeRuleEvaluationContext = new NodeRuleEvaluationContext(answerList, easyVisaNodeInstance, packageId, applicantId)
            this.ruleActionHandler.generateDynamicAttribute(attributeRule, nodeRuleEvaluationContext);
        }
    }
}
