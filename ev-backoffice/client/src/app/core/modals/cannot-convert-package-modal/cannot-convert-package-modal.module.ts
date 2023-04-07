import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

import { CannotConvertPackageModalComponent } from './cannot-convert-package-modal.component';
import { RepresentativeTypePipeModule } from '../../../shared/pipes/representative-type/representative-type-pipe.module';
import { ApplicantTypePipeModule } from '../../../task-queue/pipes/applicantTypePipe.module';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    RepresentativeTypePipeModule,
    ApplicantTypePipeModule,
  ],
  declarations: [
    CannotConvertPackageModalComponent,
  ],
  exports: [
    CannotConvertPackageModalComponent,
  ],
  entryComponents: [
    CannotConvertPackageModalComponent,
  ],
})

export class CannotConvertPackageModalModule {
}
