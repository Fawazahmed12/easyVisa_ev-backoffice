import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { InviteColleaguesComponent } from './invite-colleagues.component';
import { InviteColleaguesRoutingModule } from './invite-colleagues.routing.module';
import { SpinnerModule } from '../../../components/spinner/spinner.module';

@NgModule({
  imports: [
    SharedModule,
    InviteColleaguesRoutingModule,
    SpinnerModule,
  ],
  declarations: [
    InviteColleaguesComponent
  ],
  exports: [
    InviteColleaguesComponent
  ]
})
export class InviteColleaguesModule {
}
