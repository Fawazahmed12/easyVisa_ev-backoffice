import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MyPackagesResolverService } from '../../core/resolvers/my-packages-resolver.service';

import { LegalRepresentativeComponent } from './legal-representative.component';


export const routes: Routes = [
  {
    path: '',
    component: LegalRepresentativeComponent,
    resolve: [
      MyPackagesResolverService
    ]
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
export class LegalRepresentativeRoutingModule {
}
