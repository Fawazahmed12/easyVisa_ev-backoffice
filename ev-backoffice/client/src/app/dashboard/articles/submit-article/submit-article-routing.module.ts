import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SubmitArticleComponent } from './submit-article.component';
import { FeeDetailsResolverService } from '../../../core/resolvers/fee-details-resolver.service';

const routes: Routes = [
  {
    path: '',
    component: SubmitArticleComponent,
    resolve: {
      feeDetails: FeeDetailsResolverService,
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SubmitArticleRoutingModule {
}
