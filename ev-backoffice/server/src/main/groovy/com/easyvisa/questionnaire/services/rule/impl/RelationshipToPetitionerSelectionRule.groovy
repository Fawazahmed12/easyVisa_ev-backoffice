package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.PackageService
import com.easyvisa.SectionCompletionStatusService
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.benefitcategoryfeatures.BenefitCategoryFeaturesFactory
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Intro Questions For Beneficiary (The person who is immigrating to the United States)
 * Question: Q_1615 - What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States?
 *
 * Notes:  This rule will clear the answer of the following question, if user selects other than 'Spouse' as answer
 * Sec_basisPetitionToRemoveConditionsOnResidence/SubSec_basisPetitionToRemoveConditionsOnResidence/Q_1701
 *
 */

@Component
class RelationshipToPetitionerSelectionRule extends BaseComputeRule {

    private static String RULE_NAME = 'RelationshipToPetitionerSelectionRule';
    private static String MARRIED_TO_THE_SAME_PERSON_PATH = 'Sec_basisPetitionToRemoveConditionsOnResidence/SubSec_basisPetitionToRemoveConditionsOnResidence/Q_1701';

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    PackageService packageService
    AnswerService answerService;
    SectionCompletionStatusService sectionCompletionStatusService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance() as QuestionNodeInstance;
        Answer relationshipToPetitionerAnswer = questionNodeInstance.getAnswer()
        // The value of 'canTriggerWarning' becomes TRUE only if RelationshipToPetitioner is Spouse
        Boolean canTriggerWarning = packageService.getBenefitCategoryFeature(nodeRuleEvaluationContext.packageId)
                .canTriggerRelationshipToPetitionerAction(nodeRuleEvaluationContext)
        return new Outcome(relationshipToPetitionerAnswer.getValue(), canTriggerWarning)
    }


    /*
    * This method gets called only while answered the question other than 'Spouse'.
    *  As we need to display the following question only if user has answered 'Spouse'
    *
    *  Q_1701: Is the person you are currently married to, the same person who sponsored you for your conditional residence status (conditional 'green card')?
    *
    */
    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext answerContext, Answer previousAnswer) {
        Answer relationshipToPetitionerAnswer = answerContext.findAnswerByPath(MARRIED_TO_THE_SAME_PERSON_PATH)
        if(Answer.isValidAnswer(relationshipToPetitionerAnswer)) {
            answerService.removeAnswers([relationshipToPetitionerAnswer])
        }
    }
}

