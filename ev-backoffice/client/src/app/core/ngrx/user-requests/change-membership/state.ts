import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { UserRequestService } from '../request.service';

export const changeMembershipPatchRequestHandler = createRequestHandler('ChangeMembershipPatch');

export function changeMembershipPatchRequestReducer(state, action) {
  return changeMembershipPatchRequestHandler.reducer(state, action);
}


@Injectable()
export class ChangeMembershipPatchRequestEffects {

  @Effect()
  changeMembershipPatch$: Observable<Action> = changeMembershipPatchRequestHandler.effect(
    this.actions$,
    this.userRequestService.changeMembershipRequest.bind(this.userRequestService)
  );

  constructor(
    private actions$: Actions,
    private userRequestService: UserRequestService
  ) {
  }
}
