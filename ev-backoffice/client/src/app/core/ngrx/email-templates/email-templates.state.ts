import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';

import { createFeatureSelector, createSelector } from '@ngrx/store';

import { EmailTemplate } from '../../models/email-template.model';
import { AuthState } from '../auth/auth.state';

export const EMAIL_TEMPLATES = 'EmailTemplates';

export interface EmailTemplatesState extends EntityState<EmailTemplate> {
  emailTemplateVariables: any;
}

export const adapter: EntityAdapter<EmailTemplate> = createEntityAdapter<EmailTemplate>({
  selectId: (entity) => entity.templateType
});

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectEmailTemplates = selectAll;

export const selectEmailTemplatesEntities = selectEntities;

export const selectEmailTemplatesState = createFeatureSelector<EmailTemplatesState>(EMAIL_TEMPLATES);

export const getEmailTemplates = createSelector(
  selectEmailTemplatesState,
  selectEmailTemplates,
);

export const getEmailTemplatesEntities = createSelector(
  selectEmailTemplatesState,
  selectEmailTemplatesEntities,
);

export const selectEmailTemplateVariables = ({emailTemplateVariables}: EmailTemplatesState) => emailTemplateVariables;

export const getEmailTemplateVariables = createSelector(
  selectEmailTemplatesState,
  selectEmailTemplateVariables,
);
