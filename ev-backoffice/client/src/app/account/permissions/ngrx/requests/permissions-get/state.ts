import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';
import { PermissionsModuleRequestService } from '../request.service';


export const permissionsGetRequestHandler = createRequestHandler('GetPermissionsRequest');

export function permissionsGetRequestReducer(state, action) {
  return permissionsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class PermissionsGetRequestEffects {

  @Effect()
  permissions$: Observable<Action> = permissionsGetRequestHandler.effect(
    this.actions$,
    this.permissionsModuleRequestService.permissionsGetRequest.bind(this.permissionsModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private permissionsModuleRequestService: PermissionsModuleRequestService,
  ) {
  }
}
