import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { NotificationsRequestService } from '../request.service';

export const taskQueueCountsGetRequestHandler = createRequestHandler('Task Queue Counts Get');

export function taskQueueCountsGetRequestReducer(state, action) {
  return taskQueueCountsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class TaskQueueCountsGetRequestEffects {

  @Effect()
  taskQueueCountsGetSelfData$: Observable<Action> = taskQueueCountsGetRequestHandler.effect(
    this.actions$,
    this.notificationsRequestService.taskQueueCountsGetRequest.bind(this.notificationsRequestService)
  );

  constructor(
    private actions$: Actions,
    private notificationsRequestService: NotificationsRequestService
  ) {
  }
}
