import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';
import { PermissionsModuleRequestService } from '../request.service';

export const createEmployeePostRequestHandler = createRequestHandler('PostCreateEmployee');

export function createEmployeePostRequestReducer(state, action) {
  return createEmployeePostRequestHandler.reducer(state, action);
}

@Injectable()
export class CreateEmployeePostRequestEffects {

  @Effect()
  createEmployee$: Observable<Action> = createEmployeePostRequestHandler.effect(
    this.actions$,
    this.permissionsModuleRequestService.createEmployeePost.bind(this.permissionsModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private permissionsModuleRequestService: PermissionsModuleRequestService,
  ) {
  }
}
