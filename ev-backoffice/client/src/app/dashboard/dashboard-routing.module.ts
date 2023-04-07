import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { I18nResolverService } from '../core/i18n/i18n-resolver.service';

import { DashboardComponent } from './dashboard.component';
import { DashboardGuardService } from '../core/guards/dashboard-guard.service';
import { RoleGuardService } from '../core/guards/role-guard.service';
import { Role } from '../core/models/role.enum';
import { AffiliatedOrganizationsResolverService } from '../core/resolvers/affiliated-organizations-resolver.service';
import { ClientNotificationsResolverService } from '../core/resolvers/client-notifications-resolver.service';

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      { path: '', pathMatch: 'full', canActivate: [DashboardGuardService]},
      { path: 'progress-status', loadChildren: () => import('./progress-status/progress-status.module').then(m => m.ProgressStatusModule) },
      { path: 'settings', loadChildren: () => import('./settings/settings.module').then(m => m.SettingsModule) },
      { path: 'articles', loadChildren: () => import('./articles/articles.module').then(m => m.ArticlesModule) },
      { path: 'financial', loadChildren: () => import('./financial/financial.module').then(m => m.FinancialModule) },
      { path: 'marketing', loadChildren: () => import('./marketing/marketing.module').then(m => m.MarketingModule) },
      { path: 'tutorials', loadChildren: () => import('./tutorials/tutorials.module').then(m => m.TutorialsModule) },
      {
        path: 'uscis-edition-dates',
        loadChildren: () => import('./uscis-edition-dates/uscis-edition-dates.module').then(m => m.UscisEditionDatesModule)
      },
      {
        path: 'alerts',
        loadChildren: () => import('../components/alerts/alerts.module').then(m => m.AlertsModule),
        canActivate: [RoleGuardService],
        data: {roles: [Role.ROLE_USER]}
      },
      { path: 'process-links', loadChildren: () => import('./process-links/process-links.module').then(m => m.ProcessLinksModule) },
      { path: 'process-links', loadChildren: () => import('./process-links/process-links.module').then(m => m.ProcessLinksModule) },
    ],
    resolve: {
      translation: I18nResolverService,
      affiliatedOrganizations: AffiliatedOrganizationsResolverService,
      clientNotifications: ClientNotificationsResolverService
    },
    data: {
      translationUrl: 'dashboard-module',
    },
  },
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class DashboardRoutingModule {
}
