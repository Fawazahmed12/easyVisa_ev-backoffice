package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.RepeatingQuestionGroup;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepeatingQuestionGroupDAO {

    @Autowired
    RepeatingQuestionGroupRepository repeatingQuestionGroupRepository;

    public RepeatingQuestionGroup findByEasyVisaId(String questVersion, String easyVisaId) {
        return (RepeatingQuestionGroup) repeatingQuestionGroupRepository.findByEasyVisaId(questVersion, easyVisaId).copy();
    }
}
