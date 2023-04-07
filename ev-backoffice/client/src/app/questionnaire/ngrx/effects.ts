import { QuestionnaireEffects } from './questionnaire/questionnaire.effects';
import { QuestionnaireRequestEffects } from './questionnaire-requests/effects';

export const effects = [
  QuestionnaireEffects,
  ...QuestionnaireRequestEffects
];
