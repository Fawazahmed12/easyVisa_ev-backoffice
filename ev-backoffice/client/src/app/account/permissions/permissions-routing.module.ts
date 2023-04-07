import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RoleAndAdminGuardService } from '../../core/guards/role-and-admin-guard.service';
import { Role } from '../../core/models/role.enum';

import { PermissionsResolverService } from './permissions-resolver.service';
import { EditUserResolverService } from './add-edit-user/edit-user-resolver.service';


export const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        loadChildren: () => import('./permissions-table/permissions-table.module').then(m => m.PermissionsTableModule),
        resolve: [
          PermissionsResolverService,
        ]
      },
      {
        path: 'invite-member',
        loadChildren: () => import('./invite-member/invite-member.module').then(m => m.InviteMemberModule),
        canActivate: [RoleAndAdminGuardService],
        data: {roles: [Role.ROLE_OWNER, Role.ROLE_EV, Role.ROLE_ATTORNEY]},
      },
      {
        path: 'add-user',
        loadChildren: () => import('./add-edit-user/add-edit-user.module').then(m => m.AddEditUserModule),
        canActivate: [RoleAndAdminGuardService],
        data: {roles: [Role.ROLE_OWNER, Role.ROLE_EV, Role.ROLE_ATTORNEY]},
      },
      {
        path: ':id',
        resolve: [
          EditUserResolverService
        ],
        canActivate: [RoleAndAdminGuardService],
        data: {roles: [Role.ROLE_OWNER, Role.ROLE_EV, Role.ROLE_ATTORNEY]},
        children: [
          {
            path: 'edit-user',
            loadChildren: () => import('./add-edit-user/add-edit-user.module').then(m => m.AddEditUserModule),
          },
        ],
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
export class PermissionsRoutingModule {
}
