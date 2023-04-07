import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

import { MembersOfBlockedOrOpenPackageModalComponent } from './members-of-blocked-or-open-package-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    MembersOfBlockedOrOpenPackageModalComponent,
  ],
  exports: [
    MembersOfBlockedOrOpenPackageModalComponent,
  ],
  entryComponents: [
    MembersOfBlockedOrOpenPackageModalComponent,
  ]
})

export class MembersOfBlockedOrOpenPackageModalModule {
}
