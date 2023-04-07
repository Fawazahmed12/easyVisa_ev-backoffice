import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { DashboardRequestService } from '../request.service';


export const inviteColleaguesPostRequestHandler = createRequestHandler('PostInviteColleaguesRequest');

export function inviteColleaguesPostRequestReducer(state, action) {
  return inviteColleaguesPostRequestHandler.reducer(state, action);
}

@Injectable()
export class InviteColleaguesPostRequestEffects {

  @Effect()
  inviteColleaguesPost$: Observable<Action> = inviteColleaguesPostRequestHandler.effect(
    this.actions$,
    this.dashboardRequestService.inviteColleaguesPost.bind(this.dashboardRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardRequestService: DashboardRequestService,
  ) {
  }
}
