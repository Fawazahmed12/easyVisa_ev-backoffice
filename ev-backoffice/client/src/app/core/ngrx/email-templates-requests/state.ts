import { createFeatureSelector, createSelector } from '@ngrx/store';


import { RequestState } from '../utils';
import { EmailTemplate } from '../../models/email-template.model';


export const EMAIL_TEMPLATES_REQUEST = 'EmailTemplatesRequest';

export interface EmailTemplatesRequestState {
  defaultEmailTemplateGet?: RequestState<EmailTemplate>;
  emailTemplateGet?: RequestState<EmailTemplate>;
  emailTemplatesGet?: RequestState<EmailTemplate[]>;
  emailTemplatePut?: RequestState<EmailTemplate>;
  emailTemplateVariablesGet?: RequestState<any>;
}

export const selectEmailTemplatesRequestState = createFeatureSelector<EmailTemplatesRequestState>(EMAIL_TEMPLATES_REQUEST);

export const selectDefaultEmailTemplateGetRequestState = createSelector(
  selectEmailTemplatesRequestState,
  (state: EmailTemplatesRequestState) => state.defaultEmailTemplateGet
);

export const selectEmailTemplateGetRequestState = createSelector(
  selectEmailTemplatesRequestState,
  (state: EmailTemplatesRequestState) => state.emailTemplateGet
);

export const selectEmailTemplatesGetRequestState = createSelector(
  selectEmailTemplatesRequestState,
  (state: EmailTemplatesRequestState) => state.emailTemplatesGet
);

export const selectEmailTemplatePutRequestState = createSelector(
  selectEmailTemplatesRequestState,
  (state: EmailTemplatesRequestState) => state.emailTemplatePut
);


export const selectEmailTemplateVariablesGetRequestState = createSelector(
  selectEmailTemplatesRequestState,
  (state: EmailTemplatesRequestState) => state.emailTemplateVariablesGet
);

export { defaultEmailTemplateGetRequestHandler } from './default-email-template-get/state';
export { emailTemplateGetRequestHandler } from './email-template-get/state';
export { emailTemplatesGetRequestHandler } from './email-templates-get/state';
export { emailTemplatePutRequestHandler } from './email-template-put/state';
export { emailTemplateVariablesGetRequestHandler } from './email-template-variables-get/state';
