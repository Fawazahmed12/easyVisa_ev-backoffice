import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';
import { AccessDeniedForTraineeModalComponent } from './access-denied-for-trainee-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    CommonModule
  ],
  declarations: [
    AccessDeniedForTraineeModalComponent,
  ],
  exports: [
    AccessDeniedForTraineeModalComponent,
  ],
  entryComponents: [
    AccessDeniedForTraineeModalComponent,
  ],
})
export class AccessDeniedForTraineeModalModule {
}
