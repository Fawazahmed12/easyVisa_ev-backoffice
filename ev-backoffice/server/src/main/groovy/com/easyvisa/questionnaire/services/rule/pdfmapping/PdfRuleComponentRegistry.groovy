package com.easyvisa.questionnaire.services.rule.pdfmapping


import com.easyvisa.questionnaire.answering.rule.IFieldMappingRule
import org.springframework.stereotype.Component

@Component
class PdfRuleComponentRegistry {
    private Map<String, IFieldMappingRule> fieldMappingRuleMap = new HashMap<>()

    void registerFieldMappingRules(String ruleName, IFieldMappingRule ruleComponent) {
        fieldMappingRuleMap[ruleName] = ruleComponent
    }

    IFieldMappingRule getFieldMappingRule(String ruleName) {
        return fieldMappingRuleMap[ruleName]
    }
}
