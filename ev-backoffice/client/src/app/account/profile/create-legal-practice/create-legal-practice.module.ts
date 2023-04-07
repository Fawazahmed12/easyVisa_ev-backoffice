import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BlockModule } from '../../../shared/components/block/block.module';
import { SharedModule } from '../../../shared/shared.module';
import { VerifyMemberModule } from '../../../components/verify-member/verify-member.module';
import { OrganizationTypeModule } from '../../../shared/pipes/organization-type/organization-type.module';
import { RepresentativeTypePipeModule } from '../../../shared/pipes/representative-type/representative-type-pipe.module';

import { InviteRequestService } from '../../services/invite-request.service';
import { InvitationRequestSentModule } from '../../modals/invitation-request-sent/invitation-request-sent.module';

import { CreateLegalPracticeComponent } from './create-legal-practice.component';
import { CreateLegalPracticeRoutingModule } from './create-legal-practice-routing.module';


@NgModule({
  imports: [
    BlockModule,
    SharedModule,
    CommonModule,
    VerifyMemberModule,
    OrganizationTypeModule,
    InvitationRequestSentModule,
    RepresentativeTypePipeModule,
    CreateLegalPracticeRoutingModule,
  ],
  declarations: [
    CreateLegalPracticeComponent,
  ],
  exports: [
    CreateLegalPracticeComponent,
  ],
  providers: [
    InviteRequestService,
  ]
})
export class CreateLegalPracticeModule { }
