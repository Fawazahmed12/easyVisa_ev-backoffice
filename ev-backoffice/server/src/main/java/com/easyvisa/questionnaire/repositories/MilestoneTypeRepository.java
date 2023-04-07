package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.MilestoneType;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneTypeRepository extends Neo4jRepository<MilestoneType, Long> {

    @Query("MATCH (benefitCategory:BenefitCategory{questVersion:$questVersion, easyVisaId:$benefitCategoryId})-[relrs:has]->(milestoneType:MilestoneType) RETURN milestoneType ORDER BY milestoneType.order")
    List<MilestoneType> findAllMilestoneTypesByBenefitCategory(@Param("questVersion") String questVersion, @Param("benefitCategoryId") String benefitCategoryId);

    @Query("MATCH (milestoneType:MilestoneType{questVersion:$questVersion, easyVisaId:$milestoneTypeId}) RETURN milestoneType")
    MilestoneType getMilestoneTypeById(@Param("questVersion") String questVersion, @Param("milestoneTypeId") String milestoneTypeId);
}
