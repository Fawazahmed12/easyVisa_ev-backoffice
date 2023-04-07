import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FeeDetailsResolverService } from '../../../core/resolvers/fee-details-resolver.service';

import { EditPreviewProfileComponent } from './edit-preview-profile.component';
import { ProfileResolverService } from './resolvers/profile-resolver.service';
import { OrganizationResolverService } from './resolvers/organization-resolver.service';
import { MyReviewsResolverService } from './resolvers/my-reviews-resolver.service';
import { MyRatingsResolverService } from './resolvers/my-rating-resolver.service';

export const routes: Routes = [
  {
    path: '',
    component: EditPreviewProfileComponent,
    resolve: [
      ProfileResolverService,
      OrganizationResolverService,
      FeeDetailsResolverService,
      MyReviewsResolverService,
      MyRatingsResolverService
    ],
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
export class EditPreviewProfileRoutingModule {
}
