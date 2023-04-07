import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PackageApplicantsComponent } from './package-applicants.component';

export const routes: Routes = [
  {
    path: '',
    component: PackageApplicantsComponent,
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
export class PackageApplicantsRoutingModule {
}
