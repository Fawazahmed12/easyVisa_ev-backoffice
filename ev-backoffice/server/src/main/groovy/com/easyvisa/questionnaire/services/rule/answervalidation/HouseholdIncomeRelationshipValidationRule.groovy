package com.easyvisa.questionnaire.services.rule.answervalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.AnswerValidationRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.IAnswerValidationRule
import com.easyvisa.questionnaire.dto.AnswerValidationDto
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.util.stream.Collectors

/**
 * Section: Family Information
 * SubSection: Household Income
 * Question : (Q_1320) Relationship to you
 *
 * Notes:  The option "Intending Immigrant WITHOUT Dependents" CANNOT be selected for more than one iteration of this subsection "Household Income & Assets".
 * If the user tries to select that option a second time, a pop up appears that says:
 *      The option "Intending Immigrant WITHOUT Dependents" CANNOT be selected for more than one household member who is contributing
 *      assets to the Petitioner in order to meet the minimum poverty guideline requirements.
 */

@Component
class HouseholdIncomeRelationshipValidationRule implements IAnswerValidationRule {

    private static String RULE_NAME = 'HouseholdIncomeRelationshipValidationRule'
    private static String RELATIONSHIP_FIELD_PATH = 'Sec_familyInformation/SubSec_householdIncome/Q_1320'
    private static String IMMIGRANT_RELATIONSHIP_VALUE = 'Intending Immigrant WITHOUT Dependents'
    private static String IMMIGRANT_RELATIONSHIP_ERROR_MESSAGE = 'The option "Intending Immigrant WITHOUT Dependents" CANNOT be selected for more than one household member who is contributing assets to the Petitioner in order to meet the minimum poverty guideline requirements.'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerValidationRule(RULE_NAME, this);
    }

    @Override
    AnswerValidationDto validateAnswer(AnswerValidationRuleEvaluationContext ruleEvaluationContext) {
        Answer answerToSave = ruleEvaluationContext.answerToSave;
        QuestionNodeInstance questionNodeInstance = ruleEvaluationContext.questionNodeInstance;
        List<Answer> relationShipAnswerList = ruleEvaluationContext.findAnswerListByPath(RELATIONSHIP_FIELD_PATH);

        List<Answer> immigrantRelationshipAnswerList = relationShipAnswerList.stream()
                .filter({ relationshipAnswer -> Answer.isValidAnswer(relationshipAnswer) && (relationshipAnswer.value == IMMIGRANT_RELATIONSHIP_VALUE) })
                .collect(Collectors.toList());

        AnswerValidationDto answerValidationDto = ruleEvaluationContext.constructAnswerValidationDto();
        if (immigrantRelationshipAnswerList.size() == 1 && answerToSave.value == IMMIGRANT_RELATIONSHIP_VALUE) {
            answerValidationDto.setErrorMessage(IMMIGRANT_RELATIONSHIP_ERROR_MESSAGE);
            String answerPath = ruleEvaluationContext.getAnswerToSavePath();
            Answer previousAnswer = ruleEvaluationContext.findAnswerByPath(answerPath);
            answerValidationDto.setResetValue(previousAnswer?.value);
            return answerValidationDto;
        }
        return answerValidationDto;
    }

}
