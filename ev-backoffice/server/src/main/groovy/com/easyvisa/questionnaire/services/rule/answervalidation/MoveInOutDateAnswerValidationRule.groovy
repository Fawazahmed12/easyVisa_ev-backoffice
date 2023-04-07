package com.easyvisa.questionnaire.services.rule.answervalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.AnswerValidationRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.IAnswerValidationRule
import com.easyvisa.questionnaire.dto.AnswerValidationDto
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.util.DateUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate

/**
 * Section: Address History
 * SubSection: Previous Physical Address(es) within 5 Years
 * Applicant: Petitioner / Beneficiary
 * Question : (Q_64) & (Q_2050) When did you move into this address?
 *            (Q_65) & (Q_2052) When did you move out of this address?
 *
 * Notes:  This rule validates whether the move-in date should be lesser than move-out date
 *         of the same repeating iteration.
 *
 *  If it passes the validation, then we are allowing the UI to save the move-in/out date
 *  If it fails, then shou the pop-up with error message
 * */

@Component
class MoveInOutDateAnswerValidationRule implements IAnswerValidationRule {
    private static String RULE_NAME = 'MoveInOutDateAnswerValidationRule'

    private static String MOVE_IN_OUT_DATE_ERROR_MESSAGE = "Move-Into date '#moveInDate' should be less than Move-Out date '#moveOutDate'."

    private static String MOVE_IN_DATE = '#moveInDate'
    private static String MOVE_OUT_DATE = '#moveOutDate'
    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerValidationRule(RULE_NAME, this);
    }

    @Override
    AnswerValidationDto validateAnswer(AnswerValidationRuleEvaluationContext ruleEvaluationContext) {
        Answer answerToSave = ruleEvaluationContext.answerToSave;
        QuestionNodeInstance questionNodeInstance = ruleEvaluationContext.questionNodeInstance;

        String[] ruleParams = questionNodeInstance.getAnswerValidationRuleParam().split(",")
        String moveDateType = ruleParams[0]
        String VALIDATE_AGAINST_PATH = ruleParams[1] + "/" + answerToSave.index;
        Answer validateAgainstAnswer = ruleEvaluationContext.findAnswerByPath(VALIDATE_AGAINST_PATH);
        AnswerValidationDto answerValidationDto = ruleEvaluationContext.constructAnswerValidationDto()
        if (Answer.isValidAnswer(validateAgainstAnswer)) {
            Answer moveInDate = ("#${moveDateType}" == MOVE_IN_DATE) ? validateAgainstAnswer : answerToSave
            Answer moveOutDate = ("#${moveDateType}" == MOVE_OUT_DATE) ? validateAgainstAnswer : answerToSave
            validateMoveInOutDate(moveInDate, moveOutDate, answerValidationDto, ruleEvaluationContext)
        }
        return answerValidationDto
    }

    private void validateMoveInOutDate(Answer moveInDate, Answer moveOutDate, AnswerValidationDto answerValidationDto,
                                       AnswerValidationRuleEvaluationContext ruleEvaluationContext) {
        String startDateAsString = DateUtil.normalizeEasyVisaDateFormat(moveInDate.getValue());
        String endDateAsString = DateUtil.normalizeEasyVisaDateFormat(moveOutDate.getValue());
        LocalDate startDate = DateUtil.localDate(startDateAsString);
        LocalDate endDate = DateUtil.localDate(endDateAsString);
        if (startDate.isAfter(endDate)) {
            String UPDATED_MOVE_IN_OUT_DATE_ERROR_MESSAGE = MOVE_IN_OUT_DATE_ERROR_MESSAGE
                    .replaceAll(MOVE_IN_DATE, "${startDate}")
                    .replaceAll(MOVE_OUT_DATE, "${endDate}")
            answerValidationDto.setErrorMessage(UPDATED_MOVE_IN_OUT_DATE_ERROR_MESSAGE);
            String answerToSavePath = ruleEvaluationContext.getAnswerToSavePath();
            Answer previousAnswer = ruleEvaluationContext.findAnswerByPath(answerToSavePath);
            answerValidationDto.setResetValue(previousAnswer?.value);
        }
    }
}


