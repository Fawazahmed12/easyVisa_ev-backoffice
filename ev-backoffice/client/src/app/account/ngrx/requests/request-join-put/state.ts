import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const requestJoinPutRequestHandler = createRequestHandler('PutRequestJoinRequest');

export function requestJoinPutRequestReducer(state, action) {
  return requestJoinPutRequestHandler.reducer(state, action);
}

@Injectable()
export class RequestJoinPutRequestEffects {

  @Effect()
  request$: Observable<Action> = requestJoinPutRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.requestJoinPutRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
