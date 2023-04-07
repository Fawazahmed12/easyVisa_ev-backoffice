import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EmailToPackageApplicantsComponent } from './email-to-package-applicants.component';
import { FeeScheduleSettingsResolverService } from '../../core/resolvers/fee-schedule-settings-resolver.service';

export const routes: Routes = [
  {
    path: '',
    component: EmailToPackageApplicantsComponent,
    resolve: [FeeScheduleSettingsResolverService],
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
export class EmailToPackageApplicantsRoutingModule {
}
