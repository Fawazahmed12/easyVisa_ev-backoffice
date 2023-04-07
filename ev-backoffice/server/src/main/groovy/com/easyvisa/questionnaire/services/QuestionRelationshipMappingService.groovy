package com.easyvisa.questionnaire.services

import com.easyvisa.AsyncService
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.answering.rule.FormQuestionEvaluationContext
import com.easyvisa.questionnaire.repositories.FormDAO
import com.easyvisa.questionnaire.repositories.SectionDAO
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDate

@Slf4j
@Service
@Transactional
@GrailsCompileStatic
class QuestionRelationshipMappingService {

    AsyncService asyncService

    @Autowired
    FormDAO formDAO

    @Autowired
    SectionDAO sectionDAO

    Map<String, Map<String, Set<String>>> versionToQuestionFormMap = new HashMap<>()
    Map<String, Map<String, Set<String>>> versionToRQGFormMap = new HashMap<>()
    Map<String, Map<String, Set<String>>> versionToSectionFormMap = new HashMap<>()
    Map<String, Map<String, Set<String>>> versionToSubSectionFormMap = new HashMap<>()
    Map<String, Map<String, Set<String>>> versionToFormBenefitCategoryMap = new HashMap<>()


    void preFetchAllQuestionRelationshipMapData() {
        int startIndex = 0
        List<QuestionnaireVersion> questionnaireVersionList = QuestionnaireVersion.findAll() as List<QuestionnaireVersion>
        this.initializeQuestionFormMapper(questionnaireVersionList, startIndex)
    }


    @CompileDynamic
    private void initializeQuestionFormMapper(List<QuestionnaireVersion> questionnaireVersionList, int currentRunningIndex) {
        if (questionnaireVersionList.size() < (currentRunningIndex + 1)) {
            return
        }


        String questVersion = questionnaireVersionList.get(currentRunningIndex).questVersion
        Map<String, Set<String>> questionFormMapper = formDAO.buildQuestionFormMap(questVersion)
        versionToQuestionFormMap.put(questVersion, questionFormMapper)

        Map<String, Set<String>> repeatingQuestionGroupFormMapper = formDAO.buildRQGFormMap(questVersion)
        versionToRQGFormMap.put(questVersion, repeatingQuestionGroupFormMapper)

        Map<String, Set<String>> sectionFormMapper = sectionDAO.buildSectionFormMap(questVersion)
        versionToSectionFormMap.put(questVersion, sectionFormMapper)

        Map<String, Set<String>> subSectionFormMapper = formDAO.buildSubSectionFormMap(questVersion)
        versionToSubSectionFormMap.put(questVersion, subSectionFormMapper)

        Map<String, Set<String>> formBenefitCategoryMapper = formDAO.buildFormBenefitCategoryMap(questVersion)
        versionToFormBenefitCategoryMap.put(questVersion, formBenefitCategoryMapper)

        log.info(">>>>>>>>>>>>>>>> Sucessfully PreFetched Neo4J data for QuestionForm relationship in QuestionnaireVersion [${questVersion}] >>>>>>>>>>>>>>>>")
        initializeQuestionFormMapper(questionnaireVersionList, ++currentRunningIndex)
    }


    // This method checks, If the given Question is part of a given For
    boolean isQuestionIncludedInForm(String questVersion, String questionId, String formId) {
        Collection<String> questionMappedForms = getQuestionMappedForms(questVersion, questionId)
        return questionMappedForms.contains(formId)
    }

    boolean isRQGIncludedInForm(String questVersion, String rqgId, String formId) {
        Collection<String> repeatingQuestionGroupMappedForms = getRQGMappedForms(questVersion, rqgId)
        return repeatingQuestionGroupMappedForms.contains(formId)
    }


    // This method checks, If the given Section is part of a given Form
    boolean isSectionIncludedInForm(String questVersion, String sectionId, String formId) {
        Collection<String> sectionMappedForms = getSectionMappedForms(questVersion, sectionId)
        return sectionMappedForms.contains(formId)
    }

    // This method checks, If the given SubSection is part of a given Form
    boolean isSubSectionIncludedInForm(String questVersion, String subSectionId, String formId) {
        Collection<String> subSectionMappedForms = getSubSectionMappedForms(questVersion, subSectionId)
        return subSectionMappedForms.contains(formId)
    }


