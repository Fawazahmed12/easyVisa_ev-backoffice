import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { AlertsRequestService } from '../request.service';

export const alertPutRequestHandler = createRequestHandler('PutAlertRequest');

export function alertPutRequestReducer(state, action) {
  return alertPutRequestHandler.reducer(state, action);
}

@Injectable()
export class AlertPutRequestEffects {

  @Effect()
  alert$: Observable<Action> = alertPutRequestHandler.effect(
    this.actions$,
    this.alertsRequestService.alertPutRequest.bind(this.alertsRequestService)
  );

  constructor(
    private actions$: Actions,
    private alertsRequestService: AlertsRequestService,
  ) {
  }
}
