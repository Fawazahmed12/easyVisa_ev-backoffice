import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AuthModuleRequestService } from '../request.service';

export const showUsernamePostRequestHandler = createRequestHandler('ShowUsernamePostRequest');

export function showUsernamePostRequestReducer(state, action) {
  return showUsernamePostRequestHandler.reducer(state, action);
}

@Injectable()
export class ShowUsernamePostRequestEffects {

  @Effect()
  showUsername$: Observable<Action> = showUsernamePostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.showUsernamePostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
