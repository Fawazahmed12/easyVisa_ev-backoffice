import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../../../components/modal-header/modal-header.module';
import { DatepickerGroupModule } from '../../../../../../../components/datepicker-group/datepicker-group.module';

import { AddBarAdmissionModalComponent } from './add-bar-admission-modal.component';

@NgModule({
  imports: [
    SharedModule,
    DatepickerGroupModule,
    ModalHeaderModule,
  ],
  declarations: [
    AddBarAdmissionModalComponent,
  ],
  exports: [
    AddBarAdmissionModalComponent,
  ],
  entryComponents: [
    AddBarAdmissionModalComponent,
  ],
})

export class AddBarAdmissionModalModule {
}
