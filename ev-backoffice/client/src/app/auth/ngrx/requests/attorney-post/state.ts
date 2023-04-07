import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { AuthModuleRequestService } from '../request.service';

export const attorneyPostRequestHandler = createRequestHandler('AttorneyPostRequest');

export function attorneyPostRequestReducer(state, action) {
  return attorneyPostRequestHandler.reducer(state, action);
}

@Injectable()
export class AttorneyPostRequestEffects {

  @Effect()
  referringUserData$: Observable<Action> = attorneyPostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.attorneyPostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
