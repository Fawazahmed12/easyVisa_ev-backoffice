import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';
import { RepresentativeTypePipeModule } from '../../../../../../shared/pipes/representative-type/representative-type-pipe.module';
import { OrganizationTypeModule } from '../../../../../../shared/pipes/organization-type/organization-type.module';
import {
  IfActiveOrganizationDirectiveModule
} from '../../../../../../shared/directives/if-active-organization/if-active-organization-directive.module';

import { MembershipComponent } from './membership.component';
import { ImportantMessageModalModule } from './important-message-modal/important-message-modal.module';
import { MembershipService } from './membership.service';
import { InviteRequestService } from '../../../../../services/invite-request.service';

@NgModule({
  imports: [
    SharedModule,
    RepresentativeTypePipeModule,
    IfActiveOrganizationDirectiveModule,
    ImportantMessageModalModule,
    OrganizationTypeModule
  ],
  declarations: [
    MembershipComponent,
  ],
  exports: [
    MembershipComponent,
  ],
  providers: [
    MembershipService,
    InviteRequestService,
  ]
})

export class MembershipModule {
}
