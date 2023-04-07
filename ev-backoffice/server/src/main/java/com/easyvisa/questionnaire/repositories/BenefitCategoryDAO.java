package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.BenefitCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BenefitCategoryDAO {

    @Autowired
    BenefitCategoryRepository benefitCategoryRepository;

    public List<BenefitCategory> findAllBenefitCategory(String questVersion) {
        List<BenefitCategory> benefitCategoryList = benefitCategoryRepository.getAll(questVersion);
        return Collections.unmodifiableList(benefitCategoryList);
    }
}
