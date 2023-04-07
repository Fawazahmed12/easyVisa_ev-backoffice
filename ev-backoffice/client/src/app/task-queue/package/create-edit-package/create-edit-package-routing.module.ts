import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CreateEditPackageComponent } from './create-edit-package.component';

export const routes: Routes = [
  {
    path: '',
    component: CreateEditPackageComponent,
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
export class CreateEditPackageRoutingModule {
}
