import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { VerifyRegistrationComponent } from './verify-registration.component';
import { VerifyRegistrationGuardService } from './verify-registration-guard.service';


export const routes: Routes = [
  {
    path: '',
    component: VerifyRegistrationComponent,
    canActivate: [ VerifyRegistrationGuardService ],
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
export class VerifyRegistrationRoutingModule {
}
