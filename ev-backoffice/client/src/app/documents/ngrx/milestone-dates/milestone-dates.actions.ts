import { Action } from '@ngrx/store';


import { MILESTONE_DATES } from './milestone-dates.state';
import { MilestoneDate } from '../../models/milestone-date.model';


export const MilestoneDatesActionTypes = {
  GetMilestoneDates: `[${MILESTONE_DATES}] Get Milestone Dates`,
  GetMilestoneDatesSuccess: `[${MILESTONE_DATES}] Get Milestone Dates Success`,
  GetMilestoneDatesFailure: `[${MILESTONE_DATES}] Get Milestone Dates Failure`,
  PostMilestoneDate: `[${MILESTONE_DATES}] Post Milestone Date`,
  PostMilestoneDateSuccess: `[${MILESTONE_DATES}] Post Milestone Date Success`,
  PostMilestoneDateFailure: `[${MILESTONE_DATES}] Post Milestone Date Failure`,
};


export class GetMilestoneDates implements Action {
  readonly type = MilestoneDatesActionTypes.GetMilestoneDates;

  constructor(public payload: number) {
  }
}

export class GetMilestoneDatesSuccess implements Action {
  readonly type = MilestoneDatesActionTypes.GetMilestoneDatesSuccess;

  constructor(public payload: MilestoneDate[]) {
  }
}

export class GetMilestoneDatesFailure implements Action {
  readonly type = MilestoneDatesActionTypes.GetMilestoneDatesFailure;

  constructor(public payload: any) {
  }
}

export class PostMilestoneDate implements Action {
  readonly type = MilestoneDatesActionTypes.PostMilestoneDate;

  constructor(public payload: MilestoneDate) {
  }
}

export class PostMilestoneDateSuccess implements Action {
  readonly type = MilestoneDatesActionTypes.PostMilestoneDateSuccess;

  constructor(public payload: MilestoneDate) {
  }
}

export class PostMilestoneDateFailure implements Action {
  readonly type = MilestoneDatesActionTypes.PostMilestoneDateFailure;

  constructor(public payload: any) {
  }
}


export type MilestoneDatesActionsUnion =
  | GetMilestoneDates
  | GetMilestoneDatesSuccess
  | GetMilestoneDatesFailure
  | PostMilestoneDate
  | PostMilestoneDateSuccess
  | PostMilestoneDateFailure;
