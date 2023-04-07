import { Action } from '@ngrx/store';

import { Warning } from '../../models/warning.model';

import { WARNINGS } from './warnings.state';

export const WarningsActionTypes = {
  DeleteWarnings: `[${WARNINGS}] Delete Warnings`,
  DeleteWarningsSuccess: `[${WARNINGS}] Delete Warnings Success`,
  DeleteWarningsFailure: `[${WARNINGS}] Delete Warnings Failure`,
  GetWarnings: `[${WARNINGS}] Get Warnings`,
  GetWarningsSuccess: `[${WARNINGS}] Get Warnings Success`,
  GetWarningsFailure: `[${WARNINGS}] Get Warnings Failure`,
  PutWarning: `[${WARNINGS}] Put Warning`,
  PutWarningSuccess: `[${WARNINGS}] Put Warning Success`,
  PutWarningFailure: `[${WARNINGS}] Put Warning Failure`,
  SetActiveWarning: `[${WARNINGS}] Set Active Warning`,
  ShowErrorWarningModal: `[${WARNINGS}] Show Error Warning Modal`,
};

export class DeleteWarnings implements Action {
  readonly type = WarningsActionTypes.DeleteWarnings;

  constructor(public payload) {
  }
}

export class DeleteWarningsSuccess implements Action {
  readonly type = WarningsActionTypes.DeleteWarningsSuccess;

  constructor(public payload: any) {
  }
}

export class DeleteWarningsFailure implements Action {
  readonly type = WarningsActionTypes.DeleteWarningsFailure;

  constructor(public payload?: any) {
  }
}

export class GetWarnings implements Action {
  readonly type = WarningsActionTypes.GetWarnings;

  constructor(public payload?: {sort: string; order: string}) {
  }
}

export class GetWarningsSuccess implements Action {
  readonly type = WarningsActionTypes.GetWarningsSuccess;

  constructor(public payload: {body: Warning[]; xTotalCount: string}) {
  }
}

export class GetWarningsFailure implements Action {
  readonly type = WarningsActionTypes.GetWarningsFailure;

  constructor(public payload?: any) {
  }
}

export class PutWarning implements Action {
  readonly type = WarningsActionTypes.PutWarning;

  constructor(public payload:  Warning) {
  }
}

export class PutWarningSuccess implements Action {
  readonly type = WarningsActionTypes.PutWarningSuccess;

  constructor(public payload: Warning) {
  }
}

export class PutWarningFailure implements Action {
  readonly type = WarningsActionTypes.PutWarningFailure;

  constructor(public payload?: any) {
  }
}

export class SetActiveWarning implements Action {
  readonly type = WarningsActionTypes.SetActiveWarning;

  constructor(public payload: number) {
  }
}

export class ShowErrorWarningModal implements Action {
  readonly type = WarningsActionTypes.ShowErrorWarningModal;

  constructor(public payload: any) {
  }
}

export type WarningsActionsUnion =
  | DeleteWarnings
  | DeleteWarningsSuccess
  | DeleteWarningsFailure
  | GetWarnings
  | GetWarningsSuccess
  | GetWarningsFailure
  | PutWarning
  | PutWarningSuccess
  | PutWarningFailure
  | SetActiveWarning;
