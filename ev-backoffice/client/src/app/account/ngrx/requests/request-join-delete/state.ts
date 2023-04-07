import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const requestJoinDeleteRequestHandler = createRequestHandler('RequestJoinDelete');

export function requestJoinDeleteRequestReducer(state, action) {
  return requestJoinDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class RequestJoinDeleteRequestEffects {

  @Effect()
  requestJoinDelete$: Observable<Action> = requestJoinDeleteRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.requestJoinDeleteRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
