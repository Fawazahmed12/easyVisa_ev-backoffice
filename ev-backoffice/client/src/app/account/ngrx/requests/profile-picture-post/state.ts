import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const profilePicturePostRequestHandler = createRequestHandler('PostProfilePictureRequest');

export function profilePicturePostRequestReducer(state, action) {
  return profilePicturePostRequestHandler.reducer(state, action);
}

@Injectable()
export class ProfilePicturePostRequestEffects {

  @Effect()
  profilePicture$: Observable<Action> = profilePicturePostRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.profilePicturePostRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
