package com.easyvisa.questionnaire.services.rule.sectionvisibility


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.ISectionVisibilityRule
import com.easyvisa.questionnaire.answering.rule.SectionVisibilityRuleEvaluationContext
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Relatives Who Will Experience Extreme Hardship if You Are Inadmissible to the United States
 * Question: (Q_3658) Do you have another person, either your spouse, parent, child, fianc$e$(e), or child of a
 *                    fianc$e$(e), different from the relative you identified above, who either has or will suffer an
 *                    'extreme hardship' in the future if you are found inadmissible to the United States?
 *                     This person MUST be either a U.S. citizen or  LPR (Lawful Permanent Resident).
 * Form: FSS_601_extremeHardshipForRelatives
 * Notes: If user answered 'No', then when he clicks the 'Next' button, the Questionnaire goes to Section ' Statement of Applicant'.
 *        If user answered 'Yes', then the SECTION 'Parent/Child to be Considered' appears in the Index panel.
 *        Also, if user answered 'Yes' to question ,  THEN tick the box AFTER Question 8 in Part 5.
 *        Also, if user answered 'Yes', then when user clicks the 'Next' button, then they are taken to the section (that just appeared) called 'Parent/Child to be Considered'.
 *
 *
 *
 * Question: (Q_6023) Do you have another relative (only a spouse or parent and they MUST be either a U.S. citizen
 *                  or LPR (Lawful Permanent Resident)) who would experience extreme hardship if you were refused admission to the United States?
 * Form: FSS_601A_extremeHardshipForRelatives
 * Form Notes: This question does NOT appear if the user has already previously answered 'Yes' to this question
 *           (because ONLY 2 iterations of people are allowed in this 601A form).
 *           If user answered 'No', then when he clicks the 'Next' button, the Questionnaire goes to Section ' Statement of Applicant’.
 * */
@Component
class NonRelativeWouldExperienceExtremeHardshipApplicableRule implements ISectionVisibilityRule {

    private static String RULE_NAME = 'NonRelativeWouldExperienceExtremeHardshipApplicableRule'
    private static String QUESTION_FIELD_PATH_FROM_FORM601 = 'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3658'
    private static String QUESTION_FIELD_PATH_FROM_FORM601A = 'Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_6023/0';
    // It appears for one iteration

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerSectionVisibilityRules(RULE_NAME, this);
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(SectionVisibilityRuleEvaluationContext ruleEvaluationContext) {
        SectionNodeInstance sectionNodeInstance = ruleEvaluationContext.getSectionNodeInstance();
        sectionNodeInstance.setVisibility(false);
        this.validateAndUpdateSectionVisibility(ruleEvaluationContext, QUESTION_FIELD_PATH_FROM_FORM601);
        this.validateAndUpdateSectionVisibility(ruleEvaluationContext, QUESTION_FIELD_PATH_FROM_FORM601A);
    }


    void validateAndUpdateSectionVisibility(SectionVisibilityRuleEvaluationContext ruleEvaluationContext, String questionPath) {
        Answer informationAboutThisRelativeAnswer = Answer.findByPackageIdAndApplicantIdAndPath(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, questionPath)
        if (Answer.isValidAnswer(informationAboutThisRelativeAnswer) && informationAboutThisRelativeAnswer.value == RelationshipTypeConstants.YES.value) {
            SectionNodeInstance sectionNodeInstance = ruleEvaluationContext.getSectionNodeInstance();
            sectionNodeInstance.setVisibility(true);
        }
    }
}
