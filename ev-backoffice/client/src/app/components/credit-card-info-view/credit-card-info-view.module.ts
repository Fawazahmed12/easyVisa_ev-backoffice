import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { CreditCardInfoViewComponent } from './credit-card-info-view.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
  ],
  declarations: [
    CreditCardInfoViewComponent,
  ],
  exports: [
    CreditCardInfoViewComponent,
  ]
})
export class CreditCardInfoViewModule { }
