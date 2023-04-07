import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RepBasicInfoComponent } from './rep-basic-info.component';

export const routes: Routes = [
  {
    path: '',
    component: RepBasicInfoComponent,
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
export class RepBasicInfoRoutingModule {
}
