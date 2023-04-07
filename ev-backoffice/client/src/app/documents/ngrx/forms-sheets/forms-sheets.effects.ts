import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';

import { BlankForm, FormsSheets } from '../../models/forms-sheets.model';

import { formsSheetsGetRequestHandler } from '../forms-sheets-requests/forms-sheets-get/state';

import {
  FormsSheetsActionTypes, GetBlanks,
  GetBlanksSuccess, GetDownloadBlanks, GetDownloadBlanksFailure,
  GetDownloadBlanksSuccess,
  GetDownloadForms,
  GetDownloadFormsFailure,
  GetDownloadFormsSuccess,
  GetFormsSheets,
  GetFormsSheetsFailure,
  GetFormsSheetsSuccess, GetPrintBlank, GetPrintBlankFailure, GetPrintBlankSuccess,
  GetPrintForm,
  GetPrintFormFailure,
  GetPrintFormSuccess, SelectApplicants
} from './forms-sheets.actions';
import { printFormGetRequestHandler } from '../forms-sheets-requests/print-uscis-form-get/state';
import { downloadFormsGetRequestHandler } from '../forms-sheets-requests/download-forms-get/state';
import { blanksGetRequestHandler } from '../forms-sheets-requests/blanks-get/state';
import { downloadBlanksGetRequestHandler } from '../forms-sheets-requests/download-blanks-get/state';
import { printBlankGetRequestHandler } from '../forms-sheets-requests/print-blank-get/state';


@Injectable()
export class FormsSheetsEffects {

  @Effect()
  getFormsSheets$: Observable<Action> = this.actions$.pipe(
    ofType(FormsSheetsActionTypes.GetFormsSheets),
    map(({ payload }: GetFormsSheets) => formsSheetsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getFormsSheetsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(formsSheetsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<FormsSheets>) => new GetFormsSheetsSuccess(payload))
  );

  @Effect()
  getFormsSheetsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(formsSheetsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetFormsSheetsFailure(payload))
  );

  @Effect()
  getPrintForm$: Observable<Action> = this.actions$.pipe(
    ofType(FormsSheetsActionTypes.GetPrintForm),
    map(({ payload }: GetPrintForm) => printFormGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getPrintFormSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(printFormGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new GetPrintFormSuccess(payload))
  );

  @Effect()
  getPrintFormFailure$: Observable<Action> = this.actions$.pipe(
    ofType(printFormGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetPrintFormFailure(payload))
  );

  @Effect()
  getDownloadForms$: Observable<Action> = this.actions$.pipe(
    ofType(FormsSheetsActionTypes.GetDownloadForms),
    map(({ payload }: GetDownloadForms) => downloadFormsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getDownloadFormsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(downloadFormsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new GetDownloadFormsSuccess(payload))
  );

  @Effect()
  getDownloadFormsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(downloadFormsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetDownloadFormsFailure(payload))
  );

  @Effect()
  getBlanks$: Observable<Action> = this.actions$.pipe(
    ofType(FormsSheetsActionTypes.GetBlanks),
    map(({ payload }: GetBlanks) => blanksGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getBlanksSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(blanksGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<BlankForm[]>) => new GetBlanksSuccess(payload))
  );

  @Effect()
  getBlanksFailure$: Observable<Action> = this.actions$.pipe(
    ofType(blanksGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetDownloadFormsFailure(payload))
  );

  @Effect()
  getDownloadBlanks$: Observable<Action> = this.actions$.pipe(
    ofType(FormsSheetsActionTypes.GetDownloadBlanks),
    map(({ payload }: GetDownloadBlanks) => downloadBlanksGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getDownloadBlanksSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(downloadBlanksGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new GetDownloadBlanksSuccess(payload))
  );

  @Effect()
  getDownloadBlanksFailure$: Observable<Action> = this.actions$.pipe(
    ofType(downloadBlanksGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetDownloadBlanksFailure(payload))
  );

  @Effect()
  getPrintBlank$: Observable<Action> = this.actions$.pipe(
    ofType(FormsSheetsActionTypes.GetPrintBlank),
    map(({ payload }: GetPrintBlank) => printBlankGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getPrintBlankSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(printBlankGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new GetPrintBlankSuccess(payload))
  );

  @Effect()
  getPrintBlankFailure$: Observable<Action> = this.actions$.pipe(
    ofType(printBlankGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetPrintBlankFailure(payload))
  );

  constructor(private actions$: Actions) {
  }
}
