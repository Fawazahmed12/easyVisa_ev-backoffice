package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.Form;
import com.easyvisa.questionnaire.model.Question;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FormRepository extends Neo4jRepository<Form, Long> {

    @Query("MATCH (form:Form{questVersion:$questVersion}) RETURN form ORDER BY form.order")
    List<Form> getAll(@Param("questVersion") String questVersion);

    @Query("MATCH (form:Form{questVersion:$questVersion, easyVisaId:$formId}) RETURN form")
    Form getFormById(@Param("questVersion") String questVersion, @Param("formId") String formId);

    @Query("MATCH (continuationSheet:ContinuationSheet{questVersion:$questVersion, easyVisaId:$continuationSheetId})<-[relrs:has]-(form:Form) RETURN form")
    Form findByContinuationSheet(@Param("questVersion") String questVersion, @Param("continuationSheetId") String continuationSheetId);

    @Query("MATCH (question:Question{questVersion:$questVersion}) WHERE question.easyVisaId IN $questionIdList MATCH (question)<-[*..6]-(formSubsection:FormSubSection)<-[:has]-(form:Form) RETURN form ORDER BY form.order")
    List<Form> findAllQuestionnaireForms(@Param("questVersion") String questVersion, @Param("questionIdList") List<String> questionIdList);

    @Query("MATCH (benefitCategory:BenefitCategory{questVersion:$questVersion, easyVisaId:$benefitCategoryId})-[relrs:has]->(form:Form) RETURN form ORDER BY form.order")
    List<Form> findAllFormsByBenefitCategory(@Param("questVersion") String questVersion, @Param("benefitCategoryId") String benefitCategoryId);
}

