import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FeeDetailsResolverService } from '../../core/resolvers/fee-details-resolver.service';
import { RoleGuardService } from '../../core/guards/role-guard.service';
import { Role } from '../../core/models/role.enum';
import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';
import { RepresentativeSelectedGuardService } from '../../core/guards/representative-selected-guard.service';
import { FinancialPositionGuardService } from '../../core/guards/financial-position-guard.service';

import { FinancialResolverService } from './financial-resolver.service';


export const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        loadChildren: () => import('./financial-main/financial-main.module').then(m => m.FinancialMainModule),
        resolve: [
          FeeDetailsResolverService,
          FinancialResolverService,
        ],
        canActivate: [
          RoleGuardService,
          FinancialPositionGuardService,
          RepresentativeSelectedGuardService,
        ],
        data: {
          roles: [Role.ROLE_EV, Role.ROLE_ATTORNEY, Role.ROLE_OWNER, Role.ROLE_EMPLOYEE],
          positions: [
            EmployeePosition.PARTNER,
            EmployeePosition.ATTORNEY,
          ],
        },
      },
      {
        path: 'invite-colleagues',
        loadChildren: () => import('./invite-colleagues/invite-colleagues.module').then(m => m.InviteColleaguesModule),
      },
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
export class FinancialRoutingModule {
}
