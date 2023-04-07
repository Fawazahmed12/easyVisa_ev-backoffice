import { Action } from '@ngrx/store';

import { EmailTemplate } from '../../models/email-template.model';

import { EMAIL_TEMPLATES } from './email-templates.state';
import { EmailTemplateTypes } from '../../models/email-template-types.enum';

export const EmailTemplatesActionTypes = {
  GetDefaultEmailTemplate: `[${EMAIL_TEMPLATES}] Get Default Email Template`,
  GetEmailTemplate: `[${EMAIL_TEMPLATES}] Get Email Template`,
  GetEmailTemplateSuccess: `[${EMAIL_TEMPLATES}] Get Email Template Success`,
  GetEmailTemplateFailure: `[${EMAIL_TEMPLATES}] Get Email Template Failure`,
  GetEmailTemplates: `[${EMAIL_TEMPLATES}] Get Email Templates`,
  GetEmailTemplatesSuccess: `[${EMAIL_TEMPLATES}] Get Email Templates Success`,
  GetEmailTemplatesFailure: `[${EMAIL_TEMPLATES}] Get Email Templates Failure`,
  PutEmailTemplate: `[${EMAIL_TEMPLATES}] Put Email Template`,
  PutEmailTemplateSuccess: `[${EMAIL_TEMPLATES}] Put Email Template Success`,
  PutEmailTemplateFailure: `[${EMAIL_TEMPLATES}] Put Email Template Failure`,
  GetEmailTemplateVariables: `[${EMAIL_TEMPLATES}] Get Email Template Variables`,
  GetEmailTemplateVariablesSuccess: `[${EMAIL_TEMPLATES}] Get Email Template Variables Success`,
  GetEmailTemplateVariablesFailure: `[${EMAIL_TEMPLATES}] Get Email Template Variables Failure`,
};

export class GetDefaultEmailTemplate implements Action {
  readonly type = EmailTemplatesActionTypes.GetDefaultEmailTemplate;

  constructor(public payload: string) {
  }
}

export class GetEmailTemplate implements Action {
  readonly type = EmailTemplatesActionTypes.GetEmailTemplate;

  constructor(public payload: string | {type: string; defaultTemplate?: boolean; representativeId: number}) {
  }
}

export class GetEmailTemplateSuccess implements Action {
  readonly type = EmailTemplatesActionTypes.GetEmailTemplateSuccess;

  constructor(public payload: EmailTemplate) {
  }
}

export class GetEmailTemplateFailure implements Action {
  readonly type = EmailTemplatesActionTypes.GetEmailTemplateFailure;

  constructor(public payload?: any) {
  }
}

export class GetEmailTemplates implements Action {
  readonly type = EmailTemplatesActionTypes.GetEmailTemplates;

  constructor(public payload: {templateType: EmailTemplateTypes[]; representativeId: string}) {
  }
}

export class GetEmailTemplatesSuccess implements Action {
  readonly type = EmailTemplatesActionTypes.GetEmailTemplatesSuccess;

  constructor(public payload: EmailTemplate[]) {
  }
}

export class GetEmailTemplatesFailure implements Action {
  readonly type = EmailTemplatesActionTypes.GetEmailTemplatesFailure;

  constructor(public payload?: any) {
  }
}

export class PutEmailTemplate implements Action {
  readonly type = EmailTemplatesActionTypes.PutEmailTemplate;

  constructor(public payload: EmailTemplate) {
  }
}

export class PutEmailTemplateSuccess implements Action {
  readonly type = EmailTemplatesActionTypes.PutEmailTemplateSuccess;

  constructor(public payload: EmailTemplate) {
  }
}

export class PutEmailTemplateFailure implements Action {
  readonly type = EmailTemplatesActionTypes.PutEmailTemplateFailure;

  constructor(public payload?: any) {
  }
}


export class GetEmailTemplateVariables implements Action {
  readonly type = EmailTemplatesActionTypes.GetEmailTemplateVariables;

  constructor(public payload: any) {
  }
}

export class GetEmailTemplateVariablesSuccess implements Action {
  readonly type = EmailTemplatesActionTypes.GetEmailTemplateVariablesSuccess;

  constructor(public payload: any) {
  }
}

export class GetEmailTemplateVariablesFailure implements Action {
  readonly type = EmailTemplatesActionTypes.GetEmailTemplateVariablesFailure;

  constructor(public payload?: any) {
  }
}

export type EmailTemplatesActionsUnion =
  | GetDefaultEmailTemplate
  | GetEmailTemplate
  | GetEmailTemplateSuccess
  | GetEmailTemplateFailure
  | GetEmailTemplates
  | GetEmailTemplatesSuccess
  | GetEmailTemplatesFailure
  | PutEmailTemplate
  | PutEmailTemplateSuccess
  | PutEmailTemplateFailure
  | GetEmailTemplateVariables
  | GetEmailTemplateVariablesSuccess
  | GetEmailTemplateVariablesFailure;
