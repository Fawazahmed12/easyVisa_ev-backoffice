import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';
import { PermissionsModuleRequestService } from '../request.service';

export const inviteMemberPutRequestHandler = createRequestHandler('PutInviteMemberRequest');

export function inviteMemberPutRequestReducer(state, action) {
  return inviteMemberPutRequestHandler.reducer(state, action);
}

@Injectable()
export class InviteMemberPutRequestEffects {

  @Effect()
  inviteMember$: Observable<Action> = inviteMemberPutRequestHandler.effect(
    this.actions$,
    this.permissionsModuleRequestService.inviteMemberPut.bind(this.permissionsModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private permissionsModuleRequestService: PermissionsModuleRequestService,
  ) {
  }
}
