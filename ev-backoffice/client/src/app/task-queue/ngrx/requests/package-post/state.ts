import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { createRequestHandler, RequestFailAction, RequestSuccessAction } from '../../../../core/ngrx/utils';
import {
  OpenPackagesFailModals,
  PackagesActionTypes,
  PostPackage,
  PostPackageSuccess
} from '../../../../core/ngrx/packages/packages.actions';

import { Package } from '../../../../core/models/package/package.model';

import { TaskQueueModuleRequestService } from '../request.service';

export const packagePostRequestHandler = createRequestHandler('PostPackageRequest');

export function packagePostRequestReducer(state, action) {
  return packagePostRequestHandler.reducer(state, action);
}

@Injectable()
export class PackagePostRequestEffects {

  @Effect()
  packageData$: Observable<Action> = packagePostRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.packagePostRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect()
  postPackage$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.PostPackage),
    map(({payload}: PostPackage) => packagePostRequestHandler.requestAction(payload))
  );

  @Effect()
  postPackageSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(packagePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    switchMap(({payload}: RequestSuccessAction<{package: Package; message: {type: any; text: string}[]}>) => [
        new OpenPackagesFailModals(payload.messages),
        new PostPackageSuccess(payload.package)
      ]
    )
  );

  @Effect()
  postPackageFail$: Observable<Action> = this.actions$.pipe(
    ofType(packagePostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new OpenPackagesFailModals(payload.error.errors))
  );


  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
