import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { RepresentativeTypePipeModule } from '../../../../../shared/pipes/representative-type/representative-type-pipe.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';
import { ApplicantsListModule } from '../../../../components/applicants-list/applicants-list.module';

import { UpdatePackageStatusModalComponent } from './update-package-status-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ApplicantsListModule,
    ModalHeaderModule,
    RepresentativeTypePipeModule,
  ],
  declarations: [
    UpdatePackageStatusModalComponent,
  ],
  exports: [
    UpdatePackageStatusModalComponent,
  ],
  entryComponents: [
    UpdatePackageStatusModalComponent,
  ],
})

export class UpdatePackageStatusModalModule {
}
