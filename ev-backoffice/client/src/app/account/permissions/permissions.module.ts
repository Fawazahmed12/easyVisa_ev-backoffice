import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { InvitationRequestSentModule } from '../modals/invitation-request-sent/invitation-request-sent.module';

import { PermissionsRoutingModule } from './permissions-routing.module';
import { NgrxPermissionsModule } from './ngrx/module';
import { PermissionsService } from './permissions.service';
import { PermissionsResolverService } from './permissions-resolver.service';
import { EditUserResolverService } from './add-edit-user/edit-user-resolver.service';
import { WithdrawInviteModule } from './modals/withdraw-invite/withdraw-invite.module';

@NgModule({
  imports: [
    SharedModule,
    PermissionsRoutingModule,
    NgrxPermissionsModule,
    InvitationRequestSentModule,
    WithdrawInviteModule
  ],
  providers: [
    PermissionsService,
    PermissionsResolverService,
    EditUserResolverService
  ]
})
export class PermissionsModule {
}
