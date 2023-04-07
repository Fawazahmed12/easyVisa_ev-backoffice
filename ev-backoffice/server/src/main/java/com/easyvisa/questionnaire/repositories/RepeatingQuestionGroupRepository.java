package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.RepeatingQuestionGroup;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepeatingQuestionGroupRepository extends Neo4jRepository<RepeatingQuestionGroup, Long> {

    @Query("MATCH (repeatingQuestionGroup:RepeatingQuestionGroup{questVersion:$questVersion, easyVisaId:$easyVisaId}) RETURN repeatingQuestionGroup")
    RepeatingQuestionGroup findByEasyVisaId(@Param("questVersion") String questVersion,
                                            @Param("easyVisaId") String easyVisaId);
}
