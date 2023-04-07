import { EmailTemplatesRequestState } from './state';

import { emailTemplateGetRequestReducer } from './email-template-get/state';
import { emailTemplatesGetRequestReducer } from './email-templates-get/state';
import { emailTemplatePutRequestReducer } from './email-template-put/state';
import { defaultEmailTemplateGetRequestReducer } from './default-email-template-get/state';
import { emailTemplateVariablesGetRequestReducer } from './email-template-variables-get/state';

export function reducer(state: EmailTemplatesRequestState = {}, action): EmailTemplatesRequestState {
  return {
    emailTemplateGet: emailTemplateGetRequestReducer(state.emailTemplateGet, action),
    defaultEmailTemplateGet: defaultEmailTemplateGetRequestReducer(state.defaultEmailTemplateGet, action),
    emailTemplatesGet: emailTemplatesGetRequestReducer(state.emailTemplatesGet, action),
    emailTemplatePut: emailTemplatePutRequestReducer(state.emailTemplatePut, action),
    emailTemplateVariablesGet: emailTemplateVariablesGetRequestReducer(state.emailTemplateVariablesGet, action),
  };
}
