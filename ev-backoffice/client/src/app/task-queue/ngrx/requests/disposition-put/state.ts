import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { TaskQueueModuleRequestService } from '../request.service';

export const dispositionPutRequestHandler = createRequestHandler('PutDispositionRequest');

export function dispositionPutRequestReducer(state, action) {
  return dispositionPutRequestHandler.reducer(state, action);
}

@Injectable()
export class DispositionPutRequestEffects {

  @Effect()
  dispositionPut$: Observable<Action> = dispositionPutRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.dispositionPutRequest.bind(this.taskQueueModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
