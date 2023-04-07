import { FormsSheetsRequestState } from './state';

import { formsSheetsGetRequestReducer } from './forms-sheets-get/state';
import { printFormGetRequestReducer } from './print-uscis-form-get/state';
import { downloadFormsGetRequestReducer } from './download-forms-get/state';
import { blanksGetRequestReducer } from './blanks-get/state';
import { downloadBlanksGetRequestReducer } from './download-blanks-get/state';
import { printBlankGetRequestReducer } from './print-blank-get/state';


export function reducer(state: FormsSheetsRequestState = {}, action): FormsSheetsRequestState {
  return {
    formsSheetsGet: formsSheetsGetRequestReducer(state.formsSheetsGet, action),
    printFormGet: printFormGetRequestReducer(state.printFormGet, action),
    downloadFormsGet: downloadFormsGetRequestReducer(state.downloadFormsGet, action),
    blanksGet: blanksGetRequestReducer(state.blanksGet, action),
    downloadBlanksGet: downloadBlanksGetRequestReducer(state.downloadBlanksGet, action),
    printBlankGet: printBlankGetRequestReducer(state.printBlankGet, action),
  };
}
