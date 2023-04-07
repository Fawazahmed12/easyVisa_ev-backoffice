import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

import { InviteRequestService } from '../../services/invite-request.service';

import { InvitationRequestSentComponent } from './invitation-request-sent.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule
  ],
  declarations: [
    InvitationRequestSentComponent,
  ],
  exports: [
    InvitationRequestSentComponent,
  ],
  entryComponents: [
    InvitationRequestSentComponent,
  ],
  providers: [
    InviteRequestService,
  ]
})

export class InvitationRequestSentModule {
}