    // This method checks, If the given Question is exclusively part of  a given Form
    // (Except the given form, No other forms should have this Question)
    boolean isOnlyIncluded(String questVersion, String questionId, String formId) {
        Collection<String> questionMappedForms = getQuestionMappedForms(questVersion, questionId)
        return (questionMappedForms.size() == 1) && questionMappedForms.contains(formId)
    }


    boolean isFormIncludedInBenefitCategory(String questVersion, String formId, ImmigrationBenefitCategory immigrationBenefitCategory) {
        String benefitCategoryId = immigrationBenefitCategory.easyVisaId
        Map<String, Set<String>> formBenefitCategoryMapper = versionToFormBenefitCategoryMap.get(questVersion, new HashMap<>())
        Collection<String> formMappedBenefitCategories = formBenefitCategoryMapper.get(benefitCategoryId, new HashSet<>())
        return formMappedBenefitCategories.contains(formId)
    }


    // This method checks, If the given Form is part of a given BenefitCategory,
    //                     then check the given section is a part of Form
    boolean isSectionIncluded(FormQuestionEvaluationContext formQuestionEvaluationContext) {
        return isFormIncludedInBenefitCategory(formQuestionEvaluationContext.questionnaireVersion, formQuestionEvaluationContext.formId, formQuestionEvaluationContext.immigrationBenefitCategory) &&
                isSectionIncludedInForm(formQuestionEvaluationContext.questionnaireVersion, formQuestionEvaluationContext.easyVisaId, formQuestionEvaluationContext.formId)
    }

    // This method checks, If the given Form is part of a given BenefitCategory,
    //                     then check the given question is a part of Form
    boolean isQuestionIncluded(FormQuestionEvaluationContext formQuestionEvaluationContext) {
        return isFormIncludedInBenefitCategory(formQuestionEvaluationContext.questionnaireVersion, formQuestionEvaluationContext.formId, formQuestionEvaluationContext.immigrationBenefitCategory) &&
                isQuestionIncludedInForm(formQuestionEvaluationContext.questionnaireVersion, formQuestionEvaluationContext.easyVisaId, formQuestionEvaluationContext.formId)
    }

    boolean isRQGIncluded(FormQuestionEvaluationContext formRqgEvaluationContext) {
        return isFormIncludedInBenefitCategory(formRqgEvaluationContext.questionnaireVersion, formRqgEvaluationContext.formId, formRqgEvaluationContext.immigrationBenefitCategory) &&
                isRQGIncludedInForm(formRqgEvaluationContext.questionnaireVersion, formRqgEvaluationContext.easyVisaId, formRqgEvaluationContext.formId)
    }

    // This method checks, If the given Form is part of a given BenefitCategory,
    //                     then check the given SubSection is a part of Form
    boolean isSubSectionIncluded(FormQuestionEvaluationContext formQuestionEvaluationContext) {
        return isFormIncludedInBenefitCategory(formQuestionEvaluationContext.questionnaireVersion, formQuestionEvaluationContext.formId, formQuestionEvaluationContext.immigrationBenefitCategory) &&
                isSubSectionIncludedInForm(formQuestionEvaluationContext.questionnaireVersion, formQuestionEvaluationContext.easyVisaId, formQuestionEvaluationContext.formId)
    }


    private Collection<String> getQuestionMappedForms(String questVersion, String questionId) {
        Map<String, Set<String>> questionFormMapper = versionToQuestionFormMap.get(questVersion, new HashMap<>())
        Collection<String> questionMappedForms = questionFormMapper.get(questionId, new HashSet<>())
        return questionMappedForms
    }
    private Collection<String> getRQGMappedForms(String questVersion, String rqgId) {
        Map<String, Set<String>> rqgFormMapper = versionToRQGFormMap.get(questVersion, new HashMap<>())
        Collection<String> rqgMappedForms = rqgFormMapper.get(rqgId, new HashSet<>())
        return rqgMappedForms
    }


    private Collection<String> getSectionMappedForms(String questVersion, String sectionId) {
        Map<String, Set<String>> sectionFormMapper = versionToSectionFormMap.get(questVersion, new HashMap<>())
        Collection<String> sectionMappedForms = sectionFormMapper.get(sectionId, new HashSet<>())
        return sectionMappedForms
    }

    private Collection<String> getSubSectionMappedForms(String questVersion, String subSectionId) {
        Map<String, Set<String>> subSectionFormMapper = versionToSubSectionFormMap.get(questVersion, new HashMap<>())
        Collection<String> subSectionMappedForms = subSectionFormMapper.get(subSectionId, new HashSet<>())
        return subSectionMappedForms
    }
}
