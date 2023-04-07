import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';
import { PermissionsModuleRequestService } from '../request.service';


export const permissionGetRequestHandler = createRequestHandler('GetPermissionRequest');

export function permissionGetRequestReducer(state, action) {
  return permissionGetRequestHandler.reducer(state, action);
}

@Injectable()
export class PermissionGetRequestEffects {

  @Effect()
  permission$: Observable<Action> = permissionGetRequestHandler.effect(
    this.actions$,
    this.permissionsModuleRequestService.permissionGetRequest.bind(this.permissionsModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private permissionsModuleRequestService: PermissionsModuleRequestService,
  ) {
  }
}
