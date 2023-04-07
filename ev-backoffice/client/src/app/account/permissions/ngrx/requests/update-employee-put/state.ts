import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';
import { PermissionsModuleRequestService } from '../request.service';

export const updateEmployeePutRequestHandler = createRequestHandler('PutUpdateEmployee');

export function updateEmployeePutRequestReducer(state, action) {
  return updateEmployeePutRequestHandler.reducer(state, action);
}

@Injectable()
export class UpdateEmployeePutRequestEffects {

  @Effect()
  updateEmployee$: Observable<Action> = updateEmployeePutRequestHandler.effect(
    this.actions$,
    this.permissionsModuleRequestService.updateEmployeePut.bind(this.permissionsModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private permissionsModuleRequestService: PermissionsModuleRequestService,
  ) {
  }
}
