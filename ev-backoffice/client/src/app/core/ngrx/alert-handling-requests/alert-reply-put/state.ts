import { Injectable } from '@angular/core';

import { Action, select, Store, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { filter, map, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { Role } from '../../../models/role.enum';
import { AuthService } from '../../../services';

import { GetMenuOrganizations } from '../../organizations/organizations.actions';
import { createRequestHandler, RequestSuccessAction } from '../../utils';
import { getCurrentUserRoles } from '../../user/user.state';
import { State } from '../../state';

import { AlertHandlingRequestService } from '../request.service';

export const alertReplyPutRequestHandler = createRequestHandler('PutAlertReplyRequest');

export function alertReplyPutRequestReducer(state, action) {
  return alertReplyPutRequestHandler.reducer(state, action);
}

@Injectable()
export class AlertReplyPutRequestEffects {

  @Effect()
  alertReply$: Observable<Action> = alertReplyPutRequestHandler.effect(
    this.actions$,
    this.alertHandlingRequestService.alertReplyPutRequest.bind(this.alertHandlingRequestService)
  );

  @Effect()
  alertReplySuccess$: Observable<Action> = this.actions$.pipe(
    ofType(alertReplyPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    filter(({payload}: RequestSuccessAction<{updateUser: boolean}>) => payload.updateOrgMenu),
    withLatestFrom(this.store.pipe(select(getCurrentUserRoles))),
    filter(([, roles]) => !roles.some((role) => role === Role.ROLE_USER)),
    map(() => new GetMenuOrganizations()),
  );

  constructor(
    private actions$: Actions,
    private alertHandlingRequestService: AlertHandlingRequestService,
    private authService: AuthService,
    private store: Store<State>,
  ) {
  }
}
