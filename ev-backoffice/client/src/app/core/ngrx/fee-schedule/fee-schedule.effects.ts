import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { ModalService } from '../../services';

import { RequestFailAction, RequestSuccessAction } from '../utils';
import {
  FeeScheduleDataActionTypes,
  GetFeeScheduleSettingsFailure,
  GetFeeScheduleSettingsSuccess,
  OpenFeeScheduleFailModal,
  PostFeeScheduleSettings,
  PostFeeScheduleSettingsFailure,
  PostFeeScheduleSettingsSuccess
} from './fee-schedule.actions';
import { feeScheduleSettingsGetRequestHandler } from '../fee-schedule-requests/fee-schedule-settings-get/state';

import { feeScheduleSettingsPostRequestHandler } from '../fee-schedule-requests/fee-schedule-settings-post/state';
import { FeeSchedule } from '../../models/fee-schedule.model';

@Injectable()
export class FeeScheduleEffects {

  @Effect()
  getFeeScheduleSettings$: Observable<Action> = this.actions$.pipe(
    ofType(FeeScheduleDataActionTypes.GetFeeScheduleSettings),
    map(() => feeScheduleSettingsGetRequestHandler.requestAction())
  );

  @Effect()
  getFeeScheduleSettingsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(feeScheduleSettingsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<FeeSchedule[]>) => new GetFeeScheduleSettingsSuccess(payload))
  );

  @Effect()
  getFeeScheduleSettingsFail$: Observable<Action> = this.actions$.pipe(
    ofType(feeScheduleSettingsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new GetFeeScheduleSettingsFailure(payload))
  );

  @Effect()
  postFeeScheduleSettings$: Observable<Action> = this.actions$.pipe(
    ofType(FeeScheduleDataActionTypes.PostFeeScheduleSettings),
    map(({payload}: PostFeeScheduleSettings) => feeScheduleSettingsPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postFeeScheduleSettingsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(feeScheduleSettingsPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<FeeSchedule[]>) => new PostFeeScheduleSettingsSuccess(payload))
  );

  @Effect()
  postFeeScheduleSettingsFail$: Observable<Action> = this.actions$.pipe(
    ofType(feeScheduleSettingsPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PostFeeScheduleSettingsFailure(payload))
  );

  @Effect()
  requestsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(
      FeeScheduleDataActionTypes.GetFeeScheduleSettingsFailure,
      FeeScheduleDataActionTypes.PostFeeScheduleSettingsFailure,
    ),
    map(({payload}: RequestFailAction<any>) => new OpenFeeScheduleFailModal(payload))
  );

  @Effect({dispatch: false})
  openFeeScheduleFailModal$: Observable<Action> = this.actions$.pipe(
    ofType(FeeScheduleDataActionTypes.OpenFeeScheduleFailModal),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || [payload.error]))
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
