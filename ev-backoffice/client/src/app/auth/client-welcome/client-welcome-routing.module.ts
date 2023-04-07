import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ClientWelcomeComponent } from './client-welcome.component';

export const routes: Routes = [
  {
    path: '',
    component: ClientWelcomeComponent,
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
export class ClientWelcomeRoutingModule {
}
