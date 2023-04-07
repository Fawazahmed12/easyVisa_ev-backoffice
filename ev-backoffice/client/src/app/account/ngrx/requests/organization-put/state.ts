import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const organizationPutRequestHandler = createRequestHandler('PutOrganizationRequest');

export function organizationPutRequestReducer(state, action) {
  return organizationPutRequestHandler.reducer(state, action);
}

@Injectable()
export class OrganizationPutRequestEffects {

  @Effect()
  organization$: Observable<Action> = organizationPutRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.organizationPutRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
