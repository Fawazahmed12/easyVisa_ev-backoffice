import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { TaskQueueModuleRequestService } from '../request.service';

export const applicantGetRequestHandler = createRequestHandler('GetApplicantRequest');

export function applicantGetRequestReducer(state, action) {
  return applicantGetRequestHandler.reducer(state, action);
}

@Injectable()
export class ApplicantGetRequestEffects {

  @Effect()
  verifyEmailData$: Observable<Action> = applicantGetRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.applicantRequest.bind(this.taskQueueModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
