import { NgModule } from '@angular/core';

import { ModalHeaderModule } from '../../../../components/modal-header/modal-header.module';
import { SharedModule } from '../../../../shared/shared.module';

import { EditAmountOwedComponent } from './edit-amount-owed.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    EditAmountOwedComponent,
  ],
  exports: [
    EditAmountOwedComponent,
  ],
})
export class EditAmountOwedModule {
}
