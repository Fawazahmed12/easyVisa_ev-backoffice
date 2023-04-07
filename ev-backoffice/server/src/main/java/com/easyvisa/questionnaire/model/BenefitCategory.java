package com.easyvisa.questionnaire.model;

import org.apache.commons.lang.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class BenefitCategory extends EasyVisaNode {

    @Property
    private String categoryName;

    @Override
    public EasyVisaNode copy() {
        BenefitCategory benefitCategory = new BenefitCategory();
        this.copyBaseProps(benefitCategory);
        return benefitCategory;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        BenefitCategory targetPdfField = (BenefitCategory) target;
        targetPdfField.categoryName = this.categoryName;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String trimmedBenefitCategoryId(){
        return StringUtils.substringAfterLast(this.getId(), "BC_");
    }
}
