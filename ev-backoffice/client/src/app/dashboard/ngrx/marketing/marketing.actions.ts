import { Action } from '@ngrx/store';

import { MarketingDetails } from '../../models/marketing-details.model';

import { MARKETING } from './marketing.state';


export const MarketingActionTypes = {
  GetMarketingDetails: `[${MARKETING}] Get Marketing Details`,
  GetMarketingDetailsSuccess: `[${MARKETING}] Get Marketing Details Success`,
  GetMarketingDetailsFailure: `[${MARKETING}] Get Marketing Details Failure`,
};

export class GetMarketingDetails implements Action {
  readonly type = MarketingActionTypes.GetMarketingDetails;

  constructor(public payload?: any) {
  }
}

export class GetMarketingDetailsSuccess implements Action {
  readonly type = MarketingActionTypes.GetMarketingDetailsSuccess;

  constructor(public payload: MarketingDetails) {
  }
}

export class GetMarketingDetailsFailure implements Action {
  readonly type = MarketingActionTypes.GetMarketingDetailsFailure;

  constructor(public payload?: any) {
  }
}


export type MarketingActionsUnion =
  | GetMarketingDetails
  | GetMarketingDetailsSuccess
  | GetMarketingDetailsFailure;
