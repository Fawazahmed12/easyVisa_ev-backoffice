import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { EMPTY, Observable } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';

import { createRequestHandler, RequestFailAction } from '../../../../core/ngrx/utils';
import { ModalService } from '../../../../core/services/modal.service';
import { OkButton } from '../../../../core/modals/confirm-modal/confirm-modal.component';

import { TaskQueueModuleRequestService } from '../request.service';

export const applicantInvitePostRequestHandler = createRequestHandler('PostApplicantInviteRequest');

export function applicantInvitePostRequestReducer(state, action) {
  return applicantInvitePostRequestHandler.reducer(state, action);
}

@Injectable()
export class ApplicantInvitePostRequestEffects {

  @Effect()
  applicantInviteData$: Observable<Action> = applicantInvitePostRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.applicantInvitePostRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect({dispatch: false})
  applicantInviteDataFail$: Observable<Action> = this.actions$.pipe(
    ofType(applicantInvitePostRequestHandler.ActionTypes.REQUEST_FAIL),
    switchMap(({payload}: RequestFailAction<any>) =>
      this.modalService.showErrorModal(payload.error.errors).pipe(
        catchError(() => EMPTY),
      )
    )
  );

  @Effect({dispatch: false})
  applicantInviteDataSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(applicantInvitePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    tap(({payload}: RequestFailAction<any>) => {
      this.modalService.openConfirmModal({
        header: '',
        body: payload.message,
        buttons: [OkButton],
        centered: true,
      });
    })
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
