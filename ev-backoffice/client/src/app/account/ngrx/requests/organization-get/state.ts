import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const organizationGetRequestHandler = createRequestHandler('GetOrganizationRequest');

export function organizationGetRequestReducer(state, action) {
  return organizationGetRequestHandler.reducer(state, action);
}

@Injectable()
export class OrganizationGetRequestEffects {

  @Effect()
  organization$: Observable<Action> = organizationGetRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.organizationGetRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
