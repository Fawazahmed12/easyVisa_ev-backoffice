import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ShowUsernameComponent } from './show-username.component';
import { ShowUsernameResolverService } from './show-username-resolver.service';

export const routes: Routes = [
  {
    path: '',
    component: ShowUsernameComponent,
    resolve: {
      showUsername: ShowUsernameResolverService,
    }
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
export class ShowUsernameRoutingModule {
}
