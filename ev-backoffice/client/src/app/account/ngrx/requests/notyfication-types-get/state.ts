import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const notificationTypesGetRequestHandler = createRequestHandler('GetNotificationTypesRequest');

export function notificationTypesGetRequestReducer(state, action) {
  return notificationTypesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class NotificationTypesGetRequestEffects {

  @Effect()
  notificationTypes$: Observable<Action> = notificationTypesGetRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.notificationTypesGetRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
