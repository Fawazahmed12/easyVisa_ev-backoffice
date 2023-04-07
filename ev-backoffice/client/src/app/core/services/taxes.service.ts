import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { filter, share } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { State } from '../ngrx/state';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import { RequestState } from '../ngrx/utils';
import { PostEstimatedTax } from '../ngrx/taxes/taxes.actions';
import { getPackageChangingStatusFee, getReactivationFee, getSighUpFee } from '../ngrx/taxes/taxes.state';
import { selectEstimatedTaxPostRequestState } from '../ngrx/taxes-requests/state';

import { PackageApplicant } from '../models/package/package-applicant.model';
import { TaxTypes } from '../models/tax-types.enum';
import { EstimatedTax } from '../models/estimated-tax.model';
import { TaxAddress } from '../models/tax-address.model';

@Injectable()
export class TaxesService {

  sighUpFeeWithTax$: Observable<EstimatedTax>;
  reactivationFeeWithTax$: Observable<EstimatedTax>;
  packageChangingStatusFeeWithTax$: Observable<EstimatedTax>;
  postFeeWithEstimatedTaxRequestState$: Observable<RequestState<EstimatedTax>>;

  constructor(
    private store: Store<State>,
  ) {
    this.sighUpFeeWithTax$ = this.store.pipe(select(getSighUpFee));
    this.reactivationFeeWithTax$ = this.store.pipe(select(getReactivationFee));
    this.packageChangingStatusFeeWithTax$ = this.store.pipe(select(getPackageChangingStatusFee));
    this.postFeeWithEstimatedTaxRequestState$ = this.store.pipe(select(selectEstimatedTaxPostRequestState));
  }

  postEstimatedTax(data: {
    type: TaxTypes;
    address?: TaxAddress;
    packageId?: number;
    packageObj?: {
      applicants: PackageApplicant[];
    };
  }) {
    this.store.dispatch(new PostEstimatedTax(data));
    return this.postFeeWithEstimatedTaxRequestState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }
}
