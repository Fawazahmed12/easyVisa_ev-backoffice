import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { InviteColleaguesComponent } from './invite-colleagues.component';
import { FeeDetailsResolverService } from '../../../core/resolvers/fee-details-resolver.service';


const routes: Routes = [
  {
    path: '',
    component: InviteColleaguesComponent,
    resolve: {
      feeDetails: FeeDetailsResolverService,
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InviteColleaguesRoutingModule {
}
