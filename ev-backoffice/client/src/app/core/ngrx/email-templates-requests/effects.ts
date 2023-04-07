import { EmailTemplateGetRequestEffects } from './email-template-get/state';
import { EmailTemplatesGetRequestEffects } from './email-templates-get/state';
import { EmailTemplatePutRequestEffects } from './email-template-put/state';
import { DefaultEmailTemplateGetRequestEffects } from './default-email-template-get/state';
import { EmailTemplateVariablesGetRequestEffects } from './email-template-variables-get/state';

export const EmailTemplatesRequestEffects = [
  DefaultEmailTemplateGetRequestEffects,
  EmailTemplateGetRequestEffects,
  EmailTemplatesGetRequestEffects,
  EmailTemplatePutRequestEffects,
  EmailTemplateVariablesGetRequestEffects
];
