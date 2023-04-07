import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { MembershipModalModule } from './membership-modal/membership-modal.module';
import { ChangeMembershipStatusComponent } from './change-membership-status.component';


@NgModule({
  imports: [
    SharedModule,
    MembershipModalModule,
  ],
  declarations: [
    ChangeMembershipStatusComponent,
  ],
  exports: [
    ChangeMembershipStatusComponent,
  ]
})

export class ChangeMembershipStatusModule {
}
