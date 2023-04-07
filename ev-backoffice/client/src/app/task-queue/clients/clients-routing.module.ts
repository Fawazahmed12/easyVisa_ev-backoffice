import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';

import { ClientsPackageResolverService } from '../resolvers/clients-package-resolver.service';

export const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        loadChildren: () => import('./packages/packages.module').then(m => m.PackagesModule),
      },
      {
        path: ':id',
        resolve: [
          ClientsPackageResolverService,
        ],
        children: [
          {
            path: 'invitation-to-register',
            loadChildren: () =>
              import('../email-to-package-applicants/email-to-package-applicants.module')
              .then(m => m.EmailToPackageApplicantsModule),
            data: {
              emailTemplateType: EmailTemplateTypes.INVITE_APPLICANT,
            },
          },
          {
            path: 'uscis-package-applicants',
            loadChildren: () => import('./package-applicants/package-applicants.module').then(m => m.PackageApplicantsModule),
          },
        ]
      }
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
export class ClientsRoutingModule {
}
