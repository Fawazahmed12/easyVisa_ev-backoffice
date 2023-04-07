import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { AlertsRequestService } from '../request.service';

export const alertsGetRequestHandler = createRequestHandler('GetAlertsRequest');

export function alertsGetRequestReducer(state, action) {
  return alertsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class AlertsGetRequestEffects {

  @Effect()
  alerts$: Observable<Action> = alertsGetRequestHandler.effect(
    this.actions$,
    this.alertsRequestService.alertsRequest.bind(this.alertsRequestService)
  );

  constructor(
    private actions$: Actions,
    private alertsRequestService: AlertsRequestService,
  ) {
  }
}
