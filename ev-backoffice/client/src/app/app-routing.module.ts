import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegistrationFinishGuardService } from './core/guards';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { ActiveMembershipGuardService } from './core/guards/active-membership-guard.service';
import { UnpaidGuardService } from './core/guards/unpaid-guard.service';
import { NotFoundErrorComponent } from './components/not-found-error/not-found-error.component';
import { StoreUrlGuardService } from './core/guards/store-url-guard.service';

const routes: Routes = [
  { path: '', redirectTo: 'index', pathMatch: 'full' },
  { path: 'auth', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },
  {
    path: '',
    children: [
      {
        path: 'redirect-employee',
        loadChildren: () => import('./redirect-employee/redirect-employee.module').then(m => m.RedirectEmployeeModule)
      },
      {
        path: 'account',
        loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
      },
      {
        path: 'alerts',
        loadChildren: () => import('./alert-handling/alert-handling.module').then(m => m.AlertHandlingModule)
      },
      {
        path: 'documents',
        loadChildren: () => import('./documents/documents.module').then(m => m.DocumentsModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService
        ],
      },
      {
        path: 'task-queue',
        loadChildren: () => import('./task-queue/task-queue.module').then(m => m.TaskQueueModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService
        ]
      },
      {
        path: 'home',
        loadChildren: () => import('./home/home.module').then(m => m.HomeModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService,
        ],
      },
      {
        path: 'questionnaire',
        loadChildren: () => import('./questionnaire/questionnaire.module').then(m => m.QuestionnaireModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService,
        ],
      },
      {
        path: 'dashboard',
        loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule),
        canActivate: [
          ActiveMembershipGuardService,
          UnpaidGuardService,
        ],
      },
      {
        path: 'super-admin',
        loadChildren: () => import('./super-admin/super-admin.module').then(m => m.SuperAdminModule),
      },
    ],
    canActivate: [RegistrationFinishGuardService, StoreUrlGuardService],
    canActivateChild: [
      StoreUrlGuardService
    ],
  },
  {
    path: 'index',
    loadChildren: () => import('./index/index.module').then(m => m.IndexModule),
    canActivate: [RegistrationFinishGuardService, StoreUrlGuardService],
  },
  {
    path: '404',
    component: NotFoundErrorComponent,
  },
  { path: '**', component: NotFoundComponent, },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
