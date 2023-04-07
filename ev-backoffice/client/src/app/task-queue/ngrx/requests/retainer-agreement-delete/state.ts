import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { TaskQueueModuleRequestService } from '../request.service';

export const retainerAgreementDeleteRequestHandler = createRequestHandler('DeleteRetainerAgreementRequest');

export function retainerAgreementDeleteRequestReducer(state, action) {
  return retainerAgreementDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class RetainerAgreementDeleteRequestEffects {

  @Effect()
  retainerAgreementRemove$: Observable<Action> = retainerAgreementDeleteRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.retainerAgreementDeleteRequest.bind(this.taskQueueModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
