import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { SelectNameModule } from '../../components/select-name/select-name.module';

import { EmailTemplatesComponent } from './email-templates.component';
import { EmailTemplatesRoutingModule } from './email-templates-routing.module';
import { EmailTemplateEditorModule } from './components/email-template-editor/email-template-editor.module';
import { UscisEditionDatesEditorModule } from './components/uscis-edition-dates-editor/uscis-edition-dates-editor.module';
import { UscisDatesFormGroupService } from './services/uscis-dates-form-group.service';

@NgModule({
  imports: [
    SharedModule,
    EmailTemplatesRoutingModule,
    EmailTemplateEditorModule,
    SelectNameModule,
    UscisEditionDatesEditorModule
  ],
  declarations: [
    EmailTemplatesComponent,
  ],
  providers: [
    UscisDatesFormGroupService
  ]
})
export class EmailTemplatesModule {
}
