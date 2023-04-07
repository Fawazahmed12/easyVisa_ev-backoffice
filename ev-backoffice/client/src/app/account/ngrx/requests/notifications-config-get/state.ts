import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const notificationsConfigGetRequestHandler = createRequestHandler('GetNotificationsConfig');

export function notificationsConfigGetRequestReducer(state, action) {
  return notificationsConfigGetRequestHandler.reducer(state, action);
}

@Injectable()
export class NotificationsConfigGetRequestEffects {

  @Effect()
  notificationsConfig$: Observable<Action> = notificationsConfigGetRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.notificationsConfigGetRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
