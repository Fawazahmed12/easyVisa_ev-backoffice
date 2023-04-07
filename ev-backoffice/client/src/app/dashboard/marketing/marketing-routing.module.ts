import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RoleGuardService } from '../../core/guards/role-guard.service';
import { Role } from '../../core/models/role.enum';
import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';
import { RepresentativeSelectedGuardService } from '../../core/guards/representative-selected-guard.service';

import { MarketingComponent } from './marketing.component';
import { MarketingResolverService } from './marketing-resolver.service';
import { FinancialPositionGuardService } from '../../core/guards/financial-position-guard.service';

const routes: Routes = [
  {
    path: '',
    component: MarketingComponent,
    resolve: [
      MarketingResolverService,
    ],
    canActivate: [
      RoleGuardService,
      RepresentativeSelectedGuardService,
      FinancialPositionGuardService,
    ],
    data: {
      roles: [Role.ROLE_ATTORNEY, Role.ROLE_EV, Role.ROLE_OWNER, Role.ROLE_EMPLOYEE],
      positions: [
        EmployeePosition.PARTNER,
        EmployeePosition.ATTORNEY,
      ]
    }
  },
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class MarketingRoutingModule {
}
