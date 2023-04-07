import { Action } from '@ngrx/store';

import { EstimatedTax } from '../../models/estimated-tax.model';
import { TaxTypes } from '../../models/tax-types.enum';

import { TAXES } from './taxes.state';
import { TaxAddress } from '../../models/tax-address.model';


export const TaxesActionTypes = {
  PostEstimatedTax: `[${TAXES}] Post Estimated Tax`,
  PostEstimatedTaxSuccess: `[${TAXES}] Post Estimated Tax Success`,
  PostEstimatedTaxFailure: `[${TAXES}] Post Estimated Tax Failure`,
};


export class PostEstimatedTax implements Action {
  readonly type = TaxesActionTypes.PostEstimatedTax;

  constructor(public payload: {type: TaxTypes; address?: TaxAddress; packageId?: number}) {}
}

export class PostEstimatedTaxSuccess implements Action {
  readonly type = TaxesActionTypes.PostEstimatedTaxSuccess;

  constructor(public payload: {estimatesTax: EstimatedTax; taxType: TaxTypes}) {}
}

export class PostEstimatedTaxFailure implements Action {
  readonly type = TaxesActionTypes.PostEstimatedTaxFailure;

  constructor(public payload: any) {}
}


export type TaxesActionUnion =
  | PostEstimatedTax
  | PostEstimatedTaxSuccess
  | PostEstimatedTaxFailure;
