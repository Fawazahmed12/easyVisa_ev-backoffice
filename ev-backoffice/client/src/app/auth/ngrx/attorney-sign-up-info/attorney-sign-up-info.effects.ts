import { Injectable } from '@angular/core';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { RequestSuccessAction } from '../../../core/ngrx/utils';
import { RequestFailAction } from '../../../core/ngrx/utils';
import { ModalService } from '../../../core/services';

import { ReferringUserModel } from '../../models/referring-user.model';

import { referringUserGetRequestHandler } from '../requests/referring-user/state';
import { attorneyPostRequestHandler } from '../requests/attorney-post/state';
import { addReferralPostRequestHandler } from '../requests/add-referral-post/state';

import {
  GetReferringUser,
  GetReferringUserSuccess, PostAttorneyUser, PostAttorneyUserSuccess,
  AttorneySignUpInfoActionTypes, PostAddReferralSuccess, PostAddReferralFailure, PostAddReferral, PostAttorneyUserFailure,
} from './attorney-sign-up-info.actions';
import { OkButtonLg } from '../../../core/modals/confirm-modal/confirm-modal.component';

@Injectable()
export class AttorneySignUpInfoEffects {

  @Effect()
  getReferringUser$: Observable<Action> = this.actions$.pipe(
    ofType(AttorneySignUpInfoActionTypes.GetReferringUser),
    map(({payload}: GetReferringUser) => referringUserGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getReferringUserSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(referringUserGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<ReferringUserModel>) => new GetReferringUserSuccess(payload))
  );

  @Effect({dispatch: false})
  getReferringUserFail$: Observable<Action> = this.actions$.pipe(
    ofType(referringUserGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  postAttorneyUser$: Observable<Action> = this.actions$.pipe(
    ofType(AttorneySignUpInfoActionTypes.PostAttorneyUser),
    map(({payload}: PostAttorneyUser) => attorneyPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postAttorneySuccess$: Observable<Action> = this.actions$.pipe(
    ofType(attorneyPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<ReferringUserModel>) => new PostAttorneyUserSuccess(payload))
  );

  @Effect()
  postAttorneyFail$: Observable<Action> = this.actions$.pipe(
    ofType(attorneyPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PostAttorneyUserFailure(payload))
  );

  @Effect()
  postAddReferral$: Observable<Action> = this.actions$.pipe(
    ofType(AttorneySignUpInfoActionTypes.PostAddReferral),
    map(({payload}: PostAddReferral) => addReferralPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postAddReferralSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(addReferralPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<{ email: string }>) => new PostAddReferralSuccess(payload))
  );

  @Effect()
  postAddReferralFail$: Observable<Action> = this.actions$.pipe(
    ofType(addReferralPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PostAddReferralFailure(payload))
  );

  @Effect({dispatch: false})
  showErrorModal$: Observable<Action> = this.actions$.pipe(
    ofType(
      AttorneySignUpInfoActionTypes.PostAddReferralFailure,
      AttorneySignUpInfoActionTypes.PostAttorneyUserFailure,
    ),
    tap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || [payload.error]))
  );

  @Effect({dispatch: false})
  showSuccessModal$: Observable<Action> = this.actions$.pipe(
    ofType(AttorneySignUpInfoActionTypes.PostAddReferralSuccess),
    tap(() => this.modalService.openConfirmModal({
      header: 'TEMPLATE.AUTH.STANDARD_CHARGES.REFERRAL_ADDED',
      body: 'TEMPLATE.AUTH.STANDARD_CHARGES.REFERRAL_ADDED_SUCCESS',
      buttons: [OkButtonLg],
      centered: true,
    }))
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
