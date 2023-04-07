import { Injectable } from '@angular/core';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map, pluck, switchMap, withLatestFrom } from 'rxjs/operators';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';
import { UserActionTypes } from '../../../core/ngrx/user/user.actions';

import { RequestJoin } from '../../models/request-join.model';
import { Invite } from '../../models/invite.model';
import { State } from '../../../core/ngrx/state';

import { ReviewsActionsUnion } from '../reviews/reviews.actions';
import { inviteAttorneyPutRequestHandler } from '../requests/invite-attorney-put/state';
import { inviteDeleteRequestHandler } from '../requests/invite-delete/state';
import { requestJoinPutRequestHandler } from '../requests/request-join-put/state';
import { requestJoinDeleteRequestHandler } from '../requests/state';
import { getRequestJoin } from '../state';

import {
  DeleteInviteFailure,
  DeleteInviteSuccess, DeleteRequest, DeleteRequestFailure, DeleteRequestSuccess,
  InviteRequestActionTypes,
  PutInvite, PutInviteFailure,
  PutInviteSuccess, PutRequestFailure, PutRequestSuccess,
  SetInvite, SetRequest
} from './invite-request.action';

import { ModalService } from '../../../core/services';

@Injectable()
export class InviteRequestEffects {

  @Effect()
  PutInvite$: Observable<Action> = this.actions$.pipe(
    ofType(InviteRequestActionTypes.PutInvite),
    map(({payload}: PutInvite) => inviteAttorneyPutRequestHandler.requestAction(payload))
  );

  @Effect()
  PutInviteSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(inviteAttorneyPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<any>) => new PutInviteSuccess(payload))
  );

  @Effect()
  PutInviteFailure$: Observable<Action> = this.actions$.pipe(
    ofType(inviteAttorneyPutRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestSuccessAction<any>) => new PutInviteFailure(payload))
  );

  @Effect()
  PutRequest$: Observable<Action> = this.actions$.pipe(
    ofType(InviteRequestActionTypes.PutRequest),
    map(({payload}: PutInvite) => requestJoinPutRequestHandler.requestAction(payload))
  );

  @Effect()
  PutRequestSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(requestJoinPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<any>) => new PutRequestSuccess(payload))
  );

  @Effect()
  PutRequestFailure$: Observable<Action> = this.actions$.pipe(
    ofType(requestJoinPutRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PutRequestFailure(payload))
  );

  @Effect({dispatch: false})
  openFailModal$: Observable<Action> = this.actions$.pipe(
    ofType(
      InviteRequestActionTypes.PutRequestFailure,
      InviteRequestActionTypes.PutInviteFailure,
      InviteRequestActionTypes.DeleteRequestFailure,
      InviteRequestActionTypes.DeleteInviteFailure,
    ),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || [payload.error]))
  );

  @Effect()
  DeleteRequest$: Observable<Action> = this.actions$.pipe(
    ofType(InviteRequestActionTypes.DeleteRequest),
    withLatestFrom(this.store.pipe(select(getRequestJoin))),
    map(([, requestJoin]: [DeleteRequest, RequestJoin]) => requestJoinDeleteRequestHandler.requestAction({
        requestId: requestJoin.requestId,
        organizationId: requestJoin.organizationId,
      }))
  );

  @Effect()
  DeleteRequestSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(requestJoinDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<any>) => new DeleteRequestSuccess(payload))
  );

  @Effect()
  DeleteRequestFailure$: Observable<Action> = this.actions$.pipe(
    ofType(requestJoinDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new DeleteRequestFailure(payload))
  );

  @Effect()
  DeleteInvite$: Observable<Action> = this.actions$.pipe(
    ofType(InviteRequestActionTypes.DeleteInvite),
    map(() => inviteDeleteRequestHandler.requestAction())
  );

  @Effect()
  DeleteInviteSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(inviteDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<any>) => new DeleteInviteSuccess(payload))
  );

  @Effect()
  DeleteInviteFailure$: Observable<Action> = this.actions$.pipe(
    ofType(inviteDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestSuccessAction<any>) => new DeleteInviteFailure(payload))
  );

  @Effect()
  setInvite$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.GetUserSuccess),
    pluck('payload', 'profile', 'newFirmInviteDetails'),
    map((invite: Invite) => new SetInvite(invite))
  );

  @Effect()
  setRequest$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.GetUserSuccess),
    pluck('payload', 'profile', 'newFirmJoinRequestDetails'),
    map((request: RequestJoin) => new SetRequest(request))
  );

  constructor(
    private actions$: Actions<ReviewsActionsUnion>,
    private modalService: ModalService,
    private store: Store<State>
  ) {
  }
}
