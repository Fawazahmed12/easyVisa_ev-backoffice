package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.EasyVisaNode;
import com.easyvisa.questionnaire.model.Question;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface QuestionRepository extends Neo4jRepository<Question, Long> {

    @Query("MATCH (repeatingGroup:RepeatingQuestionGroup{questVersion:$questVersion, easyVisaId:$easyVisaId})-[rel]->(child)  RETURN child ORDER BY rel.order")
    List<Question> findChildrenOfRepeatingGroupByEasyVisaId(@Param("questVersion") String questVersion, @Param("easyVisaId") String easyVisaId);

    @Query("MATCH (repeatingGroup:RepeatingQuestionGroup{questVersion:$questVersion, easyVisaId:$easyVisaId})-[*..5]->(child)  RETURN child")
    List<Question> findLinkedChildrenOfRepeatingGroupByEasyVisaId(@Param("questVersion") String questVersion, @Param("easyVisaId") String easyVisaId);

    @Query("MATCH (question:Question{questVersion:$questVersion, easyVisaId:$easyVisaId}) RETURN question")
    Question findByEasyVisaId(@Param("questVersion") String questVersion, @Param("easyVisaId") String easyVisaId);

    @Query("MATCH (form:Form{questVersion:$questVersion,easyVisaId:$formId})-[:has]->(formSubsection:FormSubSection)<-[:has]-(subsection:SubSection{easyVisaId:$subSectionId}),(formSubsection)-[*]->(question:Question) return question")
    List<Question> findQuestionByFormSubsection(@Param("questVersion") String questVersion,@Param("formId") String formId,@Param("subSectionId") String subSectionId);
}

