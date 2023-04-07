import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ReferringUserResolverService } from '../resolvers/referring-user-resolver.service';

import { AttorneySignUpComponent } from './attorney-sign-up.component';

const routes: Routes = [
  {
    path: '',
    component: AttorneySignUpComponent,
    resolve: {
      referringUser: ReferringUserResolverService,
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AttorneySignUpRoutingModule { }
