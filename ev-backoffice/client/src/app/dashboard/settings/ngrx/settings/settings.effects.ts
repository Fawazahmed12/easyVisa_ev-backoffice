import { Injectable } from '@angular/core';

import { EMPTY, Observable } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../../core/ngrx/utils';
import { ModalService } from '../../../../core/services';

import { MarketingDetails } from '../../../models/marketing-details.model';

import { rankingDataGetRequestHandler } from '../requests/ranking-data-get/state';

import {
  GetRankingData,
  GetRankingDataFailure,
  GetRankingDataSuccess, GetRepresentativesCountFailure, GetRepresentativesCountSuccess,
  PutRankingDataFailure, PutRankingDataSuccess,
  SettingsActionTypes,
  GetBatchJobsConfig, GetBatchJobsConfigSuccess, GetBatchJobsConfigFailure,
  PatchBatchJobsConfig, PatchBatchJobsConfigSuccess, PatchBatchJobsConfigFailure,
} from './settings.actions';
import { rankingDataPutRequestHandler } from '../requests/ranking-data-put/state';
import { representativesCountGetRequestHandler } from '../requests/representatives-count-get/state';
import { batchJobsGetRequestHandler } from '../requests/batch-jobs-get/state';
import { batchJobsPatchRequestHandler } from '../requests/batch-jobs-patch/state';
import { RepresentativesCount } from '../../../models/representatives-count.model';
import { Job } from '../../../models/site-jobs';


@Injectable()
export class SettingsEffects {

  @Effect()
  getRankingData$: Observable<Action> = this.actions$.pipe(
    ofType(SettingsActionTypes.GetRankingData),
    map(({payload}: GetRankingData) => rankingDataGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getRankingDataSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(rankingDataGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<MarketingDetails>) => new GetRankingDataSuccess(payload))
  );

  @Effect({dispatch: false})
  getRankingDataFail$: Observable<Action> = this.actions$.pipe(
    ofType(rankingDataGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  putRankingData$: Observable<Action> = this.actions$.pipe(
    ofType(SettingsActionTypes.PutRankingData),
    map(({payload}: GetRankingData) => rankingDataPutRequestHandler.requestAction(payload))
  );

  @Effect()
  putRankingDataSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(rankingDataPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<MarketingDetails>) => new PutRankingDataSuccess(payload))
  );

  @Effect()
  putRankingDataFail$: Observable<Action> = this.actions$.pipe(
    ofType(rankingDataPutRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PutRankingDataFailure(payload))
  );

  @Effect()
  getRepresentativesCount$: Observable<Action> = this.actions$.pipe(
    ofType(SettingsActionTypes.GetRepresentativesCount),
    map(({payload}: GetRankingData) => representativesCountGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getRepresentativesCountSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(representativesCountGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<RepresentativesCount>) => new GetRepresentativesCountSuccess(payload))
  );

  @Effect({dispatch: false})
  getRepresentativesCountFail$: Observable<Action> = this.actions$.pipe(
    ofType(representativesCountGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect({dispatch: false})
  openFailModal$: Observable<Action> = this.actions$.pipe(
    ofType(
      SettingsActionTypes.PutRankingDataFailure,
      SettingsActionTypes.PatchBatchJobsConfig,
    ),
    switchMap(({payload}: RequestFailAction<any>) =>
      this.modalService.showErrorModal(payload.error.errors || [payload.error] || payload.message).pipe(
        catchError(() => EMPTY))
    )
  );

  @Effect()
  batchJobsGet$: Observable<Action> = this.actions$.pipe(
    ofType(SettingsActionTypes.GetBatchJobsConfig),
    map(({payload}: GetBatchJobsConfig) => batchJobsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  batchJobsGetSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(batchJobsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Job>) => new GetBatchJobsConfigSuccess(payload))
  );

  @Effect()
  batchJobsGetFail$: Observable<Action> = this.actions$.pipe(
    ofType(batchJobsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new GetBatchJobsConfigFailure(payload))
  );

  @Effect()
  batchJobsPatch$: Observable<Action> = this.actions$.pipe(
    ofType(SettingsActionTypes.PatchBatchJobsConfig),
    map(({payload}: PatchBatchJobsConfig) => batchJobsPatchRequestHandler.requestAction(payload))
  );

  @Effect()
  batchJobsPatchSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(batchJobsPatchRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Job>) => new PatchBatchJobsConfigSuccess(payload))
  );

  @Effect()
  batchJobsPatchFail$: Observable<Action> = this.actions$.pipe(
    ofType(batchJobsPatchRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PatchBatchJobsConfigFailure(payload))
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
