import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EmailTemplatesResolverService } from '../../core/resolvers/email-templates-resolver.service';
import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';

import { EmailTemplatesComponent } from './email-templates.component';

export const emailTemplateTypes = [
  EmailTemplateTypes.NEW_CLIENT,
  EmailTemplateTypes.UPDATED_CLIENT,
  EmailTemplateTypes.INVITE_APPLICANT,
  EmailTemplateTypes.INVITE_COLLEAGUE_TO_EASYVISA,
  EmailTemplateTypes.DOCUMENT_REJECTION_NOTIFICATION,
  EmailTemplateTypes.ADDITIONAL_FEES,
  EmailTemplateTypes.RETAINER_AGREEMENT_NEW,
  EmailTemplateTypes.RETAINER_AGREEMENT_UPDATED,
  EmailTemplateTypes.COVER_LETTER_NEW,
  EmailTemplateTypes.COVER_LETTER_UPDTAED,
  EmailTemplateTypes.CLOSING_TEXT,
  EmailTemplateTypes.NEW_EMPLOYEE_REGISTRATION_INVITE,
  EmailTemplateTypes.USCIS_FORM_EDITION_UPDATE_TO_APPLICANTS,
  EmailTemplateTypes.USCIS_FORM_EDITION_UPDATE_TO_ORG_MEMBERS,
];

export const routes: Routes = [
  {
    path: '',
    component: EmailTemplatesComponent,
    resolve: [
      EmailTemplatesResolverService,
    ],
    data: {emailTemplateTypes}
  },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class EmailTemplatesRoutingModule {
}
