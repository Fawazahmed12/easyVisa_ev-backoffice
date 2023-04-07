import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

import { EmailTemplateVariablesModalComponent } from './email-template-variables-modal.component';
import { SpinnerModule } from '../../../components/spinner/spinner.module';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    SpinnerModule
  ],
  declarations: [
    EmailTemplateVariablesModalComponent,
  ],
  exports: [
    EmailTemplateVariablesModalComponent,
  ],
  entryComponents: [
    EmailTemplateVariablesModalComponent,
  ],
})

export class EmailTemplateVariablesModalModule {
}
