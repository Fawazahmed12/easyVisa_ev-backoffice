package com.easyvisa.questionnaire.services.rule.answervalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.AnswerValidationRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.IAnswerValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.AnswerValidationDto
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.services.rule.sectioncompletion.FamilyInformationCompletionRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.util.stream.Collectors

/**
 *
 * Section: Family Information
 * SubSection: Children Information
 * Question: Is [insert this child's name] applying with the Beneficiary, [insert Beneficiary Name]?
 *
 * Section: Family Information
 * SubSection: Current Spouse
 * Question:  Is [insert Beneficiary Name]'s spouse applying with the [insert Beneficiary Name]?
 *
 * Notes:  We count the yeses to that question versus the number of paid derivatives in the package. If the user attempts to add another, they get an error popup:
 */

@Component
class FamilyInformationAddDerivativeValidationRule implements IAnswerValidationRule {

    private static String RULE_NAME = 'FamilyInformationAddDerivativeValidationRule'
    private static String IMMIGRANT_RELATIONSHIP_ERROR_MESSAGE = 'You have reached the allowable number of beneficiaries in this package. Contact your legal representative to add another.'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    FamilyInformationCompletionRule familyInformationCompletionRule;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerValidationRule(RULE_NAME, this);
    }

    @Override
    AnswerValidationDto validateAnswer(AnswerValidationRuleEvaluationContext answerValidationContext) {
        Answer answerToSave = answerValidationContext.answerToSave;
        String answerValue = EasyVisaNode.normalizeAnswer(answerToSave.getValue())
        AnswerValidationDto answerValidationDto = answerValidationContext.constructAnswerValidationDto();
        NodeRuleEvaluationContext ruleEvaluationContext = answerValidationContext.getNodeRuleEvaluationContext();
        boolean hasDerivativeAnswersEqualToPackageDerivatives = this.familyInformationCompletionRule.hasQuestionnaireDerivativesEqualToPackageDerivatives(ruleEvaluationContext);
        /* TODO.. No need to validate the 'yes' questions from SPouse/Children subsections
            Ref: https://easyvisa.atlassian.net/browse/EV-2104?focusedCommentId=16828
        if(hasDerivativeAnswersEqualToPackageDerivatives && answerValue == RelationshipTypeConstants.YES.value) {
            answerValidationDto.setErrorMessage(IMMIGRANT_RELATIONSHIP_ERROR_MESSAGE);
            String answerPath = this.getAnswerPath(answerToSave);
            Answer previousAnswer = answerValidationContext.findAnswerByPath(answerPath);
            answerValidationDto.setResetValue(previousAnswer?.value);
        }*/
        return answerValidationDto;
    }

    String getAnswerPath(Answer answer) {
        List answerPathParts = [answer.sectionId, answer.subsectionId, answer.questionId]
        if (answer.index >= 0) {
            answerPathParts.add(answer.index)
        }
        String answerPath = answerPathParts.join('/')
        return answerPath
    }
}
