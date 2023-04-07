import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { EmailTemplate } from '../../models/email-template.model';
import { emailTemplateGetRequestHandler } from '../email-templates-requests/email-template-get/state';
import { RequestFailAction, RequestSuccessAction } from '../utils';
import {
  EmailTemplatesActionTypes, GetDefaultEmailTemplate,
  GetEmailTemplate,
  GetEmailTemplates,
  GetEmailTemplatesSuccess,
  GetEmailTemplateSuccess, GetEmailTemplateVariables, GetEmailTemplateVariablesSuccess,
  PutEmailTemplate,
  PutEmailTemplateSuccess
} from './email-templates.actions';
import { emailTemplatesGetRequestHandler } from '../email-templates-requests/email-templates-get/state';
import { emailTemplatePutRequestHandler } from '../email-templates-requests/email-template-put/state';
import { defaultEmailTemplateGetRequestHandler } from '../email-templates-requests/default-email-template-get/state';
import { OkButton } from '../../modals/confirm-modal/confirm-modal.component';
import { ModalService } from '../../services';
import {emailTemplateVariablesGetRequestHandler} from '../email-templates-requests/email-template-variables-get/state';

@Injectable()
export class EmailTemplatesEffects {

  @Effect()
  getDefaultEmailTemplate$: Observable<Action> = this.actions$.pipe(
    ofType(EmailTemplatesActionTypes.GetDefaultEmailTemplate),
    map(({payload}: GetDefaultEmailTemplate) => defaultEmailTemplateGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getEmailTemplate$: Observable<Action> = this.actions$.pipe(
    ofType(EmailTemplatesActionTypes.GetEmailTemplate),
    map(({payload}: GetEmailTemplate) => emailTemplateGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getEmailTemplateSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(
      defaultEmailTemplateGetRequestHandler.ActionTypes.REQUEST_SUCCESS,
      emailTemplateGetRequestHandler.ActionTypes.REQUEST_SUCCESS,
    ),
    map(({payload}: RequestSuccessAction<EmailTemplate>) => new GetEmailTemplateSuccess(payload))
  );

  @Effect({dispatch: false})
  getEmailTemplateFail$: Observable<Action> = this.actions$.pipe(
    ofType(
      defaultEmailTemplateGetRequestHandler.ActionTypes.REQUEST_FAIL,
      emailTemplateGetRequestHandler.ActionTypes.REQUEST_FAIL,
    ),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  getEmailTemplates$: Observable<Action> = this.actions$.pipe(
    ofType(EmailTemplatesActionTypes.GetEmailTemplates),
    map(({payload}: GetEmailTemplates) => emailTemplatesGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getEmailTemplatesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(emailTemplatesGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<EmailTemplate[]>) => new GetEmailTemplatesSuccess(payload))
  );

  @Effect({dispatch: false})
  getEmailTemplatesFail$: Observable<Action> = this.actions$.pipe(
    ofType(emailTemplatesGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  putEmailTemplate$: Observable<Action> = this.actions$.pipe(
    ofType(EmailTemplatesActionTypes.PutEmailTemplate),
    map(({payload}: PutEmailTemplate) => emailTemplatePutRequestHandler.requestAction(payload))
  );

  @Effect()
  putEmailTemplateSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(emailTemplatePutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<EmailTemplate>) => new PutEmailTemplateSuccess(payload)),
    tap(({ payload }: RequestFailAction<any>) => {
      this.modalService.openConfirmModal({
          header: 'TEMPLATE.ACCOUNT.NOTIFICATIONS_REMINDERS.SUCCESS_MODAL.TITLE',
          body: 'TEMPLATE.ACCOUNT.NOTIFICATIONS_REMINDERS.SUCCESS_MODAL.P_1',
          buttons: [OkButton],
          centered: true,
        }
      );
    })
  );

  @Effect({dispatch: false})
  putEmailTemplateFail$: Observable<Action> = this.actions$.pipe(
    ofType(emailTemplatePutRequestHandler.ActionTypes.REQUEST_FAIL),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || [payload.error]))
  );

  @Effect()
  getEmailTemplateVariables$: Observable<Action> = this.actions$.pipe(
    ofType(EmailTemplatesActionTypes.GetEmailTemplateVariables),
    map(({payload}: GetEmailTemplateVariables) => emailTemplateVariablesGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getEmailTemplateVariablesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(
      emailTemplateVariablesGetRequestHandler.ActionTypes.REQUEST_SUCCESS,
    ),
    map(({payload}: RequestSuccessAction<EmailTemplate>) => new GetEmailTemplateVariablesSuccess(payload))
  );

  @Effect({dispatch: false})
  getEmailTemplateVariablesFail$: Observable<Action> = this.actions$.pipe(
    ofType(
      emailTemplateVariablesGetRequestHandler.ActionTypes.REQUEST_FAIL,
    ),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService
  ) {
  }

}
