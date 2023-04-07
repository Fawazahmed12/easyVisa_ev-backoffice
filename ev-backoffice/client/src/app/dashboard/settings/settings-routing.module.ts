import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { FeeDetailsResolverService } from '../../core/resolvers/fee-details-resolver.service';
import { RoleGuardService } from '../../core/guards/role-guard.service';
import { Role } from '../../core/models/role.enum';
import { FeeScheduleSettingsResolverService } from '../../core/resolvers/fee-schedule-settings-resolver.service';
import { GovernmentFeeResolverService } from '../../core/resolvers/government-fee-resolver.service';

import { BatchJobsResolverService } from './resolvers/batch-jobs.resolver.service';
import { SettingsComponent } from './settings.component';


const routes: Routes = [
  {
    path: '',
    component: SettingsComponent,
    resolve: {
      feeDetails: FeeDetailsResolverService,
      feeScheduleSettings: FeeScheduleSettingsResolverService,
      governmentFee: GovernmentFeeResolverService,
      batchJobs: BatchJobsResolverService,
    },
    canActivate: [ RoleGuardService ],
    data: {roles: [Role.ROLE_OWNER]}
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class SettingsRoutingModule {
}
