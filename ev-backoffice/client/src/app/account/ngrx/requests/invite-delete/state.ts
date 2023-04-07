import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const inviteDeleteRequestHandler = createRequestHandler('InviteDelete');

export function inviteDeleteRequestReducer(state, action) {
  return inviteDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class InviteDeleteRequestEffects {

  @Effect()
  inviteDelete$: Observable<Action> = inviteDeleteRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.inviteDeleteRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
