import {Injectable} from '@angular/core';
import {Action,} from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';
import {Observable} from 'rxjs';
import {createRequestHandler} from '../../../../core/ngrx/utils';
import {TaskQueueModuleRequestService} from '../request.service';
import { ChangePackageStatus, PackagesActionTypes } from '../../../../core/ngrx/packages/packages.actions';
import { map } from 'rxjs/operators';
import { changePackageStatusPostRequestHandler } from '../change-package-status-post/state';
export const changePackageOwedPatchRequestHandler = createRequestHandler('PutChangePackageOwedRequest');
export function changePackageOwedPatchRequestReducer(state, action) {
  return changePackageOwedPatchRequestHandler.reducer(state, action);
}

@Injectable()
export class ChangePackageOwedPatchRequestEffects {


  @Effect()
  changeOwed$: Observable<Action> = changePackageOwedPatchRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.changePackageOwedPatchRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect()
  changePackageOwed$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.ChangePackageOwed),
    map(({payload}: ChangePackageStatus) => changePackageOwedPatchRequestHandler.requestAction(payload))
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
