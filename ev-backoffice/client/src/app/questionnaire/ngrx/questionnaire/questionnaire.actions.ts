import { Action } from '@ngrx/store';
import { FormlyFieldConfig } from '@ngx-formly/core';

import {
  Answer,
  AnswerModel,
  AnswerSaveRequestItem, AnswerValidationModel, QuestionnaireAccessState,
  QuestionnaireModel,
  QuestionnaireParam,
  RepeatGroupAddRequestItem,
  RepeatGroupModel,
  RepeatGroupRemoveRequestItem, SectionWarningModel
} from '../../models/questionnaire.model';

import { QUESTIONNAIRE_STATE } from './questionnaire.state';


export const QuestionnaireActionTypes = {
  GetQuestionnaireAccessState: `[${QUESTIONNAIRE_STATE}] Questionnaire Access Request`,
  GetQuestionnaireAccessStateSuccess: `[${QUESTIONNAIRE_STATE}] Questionnaire Access Success`,
  GetQuestionnaireAccessStateFailure: `[${QUESTIONNAIRE_STATE}] Questionnaire Access Failure`,
  GetSections: `[${QUESTIONNAIRE_STATE}] Sections Request`,
  GetSectionsSuccess: `[${QUESTIONNAIRE_STATE}] Sections Success`,
  GetSectionsFailure: `[${QUESTIONNAIRE_STATE}] Sections Failure`,
  GetQuestions: `[${QUESTIONNAIRE_STATE}] Questions Request`,
  GetQuestionsSuccess: `[${QUESTIONNAIRE_STATE}] Questions Success`,
  GetQuestionsFailure: `[${QUESTIONNAIRE_STATE}] Questions Failure`,
  GetAnswers: `[${QUESTIONNAIRE_STATE}] Answers Request`,
  GetAnswersSuccess: `[${QUESTIONNAIRE_STATE}] Answers Success`,
  GetAnswersFailure: `[${QUESTIONNAIRE_STATE}] Answers Failure`,
  AnswerSaveRequest: `[${QUESTIONNAIRE_STATE}] Answer Save Request`,
  AnswerValidationRequest: `[${QUESTIONNAIRE_STATE}] Answer Validation Request`,
  PostAnswer: `[${QUESTIONNAIRE_STATE}] Post Answer`,
  PostAnswerSuccess: `[${QUESTIONNAIRE_STATE}] Post Answer Success`,
  PostAnswerFailure: `[${QUESTIONNAIRE_STATE}] Post Answer Failure`,
  RepeatGroupAddRequest: `[${QUESTIONNAIRE_STATE}] Add RepeatGroup Request`,
  RepeatGroupRemoveRequest: `[${QUESTIONNAIRE_STATE}] Remove RepeatGroup Request`,
  PostRepeatGroup: `[${QUESTIONNAIRE_STATE}] Post RepeatGroup`,
  PostRepeatGroupSuccess: `[${QUESTIONNAIRE_STATE}] Post RepeatGroup Success`,
  PostRepeatGroupFailure: `[${QUESTIONNAIRE_STATE}] Post RepeatGroup Failure`,
  DeleteRepeatGroup: `[${QUESTIONNAIRE_STATE}] Delete RepeatGroup`,
  DeleteRepeatGroupSuccess: `[${QUESTIONNAIRE_STATE}] Delete RepeatGroup Success`,
  DeleteRepeatGroupFailure: `[${QUESTIONNAIRE_STATE}] Delete RepeatGroup Failure`,
  GetSectionWarning: `[${QUESTIONNAIRE_STATE}] Section Warning Request`,
  GetSectionWarningSuccess: `[${QUESTIONNAIRE_STATE}] Section Warning Success`,
  GetSectionWarningFailure: `[${QUESTIONNAIRE_STATE}] Section Warning Failure`,
  GetAnswerValidation: `[${QUESTIONNAIRE_STATE}] Answer Validation`,
  GetAnswerValidationSuccess: `[${QUESTIONNAIRE_STATE}] Answer Validation Success`,
  GetAnswerValidationFailure: `[${QUESTIONNAIRE_STATE}] Answer Validation Failure`,
  ResetAnswerValidation: `[${QUESTIONNAIRE_STATE}] Answer Validation Reset`,
};


export class GetQuestionnaireAccessState implements Action {
  readonly type = QuestionnaireActionTypes.GetQuestionnaireAccessState;

  constructor(public payload: string) {
  }
}

export class GetQuestionnaireAccessStateSuccess implements Action {
  readonly type = QuestionnaireActionTypes.GetQuestionnaireAccessStateSuccess;

  constructor(public payload: QuestionnaireAccessState) {
  }
}

export class GetQuestionnaireAccessStateFailure implements Action {
  readonly type = QuestionnaireActionTypes.GetQuestionnaireAccessStateFailure;

  constructor(public payload: any) {
  }
}


export class GetSections implements Action {
  readonly type = QuestionnaireActionTypes.GetSections;

  constructor(public payload: string) {
  }
}

export class GetSectionsSuccess implements Action {
  readonly type = QuestionnaireActionTypes.GetSectionsSuccess;

  constructor(public payload: QuestionnaireModel[]) {
  }
}

export class GetSectionsFailure implements Action {
  readonly type = QuestionnaireActionTypes.GetSectionsFailure;

  constructor(public payload: any) {
  }
}

export class GetQuestions implements Action {
  readonly type = QuestionnaireActionTypes.GetQuestions;

  constructor(public payload: QuestionnaireParam) {
  }
}

