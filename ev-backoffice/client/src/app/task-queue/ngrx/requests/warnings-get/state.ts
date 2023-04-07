import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { TaskQueueModuleRequestService } from '../request.service';

export const warningsGetRequestHandler = createRequestHandler('GetWarningsRequest');

export function warningsGetRequestReducer(state, action) {
  return warningsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class WarningsGetRequestEffects {

  @Effect()
  warnings$: Observable<Action> = warningsGetRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.warningsGetRequest.bind(this.taskQueueModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
