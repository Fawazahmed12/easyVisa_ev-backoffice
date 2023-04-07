package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.Document;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends Neo4jRepository<Document, Long> {

    @Query("MATCH (document:Document{questVersion:$questVersion, easyVisaId:$documentId}) RETURN document")
    Document getDocumentById(@Param("questVersion") String questVersion, @Param("documentId") String documentId);


    @Query("MATCH (document:Document{questVersion:$questVersion}) RETURN document ORDER BY document.order")
    List<Document> getAll(@Param("questVersion") String questVersion);
}
