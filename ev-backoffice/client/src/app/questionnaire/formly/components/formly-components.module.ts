import { NgModule } from '@angular/core';
import { TranslateValidationMessageComponent } from './translate-validation-message.component';
import { SharedModule } from '../../../shared/shared.module';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    TranslateValidationMessageComponent,
  ],
  exports: [
    TranslateValidationMessageComponent,
  ],
})
export class FormlyComponentsModule {
}
