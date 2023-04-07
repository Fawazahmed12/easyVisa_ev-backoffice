import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';
import { PermissionsModuleRequestService } from '../request.service';

export const verifyMemberPostRequestHandler = createRequestHandler('PostVerifyMemberRequest');

export function verifyMemberPostRequestReducer(state, action) {
  return verifyMemberPostRequestHandler.reducer(state, action);
}

@Injectable()
export class VerifyMemberPostRequestEffects {

  @Effect()
  verifyMember$: Observable<Action> = verifyMemberPostRequestHandler.effect(
    this.actions$,
    this.permissionsModuleRequestService.verifyMemberPost.bind(this.permissionsModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private permissionsModuleRequestService: PermissionsModuleRequestService,
  ) {
  }
}
