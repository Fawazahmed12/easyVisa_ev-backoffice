import { Action } from '@ngrx/store';

import { ProgressStatus } from '../../models/progress-status.model';

import { PROGRESS_STATUSES } from './progress-statuses.state';


export const ProgressStatusesActionTypes = {
  GetQuestionnaireProgress: `[${PROGRESS_STATUSES}] Get Questionnaire Progress`,
  GetQuestionnaireProgressSuccess: `[${PROGRESS_STATUSES}] Get Questionnaire Progress Success`,
  GetQuestionnaireProgressFailure: `[${PROGRESS_STATUSES}] Get Questionnaire Progress Failure`,
  GetDocumentProgress: `[${PROGRESS_STATUSES}] Get Document Progress`,
  GetDocumentProgressSuccess: `[${PROGRESS_STATUSES}] Get Document Progress Success`,
  GetDocumentProgressFailure: `[${PROGRESS_STATUSES}] Get Document Progress Failure`,
};

export class GetQuestionnaireProgress implements Action {
  readonly type = ProgressStatusesActionTypes.GetQuestionnaireProgress;

  constructor(public payload: number) {
  }
}

export class GetQuestionnaireProgressSuccess implements Action {
  readonly type = ProgressStatusesActionTypes.GetQuestionnaireProgressSuccess;

  constructor(public payload: ProgressStatus[]) {
  }
}

export class GetQuestionnaireProgressFailure implements Action {
  readonly type = ProgressStatusesActionTypes.GetQuestionnaireProgressFailure;

  constructor(public payload?: any) {
  }
}

export class GetDocumentProgress implements Action {
  readonly type = ProgressStatusesActionTypes.GetDocumentProgress;

  constructor(public payload: number) {
  }
}

export class GetDocumentProgressSuccess implements Action {
  readonly type = ProgressStatusesActionTypes.GetDocumentProgressSuccess;

  constructor(public payload: ProgressStatus[]) {
  }
}

export class GetDocumentProgressFailure implements Action {
  readonly type = ProgressStatusesActionTypes.GetDocumentProgressFailure;

  constructor(public payload?: any) {
  }
}

export type ProgressStatusesActionsUnion =
  | GetQuestionnaireProgress
  | GetQuestionnaireProgressSuccess
  | GetQuestionnaireProgressFailure
  | GetDocumentProgress
  | GetDocumentProgressSuccess
  | GetDocumentProgressFailure;
