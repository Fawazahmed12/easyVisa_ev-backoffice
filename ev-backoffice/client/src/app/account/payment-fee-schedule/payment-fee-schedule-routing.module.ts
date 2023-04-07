import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Role } from '../../core/models/role.enum';
import { RoleAndAdminGuardService } from '../../core/guards/role-and-admin-guard.service';
import { FeeScheduleResolverService } from '../../core/resolvers/fee-schedule-resolver.service';

import { PaymentFeeScheduleComponent } from './payment-fee-schedule.component';

export const routes: Routes = [
  {
    path: '',
    component: PaymentFeeScheduleComponent,
    canActivate: [ RoleAndAdminGuardService ],
    data: {roles: [Role.ROLE_OWNER, Role.ROLE_ATTORNEY, Role.ROLE_EMPLOYEE]},
    resolve: [
      FeeScheduleResolverService,
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
export class PaymentFeeScheduleRoutingModule {
}
