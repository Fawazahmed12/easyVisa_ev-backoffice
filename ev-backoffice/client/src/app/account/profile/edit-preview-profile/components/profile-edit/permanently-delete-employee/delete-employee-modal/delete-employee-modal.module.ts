import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../../../components/modal-header/modal-header.module';
import { DeleteEmployeeModalComponent } from './delete-employee-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    DeleteEmployeeModalComponent,
  ],
  exports: [
    DeleteEmployeeModalComponent,
  ],
  entryComponents: [
    DeleteEmployeeModalComponent,
  ]
})

export class DeleteEmployeeModalModule {
}
