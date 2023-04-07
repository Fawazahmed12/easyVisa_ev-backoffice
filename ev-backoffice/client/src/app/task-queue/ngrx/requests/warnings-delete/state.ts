import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { TaskQueueModuleRequestService } from '../request.service';

export const warningsDeleteRequestHandler = createRequestHandler('DeleteWarningsRequest');

export function warningsDeleteRequestReducer(state, action) {
  return warningsDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class WarningsDeleteRequestEffects {

  @Effect()
  warnings$: Observable<Action> = warningsDeleteRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.warningsDeleteRequest.bind(this.taskQueueModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
