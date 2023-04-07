import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';
import {
  AdditionalApplicantFeeInfoModule
} from '../../../../components/additional-applicant-fee-info/additional-applicant-fee-info.module';

import { CancelModificationsModalModule } from '../modals/cancel-modifications-modal/cancel-modifications-modal.module';

import { DispositionPackageComponent } from './disposition-package.component';
import { MODALS } from './modals';

@NgModule({
  imports: [
    SharedModule,
    AdditionalApplicantFeeInfoModule,
    CancelModificationsModalModule,
  ],
  declarations: [
    DispositionPackageComponent,
    MODALS,
  ],
  exports: [
    DispositionPackageComponent,
  ]
})
export class DispositionPackageModule {
}
