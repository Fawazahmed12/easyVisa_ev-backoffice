import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const isAdminPostRequestHandler = createRequestHandler('PostIsAdminRequest');

export function isAdminPostRequestReducer(state, action) {
  return isAdminPostRequestHandler.reducer(state, action);
}

@Injectable()
export class IsAdminPostRequestEffects {

  @Effect()
  isAdmin$: Observable<Action> = isAdminPostRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.isAdminPostRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
