import { createFeatureSelector } from '@ngrx/store';

import { RequestState } from '../../../core/ngrx/utils';
import { FormsSheets } from '../../models/forms-sheets.model';


export const FORMS_SHEETS_REQUESTS = 'FormsSheetsRequests';

export interface FormsSheetsRequestState {
  formsSheetsGet?: RequestState<FormsSheets>;
  printFormGet?: RequestState<any>;
  downloadFormsGet?: RequestState<any>;
  blanksGet?: RequestState<any>;
  downloadBlanksGet?: RequestState<any>;
  printBlankGet?: RequestState<any>;
}

export const selectFormsSheetsModuleRequestsState = createFeatureSelector<FormsSheetsRequestState>(FORMS_SHEETS_REQUESTS);

export const selectFormsSheetsGetRequestState = (state: FormsSheetsRequestState) => state.formsSheetsGet;
export const selectPrintFormGetRequestState = (state: FormsSheetsRequestState) => state.printFormGet;
export const selectDownloadFormsGetRequestState = (state: FormsSheetsRequestState) => state.downloadFormsGet;
export const selectBlanksGetRequestState = (state: FormsSheetsRequestState) => state.blanksGet;
export const selectDownloadBlanksGetRequestState = (state: FormsSheetsRequestState) => state.downloadBlanksGet;
export const selectPrintBlankGetRequestState = (state: FormsSheetsRequestState) => state.printBlankGet;


export { formsSheetsGetRequestHandler } from './forms-sheets-get/state';
export { printFormGetRequestHandler } from './print-uscis-form-get/state';
export { downloadFormsGetRequestHandler } from './download-forms-get/state';
export { blanksGetRequestHandler } from './blanks-get/state';
export { downloadBlanksGetRequestHandler } from './download-blanks-get/state';
export { printBlankGetRequestHandler } from './print-blank-get/state';


