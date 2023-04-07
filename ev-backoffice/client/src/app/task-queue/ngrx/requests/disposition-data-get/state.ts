import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { TaskQueueModuleRequestService } from '../request.service';

export const dispositionDataGetRequestHandler = createRequestHandler('GetDispositionDataRequest');

export function dispositionDataGetRequestReducer(state, action) {
  return dispositionDataGetRequestHandler.reducer(state, action);
}

@Injectable()
export class DispositionDataGetRequestEffects {

  @Effect()
  disposition$: Observable<Action> = dispositionDataGetRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.dispositionDataGetRequest.bind(this.taskQueueModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
