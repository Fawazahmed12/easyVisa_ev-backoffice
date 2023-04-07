import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';

import { ChangeCaseStatusModalComponent } from './change-case-status-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    ChangeCaseStatusModalComponent,
  ],
  exports: [
    ChangeCaseStatusModalComponent,
  ],
  entryComponents: [
    ChangeCaseStatusModalComponent,
  ],
})

export class ChangeCaseStatusModalModule {
}
