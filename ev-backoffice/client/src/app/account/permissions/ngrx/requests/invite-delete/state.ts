import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';
import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';

import { PermissionsModuleRequestService } from '../request.service';

export const inviteDeleteRequestHandler = createRequestHandler('InviteDeletePermissions');

export function inviteDeleteRequestReducer(state, action) {
  return inviteDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class InviteDeleteRequestEffects {

  @Effect()
  inviteDeletePermissions$: Observable<Action> = inviteDeleteRequestHandler.effect(
    this.actions$,
    this.permissionsModuleRequestService.inviteDeleteRequest.bind(this.permissionsModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private permissionsModuleRequestService: PermissionsModuleRequestService,
  ) {
  }
}
