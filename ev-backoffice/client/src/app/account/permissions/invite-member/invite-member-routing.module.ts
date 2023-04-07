import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { InviteMemberComponent } from './invite-member.component';

const routes: Routes = [
  {
    path: '',
    component: InviteMemberComponent,
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class InviteMemberRoutingModule {
}
