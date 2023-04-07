import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DispositionsComponent } from './dispositions.component';
import { DispositionsResolverService } from './dispositions-resolver.service';

export const routes: Routes = [
  {
    path: '',
    component: DispositionsComponent,
    resolve: [
      DispositionsResolverService,
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
export class DispositionsRoutingModule {
}
