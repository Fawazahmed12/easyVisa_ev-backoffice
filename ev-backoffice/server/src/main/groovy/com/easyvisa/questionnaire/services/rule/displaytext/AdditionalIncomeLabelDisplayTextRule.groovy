package com.easyvisa.questionnaire.services.rule.displaytext

import com.easyvisa.PackageService
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDisplayTextRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Family Information
 * SubSection: Household Size/Dependents
 * Question: (Q_1312) You need additional income or assets in order to sponsor your [relative(s)/fiancé(e)].
 *
 * P.Notes: The variable in the red text warning in the rectangular brackets [] should be "fiancé(e)"
 *                   if the Immigration benefit category is K-1/K-3 AND
 *                   the Petitioner answered either Single (NEVER Married), Divorced, Widowed, Legally Separated, Marriage Annulled, or Other in the question "What is your current marital status?".
 *          The variable in the red text warning in the rectangular brackets [] should be "relative(s)"
 *                   if they selected any other Immigration Benefit Category (other than K-1/K-3) OR
 *                   if the Petitioner selected K-1/K-3 AND answered 'Married' to question "What is your current marital status?"
 *
 *  Notes: Questions in this subsection has connected to the Form-864 alone
 */

@CompileStatic
@Component
class AdditionalIncomeLabelDisplayTextRule implements IDisplayTextRule {

    private static String RULE_NAME = 'AdditionalIncomeLabelDisplayTextRule'

    private static String TEMPLATE_PLACEHOLDER = "\\[relative\\(s\\)/fiancé\\(e\\)\\]";
    private static String RELATIVE_TEXT = "relative(s)";
    private static String FIANCE_TEXT = "fiancé(e)";

    // What is your current marital status?
    private static String MARITAL_STATUS_PATH = "Sec_familyInformation/SubSec_maritalStatus/Q_1204";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    PackageService packageService

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDisplayTextRule(RULE_NAME, this);
    }

    @Override
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    String generateDisplayText(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String displayText = questionNodeInstance.getDisplayText();
        String updatedDisplayText = displayText;
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(ruleEvaluationContext.packageId)
        if (!ImmigrationBenefitCategory.K1K3.equals(directBenefitCategory)) {
            updatedDisplayText = updatedDisplayText.replaceAll(TEMPLATE_PLACEHOLDER, RELATIVE_TEXT);
            return updatedDisplayText
        }

        Answer maritalStatusAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.getPackageId(),
                ruleEvaluationContext.getApplicantId(), MARITAL_STATUS_PATH);
        if (Answer.isValidAnswer(maritalStatusAnswer)) {
            String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue());
            String replacableText = (maritalStatusAnswerValue == RelationshipTypeConstants.MARRIED.value) ? RELATIVE_TEXT : FIANCE_TEXT;
            updatedDisplayText = updatedDisplayText.replaceAll(TEMPLATE_PLACEHOLDER, replacableText);
        }
        return updatedDisplayText;
    }
}
