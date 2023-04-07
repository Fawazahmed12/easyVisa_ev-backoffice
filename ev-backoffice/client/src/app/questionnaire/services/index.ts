import { QuestionnaireService } from './questionnaire.service';
import { FocusManagerService } from './focusmanager.service';
import { PdfPrintTestingService } from '../pdf-print-testing/pdf-print-testing.service';

export const PROVIDERS = [
  QuestionnaireService,
  FocusManagerService,
  PdfPrintTestingService
];

export * from './questionnaire.service';

