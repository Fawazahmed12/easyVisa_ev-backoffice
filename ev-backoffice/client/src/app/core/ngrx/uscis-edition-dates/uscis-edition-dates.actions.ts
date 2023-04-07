import { Action } from '@ngrx/store';

import { USCIS_EDITION_DATES } from './uscis-edition-dates.state';
import { UscisEditionDatesModel } from '../../models/uscis-edition-dates.model';

export const UscisEditionDatesActionTypes = {
  GetUscisEditionDates: `[${USCIS_EDITION_DATES}] Get Uscis Edition Dates`,
  GetUscisEditionDatesSuccess: `[${USCIS_EDITION_DATES}] Get Uscis Edition Dates Success`,
  GetUscisEditionDatesFailure: `[${USCIS_EDITION_DATES}] Get Uscis Edition Dates Failure`,
  PutUscisEditionDates: `[${USCIS_EDITION_DATES}] Put Uscis Edition Dates`,
  PutUscisEditionDatesSuccess: `[${USCIS_EDITION_DATES}] Put Uscis Edition Dates Success`,
  PutUscisEditionDatesFailure: `[${USCIS_EDITION_DATES}] Put Uscis Edition Dates Failure`
};


export class GetUscisEditionDates implements Action {
  readonly type = UscisEditionDatesActionTypes.GetUscisEditionDates;

  constructor(public payload?: {sort: string; order: string}) {
  }
}

export class GetUscisEditionDatesSuccess implements Action {
  readonly type = UscisEditionDatesActionTypes.GetUscisEditionDatesSuccess;

  constructor(public payload: UscisEditionDatesModel[]) {
  }
}

export class GetUscisEditionDatesFailure implements Action {
  readonly type = UscisEditionDatesActionTypes.GetUscisEditionDatesFailure;

  constructor(public payload?: any) {
  }
}

export class PutUscisEditionDates implements Action {
  readonly type = UscisEditionDatesActionTypes.PutUscisEditionDates;

  constructor(public payload: UscisEditionDatesModel[]) {
  }
}

export class PutUscisEditionDatesSuccess implements Action {
  readonly type = UscisEditionDatesActionTypes.PutUscisEditionDatesSuccess;

  constructor(public payload: UscisEditionDatesModel[]) {
  }
}

export class PutUscisEditionDatesFailure implements Action {
  readonly type = UscisEditionDatesActionTypes.PutUscisEditionDatesFailure;

  constructor(public payload?: any) {
  }
}

export type UscisEditionDatesActionsUnion =
  | GetUscisEditionDates
  | GetUscisEditionDatesSuccess
  | GetUscisEditionDatesFailure
  | PutUscisEditionDates
  | PutUscisEditionDatesSuccess
  | PutUscisEditionDatesFailure;
