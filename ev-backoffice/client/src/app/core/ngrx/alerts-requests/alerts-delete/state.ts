import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { AlertsRequestService } from '../request.service';

export const alertsDeleteRequestHandler = createRequestHandler('DeleteAlertsRequest');

export function alertsDeleteRequestReducer(state, action) {
  return alertsDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class AlertsDeleteRequestEffects {

  @Effect()
  alertsData$: Observable<Action> = alertsDeleteRequestHandler.effect(
    this.actions$,
    this.alertsRequestService.alertsDeleteRequest.bind(this.alertsRequestService)
  );

  constructor(
    private actions$: Actions,
    private alertsRequestService: AlertsRequestService,
  ) {
  }
}
