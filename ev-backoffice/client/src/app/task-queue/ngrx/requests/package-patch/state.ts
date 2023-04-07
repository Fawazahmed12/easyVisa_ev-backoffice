import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';

import { createRequestHandler, RequestSuccessAction } from '../../../../core/ngrx/utils';
import {
  OpenPackagesFailModals,
  PackagesActionTypes,
  PatchPackage,
  PatchPackageSuccess
} from '../../../../core/ngrx/packages/packages.actions';
import { Package } from '../../../../core/models/package/package.model';

import { TaskQueueModuleRequestService } from '../request.service';


export const packagePatchRequestHandler = createRequestHandler('PatchPackageRequest');

export function packagePatchRequestReducer(state, action) {
  return packagePatchRequestHandler.reducer(state, action);
}

@Injectable()
export class PackagePatchRequestEffects {

  @Effect()
  packageData$: Observable<Action> = packagePatchRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.packagePatchRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect()
  patchPackage$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.PatchPackage),
    map(({payload}: PatchPackage) => packagePatchRequestHandler.requestAction(payload))
  );

  @Effect()
  patchPackageWithoutReminder$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.PatchPackageWithoutReminder),
    map(({payload}: PatchPackage) => packagePatchRequestHandler.requestAction({...payload, params: {skipReminders: true}}))
  );

  @Effect()
  updatePackageSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(
      packagePatchRequestHandler.ActionTypes.REQUEST_SUCCESS,
    ),
    mergeMap(({payload}: RequestSuccessAction<{package: Package; messages: {type: any; text: string}[]}>) => [
        new OpenPackagesFailModals(payload.messages),
        new PatchPackageSuccess(payload.package)
      ]
    )
  );

  @Effect()
  updatePackageFailure$: Observable<Action> = this.actions$.pipe(
    ofType(
      packagePatchRequestHandler.ActionTypes.REQUEST_FAIL,
    ),
    map(({payload}: RequestSuccessAction<any>) => new OpenPackagesFailModals(payload.error.errors))
  );


  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
