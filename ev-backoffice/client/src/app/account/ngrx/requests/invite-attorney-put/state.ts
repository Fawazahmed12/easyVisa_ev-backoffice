import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const inviteAttorneyPutRequestHandler = createRequestHandler('PutInviteAttorneyRequest');

export function inviteAttorneyPutRequestReducer(state, action) {
  return inviteAttorneyPutRequestHandler.reducer(state, action);
}

@Injectable()
export class InviteAttorneyPutRequestEffects {

  @Effect()
  invite$: Observable<Action> = inviteAttorneyPutRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.inviteAttorneyPutRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
