import {Injectable} from '@angular/core';

import {select, Store} from '@ngrx/store';

import {filter, map, share, switchMap} from 'rxjs/operators';
import {Observable, of} from 'rxjs';

import {State} from '../ngrx/state';
import {GetBenefitCategories, GetFeeDetails, GetGovernmentFee} from '../ngrx/config-data/config-data.actions';
import {throwIfRequestFailError} from '../ngrx/utils/rxjs-utils';
import {RequestState} from '../ngrx/utils';
import {
  selectBenefitsGetRequestState,
  selectFeeDetailsGetRequestState,
  selectGovernmentFeeGetRequestState
} from '../ngrx/config-data-requests/state';
import {
  getAllBenefitGroups,
  getBenefitCategoriesNoDerivatives,
  getBenefitCategoriesWithDerivatives,
  getBenefitGroupsNoDerivatives,
  getBenefitGroupsWithDerivatives,
  getBenefits,
  getBenefitsCategories,
  getFeeDetails,
  getGovernmentFee,
  getNoPetitionerBenefitCategories,
  getNoPetitionerBenefitGroups,
  getSearchGroups
} from '../ngrx/config-data/config-data.state';

import {FeeDetails} from '../models/fee-details.model';
import {GovernmentFee} from '../models/government-fee.model';
import {BenefitCategoryModel, BenefitGroupModel, Benefits} from '../models/benefits.model';
import {CitizenshipStatus} from '../models/citizenship-status.enum';
import {BenefitCategories} from '../models/benefit-categories.enum';

@Injectable()
export class ConfigDataService {

  feeDetailsRequestState$: Observable<RequestState<FeeDetails>>;
  governmentFeeGetRequestState$: Observable<RequestState<GovernmentFee>>;
  benefitsGetRequestState$: Observable<RequestState<BenefitCategoryModel>>;
  feeDetails$: Observable<FeeDetails>;
  governmentFee$: Observable<GovernmentFee>;
  benefits$: Observable<Benefits>;
  allBenefitGroups$: Observable<BenefitGroupModel[]>;
  searchGroups$: Observable<BenefitGroupModel[]>;
  noPetitionerBenefitGroups$: Observable<BenefitGroupModel[]>;
  benefitGroupsNoDerivatives$: Observable<BenefitGroupModel[]>;
  benefitGroupsWithDerivatives$: Observable<BenefitGroupModel[]>;
  noPetitionerBenefitCategories$: Observable<BenefitCategoryModel[]>;
  allBenefitCategories$: Observable<BenefitCategoryModel[]>;
  noDerivativesBenefitCategories$: Observable<BenefitCategoryModel[]>;
  withDerivativesBenefitCategories$: Observable<BenefitCategoryModel[]>;

  constructor(
    private store: Store<State>,
  ) {
    this.feeDetailsRequestState$ = this.store.pipe(select(selectFeeDetailsGetRequestState));
    this.governmentFeeGetRequestState$ = this.store.pipe(select(selectGovernmentFeeGetRequestState));
    this.benefitsGetRequestState$ = this.store.pipe(select(selectBenefitsGetRequestState));
    this.feeDetails$ = this.store.pipe(select(getFeeDetails));
    this.governmentFee$ = this.store.pipe(select(getGovernmentFee));
    this.benefits$ = this.store.pipe(select(getBenefits));
    this.allBenefitGroups$ = this.store.pipe(select(getAllBenefitGroups));
    this.searchGroups$ = this.store.pipe(select(getSearchGroups));
    this.noPetitionerBenefitGroups$ = this.store.pipe(select(getNoPetitionerBenefitGroups));
    this.benefitGroupsNoDerivatives$ = this.store.pipe(select(getBenefitGroupsNoDerivatives));
    this.benefitGroupsWithDerivatives$ = this.store.pipe(select(getBenefitGroupsWithDerivatives));
    this.allBenefitCategories$ = this.store.pipe(select(getBenefitsCategories));
    this.noPetitionerBenefitCategories$ = this.store.pipe(select(getNoPetitionerBenefitCategories));
    this.noDerivativesBenefitCategories$ = this.store.pipe(select(getBenefitCategoriesNoDerivatives));
    this.withDerivativesBenefitCategories$ = this.store.pipe(select(getBenefitCategoriesWithDerivatives));
  }

  getFeeDetails() {
    return this.feeDetails$.pipe(
      switchMap((fees) => {
        if (fees === null) {
          this.store.dispatch(new GetFeeDetails());
          return this.feeDetailsRequestState$.pipe(
            filter((state) => !state.loading),
            throwIfRequestFailError(),
            share(),
          );
        }
        return of(fees);
      })
    );
  }

  getGovernmentFee() {
    return this.governmentFee$.pipe(
      switchMap((governmentFee) => {
        if (governmentFee === null) {
          this.store.dispatch(new GetGovernmentFee());
          return this.governmentFeeGetRequestState$.pipe(
            filter((state) => !state.loading),
            throwIfRequestFailError(),
            share(),
          );
        }
        return of(governmentFee);
      })
    );
  }

  getBenefits() {
    return this.benefits$.pipe(
      switchMap((fees) => {
        if (fees === null) {
          this.store.dispatch(new GetBenefitCategories());
          return this.benefitsGetRequestState$.pipe(
            filter((state) => !state.loading),
            throwIfRequestFailError(),
            share(),
          );
        }
        return of(fees);
      })
    );
  }

  getUpdatedNoPetitionerBenefitCategories(citizenshipStatus: CitizenshipStatus): Observable<BenefitCategoryModel[]> {
    return this.noPetitionerBenefitCategories$.pipe(
      map((benefitCategories) => {
        switch (citizenshipStatus) {
          case CitizenshipStatus.ALIEN: {
            return benefitCategories.map((benefitCategory) => {
              switch (benefitCategory.value) {
                case BenefitCategories.NATURALIZATION: {
                  return {
                    ...benefitCategory,
                    disabled: true
                  };
                }
                case BenefitCategories.SIX01:
                case BenefitCategories.SIX01A: {
                  return {
                    ...benefitCategory,
                    disabled: false
                  };
                }
                default: {
                  return benefitCategory;
                }
              }
            });
          }
          case CitizenshipStatus.LPR:
          case CitizenshipStatus.U_S_CITIZEN:
          case CitizenshipStatus.U_S_NATIONAL:{
            return benefitCategories.map((benefitCategory) => ({
                ...benefitCategory,
                disabled: true
              }));
          }
          default: {
            return benefitCategories;
          }
        }
      })
    );
  }
}
