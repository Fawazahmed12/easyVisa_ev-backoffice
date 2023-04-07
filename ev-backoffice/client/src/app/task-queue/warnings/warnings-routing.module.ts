import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { WarningsComponent } from './warnings.component';
import { WarningsResolverService } from './warnings-resolver.service';


export const routes: Routes = [
  {
    path: '',
    component: WarningsComponent,
    resolve: [
      WarningsResolverService,
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
export class WarningsRoutingModule {
}
