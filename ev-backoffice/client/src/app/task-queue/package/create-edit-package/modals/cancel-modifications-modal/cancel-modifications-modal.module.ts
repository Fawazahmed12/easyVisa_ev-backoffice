import { NgModule } from '@angular/core';

import { CancelModificationsModalComponent } from './cancel-modifications-modal.component';

import { SharedModule } from '../../../../../shared/shared.module';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    CancelModificationsModalComponent,
  ],
  exports: [
    CancelModificationsModalComponent,
  ],
  entryComponents: [
    CancelModificationsModalComponent,
  ],
})
export class CancelModificationsModalModule {
}
