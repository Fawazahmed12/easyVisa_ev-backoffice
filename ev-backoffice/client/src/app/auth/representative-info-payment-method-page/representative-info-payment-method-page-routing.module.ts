import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FeeDetailsResolverService } from '../../core/resolvers/fee-details-resolver.service';

import { RepresentativeInfoPaymentMethodPageComponent } from './representative-info-payment-method-page.component';


export const routes: Routes = [
  {
    path: '',
    component: RepresentativeInfoPaymentMethodPageComponent,
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
export class RepresentativeInfoPaymentMethodPageRoutingModule {
}
