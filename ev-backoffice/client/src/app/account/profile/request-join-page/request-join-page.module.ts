import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../../shared/shared.module';
import { RepresentativeTypePipeModule } from '../../../shared/pipes/representative-type/representative-type-pipe.module';
import { BlockModule } from '../../../shared/components/block/block.module';
import { VerifyMemberModule } from '../../../components/verify-member/verify-member.module';
import { OrganizationTypeModule } from '../../../shared/pipes/organization-type/organization-type.module';

import { InvitationRequestSentModule } from '../../modals/invitation-request-sent/invitation-request-sent.module';

import { RequestJoinPageComponent } from './request-join-page.component';
import { RequestJoinPageRoutingModule } from './request-join-page-routing.module';
import { RequestJoinPageService } from './request-join-page.service';


@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    RequestJoinPageRoutingModule,
    BlockModule,
    VerifyMemberModule,
    RepresentativeTypePipeModule,
    OrganizationTypeModule,
    InvitationRequestSentModule
  ],
  declarations: [
    RequestJoinPageComponent,
  ],
  exports: [
    RequestJoinPageComponent,
  ],
  providers: [
    RequestJoinPageService
  ]
})
export class RequestJoinPageModule { }
