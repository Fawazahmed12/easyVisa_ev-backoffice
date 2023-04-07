import { createFeatureSelector, createSelector } from '@ngrx/store';
import { EstimatedTax } from '../../models/estimated-tax.model';

export const TAXES = 'Taxes';

export interface TaxesState {
  sighUpFee: EstimatedTax;
  reactivationFee: EstimatedTax;
  packageChangingStatusFee: EstimatedTax;
}

export const selectTaxesState = createFeatureSelector<TaxesState>(TAXES);

export const selectSighUpFee = ({sighUpFee}: TaxesState) => sighUpFee;
export const selectReactivationFee = ({reactivationFee}: TaxesState) => reactivationFee;
export const selectPackageChangingStatusFee = ({packageChangingStatusFee}: TaxesState) => packageChangingStatusFee;

export const getSighUpFee = createSelector(
  selectTaxesState,
  selectSighUpFee,
);

export const getReactivationFee = createSelector(
  selectTaxesState,
  selectReactivationFee,
);

export const getPackageChangingStatusFee = createSelector(
  selectTaxesState,
  selectPackageChangingStatusFee,
);
