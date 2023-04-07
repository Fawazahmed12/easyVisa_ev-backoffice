import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { UserRequestService } from '../request.service';

export const convertToAttorneyPostRequestHandler = createRequestHandler('ConvertToAttorneyPost');

export function convertToAttorneyPostRequestReducer(state, action) {
  return convertToAttorneyPostRequestHandler.reducer(state, action);
}


@Injectable()
export class ConvertToAttorneyPostRequestEffects {

  @Effect()
  convertToAttorneyPost$: Observable<Action> = convertToAttorneyPostRequestHandler.effect(
    this.actions$,
    this.userRequestService.convertToAttorneyPostRequest.bind(this.userRequestService)
  );

  constructor(
    private actions$: Actions,
    private userRequestService: UserRequestService
  ) {
  }
}
