import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AttorneyWelcomeComponent } from './attorney-welcome.component';

export const routes: Routes = [
  {
    path: '',
    component: AttorneyWelcomeComponent,
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
export class AttorneyWelcomeRoutingModule {
}
