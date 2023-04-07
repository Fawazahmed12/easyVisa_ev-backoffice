import { createFeatureSelector, createSelector } from '@ngrx/store';


import { RequestState } from '../utils';
import { UscisEditionDatesModel } from '../../models/uscis-edition-dates.model';


export const USCIS_EDITION_DATES_REQUEST = 'UscisEditionDatesRequest';

export interface UscisEditionDatesRequestState {
  uscisEditionDatesGet?: RequestState<UscisEditionDatesModel[]>;
  uscisEditionDatesPut?: RequestState<UscisEditionDatesModel[]>;
}

export const selectUscisEditionDatesRequestState = createFeatureSelector<UscisEditionDatesRequestState>(USCIS_EDITION_DATES_REQUEST);


export const selectUscisEditionDatesGetRequestState = createSelector(
  selectUscisEditionDatesRequestState,
  (state: UscisEditionDatesRequestState) => state.uscisEditionDatesGet
);

export const selectUscisEditionDatesPutRequestState = createSelector(
  selectUscisEditionDatesRequestState,
  (state: UscisEditionDatesRequestState) => state.uscisEditionDatesPut
);


export { uscisEditionDatesGetRequestHandler } from './uscis-edition-dates-get/state';
export { uscisEditionDatesPutRequestHandler } from './uscis-edition-dates-put/state';
