import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { TaskQueueModuleRequestService } from '../request.service';

export const warningPutRequestHandler = createRequestHandler('PutWarningRequest');

export function warningPutRequestReducer(state, action) {
  return warningPutRequestHandler.reducer(state, action);
}

@Injectable()
export class WarningPutRequestEffects {

  @Effect()
  warning$: Observable<Action> = warningPutRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.warningPutRequest.bind(this.taskQueueModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
