import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../components/modal-header/modal-header.module';

import { WithdrawInviteComponent } from './withdraw-invite.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule
  ],
  declarations: [
    WithdrawInviteComponent,
  ],
  exports: [
    WithdrawInviteComponent,
  ],
  entryComponents: [
    WithdrawInviteComponent,
  ],
})

export class WithdrawInviteModule {
}
