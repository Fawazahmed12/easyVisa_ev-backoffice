import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { AuthFormFieldModule } from '../auth-form-field/auth-form-field.module';
import { AddressComponent } from './address.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    AuthFormFieldModule,
  ],
  declarations: [
    AddressComponent,
  ],
  exports: [
    AddressComponent,
  ]
})
export class AddressModule { }
