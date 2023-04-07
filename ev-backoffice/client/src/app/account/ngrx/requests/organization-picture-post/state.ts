import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const organizationPicturePostRequestHandler = createRequestHandler('PostOrganizationPictureRequest');

export function organizationPicturePostRequestReducer(state, action) {
  return organizationPicturePostRequestHandler.reducer(state, action);
}

@Injectable()
export class OrganizationPicturePostRequestEffects {

  @Effect()
  organizationPicture$: Observable<Action> = organizationPicturePostRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.organizationPicturePostRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
