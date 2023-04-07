import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { GetPackage, GetPackageSuccess, PackagesActionTypes } from '../../../../core/ngrx/packages/packages.actions';
import { createRequestHandler, RequestFailAction, RequestSuccessAction } from '../../../../core/ngrx/utils';

import { Package } from '../../../../core/models/package/package.model';

import { TaskQueueModuleRequestService } from '../request.service';

export const packageGetRequestHandler = createRequestHandler('GetPackageRequest');

export function packageRequestReducer(state, action) {
  return packageGetRequestHandler.reducer(state, action);
}

@Injectable()
export class PackageGetRequestEffects {

  @Effect()
  packageData$: Observable<Action> = packageGetRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.packageRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect()
  getPackage$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.GetPackage),
    map(({payload}: GetPackage) => packageGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getPackageSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(packageGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Package>) => new GetPackageSuccess(payload))
  );

  @Effect({dispatch: false})
  getPackageFail$: Observable<Action> = this.actions$.pipe(
    ofType(packageGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );


  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
