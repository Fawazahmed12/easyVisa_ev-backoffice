import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MyPackagesResolverService } from '../../core/resolvers/my-packages-resolver.service';

import { WriteReviewComponent } from './write-review.component';

export const routes: Routes = [
  {
    path: '',
    component: WriteReviewComponent,
    resolve: [
      MyPackagesResolverService
    ]
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
export class WriteReviewRoutingModule {
}
