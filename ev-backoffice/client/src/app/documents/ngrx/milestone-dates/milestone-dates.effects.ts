import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';

import { FormsSheets } from '../../models/forms-sheets.model';

import {
  GetMilestoneDates,
  GetMilestoneDatesFailure,
  GetMilestoneDatesSuccess,
  MilestoneDatesActionTypes, PostMilestoneDate, PostMilestoneDateFailure, PostMilestoneDateSuccess
} from './milestone-dates.actions';
import { milestoneDatesGetRequestHandler } from '../milestones-dates-requests/milestone-dates-get/state';
import { milestoneDatePostRequestHandler } from '../milestones-dates-requests/milestone-date-post/state';
import { MilestoneDate } from '../../models/milestone-date.model';



@Injectable()
export class MilestoneDatesEffects {

  @Effect()
  getMilestoneDates$: Observable<Action> = this.actions$.pipe(
    ofType(MilestoneDatesActionTypes.GetMilestoneDates),
    map(({ payload }: GetMilestoneDates) => milestoneDatesGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getMilestoneDatesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(milestoneDatesGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<MilestoneDate[]>) => new GetMilestoneDatesSuccess(payload))
  );

  @Effect()
  getMilestoneDatesFailure$: Observable<Action> = this.actions$.pipe(
    ofType(milestoneDatesGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetMilestoneDatesFailure(payload))
  );

  @Effect()
  postMilestoneDate$: Observable<Action> = this.actions$.pipe(
    ofType(MilestoneDatesActionTypes.PostMilestoneDate),
    map(({ payload }: PostMilestoneDate) => milestoneDatePostRequestHandler.requestAction(payload))
  );

  @Effect()
  postMilestoneDateSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(milestoneDatePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<MilestoneDate>) => new PostMilestoneDateSuccess(payload))
  );

  @Effect()
  postMilestoneDateFailure$: Observable<Action> = this.actions$.pipe(
    ofType(milestoneDatePostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new PostMilestoneDateFailure(payload))
  );


  constructor(private actions$: Actions) {
  }
}
