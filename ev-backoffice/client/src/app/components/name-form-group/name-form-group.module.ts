import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { NameFormGroupComponent } from './name-form-group.component';
import {
  HorizontalFormFieldModule
} from '../../task-queue/package/create-edit-package/components/horizontal-form-field/horizontal-form-field.module';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HorizontalFormFieldModule,
  ],
  declarations: [
    NameFormGroupComponent,
  ],
  exports: [
    NameFormGroupComponent,
  ]
})
export class NameFormGroupModule {
}
