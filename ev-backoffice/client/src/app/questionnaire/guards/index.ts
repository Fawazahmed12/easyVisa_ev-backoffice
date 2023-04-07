import { DeactiveQuestionnaireGuardService } from './deactive-questionnaire-guard.service';
import { ActiveQuestionnaireGuardService } from './active-questionnaire-guard.service';
import { ActiveSectionGuardService } from './active-section-guard.service';

export const GUARD_PROVIDERS = [
  DeactiveQuestionnaireGuardService,
  ActiveQuestionnaireGuardService,
  ActiveSectionGuardService,
];

