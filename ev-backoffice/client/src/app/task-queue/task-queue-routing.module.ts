import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { I18nResolverService } from '../core/i18n/i18n-resolver.service';
import { RoleGuardService } from '../core/guards/role-guard.service';
import { Role } from '../core/models/role.enum';
import { FeeDetailsResolverService } from '../core/resolvers/fee-details-resolver.service';
import { PositionGuardService } from '../core/guards/position-guard.service';
import { EmployeePosition } from '../account/permissions/models/employee-position.enum';
import { CreateEditPackageGuardService } from '../core/guards/create-edit-package-guard.service';

import { TaskQueueComponent } from './task-queue.component';
import { TaskQueueGuardService } from '../core/guards/task-queue-guard.service';
import { TaskQueueNotificationsResolverService } from './resolvers/task-queue-notifications-resolver.service';

export const routes: Routes = [
  {
    path: '',
    component: TaskQueueComponent,
    children: [
      {path: '', pathMatch: 'full', canActivate: [TaskQueueGuardService]},
      {path: 'alerts', loadChildren: () => import('../components/alerts/alerts.module').then(m => m.AlertsModule)},
      {path: 'clients', loadChildren: () => import('./clients/clients.module').then(m => m.ClientsModule)},
      {path: 'dispositions', loadChildren: () => import('./dispositions/dispositions.module').then(m => m.DispositionsModule)},
      {
        path: 'package',
        loadChildren: () => import('./package/package.module').then(m => m.PackageModule),
        canActivate: [PositionGuardService],
        canDeactivate: [CreateEditPackageGuardService],
        data: {
          positions: [
            EmployeePosition.PARTNER,
            EmployeePosition.ATTORNEY,
            EmployeePosition.MANAGER,
            EmployeePosition.EMPLOYEE,
          ]
        }
      },
      {path: 'warnings', loadChildren: () => import('./warnings/warnings.module').then(m => m.WarningsModule)},
      {path: 'additional-fees', loadChildren: () => import('./additional-fees/additional-fees.module').then(m => m.AdditionalFeesModule)},

    ],
    resolve: [
      I18nResolverService,
      FeeDetailsResolverService
    ],
    canActivate: [RoleGuardService],
    data: {
      translationUrl: 'task-queue-module',
      roles: [
        Role.ROLE_OWNER,
        Role.ROLE_ATTORNEY,
        Role.ROLE_EMPLOYEE,
        Role.ROLE_EV
      ]
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TaskQueueRoutingModule {
}
