import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { createRequestHandler, RequestFailAction } from '../../../../core/ngrx/utils';
import { OpenPackagesFailModals } from '../../../../core/ngrx/packages/packages.actions';

import { ModalService } from '../../../../core/services/modal.service';
import { OkButton } from '../../../../core/modals/confirm-modal/confirm-modal.component';

import { TaskQueueModuleRequestService } from '../request.service';

export const packageWelcomeEmailPostRequestHandler = createRequestHandler('PostPackageWelcomeEmailRequest');

export function packageWelcomeEmailPostRequestReducer(state, action) {
  return packageWelcomeEmailPostRequestHandler.reducer(state, action);
}

@Injectable()
export class PackageWelcomeEmailPostRequestEffects {

  @Effect()
  packageWelcomeEmailData$: Observable<Action> = packageWelcomeEmailPostRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.packageWelcomeEmailPostRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect()
  packageWelcomeEmailDataFail$: Observable<Action> = this.actions$.pipe(
    ofType(packageWelcomeEmailPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new OpenPackagesFailModals(payload.error.errors))
  );

  @Effect({dispatch: false})
  packageWelcomeEmailDataSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(packageWelcomeEmailPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    tap(({payload}: RequestFailAction<any>) => {
      this.modalService.openConfirmModal({
        header: 'TEMPLATE.TASK_QUEUE.APPLICANT.EMAIL_SENT',
        body: payload.message,
        buttons: [OkButton],
        centered: true,
      });
    })
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
    private modalService: ModalService,
  ) {
  }
}
