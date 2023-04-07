import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';
import { FindLabelPipeModule } from '../../shared/pipes/find-label/find-label-pipe.module';

import { TaxesInfoModule } from '../taxes-info/taxes-info.module';

import { AdditionalApplicantFeeInfoComponent } from './additional-applicant-fee-info.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FindLabelPipeModule,
    TaxesInfoModule
  ],
  declarations: [
    AdditionalApplicantFeeInfoComponent,
  ],
  exports: [
    AdditionalApplicantFeeInfoComponent,
  ]
})
export class AdditionalApplicantFeeInfoModule { }
