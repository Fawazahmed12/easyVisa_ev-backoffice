import { FormsSheetsGetRequestEffects } from './forms-sheets-get/state';
import { PrintFormGetRequestEffects } from './print-uscis-form-get/state';
import { DownloadFormsGetRequestEffects } from './download-forms-get/state';
import { BlanksGetRequestEffects } from './blanks-get/state';
import { DownloadBlanksGetRequestEffects } from './download-blanks-get/state';
import { PrintBlankGetRequestEffects } from './print-blank-get/state';


export const FormsSheetsRequestEffects = [
  FormsSheetsGetRequestEffects,
  PrintFormGetRequestEffects,
  DownloadFormsGetRequestEffects,
  BlanksGetRequestEffects,
  DownloadBlanksGetRequestEffects,
  PrintBlankGetRequestEffects,
];
