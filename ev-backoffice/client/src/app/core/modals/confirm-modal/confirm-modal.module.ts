import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ConfirmModalComponent } from './confirm-modal.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    ConfirmModalComponent,
  ],
  exports: [
    ConfirmModalComponent,
  ],
  entryComponents: [
    ConfirmModalComponent,
  ],
})
export class ConfirmModalModule {
}
