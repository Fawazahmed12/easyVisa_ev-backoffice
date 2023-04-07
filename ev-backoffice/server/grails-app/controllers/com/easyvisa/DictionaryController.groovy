package com.easyvisa

import com.easyvisa.enums.EmailTemplateVariable
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.ImmigrationBenefitGroups
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Role.EMPLOYEE])
class DictionaryController implements IErrorHandler {

    @Secured([Role.EMPLOYEE])
    def benefits() {
        Map result = [:]
        result['noPetitionerBenefitGroups'] = prepareBenefitGroup(ImmigrationBenefitGroups.noPetitionerGroups)
        result['benefitGroupsWithDerivatives'] = prepareBenefitGroup(ImmigrationBenefitGroups.beneficiaryAndDerivativesGroups)
        result['benefitGroupsNoDerivatives'] = prepareBenefitGroup(ImmigrationBenefitGroups.beneficiaryAndNoDerivativesGroups)
        result['searchGroups'] = prepareBenefitGroup(ImmigrationBenefitGroups.searchGroups)
        result['benefitCategories'] = prepareBenefitCategories(ImmigrationBenefitCategory.values().toList())
        result['disabledLPRCategories'] = prepareBenefitCategoryNames(ImmigrationBenefitCategory.disabledCategoriesForLPRPetitioner)
        result['disabledUSCitizenCategories'] = prepareBenefitCategoryNames(ImmigrationBenefitCategory.disabledCategoriesForUSCitizenPetitioner)
        result['disabledUSDerivativeCategories'] = prepareBenefitCategoryNames(ImmigrationBenefitCategory.disabledCategoriesForUSDerivativeApplicants)
        result['disabledLPRDerivativeCategories'] = prepareBenefitCategoryNames(ImmigrationBenefitCategory.disabledCategoriesForLPRDerivativeApplicants)
        render result as JSON
    }

    @Secured([Role.EMPLOYEE])
    def emailTemplateVariables(GetEmailTemplateVariableCommand command) {
        Map result = [:]
        command.emailTemplate.each {
            result[it.name()] = EmailTemplateVariable.getEmailTemplateVariables(it).collect {"${it.name()}"}.sort()
        }
        render result as JSON
    }

    private List<Map<String, String>> prepareBenefitGroup(List<ImmigrationBenefitGroups> groups) {
        groups.collect {
            [value:it.name(), label:it.displayName, shortName:it.shortName, note:it.note]
        }
    }

    private List<Map<String, String>> prepareBenefitCategories(List<ImmigrationBenefitCategory> categories) {
        categories.collect {
            [value:it.name(), label:it.abbreviation, searchLabel:it.searchAbbreviation, fullLabel:it.description,
             benefitGroup:it.group.name(), disabled:!it.active, note:it.note]
        } as List<Map<String, String>>
    }

    private List<String> prepareBenefitCategoryNames(List<ImmigrationBenefitCategory> categories) {
        categories*.name()
    }

}
