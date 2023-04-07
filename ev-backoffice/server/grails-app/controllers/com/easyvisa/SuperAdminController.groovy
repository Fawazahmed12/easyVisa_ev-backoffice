package com.easyvisa

import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.model.BenefitCategory
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.Form
import com.easyvisa.questionnaire.model.Question
import com.easyvisa.questionnaire.model.Section
import com.easyvisa.questionnaire.repositories.BenefitCategoryRepository
import com.easyvisa.questionnaire.repositories.SectionRepository
import com.easyvisa.questionnaire.services.DocumentService
import com.easyvisa.questionnaire.services.QuestionnaireService
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.beans.factory.annotation.Autowired

class SuperAdminController {
//    @Autowired
//    BenefitCategoryRepository benefitCategoryRepository;

    @Autowired
    QuestionnaireService questionnaireService

    @Autowired
    DocumentService documentService

    @Autowired
    SectionRepository sectionRepository;

    @Secured([Role.OWNER])
    def getQuestionnaireVersion(){
        List<QuestionnaireVersion> questionnaireVersionList = QuestionnaireVersion.findAll() as List<QuestionnaireVersion>
        render( questionnaireVersionList as JSON)
    }

    @Secured([Role.OWNER])
    def getBenefitCategory(String questVersion){
        List<BenefitCategory> benefitCategoryList = questionnaireService.findAllBenefitCategory(questVersion)
        render( benefitCategoryList as JSON)
    }

    @Secured([Role.OWNER])
    def getForm(String questVersion,String benefitCategoryId){
        List<Form> formsList = documentService.findFormsByBenefitCategory(questVersion, benefitCategoryId)
        render(formsList as JSON)
    }

    @Secured([Role.OWNER])
    def getSection(String questVersion, String formId){
        List<Section> sectionList = questionnaireService.fetchSectionsByForm(questVersion, formId);
        render(sectionList as JSON)
    }

    @Secured([Role.OWNER])
    def getSubSection(String questVersion, String formId, String sectionId){
        List<Map<String, EasyVisaNode>> subSectionsList =  questionnaireService.findAllSubSections(questVersion, formId, sectionId)
        render(subSectionsList as JSON)
    }

    @Secured([Role.OWNER])
    def getQuestion(String questVersion, String formId, String subsectionId){
        List<Question> questionList = questionnaireService.findQuestionByFormSubsection(questVersion,formId,subsectionId);
        render(questionList as JSON)
    }

    @Secured([Role.OWNER])
    def getMetadata(String questVersion,String questionId){
        Question metadataList = questionnaireService.findQuestionById(questVersion,questionId);
        render(metadataList as JSON)
    }

}

