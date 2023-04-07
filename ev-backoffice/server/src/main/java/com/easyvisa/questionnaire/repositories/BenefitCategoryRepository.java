package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.BenefitCategory;
import org.springframework.data.neo4j.annotation.Query;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenefitCategoryRepository extends Neo4jRepository<BenefitCategory, Long> {

    @Query("MATCH (n:BenefitCategory{questVersion:$questVersion}) RETURN n")
    List<BenefitCategory> getAll(@Param("questVersion") String questVersion);
}
