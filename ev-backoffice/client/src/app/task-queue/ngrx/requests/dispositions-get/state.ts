import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { TaskQueueModuleRequestService } from '../request.service';

export const dispositionsGetRequestHandler = createRequestHandler('GetDispositionsRequest');

export function dispositionsGetRequestReducer(state, action) {
  return dispositionsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class DispositionsGetRequestEffects {

  @Effect()
  dispositions$: Observable<Action> = dispositionsGetRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.dispositionsGetRequest.bind(this.taskQueueModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
