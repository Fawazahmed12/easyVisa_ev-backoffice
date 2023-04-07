import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { TaskQueueModuleRequestService } from '../request.service';

export const retainerAgreementPostRequestHandler = createRequestHandler('PostRetainerAgreementRequest');

export function retainerAgreementPostRequestReducer(state, action) {
  return retainerAgreementPostRequestHandler.reducer(state, action);
}

@Injectable()
export class RetainerAgreementPostRequestEffects {

  @Effect()
  retainerAgreement$: Observable<Action> = retainerAgreementPostRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.retainerAgreementPostRequest.bind(this.taskQueueModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
