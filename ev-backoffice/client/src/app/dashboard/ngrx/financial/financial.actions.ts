import { Action } from '@ngrx/store';

import { FinancialDetails } from '../../models/financial-details.model';

import { FINANCIAL } from './financial.state';


export const FinancialActionTypes = {
  GetFinancialDetails: `[${FINANCIAL}] Get Financial Details`,
  GetFinancialDetailsSuccess: `[${FINANCIAL}] Get Financial Details Success`,
  GetFinancialDetailsFailure: `[${FINANCIAL}] Get Financial Details Failure`,
  PostInviteColleagues: `[${FINANCIAL}] Post Invite Colleagues`,
  PostInviteColleaguesSuccess: `[${FINANCIAL}] Post Invite Colleagues Success`,
  PostInviteColleaguesFailure: `[${FINANCIAL}] Post Invite Colleagues Failure`
};

export class GetFinancialDetails implements Action {
  readonly type = FinancialActionTypes.GetFinancialDetails;

  constructor(public payload?: any) {
  }
}

export class GetFinancialDetailsSuccess implements Action {
  readonly type = FinancialActionTypes.GetFinancialDetailsSuccess;

  constructor(public payload: FinancialDetails) {
  }
}

export class GetFinancialDetailsFailure implements Action {
  readonly type = FinancialActionTypes.GetFinancialDetailsFailure;

  constructor(public payload?: any) {
  }
}

export class PostInviteColleagues implements Action {
  readonly type = FinancialActionTypes.PostInviteColleagues;

  constructor(public payload) {
  }
}

export class PostInviteColleaguesSuccess implements Action {
  readonly type = FinancialActionTypes.PostInviteColleaguesSuccess;

  constructor(public payload) {
  }
}

export class PostInviteColleaguesFailure implements Action {
  readonly type = FinancialActionTypes.PostInviteColleaguesFailure;

  constructor(public payload) {
  }
}


export type FinancialActionsUnion =
  | GetFinancialDetails
  | GetFinancialDetailsSuccess
  | GetFinancialDetailsFailure;
