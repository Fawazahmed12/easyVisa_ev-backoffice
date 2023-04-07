import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';
import { PackagesRequestService } from '../request.service';


// TODO: refactor this to use existing request in task-queue
export const activePackageGetRequestHandler = createRequestHandler('Active Package Get');

export function activePackageGetRequestReducer(state, action) {
  return activePackageGetRequestHandler.reducer(state, action);
}

@Injectable()
export class ActivePackageGetRequestEffects {

  @Effect()
  representativesGet$: Observable<Action> = activePackageGetRequestHandler.effect(
    this.actions$,
    this.packagesRequestService.activePackageGetRequest.bind(this.packagesRequestService)
  );

  constructor(
    private actions$: Actions,
    private packagesRequestService: PackagesRequestService
  ) {
  }
}
