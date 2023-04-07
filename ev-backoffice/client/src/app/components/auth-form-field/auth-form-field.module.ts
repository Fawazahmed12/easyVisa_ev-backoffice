import { NgModule } from '@angular/core';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { AuthFormFieldComponent } from './auth-form-field.component';
import { AuthFormFieldErrorComponent } from './auth-form-field-error.component';

@NgModule({
  imports: [
    SharedModule,
    NgbTooltipModule,
  ],
  exports: [
    AuthFormFieldComponent,
    AuthFormFieldErrorComponent,
  ],
  declarations: [
    AuthFormFieldComponent,
    AuthFormFieldErrorComponent,
  ]
})
export class AuthFormFieldModule {
}
