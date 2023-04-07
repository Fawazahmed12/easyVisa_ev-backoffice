package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.MilestoneType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MilestoneTypeDAO {

    @Autowired
    MilestoneTypeRepository milestoneTypeRepository;

    public List<MilestoneType> findMilestoneTypesByBenefitCategory(String questVersion, String benefitCategoryId) {
        List<MilestoneType> milestoneTypeList = milestoneTypeRepository.findAllMilestoneTypesByBenefitCategory(questVersion, benefitCategoryId);
        return milestoneTypeList;
    }

    public MilestoneType getMilestoneTypeById(String questVersion, String milestoneTypeId) {
        return milestoneTypeRepository.getMilestoneTypeById(questVersion, milestoneTypeId);
    }
}
