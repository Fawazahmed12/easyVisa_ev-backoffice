import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { PasswordMeterComponent } from './password-meter.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [
    PasswordMeterComponent,
  ],
  exports: [
    PasswordMeterComponent,
  ]
})
export class PasswordMeterModule { }
