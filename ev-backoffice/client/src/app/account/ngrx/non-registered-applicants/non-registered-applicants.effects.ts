import { Injectable } from '@angular/core';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';
import { EMPTY, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';
import { FeeDetails } from '../../../core/models/fee-details.model';
import { ModalService } from '../../../core/services';

import { nonRegisteredApplicantDeleteRequestHandler } from '../requests/non-registered-applicant-delete/state';

import {
  DeleteNonRegisteredApplicant,
  DeleteNonRegisteredApplicantFailure,
  DeleteNonRegisteredApplicantSuccess,
  NonRegisteredApplicantsActionTypes
} from './non-registered-applicants.action';
import { OkButton, OkButtonLg } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { catchError } from 'rxjs/operators';

@Injectable()
export class NonRegisteredApplicantsEffects {

  @Effect()
  deleteNonRegisteredApplicant$: Observable<Action> = this.actions$.pipe(
    ofType(NonRegisteredApplicantsActionTypes.DeleteNonRegisteredApplicant),
    map(({payload}: DeleteNonRegisteredApplicant) => nonRegisteredApplicantDeleteRequestHandler.requestAction(payload))
  );

  @Effect()
  deleteNonRegisteredApplicantSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(nonRegisteredApplicantDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<FeeDetails>) => new DeleteNonRegisteredApplicantSuccess(payload))
  );

  @Effect()
  deleteNonRegisteredApplicantFail$: Observable<Action> = this.actions$.pipe(
    ofType(nonRegisteredApplicantDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new DeleteNonRegisteredApplicantFailure(payload))
  );

  @Effect({dispatch: false})
  openFailModal$: Observable<Action> = this.actions$.pipe(
    ofType(
      NonRegisteredApplicantsActionTypes.DeleteNonRegisteredApplicantFailure,
    ),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || [payload.error]))
  );

  @Effect({dispatch: false})
  openDeleteDataModal$: Observable<Action> = this.actions$.pipe(
    ofType(
      NonRegisteredApplicantsActionTypes.DeleteNonRegisteredApplicantSuccess,
    ),
    switchMap(() => this.modalService.openConfirmModal({
        header: 'TEMPLATE.ACCOUNT.PROFILE.DELETE_DATA_OF_APPLICANT.DELETE_DATA_OF_APPLICANT_MODAL_SUCCESS.HEADER',
        body: 'TEMPLATE.ACCOUNT.PROFILE.DELETE_DATA_OF_APPLICANT.DELETE_DATA_OF_APPLICANT_MODAL_SUCCESS.P_1',
        centered: true,
        buttons: [OkButtonLg],
      }).pipe(
        catchError(() => EMPTY)
      ))
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
