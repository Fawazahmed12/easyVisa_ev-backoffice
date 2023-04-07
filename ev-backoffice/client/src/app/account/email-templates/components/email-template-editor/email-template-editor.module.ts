import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';

import { EmailTemplateEditorComponent } from './email-template-editor.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    EmailTemplateEditorComponent,
  ],
  exports: [
    EmailTemplateEditorComponent,
  ]
})
export class EmailTemplateEditorModule {
}
