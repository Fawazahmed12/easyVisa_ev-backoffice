import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const remindersGetRequestHandler = createRequestHandler('GetRemindersRequest');

export function remindersGetRequestReducer(state, action) {
  return remindersGetRequestHandler.reducer(state, action);
}

@Injectable()
export class RemindersGetRequestEffects {

  @Effect()
  remindersGet$: Observable<Action> = remindersGetRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.remindersGetRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
