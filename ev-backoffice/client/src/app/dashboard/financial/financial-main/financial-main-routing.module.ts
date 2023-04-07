import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FinancialMainComponent } from './financial-main.component';
import { FeeDetailsResolverService } from '../../../core/resolvers/fee-details-resolver.service';

const routes: Routes = [
  {
    path: '',
    component: FinancialMainComponent,
    resolve: {
      feeDetails: FeeDetailsResolverService,
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FinancialMainRoutingModule {
}
