import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { CreditCardInfoComponent } from './credit-card-info.component';
import { AuthFormFieldModule } from '../auth-form-field/auth-form-field.module';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    AuthFormFieldModule,
    NgbTooltipModule,
  ],
  declarations: [
    CreditCardInfoComponent,
  ],
  exports: [
    CreditCardInfoComponent,
  ]
})
export class CreditCardInfoModule { }
