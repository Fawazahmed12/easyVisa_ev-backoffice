import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AttorneyLandingPageComponent } from './attorney-landing-page.component';
import { ReferringUserResolverService } from '../resolvers/referring-user-resolver.service';
import { FeeDetailsResolverService } from '../../core/resolvers/fee-details-resolver.service';

export const routes: Routes = [
  {
    path: '',
    component: AttorneyLandingPageComponent,
    resolve: {
      referringUser: ReferringUserResolverService,
      feeDetails: FeeDetailsResolverService,
    }
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
export class AttorneyLandingPageRoutingModule {
}
