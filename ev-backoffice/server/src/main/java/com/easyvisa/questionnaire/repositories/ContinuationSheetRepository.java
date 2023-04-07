package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.ContinuationSheet;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContinuationSheetRepository extends Neo4jRepository<ContinuationSheet, Long> {

    @Query("MATCH (form:Form{easyVisaId:$formId, questVersion:$questVersion})-[relrs:has]->(continuationSheet:ContinuationSheet) RETURN continuationSheet")
    List<ContinuationSheet> findByForm(@Param("questVersion") String questVersion, @Param("formId") String formId);

    @Query("MATCH (continuationSheet:ContinuationSheet{easyVisaId:$continuationSheetId, questVersion:$questVersion}) RETURN continuationSheet")
    ContinuationSheet findById(@Param("questVersion") String questVersion, @Param("continuationSheetId") String continuationSheetId);

    @Query("MATCH (question:Question{questVersion:$questVersion}) WHERE question.easyVisaId IN $questionIdList MATCH (question)-[*..6]->(formQuestion:FormQuestion)-[:has]->(pdfField:PdfField) WHERE pdfField.continuationSheetNodeId IS NOT NULL RETURN pdfField.continuationSheetNodeId")
    List<String> findQuestionnaireContinuationSheetIdList(@Param("questVersion") String questVersion, @Param("questionIdList") List<String> questionIdList);
}
