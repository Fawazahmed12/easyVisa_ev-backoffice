import { Action } from '@ngrx/store';

import { BlankForm, FormsRequestModel, FormsSheets } from '../../models/forms-sheets.model';

import { FORMS_SHEETS } from './forms-sheets.state';


export const FormsSheetsActionTypes = {
  GetFormsSheets: `[${FORMS_SHEETS}] Get Forms Sheets`,
  GetFormsSheetsSuccess: `[${FORMS_SHEETS}] Get Forms Sheets Success`,
  GetFormsSheetsFailure: `[${FORMS_SHEETS}] Get Forms Sheets Failure`,
  GetPrintForm: `[${FORMS_SHEETS}] Get Print Form`,
  GetPrintFormSuccess: `[${FORMS_SHEETS}] Get Print Form Success`,
  GetPrintFormFailure: `[${FORMS_SHEETS}] Get Print Form Failure`,
  GetDownloadForms: `[${FORMS_SHEETS}] Get Download Forms`,
  GetDownloadFormsSuccess: `[${FORMS_SHEETS}] Get Download Forms Success`,
  GetDownloadFormsFailure: `[${FORMS_SHEETS}] Get Download Forms Failure`,
  GetBlanks: `[${FORMS_SHEETS}] Get Blanks`,
  GetBlanksSuccess: `[${FORMS_SHEETS}] Get Blanks Success`,
  GetBlanksFailure: `[${FORMS_SHEETS}] Get Blanks Failure`,
  GetDownloadBlanks: `[${FORMS_SHEETS}] Get Download Blanks`,
  GetDownloadBlanksSuccess: `[${FORMS_SHEETS}] Get Download Blanks Success`,
  GetDownloadBlanksFailure: `[${FORMS_SHEETS}] Get Download Blanks Failure`,
  GetPrintBlank: `[${FORMS_SHEETS}] Get Print Blank`,
  GetPrintBlankSuccess: `[${FORMS_SHEETS}] Get Print Blank Success`,
  GetPrintBlankFailure: `[${FORMS_SHEETS}] Get Print Blank Failure`,
  SelectApplicants: `[${FORMS_SHEETS}] Select Applicants`,
};


export class GetFormsSheets implements Action {
  readonly type = FormsSheetsActionTypes.GetFormsSheets;

  constructor(public payload: number) {
  }
}

export class GetFormsSheetsSuccess implements Action {
  readonly type = FormsSheetsActionTypes.GetFormsSheetsSuccess;

  constructor(public payload: FormsSheets) {
  }
}

export class GetFormsSheetsFailure implements Action {
  readonly type = FormsSheetsActionTypes.GetFormsSheetsFailure;

  constructor(public payload: any) {
  }
}

export class GetPrintForm implements Action {
  readonly type = FormsSheetsActionTypes.GetPrintForm;

  constructor(public payload: FormsRequestModel) {
  }
}

export class GetPrintFormSuccess implements Action {
  readonly type = FormsSheetsActionTypes.GetPrintFormSuccess;

  constructor(public payload: any) {
  }
}

export class GetPrintFormFailure implements Action {
  readonly type = FormsSheetsActionTypes.GetPrintFormFailure;

  constructor(public payload: any) {
  }
}

export class GetDownloadForms implements Action {
  readonly type = FormsSheetsActionTypes.GetDownloadForms;

  constructor(public payload: FormsRequestModel) {
  }
}

export class GetDownloadFormsSuccess implements Action {
  readonly type = FormsSheetsActionTypes.GetDownloadFormsSuccess;

  constructor(public payload: any) {
  }
}

export class GetDownloadFormsFailure implements Action {
  readonly type = FormsSheetsActionTypes.GetDownloadFormsFailure;

  constructor(public payload: any) {
  }
}

export class GetBlanks implements Action {
  readonly type = FormsSheetsActionTypes.GetBlanks;

  constructor(public payload: any) {
  }
}

export class GetBlanksSuccess implements Action {
  readonly type = FormsSheetsActionTypes.GetBlanksSuccess;

  constructor(public payload: BlankForm[]) {
  }
}

export class GetBlanksFailure implements Action {
  readonly type = FormsSheetsActionTypes.GetBlanksFailure;

  constructor(public payload: any) {
  }
}

export class GetDownloadBlanks implements Action {
  readonly type = FormsSheetsActionTypes.GetDownloadBlanks;

  constructor(public payload: {formIdList: string[]}) {
  }
}

export class GetDownloadBlanksSuccess implements Action {
  readonly type = FormsSheetsActionTypes.GetDownloadBlanksSuccess;

  constructor(public payload: any) {
  }
}

export class GetDownloadBlanksFailure implements Action {
  readonly type = FormsSheetsActionTypes.GetDownloadBlanksFailure;

  constructor(public payload: any) {
  }
}

export class GetPrintBlank implements Action {
  readonly type = FormsSheetsActionTypes.GetPrintBlank;

  constructor(public payload: {formId: number}) {
  }
}

export class GetPrintBlankSuccess implements Action {
  readonly type = FormsSheetsActionTypes.GetPrintBlankSuccess;

  constructor(public payload: any) {
  }
}

export class GetPrintBlankFailure implements Action {
  readonly type = FormsSheetsActionTypes.GetPrintBlankFailure;

  constructor(public payload: any) {
  }
}

export class SelectApplicants implements Action {
  readonly type = FormsSheetsActionTypes.SelectApplicants;

  constructor(public payload: number[]) {
  }
}


export type FormsSheetsActionsUnion =
  | GetFormsSheets
  | GetFormsSheetsSuccess
  | GetFormsSheetsFailure
  | GetPrintForm
  | GetPrintFormSuccess
  | GetPrintFormFailure
  | GetDownloadForms
  | GetDownloadFormsSuccess
  | GetDownloadFormsFailure
  | GetBlanks
  | GetBlanksSuccess
  | GetBlanksFailure
  | GetDownloadBlanks
  | GetDownloadBlanksSuccess
  | GetDownloadBlanksFailure
  | GetPrintBlank
  | GetPrintBlankSuccess
  | GetPrintBlankFailure
  | SelectApplicants;
