import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../../../../shared/pipes/find-label/find-label-pipe.module';

import { LicenseFormComponent } from './license-form.component';
import { NumberOfYearsPipeModule } from './pipes/number-of-years-pipe.module';
import { AddBarAdmissionModalModule } from './add-bar-admission-modal/add-bar-admission-modal.module';

@NgModule({
  imports: [
    SharedModule,
    FindLabelPipeModule,
    NumberOfYearsPipeModule,
    AddBarAdmissionModalModule,
  ],
  declarations: [
    LicenseFormComponent,
  ],
  exports: [
    LicenseFormComponent,
  ]
})

export class LicenseFormModule {
}
