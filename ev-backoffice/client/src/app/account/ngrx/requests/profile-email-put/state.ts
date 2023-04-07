import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const profileEmailPutRequestHandler = createRequestHandler('PutProfileEmailRequest');

export function profileEmailPutRequestReducer(state, action) {
  return profileEmailPutRequestHandler.reducer(state, action);
}

@Injectable()
export class ProfileEmailPutRequestEffects {

  @Effect()
  profileEmail$: Observable<Action> = profileEmailPutRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.profileEmailPutRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
