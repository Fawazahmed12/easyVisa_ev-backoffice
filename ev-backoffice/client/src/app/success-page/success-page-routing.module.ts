import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SuccessPageComponent } from './success-page.component';


export const routes: Routes = [
  {
    path: '',
    component: SuccessPageComponent,
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
export class SuccessPageRoutingModule {
}
