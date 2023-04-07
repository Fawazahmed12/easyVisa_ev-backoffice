package com.easyvisa.questionnaire.services


import com.easyvisa.questionnaire.QuestionnaireCompletionStats
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.model.*
import com.easyvisa.questionnaire.repositories.SectionDAO
import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.stream.Collectors

@Component
class QuestionnaireCompletionWeightageEvaluator {

    @Autowired
    SectionDAO sectionDAO;

    @Autowired
    QuestionnaireService questionnaireService;

    @Transactional
    void generateCompletionWeightageMetaData() {
        QuestionnaireVersion.findAll().each {
            generateCompletionWeightageMeta(it);
        }
    }

    @Transactional
    private generateCompletionWeightageMeta(QuestionnaireVersion questionnaireVersion) {
        List<QuestionnaireCompletionStats> questionnaireCompletionConfigList = [];
        List<BenefitCategory> benefitCategoryList = this.questionnaireService.findAllBenefitCategory(questionnaireVersion.questVersion);
        benefitCategoryList.each {
            this.execute(questionnaireVersion, it.id, questionnaireCompletionConfigList);
        }

        //Remove all existing completion-config mete-data
        QuestionnaireCompletionStats.findAllByQuestionnaireVersion(questionnaireVersion)
                *.delete(flush: true, failOnError: true)

        //Insert all newly generated meta-data
        questionnaireCompletionConfigList.each { questionnaireCompletionConfig ->
            questionnaireCompletionConfig.save(failOnError: true, flush: true)
        }
    }


    private execute(QuestionnaireVersion questionnaireVersion, String benefitCategoryId, List<QuestionnaireCompletionStats> questionnaireCompletionConfigList) {
        this.populateQuestionsByBenefitCategoryAndApplicantType(questionnaireVersion, benefitCategoryId, ApplicantType.Petitioner.name(), questionnaireCompletionConfigList);
        this.populateQuestionsByBenefitCategoryAndApplicantType(questionnaireVersion, benefitCategoryId, ApplicantType.Beneficiary.name(), questionnaireCompletionConfigList);
    }


    private populateQuestionsByBenefitCategoryAndApplicantType(QuestionnaireVersion questionnaireVersion, String benefitCategoryId, String applicantType, List<QuestionnaireCompletionStats> questionnaireCompletionConfigList) {
        int benefitCategoryQuestionsCount = 0;
        String questVersion = questionnaireVersion.questVersion;
        List<Section> sections = this.sectionsByBenefitCategoryAndApplicantType(questVersion, benefitCategoryId, applicantType);
        List<QuestionnaireCompletionStats> completionConfigList = sections.stream()
                .map({ section ->
            String sectionId = section.getId();
            List<String> questionIdList = this.questionGraphByBenefitCategoryAndSection(questVersion, benefitCategoryId, sectionId);
            benefitCategoryQuestionsCount += questionIdList.size()
            return new QuestionnaireCompletionStats(benefitCategoryId: benefitCategoryId,
                    sectionId: sectionId, questionnaireVersion: questionnaireVersion,
                    applicantType: applicantType, sectionQuestionsCount: questionIdList.size());
        }).collect(Collectors.toList());

        completionConfigList.eachWithIndex { questionnaireCompletionConfig, questionnaireCompletionIndex ->
            Double weightageValue = (questionnaireCompletionConfig.sectionQuestionsCount / benefitCategoryQuestionsCount) * 100;
            questionnaireCompletionConfig.setWeightageValue(weightageValue.round(2));
            questionnaireCompletionConfig.setBenefitCategoryQuestionsCount(benefitCategoryQuestionsCount);
            questionnaireCompletionConfigList.add(questionnaireCompletionConfig);
        }
    }


    private List<Section> sectionsByBenefitCategoryAndApplicantType(String questVersion, String benefitCategoryId, String applicantType) {
        List<Section> sectionList = questionnaireService.sectionsByBenefitCategoryAndApplicantType(questVersion, benefitCategoryId, applicantType);
        return sectionList
    }


    private List<String> questionGraphByBenefitCategoryAndSection(String questVersion, String benefitCategoryId, String sectionId) {
        Section section = questionnaireService.sectionQuestionByBenefitCategoryAndSection(questVersion, benefitCategoryId, sectionId);
        List<String> questionIdList = this.flatten(section);
        return questionIdList;
    }


    private List<String> flatten(EasyVisaNode sectionNode) {
        List<String> excludedEasyVisaNodeList = [];
        sectionNode.getChildren().each { subSection ->
            subSection.getChildren().each { easyVisaNode ->
                this.flatten(easyVisaNode, excludedEasyVisaNodeList);
            }
        }
        return excludedEasyVisaNodeList;
    }


    private void flatten(EasyVisaNode easyVisaNode, List<String> excludedEasyVisaNodeList) {
        if (easyVisaNode instanceof Question && !easyVisaNode.getExcludeFromPercentageCalculation()) {
            excludedEasyVisaNodeList.add(easyVisaNode.id);
        }
        easyVisaNode.getChildren().each { childNode ->
            this.flatten(childNode, excludedEasyVisaNodeList);
        }
    }
}
