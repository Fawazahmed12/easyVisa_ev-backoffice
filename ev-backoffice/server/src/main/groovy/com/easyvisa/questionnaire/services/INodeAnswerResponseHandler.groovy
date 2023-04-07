package com.easyvisa.questionnaire.services

import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.dto.AnswerRequestDto

public interface INodeAnswerResponseHandler {
    Outcome handle(AnswerRequestDto answerRequestDto);
}
