import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Role } from '../../core/models/role.enum';
import { RoleAndAdminGuardService } from '../../core/guards/role-and-admin-guard.service';

import { NotificationsRemindersComponent } from './notifications-reminders.component';


export const routes: Routes = [
  {
    path: '',
    component: NotificationsRemindersComponent,
    canActivate: [ RoleAndAdminGuardService ],
    data: {roles: [Role.ROLE_OWNER, Role.ROLE_ATTORNEY, Role.ROLE_EMPLOYEE ]},
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
export class NotificationsRemindersRoutingModule {
}
