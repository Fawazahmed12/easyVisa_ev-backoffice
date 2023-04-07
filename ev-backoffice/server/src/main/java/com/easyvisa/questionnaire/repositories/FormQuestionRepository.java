package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.FormQuestion;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormQuestionRepository extends Neo4jRepository<FormQuestion, Long> {

    @Query("MATCH (form:Form{questVersion:$questVersion, easyVisaId:$formId})-[relrs:has]->(formQuestion:FormQuestion)<-[:has]-"
            + "(question:Question) WHERE question.easyVisaId IN $questionIdList RETURN formQuestion")
    List<FormQuestion> findByFormAndQuestionList(@Param("questVersion") String questVersion, @Param("formId") String formId, @Param("questionIdList") List<String> questionIdList);
}
