package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.Form;
import org.neo4j.ogm.response.model.QueryResultModel;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FormDAO {

    @Autowired
    Session neo4jSession;

    @Autowired
    FormRepository formRepository;

    public Form formByContinuationSheet(String questVersion, String continuationSheet) {
        Form form = formRepository.findByContinuationSheet(questVersion, continuationSheet);
        return form;
    }

    public Form getFormById(String questVersion, String formId) {
        return formRepository.getFormById(questVersion, formId);
    }

    public List<Form> findAllForms(String questVersion) {
        List<Form> formList = formRepository.getAll(questVersion);
        return Collections.unmodifiableList(formList);
    }

    public List<Form> findAllQuestionnaireForms(String questVersion, List<String> questionIdList) {
        List<Form> formList = formRepository.findAllQuestionnaireForms(questVersion, questionIdList);
        return formList;
    }

    public List<Form> findFormsByBenefitCategory(String questVersion, String benefitCategoryId) {
        List<Form> formList = formRepository.findAllFormsByBenefitCategory(questVersion, benefitCategoryId);
        return Collections.unmodifiableList(formList);
    }


    public Map<String, Set<String>> buildQuestionFormMap(String questVersion) {
        String formQuestionGraphCQL = "MATCH (B:BenefitCategory{questVersion:'questVersionParam'})-[:has]->(form:Form)-[relrs:has]->(section:Section)-[*..6]->(question:Question) RETURN  question.easyVisaId as questionId, form.easyVisaId as formId";
        formQuestionGraphCQL = formQuestionGraphCQL.replaceAll("questVersionParam", questVersion);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(formQuestionGraphCQL, new HashMap<>());

        Map<String, Set<String>> questionFormMapper = new HashMap<>();
        for (Map<String, Object> itemMap : queryResultModel) {
            String questionId = (String) itemMap.get("questionId");
            String formId = (String) itemMap.get("formId");

            Set<String> questionMappedForms = questionFormMapper.computeIfAbsent(questionId, k -> new HashSet<>());
            questionMappedForms.add(formId);
        }
        return questionFormMapper;
    }
    public Map<String, Set<String>> buildRQGFormMap(String questVersion) {
        String formQuestionGraphCQL = "MATCH (B:BenefitCategory{questVersion:'questVersionParam'})-[:has]->(form:Form)-[*..6]->(repeatingQuestionGroup:RepeatingQuestionGroup)  RETURN  repeatingQuestionGroup.easyVisaId as repeatingQuestionGroupId, form.easyVisaId as formId";
        formQuestionGraphCQL = formQuestionGraphCQL.replaceAll("questVersionParam", questVersion);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(formQuestionGraphCQL, new HashMap<>());

        Map<String, Set<String>> repeatingQuestionGroupFormMapper = new HashMap<>();
        for (Map<String, Object> itemMap : queryResultModel) {
            String repeatingQuestionGroupId = (String) itemMap.get("repeatingQuestionGroupId");
            String formId = (String) itemMap.get("formId");

            Set<String> repeatingQuestionGroupMappedForms = repeatingQuestionGroupFormMapper.computeIfAbsent(repeatingQuestionGroupId, k -> new HashSet<>());
            repeatingQuestionGroupMappedForms.add(formId);
        }
        return repeatingQuestionGroupFormMapper;
    }

    public Map<String, Set<String>> buildSubSectionFormMap(String questVersion) {

        String formSubSectionGraphCQL = "MATCH (B:BenefitCategory{questVersion:'questVersionParam'})-[:has]->(form:Form)-[:has]->(:FormSubSection)<-[:has]-(ss:SubSection) RETURN  ss.easyVisaId as subSectionId, form.easyVisaId as formId";
        formSubSectionGraphCQL = formSubSectionGraphCQL.replaceAll("questVersionParam", questVersion);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(formSubSectionGraphCQL, new HashMap<>());

        Map<String, Set<String>> subSectionFormMapper = new HashMap<>();
        for (Map<String, Object> itemMap : queryResultModel) {
            String subSectionId = (String) itemMap.get("subSectionId");
            String formId = (String) itemMap.get("formId");

            Set<String> subSectionMappedForms = subSectionFormMapper.computeIfAbsent(subSectionId, k -> new HashSet<>());
            subSectionMappedForms.add(formId);
        }
        return subSectionFormMapper;
    }

    public Map<String, Set<String>> buildFormBenefitCategoryMap(String questVersion) {
        String benefitCategoryFormGraphCQL = "MATCH (bc:BenefitCategory{questVersion:'questVersionParam'})-[:has]->(form:Form) RETURN  bc.easyVisaId as benefitCategoryId, form.easyVisaId as formId";
        benefitCategoryFormGraphCQL = benefitCategoryFormGraphCQL.replaceAll("questVersionParam", questVersion);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(benefitCategoryFormGraphCQL, new HashMap<>());

        Map<String, Set<String>> formBenefitCategoryMapper = new HashMap<>();
        for (Map<String, Object> itemMap : queryResultModel) {
            String benefitCategoryId = (String) itemMap.get("benefitCategoryId");
            String formId = (String) itemMap.get("formId");

            Set<String> formMappedBenefitCategories = formBenefitCategoryMapper.computeIfAbsent(benefitCategoryId, k -> new HashSet<>());
            formMappedBenefitCategories.add(formId);
        }
        return formBenefitCategoryMapper;
    }
}
