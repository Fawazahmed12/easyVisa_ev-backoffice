import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { OrganizationsRequestService } from '../request.service';

export const menuOrganizationsGetRequestHandler = createRequestHandler('Menu Organizations Get');

export function menuOrganizationsRequestReducer(state, action) {
  return menuOrganizationsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class MenuOrganizationsGetRequestEffects {

  @Effect()
  menuOrganizationsGetSelfData$: Observable<Action> = menuOrganizationsGetRequestHandler.effect(
    this.actions$,
    this.organizationsRequestService.menuOrganizationsGetRequest.bind(this.organizationsRequestService)
  );

  constructor(
    private actions$: Actions,
    private organizationsRequestService: OrganizationsRequestService
  ) {
  }
}
