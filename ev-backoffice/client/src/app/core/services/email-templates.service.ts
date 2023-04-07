import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { select, Store } from '@ngrx/store';
import { State } from '../ngrx/state';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import {
  getEmailTemplates,
  getEmailTemplatesEntities, getEmailTemplateVariables,
} from '../ngrx/email-templates/email-templates.state';
import {
  GetEmailTemplate,
  GetEmailTemplates, GetEmailTemplateVariables,
  PutEmailTemplate
} from '../ngrx/email-templates/email-templates.actions';
import { RequestState } from '../ngrx/utils';
import { EmailTemplate } from '../models/email-template.model';
import {
  selectDefaultEmailTemplateGetRequestState,
  selectEmailTemplateGetRequestState,
  selectEmailTemplatePutRequestState,
  selectEmailTemplatesGetRequestState, selectEmailTemplateVariablesGetRequestState
} from '../ngrx/email-templates-requests/state';
import { EmailTemplateTypes } from '../models/email-template-types.enum';

@Injectable()
export class EmailTemplatesService {
  getEmailTemplateRequest$: Observable<RequestState<EmailTemplate>>;
  getDefaultEmailTemplateRequest$: Observable<RequestState<EmailTemplate>>;
  putEmailTemplateRequest$: Observable<RequestState<EmailTemplate>>;
  getEmailTemplatesRequest$: Observable<RequestState<EmailTemplate[]>>;
  emailTemplates$: Observable<EmailTemplate[]>;
  emailTemplatesEntities$: Observable<any>;
  getEmailTemplateVariablesRequest$: Observable<RequestState<any>>;
  emailTemplateVariables$: Observable<any>;

  constructor(
    private store: Store<State>,
  ) {
    this.emailTemplates$ = this.store.pipe(select(getEmailTemplates));
    this.emailTemplatesEntities$ = this.store.pipe(select(getEmailTemplatesEntities));
    this.getEmailTemplatesRequest$ = this.store.pipe(select(selectEmailTemplatesGetRequestState));
    this.getDefaultEmailTemplateRequest$ = this.store.pipe(select(selectDefaultEmailTemplateGetRequestState));
    this.getEmailTemplateRequest$ = this.store.pipe(select(selectEmailTemplateGetRequestState));
    this.putEmailTemplateRequest$ = this.store.pipe(select(selectEmailTemplatePutRequestState));
    this.getEmailTemplateVariablesRequest$ = this.store.pipe(select(selectEmailTemplateVariablesGetRequestState));
    this.emailTemplateVariables$ = this.store.pipe(select(getEmailTemplateVariables));
  }

  getEmailTemplates(params: {templateType: EmailTemplateTypes[]; representativeId: string}) {
    this.store.dispatch(new GetEmailTemplates(params));
    return this.getEmailTemplatesRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updateEmailTemplate(emailTemplate) {
    this.store.dispatch(new PutEmailTemplate(emailTemplate));
    return this.putEmailTemplateRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getEmailTemplate(template, defaultTemplate?) {
    const params = {
      type: template.templateType,
      representativeId: template.representativeId,
      defaultTemplate: defaultTemplate || false,
    };
    this.store.dispatch(new GetEmailTemplate(params));
    return this.getEmailTemplateRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getEmailTemplateVariables(params) {
    this.store.dispatch(new GetEmailTemplateVariables(params));
    return this.getEmailTemplateVariablesRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

}
