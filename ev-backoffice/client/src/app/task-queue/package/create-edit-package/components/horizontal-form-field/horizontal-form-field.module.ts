import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';

import { HorizontalFormFieldComponent } from './horizontal-form-field.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  exports: [
    HorizontalFormFieldComponent,
  ],
  declarations: [
    HorizontalFormFieldComponent,
  ]
})
export class HorizontalFormFieldModule {
}
