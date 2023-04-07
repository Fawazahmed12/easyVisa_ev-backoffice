import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PaySignUpFeeComponent } from './pay-sign-up-fee.component';
import { FeeDetailsResolverService } from '../../core/resolvers/fee-details-resolver.service';


export const routes: Routes = [
  {
    path: '',
    component: PaySignUpFeeComponent,
    resolve: {
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
export class PaySignUpFeeRoutingModule {
}
