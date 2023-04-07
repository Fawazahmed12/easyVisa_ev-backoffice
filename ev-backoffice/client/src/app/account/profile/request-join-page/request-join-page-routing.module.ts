import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RequestJoinPageComponent } from './request-join-page.component';
import { ActiveMembershipGuardService } from '../../../core/guards/active-membership-guard.service';

export const routes: Routes = [
  {
    path: '',
    component: RequestJoinPageComponent,
    canActivate: [ActiveMembershipGuardService]
  },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class RequestJoinPageRoutingModule {
}
