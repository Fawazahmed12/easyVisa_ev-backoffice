import { Injectable } from '@angular/core';

import { Action, select, Store } from '@ngrx/store';
import { filter, share } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';
import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';

import { getMilestoneDatePostRequestState, getMilestoneDates, getMilestoneDatesGetRequestState, } from '../ngrx/state';
import { GetMilestoneDates, PostMilestoneDate } from '../ngrx/milestone-dates/milestone-dates.actions';
import { MilestoneDate } from '../models/milestone-date.model';
import { MilestoneDatesEffects } from '../ngrx/milestone-dates/milestone-dates.effects';
import { HttpErrorResponse } from '@angular/common/http';
import { ModalService } from '../../core/services';

@Injectable()
export class MilestoneDatesService {
  getMilestoneDatesRequestState$: Observable<RequestState<MilestoneDate[]>>;
  postMilestoneDateRequestState$: Observable<RequestState<MilestoneDate>>;
  milestoneDates$: Observable<any>;
  postMilestoneDateFailAction$: Observable<Action>;

  constructor(
    private store: Store<State>,
    private milestoneDatesEffects: MilestoneDatesEffects,
    private modalService: ModalService
  ) {
    this.getMilestoneDatesRequestState$ = this.store.pipe(select(getMilestoneDatesGetRequestState));
    this.postMilestoneDateRequestState$ = this.store.pipe(select(getMilestoneDatePostRequestState));
    this.milestoneDates$ = this.store.pipe(select(getMilestoneDates));
    this.postMilestoneDateFailAction$ = this.milestoneDatesEffects.postMilestoneDateFailure$;
  }

  getMilestoneDates(data) {
    this.store.dispatch(new GetMilestoneDates(data));
    return this.getMilestoneDatesRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  postMilestoneDate(data) {
    this.store.dispatch(new PostMilestoneDate(data));
    return this.postMilestoneDateRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  documentAccessErrorFilter(action) {
    const ACCESS_ERROR_TYPE = 'INVALID_DOCUMENTPORTAL_ACCESS';
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    const errors = payload.error.errors || [ payload.error ];
    const accessError = errors[ 0 ] || { type: '' };
    if (accessError.type !== ACCESS_ERROR_TYPE) {
      return false;
    }
    const errorMessages = accessError.message.split('|');
    const errorMessage = errorMessages[ 0 ];
    const sourceFieldId = errorMessages[ 1 ];
    accessError.message = errorMessage;
    return false;
  }

  documentAccessErrorHandler(action) {
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    this.modalService.showErrorModal(payload.error.errors || [ payload.error ]);
  }

}
