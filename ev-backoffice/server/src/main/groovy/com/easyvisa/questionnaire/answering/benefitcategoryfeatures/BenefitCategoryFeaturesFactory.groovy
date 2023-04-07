package com.easyvisa.questionnaire.answering.benefitcategoryfeatures

import com.easyvisa.enums.ImmigrationBenefitCategory

class BenefitCategoryFeaturesFactory {

    static BaseBenefitCategoryFeature getBenefitCategoryFeatures(ImmigrationBenefitCategory immigrationBenefitCategory) {
        switch (immigrationBenefitCategory) {
            case ImmigrationBenefitCategory.DISABILITY:
                return new DisabilityBenefitCategoryFeature()
            case ImmigrationBenefitCategory.EAD:
                return new EADBenefitCategoryFeature()
            case ImmigrationBenefitCategory.F1_A:
                return new F1ABenefitCategoryFeature()
            case ImmigrationBenefitCategory.F2_A:
                return new F2ABenefitCategoryFeature()
            case ImmigrationBenefitCategory.F3_A:
                return new F3ABenefitCategoryFeature()
            case ImmigrationBenefitCategory.F4_A:
                return new F4ABenefitCategoryFeature()
            case ImmigrationBenefitCategory.F1_B:
                return new F1BBenefitCategoryFeature()
            case ImmigrationBenefitCategory.F2_B:
                return new F2BBenefitCategoryFeature()
            case ImmigrationBenefitCategory.F3_B:
                return new F3BBenefitCategoryFeature()
            case ImmigrationBenefitCategory.F4_B:
                return new F4BBenefitCategoryFeature()
            case ImmigrationBenefitCategory.IR1:
                return new IR1BenefitCategoryFeature()
            case ImmigrationBenefitCategory.IR2:
                return new IR2BenefitCategoryFeature()
            case ImmigrationBenefitCategory.IR5:
                return new IR5BenefitCategoryFeature()
            case ImmigrationBenefitCategory.K1K3:
                return new K1K3BenefitCategoryFeature()
            case ImmigrationBenefitCategory.K2K4:
                return new K2K4BenefitCategoryFeature()
            case ImmigrationBenefitCategory.LPRCHILD:
                return new LPRChildBenefitCategoryFeature()
            case ImmigrationBenefitCategory.LPRSPOUSE:
                return new LPRSpouseBenefitCategoryFeature()
            case ImmigrationBenefitCategory.NATURALIZATION:
                return new NaturalizationBenefitCategoryFeature()
            case ImmigrationBenefitCategory.REMOVECOND:
                return new RemoveCondnBenefitCategoryFeature()
            case ImmigrationBenefitCategory.SIX01:
                return new Six01BenefitCategoryFeature()
            case ImmigrationBenefitCategory.SIX01A:
                return new Six01ABenefitCategoryFeature()
        }
    }
}
