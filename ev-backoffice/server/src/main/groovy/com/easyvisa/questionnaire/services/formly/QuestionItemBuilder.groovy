package com.easyvisa.questionnaire.services.formly

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.*
import com.easyvisa.questionnaire.meta.UIStyleMeta
import com.easyvisa.questionnaire.services.MetaDataMapper
import com.easyvisa.questionnaire.services.RuleActionHandler
import groovy.transform.CompileStatic
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.MessageFormat

@Component
@CompileStatic
public class QuestionItemBuilder {

    @Autowired
    private TemplateOptionsBuilder templateOptionsBuilder;

    @Autowired
    RuleActionHandler ruleActionHandler;

    QuestionItemDto buildQuestionItem(IFieldGroup parentField, List<Answer> answerList, Long packageId,
                                      Long applicantId, QuestionNodeInstance questionNodeInstance) {
        TemplateOption templateOption = templateOptionsBuilder.generateTemplateOption(packageId, applicantId, questionNodeInstance);
        this.evaluateAnswerCompletionValidation(answerList, packageId, applicantId, questionNodeInstance, templateOption)
        QuestionItemDto questionItemDto = new QuestionItemDto(parentField, questionNodeInstance.getId(), questionNodeInstance.getName(),
                questionNodeInstance.getInputType(), templateOption);
        return questionItemDto;
    }


    private void evaluateAnswerCompletionValidation(List<Answer> answerList, Long packageId, Long applicantId,
                                                    QuestionNodeInstance questionNodeInstance,
                                                    TemplateOption templateOption) {
        Answer answer = questionNodeInstance.getAnswer();
        boolean hasQuestionAnswered = Answer.isValidAnswer(answer);
        if (StringUtils.isNotEmpty(questionNodeInstance.answerCompletionValidationRule)) {
            NodeRuleEvaluationContext ruleEvaluationContext = new NodeRuleEvaluationContext(answerList, questionNodeInstance, packageId, applicantId)
            hasQuestionAnswered = ruleActionHandler.validateAnswerCompletion(questionNodeInstance.answerCompletionValidationRule, ruleEvaluationContext)
        }
        Map templateOptionAttributes = templateOption.getAttributes();
        templateOptionAttributes[TemplateOptionAttributes.HASQUESTIONANSWERED.getValue()] = hasQuestionAnswered;
    }


    IFieldGroup buildLabelItem(IFieldGroup parentField, QuestionNodeInstance questionNodeInstance, MetaDataMapper metaDataMapper) {
        UIStyleMeta uiStyleMeta = metaDataMapper.getUIStyleMeta(MetaDataMapper.LABEL)
        HeaderTextDto headerTextDto = new HeaderTextDto(parentField, questionNodeInstance.id);
        headerTextDto.setStyleClassName(questionNodeInstance.getStyleClassName());

        Object[] params = [uiStyleMeta.getStyle().getHeader(), questionNodeInstance.getDisplayText()];
        String template = MessageFormat.format("<{0}>{1}</{0}>", params);
        headerTextDto.setTemplate(template);
        return headerTextDto;
    }
}
