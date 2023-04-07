import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler, RequestFailAction, RequestSuccessAction } from '../../../../core/ngrx/utils';

import { TaskQueueModuleRequestService } from '../request.service';
import { map, tap } from 'rxjs/operators';
import { Package } from '../../../../core/models/package/package.model';
import { DeletePackagesSuccess, GetPackageSuccess } from '../../../../core/ngrx/packages/packages.actions';
import { packageGetRequestHandler } from '../package-get/state';

export const deleteLeadPackagesRequestHandler = createRequestHandler('RemoveLeadPackagesRequest');

export function leadPackagesDeleteRequestReducer(state, action) {
  return deleteLeadPackagesRequestHandler.reducer(state, action);
}

@Injectable()
export class LeadPackagesDeleteRequestEffects {

  @Effect()
  packagesData$: Observable<Action> = deleteLeadPackagesRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.deleteLeadPackagesRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect()
  deletePackagesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(deleteLeadPackagesRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<number[]>) => new DeletePackagesSuccess(payload))
  );

  @Effect({dispatch: false})
  deletePackagesFail$: Observable<Action> = this.actions$.pipe(
    ofType(deleteLeadPackagesRequestHandler.ActionTypes.REQUEST_FAIL),
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
