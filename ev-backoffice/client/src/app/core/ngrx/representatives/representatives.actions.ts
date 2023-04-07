import { Action } from '@ngrx/store';

import { REPRESENTATIVES } from './representatives.state';

import { Attorney, AttorneyMenu } from '../../models/attorney.model';
import { FeeSchedule } from '../../models/fee-schedule.model';

export const RepresentativesActionTypes = {
  GetRepresentatives: `[${REPRESENTATIVES}] Get Representatives`,
  GetRepresentativesSuccess: `[${REPRESENTATIVES}] Get Representatives Success`,
  GetRepresentativesFailure: `[${REPRESENTATIVES}] Get Representatives Failure`,
  GetFeeSchedule: `[${REPRESENTATIVES}] Get Fee Schedule`,
  GetFeeScheduleSuccess: `[${REPRESENTATIVES}] Get Fee Schedule Success`,
  GetFeeScheduleFailure: `[${REPRESENTATIVES}] Get Fee Schedule Failure`,
  GetRepresentativesMenu: `[${REPRESENTATIVES}] Get Representatives Menu`,
  GetRepresentativesMenuSuccess: `[${REPRESENTATIVES}] Get Representatives Menu Success`,
  GetRepresentativesMenuFailure: `[${REPRESENTATIVES}] Get Representatives Menu Failure`,
  SetCurrentRepresentativeId: `[${REPRESENTATIVES}] Set Current Representative Id`,
  SetCurrentRepresentativeIdToLocalStorage: `[${REPRESENTATIVES}] Set Current Representative Id To Local Storage`,
  RemoveCurrentRepresentativeIdFromLocalStorage: `[${REPRESENTATIVES}] Remove Current Representative Id From Local Storage`,
  UpdateCurrentRepresentativeId: `[${REPRESENTATIVES}] Update Current Representative Id`,
  UpdateRepresentative: `[${REPRESENTATIVES}] Update Representative`,
};

export class GetFeeSchedule implements Action {
  readonly type = RepresentativesActionTypes.GetFeeSchedule;

  constructor(public payload: any) {
  }
}

export class GetFeeScheduleSuccess implements Action {
  readonly type = RepresentativesActionTypes.GetFeeScheduleSuccess;

  constructor(public payload: {response: FeeSchedule[]; id: number}) {
  }
}

export class GetFeeScheduleFailure implements Action {
  readonly type = RepresentativesActionTypes.GetFeeScheduleFailure;

  constructor(public payload?: any) {
  }
}

export class GetRepresentatives implements Action {
  readonly type = RepresentativesActionTypes.GetRepresentatives;

  constructor(public payload: string) {
  }
}

export class GetRepresentativesSuccess implements Action {
  readonly type = RepresentativesActionTypes.GetRepresentativesSuccess;

  constructor(public payload: Attorney[]) {
  }
}

export class GetRepresentativesFailure implements Action {
  readonly type = RepresentativesActionTypes.GetRepresentativesFailure;

  constructor(public payload?: any) {
  }
}

export class GetRepresentativesMenu implements Action {
  readonly type = RepresentativesActionTypes.GetRepresentativesMenu;

  constructor(public payload: string) {
  }
}

export class GetRepresentativesMenuSuccess implements Action {
  readonly type = RepresentativesActionTypes.GetRepresentativesMenuSuccess;

  constructor(public payload: AttorneyMenu[]) {
  }
}

export class GetRepresentativesMenuFailure implements Action {
  readonly type = RepresentativesActionTypes.GetRepresentativesMenuFailure;

  constructor(public payload?: any) {
  }
}

export class SetCurrentRepresentativeId implements Action {
  readonly type = RepresentativesActionTypes.SetCurrentRepresentativeId;

  constructor(public payload: any) {
  }
}

export class SetCurrentRepresentativeIdToLocalStorage implements Action {
  readonly type = RepresentativesActionTypes.SetCurrentRepresentativeIdToLocalStorage;

  constructor(public payload: any) {
  }
}

export class RemoveCurrentRepresentativeIdFromLocalStorage implements Action {
  readonly type = RepresentativesActionTypes.RemoveCurrentRepresentativeIdFromLocalStorage;

  constructor(public payload: any) {
  }
}

export class UpdateCurrentRepresentativeId implements Action {
  readonly type = RepresentativesActionTypes.UpdateCurrentRepresentativeId;

  constructor(public payload: string) {
  }
}

export class UpdateRepresentative implements Action {
  readonly type = RepresentativesActionTypes.UpdateRepresentative;

  constructor(public payload: Attorney) {
  }
}

export type RepresentativesActionsUnion =
  | GetRepresentatives
  | GetRepresentativesSuccess
  | GetRepresentativesFailure
  | GetFeeSchedule
  | GetFeeScheduleSuccess
  | GetFeeScheduleFailure
  | GetRepresentativesMenu
  | GetRepresentativesMenuSuccess
  | GetRepresentativesMenuFailure
  | SetCurrentRepresentativeId
  | SetCurrentRepresentativeIdToLocalStorage
  | RemoveCurrentRepresentativeIdFromLocalStorage
  | UpdateCurrentRepresentativeId
  | UpdateRepresentative;
