import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';

import { PackageResolverService } from '../resolvers/package-resolver.service';

import { PackageComponent } from './package.component';
import { FeeScheduleSettingsResolverService } from '../../core/resolvers/fee-schedule-settings-resolver.service';

export const routes: Routes = [
    {
      path: '',
      component: PackageComponent,
      children: [
        { path: '', redirectTo: 'create', pathMatch: 'full' },
        {
          path: 'create',
          loadChildren: () => import('./create-edit-package/create-edit-package.module')
            .then(m => m.CreateEditPackageModule),
        },
        {
          path: ':id',
          resolve: [
            PackageResolverService,
            FeeScheduleSettingsResolverService,
          ],
          children: [
            {
              path: 'edit',
              loadChildren: () => import('./create-edit-package/create-edit-package.module')
                .then(m => m.CreateEditPackageModule),
            },
            {
              path: 'welcome-email',
              loadChildren: () => import('../email-to-package-applicants/email-to-package-applicants.module')
                .then(m => m.EmailToPackageApplicantsModule),
              data: {
                emailTemplateType: EmailTemplateTypes.NEW_CLIENT,
              },
            },
            {
              path: 'updated-package-email',
              loadChildren: () => import('../email-to-package-applicants/email-to-package-applicants.module')
                .then(m => m.EmailToPackageApplicantsModule),
              data: {
                emailTemplateType: EmailTemplateTypes.UPDATED_CLIENT,
              },
            },
          ]
        },
      ]
    }
  ]
;

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class PackageRoutingModule {
}