export class GetQuestionsSuccess implements Action {
  readonly type = QuestionnaireActionTypes.GetQuestionsSuccess;

  constructor(public payload: FormlyFieldConfig[]) {
  }
}

export class GetQuestionsFailure implements Action {
  readonly type = QuestionnaireActionTypes.GetQuestionsFailure;

  constructor(public payload: any) {
  }
}


export class GetAnswers implements Action {
  readonly type = QuestionnaireActionTypes.GetAnswers;

  constructor(public payload: QuestionnaireParam) {
  }
}

export class GetAnswersSuccess implements Action {
  readonly type = QuestionnaireActionTypes.GetAnswersSuccess;

  constructor(public payload: Answer) {
  }
}

export class GetAnswersFailure implements Action {
  readonly type = QuestionnaireActionTypes.GetAnswersFailure;

  constructor(public payload: any) {
  }
}

export class AnswerSaveRequest implements Action {
  readonly type = QuestionnaireActionTypes.AnswerSaveRequest;

  constructor(public payload: AnswerSaveRequestItem) {
  }
}


export class AnswerValidationRequest implements Action {
  readonly type = QuestionnaireActionTypes.AnswerValidationRequest;

  constructor(public payload: AnswerSaveRequestItem) {
  }
}


export class PostAnswer implements Action {
  readonly type = QuestionnaireActionTypes.PostAnswer;

  constructor(public payload: AnswerModel) {
  }
}

export class PostAnswerSuccess implements Action {
  readonly type = QuestionnaireActionTypes.PostAnswerSuccess;

  constructor(public payload: any) {
  }
}

export class PostAnswerFailure implements Action {
  readonly type = QuestionnaireActionTypes.PostAnswerFailure;

  constructor(public payload: any) {
  }
}

export class RepeatGroupAddRequest implements Action {
  readonly type = QuestionnaireActionTypes.RepeatGroupAddRequest;

  constructor(public payload: RepeatGroupAddRequestItem) {
  }
}

export class RepeatGroupRemoveRequest implements Action {
  readonly type = QuestionnaireActionTypes.RepeatGroupRemoveRequest;

  constructor(public payload: RepeatGroupRemoveRequestItem) {
  }
}


export class PostRepeatGroup implements Action {
  readonly type = QuestionnaireActionTypes.PostRepeatGroup;

  constructor(public payload: RepeatGroupModel) {
  }
}

export class PostRepeatGroupSuccess implements Action {
  readonly type = QuestionnaireActionTypes.PostRepeatGroupSuccess;

  constructor(public payload: string) {
  }
}

export class PostRepeatGroupFailure implements Action {
  readonly type = QuestionnaireActionTypes.PostRepeatGroupFailure;

  constructor(public payload: any) {
  }
}


export class DeleteRepeatGroup implements Action {
  readonly type = QuestionnaireActionTypes.DeleteRepeatGroup;

  constructor(public payload: RepeatGroupModel) {
  }
}

export class DeleteRepeatGroupSuccess implements Action {
  readonly type = QuestionnaireActionTypes.DeleteRepeatGroupSuccess;

  constructor(public payload: string) {
  }
}

export class DeleteRepeatGroupFailure implements Action {
  readonly type = QuestionnaireActionTypes.DeleteRepeatGroupFailure;

  constructor(public payload: any) {
  }
}

export class GetSectionWarning implements Action {
  readonly type = QuestionnaireActionTypes.GetSectionWarning;

  constructor(public payload: QuestionnaireParam) {
  }
}

export class GetSectionWarningSuccess implements Action {
  readonly type = QuestionnaireActionTypes.GetSectionWarningSuccess;

  constructor(public payload: SectionWarningModel) {
  }
}

export class GetSectionWarningFailure implements Action {
  readonly type = QuestionnaireActionTypes.GetSectionWarningFailure;

  constructor(public payload: any) {
  }
}

export class GetAnswerValidation implements Action {
  readonly type = QuestionnaireActionTypes.GetAnswerValidation;

  constructor(public payload: AnswerModel) {
  }
}

export class GetAnswerValidationSuccess implements Action {
  readonly type = QuestionnaireActionTypes.GetAnswerValidationSuccess;

  constructor(public payload: AnswerValidationModel) {
  }
}

export class GetAnswerValidationFailure implements Action {
  readonly type = QuestionnaireActionTypes.GetAnswerValidationFailure;

  constructor(public payload: any) {
  }
}

export class ResetAnswerValidation implements Action {
  readonly type = QuestionnaireActionTypes.ResetAnswerValidation;

  constructor(public payload?: any) {
  }
}



export type QuestionnaireActionsUnion =
  | GetSections
  | GetSectionsSuccess
  | GetSectionsFailure
  | GetQuestions
  | GetQuestionsSuccess
  | GetQuestionsFailure
  | GetAnswers
  | GetAnswersSuccess
  | GetAnswersFailure
  | AnswerSaveRequest
  | PostAnswer
  | PostAnswerSuccess
  | PostAnswerFailure
  | RepeatGroupAddRequest
  | RepeatGroupRemoveRequest
  | PostRepeatGroup
  | PostRepeatGroupSuccess
  | PostRepeatGroupFailure
  | DeleteRepeatGroup
  | DeleteRepeatGroupSuccess
  | DeleteRepeatGroupFailure
  | GetSectionWarning
  | GetSectionWarningSuccess
  | GetSectionWarningFailure
  | GetAnswerValidation
  | GetAnswerValidationSuccess
  | GetAnswerValidationFailure
  | ResetAnswerValidation;
