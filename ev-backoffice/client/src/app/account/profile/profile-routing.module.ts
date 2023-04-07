import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RoleGuardService } from '../../core/guards/role-guard.service';
import { Role } from '../../core/models/role.enum';


export const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        loadChildren: () => import('./edit-preview-profile/edit-preview-profile.module').then(m => m.EditPreviewProfileModule),
      },
      {
        path: 'invite-attorney-to-create-new-law-firm',
        loadChildren: () => import('./create-legal-practice/create-legal-practice.module').then(m => m.CreateLegalPracticeModule),
        canActivate: [RoleGuardService],
        data: {
          roles: [
            Role.ROLE_OWNER,
            Role.ROLE_EV,
            Role.ROLE_ATTORNEY,
            Role.ROLE_EMPLOYEE
          ]
        }
      },
      {
        path: 'request-to-join',
        loadChildren: () => import('./request-join-page/request-join-page.module').then(m => m.RequestJoinPageModule),
        canActivate: [RoleGuardService],
        data: {
          roles: [
            Role.ROLE_OWNER,
            Role.ROLE_EV,
            Role.ROLE_ATTORNEY,
            Role.ROLE_EMPLOYEE
          ]
        }
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
export class ProfileRoutingModule {
}
