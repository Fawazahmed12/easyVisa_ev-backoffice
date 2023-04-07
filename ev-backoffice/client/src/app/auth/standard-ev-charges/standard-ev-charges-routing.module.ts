import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { StandardEvChargesComponent } from './standard-ev-charges.component';
import { FeeDetailsResolverService } from '../../core/resolvers/fee-details-resolver.service';
import { FeeScheduleSettingsResolverService } from '../../core/resolvers/fee-schedule-settings-resolver.service';
import { BenefitsResolverService } from '../../core/resolvers/benefits-resolver.service';

export const routes: Routes = [
  {
    path: '',
    component: StandardEvChargesComponent,
    resolve: {
      feeDetails: FeeDetailsResolverService,
      feeScheduleSettings: FeeScheduleSettingsResolverService,
      benefits: BenefitsResolverService
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
export class StandardEvChargesRoutingModule {
}
