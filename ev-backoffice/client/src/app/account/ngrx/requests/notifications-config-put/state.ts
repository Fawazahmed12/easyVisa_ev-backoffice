import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const notificationsConfigPutRequestHandler = createRequestHandler('PutNotificationsConfig');

export function notificationsConfigPutRequestReducer(state, action) {
  return notificationsConfigPutRequestHandler.reducer(state, action);
}

@Injectable()
export class NotificationsConfigPutRequestEffects {

  @Effect()
  ratings$: Observable<Action> = notificationsConfigPutRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.notificationsConfigPutRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
