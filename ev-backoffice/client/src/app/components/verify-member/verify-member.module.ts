import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { VerifyMemberComponent } from './verify-member.component';


@NgModule({
  imports: [
    SharedModule,
    CommonModule,
  ],
  declarations: [
    VerifyMemberComponent,
  ],
  exports: [
    VerifyMemberComponent,
  ]
})
export class VerifyMemberModule { }
