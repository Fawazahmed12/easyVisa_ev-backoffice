package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.EasyVisaNode;
import com.easyvisa.questionnaire.model.FormSubSection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FormSubSectionRepository extends Neo4jRepository<FormSubSection, Long> {

    @Query("MATCH (B:BenefitCategory{questVersion:$questVersion, easyVisaId:$benefitCategoryId})-[:has]->(form:Form)-[relrs:has]->(section:Section{easyVisaId:$sectionId})-[:has]->"
            + "(subsection:SubSection)-[:has]->" + "(formSubsection:FormSubSection)<-[:has]-(form) RETURN subsection, formSubsection")
    List<Map<String, EasyVisaNode>> findByBenefitCategoryAndSection(@Param("questVersion") String questVersion,
                                                                    @Param("benefitCategoryId") String benefitCategoryId,
                                                                    @Param("sectionId") String sectionId);


    @Query("MATCH (form:Form{questVersion:$questVersion, easyVisaId:$formId})-[relrs:has]->(section:Section{easyVisaId:$sectionId})-[:has]->"
            + "(subsection:SubSection)-[:has]->" + "(formSubsection:FormSubSection)<-[:has]-(form) RETURN subsection, formSubsection")
    List<Map<String, EasyVisaNode>> findByFormAndSection(@Param("questVersion") String questVersion,
                                                         @Param("formId") String formId,
                                                         @Param("sectionId") String sectionId);


    @Query("MATCH (form:Form{questVersion:$questVersion}) WHERE  NOT (form.easyVisaId = $formId)"
            + "MATCH (form)-[relrs:has]->(section:Section{questVersion:$questVersion, easyVisaId:$sectionId})-[:has]->"
            + "(subsection:SubSection)-[:has]->" + "(formSubsection:FormSubSection)<-[:has]-(form) RETURN formSubsection.easyVisaId")
    List<String> findByExcludedFormAndSection(@Param("questVersion") String questVersion,
                                              @Param("formId") String formId,
                                              @Param("sectionId") String sectionId);
}
