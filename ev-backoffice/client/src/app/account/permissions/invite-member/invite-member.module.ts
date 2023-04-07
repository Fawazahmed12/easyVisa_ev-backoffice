import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../../shared/shared.module';
import { VerifyMemberModule } from '../../../components/verify-member/verify-member.module';
import { OrganizationTypeModule } from '../../../shared/pipes/organization-type/organization-type.module';
import { RepresentativeTypePipeModule } from '../../../shared/pipes/representative-type/representative-type-pipe.module';

import { InviteMemberComponent } from './invite-member.component';
import { InviteMemberRoutingModule } from './invite-member-routing.module';
import { BlockModule } from '../../../shared/components/block/block.module';

@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    VerifyMemberModule,
    OrganizationTypeModule,
    RepresentativeTypePipeModule,
    InviteMemberRoutingModule,
    BlockModule
  ],
  declarations: [
    InviteMemberComponent,
  ],
  exports: [
    InviteMemberComponent,
  ]
})
export class InviteMemberModule {
}
