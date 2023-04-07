import { QuestionnaireProgressGetRequestEffects } from './questionnaire-progress-get/state';
import { DocumentProgressGetRequestEffects } from './document-progress-get/state';

export const ProgressStatusesModuleRequestEffects = [
  QuestionnaireProgressGetRequestEffects,
  DocumentProgressGetRequestEffects,
];
