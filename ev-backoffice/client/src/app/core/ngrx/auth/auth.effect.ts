import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { ModalService } from '../../services';

import { RequestFailAction, RequestSuccessAction } from '../utils';
import { changePasswordPutRequestHandler } from '../auth-requests/change-password/state';

import { AuthActionTypes, PutPassword, PutPasswordSuccess } from './auth.actions';
import { OkButton } from '../../modals/confirm-modal/confirm-modal.component';


@Injectable()
export class AuthEffects {

  @Effect()
  putPassword$: Observable<Action> = this.actions$.pipe(
    ofType(AuthActionTypes.PutPassword),
    map(({payload}: PutPassword) => changePasswordPutRequestHandler.requestAction(payload))
  );

  @Effect()
  putPasswordSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(changePasswordPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<any>) => new PutPasswordSuccess(payload))
  );

  @Effect({dispatch: false})
  putPasswordSuccessModal$: Observable<Action> = this.actions$.pipe(
    ofType(AuthActionTypes.PutPasswordSuccess),
    switchMap(() =>
      this.modalService.openConfirmModal({
        header: 'TEMPLATE.ACCOUNT.LOGIN_CREDENTIALS.PASSWORD_UPDATED_TITLE',
        body: 'TEMPLATE.ACCOUNT.LOGIN_CREDENTIALS.PASSWORD_UPDATED_BODY',
        centered: true,
        buttons: [OkButton],
      })
    )
  );

  @Effect({dispatch: false})
  putPasswordFail$: Observable<Action> = this.actions$.pipe(
    ofType(changePasswordPutRequestHandler.ActionTypes.REQUEST_FAIL),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || payload.error))
  );


  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
