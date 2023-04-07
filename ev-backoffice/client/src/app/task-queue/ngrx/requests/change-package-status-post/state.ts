import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';

import { createRequestHandler, RequestFailAction, RequestSuccessAction } from '../../../../core/ngrx/utils';
import { Package } from '../../../../core/models/package/package.model';
import {
  ChangePackageStatus, OpenPackagesFailModals,
  PackagesActionTypes,
  PatchPackageSuccess
} from '../../../../core/ngrx/packages/packages.actions';

import { TaskQueueModuleRequestService } from '../request.service';

export const changePackageStatusPostRequestHandler = createRequestHandler('PostChangePackageStatusRequest');

export function changePackageStatusPostRequestReducer(state, action) {
  return changePackageStatusPostRequestHandler.reducer(state, action);
}

@Injectable()
export class ChangePackageStatusPostRequestEffects {

  @Effect()
  changeStatus$: Observable<Action> = changePackageStatusPostRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.changePackageStatusPostRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect()
  changePackageStatus$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.ChangePackageStatus),
    map(({payload}: ChangePackageStatus) => changePackageStatusPostRequestHandler.requestAction(payload))
  );

  @Effect()
  updatePackageSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(
      changePackageStatusPostRequestHandler.ActionTypes.REQUEST_SUCCESS,
    ),
    mergeMap(({payload}: RequestSuccessAction<{package: Package; messages: {type: any; text: string}[]}>) => [
      new PatchPackageSuccess(payload.package),
      new OpenPackagesFailModals(payload.messages)
    ])
  );

  @Effect()
  updatePackageFail$: Observable<Action> = this.actions$.pipe(
    ofType(
      changePackageStatusPostRequestHandler.ActionTypes.REQUEST_FAIL,
    ),
     map(({payload}: RequestFailAction<any>) => new OpenPackagesFailModals(payload.error.errors))
  );


  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
