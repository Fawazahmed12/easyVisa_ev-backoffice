import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RoleGuardService } from '../../core/guards/role-guard.service';
import { Role } from '../../core/models/role.enum';

import { AdditionalFeesComponent } from './additional-fees.component';
import { GovernmentFeeResolverService } from '../../core/resolvers/government-fee-resolver.service';
import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';
import { PositionGuardService } from '../../core/guards/position-guard.service';


const routes: Routes = [
  {
    path: '',
    component: AdditionalFeesComponent,
    canActivate: [
      RoleGuardService,
      PositionGuardService,
    ],
    data: {
      roles: [Role.ROLE_ATTORNEY, Role.ROLE_EV, Role.ROLE_OWNER, Role.ROLE_EMPLOYEE],
      positions: [
        EmployeePosition.PARTNER,
        EmployeePosition.ATTORNEY,
        EmployeePosition.MANAGER,
        EmployeePosition.EMPLOYEE,
      ]
    },
    resolve: [
      GovernmentFeeResolverService,
    ]
  },
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class AdditionalFeesRoutingModule {
}
