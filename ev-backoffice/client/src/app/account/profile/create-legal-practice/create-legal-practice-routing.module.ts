import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CreateLegalPracticeComponent } from './create-legal-practice.component';
import { ActiveMembershipGuardService } from '../../../core/guards/active-membership-guard.service';

export const routes: Routes = [
  {
    path: '',
    component: CreateLegalPracticeComponent,
    canActivate: [ActiveMembershipGuardService]
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
export class CreateLegalPracticeRoutingModule {
}
