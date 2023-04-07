import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../../../components/modal-header/modal-header.module';
import { IfActiveUserDirectiveModule } from '../../../../../../../shared/directives/if-active-user/if-active-user-directive.module';
import { TaxesInfoModule } from '../../../../../../../components/taxes-info/taxes-info.module';

import { MembershipModalComponent } from './membership-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    IfActiveUserDirectiveModule,
    TaxesInfoModule,
  ],
  declarations: [
    MembershipModalComponent,
  ],
  exports: [
    MembershipModalComponent,
  ],
  entryComponents: [
    MembershipModalComponent,
  ]
})

export class MembershipModalModule {
}
