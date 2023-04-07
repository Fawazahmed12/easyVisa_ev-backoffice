package com.easyvisa.questionnaire.services.formly

import com.easyvisa.enums.DataTypeConstant
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.*
import com.easyvisa.questionnaire.dto.AnswerItemDto
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.services.MetaDataMapper

import java.util.stream.Collectors

public class FormlyAnswerBuilder {

    AnswerItemDto toAnswerItem(SectionNodeInstance sectionInstance) {
        AnswerItemDto sectionAnswerDto = new AnswerItemDto();
        this.visitChildren(sectionAnswerDto, sectionInstance);

        AnswerItemDto answerDto = new AnswerItemDto();
        answerDto.addChildItem(sectionInstance.getName(), sectionAnswerDto);
        return answerDto;
    }

    AnswerItemDto toAnswerItem(SubSectionNodeInstance subSectionInstance) {
        AnswerItemDto subSectionAnswerDto = new AnswerItemDto();
        this.visitChildren(subSectionAnswerDto, subSectionInstance);
        return subSectionAnswerDto;
    }

    private void visitChildren(AnswerItemDto answerItemDto, EasyVisaNodeInstance parentNode) {
        List<EasyVisaNodeInstance> easyVisaNodeInstanceList = parentNode.getChildren();

        List<EasyVisaNodeInstance> nonRepeatingGroupNodeInstanceList =
                easyVisaNodeInstanceList.stream().filter({ easyVisaNodeInstance -> !(easyVisaNodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                        .collect(Collectors.toList());
        for (EasyVisaNodeInstance easyVisaNodeInstance : nonRepeatingGroupNodeInstanceList) {
            this.addAnswerItem(answerItemDto, easyVisaNodeInstance);
        }

        List<EasyVisaNodeInstance> repeatingGroupNodeInstanceList =
                easyVisaNodeInstanceList.stream().filter({ easyVisaNodeInstance -> (easyVisaNodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                        .collect(Collectors.toList());
        if (repeatingGroupNodeInstanceList.isEmpty()) {
            return;
        }

        Map<String, List<EasyVisaNodeInstance>> groupedRepeatingGroupNodeInstanceList =
                repeatingGroupNodeInstanceList.stream()
                        .collect(Collectors.groupingBy({ easyVisaNodeInstance -> (easyVisaNodeInstance as RepeatingQuestionGroupNodeInstance).getAnswerKey() }));
        Set<String> repeatingQuestionGroupNames = groupedRepeatingGroupNodeInstanceList.keySet();
        for (String repeatingQuestionGroupName : repeatingQuestionGroupNames) {
            List<AnswerItemDto> answerItemDtoList = new ArrayList<>();
            List<EasyVisaNodeInstance> nodesInstanceListByGroupName = groupedRepeatingGroupNodeInstanceList.get(repeatingQuestionGroupName);
            for (EasyVisaNodeInstance easyVisaNodeInstance : nodesInstanceListByGroupName) {
                AnswerItemDto repeatingGroupAnswerItemDto = new AnswerItemDto();
                this.addAnswerItem(repeatingGroupAnswerItemDto, easyVisaNodeInstance);
                answerItemDtoList.add(repeatingGroupAnswerItemDto);
            }
            answerItemDto.addChildItem(repeatingQuestionGroupName, answerItemDtoList);
        }
    }

    private void addAnswerItem(AnswerItemDto answerItemDto, EasyVisaNodeInstance easyVisaNodeInstance) {
        EasyVisaNode easyVisaNode = easyVisaNodeInstance.getDefinitionNode();
        String nodeInstanceType = easyVisaNode.getClass().getSimpleName();

        switch (nodeInstanceType) {
            case MetaDataMapper.SUBSECTION:
                SubSectionNodeInstance subSectionInstance = (SubSectionNodeInstance) easyVisaNodeInstance;
                this.addSubsectionNodeAnswerItem(answerItemDto, subSectionInstance);
                break;

            case MetaDataMapper.QUESTION:
                QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) easyVisaNodeInstance;
                this.addQuestionNodeAnswerItem(answerItemDto, questionNodeInstance);
                break;

            case MetaDataMapper.REPEATING_QUESTION_GROUP:
                RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) easyVisaNodeInstance;
                this.addRepeatingGroupAnswerItem(answerItemDto, repeatingQuestionGroupNodeInstance);
                break;
        }
    }

    private void addSubsectionNodeAnswerItem(AnswerItemDto answerItemDto, SubSectionNodeInstance subSectionNodeInstance) {
        AnswerItemDto subSectionAnswerDto = this.toAnswerItem(subSectionNodeInstance);
        answerItemDto.addChildItem(subSectionNodeInstance.getName(), subSectionAnswerDto);
    }

    private void addQuestionNodeAnswerItem(AnswerItemDto answerItemDto, QuestionNodeInstance questionNodeInstance) {
        Answer answer = questionNodeInstance.getAnswer();
        if (questionNodeInstance.dataType == DataTypeConstant.BOOLEAN.value) {
            Boolean booleanAnswer = (answer.value == 'true')
            answerItemDto.addChildItem(questionNodeInstance.getName(), booleanAnswer);
        } else {
            answerItemDto.addChildItem(questionNodeInstance.getName(), answer.getValue());
        }
        this.visitChildren(answerItemDto, questionNodeInstance);
    }

    private void addRepeatingGroupAnswerItem(AnswerItemDto repeatingGroupAnswerDto, RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance) {
        this.visitChildren(repeatingGroupAnswerDto, repeatingQuestionGroupInstance);
    }
}
