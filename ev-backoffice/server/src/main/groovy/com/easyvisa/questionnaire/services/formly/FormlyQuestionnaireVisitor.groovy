package com.easyvisa.questionnaire.services.formly


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.answering.rule.FormlyFieldEvaluationContext
import com.easyvisa.questionnaire.dto.*
import com.easyvisa.questionnaire.meta.UIStyleMeta
import com.easyvisa.questionnaire.services.MetaDataMapper
import com.easyvisa.questionnaire.services.RuleActionHandler
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate
import java.util.stream.Collectors

class FormlyQuestionnaireVisitor implements INodeInstanceVisitor {

    private MetaDataMapper metaDataMapper
    private QuestionItemBuilder questionItemBuilder
    private FieldItemDto formlySectionDto
    private IFieldGroup parentFieldItemDto
    private FieldItemDto lastVisitedSubSecFieldItemDto
    RuleActionHandler ruleActionHandler
    private Long packageId
    private Long applicantId
    private List<Answer> answerList
    private def questionnaireAccessState

    FormlyQuestionnaireVisitor(Long packageId, Long applicantId, def questionnaireAccessState,
                               List<Answer> answerList,
                               MetaDataMapper metaDataMapper,
                               QuestionItemBuilder questionItemBuilder,
                               RuleActionHandler ruleActionHandler) {
        this.packageId = packageId;
        this.applicantId = applicantId;
        this.questionnaireAccessState = questionnaireAccessState;
        this.answerList = answerList;
        this.metaDataMapper = metaDataMapper
        this.questionItemBuilder = questionItemBuilder
        this.ruleActionHandler = ruleActionHandler
    }


    @Override
    void visit(SectionNodeInstance sectionInstance) {
        formlySectionDto = new FieldItemDto(this.parentFieldItemDto, sectionInstance.getName(), sectionInstance.getId())
        this.parentFieldItemDto = formlySectionDto;
        this.visitChildren(sectionInstance)
    }

    @Override
    void visit(SubSectionNodeInstance subSectionInstance) {
        IFieldGroup currentParent = this.parentFieldItemDto;
        FieldItemDto subSectionDto = new FieldItemDto(this.parentFieldItemDto, subSectionInstance.getName(), subSectionInstance.getId(),
                subSectionInstance.getDisplayText(), metaDataMapper.getUIStyleMeta(MetaDataMapper.SUBSECTION), subSectionInstance.getStyleClassName())
        subSectionDto.setHide(!subSectionInstance.isVisibility());
        this.parentFieldItemDto.addFieldGroup(subSectionDto)

        // make the current subsection a parent
        this.parentFieldItemDto = subSectionDto;
        this.lastVisitedSubSecFieldItemDto = subSectionDto
        this.visitChildren(subSectionInstance)
        this.parentFieldItemDto = currentParent;
        this.lastVisitedSubSecFieldItemDto = null;
    }

    @Override
    void visit(QuestionNodeInstance questionInstance) {
        IFieldGroup currentParent = this.parentFieldItemDto;
        IFieldGroup fieldGroupDto;
        if (questionInstance.inputType == InputTypeConstant.LABEL.value) {
            fieldGroupDto = questionItemBuilder.buildLabelItem(this.parentFieldItemDto, questionInstance, this.metaDataMapper)
        } else {
            fieldGroupDto = this.buildQuestionItemDto(questionInstance);
        }
        this.parentFieldItemDto.addFieldGroup(fieldGroupDto)
        this.visitChildren(questionInstance)
        this.parentFieldItemDto = currentParent;
    }


    private IFieldGroup buildQuestionItemDto(QuestionNodeInstance questionNodeInstance) {
        QuestionItemDto questionItemDto = questionItemBuilder.buildQuestionItem(this.parentFieldItemDto, this.answerList, this.packageId,
                this.applicantId, questionNodeInstance)
        questionItemDto.setStyleClassName(questionNodeInstance.getStyleClassName());
        questionItemDto.setWrappers(questionNodeInstance.getWrappers());
        questionItemDto.setDefaultValue(questionNodeInstance.getDefaultValue());
        questionItemDto.addSubSectionAttribute(this.lastVisitedSubSecFieldItemDto.id)
        if (this.questionnaireAccessState.readOnly) {
            questionItemDto.addDisabledAttribute();
        }
        return questionItemDto;
    }

    @Override
    void visit(RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance) {
        IFieldGroup currentParent = this.parentFieldItemDto
        UIStyleMeta uiStyleMeta = metaDataMapper.getUIStyleMeta(MetaDataMapper.REPEATING_QUESTION_GROUP)
        RepeatingQuestionGroupDto repeatingQuestionGroupDto = new RepeatingQuestionGroupDto(this.parentFieldItemDto, repeatingQuestionGroupInstance.id,
                repeatingQuestionGroupInstance.answerIndex, repeatingQuestionGroupInstance.totalRepeatCount, repeatingQuestionGroupInstance.getAnswerKey())
        repeatingQuestionGroupDto.setStyleClassName(repeatingQuestionGroupInstance.getStyleClassName());
        repeatingQuestionGroupDto.setWrappers(repeatingQuestionGroupInstance.getWrappers());
        repeatingQuestionGroupDto.setFieldGroupClassName(uiStyleMeta.getStyle().getFieldGroupClassName())
        repeatingQuestionGroupDto.buildTemplateOptionAttributes(repeatingQuestionGroupInstance.getAttributes(), repeatingQuestionGroupInstance.getDisplayText(),
                repeatingQuestionGroupInstance.getAddButtonTitle(), this.lastVisitedSubSecFieldItemDto.id)
        this.parentFieldItemDto.addFieldGroup(repeatingQuestionGroupDto)

        // make the current repeatingQuestionGroupDto a parent
        this.parentFieldItemDto = repeatingQuestionGroupDto;
        this.visitChildren(repeatingQuestionGroupInstance)
        this.parentFieldItemDto = currentParent;
    }

    @Override
    void visit(DocumentActionNodeInstance documentActionInstance) {
    }

    @Override
    void visit(TerminalNodeInstance terminalNodeInstance) {
    }


    private void visitChildren(EasyVisaNodeInstance parentNode) {
        List<EasyVisaNodeInstance> easyVisaNodeInstanceList = parentNode.getChildren().stream()
                .filter({ easyVisaNodeInstance -> easyVisaNodeInstance.isVisibility() })
                .collect(Collectors.toList())
        for (EasyVisaNodeInstance easyVisaNodeInstance : easyVisaNodeInstanceList) {
            easyVisaNodeInstance.accept(this)
        }

        this.orderDisplayQuestions(parentNode);
    }

    private void orderDisplayQuestions(EasyVisaNodeInstance parentNode) {
        if (StringUtils.isNotEmpty(parentNode.getDisplayOrderChildren())) {
            FormlyFieldEvaluationContext fieldDtoEvaluationContext = new FormlyFieldEvaluationContext(parentNode.getDisplayOrderChildren(), this.parentFieldItemDto)
            this.ruleActionHandler.orderDisplayQuestions(fieldDtoEvaluationContext);
        }
    }

    FieldItemDto getFormlySectionDto() {
        return formlySectionDto
    }
}
