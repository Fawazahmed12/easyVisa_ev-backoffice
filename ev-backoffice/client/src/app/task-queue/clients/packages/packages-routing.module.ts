import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PackagesComponent } from './packages.component';
import { PackagesResolverService } from './packages-resolver.service';

export const routes: Routes = [
  {
    path: '',
    component: PackagesComponent,
    resolve: [
      PackagesResolverService,
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
export class PackagesRoutingModule {
}
