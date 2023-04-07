package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.Section;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends Neo4jRepository<Section, Long> {

    @Query("MATCH (section:Section{questVersion:$questVersion, easyVisaId:$easyVisaId})  RETURN section")
    Section findByEasyVisaId(@Param("questVersion") String questVersion,
                             @Param("easyVisaId") String easyVisaId);

    @Query("MATCH (bc:BenefitCategory{questVersion:$questVersion, easyVisaId:$benefitCategoryId})-[:has]->(form:Form)-[relrs:has]->(section:Section{applicantType:$applicantType}) RETURN section")
    List<Section> findByBenefitCategoryId(@Param("questVersion") String questVersion,
                                 @Param("benefitCategoryId") String benefitCategoryId,
                                 @Param("applicantType") String applicantType);

    @Query("MATCH (form:Form{questVersion:$questVersion, easyVisaId:$formId})-[relrs:has]->(section:Section) RETURN section")
    List<Section> sectionsByForm(@Param("questVersion") String questVersion,
                                 @Param("formId") String formId);

    @Query("MATCH (form:Form{questVersion:$questVersion})-[relrs:has]->(section:Section) RETURN section")
    List<Section> sectionsByQuestVersion(@Param("questVersion") String questVersion);
}


